package com.atlasot.netbroker

import android.net.Network
import java.io.EOFException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

data class ModbusIdentity(
    val conformityLevel: Int,
    val moreFollows: Boolean,
    val nextObjectId: Int,
    val objects: Map<Int, String>,
)

object ModbusDeviceIdCodec {
    fun request(transactionId: Int, unitId: Int): ByteArray {
        require(transactionId in 0..65535); require(unitId in 0..247)
        return byteArrayOf(
            (transactionId ushr 8).toByte(), transactionId.toByte(), 0, 0, 0, 5, unitId.toByte(),
            0x2B, 0x0E, 0x01, 0x00
        )
    }

    fun parseResponse(bytes: ByteArray, transactionId: Int, unitId: Int): ModbusIdentity {
        require(bytes.size in 15..512) { "invalid response size" }
        fun u(index: Int) = bytes[index].toInt() and 0xff
        require((u(0) shl 8 or u(1)) == transactionId) { "transaction mismatch" }
        require(u(2) == 0 && u(3) == 0) { "protocol mismatch" }
        val declared = u(4) shl 8 or u(5)
        require(declared == bytes.size - 6) { "length mismatch" }
        require(u(6) == unitId && u(7) == 0x2B && u(8) == 0x0E && u(9) == 0x01) { "function mismatch" }
        val conformity = u(10); val more = u(11) != 0; val next = u(12); val count = u(13)
        require(count <= 3) { "basic identity returned too many objects" }
        var cursor = 14
        val objects = linkedMapOf<Int, String>()
        repeat(count) {
            require(cursor + 2 <= bytes.size) { "truncated object" }
            val id = u(cursor++); val length = u(cursor++)
            require(id in 0..2 && length <= 248 && cursor + length <= bytes.size) { "invalid object" }
            require(id !in objects) { "duplicate object" }
            objects[id] = bytes.copyOfRange(cursor, cursor + length).toString(Charsets.ISO_8859_1)
            cursor += length
        }
        require(cursor == bytes.size) { "trailing response bytes" }
        return ModbusIdentity(conformity, more, next, objects)
    }
}

class ModbusDeviceIdClient {
    private val active = ConcurrentHashMap<String, Socket>()

    fun execute(caseId: String, network: Network, target: String, port: Int, unitId: Int, timeoutMs: Int): ByteArray {
        val transactionId = (System.nanoTime() and 0xffff).toInt()
        val socket = Socket()
        active[caseId] = socket
        try {
            network.bindSocket(socket)
            socket.soTimeout = timeoutMs
            socket.connect(InetSocketAddress(target, port), timeoutMs)
            socket.getOutputStream().write(ModbusDeviceIdCodec.request(transactionId, unitId))
            socket.getOutputStream().flush()
            val header = socket.getInputStream().readExactly(7)
            val remaining = ((header[4].toInt() and 0xff) shl 8 or (header[5].toInt() and 0xff)) - 1
            require(remaining in 8..505) { "invalid Modbus length" }
            val response = header + socket.getInputStream().readExactly(remaining)
            ModbusDeviceIdCodec.parseResponse(response, transactionId, unitId)
            return response
        } finally {
            active.remove(caseId, socket)
            runCatching { socket.close() }
        }
    }

    fun stop(caseId: String) { active.remove(caseId)?.let { runCatching { it.close() } } }
    fun stopAll() { active.keys.toList().forEach(::stop) }

    private fun java.io.InputStream.readExactly(size: Int): ByteArray {
        val value = ByteArray(size); var offset = 0
        while (offset < size) {
            val count = read(value, offset, size - offset)
            if (count < 0) throw EOFException("truncated response")
            offset += count
        }
        return value
    }
}
