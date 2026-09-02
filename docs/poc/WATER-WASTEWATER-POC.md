# P0-WATER product contract

_Status: target product contract. Current implementation status is maintained only in [IMPLEMENTATION.md](../../IMPLEMENTATION.md)._

P0-WATER is the first bounded Atlas OT Scout assessment product. This document owns the **scope, permitted assessment modes, checks, deliverable and definition of done**. Procedure belongs in [ASSESSMENT-METHOD.md](ASSESSMENT-METHOD.md), exact network mechanics belong in [NETWORK-EXECUTION.md](../architecture/NETWORK-EXECUTION.md), and test detail belongs in [TEST-AND-ACCEPTANCE.md](TEST-AND-ACCEPTANCE.md).

## 1. Outcome

An authorized assessor can use one Atlas kit to assess **one water/wastewater pumping or treatment control segment**, reconcile approved evidence with the customer's expected inventory, review evidence-linked conditions and hand off an externally verifiable assessment package without a cloud dependency.

P0-WATER is not a penetration test, certification audit, vulnerability exploit test or enterprise-wide monitoring deployment.

## 2. Assessment unit

One case is bounded to:

- one legal entity and physical site;
- one named process area;
- one Layer-2 control segment/VLAN;
- up to 64 imported asset records;
- up to 256 observed endpoints;
- up to 16 allowlisted active-identity targets;
- one approved passive capture point or imported capture set;
- up to four hours of field collection;
- one assessor and one reviewer;
- one finalized report revision.

Larger work is split into separate cases.

## 3. Roles

| Role | Product responsibility |
|---|---|
| Assessor | Prepare the case, collect/import evidence, propose reconciliation and findings |
| Operational approver | Approve process scope, criticality, operating window and stop authority |
| Security approver | Approve interfaces, active targets, retention and export constraints |
| Reviewer | Accept/reject material identity claims and findings and authorize finalization |
| Pack administrator | Install approved signed content packs without changing finalized cases |

The audit trail records the role used for each approval or review action.

## 4. Evidence modes

| Mode | Purpose | Network effect | Product boundary |
|---|---|---|---|
| **H1 — exact active identity** | Resolve one documented identity gap | One compiled request to one approved target | Governed entirely by the [network-execution contract](../architecture/NETWORK-EXECUTION.md) |
| **H2 — live passive** | Observe mirrored Ethernet evidence | Receive only from an approved SPAN/TAP path | Dedicated Android Capture Broker and confined native capture daemon; no claim of visibility from an ordinary access port |
| **H3 — offline import** | Analyze customer- or lab-supplied PCAP/PCAPNG | No packets transmitted | Preserve source, hash, collection context and visibility limits |
| **H4 — approved radio observation** | Record bounded Wi-Fi/BLE presence evidence | Android high-level scan APIs only | No Wi-Fi association/deauthentication, monitor-mode claim, BLE connection or GATT operation in P0 |

P0 defaults to the least intrusive mode that can answer the documented evidence question. The product must state when the selected visibility is insufficient.

## 5. Assessment workflow

The normative method is [ASSESSMENT-METHOD.md](ASSESSMENT-METHOD.md). At product level the workflow is:

1. **Prepare** — establish authorization, scope, exclusions, data handling and stop conditions.
2. **Collect** — acquire physical, imported, passive or explicitly approved active evidence.
3. **Review** — preserve raw observations and review them before changing accepted inventory.
4. **Reconcile** — make missing, unexpected, conflicting and probable matches explicit.
5. **Assess** — run deterministic water-pack checks only where required evidence exists.
6. **Finalize** — independent review, limitation check, immutable snapshot and signed export.

## 6. Initial active operation

The initial P0 active identity operation is **Modbus/TCP Read Device Identification, function `0x2B` / MEI `0x0E`, basic objects only**, to one exact authorized target.

No subnet scan, port scan, unit-ID sweep, register read/write, credential operation or generic socket command is part of the P0 product contract. Any future active protocol operation requires a separately approved architecture/profile and release evidence before it enters this contract.

