package com.atlasot.domain

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant

@JvmInline
value class CaseId(val value: String) {
    init { require(value.isNotBlank()) { "case id must not be blank" } }
    override fun toString(): String = value
}

@JvmInline
value class ActorId(val value: String) {
    init { require(value.isNotBlank()) { "actor id must not be blank" } }
    override fun toString(): String = value
}

@JvmInline
value class AuthorizationId(val value: String) {
    init { require(value.isNotBlank()) { "authorization id must not be blank" } }
    override fun toString(): String = value
}

@JvmInline
value class SnapshotId(val value: String) {
    init { require(value.isNotBlank()) { "snapshot id must not be blank" } }
    override fun toString(): String = value
}

@JvmInline
value class Sha256(val value: String) {
    init { require(value.matches(Regex("[0-9a-f]{64}"))) { "canonical lowercase SHA-256 required" } }
    override fun toString(): String = value

    companion object {
        fun digest(text: String): Sha256 {
            val bytes = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(StandardCharsets.UTF_8))
            return Sha256(bytes.joinToString("") { "%02x".format(it.toInt() and 0xff) })
        }
    }
}

internal object CanonicalText {
    fun encode(vararg values: String): String = encode(values.asIterable())
    fun encode(values: Iterable<String>): String = values.joinToString(separator = "") { value ->
        val size = value.toByteArray(StandardCharsets.UTF_8).size
        "$size:$value"
    }
}

enum class CaseState {
    DRAFT,
    PREPARED,
    AWAITING_AUTHORIZATION,
    AUTHORIZED,
    COLLECTING,
    PAUSED,
    EVIDENCE_REVIEW,
    RECONCILING,
    ASSESSING,
    REVIEW_PENDING,
    READY_TO_FINALIZE,
    FINALIZED,
    SUPERSEDED,
    EXPIRED,
    CANCELLED,
}

enum class ActorRole {
    ASSESSOR,
    OPERATIONAL_APPROVER,
    SECURITY_APPROVER,
    REVIEWER,
    PACK_ADMINISTRATOR,
}

data class ActorRef(
    val id: ActorId,
    val displayName: String,
    val role: ActorRole,
) {
    init { require(displayName.isNotBlank()) { "actor display name must not be blank" } }
}

data class Approval(
    val actor: ActorRef,
    val at: Instant,
) {
    val role: ActorRole get() = actor.role
}

enum class EvidenceMethod {
    H1_EXACT_ACTIVE_IDENTITY,
    H2_LIVE_PASSIVE,
    H3_OFFLINE_IMPORT,
    H4_APPROVED_RADIO_OBSERVATION,
}

enum class StopCondition {
    PROCESS_ALARM,
    NETWORK_INSTABILITY,
    ROUTE_CHANGE,
    APPROVER_REQUEST,
    DEVICE_DETACH,
    BUDGET_OR_EXPIRY,
    MANUAL_STOP,
}

data class AssessmentContext(
    val legalEntity: String,
    val site: String,
    val processArea: String,
    val assessmentPack: String,
) {
    init {
        require(legalEntity.isNotBlank()) { "legal entity is required" }
        require(site.isNotBlank()) { "site is required" }
        require(processArea.isNotBlank()) { "process area is required" }
        require(assessmentPack.isNotBlank()) { "assessment pack is required" }
    }
}

data class AssessmentObjective(
    val question: String,
    val requestedDecision: String,
    val stakeholderRole: String,
    val successCriteria: Set<String>,
    val evidenceNeeded: Set<String>,
) {
    init {
        require(question.isNotBlank()) { "assessment question is required" }
        require(requestedDecision.isNotBlank()) { "requested decision is required" }
        require(stakeholderRole.isNotBlank()) { "stakeholder role is required" }
        require(successCriteria.isNotEmpty() && successCriteria.none { it.isBlank() }) { "success criteria are required" }
        require(evidenceNeeded.isNotEmpty() && evidenceNeeded.none { it.isBlank() }) { "evidence needs are required" }
    }
}

