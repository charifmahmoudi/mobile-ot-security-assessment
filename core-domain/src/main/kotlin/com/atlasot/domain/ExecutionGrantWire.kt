package com.atlasot.domain

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.time.Instant

data class SignedGrantEnvelope(val payload: ByteArray, val signature: ByteArray)

object ExecutionGrantWire {
    private const val VERSION = 1
    private const val MAX_COLLECTION = 64

    fun encode(grant: ExecutionGrant): ByteArray = ByteArrayOutputStream().use { bytes ->
        DataOutputStream(bytes).use { out ->
            out.writeInt(VERSION)
            out.writeUTF(grant.grantId); out.writeUTF(grant.caseId); out.writeUTF(grant.authorizationHash)
            out.writeUTF(grant.operation.name); out.writeLong(grant.networkHandle); out.writeUTF(grant.targetIp)
            out.writeInt(grant.targetPort); out.writeInt(grant.unitId ?: -1)
            writeSet(out, grant.scopeCidrs); writeSet(out, grant.exclusions)
            out.writeInt(grant.maxPackets); out.writeLong(grant.maxBytes); out.writeInt(grant.retries)
            out.writeInt(grant.timeoutMs); out.writeInt(grant.concurrency)
            out.writeLong(grant.issuedAt.toEpochMilli()); out.writeLong(grant.expiresAt.toEpochMilli()); out.writeUTF(grant.nonce)
        }
        bytes.toByteArray()
    }

    fun decode(payload: ByteArray): ExecutionGrant = DataInputStream(ByteArrayInputStream(payload)).use { input ->
        require(input.readInt() == VERSION) { "unsupported grant version" }
        ExecutionGrant(
            grantId = input.readUTF(), caseId = input.readUTF(), authorizationHash = input.readUTF(),
            operation = Operation.valueOf(input.readUTF()), networkHandle = input.readLong(), targetIp = input.readUTF(),
            targetPort = input.readInt(), unitId = input.readInt().let { if (it < 0) null else it },
            scopeCidrs = readSet(input), exclusions = readSet(input), maxPackets = input.readInt(),
            maxBytes = input.readLong(), retries = input.readInt(), timeoutMs = input.readInt(),
            concurrency = input.readInt(), issuedAt = Instant.ofEpochMilli(input.readLong()),
            expiresAt = Instant.ofEpochMilli(input.readLong()), nonce = input.readUTF(),
        ).also { require(input.available() == 0) { "trailing grant bytes" } }
    }

    fun envelope(payload: ByteArray, signature: ByteArray): ByteArray = ByteArrayOutputStream().use { bytes ->
        DataOutputStream(bytes).use { out ->
            out.writeInt(payload.size); out.write(payload); out.writeInt(signature.size); out.write(signature)
        }
        bytes.toByteArray()
    }

    fun openEnvelope(encoded: ByteArray): SignedGrantEnvelope = DataInputStream(ByteArrayInputStream(encoded)).use { input ->
        val payloadLength = input.readInt(); require(payloadLength in 1..65536) { "invalid payload length" }
        val payload = ByteArray(payloadLength).also { input.readFully(it) }
        val signatureLength = input.readInt(); require(signatureLength in 64..256) { "invalid signature length" }
        val signature = ByteArray(signatureLength).also { input.readFully(it) }
        require(input.available() == 0) { "trailing envelope bytes" }
        SignedGrantEnvelope(payload, signature)
    }

    private fun writeSet(out: DataOutputStream, values: Set<String>) {
        require(values.size <= MAX_COLLECTION)
        out.writeInt(values.size); values.sorted().forEach(out::writeUTF)
    }

    private fun readSet(input: DataInputStream): Set<String> {
        val count = input.readInt(); require(count in 0..MAX_COLLECTION) { "invalid set length" }
        return buildSet { repeat(count) { add(input.readUTF()) } }
    }
}
