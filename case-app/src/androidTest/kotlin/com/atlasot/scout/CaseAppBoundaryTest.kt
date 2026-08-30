package com.atlasot.scout

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CaseAppBoundaryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test fun caseAppDoesNotRequestInternetPermission() {
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        assertFalse(info.requestedPermissions.orEmpty().contains(Manifest.permission.INTERNET))
    }

    @Test fun parserServiceRunsAsAnIsolatedNonExportedProcess() {
        val info = context.packageManager.getServiceInfo(
            ComponentName(context, ParserService::class.java), PackageManager.GET_META_DATA
        )
        assertTrue(info.flags and ServiceInfo.FLAG_ISOLATED_PROCESS != 0)
        assertFalse(info.exported)
        assertNotNull(info.name)
    }

    @Test fun applicationLaunchesAndShowsOfflineBoundary() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val text = activity.findViewById<android.widget.TextView>(MainActivity.STATUS_VIEW_ID)
                assertTrue(text.text.contains("P0-WATER"))
            }
        }
    }
}