data class CaseScope(
    val cidrs: Set<IPv4Cidr> = emptySet(),
    val excludedAddresses: Set<Int> = emptySet(),
    val allowedOperations: Set<Operation> = emptySet(),
    val evidenceMethods: Set<EvidenceMethod>,
    val interfaceIds: Set<String> = emptySet(),
    val capturePoints: Set<String> = emptySet(),
    val physicalAreas: Set<String> = emptySet(),
) {
    init {
        require(evidenceMethods.isNotEmpty()) { "at least one evidence method is required" }
        require(interfaceIds.none { it.isBlank() }) { "interface ids must not be blank" }
        require(capturePoints.none { it.isBlank() }) { "capture points must not be blank" }
        require(physicalAreas.none { it.isBlank() }) { "physical areas must not be blank" }
        require(allowedOperations.isEmpty() || EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY in evidenceMethods) {
            "active operations require the H1 evidence method"
        }
        require(EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY !in evidenceMethods || allowedOperations.isNotEmpty()) {
            "H1 evidence method requires at least one allowed operation"
        }
        require(EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY !in evidenceMethods || cidrs.isNotEmpty()) {
            "H1 evidence method requires an explicit CIDR scope"
        }
        require(EvidenceMethod.H2_LIVE_PASSIVE !in evidenceMethods || capturePoints.isNotEmpty()) {
            "H2 live passive method requires an approved capture point"
        }
    }

    fun hasCollectionBoundary(): Boolean = cidrs.isNotEmpty() || capturePoints.isNotEmpty() || physicalAreas.isNotEmpty()

    fun fingerprint(): Sha256 = Sha256.digest(CanonicalText.encode(
        cidrs.sortedWith(compareBy<IPv4Cidr> { it.network }.thenBy { it.prefix }).joinToString(",") { "${it.network}/${it.prefix}" },
        excludedAddresses.sorted().joinToString(","),
        allowedOperations.sortedBy { it.name }.joinToString(",") { it.name },
        evidenceMethods.sortedBy { it.name }.joinToString(",") { it.name },
        interfaceIds.sorted().joinToString(","),
        capturePoints.sorted().joinToString(","),
        physicalAreas.sorted().joinToString(","),
    ))
}

data class CaseDataPolicy(
    val classification: String,
    val retainPayloads: Boolean,
    val includeRawCapturesInExport: Boolean,
    val exportDestination: String?,
    val deleteAfter: Instant?,
) {
    init {
        require(classification.isNotBlank()) { "data classification is required" }
        require(exportDestination == null || exportDestination.isNotBlank()) { "export destination must not be blank" }
    }

    fun fingerprint(): Sha256 = Sha256.digest(CanonicalText.encode(
        classification,
        retainPayloads.toString(),
        includeRawCapturesInExport.toString(),
        exportDestination.orEmpty(),
        deleteAfter?.toString().orEmpty(),
    ))
}

data class CaseAuthorization(
    val id: AuthorizationId,
    val artifactHash: Sha256,
    val validFrom: Instant,
    val validUntil: Instant,
    val scopeHash: Sha256,
    val dataPolicyHash: Sha256,
    val approvals: Set<Approval>,
) {
    init {
        require(validFrom.isBefore(validUntil)) { "authorization window must be positive" }
        val roles = approvals.map { it.role }.toSet()
        require(ActorRole.OPERATIONAL_APPROVER in roles && ActorRole.SECURITY_APPROVER in roles) {
            "operational and security approvals are required"
        }
        require(approvals.all { !it.at.isAfter(validUntil) }) { "approval cannot occur after authorization expiry" }
    }

    fun isValidAt(now: Instant): Boolean = !now.isBefore(validFrom) && now.isBefore(validUntil)
}

