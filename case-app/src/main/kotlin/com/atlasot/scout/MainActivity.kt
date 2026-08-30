package com.atlasot.scout

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        root.addView(TextView(this).apply {
            id = STATUS_VIEW_ID
            text = "Atlas OT Scout\nP0-WATER offline case application"
            textSize = 22f
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "Network authority is isolated in the signed broker package."
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 0)
        })
        setContentView(root)
    }

    companion object { const val STATUS_VIEW_ID = 0x41544C41 }
}
