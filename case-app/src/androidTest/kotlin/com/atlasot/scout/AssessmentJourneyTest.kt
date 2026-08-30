package com.atlasot.scout

import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.widget.Button
import android.widget.CheckBox
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

    @Test fun outOfScopeTargetIsStoppedBeforeBrokerContact() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<Button>(MainActivity.PRIMARY_ACTION_ID).performClick()
                activity.findViewById<android.widget.EditText>(MainActivity.TARGET_FIELD_ID).setText("192.0.2.5")
                activity.findViewById<CheckBox>(MainActivity.AUTHORIZATION_CHECK_ID).isChecked = true
                activity.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID).performClick()
                val validation = activity.findViewById<TextView>(MainActivity.VALIDATION_MESSAGE_ID)
                assertTrue(validation.text.contains("outside the authorized CIDR"))
                assertTrue(activity.findViewById<TextView>(MainActivity.SCREEN_TITLE_ID).text.contains("New authorized assessment"))
            }
        }
    }

    @Test fun contentUriUploadReachesEvidenceBackedAssetReviewUi() {
        val fixtures = mapOf(
            "modbus.pcap" to "Modbus/TCP",
            "dnp3.pcap" to "DNP3",
            "iec104.pcap" to "IEC 60870-5-104",
            "bacnet.pcap" to "BACnet/IP",
        )
        fixtures.forEach { (file, protocol) ->
            val intent = Intent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                MainActivity::class.java,
            ).setData(Uri.parse("content://com.atlasot.scout.testcaptures/$file"))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            ActivityScenario.launch<MainActivity>(intent).use { scenario ->
                var title = ""
                var summary = ""
                var assetCount = 0
                repeat(80) {
                    scenario.onActivity { activity ->
                        title = activity.findViewById<TextView?>(MainActivity.SCREEN_TITLE_ID)?.text?.toString().orEmpty()
                        summary = activity.findViewById<TextView?>(MainActivity.RESULT_SUMMARY_ID)?.text?.toString().orEmpty()
                        assetCount = activity.findViewById<android.widget.LinearLayout?>(MainActivity.ASSET_LIST_ID)?.childCount ?: 0
                    }
                    if (title.contains("Passive analysis complete")) return@repeat
                    SystemClock.sleep(100)
                }
                assertTrue("title was: $title", title.contains("Passive analysis complete"))
                assertTrue("summary was: $summary", summary.contains(protocol))
                assertTrue(assetCount > 0)
            }
        }
    }

    @Test fun authorizedUiGrantIdentifiesLiveEmulatedController() {
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue(arguments.getString("activeTest") == "true")
        val expected = arguments.getString("expectedIdentity") ?: "MODBUS/TCP"
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity {
                it.findViewById<Button>(MainActivity.PRIMARY_ACTION_ID).performClick()
                it.findViewById<android.widget.EditText>(MainActivity.CASE_FIELD_ID).setText("E2E-WATER-001")
                it.findViewById<android.widget.EditText>(MainActivity.SITE_FIELD_ID).setText("CI treatment cell")
                it.findViewById<CheckBox>(MainActivity.AUTHORIZATION_CHECK_ID).isChecked = true
                val active = it.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID)
                assertTrue(active.isEnabled)
                active.performClick()
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
