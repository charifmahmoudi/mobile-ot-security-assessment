package com.atlasot.netbroker

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrokerBoundaryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun brokerHasNetworkPermissionAndSignatureProtectedService() {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        assertTrue(packageInfo.requestedPermissions.orEmpty().contains(Manifest.permission.INTERNET))
        val service = context.packageManager.getServiceInfo(ComponentName(context, BrokerService::class.java), PackageManager.GET_META_DATA)
        assertTrue(service.exported)
        assertEquals("com.atlasot.permission.BIND_NETWORK_BROKER", service.permission)
        val permission = context.packageManager.getPermissionInfo(service.permission, 0)
        assertEquals(PermissionInfo.PROTECTION_SIGNATURE, permission.protection and PermissionInfo.PROTECTION_MASK_BASE)
    }
}
