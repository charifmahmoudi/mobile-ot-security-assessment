# P0-WATER Test and Acceptance Plan

## Test levels

| Level | Environment | Purpose |
|---|---|---|
| T0 | JVM/Rust unit tests | Domain rules, canonicalization, parsers, scoring |
| T1 | Android emulator | UI/state/error paths without hardware claims |
| T2 | Physical Android compatibility bench | USB, storage, Keystore, Wi-Fi/BLE, lifecycle |
| T3 | Isolated water lab | Packet safety, capture, identity and full report |
| T4 | Witnessed rehearsal | Independent assessor follows the procedure unaided |

Production networks are never used for parser or profile development.

## Mandatory test matrix

### Authorization and state

| Test | Expected result |
|---|---|
| Missing authorization artifact | Case cannot become Authorized |
| Clock outside window | Active grant refused |
| Target outside CIDR/allowlist | Grant refused before socket creation |
| Excluded IP selected | Grant refused |
| Case finalized | All collection and edits refused |
| App restart during Authorized state | Scope revalidated; no action resumes automatically |

### Packet safety

A separate recorder observes every frame emitted by the phone.

| Test | Expected result |
|---|---|
| Modbus basic device ID | Exactly one approved request; at most one retry after timeout |
| Modbus unit-ID sweep attempt | Impossible through UI/API; policy rejection audited |
| OPC UA discovery | Only HEL/ACK, FindServers, GetEndpoints and close sequence |
| Route changes to Wi-Fi/cellular | Socket closes; no packet on alternate interface |
| Emergency stop | All probe sockets closed within 1 second |
| Authorization expiry mid-request | Execution stops and records `authorization_expired` |
| Profile signature modified | Pack/profile rejected |
| Concurrent second request | Rejected while concurrency ceiling is one |

Golden packet tests compare full request bytes except declared transaction/message IDs.

### Capture

| Test | Expected result |
|---|---|
| 100 Mbps SPAN stream, 30 min | Capture completes; measured drops reported; artifacts rotate/seal |
| USB detach | Partial artifact sealed and case paused |
| 2 GiB import | Hash, parse and UI progress complete without ANR |
| Low storage | Capture stops before reserve threshold; no prior artifact overwritten |
| PCAPNG multiple interfaces | Interface identity preserved |
| Unsupported link type | Artifact retained and limitation reported |

### Parser security

Each Rust parser runs:

- known-answer corpus;
- truncation at every byte offset;
- length-field boundary tests;
- 10,000 deterministic mutation cases;
- continuous libFuzzer/AFL-compatible fuzz target in CI;
- AddressSanitizer/UndefinedBehaviorSanitizer on host;
- memory and flow-table limit tests;
- duplicate/out-of-order TCP segment tests.

Release gate: zero reproducible crash, panic across FFI, out-of-bounds access or unbounded allocation.

### Reconciliation

Golden cases cover exact matches, IP reuse, duplicate MAC, conflicting serial, renamed asset, unsupported OUI-only match and one-to-many candidate. Expected merge/split decisions and confidence are versioned. No automatic match based only on IP, hostname or OUI may be accepted.

### Rule engine

Each WAT rule has positive, negative, insufficient-evidence and boundary fixtures. Running twice over the same case snapshot must produce byte-equivalent normative JSON after excluding documented generated timestamps.

### Security/privacy

| Test | Expected result |
|---|---|
| Device locked | Case key unavailable until authenticated |
| Copy app files from non-rooted device backup | Backup unavailable/disabled |
| Network observation during offline workflow | Zero unapproved DNS/HTTP/telemetry |
| Export without capture permission | Raw captures omitted; manifest states omission |
| Photo EXIF policy off | GPS removed |
| Invalid pack rollback | Older pack refused |
| Database/file tamper | Integrity/manifest check fails visibly |

## Performance targets

Reference datasets:

- D1: 1 GiB, 10 million packets, 100 endpoints;
- D2: 2 GiB, malformed-heavy corpus;
- D3: 64-row inventory, 256 endpoints, 100,000 normalized observations.

Measure on each supported phone:

- import and parse duration;
- peak RSS;
- UI frame/ANR status;
- database size;
- report duration;
- battery/thermal state;
- capture drop rate.

Pass: no ANR, parser RSS ≤256 MiB, application RSS target ≤768 MiB, and report generation ≤120 seconds for D3.

## Compatibility matrix gate

Before P0 completion test at least:

- two Android phone models from different OEMs;
- Android API 29 and one current supported release;
- two USB Ethernet NIC models;
- one powered hub;
- one capture-accessory path;
- Wi-Fi and BLE permissions across tested API levels.

Record VID:PID, firmware, kernel/build, driver, link speeds, DHCP/static-IP behavior, socket binding and detach recovery. Unsupported combinations are blocked in field mode, not merely documented.

## Full assessment rehearsal

The witness receives only:

- signed authorization;
- seed inventory;
- site/lab diagram;
- Android kit;
- user documentation.

They must complete prepare, walkdown, passive capture, optional A1, reconciliation, findings, review and signed export. Pass conditions:

- zero prohibited packets;
- all golden unexpected/conflict conditions found;
- no false confirmed model identity;
- every report fact traceable;
- report limitations accurate;
- independent reviewer can validate package hashes;
- total operator time ≤4 hours excluding capture window;
- no developer intervention.

## Release evidence

The release record contains test results, packet traces, fuzz summaries, compatibility matrix, SBOM, signed provenance, threat-model approval, open defects and OT reviewer sign-off. Any open Critical/High defect blocks a field build.
