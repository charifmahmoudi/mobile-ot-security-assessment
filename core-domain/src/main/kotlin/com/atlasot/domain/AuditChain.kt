package com.atlasot.domain

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant

data class AuditEvent(
    val sequence: Long,
    val at: Instant,
    val actor: String,
    val role: String,
    val action: String,
    val objectId: String,
    val previousHash: String,
) {
    fun eventHash(): String {
        val canonical = listOf(sequence, at.toEpochMilli(), actor, role, action, objectId, previousHash).joinToString("|")
        return MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}

object AuditChain {
    const val GENESIS = "0000000000000000000000000000000000000000000000000000000000000000"
    fun verify(events: List<AuditEvent>): Boolean {
        var previous = GENESIS
        for ((index, event) in events.withIndex()) {
            if (event.sequence != index + 1L || event.previousHash != previous) return false
            previous = event.eventHash()
        }
        return true
    }
}