enum class AuditEventType {
    CASE_CREATED,
    CASE_PREPARED,
    AUTHORIZATION_REQUESTED,
    AUTHORIZATION_APPROVED,
    COLLECTION_STARTED,
    COLLECTION_PAUSED,
    EVIDENCE_REVIEW_STARTED,
    RECONCILIATION_STARTED,
    ASSESSMENT_STARTED,
    REVIEW_REQUESTED,
    REVIEW_ACCEPTED,
    REVIEW_RETURNED,
    CASE_FINALIZED,
    CASE_SUPERSEDED,
    CASE_CANCELLED,
    CASE_EXPIRED,
}

data class AuditEvent(
    val sequence: Long,
    val caseId: CaseId,
    val at: Instant,
    val actor: ActorRef,
    val type: AuditEventType,
    val objectType: String,
    val objectId: String,
    val detailsHash: Sha256,
    val previousHash: Sha256?,
    val eventHash: Sha256,
) {
    companion object {
        internal fun create(
            sequence: Long,
            caseId: CaseId,
            at: Instant,
            actor: ActorRef,
            type: AuditEventType,
            objectType: String,
            objectId: String,
            details: String,
            previousHash: Sha256?,
        ): AuditEvent {
            val detailsHash = Sha256.digest(details)
            val eventHash = calculateHash(sequence, caseId, at, actor, type, objectType, objectId, detailsHash, previousHash)
            return AuditEvent(sequence, caseId, at, actor, type, objectType, objectId, detailsHash, previousHash, eventHash)
        }

        internal fun calculateHash(
            sequence: Long,
            caseId: CaseId,
            at: Instant,
            actor: ActorRef,
            type: AuditEventType,
            objectType: String,
            objectId: String,
            detailsHash: Sha256,
            previousHash: Sha256?,
        ): Sha256 = Sha256.digest(CanonicalText.encode(
            "ATLAS-AUDIT-V1", sequence.toString(), caseId.value, at.toString(), actor.id.value, actor.role.name,
            type.name, objectType, objectId, detailsHash.value, previousHash?.value.orEmpty(),
        ))
    }
}

class AuditTrail private constructor(private val storedEvents: List<AuditEvent>) {
    val events: List<AuditEvent> get() = storedEvents.toList()
    val head: Sha256? get() = storedEvents.lastOrNull()?.eventHash

    fun append(
        caseId: CaseId,
        at: Instant,
        actor: ActorRef,
        type: AuditEventType,
        objectType: String = "case",
        objectId: String = caseId.value,
        details: String = "",
    ): AuditTrail {
        storedEvents.lastOrNull()?.let { previous ->
            require(!at.isBefore(previous.at)) { "audit event time cannot move backwards" }
        }
        val event = AuditEvent.create(
            sequence = storedEvents.size.toLong() + 1,
            caseId = caseId,
            at = at,
            actor = actor,
            type = type,
            objectType = objectType,
            objectId = objectId,
            details = details,
            previousHash = head,
        )
        return AuditTrail(storedEvents + event)
    }

    fun verifies(): Boolean = verify(storedEvents)

    companion object {
        fun empty(): AuditTrail = AuditTrail(emptyList())

        fun restore(events: List<AuditEvent>): AuditTrail {
            require(verify(events)) { "audit chain verification failed" }
            return AuditTrail(events.toList())
        }

        fun verify(events: List<AuditEvent>): Boolean {
            var previous: Sha256? = null
            var previousAt: Instant? = null
            val expectedCaseId = events.firstOrNull()?.caseId
            events.forEachIndexed { index, event ->
                if (event.caseId != expectedCaseId) return false
                if (previousAt != null && event.at.isBefore(previousAt)) return false
                if (event.sequence != index.toLong() + 1) return false
                if (event.previousHash != previous) return false
                val expected = AuditEvent.calculateHash(
                    event.sequence, event.caseId, event.at, event.actor, event.type,
                    event.objectType, event.objectId, event.detailsHash, event.previousHash
                )
                if (event.eventHash != expected) return false
                previous = event.eventHash
                previousAt = event.at
            }
            return true
        }
    }
}

