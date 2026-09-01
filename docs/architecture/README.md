# Architecture and security

This section is the authoritative architecture and security record for Atlas OT Scout. Current executable behavior is tracked in [IMPLEMENTATION.md](../../IMPLEMENTATION.md); architecture documents define the system boundary and target controls without overriding that implementation status.

| Document | Authority |
|---|---|
| [System and deployment](SYSTEM-AND-DEPLOYMENT.md) | Packages, processes, deployment topology and trust boundaries |
| [Component contracts](COMPONENT-CONTRACTS.md) | Interfaces, responsibilities and forbidden coupling |
| [Network execution](NETWORK-EXECUTION.md) | Signed grants, broker enforcement, exact operations and stop behavior |
| [Evidence data model](EVIDENCE-DATA-MODEL.md) | Artifact, observation, claim, asset, finding, review and report layers |
| [Security and threat model](SECURITY-AND-THREAT-MODEL.md) | Assets, adversaries, abuse cases, mitigations and residual risk |
| [Dedicated Android appliance](DEDICATED-ANDROID-APPLIANCE.md) | Target appliance architecture and physical qualification boundary |
| [Protocol and device catalog](PROTOCOL-AND-DEVICE-CATALOG.md) | Evidence-bounded protocol identity surfaces and permitted prototype actions |

Accepted and proposed design decisions are registered in the [ADR index](../adr/README.md). Product acceptance criteria are defined in the [P0-WATER specification](../poc/WATER-WASTEWATER-POC.md), and automated proof boundaries are described in the [testing index](../testing/README.md).

Return to the [documentation index](../README.md).
