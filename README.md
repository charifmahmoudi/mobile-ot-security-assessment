# Atlas OT Scout

**An offline-first Android field instrument for authorized OT asset assessment in water and wastewater environments.**

[![Android safety CI](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/android-ci.yml)
[![Documentation](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/documentation.yml/badge.svg?branch=main)](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/documentation.yml)

Atlas OT Scout helps an authorized assessor establish a site and scope, collect the least intrusive evidence, reconcile an asset inventory, draft evidence-linked findings, and identify what is still missing before a controlled handoff.

The first product pack is **P0-WATER**, limited to one drinking-water or wastewater control segment. It is not a general-purpose scanner, exploitation framework, certification audit, or continuous-monitoring platform.

> **Status:** executable research prototype. The guided Android workflow, passive PCAP/PCAPNG analysis, emulated receive-only capture path, and one-target Modbus/TCP identity operation are exercised in CI. Physical phone, USB-Ethernet, SPAN/TAP, encrypted case storage, signed report export, and production release qualification remain open gates.

## Decision supported

The product is designed to answer a narrow field question: **is the available evidence strong enough to support an inventory, risk, or handover decision for this exact process area?**

| Starting condition | Product action | Reviewable outcome |
|---|---|---|
| The spreadsheet inventory may be stale | Compare imported records with passive and approved identity evidence | Corroborated, missing, unexpected, or conflicting assets |
| The phone cannot observe a switched segment | Import a capture or use an approved receive-only SPAN/TAP path | Protocol and endpoint observations with visibility limits attached |
| One controller remains unidentified | Authorize one exact Modbus device-identification request | Confirmed identity or conservative service-only evidence |
| Evidence is incomplete | Preserve gaps, approvals, and reviewer blockers | A precise completion checklist instead of a false final report |

## Demonstration assets

| Asset | Purpose |
|---|---|
| [Live emulator demonstration](docs/demo/atlas-ot-scout-emulator-demo.mp4) | Continuous Android API 35 capture of the running application |
| [Editable buyer deck](docs/pitch/Atlas-OT-Scout-Pitch-and-Demo.pptx) | Ten-slide product, safeguards, proof, and pilot narrative |
| [PDF buyer deck](docs/pitch/Atlas-OT-Scout-Pitch-and-Demo.pdf) | Portable presentation export |
| [Presenter and recording guide](docs/demo/VIDEO-SCRIPT.md) | Talk track, provenance statement, and reproducible capture workflow |

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

- no qualified Samsung/USB-Ethernet/TAP combination;
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

## Documentation

Start with the [documentation index](docs/README.md). The primary documents are:

| Need | Canonical document |
|---|---|
| Product boundary and acceptance criteria | [P0-WATER specification](docs/poc/WATER-WASTEWATER-POC.md) |
| Current implementation status | [Executable baseline](IMPLEMENTATION.md) |
| Planned engineering gates | [Roadmap](ROADMAP.md) |
| Operator workflow | [User manual](docs/user-guide/USER-MANUAL.md) |
| Architecture and trust boundaries | [Technical architecture](docs/wiki/Technical-Architecture.md) |
| Test evidence and limitations | [End-to-end acceptance](docs/testing/E2E-ACCEPTANCE.md) |
| Morocco water-sector account research | [Account intelligence](docs/accounts/README.md) |
| Evidence-only business diligence | [Diligence index](docs/diligence/README.md) |

## Responsible use and license

Use active mode only on systems covered by explicit written authorization. Do not broaden a target, CIDR, unit ID, collection method, or time window to make a test pass.

No project license has been selected. Until a license and contribution terms are added, copyright remains with the repository owner and reuse is not granted.
