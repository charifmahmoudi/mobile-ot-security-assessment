# Requirements traceability and prototype gates

| Business/customer need | Requirement IDs | Design evidence | Prototype verification | Gate |
|---|---|---|---|---|
| avoid production disruption | SAFE-001–006 | action-gate ADR and safety model | golden packets; cancellation; zero writes | A1 |
| see wired evidence | COL-001–005 | capture modes/accessory ADR | two NIC + two TAP configurations | M0 |
| work with no cloud | DATA-001–004 | offline architecture | airplane-mode 100-asset case | P0 |
| trust identity | ID-001–005 | evidence/confidence schema | labeled corpus accuracy and conflict tests | P0/A1 |
| cover common OT | ID-004 | technology evidence matrix | Modbus/CIP/OPC UA lab fixtures | A1 |
| import existing inventory | DATA-005–006 | connector contract | three representative imports and round-trip | P0 |
| fit affordable phone | PLAT-001–004 | Android ADR | mid-range ARM64 performance/battery | M0 |
| defend supply chain | QUAL-001–005 | SDLC | fuzzing, SBOM, signing and review evidence | F0 |
| produce economic outcome | business model | service blueprint | ≥30% reconciliation-time reduction | F0 |
| create willingness-to-pay evidence | commercial gates | fixed-price offer | two paid/binding pilots | F0 |

## Test evidence required per protocol

- official specification/manual citation;
- exact bytes or generated canonical representation;
- owned simulator/device fixture;
- expected and malformed responses;
- network rate/retry/timeout behavior;
- safe cancellation;
- identity confidence output;
- PCAP artifact hash;
- reviewer approval;
- versioned profile signature.
