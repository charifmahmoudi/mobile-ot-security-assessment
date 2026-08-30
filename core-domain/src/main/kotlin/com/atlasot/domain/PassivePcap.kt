package com.atlasot.domain

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.time.Instant

enum class OtProtocol(val label: String) {
    MODBUS_TCP("Modbus/TCP"), DNP3("DNP3"), IEC_104("IEC 60870-5-104"),
    BACNET_IP("BACnet/IP"), ETHERNET_IP("EtherNet/IP"), S7COMM("Siemens S7comm"),
    IEC_61850_MMS("IEC 61850 MMS"), OPC_UA("OPC UA"), PROFINET("PROFINET")
}

data class PassiveAsset(
    val address: String,
    val protocols: Set<OtProtocol>,
    val role: String,
    val confidence: Int,
    val packetCount: Int,
    val evidence: List<String>,
    val vendor: String? = null,
    val product: String? = null,
    val revision: String? = null,
)

data class PassiveAnalysis(
    val sha256: String,
    val totalPackets: Int,
    val parsedPackets: Int,
    val startedAt: Instant?,
    val endedAt: Instant?,
    val assets: List<PassiveAsset>,
    val protocolCounts: Map<OtProtocol, Int>,
    val warnings: List<String>,
)

class PcapFormatException(message: String) : IllegalArgumentException(message)

/** Bounded classic-PCAP parser. PCAPNG and non-Ethernet link types fail closed. */
object PassivePcapAnalyzer {
    private const val MAX_FILE_BYTES = 64 * 1024 * 1024
    private const val MAX_CAPTURED_PACKET = 262_144

    fun analyze(input: InputStream): PassiveAnalysis = analyze(readBounded(input))

    fun analyze(bytes: ByteArray): PassiveAnalysis {
        if (bytes.size < 24) throw PcapFormatException("Capture is shorter than a PCAP header")
        if (bytes.size > MAX_FILE_BYTES) throw PcapFormatException("Capture exceeds the 64 MiB mobile analysis limit")
        val order = when (bytes.copyOfRange(0, 4).toHex()) {
            "d4c3b2a1" -> Order.LITTLE_MICRO
            "a1b2c3d4" -> Order.BIG_MICRO
            "4d3cb2a1" -> Order.LITTLE_NANO
            "a1b23c4d" -> Order.BIG_NANO
            "0a0d0d0a" -> throw PcapFormatException("PCAPNG is not supported in this milestone")
            else -> throw PcapFormatException("Unsupported capture magic")
        }
        val linkType = u32(bytes, 20, order).toInt()
        if (linkType != 1) throw PcapFormatException("Only Ethernet PCAP link type 1 is supported; found $linkType")

        val assets = linkedMapOf<String, MutableAsset>()
        val protocolCounts = linkedMapOf<OtProtocol, Int>()
        val warnings = linkedSetOf<String>()
        var cursor = 24
        var total = 0
        var parsed = 0
        var first: Instant? = null
        var last: Instant? = null
        while (cursor < bytes.size) {
            if (bytes.size - cursor < 16) throw PcapFormatException("Truncated packet record header at packet ${total + 1}")
            val seconds = u32(bytes, cursor, order)
            val fraction = u32(bytes, cursor + 4, order)
            val captured = u32(bytes, cursor + 8, order).toInt()
            val original = u32(bytes, cursor + 12, order).toInt()
            if (captured !in 0..MAX_CAPTURED_PACKET || original < captured || cursor + 16L + captured > bytes.size) {
                throw PcapFormatException("Invalid packet lengths at packet ${total + 1}")
            }
            val nanos = if (order.nano) fraction.coerceAtMost(999_999_999) else fraction.coerceAtMost(999_999) * 1_000
            val at = Instant.ofEpochSecond(seconds, nanos)
            if (first == null || at.isBefore(first)) first = at
            if (last == null || at.isAfter(last)) last = at
            total++
            val packet = bytes.copyOfRange(cursor + 16, cursor + 16 + captured)
            val observation = parseEthernet(packet)
            if (observation != null) {
                parsed++
                protocolCounts[observation.protocol] = (protocolCounts[observation.protocol] ?: 0) + 1
                record(assets, observation.client, observation, false)
                record(assets, observation.server, observation, true)
            }
            cursor += 16 + captured
        }
        if (total == 0) warnings += "Capture contains no packets"
        if (protocolCounts.isEmpty() && total > 0) warnings += "No supported OT protocol evidence was found"
        return PassiveAnalysis(
            sha256 = MessageDigest.getInstance("SHA-256").digest(bytes).toHex(), totalPackets = total,
            parsedPackets = parsed, startedAt = first, endedAt = last,
            assets = assets.values.map { it.freeze() }.sortedWith(compareByDescending<PassiveAsset> { it.confidence }.thenBy { it.address }),
            protocolCounts = protocolCounts.toMap(), warnings = warnings.toList(),
        )
    }

