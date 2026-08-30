# Product vision

## One-line promise

Turn an Android phone and a small field kit into a safe, offline-first OT/IoT asset discovery and assessment workspace.

## Job to be done

When a plant engineer, integrator, auditor, or MSSP enters an unfamiliar site, help them establish a defensible inventory of wired, Wi-Fi and Bluetooth assets, preserve packet-level evidence, identify likely device families, and produce prioritized remediation without deploying a server or sending sensitive plant data to a cloud service.

## Primary users

| Persona | Context | Unmet need | Buying trigger |
|---|---|---|---|
| OT/automation engineer | Owns uptime, often lacks security tooling | Know what is connected without disrupting it | Audit, expansion, incident, insurer request |
| Security assessor/MSSP | Visits many heterogeneous sites | Portable, repeatable evidence collection | Faster engagements and consistent reports |
| Industrial integrator | Already trusted for PLC/SCADA work | Add asset and exposure assessment | Service differentiation |
| CISO/IT manager | Accountable but distant from plant floor | Evidence and governance across sites | DGSSI/NIST/IEC 62443 program |
| SME plant manager | Small team and constrained budget | Affordable baseline and clear actions | Customer/supply-chain requirement |

## Core capabilities

- USB-C Ethernet, Wi-Fi and BLE interface inventory.
- Passive packet/metadata collection with timestamps and interface provenance.
- Active discovery profiles classified by operational risk.
- Local encrypted asset graph, observations, evidence and assessment history.
- Vendor/model confidence scoring; never turn a weak fingerprint into a fact.
- Import/export adapters for CSV, JSON, CycloneDX, STIX/TAXII where appropriate, and documented APIs.
- Offline reporting in French/English initially; Arabic support planned.
- Signed knowledge packs so protocol/device knowledge can update independently of the app.

## Non-goals for the first release

- Exploit execution, credential attacks, password spraying or uncontrolled fuzzing.
- “Scan everything” behavior on a production OT subnet.
- Replacement for permanent network monitoring, a CMDB, SIEM or engineering workstation.
- Promising complete identification of every OT device.

## Success measures for a prototype

- Correctly identify at least 90% of devices in the controlled reference lab at vendor/family level.
- Zero unintended state-changing requests in packet review.
- Reproduce an assessment from immutable evidence and signed rule versions.
- Complete a 100-asset offline assessment on a mid-range Android device.
- Export a useful inventory with provenance and confidence for another system.
