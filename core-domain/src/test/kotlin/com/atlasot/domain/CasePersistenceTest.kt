package com.atlasot.domain

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CasePersistenceTest {
    private val createdAt = Instant.parse("2026-09-02T09:00:00.123456789Z")
    private val validFrom = createdAt.plusSeconds(60)
    private val validUntil = createdAt.plusSeconds(3600)
    private val assessor = ActorRef(ActorId("assessor-persist"), "Persistence assessor", ActorRole.ASSESSOR)
    private val operations = ActorRef(ActorId("ops-persist"), "Operations approver", ActorRole.OPERATIONAL_APPROVER)
    private val security = ActorRef(ActorId("security-persist"), "Security approver", ActorRole.SECURITY_APPROVER)
    private val reviewer = ActorRef(ActorId("reviewer-persist"), "Persistence reviewer", ActorRole.REVIEWER)

    @Test
    fun `codec is deterministic and restores finalized case with exact audit history`() {
        val case = finalizedCase()
        val first = CaseCodec.encode(case)
        val second = CaseCodec.encode(case)
        assertContentEquals(first, second)

        val restored = CaseCodec.decode(first)
        assertEquals(case.id, restored.id)
        assertEquals(case.caseNumber, restored.caseNumber)
        assertEquals(case.revision, restored.revision)
        assertEquals(case.state, restored.state)
        assertEquals(case.context, restored.context)
        assertEquals(case.objective, restored.objective)
        assertEquals(case.scope, restored.scope)
        assertEquals(case.dataPolicy, restored.dataPolicy)
        assertEquals(case.authorization, restored.authorization)
        assertEquals(case.reviewDecision, restored.reviewDecision)
        assertEquals(case.finalizedSnapshot, restored.finalizedSnapshot)
        assertEquals(case.auditTrail.events, restored.auditTrail.events)
        assertEquals(case.createdAt, restored.createdAt)
        assertEquals(case.version, restored.version)
        assertTrue(restored.auditTrail.verifies())
    }

    @Test
    fun `codec rejects modified envelope before restoring domain state`() {
        val encoded = CaseCodec.encode(finalizedCase())
        encoded[encoded.size / 2] = (encoded[encoded.size / 2].toInt() xor 0x01).toByte()
        assertFailsWith<CaseIntegrityException> { CaseCodec.decode(encoded) }
    }

    @Test
    fun `codec refuses inconsistent terminal state even when object was constructed inside module`() {
        val draft = draft()
        val inconsistent = AssessmentCase(
            id = draft.id,
            caseNumber = draft.caseNumber,
            revision = draft.revision,
            supersedesSnapshotId = draft.supersedesSnapshotId,
            state = CaseState.FINALIZED,
            context = draft.context,
            objective = draft.objective,
            scope = draft.scope,
            dataPolicy = draft.dataPolicy,
            stopConditions = draft.stopConditions,
            authorization = null,
            reviewDecision = null,
            finalizedSnapshot = null,
            auditTrail = draft.auditTrail,
            createdAt = draft.createdAt,
            version = draft.version,
        )
        assertFailsWith<CaseIntegrityException> { CaseCodec.encode(inconsistent) }
    }

    @Test
    fun `superseded case round trip preserves frozen snapshot and revision lineage`() {
        val final = finalizedCase()
        val superseded = final.supersedeWith(CaseId("CASE-PERSIST-002"), assessor, validFrom.plusSeconds(40)).superseded
        val restored = CaseCodec.decode(CaseCodec.encode(superseded))
        assertEquals(CaseState.SUPERSEDED, restored.state)
        assertEquals(final.finalizedSnapshot, restored.finalizedSnapshot)
        assertEquals(final.revision, restored.revision)
        assertNotNull(restored.finalizedSnapshot)
        assertTrue(restored.auditTrail.verifies())
    }

    private fun draft() = AssessmentCase.createDraft(
        id = CaseId("CASE-PERSIST-001"),
        caseNumber = "ATLAS-PERSIST-001",
        context = AssessmentContext("Water Operator", "South Pumping Station", "Transfer pumps", "P0-WATER"),
        objective = AssessmentObjective(
            question = "Does installed equipment match the approved baseline?",
            requestedDecision = "Accept the assessed baseline or retain explicit exceptions",
            stakeholderRole = "Commissioning engineer",
            successCriteria = setOf("Every critical expected record has an explicit state"),
            evidenceNeeded = setOf("Approved inventory", "Field evidence"),
        ),
        scope = CaseScope(
            cidrs = setOf(IPv4Cidr.parse("10.42.0.0/24")),
            excludedAddresses = setOf(IPv4Cidr.parseAddress("10.42.0.254")),
            allowedOperations = setOf(Operation.MODBUS_DEVICE_ID_BASIC),
            evidenceMethods = setOf(EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY, EvidenceMethod.H3_OFFLINE_IMPORT),
            interfaceIds = setOf("usb-ethernet-1"),
            physicalAreas = setOf("Pump control room"),
        ),
        dataPolicy = CaseDataPolicy(
            classification = "customer-confidential",
            retainPayloads = false,
            includeRawCapturesInExport = false,
            exportDestination = "customer-controlled encrypted export",
            deleteAfter = createdAt.plusSeconds(30 * 24 * 3600),
        ),
        stopConditions = setOf(StopCondition.PROCESS_ALARM, StopCondition.APPROVER_REQUEST, StopCondition.MANUAL_STOP),
        creator = assessor,
        createdAt = createdAt,
    )

    private fun authorized(): AssessmentCase {
        val waiting = draft().prepare(assessor, createdAt.plusSeconds(1))
            .requestAuthorization(assessor, createdAt.plusSeconds(2))
        return waiting.authorize(
            assessor,
            createdAt.plusSeconds(5),
            CaseAuthorization(
                AuthorizationId("AUTH-PERSIST-001"),
                Sha256.digest("authorization artifact"),
                validFrom,
                validUntil,
                waiting.scopeHash,
                waiting.dataPolicyHash,
                setOf(Approval(operations, createdAt.plusSeconds(3)), Approval(security, createdAt.plusSeconds(4))),
            ),
        )
    }

    private fun finalizedCase(): AssessmentCase = authorized()
        .startCollection(assessor, validFrom)
        .pauseCollection(operations, validFrom.plusSeconds(10), "operator hold")
        .beginEvidenceReview(assessor, validFrom.plusSeconds(11))
        .beginReconciliation(assessor, validFrom.plusSeconds(12))
        .beginAssessment(assessor, validFrom.plusSeconds(13))
        .requestReview(assessor, validFrom.plusSeconds(14))
        .recordReview(CaseReviewDecision(reviewer, CaseReviewOutcome.ACCEPTED, "evidence and exceptions reviewed", validFrom.plusSeconds(15)))
        .finalizeCase(
            reviewer,
            validFrom.plusSeconds(16),
            SnapshotId("SNAP-PERSIST-001"),
            SnapshotMaterial(
                objectHashes = mapOf(
                    "inventory" to Sha256.digest("inventory"),
                    "findings" to Sha256.digest("findings"),
                ),
                toolBuild = "atlas-persistence-test",
                packVersions = mapOf("water-pack" to Sha256.digest("water-pack")),
            ),
        )
}
