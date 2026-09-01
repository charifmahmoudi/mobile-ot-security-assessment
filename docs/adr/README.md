# Architecture decision records

This index is the authoritative register of Atlas OT Scout architecture decisions. ADR numbers are unique; the filename number and the first heading must agree. Accepted ADRs describe the current P0 design. Proposed ADRs remain open decisions and must not be treated as implemented capability.

| ADR | Status | Decision |
|---|---|---|
| [0001](0001-android-first-offline-first.md) | Proposed | Android-first and offline-first |
| [0002](0002-separate-network-broker.md) | Accepted for P0 | Separate the network broker from the Case App |
| [0003](0003-external-passive-capture.md) | Accepted for P0 | Use an external receive-only path for whole-segment capture |
| [0004](0004-evidence-layering.md) | Accepted for P0 | Preserve evidence, claims, assets, and findings as separate layers |
| [0005](0005-passive-first-policy-gate.md) | Proposed | Passive first with a deterministic action gate |
| [0006](0006-capture-accessory-boundary.md) | Proposed | Make the capture-accessory boundary explicit |

## ADR rules

- Use the next unused four-digit number.
- State `Status` and `Date` near the top.
- Record context, decision, consequences, and rejected alternatives when material.
- Replace a decision by adding a new ADR and marking the old one superseded; do not silently rewrite accepted history.
- Link accepted decisions from the relevant architecture or product document.

Return to the [documentation index](../README.md).
