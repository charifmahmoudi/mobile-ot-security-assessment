# Atlas OT Scout

**A guided, offline assessment appliance for authorized OT asset assessment in water and wastewater environments.**

[![Android safety CI](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/android-ci.yml)
[![Documentation](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/documentation.yml/badge.svg?branch=main)](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/documentation.yml)

Atlas OT Scout helps an authorized assessor move from an uncertain asset picture to a defensible operating baseline. It guides the user to establish scope, collect the least intrusive useful evidence, review observations before changing inventory, reconcile expected and observed assets, close a specific identity gap only when authorized, and hand off findings with explicit limitations.

The first product pack is **P0-WATER**, limited to one drinking-water or wastewater control segment. It is not a general-purpose scanner, exploitation framework, certification audit, or continuous-monitoring platform.

> **Status:** executable research prototype. The guided Android workflow, passive PCAP/PCAPNG analysis, emulated receive-only capture path, and one-target Modbus/TCP identity operation are exercised in CI. Physical appliance, USB-Ethernet, SPAN/TAP, encrypted case storage, signed report export, and production release qualification remain open gates.

## Commercial model

Atlas is offered as a try-before-you-buy appliance:

1. We deliver and configure a device for one agreed, bounded segment.
2. We guide the first assessment and leave the appliance with the customer for a free pilot.
3. The customer uses it and judges the value in its own environment.
4. If it is useful, the customer acquires it and keeps the capability on site.
5. If it is not useful, we collect it.
6. Consulting and support packages are available when specialist help is needed.

## Decision supported

The product is designed to answer a narrow field question: **is the available evidence strong enough to support an inventory, risk, remediation, or handover decision for this exact process area?**

| Starting condition | Guided product action | Customer outcome |
|---|---|---|
| The spreadsheet inventory may be stale | Compare imported records with passive and approved identity evidence | A current baseline with corroborated, missing, unexpected and conflicting assets |
| The phone cannot observe a switched segment | Explain the approved capture options and use a receive-only SPAN/TAP path | Useful visibility without broad discovery |
| One controller remains unidentified | Authorize one exact Modbus device-identification request | A specific uncertainty is resolved without widening the scope |
| Evidence is incomplete | Preserve gaps, approvals and reviewer blockers | A defensible next-action list instead of a false final report |

## Prospect assets

| Asset | Purpose |
|---|---|
| [Guided customer-story video](docs/demo/atlas-ot-scout-emulator-demo.mp4) | Real Android footage with explanatory captions, click indicators, pacing and the commercial model |
| [Editable prospect deck](docs/pitch/Atlas-OT-Scout-Pitch-and-Demo.pptx) | Fourteen-slide current-state → guided-action → desired-state and free-pilot narrative |
| [PDF prospect deck](docs/pitch/Atlas-OT-Scout-Pitch-and-Demo.pdf) | Portable presentation export |
| [Video story and provenance](docs/demo/VIDEO-SCRIPT.md) | Exact storyline, reproducible composition and proof boundary |

These assets describe emulator and CI evidence accurately; they do not claim physical hardware or production-network qualification.

## Executable baseline

| Capability | Current behavior | Evidence |
|---|---|---|
| Site and scope setup | Three-step onboarding followed by Overview, Collect, Assets, Findings, and Report stages | API 29 and API 35 instrumentation |
| Passive file analysis | Bounded PCAP/PCAPNG import, hashing, parsing, review, and inventory reconciliation | Modbus/TCP, DNP3, IEC-104, and BACnet fixtures |
| Receive-only capture boundary | Signature-protected broker streams bounded native `AF_PACKET` capture over a file descriptor | Virtual SPAN test and zero-transmission assertion |
| Active identity | One signed, short-lived, non-replayable Modbus FC 43 / MEI 14 request to one authorized target | PyModbus, Modbus-TK, and Conpot journeys |
| Evidence reasoning | Provenance, confidence, conflicts, findings, and report-readiness blockers remain visible | Unit and guided-stage tests |

Detailed implemented-versus-deferred status is maintained in [IMPLEMENTATION.md](IMPLEMENTATION.md).

## Safety architecture

- The Case App does not request Android `INTERNET` permission.
- Network access is isolated in a signature-protected broker with a compiled, bounded operation.
- Active execution requires a signed one-use grant containing the exact target, scope, interface, expiry, and resource ceilings.
- Untrusted capture parsing runs in an isolated process.
- Passive capture is receive-only and bound to an allowlisted interface.
- Observations do not become accepted assets or findings without analyst review.
- Writes, control actions, exploitation, fuzzing, credential attacks, and broad discovery are outside P0.

See the [technical architecture](docs/wiki/Technical-Architecture.md), [security and threat model](docs/architecture/SECURITY-AND-THREAT-MODEL.md), and [network execution contract](docs/architecture/NETWORK-EXECUTION.md).

## Current limits

The prototype is not yet a professional field release. In particular:

- no qualified physical appliance/USB-Ethernet/TAP combination;
- no production case vault, reviewer signature, or deterministic final report package;
- no subnet, port, unit-ID, credential, or vulnerability sweep;
- no register read/write or control operation;
- no production Wi-Fi, BLE, or serial collection pack;
- no claim of complete visibility, exploitability, or compliance certification.

## Build and verify

Prerequisites: JDK 17, Android SDK, Python 3, and Gradle 8.13.

```bash
python3 tools/verify_documentation.py
python3 tools/verify_architecture.py
bash tools/fetch_research_pcaps.sh
bash tools/test_capture_daemon.sh

gradle --no-daemon \
  :core-domain:test \
  :case-app:testDebugUnitTest \
  :network-broker:testDebugUnitTest \
  :capture-broker:testDebugUnitTest \
  lintDebug assembleDebug
```

Device and emulator acceptance paths are defined in [.github/workflows/android-ci.yml](.github/workflows/android-ci.yml).
