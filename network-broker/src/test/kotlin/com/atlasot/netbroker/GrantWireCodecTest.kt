package com.atlasot.netbroker

import com.atlasot.domain.ExecutionGrant
import com.atlasot.domain.Operation
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GrantWireCodecTest {
    private val grant = ExecutionGrant(
        "g", "c", "a".repeat(64), Operation.MODBUS_DEVICE_ID_BASIC, 7, "192.168.1.2", 502, 1,
        setOf("192.168.1.0/24"), setOf("192.168.1.99"), 2, 512, 1, 1500, 1,
        Instant.ofEpochMilli(1000), Instant.ofEpochMilli(2000), "n"
    )

    @Test fun `grant and envelope round trip`() {
        val payload = GrantWireCodec.encode(grant)
        assertEquals(grant, GrantWireCodec.decode(payload))
        val opened = GrantWireCodec.openEnvelope(GrantWireCodec.envelope(payload, ByteArray(64) { 1 }))
        assertContentEquals(payload, opened.payload)
    }

    @Test fun `trailing bytes are rejected`() {
        assertFailsWith<IllegalArgumentException> { GrantWireCodec.decode(GrantWireCodec.encode(grant) + 1) }
    }
}
