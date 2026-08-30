# Secure development lifecycle

## Lifecycle

```mermaid
flowchart LR
  R[Research] --> D[Design]
  D --> L[Lab prototype]
  L --> F[Controlled field pilot]
  F --> P[Production]
  P --> M[Monitor and improve]
```

Each transition is a reviewed gate, not a calendar promise.

## Definition of done by phase

### Research

Claim ledger, target-account sample, protocol evidence, competitive assessment, applicable-law map, license inventory and explicit unknowns.

### Design

Threat model, misuse cases, privacy impact assessment, architecture decision records, data model, safety invariants, requirements traceability and test strategy.

### Lab prototype

No production scanning. Golden packets, malformed-input corpus, emulators/owned devices, query packet review, Android compatibility matrix, SBOM, static analysis, unit/integration tests and reproducible build.

### Controlled field pilot

Written authorization, named operator, safety observer, passive-first runbook, backups/escalation, scope allowlist, real-time stop control and post-run packet audit.

### Production

External security review, signed releases, vulnerability disclosure, support/SLA, telemetry opt-in only, data retention controls and rollback.

## Engineering controls

- protected main branch and pull-request reviews;
- conventional commits and decision records;
- requirements linked to tests;
- SAST, dependency, secret and license scans;
- fuzzing for every parser;
- golden-packet tests for every active profile;
- no network action without a policy-gate unit test;
- release artifacts signed with provenance and SBOM.

## Roadmap gates

| Gate | Exit evidence |
|---|---|
| R0 research baseline | reviewed corpus and claim ledger |
| D0 architecture | threat/privacy models and ADRs accepted |
| P0 passive prototype | imported/mirrored PCAP identifies lab corpus |
| A1 identity prototype | packet-perfect bounded queries in lab |
| M0 mobile field kit | compatibility matrix across selected Android/NIC/TAP hardware |
| F0 pilot-ready | legal/safety package and operator training |
