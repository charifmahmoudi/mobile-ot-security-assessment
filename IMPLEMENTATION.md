# Executable baseline

`IMPLEMENTATION.md` is the canonical statement of what the repository executes today. Planned capability belongs in [ROADMAP.md](ROADMAP.md) and the [P0 implementation backlog](docs/poc/IMPLEMENTATION-BACKLOG.md).

## Guided assessment UI

The Case App implements a persistent five-stage assessment shell: Overview, Collect, Assets, Findings, and Report. The dashboard recommends the next defensible action; passive observations require explicit selection before inventory changes; the inventory includes list and process-zone views; findings keep confidence separate from consequence; and report finalization remains blocked while required controls are incomplete.

The open-source integration decisions, visual tokens, and remaining interaction gates are specified in [Open-source and UX implementation](docs/product/OPEN-SOURCE-AND-UX-IMPLEMENTATION.md).

This repository contains an executable safety slice for **P0-WATER**. It is narrower than the target product: it proves that Android can enforce authorization, constrain one OT operation, preserve evidence bytes, and keep general application code away from raw network sockets.

## Implemented behavior

| Boundary | Executable behavior | Verification |
|---|---|---|
| Case lifecycle | Draft → authorized → collecting/paused → reviewing → finalized with role gates and time window | JVM unit tests |
| Execution grants | P-256 ECDSA signature, 60-second maximum lifetime, one-time nonce, CIDR/exclusion checks, and resource caps | JVM and device tests |
| Network privilege | `case-app` has no `INTERNET`; only `network-broker` has socket access | Manifest policy script and emulator tests |
| IPC | Exported broker service requires app-signature permission; AIDL carries bounded grants and an evidence file descriptor | Emulator tests |
| Active OT identification | One bounded Modbus/TCP Read Device Identification request: FC `0x2B`, MEI `0x0E`, basic objects only | Codec and end-to-end tests |
| Interface binding | Broker opens the socket through the explicitly granted Android `Network` handle | Code path and build test |
| Replay journal | Consumed nonces are synchronously persisted before a socket job is accepted | Unit and static checks |
| Parser isolation | Parser service is non-exported and runs in an Android isolated process | Manifest policy script and emulator tests |
| Emergency stop | Closes active assessment sockets; service destruction cancels queued work | Code path |
| Passive import | Bounded PCAP/PCAPNG import with SHA-256, timing, protocol, endpoint, role, confidence, and framing evidence | Sourced captures, PCAPNG, and UI tests |
| Passive capture contract | Signature-protected broker, interface attestation, bounded time/byte request, and PCAP stream over a file descriptor | Static checks and API 29/35 emulation |
| Native live capture | `AF_PACKET` daemon bound to one interface; bounded mode-0600 PCAP; no packet-send calls | Native compile, virtual SPAN capture, and zero-TX assertion |
| Passive protocol parsing | Modbus/TCP, DNP3, IEC-104, BACnet/IP, EtherNet/IP, S7comm, IEC 61850 MMS candidate, OPC UA, and PROFINET framing | Parser tests; four protocols have sourced CI fixtures |
| Assessment UI | Site setup, persistent stages, passive/active choice, pre-broker validation, and review-first inventory | API 29 and API 35 instrumentation |

## CI acceptance gate

Every push to `main` and every pull request runs:

1. architecture invariant verification;
2. documentation structure and local-reference verification;
3. JVM unit tests;
4. Android lint;
5. debug APK assembly for the Case App, Network Broker, and Capture Broker;
6. instrumentation tests on API 29 and API 35;
7. signed UI-to-network-to-result journeys against PyModbus, Modbus-TK, and Conpot;
8. native receive-only capture testing over a virtual SPAN link.

The workflow installs a pinned Gradle 8.13 runtime. Reports, logs, screenshots, test XML, and debug APKs are retained as workflow artifacts.

## Deferred release gates

This baseline is **not yet a professional water-sector assessment release**. The following remain incomplete:

- physical phone, USB-Ethernet, SPAN/TAP, and custom-image qualification;
- production encrypted case storage and key lifecycle;
- reviewer signatures and deterministic HTML/PDF/JSON/CSV report packaging;
- full evidence export and inventory connectors;
- production Wi-Fi, BLE, and serial collection;
- OPC UA active discovery after independent lab approval;
- complete P0-WATER assessment rehearsal and external review.

The current Modbus path must only be used on a network covered by written authorization.

## Canonical design references

- [P0-WATER specification](docs/poc/WATER-WASTEWATER-POC.md)
- [System and deployment](docs/architecture/SYSTEM-AND-DEPLOYMENT.md)
- [Network execution](docs/architecture/NETWORK-EXECUTION.md)
- [Dedicated Android appliance](docs/architecture/DEDICATED-ANDROID-APPLIANCE.md)
- [Security and threat model](docs/architecture/SECURITY-AND-THREAT-MODEL.md)
- [End-to-end acceptance](docs/testing/E2E-ACCEPTANCE.md)
