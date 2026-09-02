# P0-WATER test and acceptance plan

This file owns the **P0 verification levels, acceptance conditions and independent rehearsal**. It tests the product/architecture contracts; it does not redefine them. Current test coverage is reported in [IMPLEMENTATION.md](../../IMPLEMENTATION.md) and CI topology in [E2E-ACCEPTANCE.md](../testing/E2E-ACCEPTANCE.md).

## Test levels

| Level | Environment | Purpose |
|---|---|---|
| T0 | JVM/native unit and fuzz tests | Domain policy, parsers, canonicalization and deterministic rules |
| T1 | Android emulator | UI, state, IPC and error paths without physical-hardware claims |
| T2 | Physical Android compatibility bench | Image, USB, storage, Keystore, radio permissions and lifecycle |
| T3 | Isolated water lab | Packet safety, passive capture, identity and full report workflow |
| T4 | Witnessed rehearsal | Independent assessor completes the P0 method unaided |

Production networks are not parser/profile development environments.

## Authorization and state

Verify at minimum:

- missing/invalid authorization prevents protected collection;
- execution outside the authorized window is refused;
- exact target outside scope or inside exclusions is rejected;
- finalized cases cannot silently resume collection/editing;
- restart does not automatically resume network activity;
- stop authority remains locally usable.

## Packet-safety verification

An independent recorder observes packet-producing behavior.

For the initial Modbus active operation, verify:

- emitted request matches the canonical [network-execution contract](../architecture/NETWORK-EXECUTION.md);
- out-of-scope, excluded, expired, replayed or malformed grants produce no unauthorized request;
- no subnet, port or unit-ID sweep path exists;
- no register read/write or other undeclared Modbus function is emitted;
- socket binding uses the selected Android network;
- emergency stop terminates active work inside the accepted stop-time gate.

If another active protocol is later admitted into P0, it receives its own positive, negative, cancellation and full-packet acceptance fixtures before release.

## Live passive verification

For the dedicated Android passive path, verify:

- Capture Broker exposes only the bounded interface/start/stop contract;
- native daemon receives frames from the allowlisted interface and produces valid bounded capture output;
- no packet-send syscall/path is observed from the daemon;
- physical OT-facing interface has the required no-address/no-egress posture;
- SPAN/TAP visibility is independently validated;
- packet drops, duration and capture source are measured/reported;
- detach, low storage and stop events preserve a usable partial evidence record where permitted.

Virtual receive-only proof is not sufficient for field qualification; physical phone/NIC/hub/TAP testing is mandatory.

## Import and parser security

Test PCAP/PCAPNG import with known-answer, malformed, truncated, length-boundary, unsupported-link and large-file cases.

Every binary parser must have:

- deterministic known-answer corpus;
- truncation/mutation coverage;
- length/allocation boundaries;
- fuzz harness and sanitizer/host safety gates where applicable;
- flow/reassembly resource ceilings;
- safe failure without partial accepted-inventory mutation.

## Reconciliation

Golden cases cover exact match, IP reuse, duplicate MAC, conflicting serial/model, renamed asset, weak OUI/hostname-only evidence and one-to-many candidates.

Acceptance requires that weak identifiers alone cannot silently produce a strong accepted identity and that material conflicts remain reviewable.

## Water rules and reporting

Each WAT rule in the [P0 contract](WATER-WASTEWATER-POC.md) receives positive, negative, insufficient-evidence and boundary fixtures.

From the same finalized snapshot, deterministic report data must reproduce the same normative findings/metrics except for explicitly non-semantic generated metadata.

Final package tests verify:

- evidence traceability;
- limitation wording;
- review state;
- artifact/manifest hashes;
- signature/external verification;
- omission of raw captures when export policy excludes them.

## Security and privacy

Acceptance includes:

- Case App offline/no-unapproved-telemetry checks;
- protected professional case data at rest;
- backup/export policy tests;
- pack/content signature and rollback behavior;
- evidence/database tamper detection;
- photo/metadata minimization;
- lost/locked-device behavior appropriate to the supported appliance.

Security mechanics are evaluated against [SECURITY-AND-THREAT-MODEL.md](../architecture/SECURITY-AND-THREAT-MODEL.md), not independently redefined here.

## Performance datasets

Reference performance testing should cover at least:

- large clean capture;
- malformed-heavy capture;
- maximum P0 imported-asset/observed-endpoint set;
- sustained passive capture at the target field load.

Measure import/parse duration, memory, UI responsiveness, storage growth, report duration, battery/thermal behavior and capture drop rate. Exact pass thresholds are release criteria and should be changed here once measured/approved rather than copied into multiple documents.

## Compatibility gate

The compatibility matrix must contain enough independent phone/NIC/hub/TAP combinations to demonstrate that support is tied to measured hardware identities rather than an assumed Android capability.

Record exact device/build, USB VID:PID where applicable, driver/kernel, link behavior, capture visibility, packet loss, detach recovery, power/thermal behavior and zero-egress evidence. Unsupported combinations are blocked or clearly represented as unsupported.

Canonical measured records: [COMPATIBILITY-MATRIX.md](../appliance/COMPATIBILITY-MATRIX.md).

## Full independent rehearsal

The witness receives only the approved authorization, seed inventory/site diagram, supported Atlas kit and user documentation.

They must complete the P0 method without developer intervention. Pass requires:

- no prohibited network action;
- expected inventory conflicts/unexpected items are detected and reviewed;
- no false strong identity from insufficient evidence;
- every report fact is traceable;
- visibility and other limitations are accurate;
- finalized package verifies externally;
- the workflow can be completed within the approved field window.

## Release evidence

The release record includes applicable automated test results, packet traces, parser/fuzz summaries, hardware compatibility evidence, SBOM/provenance, threat-model review, open defects and independent OT/security review. Blocking defects prevent field release.