    private fun record(assets: MutableMap<String, MutableAsset>, address: String, o: Observation, server: Boolean) {
        val asset = assets.getOrPut(address) { MutableAsset(address) }
        asset.protocols += o.protocol
        asset.packetCount++
        asset.confidence = maxOf(asset.confidence, if (server) o.confidence else minOf(o.confidence, 75))
        if (server) asset.serverEvidence++ else asset.clientEvidence++
        asset.evidence += o.detail
        if (server) {
            asset.vendor = o.vendor ?: asset.vendor
            asset.product = o.product ?: asset.product
            asset.revision = o.revision ?: asset.revision
        }
    }

    private fun parseEthernet(frame: ByteArray): Observation? {
        if (frame.size < 14) return null
        val sourceMac = frame.copyOfRange(6, 12).joinToString(":") { "%02x".format(it.toInt() and 0xff) }
        val destinationMac = frame.copyOfRange(0, 6).joinToString(":") { "%02x".format(it.toInt() and 0xff) }
        var etherType = u16be(frame, 12)
        var offset = 14
        repeat(2) {
            if (etherType == 0x8100 || etherType == 0x88a8) {
                if (frame.size < offset + 4) return null
                etherType = u16be(frame, offset + 2); offset += 4
            }
        }
        if (etherType == 0x8892) return Observation(
            OtProtocol.PROFINET, sourceMac, destinationMac, 95, "EtherType 0x8892 PROFINET frame"
        )
        if (etherType != 0x0800 || frame.size < offset + 20) return null
        val ihl = (frame[offset].toInt() and 0x0f) * 4
        if (ihl < 20 || frame.size < offset + ihl) return null
        val fragment = u16be(frame, offset + 6)
        if (fragment and 0x1fff != 0) return null
        val source = ipv4(frame, offset + 12)
        val destination = ipv4(frame, offset + 16)
        return when (frame[offset + 9].toInt() and 0xff) {
            6 -> parseTcp(frame, offset + ihl, source, destination)
            17 -> parseUdp(frame, offset + ihl, source, destination)
            else -> null
        }
    }

    private fun parseTcp(frame: ByteArray, offset: Int, source: String, destination: String): Observation? {
        if (frame.size < offset + 20) return null
        val sourcePort = u16be(frame, offset); val destinationPort = u16be(frame, offset + 2)
        val header = ((frame[offset + 12].toInt() ushr 4) and 0x0f) * 4
        if (header < 20 || frame.size < offset + header) return null
        val payload = frame.copyOfRange(offset + header, frame.size)
        val servicePort = listOf(sourcePort, destinationPort).firstOrNull { it in setOf(502, 20000, 2404, 44818, 2222, 102, 4840) }
            ?: return null
        val server = if (sourcePort == servicePort) source else destination
        val client = if (sourcePort == servicePort) destination else source
        return when (servicePort) {
            502 -> classifyModbus(payload, client, server)
            20000 -> if (payload.size >= 2 && payload[0] == 0x05.toByte() && payload[1] == 0x64.toByte())
                Observation(OtProtocol.DNP3, client, server, 98, "DNP3 start bytes 0x0564 on TCP/20000") else null
            2404 -> if (payload.size >= 2 && payload[0] == 0x68.toByte() && (payload[1].toInt() and 0xff) <= payload.size - 2)
                Observation(OtProtocol.IEC_104, client, server, 98, "IEC-104 APDU start 0x68 on TCP/2404") else null
            44818, 2222 -> classifyEthernetIp(payload, client, server, servicePort)
            4840 -> classifyOpcUa(payload, client, server)
            102 -> classifyIsoOnTcp(payload, client, server)
            else -> null
        }
    }

