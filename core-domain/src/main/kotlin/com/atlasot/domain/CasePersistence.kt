package com.atlasot.domain

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant

class CaseIntegrityException(message: String, cause: Throwable? = null) : IllegalStateException(message, cause)

class CaseVersionConflictException(
    val caseId: CaseId,
    val expectedVersion: Long?,
    val actualVersion: Long?,
) : IllegalStateException(
    "case ${caseId.value} version conflict: expected ${expectedVersion ?: "new"}, actual ${actualVersion ?: "missing"}"
)

data class CaseSummary(
    val id: CaseId,
    val caseNumber: String,
    val revision: Int,
    val state: CaseState,
    val version: Long,
)

interface CaseRepository {
    fun load(id: CaseId): AssessmentCase?
    fun save(case: AssessmentCase, expectedVersion: Long?)
    fun list(): List<CaseSummary>
}

/**
 * Deterministic, bounded representation of the professional case aggregate.
 * Raw evidence artifacts are not stored here; they remain in the artifact vault.
 */
object CaseCodec {
    private val MAGIC = "ATLASCASE".toByteArray(StandardCharsets.US_ASCII)
    private const val SCHEMA_VERSION = 1
    private const val DIGEST_BYTES = 32
    private const val MAX_PAYLOAD_BYTES = 4 * 1024 * 1024
    private const val MAX_STRING_BYTES = 256 * 1024
    private const val MAX_COLLECTION_ITEMS = 10_000

    fun encode(case: AssessmentCase): ByteArray {
        require(case.auditTrail.verifies()) { "cannot persist a case with an invalid audit trail" }
        validateCaseForStorage(case)
        val payload = ByteArrayOutputStream().use { bytes ->
            DataOutputStream(bytes).use { out -> writeCase(out, case) }
            bytes.toByteArray()
        }
        require(payload.size <= MAX_PAYLOAD_BYTES) { "professional case payload exceeds storage bound" }
        val digest = digest(payload)
        return ByteArrayOutputStream(MAGIC.size + 8 + payload.size + digest.size).use { bytes ->
            DataOutputStream(bytes).use { out ->
                out.write(MAGIC)
                out.writeInt(SCHEMA_VERSION)
                out.writeInt(payload.size)
                out.write(payload)
                out.write(digest)
            }
            bytes.toByteArray()
        }
    }

    fun decode(encoded: ByteArray): AssessmentCase {
        if (encoded.size > MAX_PAYLOAD_BYTES + MAGIC.size + 8 + DIGEST_BYTES) {
            throw CaseIntegrityException("professional case payload exceeds storage bound")
        }
        return try {
            val input = DataInputStream(ByteArrayInputStream(encoded))
            val magic = ByteArray(MAGIC.size).also(input::readFully)
            if (!magic.contentEquals(MAGIC)) throw CaseIntegrityException("invalid professional case magic")
            val schema = input.readInt()
            if (schema != SCHEMA_VERSION) throw CaseIntegrityException("unsupported professional case schema $schema")
            val payloadSize = input.readInt()
            if (payloadSize !in 0..MAX_PAYLOAD_BYTES) throw CaseIntegrityException("invalid professional case payload length")
            val payload = ByteArray(payloadSize).also(input::readFully)
            val storedDigest = ByteArray(DIGEST_BYTES).also(input::readFully)
            if (input.read() != -1) throw CaseIntegrityException("trailing bytes after professional case payload")
            if (!MessageDigest.isEqual(storedDigest, digest(payload))) {
                throw CaseIntegrityException("professional case payload digest mismatch")
            }
            val payloadInput = DataInputStream(ByteArrayInputStream(payload))
            val case = readCase(payloadInput)
            if (payloadInput.read() != -1) throw CaseIntegrityException("trailing bytes inside professional case payload")
            validateCaseForStorage(case)
            case
        } catch (error: CaseIntegrityException) {
            throw error
        } catch (error: EOFException) {
            throw CaseIntegrityException("truncated professional case payload", error)
        } catch (error: IllegalArgumentException) {
            throw CaseIntegrityException("invalid professional case payload", error)
        } catch (error: Exception) {
            throw CaseIntegrityException("unable to decode professional case payload", error)
        }
    }

