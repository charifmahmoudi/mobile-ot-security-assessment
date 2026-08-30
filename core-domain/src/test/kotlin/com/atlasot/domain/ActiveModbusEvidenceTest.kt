package com.atlasot.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActiveModbusEvidenceTest {
    @Test fun `basic device identity becomes vendor product and revision evidence`() {
        val objects = byteArrayOf(
            0, 3, 'A'.code.toByte(), 'B'.code.toByte(), 'B'.code.toByte(),
            1, 3, 'P'.code.toByte(), 'L'.code.toByte(), 'C'.code.toByte(),
            2, 3, '1'.code.toByte(), '.'.code.toByte(), '0'.code.toByte(),
        )
        val pdu = byteArrayOf(0x2B, 0x0E, 1, 1, 0, 0, 3) + objects
        val length = pdu.size + 1
        val response = byteArrayOf(0, 1, 0, 0, (length ushr 8).toByte(), length.toByte(), 1) + pdu
        val result = ActiveModbusEvidence.parse(response)
        assertTrue(result.identitySupported)
        assertEquals("ABB", result.vendor)
        assertEquals("PLC", result.product)
        assertEquals("1.0", result.revision)
    }

    @Test fun `illegal function is protocol evidence but never identity evidence`() {
        val result = ActiveModbusEvidence.parse(byteArrayOf(0, 1, 0, 0, 0, 3, 1, 0xAB.toByte(), 1))
        assertFalse(result.identitySupported)
        assertEquals(null, result.vendor)
        assertTrue(result.evidence.contains("confirms the service"))
    }
}
