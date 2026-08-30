# Field-assessor user stories and acceptance criteria

## Journey 1 — choose the safe assessment mode

**As an assessor arriving on site, I need to choose passive or active work before entering technical details, so I do not accidentally transmit on an OT network.**

- The home screen states whether packets can be sent.
- Passive analysis is a first-class action, not hidden under scanning.
- Active assessment opens an authorization and scope screen before the broker is contacted.

## Journey 2 — analyze an existing capture

**As an assessor who received a PCAP/PCAPNG from a SPAN port, TAP, capture appliance, or another inventory tool, I need to import it offline and understand what was actually observed.**

- Android's document picker accepts classic PCAP and PCAPNG through a content URI without storage-wide permission.
- The app validates PCAP records or PCAPNG sections, interfaces, blocks and packet bounds before interpreting Ethernet/IP/transport framing.
- The result shows capture hash, duration, total packets, supported OT packets, protocols, endpoints, role, confidence, and the evidence behind classification.
- Unsupported link types are skipped with an explicit warning; oversized, truncated, malformed, or wholly unsupported captures fail closed or return "no supported evidence" without claiming the segment is empty.
- The screen reminds the assessor that a capture is a visibility sample, not a complete asset inventory.
- Saving an asset is a separate analyst decision after review.

## Journey 3 — perform constrained active identification

**As an authorized assessor, I need to identify one controller without reading or changing process registers.**

- The screen presents three visible steps: work order, exact target/scope, and authorization. Every populated field keeps a visible label.
- Target IPv4, CIDR membership and Modbus unit are checked before the broker is contacted.
- The exact operation and limits are visible before approval.
- The action remains disabled until written operational and security authorization is confirmed.
- The case app creates a 30-second, one-use P-256-signed grant.
- The isolated broker selects the connected Android network, enforces CIDR/port/unit/time/packet limits, persists the nonce, and sends one FC 43 / MEI 14 basic-identity request.
- The result distinguishes full identity from protocol-only service confirmation and shows interface and evidence size.

## Journey 4 — recover safely

**As an assessor facing a missing broker, malformed capture, unreachable controller, or unsupported identity function, I need an actionable explanation without the app silently broadening the scan.**

- Errors appear in the current workflow with a safe return action.
- No fallback port sweep, unit sweep, register read, or scope expansion occurs.
- A valid Modbus exception confirms the protocol but is not reported as vendor/model evidence.

## Automated acceptance mapping

| Story | JVM corpus | Android UI | Live emulator |
|---|---:|---:|---:|
| Mode and authorization | Grant policy | API 29 + 35 | — |
| Passive capture analysis | Four research PCAPs + PCAPNG conversion/corruption | Real content-URI import and four result screens | — |
| Active identification | Grant/codec tests | Authorization UI → signed case-to-broker flow | PyModbus, modbus-tk, Conpot |
| Safe failure | Malformed capture, grant and replay tests | Validation/error-state checks | modbus-tk and Conpot service-only behavior |
