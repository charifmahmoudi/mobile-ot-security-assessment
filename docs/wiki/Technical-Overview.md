# Technical Overview

This page gives a high-level model of Atlas for technical readers. Exact interfaces, privilege boundaries, protocol mechanics and security requirements remain in the versioned repository architecture.

## Component model

Atlas separates professional case work from packet-producing and packet-receiving privileges.

At a high level:

```text
Case App
  ├── professional case, evidence review, reconciliation, findings and report workflow
  ├── no general-purpose network authority
  │
  ├── signed, bounded active request
  │      ↓
  │   Network Broker
  │      ↓
  │   authorized OT target
  │
  └── passive capture request
         ↓
      Capture Broker
         ↓
      receive-only native capture path
```

Untrusted capture parsing is separated from the main user workflow so malformed evidence does not directly become accepted professional state.

## Professional case model

The case is the aggregate root for one authorized assessment engagement. It includes:

- customer/site/process context;
- assessment objective;
- scope, exclusions and evidence methods;
- data policy and stop conditions;
- authorization and role actions;
- audit trail;
- review/finalization state;
- revision/supersession lineage.

A site is context; it is not the professional case itself.

## Evidence model

Atlas separates:

```text
customer declaration
≠ raw artifact
≠ observation
≠ identity claim
≠ accepted reconciliation
≠ finding
```

This separation is central to the product's ability to explain uncertainty and preserve provenance.

## Active-network model

The initial P0 active capability is intentionally narrow: exact Modbus Device Identification to one explicitly authorized target. The Network Broker validates a signed grant and network scope before executing the compiled operation.

There is no generic scanner or arbitrary socket command in the P0 product contract.

For the exact operation, grant fields, time/budget limits and socket behavior, use [NETWORK-EXECUTION.md](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md).

## Passive model

Live passive collection uses a separate capture boundary and a receive-only native backend. Meaningful field visibility depends on a qualified SPAN/TAP path; the software cannot create switched-network visibility that the physical network does not deliver.

## Persistence and offline operation

The professional case foundation uses encrypted local persistence and Android Keystore-backed key protection. Current storage maturity and remaining field-readiness work are maintained only in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md).

## CI model

The project continuously exercises Android application/service boundaries on Android emulators and uses independent OT emulators/testbeds for active Modbus behavior. The pilot backlog extends this into a Golden Customer Assessment E2E journey that validates the customer-facing workflow, not only individual components.

Emulation proves software behavior; physical phone/NIC/SPAN/TAP support still requires measured qualification.

## Architecture authorities

Use these repository documents when exactness matters:

- [Architecture index](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/README.md)
- [System and deployment](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/SYSTEM-AND-DEPLOYMENT.md)
- [Professional case model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/PROFESSIONAL-CASE-MODEL.md)
- [Network execution](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md)
- [Evidence data model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/EVIDENCE-DATA-MODEL.md)
- [Security and threat model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/SECURITY-AND-THREAT-MODEL.md)
- [ADRs](https://github.com/charifmahmoudi/mobile-ot-security-assessment/tree/main/docs/adr)
- [Testing](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/testing/README.md)
