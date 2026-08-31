package com.atlasot.scout

import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A deliberately paced journey for adb screenrecord.
 *
 * Unlike the screenshot acceptance tests, this test exists to produce a
 * continuous, live emulator capture. Every pause keeps the current app state
 * visible long enough for a viewer to understand the operator decision.
 */
@RunWith(AndroidJUnit4::class)
class LiveDemoCaptureTest {
    @Test fun recordPassiveFirstAssessmentJourney() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            pause()

            scenario.onActivity { it.findViewById<View>(MainActivity.SITE_CARD_ID).performClick() }
            pause()

            scenario.onActivity { it.findViewById<View>(MainActivity.PRIMARY_ACTION_ID).performClick() }
            pause()

            scenario.onActivity { it.findViewById<View>(MainActivity.LIVE_CAPTURE_OPTION_ID).performClick() }
            waitFor(scenario) { activity ->
                activity.findViewById<Button?>(MainActivity.LIVE_CAPTURE_ACTION_ID)?.isEnabled == true
            }
            pause()

            scenario.onActivity { it.findViewById<View>(MainActivity.LIVE_CAPTURE_ACTION_ID).performClick() }
            waitFor(scenario) { activity ->
                activity.findViewById<TextView?>(MainActivity.SCREEN_TITLE_ID)
                    ?.text?.toString()?.contains("assets observed") == true
            }
            pause(5_000)

            scenario.onActivity { it.findViewById<View>(MainActivity.ASSETS_NAV_ID).performClick() }
            pause()

            scenario.onActivity { it.findViewById<View>(MainActivity.REPORT_NAV_ID).performClick() }
            pause(5_000)

            scenario.onActivity { activity ->
                val matches = mutableListOf<View>()
                activity.window.decorView.findViewsWithText(matches, "Report readiness", View.FIND_VIEWS_WITH_TEXT)
                assertTrue(matches.isNotEmpty())
            }
        }
    }

    private fun pause(milliseconds: Long = 3_500) = SystemClock.sleep(milliseconds)

    private fun waitFor(
        scenario: ActivityScenario<MainActivity>,
        condition: (MainActivity) -> Boolean,
    ) {
        var ready = false
        repeat(80) {
            scenario.onActivity { ready = condition(it) }
            if (ready) return
            SystemClock.sleep(100)
        }
        assertTrue("Timed out waiting for the live-demo screen", ready)
    }
}
