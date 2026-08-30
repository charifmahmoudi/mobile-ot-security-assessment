package com.atlasot.scout

import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssessmentJourneyTest {
    @Test fun homeExplainsSafetyAndActiveAuthorizationBeforeScanning() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val safety = activity.findViewById<TextView>(MainActivity.SAFETY_STATUS_ID)
                assertTrue(safety.text.contains("No packet is sent"))
                activity.findViewById<Button>(MainActivity.PRIMARY_ACTION_ID).performClick()
                val active = activity.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID)
                assertFalse(active.isEnabled)
                assertTrue(activity.findViewById<TextView>(MainActivity.ACTIVE_LIMITS_ID).text.contains("no register writes"))
            }
        }
    }

    @Test fun researchPcapsReachEvidenceBackedAssetReviewUi() {
        val fixtures = mapOf(
            "modbus.pcap" to "Modbus/TCP",
            "dnp3.pcap" to "DNP3",
            "iec104.pcap" to "IEC 60870-5-104",
            "bacnet.pcap" to "BACnet/IP",
        )
        val testAssets = InstrumentationRegistry.getInstrumentation().context.assets
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            fixtures.forEach { (file, protocol) ->
                scenario.onActivity { activity ->
                    testAssets.open(file).use { activity.analyzeCaptureForTest(file, it) }
                    val title = activity.findViewById<TextView>(MainActivity.SCREEN_TITLE_ID)
                    val summary = activity.findViewById<TextView>(MainActivity.RESULT_SUMMARY_ID)
                    val assets = activity.findViewById<android.widget.LinearLayout>(MainActivity.ASSET_LIST_ID)
                    assertTrue(title.text.contains("Passive analysis complete"))
                    assertTrue(summary.text.contains(protocol))
                    assertTrue(assets.childCount > 0)
                }
            }
        }
    }

    @Test fun authorizedUiGrantIdentifiesLiveEmulatedController() {
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue(arguments.getString("activeTest") == "true")
        val expected = arguments.getString("expectedIdentity") ?: "MODBUS/TCP"
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity {
                it.runActiveDiscovery("E2E-WATER-001", "CI treatment cell", "10.0.2.2", "10.0.2.0/24", 1)
            }
            var result = ""
            repeat(60) {
                scenario.onActivity { activity ->
                    result = activity.findViewById<TextView?>(MainActivity.ACTIVE_RESULT_ID)?.text?.toString().orEmpty()
                }
                if (result.contains(expected) || result.contains("ACTION REQUIRED")) return@repeat
                SystemClock.sleep(250)
            }
            assertTrue("active result was: $result", result.contains(expected))
        }
    }
}
