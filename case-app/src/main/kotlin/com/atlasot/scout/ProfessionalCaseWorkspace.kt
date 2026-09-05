package com.atlasot.scout

import com.atlasot.domain.ActorId
import com.atlasot.domain.ActorRef
import com.atlasot.domain.ActorRole
import com.atlasot.domain.Approval
import com.atlasot.domain.AssessmentCase
import com.atlasot.domain.AssessmentContext
import com.atlasot.domain.AssessmentObjective
import com.atlasot.domain.AuthorizationId
import com.atlasot.domain.CaseAuthorization
import com.atlasot.domain.CaseDataPolicy
import com.atlasot.domain.CaseId
import com.atlasot.domain.CaseScope
import com.atlasot.domain.CaseState
import com.atlasot.domain.EvidenceMethod
import com.atlasot.domain.IPv4Cidr
import com.atlasot.domain.Operation
import com.atlasot.domain.Sha256
import com.atlasot.domain.StopCondition
import java.time.Instant

data class ProfessionalCaseParticipants(
    val assessor: ActorRef,
    val operationalApprover: ActorRef,
    val securityApprover: ActorRef,
    val independentReviewer: ActorRef,
) {
    init {
        require(assessor.role == ActorRole.ASSESSOR)
        require(operationalApprover.role == ActorRole.OPERATIONAL_APPROVER)
        require(securityApprover.role == ActorRole.SECURITY_APPROVER)
        require(independentReviewer.role == ActorRole.REVIEWER)
    }
}

data class ProfessionalCaseInput(
    val caseId: String,
    val caseNumber: String,
    val legalEntity: String,
    val site: String,
    val processArea: String,
    val question: String,
    val requestedDecision: String,
    val stakeholderRole: String,
    val successCriteria: Set<String>,
    val evidenceNeeded: Set<String>,
    val scopeCidrs: Set<String>,
    val excludedAddresses: Set<String>,
    val methods: Set<EvidenceMethod>,
    val capturePoints: Set<String>,
    val physicalAreas: Set<String>,
    val classification: String,
    val retainPayloads: Boolean,
    val includeRawCapturesInExport: Boolean,
    val exportDestination: String?,
    val deleteAfter: Instant?,
    val stopConditions: Set<StopCondition>,
    val participants: ProfessionalCaseParticipants,
)

data class ProposedCaseAuthorization(
    val id: String,
    val artifactHash: Sha256,
    val validFrom: Instant,
    val validUntil: Instant,
    val scopeHash: Sha256,
    val dataPolicyHash: Sha256,
    val approvals: Set<Approval>,
)

/** Application boundary for the professional aggregate; UI code never invents authorization state. */
class ProfessionalCaseApplication(private val repository: SqlCipherCaseRepository) {
    fun createPrepared(input: ProfessionalCaseInput, at: Instant): AssessmentCase {
        val scope = CaseScope(
            cidrs = input.scopeCidrs.mapTo(linkedSetOf(), IPv4Cidr::parse),
            excludedAddresses = input.excludedAddresses.mapTo(linkedSetOf(), IPv4Cidr::parseAddress),
            allowedOperations = if (EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY in input.methods) {
                setOf(Operation.MODBUS_DEVICE_ID_BASIC)
            } else {
                emptySet()
            },
            evidenceMethods = input.methods,
            capturePoints = input.capturePoints,
            physicalAreas = input.physicalAreas,
        )
        val draft = AssessmentCase.createDraft(
            id = CaseId(input.caseId),
            caseNumber = input.caseNumber,
            context = AssessmentContext(input.legalEntity, input.site, input.processArea, "P0-WATER"),
            objective = AssessmentObjective(
                input.question,
                input.requestedDecision,
                input.stakeholderRole,
                input.successCriteria,
                input.evidenceNeeded,
            ),
            scope = scope,
            dataPolicy = CaseDataPolicy(
                input.classification,
                input.retainPayloads,
                input.includeRawCapturesInExport,
                input.exportDestination,
                input.deleteAfter,
            ),
            stopConditions = input.stopConditions,
            creator = input.participants.assessor,
            createdAt = at,
        )
        val prepared = draft.prepare(input.participants.assessor, at.plusMillis(1))
        val awaiting = prepared.requestAuthorization(input.participants.assessor, at.plusMillis(2))
        repository.saveNewCase(awaiting, input.participants)
        return awaiting
    }

