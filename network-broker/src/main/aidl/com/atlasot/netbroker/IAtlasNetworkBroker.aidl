package com.atlasot.netbroker;

import android.os.ParcelFileDescriptor;

interface IAtlasNetworkBroker {
    byte[] inspectInterfaces(in byte[] signedRequest);
    byte[] provisionGrantKey(in byte[] x509GrantPublicKey);
    byte[] execute(in byte[] grantEnvelope, in ParcelFileDescriptor evidenceSink);
    byte[] emergencyStop(in byte[] signedStop);
}
