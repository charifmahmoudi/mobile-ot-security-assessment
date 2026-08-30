package com.atlasot.domain

import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.time.Duration
import java.time.Instant

data class IPv4Cidr private constructor(val network: Int, val prefix: Int) {
    fun contains(address: Int): Boolean {
        val mask = if (prefix == 0) 0 else (-1 shl (32 - prefix))
        return address and mask == network
    }

    companion object {
        fun parse(value: String): IPv4Cidr {
            val parts = value.split('/')
            require(parts.size == 2) { "invalid CIDR" }
            val prefix = parts[1].toInt()
            require(prefix in 0..32) { "invalid CIDR prefix" }
            val address = parseAddress(parts[0])
            val mask = if (prefix == 0) 0 else (-1 shl (32 - prefix))
            return IPv4Cidr(address and mask, prefix)
        }

        fun parseAddress(value: String): Int {
            val octets = value.split('.')
            require(octets.size == 4) { "IPv4 literal required" }
            return octets.fold(0) { address, octet ->
                require(octet.isNotEmpty() && (octet == "0" || !octet.startsWith('0'))) {
                    "canonical IPv4 literal required"
                }
                val parsed = octet.toIntOrNull()
                require(parsed != null && parsed in 0..255) { "invalid IPv4 octet" }
                (address shl 8) or parsed
            }
        }
    }
}

data class ExecutionGrant(
    val grantId: String,
    val caseId: String,
    val authorizationHash: String,
    val operation: Operation,
    val networkHandle: Long,
    val targetIp: String,
    val targetPort: Int,
    val unitId: Int?,
    val scopeCidrs: Set<String>,
    val exclusions: Set<String>,
    val maxPackets: Int,
    val maxBytes: Long,
    val retries: Int,
    val timeoutMs: Int,
    val concurrency: Int,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val nonce: String,
)

enum class GrantRejection {
    EXPIRED, LIFETIME_TOO_LONG, REPLAYED, OUT_OF_SCOPE, EXCLUDED, OPERATION_NOT_ALLOWED,
    INVALID_LIMIT, INVALID_TARGET, INVALID_PORT, JOURNAL_FAILURE
}

sealed interface GrantDecision {
    data object Allowed : GrantDecision
    data class Rejected(val reason: GrantRejection) : GrantDecision
}

class GrantPolicy(private val consumedNonces: MutableSet<String>) {
    fun evaluate(grant: ExecutionGrant, now: Instant): GrantDecision {
        if (now.isBefore(grant.issuedAt) || !now.isBefore(grant.expiresAt)) return GrantDecision.Rejected(GrantRejection.EXPIRED)
        if (Duration.between(grant.issuedAt, grant.expiresAt) > Duration.ofSeconds(60)) return GrantDecision.Rejected(GrantRejection.LIFETIME_TOO_LONG)
        if (grant.nonce in consumedNonces) return GrantDecision.Rejected(GrantRejection.REPLAYED)
        if (grant.operation != Operation.MODBUS_DEVICE_ID_BASIC) return GrantDecision.Rejected(GrantRejection.OPERATION_NOT_ALLOWED)
        if (grant.targetPort != 502) return GrantDecision.Rejected(GrantRejection.INVALID_PORT)
        if (grant.unitId == null || grant.unitId !in 0..247) {
            return GrantDecision.Rejected(GrantRejection.INVALID_TARGET)
        }
        if (grant.maxPackets !in 1..2 || grant.maxBytes !in 1..512 || grant.retries !in 0..1 ||
            grant.timeoutMs !in 100..1500 || grant.concurrency != 1
        ) return GrantDecision.Rejected(GrantRejection.INVALID_LIMIT)
        val target = runCatching { IPv4Cidr.parseAddress(grant.targetIp) }.getOrNull()
            ?: return GrantDecision.Rejected(GrantRejection.INVALID_TARGET)
        if (grant.exclusions.any { runCatching { IPv4Cidr.parseAddress(it) }.getOrNull() == target }) {
            return GrantDecision.Rejected(GrantRejection.EXCLUDED)
        }
        if (grant.scopeCidrs.none { runCatching { IPv4Cidr.parse(it).contains(target) }.getOrDefault(false) }) {
            return GrantDecision.Rejected(GrantRejection.OUT_OF_SCOPE)
        }
        consumedNonces += grant.nonce
        return GrantDecision.Allowed
    }
}

object GrantCanonicalizer {
    fun bytes(grant: ExecutionGrant): ByteArray = listOf(
        "ATLAS-GRANT-V1", grant.grantId, grant.caseId, grant.authorizationHash, grant.operation.name,
        grant.networkHandle.toString(), grant.targetIp, grant.targetPort.toString(), grant.unitId.toString(),
        grant.scopeCidrs.sorted().joinToString(","), grant.exclusions.sorted().joinToString(","),
        grant.maxPackets.toString(), grant.maxBytes.toString(), grant.retries.toString(),
        grant.timeoutMs.toString(), grant.concurrency.toString(), grant.issuedAt.toEpochMilli().toString(),
        grant.expiresAt.toEpochMilli().toString(), grant.nonce
    ).joinToString("|").toByteArray(StandardCharsets.UTF_8)
}

object GrantSignatures {
    fun sign(grant: ExecutionGrant, privateKey: PrivateKey): ByteArray = Signature.getInstance("Ed25519").run {
        initSign(privateKey); update(GrantCanonicalizer.bytes(grant)); sign()
    }
    fun verify(grant: ExecutionGrant, signature: ByteArray, publicKey: PublicKey): Boolean = Signature.getInstance("Ed25519").run {
        initVerify(publicKey); update(GrantCanonicalizer.bytes(grant)); verify(signature)
    }
}
