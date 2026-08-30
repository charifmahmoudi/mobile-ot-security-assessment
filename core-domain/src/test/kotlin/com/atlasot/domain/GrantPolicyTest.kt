package com.atlasot.domain

import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class GrantPolicyTest {
    private val now = Instant.parse("2026-08-30T10:00:00Z")
    private fun grant(target: String = "192.168.10.20", nonce: String = "nonce-1") = ExecutionGrant(
        grantId = "grant-1", caseId = "case-1", authorizationHash = "a".repeat(64),
        operation = Operation.MODBUS_DEVICE_ID_BASIC, networkHandle = 42, targetIp = target,
        targetPort = 502, unitId = 1, scopeCidrs = setOf("192.168.10.0/24"), exclusions = setOf("192.168.10.99"),
        maxPackets = 2, maxBytes = 512, retries = 1, timeoutMs = 1500, concurrency = 1,
        issuedAt = now.minusSeconds(1), expiresAt = now.plusSeconds(30), nonce = nonce
    )

    @Test fun `valid grant is allowed once and replay rejected`() {
        val policy = GrantPolicy(mutableSetOf())
        assertIs<GrantDecision.Allowed>(policy.evaluate(grant(), now))
        assertEquals(GrantRejection.REPLAYED, (policy.evaluate(grant(), now) as GrantDecision.Rejected).reason)
    }

    @Test fun `out of scope and excluded targets are rejected`() {
        assertEquals(GrantRejection.OUT_OF_SCOPE, (GrantPolicy(mutableSetOf()).evaluate(grant("10.0.0.1"), now) as GrantDecision.Rejected).reason)
        assertEquals(GrantRejection.EXCLUDED, (GrantPolicy(mutableSetOf()).evaluate(grant("192.168.10.99"), now) as GrantDecision.Rejected).reason)
    }

    @Test fun `signature detects grant mutation`() {
        val pair = KeyPairGenerator.getInstance("EC").run {
            initialize(ECGenParameterSpec("secp256r1"))
            generateKeyPair()
        }
        val original = grant()
        val signature = GrantSignatures.sign(original, pair.private)
        assertTrue(GrantSignatures.verify(original, signature, pair.public))
        assertFalse(GrantSignatures.verify(original.copy(targetIp = "192.168.10.21"), signature, pair.public))
    }

    @Test fun `hostnames noncanonical addresses and missing unit ids are rejected`() {
        assertFailsWith<IllegalArgumentException> { IPv4Cidr.parseAddress("plc.local") }
        assertFailsWith<IllegalArgumentException> { IPv4Cidr.parseAddress("192.168.010.1") }
        val rejected = GrantPolicy(mutableSetOf()).evaluate(grant().copy(unitId = null), now)
        assertEquals(GrantRejection.INVALID_TARGET, (rejected as GrantDecision.Rejected).reason)
    }
}
