package com.atlasot.domain

import java.time.Instant

@JvmInline value class ArtifactId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class ExpectedRecordId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class ObservationId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class ClaimId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class AssetId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class ReconciliationId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class FindingId(val value: String) { init { require(value.isNotBlank()) } }
@JvmInline value class ObjectReviewId(val value: String) { init { require(value.isNotBlank()) } }

enum class EvidenceSourceType { PHYSICAL, PROTOCOL_IDENTITY, CUSTOMER_RECORD, PASSIVE_CAPTURE, WEAK_ATTRIBUTION, ASSESSOR_CONTEXT }
enum class EvidenceLevel { E1, E2, E3, E4, E5, E6 }

data class ByteRange(val startInclusive: Long, val endExclusive: Long) {
    init {
        require(startInclusive >= 0) { "byte range must start at or after zero" }
        require(endExclusive > startInclusive) { "byte range must be non-empty" }
    }
}

data class EvidenceProvenance(
    val sourceType: EvidenceSourceType,
    val artifactId: ArtifactId?,
    val byteRanges: List<ByteRange> = emptyList(),
    val observedAt: Instant,
    val actorId: ActorId?,
    val toolBuild: String?,
    val parserVersion: String?,
    val packVersion: String?,
    val method: String,
) {
    init {
        require(method.isNotBlank()) { "provenance method is required" }
        require(toolBuild == null || toolBuild.isNotBlank())
        require(parserVersion == null || parserVersion.isNotBlank())
        require(packVersion == null || packVersion.isNotBlank())
        require(byteRanges.isEmpty() || artifactId != null) { "byte ranges require an artifact" }
    }
}

data class SealedArtifact(
    val id: ArtifactId,
    val caseId: CaseId,
    val sha256: Sha256,
    val mediaType: String,
    val sizeBytes: Long,
    val sealedAt: Instant,
    val sourceDescription: String,
) {
    init {
        require(mediaType.isNotBlank())
        require(sizeBytes >= 0)
        require(sourceDescription.isNotBlank())
    }
}

data class ExpectedRecord(
    val id: ExpectedRecordId,
    val caseId: CaseId,
    val sourceArtifactId: ArtifactId,
    val sourceRow: Long,
    val sourceRowHash: Sha256,
    val declaredFields: Map<String, String>,
    val normalizedFields: Map<String, String>,
) {
    init {
        require(sourceRow >= 1)
        require(declaredFields.isNotEmpty()) { "expected record must preserve declared fields" }
        require(declaredFields.keys.none { it.isBlank() })
        require(normalizedFields.keys.none { it.isBlank() })
    }
}

data class ObservationRecord(
    val id: ObservationId,
    val caseId: CaseId,
    val subjectKey: String,
    val attributes: Map<String, String>,
    val provenance: EvidenceProvenance,
) {
    init {
        require(subjectKey.isNotBlank())
        require(attributes.isNotEmpty())
        require(attributes.keys.none { it.isBlank() })
    }
}

enum class ClaimReviewState { PROPOSED, ACCEPTED, REJECTED, UNRESOLVED }

data class IdentityClaim(
    val id: ClaimId,
    val caseId: CaseId,
    val subjectId: String,
    val attribute: String,
    val value: String,
    val sourceLevel: EvidenceLevel,
    val confidence: Double,
    val ruleId: String,
    val ruleVersion: String,
    val createdAt: Instant,
    val reviewState: ClaimReviewState,
    val contradictionGroup: String?,
    val evidenceIds: Set<ObservationId>,
) {
    init {
        require(subjectId.isNotBlank())
        require(attribute.isNotBlank())
        require(value.isNotBlank())
        require(confidence in 0.0..1.0)
        require(ruleId.isNotBlank() && ruleVersion.isNotBlank())
        require(contradictionGroup == null || contradictionGroup.isNotBlank())
        require(evidenceIds.isNotEmpty()) { "identity claim requires evidence" }
    }
}

enum class AssetStatus { CONFIRMED, PROBABLE, TENTATIVE, INSUFFICIENT, NOT_OBSERVED, EXCLUDED, UNEXPECTED, CONFLICT, UNRESOLVED }
enum class ReconciliationOutcome { MATCH_CONFIRMED, MATCH_PROBABLE, NOT_SAME_ASSET, NEEDS_MORE_EVIDENCE, SPLIT, MERGE }

data class ReconciliationDecision(
    val id: ReconciliationId,
    val caseId: CaseId,
    val expectedRecordId: ExpectedRecordId?,
    val observedSubjectId: String?,
    val outcome: ReconciliationOutcome,
    val resultingStatus: AssetStatus,
    val actor: ActorRef,
    val at: Instant,
    val rationale: String,
    val evidenceIds: Set<ObservationId>,
) {
    init {
        require(expectedRecordId != null || !observedSubjectId.isNullOrBlank()) {
            "reconciliation requires an expected record or observed subject"
        }
        require(rationale.isNotBlank())
        require(actor.role == ActorRole.ASSESSOR || actor.role == ActorRole.REVIEWER) {
            "reconciliation decision requires assessor or reviewer role"
        }
    }
}

enum class FindingReviewState { PROPOSED, UNDER_REVIEW, ACCEPTED, REJECTED, NEEDS_MORE_EVIDENCE }

data class FindingRecord(
    val id: FindingId,
    val caseId: CaseId,
    val ruleId: String,
    val condition: String,
    val affectedObjectIds: Set<String>,
    val evidenceIds: Set<ObservationId>,
    val confidence: Double,
    val consequence: Int?,
    val exposure: Int?,
    val limitations: Set<String>,
    val recommendation: String,
    val actionOwner: String?,
    val targetDate: Instant?,
    val reviewState: FindingReviewState,
) {
    init {
        require(ruleId.isNotBlank())
        require(condition.isNotBlank())
        require(affectedObjectIds.isNotEmpty() && affectedObjectIds.none { it.isBlank() })
        require(evidenceIds.isNotEmpty()) { "finding requires evidence" }
        require(confidence in 0.0..1.0)
        require(consequence == null || consequence in 1..5)
        require(exposure == null || exposure in 1..5)
        require(limitations.none { it.isBlank() })
        require(recommendation.isNotBlank())
        require(actionOwner == null || actionOwner.isNotBlank())
    }

    val riskScore: Int? get() = if (consequence != null && exposure != null) consequence * exposure else null
}

enum class ObjectReviewOutcome { ACCEPTED, REJECTED, RETURNED }

data class ObjectReviewDecision(
    val id: ObjectReviewId,
    val caseId: CaseId,
    val objectType: String,
    val objectId: String,
    val reviewer: ActorRef,
    val outcome: ObjectReviewOutcome,
    val reason: String,
    val at: Instant,
) {
    init {
        require(objectType.isNotBlank() && objectId.isNotBlank())
        require(reviewer.role == ActorRole.REVIEWER)
        require(reason.isNotBlank())
    }
}