    fun payloadHash(encoded: ByteArray): String = digest(encoded).joinToString("") { "%02x".format(it.toInt() and 0xff) }

    private fun validateCaseForStorage(case: AssessmentCase) {
        val events = case.auditTrail.events
        if (events.isEmpty()) throw CaseIntegrityException("professional case has no audit history")
        if (events.any { it.caseId != case.id }) throw CaseIntegrityException("audit history contains a different case id")
        if (events.first().type != AuditEventType.CASE_CREATED) throw CaseIntegrityException("audit history does not begin with case creation")
        if (events.first().at != case.createdAt) throw CaseIntegrityException("case creation time does not match audit history")
        if (case.version != events.size.toLong()) throw CaseIntegrityException("case version does not match audit sequence")
        if (case.revision < 1 || (case.revision == 1) != (case.supersedesSnapshotId == null)) {
            throw CaseIntegrityException("invalid case revision lineage")
        }
        if (!stateMatchesLastEvent(case.state, events.last().type)) {
            throw CaseIntegrityException("case state does not match the last audit event")
        }

        val authorization = case.authorization
        if (authorization != null) {
            if (authorization.scopeHash != case.scopeHash || authorization.dataPolicyHash != case.dataPolicyHash) {
                throw CaseIntegrityException("authorization is not bound to the stored case scope/data policy")
            }
            val event = events.lastOrNull { it.type == AuditEventType.AUTHORIZATION_APPROVED }
                ?: throw CaseIntegrityException("stored authorization has no audit event")
            if (event.objectId != authorization.id.value) throw CaseIntegrityException("authorization audit identity mismatch")
            val expectedDetails = "artifact=${authorization.artifactHash.value};from=${authorization.validFrom};until=${authorization.validUntil}"
            if (event.detailsHash != Sha256.digest(expectedDetails)) {
                throw CaseIntegrityException("authorization audit details mismatch")
            }
        } else if (case.state !in setOf(CaseState.DRAFT, CaseState.PREPARED, CaseState.AWAITING_AUTHORIZATION, CaseState.CANCELLED)) {
            throw CaseIntegrityException("case state ${case.state} requires authorization")
        }

        case.reviewDecision?.let { decision ->
            val type = if (decision.outcome == CaseReviewOutcome.ACCEPTED) AuditEventType.REVIEW_ACCEPTED else AuditEventType.REVIEW_RETURNED
            val event = events.lastOrNull { it.type == type && it.actor.id == decision.reviewer.id }
                ?: throw CaseIntegrityException("case review decision has no matching audit event")
            if (event.detailsHash != Sha256.digest(decision.reason)) throw CaseIntegrityException("case review audit details mismatch")
        }

        if (case.state in setOf(CaseState.READY_TO_FINALIZE, CaseState.FINALIZED, CaseState.SUPERSEDED)) {
            val decision = case.reviewDecision
            if (decision == null || decision.outcome != CaseReviewOutcome.ACCEPTED) {
                throw CaseIntegrityException("finalization states require an accepted review")
            }
        }

        val snapshot = case.finalizedSnapshot
        if (case.state in setOf(CaseState.FINALIZED, CaseState.SUPERSEDED)) {
            if (snapshot == null) throw CaseIntegrityException("finalized case is missing its snapshot")
            val auth = authorization ?: throw CaseIntegrityException("finalized case is missing authorization")
            if (snapshot.caseId != case.id || snapshot.caseNumber != case.caseNumber || snapshot.revision != case.revision) {
                throw CaseIntegrityException("finalized snapshot case identity mismatch")
            }
            if (snapshot.authorizationArtifactHash != auth.artifactHash || snapshot.scopeHash != case.scopeHash || snapshot.dataPolicyHash != case.dataPolicyHash) {
                throw CaseIntegrityException("finalized snapshot authorization/scope mismatch")
            }
            val finalEvent = events.lastOrNull { it.type == AuditEventType.CASE_FINALIZED }
                ?: throw CaseIntegrityException("finalized snapshot has no finalization audit event")
            if (finalEvent.objectId != snapshot.id.value || finalEvent.eventHash != snapshot.auditHead) {
                throw CaseIntegrityException("finalized snapshot audit head mismatch")
            }
            val expected = FinalizedSnapshot.create(
                snapshot.id, snapshot.caseId, snapshot.caseNumber, snapshot.revision, snapshot.finalizedAt,
                snapshot.authorizationArtifactHash, snapshot.scopeHash, snapshot.dataPolicyHash,
                snapshot.auditHead, snapshot.material,
            )
            if (expected.contentHash != snapshot.contentHash) throw CaseIntegrityException("finalized snapshot content hash mismatch")
        } else if (snapshot != null) {
            throw CaseIntegrityException("non-finalized case contains finalized snapshot material")
        }
    }

