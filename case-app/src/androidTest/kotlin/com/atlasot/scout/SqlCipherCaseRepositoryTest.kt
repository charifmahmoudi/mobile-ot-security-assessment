package com.atlasot.scout

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atlasot.domain.ActorId
import com.atlasot.domain.ActorRef
import com.atlasot.domain.ActorRole
import com.atlasot.domain.AssessmentCase
import com.atlasot.domain.AssessmentContext
import com.atlasot.domain.AssessmentObjective
import com.atlasot.domain.CaseDataPolicy
import com.atlasot.domain.CaseId
import com.atlasot.domain.CaseScope
import com.atlasot.domain.CaseState
import com.atlasot.domain.CaseVersionConflictException
import com.atlasot.domain.EvidenceMethod
import com.atlasot.domain.StopCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.FileInputStream
import java.time.Instant
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SqlCipherCaseRepositoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun encryptedRepositoryRoundTripsCaseAndRejectsStaleWrite() {
        val repository = SqlCipherCaseRepository(context)
        val suffix = UUID.randomUUID().toString()
        val assessor = ActorRef(ActorId("assessor-$suffix"), "Repository assessor", ActorRole.ASSESSOR)
        val createdAt = Instant.parse("2026-09-02T10:00:00.123456789Z")
        val draft = AssessmentCase.createDraft(
            id = CaseId("CASE-$suffix"),
            caseNumber = "ATLAS-$suffix",
            context = AssessmentContext("Water Operator", "Encrypted repository test site", "Pump room", "P0-WATER"),
            objective = AssessmentObjective(
                question = "Can this professional case be restored without weakening its invariants?",
                requestedDecision = "Accept or reject durable repository behavior",
                stakeholderRole = "Assessor",
                successCriteria = setOf("Exact round trip", "Stale writes rejected"),
                evidenceNeeded = setOf("Encrypted database row"),
            ),
            scope = CaseScope(
                evidenceMethods = setOf(EvidenceMethod.H3_OFFLINE_IMPORT),
                physicalAreas = setOf("Pump room"),
            ),
            dataPolicy = CaseDataPolicy(
                classification = "test-confidential",
                retainPayloads = false,
                includeRawCapturesInExport = false,
                exportDestination = null,
                deleteAfter = createdAt.plusSeconds(86400),
            ),
            stopConditions = setOf(StopCondition.MANUAL_STOP),
            creator = assessor,
            createdAt = createdAt,
        )

        repository.save(draft, expectedVersion = null)
        val loaded = repository.load(draft.id) ?: throw AssertionError("case was not restored")
        assertEquals(CaseState.DRAFT, loaded.state)
        assertEquals(draft.auditTrail.events, loaded.auditTrail.events)
        assertEquals(draft.createdAt, loaded.createdAt)

        val prepared = loaded.prepare(assessor, createdAt.plusSeconds(1))
        repository.save(prepared, expectedVersion = loaded.version)
        val reloaded = repository.load(draft.id) ?: throw AssertionError("updated case was not restored")
        assertEquals(CaseState.PREPARED, reloaded.state)
        assertEquals(prepared.version, reloaded.version)
        assertTrue(reloaded.auditTrail.verifies())

        var staleRejected = false
        try {
            repository.save(prepared, expectedVersion = loaded.version)
        } catch (_: CaseVersionConflictException) {
            staleRejected = true
        }
        assertTrue(staleRejected)

        val summary = repository.list().firstOrNull { it.id == draft.id }
        assertNotNull(summary)
        assertEquals(CaseState.PREPARED, summary?.state)

        repository.verifyIntegrity()
        val dbFile = context.getDatabasePath(SqlCipherCaseRepository.DATABASE_NAME)
        assertTrue(dbFile.isFile)
        val sqliteHeader = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
        val actualHeader = ByteArray(sqliteHeader.size)
        FileInputStream(dbFile).use { input ->
            assertEquals(sqliteHeader.size, input.read(actualHeader))
        }
        assertFalse("SQLCipher database must not expose a plaintext SQLite header", actualHeader.contentEquals(sqliteHeader))
    }
}
