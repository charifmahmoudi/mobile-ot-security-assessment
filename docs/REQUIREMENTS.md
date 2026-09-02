# Product requirements baseline

This file is the stable normative requirements baseline. It states what the product **MUST/SHOULD/MAY** satisfy; it is not an implementation-status document. Current behavior is in [IMPLEMENTATION.md](../IMPLEMENTATION.md), planned gates are in [ROADMAP.md](../ROADMAP.md), and the first release is narrowed by the [P0-WATER product contract](poc/WATER-WASTEWATER-POC.md).

Exact architecture mechanics are owned by the [architecture section](architecture/README.md). Requirements reference outcomes and constraints rather than duplicating grant formats, packet templates or deployment internals.

## Professional case lifecycle

- **CASE-001:** Every professional assessment record MUST belong to exactly one case revision with a stable case lineage, legal entity, site, process area and assessment pack.
- **CASE-002:** Before authorization, a case MUST state the assessment question/requested decision, explicit collection boundary, evidence methods, data-handling policy and stop conditions.
- **CASE-003:** Professional actions MUST retain actor identity and the role under which the action was performed; assessor, operational approver, security approver and independent reviewer responsibilities MUST remain distinguishable even when one permitted person holds multiple roles.
- **CASE-004:** Authorization MUST be bound to an immutable authorization artifact, a validity window, the exact case scope and the exact data-handling policy, and MUST require both operational and security approval before protected collection.
- **CASE-005:** Material case lifecycle actions MUST append to a canonical integrity-verifiable audit chain; retained/exported history MUST detect sequence, previous-hash or event-content alteration.
- **CASE-006:** Customer-declared expected records, sealed evidence artifacts, parsed observations, identity claims, accepted/reconciled asset state and findings MUST remain distinct semantic layers; a later layer MUST NOT silently overwrite its source layer.
- **CASE-007:** Finalization MUST require independent reviewer acceptance and MUST freeze the authorization identity, scope/data-policy identities, audit-chain head, tool/content identities and professional case material used by the final output.
- **CASE-008:** A finalized revision MUST NOT resume collection or semantic mutation. A correction after finalization MUST create a new revision linked to the previous finalized snapshot rather than rewriting the prior revision.
- **CASE-009:** Active execution MUST be allowed only while the case is in an authorized collecting state and MUST remain within the case operation/target scope and exclusions in addition to the Network Broker grant policy.

## Authorization and safety

- **SAFE-001:** The system MUST default to passive mode.
- **SAFE-002:** It MUST prevent active actions without a recorded authorization, scope and time window.
- **SAFE-003:** Every active adapter MUST declare exact protocol operations, risk class, rate, concurrency, retry, timeout and stop behavior.
- **SAFE-004:** The product MUST NOT implement writes, control functions, exploits, fuzzing or credential attacks as field assessment actions.
- **SAFE-005:** An operator MUST be able to cancel all active actions locally without network access.
- **SAFE-006:** The system MUST record an immutable audit event and profile/version identity for every packet-producing action in the professional case model.

## P0 industry and assessment boundary

- **POC-001:** The first industry pack MUST be water/wastewater and MUST assess one pumping/treatment control segment per case.
- **POC-002:** A P0 case MUST be limited to 64 imported assets, 256 observed endpoints and 16 allowlisted active-identity targets.
- **POC-003:** The report MUST implement the method, evidence hierarchy, confidence and risk rules defined by the P0 assessment method.
- **POC-004:** The P0 release MUST distinguish H1 direct authorized identity, H2 SPAN/TAP passive capture, H3 offline capture import and H4 approved radio observation, and MUST label their different visibility and network effects.
- **POC-005:** The product MUST NOT claim penetration-test, compliance-certification, exploitability or complete-network coverage.
- **POC-006:** The complete P0 assessment MUST function offline and produce an externally verifiable finalized package.

## Collection

- **COL-001:** The system MUST enumerate the capabilities relevant to the selected Ethernet, passive-capture, Wi-Fi and Bluetooth interfaces.
- **COL-002:** It MUST distinguish local-origin traffic, broadcast/multicast, mirrored wired traffic, radio observations and imported-capture visibility.
- **COL-003:** It MUST ingest PCAP and PCAPNG offline with capture provenance and cryptographic hash.
- **COL-004:** It MUST bound capture by configured byte/time limits, seal partial artifacts safely and stop before the storage reserve is exhausted.
- **COL-005:** It MUST make visibility limitations explicit in the UI and report.

## Identity and inventory

- **ID-001:** An asset identity claim MUST retain source evidence, observation time, rule/pack version and confidence.
- **ID-002:** Conflicting claims MUST remain visible and reduce confidence.
- **ID-003:** OUI alone MUST NOT produce a model-level identity.
- **ID-004:** P0 MUST support passive metadata and Modbus/TCP function 43/14 basic device identification; any additional active profile, including OPC UA discovery, requires its own approved network-execution contract and release evidence. EtherNet/IP/CIP active identity is outside the initial P0 active scope.
- **ID-005:** Vendor/device knowledge MUST be delivered in signed, versioned packs with citations.

## Data, privacy and security

- **DATA-001:** Cases MUST function without a cloud account or internet connection.
- **DATA-002:** Sensitive local data MUST be encrypted at rest with hardware-backed keys where available.
- **DATA-003:** Reports MUST minimize payload and secret disclosure by default.
- **DATA-004:** Retention and deletion MUST be configurable per case.
- **DATA-005:** Imports MUST preserve source and mapping provenance.
- **DATA-006:** Exports MUST include structured machine-readable data; required final formats are defined by the P0 product contract.

## Platform

- **PLAT-001:** The first supported product platform MUST be Android ARM64.
- **PLAT-002:** Supported device/NIC/TAP combinations MUST appear in a tested compatibility matrix.
- **PLAT-003:** The assessment workflow MUST remain usable on the supported field appliance without internet access.
- **PLAT-004:** General-purpose application root MUST NOT be required; any privileged passive-capture function MUST be isolated behind the dedicated appliance boundary.

## Quality and supply chain

- **QUAL-001:** Every binary parser MUST have malformed-input and fuzz tests.
- **QUAL-002:** Every active profile MUST have golden-packet, negative-scope and cancellation tests.
- **QUAL-003:** Releases MUST include an SBOM and signed provenance.
- **QUAL-004:** Dependencies MUST be pinned, licensed, owned and monitored.
- **QUAL-005:** No production pilot build may ship before threat-model, privacy, legal and external security reviews pass with no blocking finding.