    private fun parseUdp(frame: ByteArray, offset: Int, source: String, destination: String): Observation? {
        if (frame.size < offset + 8) return null
        val sourcePort = u16be(frame, offset); val destinationPort = u16be(frame, offset + 2)
        val payload = frame.copyOfRange(offset + 8, frame.size)
        if ((sourcePort == 47808 || destinationPort == 47808) && payload.size >= 4 && payload[0] == 0x81.toByte()) {
            val server = if (sourcePort == 47808) source else destination
            val client = if (sourcePort == 47808) destination else source
            return Observation(OtProtocol.BACNET_IP, client, server, 97, "BACnet BVLC type 0x81 on UDP/47808")
        }
        return null
    }

    private fun classifyModbus(payload: ByteArray, client: String, server: String): Observation? {
        if (payload.size < 8 || u16be(payload, 2) != 0) return null
        val length = u16be(payload, 4)
        if (length !in 2..254 || length > payload.size - 6) return null
        val identity = parseModbusIdentity(payload)
        val detail = if (identity == null) "Valid Modbus MBAP header on TCP/502" else "Modbus device identity objects ${identity.keys.sorted()}"
        return Observation(
            OtProtocol.MODBUS_TCP, client, server, if (identity == null) 94 else 100, detail,
            vendor = identity?.get(0), product = identity?.get(1), revision = identity?.get(2),
        )
    }

    private fun parseModbusIdentity(payload: ByteArray): Map<Int, String>? {
        if (payload.size < 14 || (payload[7].toInt() and 0xff) != 0x2b || (payload[8].toInt() and 0xff) != 0x0e) return null
        val count = payload[13].toInt() and 0xff
        if (count > 32) return null
        var cursor = 14
        val objects = linkedMapOf<Int, String>()
        repeat(count) {
            if (cursor + 2 > payload.size) return null
            val id = payload[cursor++].toInt() and 0xff
            val size = payload[cursor++].toInt() and 0xff
            if (cursor + size > payload.size) return null
            objects[id] = payload.copyOfRange(cursor, cursor + size).toString(Charsets.ISO_8859_1)
                .filter { it.code in 32..126 }.trim()
            cursor += size
        }
        return objects
    }

    private fun classifyEthernetIp(payload: ByteArray, client: String, server: String, port: Int): Observation? {
        if (payload.size < 24) return null
        val command = (payload[0].toInt() and 0xff) or ((payload[1].toInt() and 0xff) shl 8)
        if (command !in setOf(0x0063, 0x0065, 0x0066, 0x006f, 0x0070)) return null
        return Observation(OtProtocol.ETHERNET_IP, client, server, 98, "EtherNet/IP encapsulation command 0x${command.toString(16)} on TCP/$port")
    }

    private fun classifyOpcUa(payload: ByteArray, client: String, server: String): Observation? {
        if (payload.size < 8) return null
        val type = payload.copyOfRange(0, 3).toString(Charsets.US_ASCII)
        if (type !in setOf("HEL", "ACK", "ERR", "RHE", "OPN", "CLO", "MSG")) return null
        return Observation(OtProtocol.OPC_UA, client, server, 98, "OPC UA $type message on TCP/4840")
    }

