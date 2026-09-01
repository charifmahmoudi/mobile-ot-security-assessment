# Atlas OT Scout

**A guided, offline assessment appliance for authorized OT asset assessment in water and wastewater environments.**

[![Android safety CI](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/android-ci.yml/badge.svg?branch=main)](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/android-ci.yml)
[![Documentation](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/documentation.yml/badge.svg?branch=main)](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/workflows/documentation.yml)

Atlas guides an authorized assessor from an uncertain asset picture to a defensible operating baseline. The workflow establishes scope, collects the least intrusive useful evidence, requires review before inventory changes, reconciles expected and observed assets, permits one exact identity check only when authorized, and preserves limitations at handoff.

The first product pack is **P0-WATER**, limited to one drinking-water or wastewater control segment. It is not a general-purpose scanner, exploitation framework, certification audit or continuous-monitoring platform.

> **Status:** executable research prototype. The guided Android workflow, passive PCAP/PCAPNG analysis, emulated receive-only capture path and one-target Modbus/TCP identity operation are exercised in CI. Physical appliance qualification, USB-Ethernet and SPAN/TAP validation, encrypted production storage, reviewer signatures, deterministic final export and production release qualification remain open gates.

## Navigate

| Need | Authoritative route |
|---|---|
| Navigate the complete documentation | [Documentation index](docs/README.md) |
| See what executes today | [Implementation status](IMPLEMENTATION.md) |
| Understand the first product pack | [P0-WATER specification](docs/poc/WATER-WASTEWATER-POC.md) |
| Operate or demonstrate the application | [User guide](docs/user-guide/README.md) |
| Review architecture and safety boundaries | [Architecture index](docs/architecture/README.md) |
| Review verification evidence | [Testing index](docs/testing/README.md) |
| Present the commercial offer | [Prospect pitch](docs/pitch/README.md) and [guided demo](docs/demo/README.md) |
| Research Moroccan customers and stakeholders | [Business development](docs/business-development/README.md) |

## Customer decision supported

Atlas is designed to answer one field question: **is the available evidence strong enough to support an inventory, risk, remediation or handover decision for this exact process area?**

| Starting condition | Guided action | Customer outcome |
|---|---|---|
| The spreadsheet inventory may be stale | Compare imported records with passive and approved identity evidence | A baseline with corroborated, missing, unexpected and conflicting assets |
| The phone cannot observe a switched segment | Explain approved capture options and use a receive-only SPAN/TAP path | Useful visibility without broad discovery |
| One controller remains unidentified | Authorize one exact Modbus device-identification request | A specific uncertainty is resolved without widening scope |
| Evidence is incomplete | Preserve gaps, approvals and reviewer blockers | A defensible next-action list instead of a false final report |

## Commercial model

Atlas is offered as a try-before-you-buy appliance:

1. Atlas and the customer agree one bounded segment and decision.
2. Atlas delivers and configures the device, then guides the first assessment.
3. The appliance remains with the customer for a free pilot.
4. The customer acquires it if useful; Atlas retrieves it if not.
5. Consulting and support remain optional.

## Executable baseline

| Capability | Current behavior | Evidence route |
|---|---|---|
| Guided case workflow | Onboarding followed by Overview, Collect, Assets, Findings and Report stages | [Testing index](docs/testing/README.md) |
| Passive analysis | Bounded PCAP/PCAPNG import, hashing, parsing, review and reconciliation | [Implementation status](IMPLEMENTATION.md) |
| Receive-only capture boundary | Signature-protected broker streams bounded native `AF_PACKET` capture over a file descriptor | [Architecture](docs/architecture/README.md) |
| Active identity | One signed, short-lived, non-replayable Modbus FC 43 / MEI 14 request to one authorized target | [Network execution](docs/architecture/NETWORK-EXECUTION.md) |
| Evidence reasoning | Provenance, confidence, conflicts, findings and readiness blockers remain visible | [Evidence data model](docs/architecture/EVIDENCE-DATA-MODEL.md) |

Detailed implemented-versus-deferred status is maintained only in [IMPLEMENTATION.md](IMPLEMENTATION.md). Planned work belongs in [ROADMAP.md](ROADMAP.md).

## Safety boundary

- The Case App does not request Android `INTERNET` permission.
- Network access is isolated in a signature-protected broker with compiled, bounded operations.
- Active execution requires a signed one-use grant containing the exact target, scope, interface, expiry and resource ceilings.
- Untrusted capture parsing runs in an isolated process.
- Passive capture is receive-only and bound to an allowlisted interface.
- Observations do not become accepted assets or findings without analyst review.
- Writes, control actions, exploitation, fuzzing, credential attacks and broad discovery are outside P0.

See the [architecture index](docs/architecture/README.md), [security and threat model](docs/architecture/SECURITY-AND-THREAT-MODEL.md) and [network execution contract](docs/architecture/NETWORK-EXECUTION.md).

## Current limits

The prototype is not yet a professional field release. In particular:

- no qualified physical appliance, USB-Ethernet adapter or TAP combination;
- no production case vault, reviewer signature or deterministic final report package;
- no subnet, port, unit-ID, credential or vulnerability sweep;
- no register read/write or control operation;
- no production Wi-Fi, BLE or serial collection pack;
- no claim of complete visibility, exploitability or compliance certification.

## Prospect assets

| Asset | Purpose |
|---|---|
| [Guided customer-story video](docs/demo/atlas-ot-scout-emulator-demo.mp4) | Android emulator footage with explanatory captions and the commercial model |
| [Editable prospect deck](docs/pitch/Atlas-OT-Scout-Pitch-and-Demo.pptx) | Current-state → guided-action → desired-state narrative |
| [PDF prospect deck](docs/pitch/Atlas-OT-Scout-Pitch-and-Demo.pdf) | Portable presentation export |
| [Video story and provenance](docs/demo/VIDEO-SCRIPT.md) | Reproducible composition, storyline and proof boundary |

These assets describe emulator and CI evidence accurately; they do not claim physical-hardware or production-network qualification.

## Build and verify

Prerequisites: JDK 17, Android SDK, Python 3 and Gradle 8.13.

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

## Repository policy

- [Requirements baseline](docs/REQUIREMENTS.md)
- [Roadmap](ROADMAP.md)
- [Contributing](CONTRIBUTING.md)
- [Governance](GOVERNANCE.md)
- [Security policy](SECURITY.md)
