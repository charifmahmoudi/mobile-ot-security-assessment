package com.atlasot.scout

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A paced, end-to-end buyer demo recorded by adb screenrecord.
 *
 * The journey intentionally follows the assessment user story rather than a
 * gallery of screens: establish context, collect passive evidence, accept
 * observations, reason about a finding, demonstrate a scope stop, close one
 * identity gap with the controlled CI Modbus target, and finish at report
 * readiness. Every transition is the running Android application.
 */
@RunWith(AndroidJUnit4::class)
class LiveDemoCaptureTest {
    @Test
    fun recordWaterAssessmentUserStory() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            pause(2_000)

            // User story 1: enter a bounded water-treatment workspace.
            click(scenario, MainActivity.SITE_CARD_ID)
            waitForText(scenario, "North Water Treatment Plant")
            pause()

            // User story 2: choose the least intrusive evidence method.
            click(scenario, MainActivity.PRIMARY_ACTION_ID)
            waitForText(scenario, "Choose a method")
            pause()
            click(scenario, MainActivity.LIVE_CAPTURE_OPTION_ID)
            waitFor(scenario) { activity ->
                activity.findViewById<Button?>(MainActivity.LIVE_CAPTURE_ACTION_ID)?.isEnabled == true
            }
            pause()

            // User story 3: capture passive evidence and explicitly accept it.
            click(scenario, MainActivity.LIVE_CAPTURE_ACTION_ID)
            waitFor(scenario) { activity ->
                activity.findViewById<TextView?>(MainActivity.SCREEN_TITLE_ID)
                    ?.text?.toString()?.contains("assets observed") == true
            }
            pause(2_500)
            scenario.onActivity { activity ->
                val list = activity.findViewById<LinearLayout>(MainActivity.ASSET_LIST_ID)
                val checkboxes = descendants(list).filterIsInstance<CheckBox>()
                assertTrue("Expected passive observations to review", checkboxes.isNotEmpty())
                checkboxes.forEach { it.isChecked = true }
                descendants(activity.window.decorView).filterIsInstance<ScrollView>().firstOrNull()
                    ?.smoothScrollTo(0, list.height)
            }
            pause(2_000)
            click(scenario, MainActivity.SAVE_ASSETS_ID)
            waitForText(scenario, "Asset inventory")
            pause(2_500)

            // User story 4: reason from evidence, not raw device counts.
            click(scenario, MainActivity.FINDINGS_NAV_ID)
            waitForText(scenario, "Assessment findings")
            pause(2_500)

            // User story 5: return to Collect to close one unresolved identity.
            click(scenario, MainActivity.COLLECT_NAV_ID)
            waitForText(scenario, "Choose a method")
            click(scenario, MainActivity.ACTIVE_SCAN_OPTION_ID)
            waitForText(scenario, "Identify one Modbus device")
            pause(2_500)

            // First prove the local fail-closed scope boundary.
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(MainActivity.TARGET_FIELD_ID).setText("192.0.2.5")
                activity.findViewById<CheckBox>(MainActivity.AUTHORIZATION_CHECK_ID).isChecked = true
                activity.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID).performClick()
            }
            waitForText(scenario, "outside the authorized CIDR")
            pause(2_500)

            // Then run the exact authorized identity request against the CI testbed.
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(MainActivity.CASE_FIELD_ID).setText("E2E-WATER-DEMO")
                activity.findViewById<EditText>(MainActivity.SITE_FIELD_ID).setText("Treatment line 2")
                activity.findViewById<EditText>(MainActivity.TARGET_FIELD_ID).setText("10.0.2.2")
                activity.findViewById<EditText>(MainActivity.SCOPE_FIELD_ID).setText("10.0.2.0/24")
                activity.findViewById<EditText>(MainActivity.UNIT_FIELD_ID).setText("1")
                activity.findViewById<Button>(MainActivity.ACTIVE_ACTION_ID).performClick()
            }
            waitFor(scenario, 120) { activity ->
                val title = activity.findViewById<TextView?>(MainActivity.SCREEN_TITLE_ID)?.text?.toString().orEmpty()
                title.contains("Controller identified") || title.contains("Modbus service confirmed")
            }
            pause(3_000)

            // Add the validated identity and show the evidence-specific inventory view.
            click(scenario, MainActivity.SAVE_ASSETS_ID)
            waitForText(scenario, "Asset inventory")
            scenario.onActivity { activity ->
                activity.findViewById<Spinner>(MainActivity.INVENTORY_FILTER_ID).setSelection(6)
            }
            pause(2_500)

            // End on the buyer outcome: findings and explicit handoff blockers.
            click(scenario, MainActivity.FINDINGS_NAV_ID)
            waitForText(scenario, "Assessment findings")
            pause(2_000)
            click(scenario, MainActivity.REPORT_NAV_ID)
            waitForText(scenario, "Report readiness")
            pause(4_000)
        }
    }

    private fun click(scenario: ActivityScenario<MainActivity>, id: Int) {
        scenario.onActivity { activity -> activity.findViewById<View>(id).performClick() }
    }

    private fun waitForText(scenario: ActivityScenario<MainActivity>, value: String) {
        waitFor(scenario) { activity -> screenText(activity).contains(value, ignoreCase = true) }
    }

    private fun screenText(activity: MainActivity): String =
        descendants(activity.window.decorView).filterIsInstance<TextView>()
            .joinToString("\n") { it.text.toString() }

    private fun descendants(root: View): List<View> = buildList {
        add(root)
        if (root is ViewGroup) {
            for (index in 0 until root.childCount) addAll(descendants(root.getChildAt(index)))
        }
    }

    private fun pause(milliseconds: Long = 1_800) = SystemClock.sleep(milliseconds)

    private fun waitFor(
        scenario: ActivityScenario<MainActivity>,
        attempts: Int = 80,
        condition: (MainActivity) -> Boolean,
    ) {
        var ready = false
        repeat(attempts) {
            scenario.onActivity { ready = condition(it) }
            if (ready) return
            SystemClock.sleep(125)
        }
        assertTrue("Timed out waiting for the live-demo screen", ready)
    }
}
