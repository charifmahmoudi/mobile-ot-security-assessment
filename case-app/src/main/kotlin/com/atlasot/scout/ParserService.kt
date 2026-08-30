package com.atlasot.scout

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Placeholder process boundary for the Rust parser. The manifest runs this service under an
 * isolated UID with no permissions. M2 replaces this binder with the bounded parser AIDL.
 */
class ParserService : Service() {
    private val binder = Binder()
    override fun onBind(intent: Intent?): IBinder = binder
}