## 7. Water assessment checks

| Check | Reportable condition |
|---|---|
| WAT-ID-001 | In-scope imported asset lacks adequate observation or physical confirmation |
| WAT-ID-002 | Observed OT endpoint has no accepted inventory match |
| WAT-ID-003 | Material model/serial/firmware identity conflict exists across sources |
| WAT-ID-004 | Asset record exceeds the customer's accepted freshness threshold |
| WAT-NET-001 | Modbus/TCP cleartext communication is observed and relevant to scope |
| WAT-NET-002 | OPC UA evidence shows a SecurityPolicy None option; actual client use remains a separate conclusion |
| WAT-NET-003 | A management service/protocol is observed contrary to a declared policy |
| WAT-ARC-001 | Reviewer-confirmed communication conflicts with the declared zone/conduit model |
| WAT-WIR-001 | An approved radio observation associated with the area has no accepted inventory record |
| WAT-LCM-001 | Exact model/version matches a dated authoritative end-of-support source |
| WAT-EVD-001 | Capture loss, duration or visibility is insufficient for the requested conclusion |

The evidence, confidence, consequence and finding-quality rules are defined once in [ASSESSMENT-METHOD.md](ASSESSMENT-METHOD.md).

## 8. Professional deliverable

A finalized P0 case must provide:

- exact authorization, scope, exclusions, methods and limitations;
- reconciled asset state and unresolved exceptions;
- reviewed findings linked to evidence;
- communication/architecture evidence relevant to accepted findings;
- active-operation ledger;
- pack/tool/build identities needed for reproducibility;
- artifact/hash manifest and audit-chain head;
- deterministic machine-readable assessment data;
- human-readable report output;
- external signature/hash verification instructions.

Raw captures are included only when retention/export authorization permits.

## 9. Definition of done

P0-WATER is complete only when:

1. the requirements applicable to P0 have automated or witnessed verification;
2. the complete assessment method can be executed offline on the supported appliance;
3. every report fact can be traced to an artifact/byte range, physical record or explicitly identified assessor context;
4. external packet evidence proves active execution stayed inside the approved network contract;
5. live passive hardware satisfies the compatibility and zero-egress acceptance gates;
6. parser, mobile, privacy and supply-chain release gates pass with no blocking finding;
7. an independent OT reviewer accepts the methodology and report controls;
8. deterministic export and external verification pass;
9. limitations state what was not observed or tested without implying certification or complete coverage;
10. the independent full rehearsal in [TEST-AND-ACCEPTANCE.md](TEST-AND-ACCEPTANCE.md) passes.

## 10. Explicitly outside P0

- broad address/port/service discovery;
- credential testing or authenticated enumeration;
- register reads/writes or process-control actions;
- exploitation or vulnerability proof;
- active Siemens S7/PROFINET, EtherNet/IP/CIP, IEC-104, IEC 61850, DNP3, BACnet or SNMP operations;
- serial Modbus/RS-485;
- Wi-Fi monitor mode or deauthentication;
- BLE connection/GATT interaction;
- cloud synchronization;
- multi-case portfolio dashboards;
- customer CMMS/CMDB APIs;
- AI-generated actions or severity.

## 11. Related authorities

- [Requirements baseline](../REQUIREMENTS.md)
- [Assessment method](ASSESSMENT-METHOD.md)
- [Test and acceptance](TEST-AND-ACCEPTANCE.md)
- [Implementation backlog](IMPLEMENTATION-BACKLOG.md)
- [System and deployment architecture](../architecture/SYSTEM-AND-DEPLOYMENT.md)
- [Network execution](../architecture/NETWORK-EXECUTION.md)
- [Evidence data model](../architecture/EVIDENCE-DATA-MODEL.md)
- [Security and threat model](../architecture/SECURITY-AND-THREAT-MODEL.md)
- [Current implementation](../../IMPLEMENTATION.md)
- [Roadmap](../../ROADMAP.md)