    private fun stateMatchesLastEvent(state: CaseState, type: AuditEventType): Boolean = when (state) {
        CaseState.DRAFT -> type == AuditEventType.CASE_CREATED
        CaseState.PREPARED -> type == AuditEventType.CASE_PREPARED
        CaseState.AWAITING_AUTHORIZATION -> type == AuditEventType.AUTHORIZATION_REQUESTED
        CaseState.AUTHORIZED -> type == AuditEventType.AUTHORIZATION_APPROVED
        CaseState.COLLECTING -> type == AuditEventType.COLLECTION_STARTED
        CaseState.PAUSED -> type == AuditEventType.COLLECTION_PAUSED
        CaseState.EVIDENCE_REVIEW -> type == AuditEventType.EVIDENCE_REVIEW_STARTED
        CaseState.RECONCILING -> type == AuditEventType.RECONCILIATION_STARTED
        CaseState.ASSESSING -> type == AuditEventType.ASSESSMENT_STARTED || type == AuditEventType.REVIEW_RETURNED
        CaseState.REVIEW_PENDING -> type == AuditEventType.REVIEW_REQUESTED
        CaseState.READY_TO_FINALIZE -> type == AuditEventType.REVIEW_ACCEPTED
        CaseState.FINALIZED -> type == AuditEventType.CASE_FINALIZED
        CaseState.SUPERSEDED -> type == AuditEventType.CASE_SUPERSEDED
        CaseState.EXPIRED -> type == AuditEventType.CASE_EXPIRED
        CaseState.CANCELLED -> type == AuditEventType.CASE_CANCELLED
    }

    private fun writeCase(out: DataOutputStream, case: AssessmentCase) {
        out.writeText(case.id.value)
        out.writeText(case.caseNumber)
        out.writeInt(case.revision)
        out.writeNullableText(case.supersedesSnapshotId?.value)
        out.writeText(case.state.name)
        out.writeContext(case.context)
        out.writeObjective(case.objective)
        out.writeScope(case.scope)
        out.writeDataPolicy(case.dataPolicy)
        out.writeEnumSet(case.stopConditions)
        out.writeAuthorization(case.authorization)
        out.writeReview(case.reviewDecision)
        out.writeSnapshot(case.finalizedSnapshot)
        out.writeAuditEvents(case.auditTrail.events)
        out.writeInstant(case.createdAt)
        out.writeLong(case.version)
    }

