package com.atlasot.domain

import java.io.File
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ResearchPcapTest {
    private val root = File(requireNotNull(System.getenv("RESEARCH_PCAP_DIR")))

    @Test fun `ITI Modbus capture identifies Modbus assets`() = verify("modbus.pcap", OtProtocol.MODBUS_TCP)
    @Test fun `ITI DNP3 capture identifies DNP3 assets`() = verify("dnp3.pcap", OtProtocol.DNP3)
    @Test fun `ITI IEC104 capture identifies IEC104 assets`() = verify("iec104.pcap", OtProtocol.IEC_104)
    @Test fun `ITI BACnet capture identifies BACnet assets`() = verify("bacnet.pcap", OtProtocol.BACNET_IP)

    @Test fun `PCAPNG upload preserves digest and Modbus attribution`() {
        val source = File(root, "modbus.pcap").readBytes()
        val pcapng = classicLittleEndianToPcapng(source)
        val result = PassivePcapAnalyzer.analyze(pcapng)
        assertTrue(result.protocolCounts.getOrDefault(OtProtocol.MODBUS_TCP, 0) > 0)
        assertEquals(sha256(pcapng), result.sha256)
    }

    @Test fun `truncated PCAPNG fails closed`() {
        val source = classicLittleEndianToPcapng(File(root, "modbus.pcap").readBytes())
        assertFailsWith<PcapFormatException> { PassivePcapAnalyzer.analyze(source.copyOf(source.size - 1)) }
    }

    private fun verify(name: String, expected: OtProtocol) {
        val file = File(root, name)
        assertTrue(file.isFile, "research fixture missing: ${file.absolutePath}")
        val result = file.inputStream().use { PassivePcapAnalyzer.analyze(it) }
        assertTrue(result.totalPackets > 0)
        assertTrue(result.parsedPackets > 0, "no supported packets in $name")
        assertTrue(result.protocolCounts.getOrDefault(expected, 0) > 0, "missing $expected in $name")
        assertTrue(result.assets.any { expected in it.protocols && it.confidence >= 80 })
        assertEquals(64, result.sha256.length)
    }

    private fun classicLittleEndianToPcapng(pcap: ByteArray): ByteArray {
        require(pcap.copyOfRange(0, 4).contentEquals(byteArrayOf(0xd4.toByte(), 0xc3.toByte(), 0xb2.toByte(), 0xa1.toByte())))
        val out = ByteArrayOutputStream()
        fun le16(value: Int) { out.write(value and 0xff); out.write((value ushr 8) and 0xff) }
        fun le32(value: Long) { repeat(4) { out.write(((value ushr (it * 8)) and 0xff).toInt()) } }
        fun read32(offset: Int): Long = (0..3).fold(0L) { value, index -> value or ((pcap[offset + index].toLong() and 0xff) shl (index * 8)) }
        le32(0x0a0d0d0a); le32(28); le32(0x1a2b3c4d); le16(1); le16(0); le32(0xffff_ffff); le32(0xffff_ffff); le32(28)
        le32(1); le32(20); le16(1); le16(0); le32(262_144); le32(20)
        var cursor = 24
        while (cursor < pcap.size) {
            val seconds = read32(cursor); val micros = read32(cursor + 4)
            val captured = read32(cursor + 8).toInt(); val original = read32(cursor + 12)
            val padded = (captured + 3) and -4
            val blockLength = 32 + padded
            le32(6); le32(blockLength.toLong()); le32(0)
            val ticks = seconds * 1_000_000L + micros
            le32(ticks ushr 32); le32(ticks and 0xffff_ffffL); le32(captured.toLong()); le32(original)
            out.write(pcap, cursor + 16, captured); repeat(padded - captured) { out.write(0) }; le32(blockLength.toLong())
            cursor += 16 + captured
        }
        return out.toByteArray()
    }

    private fun sha256(bytes: ByteArray) = java.security.MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { "%02x".format(it.toInt() and 0xff) }
}
