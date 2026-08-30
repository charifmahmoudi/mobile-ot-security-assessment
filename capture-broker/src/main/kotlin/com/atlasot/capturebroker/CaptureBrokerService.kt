package com.atlasot.capturebroker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.ParcelFileDescriptor
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Android-side contract for the dedicated appliance capture daemon.
 *
 * Debug builds stream a labeled, immutable CI fixture through the exact Binder/FD
 * boundary. Release builds fail closed until installed as part of the signed
 * appliance image with the native AF_PACKET backend.
 */
class CaptureBrokerService : Service() {
    private val worker = Executors.newSingleThreadExecutor()
    @Volatile private var running: Future<*>? = null

    private val binder = object : IAtlasCaptureBroker.Stub() {
        override fun inspectInterfaces(): ByteArray = if (BuildConfig.DEBUG) {
            """[{"id":"span0","label":"USB Ethernet · SPAN/TAP","rxOnly":true,"addressed":false,"available":true,"backend":"EMULATED_APPLIANCE"}]"""
                .toByteArray()
        } else {
            """[{"id":"span0","label":"Dedicated capture interface","rxOnly":true,"addressed":false,"available":false,"backend":"NATIVE_DAEMON_REQUIRED"}]"""
                .toByteArray()
        }

        override fun startPassiveCapture(interfaceId: String, maxBytes: Long, durationMs: Long, sink: ParcelFileDescriptor): ByteArray {
            require(interfaceId == "span0") { "Interface is not allowlisted" }
            require(maxBytes in 1..16L * 1024 * 1024) { "Capture byte limit is invalid" }
            require(durationMs in 1_000..300_000) { "Capture duration is invalid" }
            require(running?.isDone != false) { "A capture is already active" }
            if (!BuildConfig.DEBUG) return "REJECTED:NATIVE_DAEMON_REQUIRED".toByteArray()
            running = worker.submit {
                sink.use { descriptor ->
                    FileOutputStream(descriptor.fileDescriptor).use { output ->
                        assets.open("modbus.pcap").use { input ->
                            val buffer = ByteArray(16 * 1024)
                            var total = 0L
                            while (!Thread.currentThread().isInterrupted) {
                                val count = input.read(buffer)
                                if (count < 0) break
                                val accepted = minOf(count.toLong(), maxBytes - total).toInt()
                                if (accepted <= 0) break
                                output.write(buffer, 0, accepted); total += accepted
                            }
                        }
                    }
                }
            }
            return "ACCEPTED:EMULATED_SPAN_STREAM".toByteArray()
        }

        override fun stopCapture() { running?.cancel(true) }
    }

    override fun onBind(intent: Intent?): IBinder? =
        if (intent?.action == "com.atlasot.capturebroker.BIND") binder else null

    override fun onDestroy() {
        running?.cancel(true); worker.shutdownNow(); super.onDestroy()
    }
}