enum class CaseReviewOutcome { ACCEPTED, CHANGES_REQUIRED }

data class CaseReviewDecision(
    val reviewer: ActorRef,
    val outcome: CaseReviewOutcome,
    val reason: String,
    val at: Instant,
) {
    init {
        require(reviewer.role == ActorRole.REVIEWER) { "review decision requires reviewer role" }
        require(reason.isNotBlank()) { "review reason is required" }
    }
}

data class SnapshotMaterial(
    val objectHashes: Map<String, Sha256>,
    val toolBuild: String,
    val packVersions: Map<String, Sha256>,
) {
    init {
        require(objectHashes.isNotEmpty()) { "snapshot must reference professional case material" }
        require(objectHashes.keys.none { it.isBlank() }) { "snapshot object keys must not be blank" }
        require(toolBuild.isNotBlank()) { "tool build identity is required" }
        require(packVersions.keys.none { it.isBlank() }) { "pack ids must not be blank" }
    }

    fun fingerprint(): Sha256 = Sha256.digest(CanonicalText.encode(
        objectHashes.toSortedMap().entries.joinToString(",") { "${it.key}=${it.value.value}" },
        toolBuild,
        packVersions.toSortedMap().entries.joinToString(",") { "${it.key}=${it.value.value}" },
    ))
}

data class FinalizedSnapshot(
    val id: SnapshotId,
    val caseId: CaseId,
    val caseNumber: String,
    val revision: Int,
    val finalizedAt: Instant,
    val authorizationArtifactHash: Sha256,
    val scopeHash: Sha256,
    val dataPolicyHash: Sha256,
    val auditHead: Sha256,
    val material: SnapshotMaterial,
    val contentHash: Sha256,
) {
    companion object {
        fun create(
            id: SnapshotId,
            caseId: CaseId,
            caseNumber: String,
            revision: Int,
            finalizedAt: Instant,
            authorizationArtifactHash: Sha256,
            scopeHash: Sha256,
            dataPolicyHash: Sha256,
            auditHead: Sha256,
            material: SnapshotMaterial,
        ): FinalizedSnapshot {
            val contentHash = Sha256.digest(CanonicalText.encode(
                "ATLAS-SNAPSHOT-V1", id.value, caseId.value, caseNumber, revision.toString(), finalizedAt.toString(),
                authorizationArtifactHash.value, scopeHash.value, dataPolicyHash.value, auditHead.value, material.fingerprint().value,
            ))
            return FinalizedSnapshot(
                id, caseId, caseNumber, revision, finalizedAt, authorizationArtifactHash,
                scopeHash, dataPolicyHash, auditHead, material, contentHash
            )
        }
    }
}

data class SupersessionResult(val superseded: AssessmentCase, val successor: AssessmentCase)

