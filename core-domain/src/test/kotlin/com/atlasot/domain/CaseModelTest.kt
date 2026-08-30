package com.atlasot.domain

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CaseModelTest {
    private val start = Instant.parse("2026-08-30T08:00:00Z")
    private val end = Instant.parse("2026-08-30T12:00:00Z")
    private fun draft() = AssessmentCase(
        id = "CASE-1", startsAt = start, endsAt = end,
        scope = CaseScope(setOf(IPv4Cidr.parse("192.168.10.0/24")), emptySet(), setOf(Operation.MODBUS_DEVICE_ID_BASIC))
    )

    @Test fun `complete lifecycle reaches immutable final state`() {
        val approvals = setOf(
            Approval(ApprovalRole.OPERATIONAL, "ops", start.minusSeconds(60)),
            Approval(ApprovalRole.SECURITY, "sec", start.minusSeconds(60))
        )
        val final = draft().authorize(start.minusSeconds(30), approvals).startCollection(start).pause().beginReview()
            .finalize(Approval(ApprovalRole.REVIEWER, "review", end))
        assertEquals(CaseState.FINALIZED, final.state)
        assertFailsWith<IllegalArgumentException> { final.startCollection(start) }
    }

    @Test fun `authorization requires operational and security approval`() {
        assertFailsWith<IllegalArgumentException> {
            draft().authorize(start.minusSeconds(1), setOf(Approval(ApprovalRole.OPERATIONAL, "ops", start)))
        }
    }
}
