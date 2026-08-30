package com.atlasot.netbroker

import com.atlasot.domain.ExecutionGrant
import com.atlasot.domain.ExecutionGrantWire

data class GrantEnvelope(val payload: ByteArray, val signature: ByteArray)

object GrantWireCodec {
    fun encode(grant: ExecutionGrant) = ExecutionGrantWire.encode(grant)
    fun decode(payload: ByteArray) = ExecutionGrantWire.decode(payload)
    fun envelope(payload: ByteArray, signature: ByteArray) = ExecutionGrantWire.envelope(payload, signature)
    fun openEnvelope(encoded: ByteArray) = ExecutionGrantWire.openEnvelope(encoded).let { GrantEnvelope(it.payload, it.signature) }
}