    private fun readCase(input: DataInputStream): AssessmentCase {
        val id = CaseId(input.readText())
        val caseNumber = input.readText()
        val revision = input.readInt()
        val supersedes = input.readNullableText()?.let(::SnapshotId)
        val state = input.readEnum(CaseState.entries)
        val context = input.readContext()
        val objective = input.readObjective()
        val scope = input.readScope()
        val dataPolicy = input.readDataPolicy()
        val stopConditions = input.readEnumSet(StopCondition.entries)
        val authorization = input.readAuthorization()
        val review = input.readReview()
        val snapshot = input.readSnapshot()
        val events = input.readAuditEvents()
        val createdAt = input.readInstant()
        val version = input.readLong()
        val auditTrail = AuditTrail.restore(events)
        return AssessmentCase(
            id = id,
            caseNumber = caseNumber,
            revision = revision,
            supersedesSnapshotId = supersedes,
            state = state,
            context = context,
            objective = objective,
            scope = scope,
            dataPolicy = dataPolicy,
            stopConditions = stopConditions,
            authorization = authorization,
            reviewDecision = review,
            finalizedSnapshot = snapshot,
            auditTrail = auditTrail,
            createdAt = createdAt,
            version = version,
        )
    }

    private fun DataOutputStream.writeContext(value: AssessmentContext) {
        writeText(value.legalEntity); writeText(value.site); writeText(value.processArea); writeText(value.assessmentPack)
    }

    private fun DataInputStream.readContext() = AssessmentContext(readText(), readText(), readText(), readText())

    private fun DataOutputStream.writeObjective(value: AssessmentObjective) {
        writeText(value.question); writeText(value.requestedDecision); writeText(value.stakeholderRole)
        writeStringSet(value.successCriteria); writeStringSet(value.evidenceNeeded)
    }

    private fun DataInputStream.readObjective() = AssessmentObjective(readText(), readText(), readText(), readStringSet(), readStringSet())

    private fun DataOutputStream.writeScope(value: CaseScope) {
        val cidrs = value.cidrs.sortedWith(compareBy<IPv4Cidr> { it.network }.thenBy { it.prefix })
        writeCount(cidrs.size); cidrs.forEach { writeInt(it.network); writeInt(it.prefix) }
        writeIntSet(value.excludedAddresses)
        writeEnumSet(value.allowedOperations)
        writeEnumSet(value.evidenceMethods)
        writeStringSet(value.interfaceIds)
        writeStringSet(value.capturePoints)
        writeStringSet(value.physicalAreas)
    }

    private fun DataInputStream.readScope(): CaseScope {
        val cidrs = linkedSetOf<IPv4Cidr>()
        repeat(readCount()) {
            val network = readInt()
            val prefix = readInt()
            require(prefix in 0..32) { "invalid stored CIDR prefix" }
            val cidr = IPv4Cidr.parse("${renderAddress(network)}/$prefix")
            require(cidr.network == network) { "stored CIDR is not canonical" }
            require(cidrs.add(cidr)) { "duplicate stored CIDR" }
        }
        return CaseScope(
            cidrs = cidrs,
            excludedAddresses = readIntSet(),
            allowedOperations = readEnumSet(Operation.entries),
            evidenceMethods = readEnumSet(EvidenceMethod.entries),
            interfaceIds = readStringSet(),
            capturePoints = readStringSet(),
            physicalAreas = readStringSet(),
        )
    }

    private fun DataOutputStream.writeDataPolicy(value: CaseDataPolicy) {
        writeText(value.classification)
        writeBoolean(value.retainPayloads)
        writeBoolean(value.includeRawCapturesInExport)
        writeNullableText(value.exportDestination)
        writeNullableInstant(value.deleteAfter)
    }

    private fun DataInputStream.readDataPolicy() = CaseDataPolicy(
        classification = readText(),
        retainPayloads = readBoolean(),
        includeRawCapturesInExport = readBoolean(),
        exportDestination = readNullableText(),
        deleteAfter = readNullableInstant(),
    )

    private fun DataOutputStream.writeAuthorization(value: CaseAuthorization?) {
        writeBoolean(value != null)
        if (value == null) return
        writeText(value.id.value); writeText(value.artifactHash.value); writeInstant(value.validFrom); writeInstant(value.validUntil)
        writeText(value.scopeHash.value); writeText(value.dataPolicyHash.value)
        val approvals = value.approvals.sortedWith(compareBy<Approval> { it.actor.role.name }.thenBy { it.actor.id.value }.thenBy { it.at })
        writeCount(approvals.size)
        approvals.forEach { writeActor(it.actor); writeInstant(it.at) }
    }