    fun proposeAuthorization(
        caseId: CaseId,
        operationalApproved: Boolean,
        securityApproved: Boolean,
        validFrom: Instant,
        validUntil: Instant,
        artifactText: String,
        at: Instant,
    ): ProposedCaseAuthorization {
        val case = requireNotNull(repository.load(caseId)) { "professional case not found" }
        require(case.state == CaseState.AWAITING_AUTHORIZATION) { "case is not awaiting authorization" }
        val participants = requireNotNull(repository.loadParticipants(caseId)) { "case participants not found" }
        val approvals = buildSet {
            if (operationalApproved) add(Approval(participants.operationalApprover, at))
            if (securityApproved) add(Approval(participants.securityApprover, at))
        }
        return ProposedCaseAuthorization(
            id = "AUTH-${case.caseNumber}-R${case.revision}",
            artifactHash = Sha256.digest(artifactText),
            validFrom = validFrom,
            validUntil = validUntil,
            scopeHash = case.scopeHash,
            dataPolicyHash = case.dataPolicyHash,
            approvals = approvals,
        )
    }

    fun authorize(caseId: CaseId, proposal: ProposedCaseAuthorization, at: Instant): AssessmentCase {
        val current = requireNotNull(repository.load(caseId)) { "professional case not found" }
        val authorization = CaseAuthorization(
            AuthorizationId(proposal.id),
            proposal.artifactHash,
            proposal.validFrom,
            proposal.validUntil,
            proposal.scopeHash,
            proposal.dataPolicyHash,
            proposal.approvals,
        )
        val authorized = current.authorize(
            recordedBy = requireNotNull(repository.loadParticipants(caseId)).assessor,
            at = at,
            newAuthorization = authorization,
        )
        repository.save(authorized, expectedVersion = current.version)
        return authorized
    }

    fun startCollection(caseId: CaseId, at: Instant): AssessmentCase {
        val current = requireNotNull(repository.load(caseId)) { "professional case not found" }
        val assessor = requireNotNull(repository.loadParticipants(caseId)).assessor
        val collecting = current.startCollection(assessor, at)
        repository.save(collecting, expectedVersion = current.version)
        return collecting
    }

    fun assertOperationAllowed(caseId: CaseId, target: String, at: Instant): AssessmentCase {
        val current = requireNotNull(repository.load(caseId)) { "professional case not found" }
        current.assertOperationAllowed(Operation.MODBUS_DEVICE_ID_BASIC, IPv4Cidr.parseAddress(target), at)
        return current
    }

    fun load(caseId: CaseId): AssessmentCase? = repository.load(caseId)
    fun participants(caseId: CaseId): ProfessionalCaseParticipants? = repository.loadParticipants(caseId)
    fun list() = repository.list()
}

object GoldenCustomerAssessment {
    const val CASE_ID = "GOLDEN-WATER-001"
    const val CASE_NUMBER = "ATLAS-PILOT-001"
    const val TARGET = "10.0.2.2"
    const val TARGET_SCOPE = "10.0.2.2/32"

    fun input(at: Instant, caseId: String = CASE_ID): ProfessionalCaseInput = ProfessionalCaseInput(
        caseId = caseId,
        caseNumber = if (caseId == CASE_ID) CASE_NUMBER else "$CASE_NUMBER-${caseId.takeLast(8)}",
        legalEntity = "North River Water Utility",
        site = "North River Treatment Plant",
        processArea = "Raw-water intake pumping control segment",
        question = "Does the installed pumping-control equipment match the approved maintenance baseline?",
        requestedDecision = "Approve tomorrow's maintenance scope or resolve named inventory exceptions first",
        stakeholderRole = "Maintenance and operations owner",
        successCriteria = setOf("Every critical expected asset has an explicit reconciliation outcome"),
        evidenceNeeded = setOf("Approved inventory seed", "Bounded passive evidence", "One allowlisted Modbus identity"),
        scopeCidrs = setOf(TARGET_SCOPE),
        excludedAddresses = setOf("10.0.2.15"),
        methods = setOf(EvidenceMethod.H1_EXACT_ACTIVE_IDENTITY, EvidenceMethod.H3_OFFLINE_IMPORT),
        capturePoints = emptySet(),
        physicalAreas = setOf("Raw-water intake electrical room"),
        classification = "customer-confidential",
        retainPayloads = false,
        includeRawCapturesInExport = false,
        exportDestination = "Customer-controlled encrypted handoff",
        deleteAfter = at.plusSeconds(30L * 24 * 3600),
        stopConditions = setOf(
            StopCondition.PROCESS_ALARM,
            StopCondition.NETWORK_INSTABILITY,
            StopCondition.APPROVER_REQUEST,
            StopCondition.MANUAL_STOP,
        ),
        participants = ProfessionalCaseParticipants(
            ActorRef(ActorId("assessor-amina"), "Amina El Idrissi", ActorRole.ASSESSOR),
            ActorRef(ActorId("operations-youssef"), "Youssef Benali", ActorRole.OPERATIONAL_APPROVER),
            ActorRef(ActorId("security-salma"), "Salma Alaoui", ActorRole.SECURITY_APPROVER),
            ActorRef(ActorId("reviewer-omar"), "Omar Tazi", ActorRole.REVIEWER),
        ),
    )
}
