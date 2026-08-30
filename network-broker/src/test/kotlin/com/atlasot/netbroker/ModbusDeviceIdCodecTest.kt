package com.atlasot.netbroker

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModbusDeviceIdCodecTest {
    @Test fun `request matches function 43 MEI 14 basic identity`() {
        assertContentEquals(byteArrayOf(0x12, 0x34, 0, 0, 0, 5, 1, 0x2B, 0x0E, 1, 0), ModbusDeviceIdCodec.request(0x1234, 1))
    }

    @Test fun `basic identity response is bounded and parsed`() {
        val objects = byteArrayOf(0, 3, 'A'.code.toByte(), 'B'.code.toByte(), 'C'.code.toByte(), 1, 2, 'P'.code.toByte(), '1'.code.toByte())
        val pdu = byteArrayOf(0x2B, 0x0E, 1, 1, 0, 0, 2) + objects
        val length = 1 + pdu.size
        val response = byteArrayOf(0x12, 0x34, 0, 0, (length ushr 8).toByte(), length.toByte(), 1) + pdu
        val parsed = ModbusDeviceIdCodec.parseResponse(response, 0x1234, 1)
        assertEquals("ABC", parsed.objects[0]); assertEquals("P1", parsed.objects[1])
    }

    @Test fun `mismatched transaction is rejected`() {
        val malformed = byteArrayOf(0, 1, 0, 0, 0, 9, 1, 0x2B, 0x0E, 1, 1, 0, 0, 0, 0)
        assertFailsWith<IllegalArgumentException> { ModbusDeviceIdCodec.parseResponse(malformed, 2, 1) }
    }

    @Test fun `valid illegal-function exception still confirms Modbus service`() {
        val response = byteArrayOf(0x12, 0x34, 0, 0, 0, 3, 1, 0xAB.toByte(), 1)
        assertEquals(null, ModbusDeviceIdCodec.validateResponse(response, 0x1234, 1))
    }
}