    private fun DataInputStream.readAuthorization(): CaseAuthorization? {
        if (!readBoolean()) return null
        val id = AuthorizationId(readText())
        val artifactHash = Sha256(readText())
        val validFrom = readInstant()
        val validUntil = readInstant()
        val scopeHash = Sha256(readText())
        val dataPolicyHash = Sha256(readText())
        val approvals = linkedSetOf<Approval>()
        repeat(readCount()) {
            val approval = Approval(readActor(), readInstant())
            require(approvals.add(approval)) { "duplicate approval" }
        }
        return CaseAuthorization(id, artifactHash, validFrom, validUntil, scopeHash, dataPolicyHash, approvals)
    }

    private fun DataOutputStream.writeReview(value: CaseReviewDecision?) {
        writeBoolean(value != null)
        if (value == null) return
        writeActor(value.reviewer); writeText(value.outcome.name); writeText(value.reason); writeInstant(value.at)
    }

    private fun DataInputStream.readReview(): CaseReviewDecision? {
        if (!readBoolean()) return null
        return CaseReviewDecision(readActor(), readEnum(CaseReviewOutcome.entries), readText(), readInstant())
    }

    private fun DataOutputStream.writeSnapshot(value: FinalizedSnapshot?) {
        writeBoolean(value != null)
        if (value == null) return
        writeText(value.id.value); writeText(value.caseId.value); writeText(value.caseNumber); writeInt(value.revision); writeInstant(value.finalizedAt)
        writeText(value.authorizationArtifactHash.value); writeText(value.scopeHash.value); writeText(value.dataPolicyHash.value); writeText(value.auditHead.value)
        writeShaMap(value.material.objectHashes); writeText(value.material.toolBuild); writeShaMap(value.material.packVersions)
        writeText(value.contentHash.value)
    }

    private fun DataInputStream.readSnapshot(): FinalizedSnapshot? {
        if (!readBoolean()) return null
        return FinalizedSnapshot(
            id = SnapshotId(readText()),
            caseId = CaseId(readText()),
            caseNumber = readText(),
            revision = readInt(),
            finalizedAt = readInstant(),
            authorizationArtifactHash = Sha256(readText()),
            scopeHash = Sha256(readText()),
            dataPolicyHash = Sha256(readText()),
            auditHead = Sha256(readText()),
            material = SnapshotMaterial(readShaMap(), readText(), readShaMap()),
            contentHash = Sha256(readText()),
        )
    }

    private fun DataOutputStream.writeAuditEvents(events: List<AuditEvent>) {
        writeCount(events.size)
        events.forEach { event ->
            writeLong(event.sequence); writeText(event.caseId.value); writeInstant(event.at); writeActor(event.actor); writeText(event.type.name)
            writeText(event.objectType); writeText(event.objectId); writeText(event.detailsHash.value); writeNullableText(event.previousHash?.value); writeText(event.eventHash.value)
        }
    }

    private fun DataInputStream.readAuditEvents(): List<AuditEvent> = buildList {
        repeat(readCount()) {
            add(AuditEvent(
                sequence = readLong(), caseId = CaseId(readText()), at = readInstant(), actor = readActor(),
                type = readEnum(AuditEventType.entries), objectType = readText(), objectId = readText(),
                detailsHash = Sha256(readText()), previousHash = readNullableText()?.let(::Sha256), eventHash = Sha256(readText()),
            ))
        }
    }

    private fun DataOutputStream.writeActor(value: ActorRef) {
        writeText(value.id.value); writeText(value.displayName); writeText(value.role.name)
    }

    private fun DataInputStream.readActor() = ActorRef(ActorId(readText()), readText(), readEnum(ActorRole.entries))

