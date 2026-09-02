# Executable baseline

`IMPLEMENTATION.md` is the canonical statement of what this repository executes today. Design documents may describe the P0 target; [ROADMAP.md](ROADMAP.md) records planned gates. When another document conflicts with this file about current capability, this file wins until the implementation or this record is changed.

## Current software boundary

The repository contains a pure domain layer, three Android application boundaries plus a native passive-capture daemon:

| Component | Current executable role |
|---|---|
| Core domain | Professional case lifecycle/authorization/audit/snapshot invariants, deterministic bounded case persistence codec, evidence-record contracts, grant/scope policy and deterministic parsing/business rules; no Android/storage dependency |
| Case App | Guided site workflow plus the encrypted professional-case repository adapter; passive import/review, inventory reasoning and findings/report-readiness UI; no Android `INTERNET` permission |
| Network Broker | Separately privileged active-network service for the compiled Modbus identity operation |
| Capture Broker | Separate passive-capture Binder/FD boundary; debug builds stream a labeled CI fixture and release builds fail closed until the native backend is integrated |
| Parser worker | Isolated-process parsing boundary for untrusted captures |
| `atlas_capture` | Native `AF_PACKET` receive-only daemon compiled and tested on Linux virtual Ethernet; not yet integrated and qualified on the target phone image |

The authoritative topology and privilege model are in [System and deployment](docs/architecture/SYSTEM-AND-DEPLOYMENT.md). Exact packet-producing and packet-receiving behavior is defined in [Network execution](docs/architecture/NETWORK-EXECUTION.md). The professional case lifecycle is defined in [Professional case model](docs/architecture/PROFESSIONAL-CASE-MODEL.md).

## Implemented behavior

| Area | Current behavior | Verification route |
|---|---|---|
| Professional case domain | Typed case/actor/authorization/snapshot IDs; legal/site/process context; decision-oriented assessment objective; scope, evidence methods, stop conditions and data policy; guarded `DRAFT → ... → FINALIZED/SUPERSEDED` lifecycle; role-gated transitions; authorization bound to exact scope/data-policy fingerprints; reviewer acceptance gate; finalized snapshot hash; revision/supersession semantics | `core-domain` unit tests |
| Professional audit chain | Append-only SHA-256 chained lifecycle events with actor role, object identity, details hash and previous-event hash; restoration rejects sequence/hash-chain tampering | `core-domain` unit tests |
| Professional case persistence codec | Versioned deterministic binary aggregate representation with explicit size/string/collection bounds; exact `Instant` precision; complete audit/snapshot state; envelope digest; restore rejects malformed encodings and domain/audit/snapshot inconsistencies before returning the aggregate | `core-domain` persistence tests |
| Encrypted professional case repository | Case App has a SQLCipher aggregate-checkpoint repository. A random 256-bit database key is wrapped by an Android Keystore AES-GCM key; row metadata is cross-checked against the restored aggregate; writes use optimistic expected-version checks; SQLCipher page integrity and SQLite logical integrity checks are exposed | API 29/35 instrumentation + JVM codec tests |
| Professional evidence record contracts | Separate typed records for sealed artifacts, expected records, observations, identity claims, reconciliation decisions, findings and object-review decisions; provenance/evidence/confidence/consequence invariants enforced in pure domain code | `core-domain` unit tests |
| Site workflow | Three-step site creation and a persistent Overview → Collect → Assets → Findings → Report shell | Android instrumentation |
| Legacy sample UI state | Site and working inventory state for the existing PoC screens still persist in private `SharedPreferences`; this store is not used as the professional-case repository | Android tests |
| Passive import | Bounded PCAP/PCAPNG import, hashing, metadata extraction, parsing and explicit observation review before inventory mutation | JVM + Android tests with sourced captures |
| Passive protocol parsing | Modbus/TCP, DNP3, IEC-104, BACnet/IP, EtherNet/IP, S7comm, IEC 61850 MMS candidate, OPC UA and PROFINET framing; sourced CI fixtures currently exercise a subset | Parser/unit/UI tests |
| Capture Broker boundary | Signature-protected Binder service exposes interface inspection, bounded start and stop, and streams bytes through a file descriptor | API 29/35 emulation |
| Native passive daemon | `AF_PACKET` receive on one interface, promiscuous membership, bounded classic PCAP output and no packet-send calls in the daemon source | Native compile, veth capture, symbol/syscall gate |
| Active operation | One Modbus/TCP Read Device Identification request, FC `0x2B` / MEI `0x0E`, basic objects only, to one authorized IPv4 target on TCP/502 | Codec, policy and end-to-end tests |
| Grant signing | Case App uses an Android Keystore EC key on `secp256r1`; grants are signed and verified with `SHA256withECDSA` | Domain tests + Android journey |
| Grant policy | Maximum 60-second grant lifetime; exact target must be inside an authorized CIDR and outside exclusions; unit ID 0–247; one or two packet budget, ≤512 response bytes, ≤1 retry, ≤1500 ms timeout, concurrency one | `GrantPolicy` tests |
| Replay state | Consumed nonces are persisted by the Network Broker in private `SharedPreferences` before an allowed execution proceeds | Broker/domain tests |
| Interface use | Active socket is explicitly bound with Android `Network.bindSocket` before connect | Network Broker code + device tests |
| Emergency stop | Network Broker closes active Modbus sockets; Capture Broker cancels its active capture worker | Code path + tests |
| Analyst boundary | Imported/passive observations require explicit selection before they affect the working inventory | UI tests |

