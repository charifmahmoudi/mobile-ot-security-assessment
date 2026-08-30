package com.atlasot.scout;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Test-only content provider with no Kotlin runtime dependency in the target process. */
public final class TestCaptureProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }

    @Override public String getType(Uri uri) {
        String name = uri.getLastPathSegment();
        return name != null && name.endsWith("pcapng")
            ? "application/x-pcapng" : "application/vnd.tcpdump.pcap";
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
        String name = requireName(uri);
        MatrixCursor cursor = new MatrixCursor(new String[] { OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE });
        cursor.addRow(new Object[] { name, materialize(name).length() });
        return cursor;
    }

    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws IOException {
        if (!"r".equals(mode)) throw new IllegalArgumentException("read-only provider");
        return ParcelFileDescriptor.open(materialize(requireName(uri)), ParcelFileDescriptor.MODE_READ_ONLY);
    }

    private String requireName(Uri uri) {
        String name = uri.getLastPathSegment();
        if (name == null || name.contains("/") || name.contains("\\")) throw new IllegalArgumentException("invalid fixture name");
        return name;
    }

    private File materialize(String name) {
        File target = new File(getContext().getCacheDir(), name);
        if (target.isFile()) return target;
        try (InputStream input = getContext().getAssets().open(name);
             FileOutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) >= 0) output.write(buffer, 0, count);
            return target;
        } catch (IOException error) {
            throw new IllegalStateException("fixture unavailable: " + name, error);
        }
    }

    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
