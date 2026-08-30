package com.atlasot.domain

import java.time.Instant

enum class CaseState { DRAFT, AUTHORIZED, COLLECTING, PAUSED, REVIEWING, FINALIZED, EXPIRED, CANCELLED }
enum class ApprovalRole { OPERATIONAL, SECURITY, REVIEWER }

data class Approval(val role: ApprovalRole, val actor: String, val at: Instant)

data class CaseScope(
    val cidrs: Set<IPv4Cidr>,
    val excludedAddresses: Set<Int>,
    val allowedOperations: Set<Operation>,
)

data class AssessmentCase(
    val id: String,
    val state: CaseState = CaseState.DRAFT,
    val startsAt: Instant,
    val endsAt: Instant,
    val scope: CaseScope,
    val approvals: Set<Approval> = emptySet(),
    val version: Long = 1,
) {
    fun authorize(now: Instant, newApprovals: Set<Approval>): AssessmentCase {
        require(state == CaseState.DRAFT) { "only draft cases can be authorized" }
        require(now.isBefore(endsAt)) { "authorization window has expired" }
        val roles = newApprovals.map { it.role }.toSet()
        require(ApprovalRole.OPERATIONAL in roles && ApprovalRole.SECURITY in roles) {
            "operational and security approvals are required"
        }
        return copy(state = CaseState.AUTHORIZED, approvals = newApprovals, version = version + 1)
    }

    fun startCollection(now: Instant): AssessmentCase {
        require(state == CaseState.AUTHORIZED || state == CaseState.PAUSED) { "case is not armed" }
        require(!now.isBefore(startsAt) && now.isBefore(endsAt)) { "outside authorization window" }
        return copy(state = CaseState.COLLECTING, version = version + 1)
    }

    fun pause(): AssessmentCase {
        require(state == CaseState.COLLECTING) { "only a collecting case can pause" }
        return copy(state = CaseState.PAUSED, version = version + 1)
    }

    fun beginReview(): AssessmentCase {
        require(state == CaseState.COLLECTING || state == CaseState.PAUSED) { "collection has not started" }
        return copy(state = CaseState.REVIEWING, version = version + 1)
    }

    fun finalize(reviewer: Approval): AssessmentCase {
        require(state == CaseState.REVIEWING) { "case is not in review" }
        require(reviewer.role == ApprovalRole.REVIEWER) { "reviewer approval is required" }
        return copy(state = CaseState.FINALIZED, approvals = approvals + reviewer, version = version + 1)
    }
}

enum class Operation { MODBUS_DEVICE_ID_BASIC, OPCUA_DISCOVERY, ICMP_REACHABILITY, TCP_SERVICE_CONFIRM }
