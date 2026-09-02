# Safety & Technical Boundaries

Atlas is designed for authorized, bounded OT assessment work. The product favors the least intrusive evidence source capable of answering the assessment question.

## Evidence methods

| Method | Network effect | Use |
|---|---|---|
| **Offline PCAP/PCAPNG analysis** | No packet transmission | Analyze approved existing evidence |
| **Passive SPAN/TAP capture** | Receive-only | Observe traffic delivered by an approved mirror/tap path |
| **Exact Modbus Device Identification** | One bounded active identity operation | Resolve a specific authorized identity gap |

## Active collection boundary

The P0-WATER active workflow is not a general scanner.

The initial active operation is limited to exact Modbus Device Identification for one explicitly authorized target. The workflow does not provide subnet sweeps, port sweeps, unit-ID sweeps, register read/write operations, credential testing or exploitation.

If the target, scope or authorization is invalid, the operation must not transmit.

Exact packet, grant, timing and socket behavior is defined in [`NETWORK-EXECUTION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md).

## Passive visibility boundary

Passive collection can only analyze traffic delivered to the capture interface.

A SPAN/TAP path may provide useful evidence, but it does not automatically provide complete segment visibility. Collection duration, mirror configuration, topology and packet loss can all limit what the assessment can conclude.

Therefore:

- **not observed** is not equivalent to **absent**;
- missing traffic is not proof that communication does not exist;
- visibility limitations belong in the final result.

## Evidence and review

Atlas keeps customer declaration, raw evidence, observations, identity claims, reconciliation decisions and findings separate.

A network observation does not automatically become an accepted asset fact. Material conclusions should be traceable to evidence and subject to professional review.

## Offline operation and data handling

Atlas is designed for local assessment workflows that do not require a cloud service. Customer evidence, retention and export must still follow the authorization and data-handling terms of the engagement.

Current implementation and storage maturity are maintained in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md).

## Engineering references

Use the repository when exactness matters:

- [P0-WATER product contract](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/WATER-WASTEWATER-POC.md)
- [Assessment method](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/ASSESSMENT-METHOD.md)
- [System architecture](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/SYSTEM-AND-DEPLOYMENT.md)
- [Network execution](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md)
- [Evidence data model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/EVIDENCE-DATA-MODEL.md)
- [Security and threat model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/SECURITY-AND-THREAT-MODEL.md)
- [Testing](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/testing/README.md)