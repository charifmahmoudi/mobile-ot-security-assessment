package com.atlasot.scout

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.atlasot.domain.CaseId
import com.atlasot.domain.CaseState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoldenCasePilotE2ETest {
    @Test fun createAndAuthorizeFromEmptyApplicationState() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("pilotPhase") == "create")
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertTrue(activity.findViewById<View>(MainActivity.PROFESSIONAL_CASE_CARD_ID) == null)
                activity.findViewById<View>(MainActivity.PROFESSIONAL_CASE_ACTION_ID).performClick()
            }
            scenario.onActivity { activity ->
                val text = screenText(activity)
                assertTrue(text.contains("North River Water Utility"))
                assertTrue(text.contains("10.0.2.2/32"))
                assertTrue(text.contains("Independent reviewer"))
            }
            capture("10a-golden-case-preparation")
            scenario.onActivity { activity ->
                activity.findViewById<View>(MainActivity.CREATE_PROFESSIONAL_CASE_ID).performClick()
            }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("AWAITING AUTHORIZATION"))
                activity.findViewById<CheckBox>(MainActivity.OPERATIONAL_APPROVAL_ID).isChecked = true
                activity.findViewById<View>(MainActivity.RECORD_APPROVALS_ID).performClick()
                assertTrue(screenText(activity).contains("operational and security approvals are required"))
                activity.findViewById<CheckBox>(MainActivity.SECURITY_APPROVAL_ID).isChecked = true
                activity.findViewById<View>(MainActivity.RECORD_APPROVALS_ID).performClick()
            }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("AUTHORIZED"))
                assertTrue(screenText(activity).contains("AUTHORIZATION_APPROVED"))
            }
            capture("11-golden-case-authorized")
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val restored = SqlCipherCaseRepository(context).load(CaseId(GoldenCustomerAssessment.CASE_ID))
        assertEquals(CaseState.AUTHORIZED, restored?.state)
    }

    @Test fun resumeAuthorizedCaseAfterHostForcedProcessRestart() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("pilotPhase") == "resume")
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<View>(MainActivity.PROFESSIONAL_CASE_CARD_ID).performClick()
            }
            scenario.onActivity { activity ->
                val text = screenText(activity)
                assertTrue(text.contains("AUTHORIZED"))
                assertTrue(text.contains("North River Treatment Plant"))
                assertTrue(text.contains("Amina El Idrissi"))
                assertTrue(text.contains("Omar Tazi"))
                activity.findViewById<View>(MainActivity.START_PROFESSIONAL_COLLECTION_ID).performClick()
            }
            scenario.onActivity { activity ->
                assertTrue(screenText(activity).contains("PROTECTED COLLECTION AVAILABLE"))
            }
            capture("12-golden-case-restored")
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = SqlCipherCaseRepository(context)
        assertEquals(CaseState.COLLECTING, repository.load(CaseId(GoldenCustomerAssessment.CASE_ID))?.state)
        repository.verifyIntegrity()
    }

    private fun screenText(activity: MainActivity): String =
        descendants(activity.window.decorView).filterIsInstance<TextView>().joinToString("\n") { it.text.toString() }

    private fun descendants(root: View): List<View> = buildList {
        add(root)
        if (root is ViewGroup) for (index in 0 until root.childCount) addAll(descendants(root.getChildAt(index)))
    }

    private fun capture(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        SystemClock.sleep(800)
        val resolver = instrumentation.targetContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name-api${Build.VERSION.SDK_INT}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AtlasOT")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val output = checkNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        resolver.openOutputStream(output).use { stream ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, checkNotNull(stream)))
        }
        bitmap.recycle()
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(output, values, null, null)
    }
}