    private fun DataOutputStream.writeShaMap(value: Map<String, Sha256>) {
        val entries = value.toSortedMap().entries
        writeCount(entries.size); entries.forEach { writeText(it.key); writeText(it.value.value) }
    }

    private fun DataInputStream.readShaMap(): Map<String, Sha256> {
        val result = linkedMapOf<String, Sha256>()
        repeat(readCount()) {
            val key = readText(); val value = Sha256(readText())
            require(result.put(key, value) == null) { "duplicate map key" }
        }
        return result
    }

    private fun DataOutputStream.writeStringSet(value: Set<String>) {
        val values = value.sorted(); writeCount(values.size); values.forEach { writeText(it) }
    }

    private fun DataInputStream.readStringSet(): Set<String> {
        val result = linkedSetOf<String>()
        repeat(readCount()) { require(result.add(readText())) { "duplicate string set value" } }
        return result
    }

    private fun DataOutputStream.writeIntSet(value: Set<Int>) {
        val values = value.sorted(); writeCount(values.size); values.forEach { writeInt(it) }
    }

    private fun DataInputStream.readIntSet(): Set<Int> {
        val result = linkedSetOf<Int>()
        repeat(readCount()) { require(result.add(readInt())) { "duplicate integer set value" } }
        return result
    }

    private fun <T : Enum<T>> DataOutputStream.writeEnumSet(value: Set<T>) {
        val values = value.sortedBy { it.name }; writeCount(values.size); values.forEach { writeText(it.name) }
    }

    private fun <T : Enum<T>> DataInputStream.readEnumSet(values: List<T>): Set<T> {
        val result = linkedSetOf<T>()
        repeat(readCount()) { require(result.add(readEnum(values))) { "duplicate enum set value" } }
        return result
    }

    private fun <T : Enum<T>> DataInputStream.readEnum(values: List<T>): T {
        val name = readText()
        return values.firstOrNull { it.name == name } ?: throw CaseIntegrityException("unknown enum value $name")
    }

    private fun DataOutputStream.writeInstant(value: Instant) { writeLong(value.epochSecond); writeInt(value.nano) }
    private fun DataInputStream.readInstant(): Instant = Instant.ofEpochSecond(readLong(), readInt().also { require(it in 0..999_999_999) }.toLong())
    private fun DataOutputStream.writeNullableInstant(value: Instant?) { writeBoolean(value != null); if (value != null) writeInstant(value) }
    private fun DataInputStream.readNullableInstant(): Instant? = if (readBoolean()) readInstant() else null

    private fun DataOutputStream.writeNullableText(value: String?) { writeBoolean(value != null); if (value != null) writeText(value) }
    private fun DataInputStream.readNullableText(): String? = if (readBoolean()) readText() else null

    private fun DataOutputStream.writeText(value: String) {
        val bytes = value.toByteArray(StandardCharsets.UTF_8)
        require(bytes.size <= MAX_STRING_BYTES) { "stored string exceeds bound" }
        writeInt(bytes.size); write(bytes)
    }

    private fun DataInputStream.readText(): String {
        val size = readInt()
        if (size !in 0..MAX_STRING_BYTES) throw CaseIntegrityException("invalid stored string length")
        val bytes = ByteArray(size).also(::readFully)
        return StandardCharsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(bytes)).toString()
    }

    private fun DataOutputStream.writeCount(value: Int) {
        require(value in 0..MAX_COLLECTION_ITEMS) { "stored collection exceeds bound" }; writeInt(value)
    }

    private fun DataInputStream.readCount(): Int {
        val value = readInt(); if (value !in 0..MAX_COLLECTION_ITEMS) throw CaseIntegrityException("invalid stored collection size"); return value
    }

    private fun digest(value: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(value)

    private fun renderAddress(address: Int): String = listOf(
        address ushr 24 and 0xff,
        address ushr 16 and 0xff,
        address ushr 8 and 0xff,
        address and 0xff,
    ).joinToString(".")
}
