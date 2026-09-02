package com.atlasot.domain

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CaseRecordsTest {
    private val now = Instant.parse("2026-09-02T08:00:00Z")
    private val caseId = CaseId("CASE-001")
    private val artifactId = ArtifactId("ART-001")
    private val observationId = ObservationId("OBS-001")

    @Test
    fun `provenance byte ranges require an immutable artifact reference`() {
        assertFailsWith<IllegalArgumentException> {
            EvidenceProvenance(
                sourceType = EvidenceSourceType.PASSIVE_CAPTURE,
                artifactId = null,
                byteRanges = listOf(ByteRange(10, 20)),
                observedAt = now,
                actorId = null,
                toolBuild = "build",
                parserVersion = "parser-1",
                packVersion = null,
                method = "PCAP import",
            )
        }
    }

    @Test
    fun `expected records preserve original declaration independently from normalized values`() {
        val record = ExpectedRecord(
            id = ExpectedRecordId("EXP-001"),
            caseId = caseId,
            sourceArtifactId = artifactId,
            sourceRow = 12,
            sourceRowHash = Sha256.digest("raw row"),
            declaredFields = mapOf("vendor" to "Schneider Electric ", "ip" to "10.0.0.10"),
            normalizedFields = mapOf("vendor" to "Schneider Electric", "ip" to "10.0.0.10"),
        )
        assertEquals("Schneider Electric ", record.declaredFields["vendor"])
        assertEquals("Schneider Electric", record.normalizedFields["vendor"])
    }

    @Test
    fun `identity claims cannot exist without evidence`() {
        assertFailsWith<IllegalArgumentException> {
            IdentityClaim(
                id = ClaimId("CLM-001"),
                caseId = caseId,
                subjectId = "endpoint-1",
                attribute = "model",
                value = "S7-1500",
                sourceLevel = EvidenceLevel.E2,
                confidence = 0.95,
                ruleId = "device-id",
                ruleVersion = "1",
                createdAt = now,
                reviewState = ClaimReviewState.PROPOSED,
                contradictionGroup = null,
                evidenceIds = emptySet(),
            )
        }
    }

    @Test
    fun `finding confidence remains separate from consequence and exposure`() {
        val finding = FindingRecord(
            id = FindingId("FND-001"),
            caseId = caseId,
            ruleId = "WAT-NET-001",
            condition = "Cleartext Modbus observed in scope",
            affectedObjectIds = setOf("AST-001"),
            evidenceIds = setOf(observationId),
            confidence = 0.98,
            consequence = 4,
            exposure = 2,
            limitations = setOf("Capture covers one approved observation window"),
            recommendation = "Review conduit controls without disrupting process availability",
            actionOwner = "Operations engineering",
            targetDate = null,
            reviewState = FindingReviewState.UNDER_REVIEW,
        )
        assertEquals(8, finding.riskScore)
        assertEquals(0.98, finding.confidence)
    }
}
