package com.atlasot.netbroker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.ParcelFileDescriptor
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atlasot.domain.ExecutionGrant
import com.atlasot.domain.Operation
import java.security.KeyPairGenerator
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrokerBinderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun signedPermissionBinderProvisionsOnceAndRejectsBadSignature() {
        val connected = CountDownLatch(1)
        var broker: IAtlasNetworkBroker? = null
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                broker = IAtlasNetworkBroker.Stub.asInterface(service)
                connected.countDown()
            }
            override fun onServiceDisconnected(name: ComponentName?) { broker = null }
        }

        val intent = Intent("com.atlasot.netbroker.BIND").setPackage(context.packageName)
        assertTrue(context.bindService(intent, connection, Context.BIND_AUTO_CREATE))
        try {
            assertTrue(connected.await(5, TimeUnit.SECONDS), "broker did not bind")
            val first = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
            assertContentEquals("PROVISIONED".toByteArray(), broker!!.provisionGrantKey(first.public.encoded))
            assertContentEquals("PROVISIONED".toByteArray(), broker!!.provisionGrantKey(first.public.encoded))
            val replacement = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
            assertContentEquals(
                "REJECTED:KEY_ALREADY_PROVISIONED".toByteArray(),
                broker!!.provisionGrantKey(replacement.public.encoded),
            )

            val now = Instant.now()
            val grant = ExecutionGrant(
                "g-binder", "c-binder", "a".repeat(64), Operation.MODBUS_DEVICE_ID_BASIC,
                1, "192.0.2.10", 502, 1, setOf("192.0.2.0/24"), emptySet(),
                2, 512, 0, 500, 1, now.minusSeconds(1), now.plusSeconds(20), "nonce-binder",
            )
            val pipe = ParcelFileDescriptor.createPipe()
            pipe[0].use { readEnd ->
                pipe[1].use { writeEnd ->
                    val envelope = GrantWireCodec.envelope(GrantWireCodec.encode(grant), ByteArray(64))
                    assertContentEquals(
                        "REJECTED:BAD_SIGNATURE".toByteArray(),
                        broker!!.execute(envelope, writeEnd),
                    )
                }
            }
        } finally {
            context.unbindService(connection)
        }
    }
}