## What CI proves

The Android safety workflow exercises builds, architecture checks, JVM tests, lint, API 29/35 instrumentation, passive capture imports, the Capture Broker file-descriptor journey, active Modbus behavior against PyModbus/modbus-tk/Conpot, and the native receive-only capture gate. `core-domain` tests additionally exercise the professional case state machine, authorization binding, role gates, audit-chain verification, deterministic persistence/restore validation, finalization/supersession and evidence-record invariants. Case App instrumentation exercises encrypted professional-case creation/load/update, stale-write rejection, database integrity checks and the encrypted on-disk boundary.

The exact verification topology and retained artifacts are documented in [Testing](docs/testing/README.md). CI proves those software paths; it does not qualify a physical OT appliance or production network.

## Not implemented or not yet qualified

The following are not current field-ready capabilities:

- the normalized SQLCipher evidence/decision schema, content-addressed encrypted artifact vault, per-case artifact-key lifecycle, retention/secure-deletion workflow and production migration/recovery tooling; the current encrypted repository is an aggregate checkpoint foundation rather than the completed evidence store;
- Case App integration of the full role-aware professional lifecycle, including user-facing durable operational/security approvals, assessment-objective editing, reviewer decisions and revision workflow;
- production lock timeout/re-authentication policy and recovery procedures for unavailable/replaced Android Keystore keys;
- integration of `atlas_capture` into the signed LineageOS appliance image with its final SELinux/init policy;
- physical phone, powered hub, USB-Ethernet and SPAN/TAP qualification;
- deterministic signed HTML/PDF/JSON/CSV final assessment package and external verification tooling; the domain snapshot hash/finalization foundation now exists;
- production inventory/CMMS/CMDB connectors;
- production Wi-Fi, BLE or serial collection packs;
- OPC UA active discovery;
- broad active discovery, subnet/port/unit-ID sweeps, register reads/writes, credentials, exploitation or control actions;
- complete P0-WATER independent rehearsal and production release qualification.

## Current-versus-target interpretation

- [Requirements](docs/REQUIREMENTS.md) state what the product must eventually satisfy.
- [P0-WATER](docs/poc/WATER-WASTEWATER-POC.md) defines the first product contract.
- [Architecture](docs/architecture/README.md) defines the intended design boundaries.
- [Roadmap](ROADMAP.md) records the remaining gate sequence.

Those documents must not be read as evidence that a target capability already executes.
