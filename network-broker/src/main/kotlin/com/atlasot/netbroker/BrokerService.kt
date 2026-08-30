package com.atlasot.netbroker

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.ParcelFileDescriptor
import android.util.Base64
import com.atlasot.domain.GrantDecision
import com.atlasot.domain.GrantPolicy
import com.atlasot.domain.GrantSignatures
import java.io.FileOutputStream
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.concurrent.Executors

class BrokerService : Service() {
    private val consumedNonces = mutableSetOf<String>()
    private val policy = GrantPolicy(consumedNonces)
    private val client = ModbusDeviceIdClient()
    private val executor = Executors.newSingleThreadExecutor()

    private val binder = object : IAtlasNetworkBroker.Stub() {
        override fun inspectInterfaces(signedRequest: ByteArray?): ByteArray {
            val manager = getSystemService(ConnectivityManager::class.java)
            val body = manager.allNetworks.joinToString(prefix = "[", postfix = "]") { network ->
                val caps = manager.getNetworkCapabilities(network)
                "{\"handle\":${network.networkHandle},\"ethernet\":${caps?.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) == true}}"
            }
            return body.toByteArray()
        }

        override fun provisionGrantKey(x509GrantPublicKey: ByteArray?): ByteArray {
            requireNotNull(x509GrantPublicKey)
            val key = KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(x509GrantPublicKey))
            val encoded = Base64.encodeToString(key.encoded, Base64.NO_WRAP)
            val existing = preferences().getString(KEY_GRANT_PUBLIC, null)
            if (existing != null && existing != encoded) return "REJECTED:KEY_ALREADY_PROVISIONED".toByteArray()
            if (!preferences().edit().putString(KEY_GRANT_PUBLIC, encoded).commit()) {
                return "REJECTED:KEY_STORAGE_FAILED".toByteArray()
            }
            return "PROVISIONED".toByteArray()
        }

        override fun execute(grantEnvelope: ByteArray?, evidenceSink: ParcelFileDescriptor?): ByteArray {
            requireNotNull(grantEnvelope); requireNotNull(evidenceSink)
            val envelope = GrantWireCodec.openEnvelope(grantEnvelope)
            val grant = GrantWireCodec.decode(envelope.payload)
            val key = loadGrantKey() ?: return "REJECTED:NO_GRANT_KEY".toByteArray()
            if (!GrantSignatures.verify(grant, envelope.signature, key)) return "REJECTED:BAD_SIGNATURE".toByteArray()
            val decision = synchronized(consumedNonces) {
                consumedNonces += preferences().getStringSet(KEY_CONSUMED_NONCES, emptySet()).orEmpty()
                val evaluated = policy.evaluate(grant, Instant.now())
                if (evaluated is GrantDecision.Allowed && !preferences().edit()
                        .putStringSet(KEY_CONSUMED_NONCES, consumedNonces.toSet()).commit()
                ) {
                    GrantDecision.Rejected(com.atlasot.domain.GrantRejection.JOURNAL_FAILURE)
                } else {
                    evaluated
                }
            }
            if (decision is GrantDecision.Rejected) return "REJECTED:${decision.reason}".toByteArray()
            val writeFd = ParcelFileDescriptor.dup(evidenceSink.fileDescriptor)
            executor.execute {
                FileOutputStream(writeFd.fileDescriptor).use { output ->
                    runCatching {
                        val network = Network.fromNetworkHandle(grant.networkHandle)
                        client.execute(grant.caseId, network, grant.targetIp, grant.targetPort, grant.unitId!!, grant.timeoutMs)
                    }.onSuccess { response ->
                        output.write(response)
                    }.onFailure { error ->
                        output.write("ERROR:${error.javaClass.simpleName}".toByteArray())
                    }
                }
                writeFd.close()
            }
            return "ACCEPTED:${grant.grantId}".toByteArray()
        }

        override fun emergencyStop(signedStop: ByteArray?): ByteArray {
            client.stopAll()
            return "STOPPED".toByteArray()
        }
    }

    override fun onBind(intent: Intent?) = binder
    override fun onDestroy() { client.stopAll(); executor.shutdownNow(); super.onDestroy() }

    private fun preferences() = getSharedPreferences("broker-security", MODE_PRIVATE)
    private fun loadGrantKey(): PublicKey? = preferences().getString(KEY_GRANT_PUBLIC, null)?.let {
        KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(Base64.decode(it, Base64.NO_WRAP)))
    }

    companion object {
        private const val KEY_GRANT_PUBLIC = "grant-public-key-v1"
        private const val KEY_CONSUMED_NONCES = "consumed-nonces-v1"
    }
}