    private fun classifyIsoOnTcp(payload: ByteArray, client: String, server: String): Observation? {
        if (payload.size < 7 || payload[0] != 0x03.toByte() || payload[1] != 0x00.toByte()) return null
        val s7 = payload.drop(7).take(16).any { it == 0x32.toByte() }
        return if (s7) Observation(OtProtocol.S7COMM, client, server, 98, "S7 protocol identifier 0x32 over ISO-on-TCP/102")
        else Observation(OtProtocol.IEC_61850_MMS, client, server, 80, "ISO-on-TCP/102; MMS candidate without S7 marker")
    }

    private fun readBounded(input: InputStream): ByteArray {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var total = 0
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            if (total > MAX_FILE_BYTES) throw PcapFormatException("Capture exceeds the 64 MiB mobile analysis limit")
            out.write(buffer, 0, count)
        }
        return out.toByteArray()
    }

    private fun u16be(bytes: ByteArray, offset: Int) = ((bytes[offset].toInt() and 0xff) shl 8) or (bytes[offset + 1].toInt() and 0xff)
    private fun u32(bytes: ByteArray, offset: Int, order: Order): Long {
        val indices = if (order.little) intArrayOf(3, 2, 1, 0) else intArrayOf(0, 1, 2, 3)
        return indices.fold(0L) { value, index -> (value shl 8) or (bytes[offset + index].toLong() and 0xff) }
    }
    private fun ipv4(bytes: ByteArray, offset: Int) = (0..3).joinToString(".") { (bytes[offset + it].toInt() and 0xff).toString() }
    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private enum class Order(val little: Boolean, val nano: Boolean) {
        LITTLE_MICRO(true, false), BIG_MICRO(false, false), LITTLE_NANO(true, true), BIG_NANO(false, true)
    }
    private data class Observation(
        val protocol: OtProtocol, val client: String, val server: String, val confidence: Int, val detail: String,
        val vendor: String? = null, val product: String? = null, val revision: String? = null,
    )
    private class MutableAsset(val address: String) {
        val protocols = linkedSetOf<OtProtocol>(); val evidence = linkedSetOf<String>()
        var packetCount = 0; var confidence = 0; var serverEvidence = 0; var clientEvidence = 0
        var vendor: String? = null; var product: String? = null; var revision: String? = null
        fun freeze() = PassiveAsset(
            address, protocols.toSet(), when {
                serverEvidence > 0 && clientEvidence > 0 -> "OT endpoint"
                serverEvidence > 0 -> "Controller/server candidate"
                else -> "Client/HMI candidate"
            }, confidence, packetCount, evidence.take(8), vendor, product, revision,
        )
    }
}

data class ActiveModbusIdentity(
    val identitySupported: Boolean,
    val vendor: String?,
    val product: String?,
    val revision: String?,
    val evidence: String,
)

object ActiveModbusEvidence {
    fun parse(response: ByteArray): ActiveModbusIdentity {
        require(response.size in 9..512) { "invalid Modbus response size" }
        fun u(index: Int) = response[index].toInt() and 0xff
        require(u(2) == 0 && u(3) == 0) { "invalid Modbus protocol id" }
        require(((u(4) shl 8) or u(5)) == response.size - 6) { "invalid Modbus length" }
        if (u(7) == 0xab) {
            require(response.size == 9 && u(8) in 1..11) { "invalid Modbus exception" }
            return ActiveModbusIdentity(false, null, null, null, "Modbus exception ${u(8)} confirms the service; device identity is unsupported")
        }
        require(response.size >= 14 && u(7) == 0x2b && u(8) == 0x0e) { "not a Modbus device identity response" }
        val count = u(13); require(count <= 32)
        var cursor = 14
        val objects = linkedMapOf<Int, String>()
        repeat(count) {
            require(cursor + 2 <= response.size)
            val id = u(cursor++); val length = u(cursor++)
            require(cursor + length <= response.size)
            objects[id] = response.copyOfRange(cursor, cursor + length).toString(Charsets.ISO_8859_1)
                .filter { it.code in 32..126 }.trim()
            cursor += length
        }
        require(cursor == response.size)
        return ActiveModbusIdentity(true, objects[0], objects[1], objects[2], "FC 43 / MEI 14 basic device identity response")
    }
}
