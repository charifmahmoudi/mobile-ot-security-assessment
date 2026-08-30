# Product requirements baseline

Requirements use MUST/SHOULD/MAY. IDs remain stable and link to tests. The first build is governed by the [P0-WATER specification](poc/WATER-WASTEWATER-POC.md); its `POC-*` requirements are normative and narrow this generic baseline.

## Authorization and safety

- **SAFE-001:** The system MUST default to passive mode.
- **SAFE-002:** It MUST prevent active actions without a recorded authorization, scope and time window.
- **SAFE-003:** Every active adapter MUST declare exact protocol operations, risk class, rate, concurrency, retry, timeout and stop behavior.
- **SAFE-004:** The prototype MUST NOT implement writes, control functions, exploits, fuzzing or credential attacks.
- **SAFE-005:** An operator MUST be able to cancel all actions locally without network access.
- **SAFE-006:** The system MUST record an immutable audit event and profile hash for every packet-producing action.

## P0 industry and assessment boundary

- **POC-001:** The first industry pack MUST be water/wastewater and MUST assess one pumping/treatment control segment per case.
- **POC-002:** A case MUST be limited to 64 imported assets, 256 observed endpoints and 16 allowlisted A1 targets.
- **POC-003:** The report MUST implement the method, checks, evidence hierarchy, confidence and risk model in the P0 specification.
- **POC-004:** The build MUST support H1 direct Ethernet, H2 SPAN/TAP capture, H3 offline import and H4 Wi-Fi/BLE observation while labeling their different visibility.
- **POC-005:** The product MUST NOT claim penetration-test, compliance-certification, exploitability or complete-network coverage.
- **POC-006:** The full assessment MUST function offline and produce a signed, externally verifiable package.

## Collection

- **COL-001:** The system MUST enumerate selected USB Ethernet, Wi-Fi and Bluetooth interface capabilities.
- **COL-002:** It MUST distinguish local-origin, broadcast/multicast, mirrored-wired, raw-Wi-Fi and imported-capture visibility.
- **COL-003:** It MUST ingest PCAP and PCAPNG offline with capture provenance and cryptographic hash.
- **COL-004:** It MUST rotate captures at the configured byte/time boundary, seal partial artifacts safely and stop before the storage reserve is exhausted.
- **COL-005:** It MUST make visibility limitations explicit in the UI and report.

## Identity and inventory

- **ID-001:** An asset identity claim MUST retain source evidence, observation time, rule/pack version and confidence.
- **ID-002:** Conflicting claims MUST remain visible and reduce confidence.
- **ID-003:** OUI alone MUST NOT produce a model-level identity.
- **ID-004:** P0 MUST support passive metadata, Modbus/TCP function 43/14 basic device identification and OPC UA FindServers/GetEndpoints after lab approval. EtherNet/IP/CIP is deferred.
- **ID-005:** Vendor/device knowledge MUST be delivered in signed, versioned packs with citations.

## Data, privacy and security

- **DATA-001:** Cases MUST function without a cloud account or internet connection.
- **DATA-002:** Sensitive local data MUST be encrypted at rest with hardware-backed keys where available.
- **DATA-003:** Reports MUST minimize payload and secret disclosure by default.
- **DATA-004:** Retention and deletion MUST be configurable per case.
- **DATA-005:** Imports MUST preserve source and mapping provenance.
- **DATA-006:** Exports MUST include CSV and JSON; additional formats require a documented schema mapping.

## Platform

- **PLAT-001:** The first supported platform MUST be Android ARM64.
- **PLAT-002:** Supported device/NIC/TAP combinations MUST appear in a tested compatibility matrix.
- **PLAT-003:** The app MUST remain usable on a representative mid-range device without network access.
- **PLAT-004:** Root access MUST NOT be required for the supported production configuration.

## Quality and supply chain

- **QUAL-001:** Every binary parser MUST have malformed-input and fuzz tests.
- **QUAL-002:** Every active profile MUST have golden-packet and cancellation tests.
- **QUAL-003:** Releases MUST include an SBOM and signed provenance.
- **QUAL-004:** Dependencies MUST be pinned, licensed, owned and monitored.
- **QUAL-005:** No production pilot build may ship before threat-model, privacy, legal and external security reviews pass.
