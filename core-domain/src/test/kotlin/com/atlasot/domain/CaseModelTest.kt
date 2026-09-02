package com.atlasot.domain

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CaseModelTest {
    private val createdAt = Instant.parse("2026-09-02T08:00:00Z")
    private val validFrom = createdAt.plusSeconds(60)
    private val validUntil = createdAt.plusSeconds(3600)

    private val assessor = ActorRef(ActorId("assessor-1"), "Lead assessor", ActorRole.ASSESSOR)
    private val operations = ActorRef(ActorId("ops-1"), "Operations approver", ActorRole.OPERATIONAL_APPROVER)
    private val security = ActorRef(ActorId("security-1"), "Security approver", ActorRole.SECURITY_APPROVER)
    private val reviewer = ActorRef(ActorId("reviewer-1"), "Independent reviewer", ActorRole.REVIEWER)

    private fun scope(exclusions: Set<Int> = emptySet()) = CaseScope(
        cidrs = setOf(IPv4Cidr.parse("10.20.30.0/24")),
        excludedAddresses = exclusions,
        allowedOperations = setOf(Operation.MODBUS_DEVICE_ID_BASIC),
        evidenceMethods = setOf(EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY, EvidenceMethod.H3_OFFLINE_IMPORT),
        physicalAreas = setOf("Pumping control room"),
    )

    private fun policy() = CaseDataPolicy(
        classification = "customer-confidential",
        retainPayloads = false,
        includeRawCapturesInExport = false,
        exportDestination = "customer-controlled encrypted export",
        deleteAfter = createdAt.plusSeconds(30 * 24 * 3600),
    )

    private fun draft(scope: CaseScope = scope()) = AssessmentCase.createDraft(
        id = CaseId("CASE-001"),
        caseNumber = "ATLAS-2026-001",
        context = AssessmentContext("Water Operator", "North Pumping Station", "Pumping control", "P0-WATER"),
        objective = AssessmentObjective(
            question = "Does the installed control equipment match the expected baseline?",
            requestedDecision = "Accept handover or retain explicit reconciliation exceptions",
            stakeholderRole = "Commissioning engineer",
            successCriteria = setOf("Every critical expected record has an explicit reconciliation state"),
            evidenceNeeded = setOf("Customer asset export", "Approved field evidence"),
        ),
        scope = scope,
        dataPolicy = policy(),
        stopConditions = setOf(StopCondition.PROCESS_ALARM, StopCondition.APPROVER_REQUEST, StopCondition.MANUAL_STOP),
        creator = assessor,
        createdAt = createdAt,
    )

    private fun authorized(scope: CaseScope = scope()): AssessmentCase {
        val waiting = draft(scope).prepare(assessor, createdAt.plusSeconds(1))
            .requestAuthorization(assessor, createdAt.plusSeconds(2))
        val authorization = CaseAuthorization(
            id = AuthorizationId("AUTH-001"),
            artifactHash = Sha256.digest("signed authorization artifact"),
            validFrom = validFrom,
            validUntil = validUntil,
            scopeHash = waiting.scopeHash,
            dataPolicyHash = waiting.dataPolicyHash,
            approvals = setOf(
                Approval(operations, createdAt.plusSeconds(3)),
                Approval(security, createdAt.plusSeconds(4)),
            ),
        )
        return waiting.authorize(assessor, createdAt.plusSeconds(5), authorization)
    }

    @Test
    fun `complete professional lifecycle freezes a verifiable snapshot`() {
        val final = authorized()
            .startCollection(assessor, validFrom)
            .pauseCollection(operations, validFrom.plusSeconds(10), "maintenance hold")
            .beginEvidenceReview(assessor, validFrom.plusSeconds(11))
            .beginReconciliation(assessor, validFrom.plusSeconds(12))
            .beginAssessment(assessor, validFrom.plusSeconds(13))
            .requestReview(assessor, validFrom.plusSeconds(14))
            .recordReview(CaseReviewDecision(reviewer, CaseReviewOutcome.ACCEPTED, "QA gates passed", validFrom.plusSeconds(15)))
            .finalizeCase(
                reviewer = reviewer,
                at = validFrom.plusSeconds(16),
                snapshotId = SnapshotId("SNAP-001"),
                material = SnapshotMaterial(
                    objectHashes = mapOf(
                        "inventory" to Sha256.digest("inventory-v1"),
                        "findings" to Sha256.digest("findings-v1"),
                    ),
                    toolBuild = "atlas-test-build",
                    packVersions = mapOf("water-pack" to Sha256.digest("water-pack-v1")),
                ),
            )

        assertEquals(CaseState.FINALIZED, final.state)
        assertTrue(final.auditTrail.verifies())
        val snapshot = assertNotNull(final.finalizedSnapshot)
        assertEquals(final.auditTrail.head, snapshot.auditHead)
        assertEquals(final.authorization?.artifactHash, snapshot.authorizationArtifactHash)
        assertFailsWith<IllegalArgumentException> { final.startCollection(assessor, validFrom.plusSeconds(20)) }
    }

    @Test
    fun `authorization is bound to the exact scope and data policy`() {
        val waiting = draft().prepare(assessor, createdAt.plusSeconds(1))
            .requestAuthorization(assessor, createdAt.plusSeconds(2))
        val mismatchedScope = scope(setOf(IPv4Cidr.parseAddress("10.20.30.99")))
        val authorization = CaseAuthorization(
            AuthorizationId("AUTH-WRONG"),
            Sha256.digest("authorization"),
            validFrom,
            validUntil,
            mismatchedScope.fingerprint(),
            waiting.dataPolicyHash,
            setOf(Approval(operations, createdAt.plusSeconds(3)), Approval(security, createdAt.plusSeconds(4))),
        )

        assertFailsWith<IllegalArgumentException> {
            waiting.authorize(assessor, createdAt.plusSeconds(5), authorization)
        }
    }

    @Test
    fun `active execution requires collecting state exact operation scope and exclusion compliance`() {
        val excluded = IPv4Cidr.parseAddress("10.20.30.99")
        val case = authorized(scope(setOf(excluded))).startCollection(assessor, validFrom)

        case.assertOperationAllowed(Operation.MODBUS_DEVICE_ID_BASIC, IPv4Cidr.parseAddress("10.20.30.10"), validFrom)
        assertFailsWith<IllegalArgumentException> {
            case.assertOperationAllowed(Operation.OPCUA_DISCOVERY, IPv4Cidr.parseAddress("10.20.30.10"), validFrom)
        }
        assertFailsWith<IllegalArgumentException> {
            case.assertOperationAllowed(Operation.MODBUS_DEVICE_ID_BASIC, excluded, validFrom)
        }
        assertFailsWith<IllegalArgumentException> {
            case.assertOperationAllowed(Operation.MODBUS_DEVICE_ID_BASIC, IPv4Cidr.parseAddress("10.20.31.10"), validFrom)
        }
    }

    @Test
    fun `reviewer acceptance is mandatory before finalization`() {
        val pending = authorized().startCollection(assessor, validFrom)
            .beginEvidenceReview(assessor, validFrom.plusSeconds(1))
            .beginReconciliation(assessor, validFrom.plusSeconds(2))
            .beginAssessment(assessor, validFrom.plusSeconds(3))
            .requestReview(assessor, validFrom.plusSeconds(4))

        assertFailsWith<IllegalArgumentException> {
            pending.finalizeCase(
                reviewer,
                validFrom.plusSeconds(5),
                SnapshotId("SNAP-NO-REVIEW"),
                SnapshotMaterial(mapOf("inventory" to Sha256.digest("inventory")), "build", emptyMap()),
            )
        }
    }

    @Test
    fun `supersession preserves finalized revision and creates a new draft revision`() {
        val final = authorized().startCollection(assessor, validFrom)
            .beginEvidenceReview(assessor, validFrom.plusSeconds(1))
            .beginReconciliation(assessor, validFrom.plusSeconds(2))
            .beginAssessment(assessor, validFrom.plusSeconds(3))
            .requestReview(assessor, validFrom.plusSeconds(4))
            .recordReview(CaseReviewDecision(reviewer, CaseReviewOutcome.ACCEPTED, "accepted", validFrom.plusSeconds(5)))
            .finalizeCase(
                reviewer, validFrom.plusSeconds(6), SnapshotId("SNAP-001"),
                SnapshotMaterial(mapOf("inventory" to Sha256.digest("inventory")), "build", emptyMap())
            )

        val result = final.supersedeWith(CaseId("CASE-002"), assessor, validFrom.plusSeconds(7))
        assertEquals(CaseState.SUPERSEDED, result.superseded.state)
        assertEquals(CaseState.DRAFT, result.successor.state)
        assertEquals(2, result.successor.revision)
        assertEquals(SnapshotId("SNAP-001"), result.successor.supersedesSnapshotId)
        assertEquals(final.caseNumber, result.successor.caseNumber)
        assertTrue(result.superseded.auditTrail.verifies())
        assertTrue(result.successor.auditTrail.verifies())
    }

    @Test
    fun `audit restoration rejects tampered history`() {
        val case = authorized()
        val events = case.auditTrail.events
        val last = events.last()
        val tampered = events.dropLast(1) + last.copy(detailsHash = Sha256.digest("tampered"))

        assertFailsWith<IllegalArgumentException> { AuditTrail.restore(tampered) }
    }

    @Test
    fun `p0 preparation rejects undeclared active protocol expansion`() {
        val broadScope = CaseScope(
            cidrs = setOf(IPv4Cidr.parse("10.20.30.0/24")),
            allowedOperations = setOf(Operation.MODBUS_DEVICE_ID_BASIC, Operation.OPCUA_DISCOVERY),
            evidenceMethods = setOf(EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY),
            physicalAreas = setOf("Pumping control room"),
        )
        assertFailsWith<IllegalArgumentException> { draft(broadScope).prepare(assessor, createdAt.plusSeconds(1)) }
    }
}
