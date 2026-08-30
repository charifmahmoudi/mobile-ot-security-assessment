package com.atlasot.capturebroker;

import android.os.ParcelFileDescriptor;

interface IAtlasCaptureBroker {
    byte[] inspectInterfaces();
    byte[] startPassiveCapture(String interfaceId, long maxBytes, long durationMs, in ParcelFileDescriptor sink);
    void stopCapture();
}
