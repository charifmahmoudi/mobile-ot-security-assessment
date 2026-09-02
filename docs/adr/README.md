# Architecture decision records

This index is the authoritative register of Atlas OT Scout architecture decisions. ADRs explain **why** a decision was made; current mechanics belong in the architecture documents. Superseded ADRs remain historical records and must not be read as current design.

| ADR | Status | Decision |
|---|---|---|
| [0001](0001-android-first-offline-first.md) | Proposed | Android-first and offline-first |
| [0002](0002-separate-network-broker.md) | Accepted for P0 | Separate the active Network Broker from the Case App |
| [0003](0003-external-passive-capture.md) | Superseded by 0007 | Historical external receive-only capture appliance |
| [0004](0004-evidence-layering.md) | Accepted for P0 | Preserve evidence, claims, assets and findings as separate layers |
| [0005](0005-passive-first-policy-gate.md) | Proposed | Passive first with a deterministic action gate |
| [0006](0006-capture-accessory-boundary.md) | Accepted for P0 | Make the capture-visibility boundary explicit |
| [0007](0007-dedicated-android-passive-capture.md) | Accepted for P0 | Integrate live passive capture into the dedicated Android appliance |

## ADR rules

- Use the next unused four-digit number.
- State status and date near the top.
- Record context, decision, consequences and rejected alternatives when material.
- Replace a decision by adding a new ADR and marking the old one superseded; do not rewrite history as though the old choice never existed.
- Link to the current architecture document instead of copying its full mechanics into the ADR.
- ADR acceptance does not imply implementation; current execution is reported only in [IMPLEMENTATION.md](../../IMPLEMENTATION.md).

Return to the [documentation index](../README.md).
