package com.atlasot.domain

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuditChainTest {
    @Test fun `chain verifies and detects changed linkage`() {
        val first = AuditEvent(1, Instant.EPOCH, "a", "assessor", "CREATE", "case", AuditChain.GENESIS)
        val second = AuditEvent(2, Instant.EPOCH.plusSeconds(1), "a", "assessor", "AUTHORIZE", "case", first.eventHash())
        assertTrue(AuditChain.verify(listOf(first, second)))
        assertFalse(AuditChain.verify(listOf(first, second.copy(previousHash = AuditChain.GENESIS))))
    }
}
