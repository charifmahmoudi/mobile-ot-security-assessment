package com.atlasot.scout

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.atlasot.domain.CaseId
import com.atlasot.domain.CaseReviewDecision
import com.atlasot.domain.CaseReviewOutcome
import com.atlasot.domain.CaseState
import com.atlasot.domain.Sha256
import com.atlasot.domain.SnapshotId
import com.atlasot.domain.SnapshotMaterial
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProfessionalCaseApplicationTest {
    private fun application() = ProfessionalCaseApplication(
        SqlCipherCaseRepository(ApplicationProvider.getApplicationContext())
    )

    @Test fun missingApprovalFailsClosedInTheDomainAuthorizationPath() {
        val app = application()
        val now = Instant.now()
        val case = app.createPrepared(GoldenCustomerAssessment.input(now, uniqueId("MISSING")), now)
        val proposal = app.proposeAuthorization(
            case.id,
            operationalApproved = true,
            securityApproved = false,
            validFrom = now.minusSeconds(1),
            validUntil = now.plusSeconds(3600),
            artifactText = "one-sided approval must not authorize",
            at = now.plusMillis(10),
        )

        val failure = runCatching { app.authorize(case.id, proposal, now.plusMillis(20)) }.exceptionOrNull()
        assertTrue(failure is IllegalArgumentException)
        assertEquals(CaseState.AWAITING_AUTHORIZATION, app.load(case.id)?.state)
    }

    @Test fun expiredAuthorityCannotStartProtectedCollection() {
        val app = application()
        val now = Instant.now()
        val case = app.createPrepared(GoldenCustomerAssessment.input(now, uniqueId("EXPIRED")), now)
        val proposal = app.proposeAuthorization(
            case.id,
            operationalApproved = true,
            securityApproved = true,
            validFrom = now,
            validUntil = now.plusSeconds(30),
            artifactText = "short deterministic authorization",
            at = now.plusMillis(10),
        )
        app.authorize(case.id, proposal, now.plusMillis(20))

        val failure = runCatching { app.startCollection(case.id, now.plusSeconds(31)) }.exceptionOrNull()
        assertTrue(failure is IllegalArgumentException)
        assertEquals(CaseState.AUTHORIZED, app.load(case.id)?.state)
    }

    @Test fun staleScopeFingerprintCannotAuthorizeAnotherCase() {
        val app = application()
        val now = Instant.now()
        val original = app.createPrepared(GoldenCustomerAssessment.input(now, uniqueId("ORIGINAL")), now)
        val stale = app.proposeAuthorization(
            original.id,
            operationalApproved = true,
            securityApproved = true,
            validFrom = now.minusSeconds(1),
            validUntil = now.plusSeconds(3600),
            artifactText = "authorization bound to original scope",
            at = now.plusMillis(10),
        )
        val changedInput = GoldenCustomerAssessment.input(now, uniqueId("CHANGED")).copy(
            scopeCidrs = setOf("10.0.2.3/32")
        )
        val changed = app.createPrepared(changedInput, now.plusSeconds(1))

        val failure = runCatching { app.authorize(changed.id, stale, now.plusSeconds(2)) }.exceptionOrNull()
        assertTrue(failure is IllegalArgumentException)
        assertEquals(CaseState.AWAITING_AUTHORIZATION, app.load(changed.id)?.state)
    }

    @Test fun finalizedAndSupersededCasesStayClosedAfterEncryptedRestore() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val repository = SqlCipherCaseRepository(context)
        val app = ProfessionalCaseApplication(repository)
        val now = Instant.now()
        val awaiting = app.createPrepared(GoldenCustomerAssessment.input(now, uniqueId("FINAL")), now)
        val proposal = app.proposeAuthorization(
            awaiting.id, true, true, now.minusSeconds(1), now.plusSeconds(3600),
            "finalized integration authorization", now.plusMillis(10),
        )
        val authorized = app.authorize(awaiting.id, proposal, now.plusMillis(20))
        val participants = requireNotNull(app.participants(awaiting.id))
        val collecting = app.startCollection(authorized.id, now.plusMillis(30))
        val final = collecting
            .beginEvidenceReview(participants.assessor, now.plusMillis(40))
            .beginReconciliation(participants.assessor, now.plusMillis(50))
            .beginAssessment(participants.assessor, now.plusMillis(60))
            .requestReview(participants.assessor, now.plusMillis(70))
            .recordReview(CaseReviewDecision(
                participants.independentReviewer,
                CaseReviewOutcome.ACCEPTED,
                "Pilot integration QA accepted",
                now.plusMillis(80),
            ))
            .finalizeCase(
                participants.independentReviewer,
                now.plusMillis(90),
                SnapshotId("SNAP-${awaiting.id.value}"),
                SnapshotMaterial(
                    mapOf("golden-outcomes" to Sha256.digest("golden outcomes v1")),
                    "pilot-instrumentation",
                    mapOf("P0-WATER" to Sha256.digest("P0-WATER fixture v1")),
                ),
            )
        repository.save(final, expectedVersion = collecting.version)

        assertTrue(runCatching { app.startCollection(final.id, now.plusMillis(100)) }.isFailure)
        assertEquals(CaseState.FINALIZED, app.load(final.id)?.state)

        val supersession = final.supersedeWith(CaseId(uniqueId("SUCCESSOR")), participants.assessor, now.plusMillis(110))
        repository.save(supersession.superseded, expectedVersion = final.version)
        repository.saveNewCase(supersession.successor, participants)
        assertTrue(runCatching { app.startCollection(supersession.superseded.id, now.plusMillis(120)) }.isFailure)
        assertEquals(CaseState.SUPERSEDED, app.load(supersession.superseded.id)?.state)
    }

    private fun uniqueId(prefix: String) = "GOLDEN-$prefix-${UUID.randomUUID()}"
}
