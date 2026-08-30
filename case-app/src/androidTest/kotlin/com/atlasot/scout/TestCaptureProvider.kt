package com.atlasot.scout

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File

/** Test-only provider that exercises the same content-URI path as Android's document picker. */
class TestCaptureProvider : ContentProvider() {
    override fun onCreate() = true

    override fun getType(uri: Uri) = if (uri.lastPathSegment?.endsWith("pcapng") == true) {
        "application/x-pcapng"
    } else "application/vnd.tcpdump.pcap"

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?,
    ): Cursor {
        val name = requireNotNull(uri.lastPathSegment)
        return MatrixCursor(arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)).apply {
            addRow(arrayOf(name, materialize(name).length()))
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        require(mode == "r")
        return ParcelFileDescriptor.open(materialize(requireNotNull(uri.lastPathSegment)), ParcelFileDescriptor.MODE_READ_ONLY)
    }

    private fun materialize(name: String): File {
        require('/' !in name && '\\' !in name)
        val target = File(requireNotNull(context).cacheDir, name)
        if (!target.isFile) requireNotNull(context).assets.open(name).use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
        return target
    }

    override fun insert(uri: Uri, values: ContentValues?) = throw UnsupportedOperationException()
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
}
