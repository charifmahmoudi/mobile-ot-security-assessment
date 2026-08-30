package com.atlasot.scout

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    @Test fun guidedShellExposesFiveProfessionalAssessmentStages() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.findViewById<View>(MainActivity.SITE_CARD_ID).performClick() }
            scenario.onActivity { activity ->
                val body = screenText(activity)
                listOf("Overview", "Collect", "Assets", "Findings", "Report").forEach { assertTrue(body.contains(it)) }
                assertTrue(body.contains("Recommended next action"))
                assertTrue(body.contains("Deliverable in progress"))
                assertTrue(body.contains("Assessment package", ignoreCase = true))
                activity.findViewById<View>(MainActivity.FINDINGS_NAV_ID).performClick()
            }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("Assessment findings", ignoreCase = true))
                activity.findViewById<View>(MainActivity.REPORT_NAV_ID).performClick()
            }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("Report readiness", ignoreCase = true))
                assertTrue(screenText(activity).contains("Independent reviewer"))
            }
            capture("10-guided-report-readiness")
        }
    }

    @Test fun siteSelectionDashboardScanMenuAndInventoryFormOneJourney() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("Choose a site"))
                assertTrue(screenText(activity).contains("No packet is sent"))
                assertTrue(screenText(activity).contains("What the assessment produces"))
            }
            capture("01-site-selection")
            scenario.onActivity { it.findViewById<View>(MainActivity.SITE_CARD_ID).performClick() }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("North Water Treatment Plant"))
                assertTrue(activity.findViewById<View>(MainActivity.INVENTORY_ACTION_ID).isEnabled)
            }
            capture("03-site-dashboard")
            scenario.onActivity { it.findViewById<View>(MainActivity.PRIMARY_ACTION_ID).performClick() }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("Choose a method"))
                assertTrue(screenText(activity).contains("What do you need to establish?"))
                assertTrue(screenText(activity).contains("Analyze PCAP / PCAPNG"))
                assertTrue(screenText(activity).contains("Identify one known controller"))
            }
            capture("04-collection-methods")
            scenario.onActivity { it.findViewById<View>(MainActivity.ACTIVE_SCAN_OPTION_ID).performClick() }
            scenario.onActivity { activity ->
                val active = activity.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID)
                assertFalse(active.isEnabled)
                assertTrue(screenText(activity).contains("no register reads or writes"))
            }
            capture("05-active-authorization")
        }
    }

    @Test fun newSiteCapturesIndustryAndMultipleVendorContext() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { it.findViewById<View>(MainActivity.NEW_SITE_ACTION_ID).performClick() }
            capture("02-new-site")
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(MainActivity.SITE_NAME_FIELD_ID).setText("Demo bottling line")
                activity.findViewById<EditText>(MainActivity.SITE_LOCATION_FIELD_ID).setText("Casablanca · Packaging hall")
                activity.findViewById<Spinner>(MainActivity.INDUSTRY_SPINNER_ID).setSelection(4)
                val checks = descendants(activity.window.decorView).filterIsInstance<CheckBox>()
                checks.take(2).forEach { it.isChecked = true }
                assertTrue(checks.size >= 2)
                activity.findViewById<View>(MainActivity.CREATE_SITE_ACTION_ID).performClick()
                assertTrue(screenText(activity).contains("Demo bottling line"))
                assertTrue(screenText(activity).contains("Food & beverage", ignoreCase = true))
            }
        }
    }

    @Test fun inventorySupportsSearchFilterAndEvidenceNavigation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity {
                it.findViewById<View>(MainActivity.SITE_CARD_ID).performClick()
                it.findViewById<View>(MainActivity.INVENTORY_ACTION_ID).performClick()
            }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("Asset inventory", ignoreCase = true))
                assertTrue(activity.findViewById<LinearLayout>(MainActivity.INVENTORY_LIST_ID).childCount >= 4)
            }
            capture("07-asset-inventory")
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(MainActivity.INVENTORY_SEARCH_ID).setText("Siemens")
                assertTrue(activity.findViewById<LinearLayout>(MainActivity.INVENTORY_LIST_ID).childCount >= 1)
                activity.findViewById<Spinner>(MainActivity.INVENTORY_FILTER_ID).setSelection(1)
            }
        }
    }

    @Test fun dedicatedApplianceStreamUsesLiveCaptureBoundaryAndInventoryParser() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity {
                it.findViewById<View>(MainActivity.SITE_CARD_ID).performClick()
                it.findViewById<View>(MainActivity.PRIMARY_ACTION_ID).performClick()
                it.findViewById<View>(MainActivity.LIVE_CAPTURE_OPTION_ID).performClick()
            }
            var ready = false
            for (attempt in 0 until 50) {
                scenario.onActivity { activity ->
                    ready = activity.findViewById<Button?>(MainActivity.LIVE_CAPTURE_ACTION_ID)?.isEnabled == true
                }
                if (ready) break
                SystemClock.sleep(100)
            }
            assertTrue("capture broker did not become ready", ready)
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("CI emulation"))
                activity.findViewById<View>(MainActivity.LIVE_CAPTURE_ACTION_ID).performClick()
            }
            capture("05-live-span-ready")
            var page = ""
            var body = ""
            for (attempt in 0 until 80) {
                scenario.onActivity { activity ->
                    page = activity.findViewById<TextView?>(MainActivity.SCREEN_TITLE_ID)?.text?.toString().orEmpty()
                    body = screenText(activity)
                }
                if (page.contains("assets observed")) break
                SystemClock.sleep(100)
            }
            assertTrue("live capture page was: " + page, page.contains("assets observed"))
            assertTrue("live capture result was: " + body, body.contains("CI SPAN replay"))
            assertTrue(body.contains("Modbus/TCP"))
            capture("06-live-span-result")
        }
    }

    @Test fun outOfScopeTargetIsStoppedBeforeBrokerContact() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToActiveSetup(scenario)
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(MainActivity.TARGET_FIELD_ID).setText("192.0.2.5")
                activity.findViewById<CheckBox>(MainActivity.AUTHORIZATION_CHECK_ID).isChecked = true
                activity.findViewById<View>(MainActivity.ACTIVE_ACTION_ID).performClick()
                assertTrue(screenText(activity).contains("outside the authorized CIDR"))
                assertTrue(screenText(activity).contains("Identify one Modbus device"))
            }
            capture("06-out-of-scope-blocked")
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
            val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, MainActivity::class.java)
                .setData(Uri.parse("content://com.atlasot.scout.testcaptures/" + file))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            ActivityScenario.launch<MainActivity>(intent).use { scenario ->
                var page = ""
                var body = ""
                var assetCount = 0
                for (attempt in 0 until 80) {
                    scenario.onActivity { activity ->
                        page = activity.findViewById<TextView?>(MainActivity.SCREEN_TITLE_ID)?.text?.toString().orEmpty()
                        body = screenText(activity)
                        assetCount = activity.findViewById<LinearLayout?>(MainActivity.ASSET_LIST_ID)?.childCount ?: 0
                    }
                    if (page.contains("assets observed")) break
                    SystemClock.sleep(100)
                }
                assertTrue("page was: " + page, page.contains("assets observed"))
                assertTrue("screen was: " + body, body.contains(protocol))
                assertTrue(assetCount > 0)
                scenario.onActivity { activity ->
                    assertFalse(activity.findViewById<Button>(MainActivity.SAVE_ASSETS_ID).isEnabled)
                    assertTrue(screenText(activity).contains("explicitly accept"))
                }
                capture("08-passive-" + file.substringBefore('.'))
            }
        }
    }

    @Test fun authorizedUiGrantIdentifiesLiveEmulatedController() {
        val arguments = InstrumentationRegistry.getArguments()
        assumeTrue(arguments.getString("activeTest") == "true")
        val expected = arguments.getString("expectedIdentity") ?: "MODBUS/TCP"
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            navigateToActiveSetup(scenario)
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(MainActivity.CASE_FIELD_ID).setText("E2E-WATER-001")
                activity.findViewById<EditText>(MainActivity.SITE_FIELD_ID).setText("CI treatment cell")
                activity.findViewById<CheckBox>(MainActivity.AUTHORIZATION_CHECK_ID).isChecked = true
                val active = activity.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID)
                assertTrue(active.isEnabled)
                active.performClick()
            }
            var result = ""
            for (attempt in 0 until 60) {
                scenario.onActivity { result = screenText(it) }
                if (result.contains(expected) || result.contains("ACTION REQUIRED")) break
                SystemClock.sleep(250)
            }
            assertTrue("active screen was: " + result, result.contains(expected))
            capture(arguments.getString("screenshotPrefix") ?: "09-active-result")
        }
    }

    private fun navigateToActiveSetup(scenario: ActivityScenario<MainActivity>) {
        scenario.onActivity {
            it.findViewById<View>(MainActivity.SITE_CARD_ID).performClick()
            it.findViewById<View>(MainActivity.PRIMARY_ACTION_ID).performClick()
            it.findViewById<View>(MainActivity.ACTIVE_SCAN_OPTION_ID).performClick()
        }
    }

    private fun screenText(activity: MainActivity): String =
        descendants(activity.window.decorView).filterIsInstance<TextView>().joinToString("\n") { it.text.toString() }

    private fun findText(activity: MainActivity, value: String): TextView =
        descendants(activity.window.decorView).filterIsInstance<TextView>()
            .first { it.text.toString() == value && it.isClickable }

    private fun descendants(root: View): List<View> = buildList {
        add(root)
        if (root is ViewGroup) for (index in 0 until root.childCount) addAll(descendants(root.getChildAt(index)))
    }

    private fun capture(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        SystemClock.sleep(200)
        val resolver = instrumentation.targetContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name + "-api" + Build.VERSION.SDK_INT + ".png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AtlasOT")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val output = checkNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        resolver.openOutputStream(output).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, checkNotNull(stream)))
        }
        bitmap.recycle()
        values.clear(); values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(output, values, null, null)
    }
}