class AssessmentCase internal constructor(
    val id: CaseId,
    val caseNumber: String,
    val revision: Int,
    val supersedesSnapshotId: SnapshotId?,
    val state: CaseState,
    val context: AssessmentContext,
    val objective: AssessmentObjective,
    val scope: CaseScope,
    val dataPolicy: CaseDataPolicy,
    val stopConditions: Set<StopCondition>,
    val authorization: CaseAuthorization?,
    val reviewDecision: CaseReviewDecision?,
    val finalizedSnapshot: FinalizedSnapshot?,
    val auditTrail: AuditTrail,
    val createdAt: Instant,
    val version: Long,
) {
    val scopeHash: Sha256 get() = scope.fingerprint()
    val dataPolicyHash: Sha256 get() = dataPolicy.fingerprint()
    val authorizationHash: String? get() = authorization?.artifactHash?.value

    fun prepare(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        requireState(CaseState.DRAFT)
        require(scope.hasCollectionBoundary()) { "case requires an explicit collection boundary" }
        require(stopConditions.isNotEmpty()) { "at least one stop condition is required" }
        require(dataPolicy.deleteAfter == null || dataPolicy.deleteAfter.isAfter(createdAt)) { "case deletion date must be after case creation" }
        if (context.assessmentPack == "P0-WATER") {
            require(scope.allowedOperations.all { it == Operation.MODBUS_DEVICE_ID_BASIC }) {
                "P0-WATER currently permits only Modbus basic device identification as an active operation"
            }
        }
        return withTransition(CaseState.PREPARED, actor, at, AuditEventType.CASE_PREPARED)
    }

    fun requestAuthorization(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        requireState(CaseState.PREPARED)
        return withTransition(
            CaseState.AWAITING_AUTHORIZATION, actor, at, AuditEventType.AUTHORIZATION_REQUESTED,
            details = "scope=${scopeHash.value};dataPolicy=${dataPolicyHash.value}"
        )
    }

    fun authorize(recordedBy: ActorRef, at: Instant, newAuthorization: CaseAuthorization): AssessmentCase {
        requireRole(recordedBy, ActorRole.ASSESSOR)
        requireState(CaseState.AWAITING_AUTHORIZATION)
        require(at.isBefore(newAuthorization.validUntil)) { "authorization has expired" }
        require(newAuthorization.approvals.all { !it.at.isAfter(at) }) { "authorization contains a future approval" }
        require(newAuthorization.scopeHash == scopeHash) { "authorization does not match current scope" }
        require(newAuthorization.dataPolicyHash == dataPolicyHash) { "authorization does not match current data policy" }
        val trail = auditTrail.append(
            id, at, recordedBy, AuditEventType.AUTHORIZATION_APPROVED,
            objectType = "authorization", objectId = newAuthorization.id.value,
            details = "artifact=${newAuthorization.artifactHash.value};from=${newAuthorization.validFrom};until=${newAuthorization.validUntil}"
        )
        return recreate(state = CaseState.AUTHORIZED, authorization = newAuthorization, auditTrail = trail, version = version + 1)
    }

    fun startCollection(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        require(state == CaseState.AUTHORIZED || state == CaseState.PAUSED) { "case is not authorized for collection" }
        requireValidAuthorization(at)
        return withTransition(CaseState.COLLECTING, actor, at, AuditEventType.COLLECTION_STARTED)
    }

    fun pauseCollection(actor: ActorRef, at: Instant, reason: String): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR, ActorRole.OPERATIONAL_APPROVER, ActorRole.SECURITY_APPROVER)
        requireState(CaseState.COLLECTING)
        require(reason.isNotBlank()) { "pause reason is required" }
        return withTransition(CaseState.PAUSED, actor, at, AuditEventType.COLLECTION_PAUSED, details = reason)
    }

    fun beginEvidenceReview(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        require(state == CaseState.COLLECTING || state == CaseState.PAUSED) { "collection has not started" }
        return withTransition(CaseState.EVIDENCE_REVIEW, actor, at, AuditEventType.EVIDENCE_REVIEW_STARTED)
    }

    fun beginReconciliation(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        requireState(CaseState.EVIDENCE_REVIEW)
        return withTransition(CaseState.RECONCILING, actor, at, AuditEventType.RECONCILIATION_STARTED)
    }

    fun beginAssessment(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        requireState(CaseState.RECONCILING)
        return withTransition(CaseState.ASSESSING, actor, at, AuditEventType.ASSESSMENT_STARTED)
    }

    fun requestReview(actor: ActorRef, at: Instant): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR)
        requireState(CaseState.ASSESSING)
        return withTransition(CaseState.REVIEW_PENDING, actor, at, AuditEventType.REVIEW_REQUESTED)
    }

    fun recordReview(decision: CaseReviewDecision): AssessmentCase {
        requireState(CaseState.REVIEW_PENDING)
        val nextState = if (decision.outcome == CaseReviewOutcome.ACCEPTED) CaseState.READY_TO_FINALIZE else CaseState.ASSESSING
        val eventType = if (decision.outcome == CaseReviewOutcome.ACCEPTED) AuditEventType.REVIEW_ACCEPTED else AuditEventType.REVIEW_RETURNED
        val trail = auditTrail.append(
            id, decision.at, decision.reviewer, eventType,
            objectType = "case-review", objectId = "$caseNumber-r$revision", details = decision.reason
        )
        return recreate(state = nextState, reviewDecision = decision, auditTrail = trail, version = version + 1)
    }

    fun finalizeCase(reviewer: ActorRef, at: Instant, snapshotId: SnapshotId, material: SnapshotMaterial): AssessmentCase {
        requireRole(reviewer, ActorRole.REVIEWER)
        requireState(CaseState.READY_TO_FINALIZE)
        val accepted = reviewDecision
        require(accepted != null && accepted.outcome == CaseReviewOutcome.ACCEPTED) { "accepted case review is required" }
        require(accepted.reviewer.id == reviewer.id) { "finalizer must be the accepting reviewer" }
        require(!at.isBefore(accepted.at)) { "finalization cannot precede reviewer acceptance" }
        val authorization = requireNotNull(authorization) { "authorization is required before finalization" }
        val trail = auditTrail.append(
            id, at, reviewer, AuditEventType.CASE_FINALIZED,
            objectType = "snapshot", objectId = snapshotId.value, details = "material=${material.fingerprint().value}"
        )
        val auditHead = requireNotNull(trail.head) { "finalization requires an audit head" }
        val snapshot = FinalizedSnapshot.create(
            snapshotId, id, caseNumber, revision, at, authorization.artifactHash,
            scopeHash, dataPolicyHash, auditHead, material
        )
        return recreate(state = CaseState.FINALIZED, finalizedSnapshot = snapshot, auditTrail = trail, version = version + 1)
    }

    fun supersedeWith(newCaseId: CaseId, actor: ActorRef, at: Instant): SupersessionResult {
        requireRole(actor, ActorRole.ASSESSOR)
        requireState(CaseState.FINALIZED)
        val snapshot = requireNotNull(finalizedSnapshot)
        val supersededTrail = auditTrail.append(
            id, at, actor, AuditEventType.CASE_SUPERSEDED,
            objectType = "snapshot", objectId = snapshot.id.value,
            details = "successor=${newCaseId.value};revision=${revision + 1}"
        )
        val superseded = recreate(state = CaseState.SUPERSEDED, auditTrail = supersededTrail, version = version + 1)
        val successor = createDraft(
            id = newCaseId, caseNumber = caseNumber, revision = revision + 1, supersedesSnapshotId = snapshot.id,
            context = context, objective = objective, scope = scope, dataPolicy = dataPolicy, stopConditions = stopConditions,
            creator = actor, createdAt = at,
        )
        return SupersessionResult(superseded, successor)
    }

    fun cancel(actor: ActorRef, at: Instant, reason: String): AssessmentCase {
        requireRole(actor, ActorRole.ASSESSOR, ActorRole.OPERATIONAL_APPROVER, ActorRole.SECURITY_APPROVER)
        require(state !in setOf(CaseState.FINALIZED, CaseState.SUPERSEDED, CaseState.CANCELLED)) { "case cannot be cancelled from $state" }
        require(reason.isNotBlank()) { "cancellation reason is required" }
        return withTransition(CaseState.CANCELLED, actor, at, AuditEventType.CASE_CANCELLED, details = reason)
    }

    fun expire(at: Instant): AssessmentCase {
        require(state !in setOf(CaseState.FINALIZED, CaseState.SUPERSEDED, CaseState.CANCELLED, CaseState.EXPIRED)) { "case cannot expire from $state" }
        val auth = requireNotNull(authorization) { "only an authorized case can expire" }
        require(!at.isBefore(auth.validUntil)) { "authorization has not expired" }
        val system = ActorRef(ActorId("atlas-system"), "Atlas system", ActorRole.PACK_ADMINISTRATOR)
        return withTransition(CaseState.EXPIRED, system, at, AuditEventType.CASE_EXPIRED)
    }

    fun assertOperationAllowed(operation: Operation, targetAddress: Int, at: Instant) {
        require(state == CaseState.COLLECTING) { "active operation requires collecting state" }
        requireValidAuthorization(at)
        require(operation in scope.allowedOperations) { "operation is not authorized by case scope" }
        require(scope.cidrs.any { it.contains(targetAddress) }) { "target is outside case scope" }
        require(targetAddress !in scope.excludedAddresses) { "target is explicitly excluded" }
    }

    private fun requireValidAuthorization(at: Instant) {
        val auth = requireNotNull(authorization) { "case has no authorization" }
        require(auth.isValidAt(at)) { "outside authorization window" }
        require(auth.scopeHash == scopeHash) { "case scope changed after authorization" }
        require(auth.dataPolicyHash == dataPolicyHash) { "case data policy changed after authorization" }
    }

    private fun requireState(expected: CaseState) { require(state == expected) { "expected state $expected but was $state" } }

    private fun requireRole(actor: ActorRef, vararg allowed: ActorRole) {
        require(actor.role in allowed.toSet()) { "role ${actor.role} is not allowed for this action" }
    }

    private fun withTransition(nextState: CaseState, actor: ActorRef, at: Instant, eventType: AuditEventType, details: String = ""): AssessmentCase {
        require(state !in setOf(CaseState.FINALIZED, CaseState.SUPERSEDED, CaseState.CANCELLED, CaseState.EXPIRED)) { "terminal case state $state cannot transition" }
        val trail = auditTrail.append(id, at, actor, eventType, details = details)
        return recreate(state = nextState, auditTrail = trail, version = version + 1)
    }

    private fun recreate(
        state: CaseState = this.state,
        authorization: CaseAuthorization? = this.authorization,
        reviewDecision: CaseReviewDecision? = this.reviewDecision,
        finalizedSnapshot: FinalizedSnapshot? = this.finalizedSnapshot,
        auditTrail: AuditTrail = this.auditTrail,
        version: Long = this.version,
    ): AssessmentCase = AssessmentCase(
        id, caseNumber, revision, supersedesSnapshotId, state, context, objective, scope, dataPolicy,
        stopConditions, authorization, reviewDecision, finalizedSnapshot, auditTrail, createdAt, version
    )

    companion object {
        fun createDraft(
            id: CaseId,
            caseNumber: String,
            revision: Int = 1,
            supersedesSnapshotId: SnapshotId? = null,
            context: AssessmentContext,
            objective: AssessmentObjective,
            scope: CaseScope,
            dataPolicy: CaseDataPolicy,
            stopConditions: Set<StopCondition>,
            creator: ActorRef,
            createdAt: Instant,
        ): AssessmentCase {
            require(creator.role == ActorRole.ASSESSOR) { "case creator must act as assessor" }
            require(caseNumber.isNotBlank()) { "case number is required" }
            require(revision >= 1) { "case revision must be positive" }
            require((revision == 1) == (supersedesSnapshotId == null)) { "only revisions after the first may supersede a snapshot" }
            val trail = AuditTrail.empty().append(
                id, createdAt, creator, AuditEventType.CASE_CREATED,
                details = "caseNumber=$caseNumber;revision=$revision;supersedes=${supersedesSnapshotId?.value.orEmpty()}"
            )
            return AssessmentCase(
                id, caseNumber, revision, supersedesSnapshotId, CaseState.DRAFT, context, objective, scope, dataPolicy,
                stopConditions.toSet(), null, null, null, trail, createdAt, 1
            )
        }
    }
}

enum class Operation { MODBUS_DEVICE_ID_BASIC, OPCUA_DISCOVERY, ICMP_REACHABILITY, TCP_SERVICE_CONFIRM }
