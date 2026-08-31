# Executable baseline

## Guided assessment UI

The Case App now implements a persistent five-stage assessment shell: Overview, Collect, Assets, Findings and Report. The dashboard recommends the next defensible action; passive observations require explicit selection before inventory changes; the inventory includes list and process-zone views; findings keep confidence separate from consequence; and report finalization remains blocked while required security controls are incomplete.

The open-source integration decisions, visual tokens and remaining engineering gates are specified in `docs/product/OPEN-SOURCE-AND-UX-IMPLEMENTATION.md`.

This repository now contains the first deployable safety slice for **P0-WATER**. It is deliberately narrower than the complete product architecture: it proves that Android can enforce authorization, constrain one OT operation, preserve evidence bytes, and keep general application code away from raw network sockets.

## What is implemented

| Boundary | Executable behavior | Verification |
|---|---|---|
| Case lifecycle | Draft → authorized → collecting/paused → reviewing → finalized with role gates and time window | JVM unit tests |
| Execution grants | P-256 ECDSA signature, 60-second maximum lifetime, one-time nonce, CIDR/exclusion checks, packet/byte/timeout/concurrency caps | JVM + device tests |
| Network privilege | `case-app` has no `INTERNET`; only `network-broker` has socket access | Manifest policy script + emulator tests |
| IPC | Exported broker service requires an app-signature permission; AIDL carries bounded grants and a file descriptor evidence sink | Emulator tests |
| Active OT identification | One bounded Modbus/TCP Read Device Identification request: FC `0x2B`, MEI `0x0E`, basic objects only | Codec unit tests |
| Interface binding | Broker opens the socket through the explicitly granted Android `Network` handle | Code path + build test |
| Replay journal | Consumed nonces are synchronously persisted before the socket job is accepted | Unit/static review boundary |
| Parser isolation | Parser service is non-exported and uses an Android isolated process | Manifest policy script + emulator tests |
| Emergency stop | Closes every active assessment socket; service destruction also cancels queued work | Code path |
| Passive import | Content-URI upload of bounded classic PCAP or PCAPNG; SHA-256, timing, protocol counts, endpoints, roles, confidence and framing evidence | Four sourced protocol captures + PCAPNG and Android UI tests |
| Dedicated-appliance contract | Separate signature-protected Passive Capture Broker; interface capability attestation; bounded time/byte request; PCAP stream over file descriptor; no Android Internet permission | Static boundary checks + API 29/35 live-stream emulation |
| Native live capture | `AF_PACKET` daemon bound to one interface; promiscuous receive; bounded mode-0600 PCAP; no packet-send calls | Native compile/static gate + virtual SPAN/veth capture and zero-TX assertion in CI |
| Passive protocol coverage | Modbus/TCP, DNP3, IEC-104, BACnet/IP, EtherNet/IP, S7comm, IEC 61850 MMS candidate, OPC UA and PROFINET framing | Parser tests; four protocols have sourced CI fixtures |
| Assessment UI | Three-step site setup, persistent five-stage orientation, one primary decision per screen, explicit passive/active choice, pre-broker scope validation and review-first inventory | API 29 + API 35 instrumentation |

## CI acceptance gate

Every push to `main` and every pull request must pass:

1. architecture invariant verification;
2. all JVM unit tests;
3. Android lint;
4. debug APK assembly for the Case App, Network Broker and Passive Capture Broker;
5. instrumentation tests on API 29 (minimum supported) and API 35 (target).
6. signed UI-to-network-to-result journeys against PyModbus, modbus-tk and Conpot.

GitHub Actions installs a pinned Gradle 8.13 runtime, so the repository does not depend on a generated wrapper binary. Emulator reports, lint results, unit XML, and debug APKs are retained as workflow artifacts.

## Deliberately deferred

This baseline is **not yet a professional water-sector assessment release**. Live capture now has an executable native daemon, Android service contract and emulated end-to-end journey, but custom-image integration and physical USB-Ethernet/SPAN/TAP qualification remain release gates. Room/SQLCipher evidence storage, BLE/Wi-Fi discovery, signed offline packs, evidence export, inventory connectors, findings/report generation, the large UNB benchmark, and the complete P0-WATER workflow remain implementation milestones. The current Modbus path must only be used on a network for which the operator has written authorization.

See `docs/architecture/DEDICATED-ANDROID-APPLIANCE.md` for the appliance boundary and hardware gate.

See `docs/architecture/IMPLEMENTABLE-ARCHITECTURE.md` and the P0-WATER specification for the target design and acceptance criteria.
