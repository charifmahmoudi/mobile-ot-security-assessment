# Architecture and security

This section owns Atlas OT Scout architecture contracts. Architecture describes the intended system boundaries; [IMPLEMENTATION.md](../../IMPLEMENTATION.md) is the only authority for how much of that design currently executes.

| Document | Authority |
|---|---|
| [System and deployment](SYSTEM-AND-DEPLOYMENT.md) | Packages/processes, privilege and trust/deployment topology |
| [Component contracts](COMPONENT-CONTRACTS.md) | Internal responsibilities and forbidden coupling |
| [Professional case model](PROFESSIONAL-CASE-MODEL.md) | Case aggregate, roles, objective, scope/authorization binding, lifecycle guards, audit chain, finalization and revision semantics |
| [Network execution](NETWORK-EXECUTION.md) | Active grant/signature mechanics, initial Modbus operation, passive broker/daemon behavior and stop semantics |
| [Evidence data model](EVIDENCE-DATA-MODEL.md) | Artifact → observation → claim → asset/finding → review → snapshot/report storage/lineage model |
| [Security and threat model](SECURITY-AND-THREAT-MODEL.md) | Security argument, threat register and residual risks |
| [Dedicated Android appliance](DEDICATED-ANDROID-APPLIANCE.md) | Live-passive privilege boundary and physical acceptance invariants |
| [Protocol and device catalog](PROTOCOL-AND-DEVICE-CATALOG.md) | Evidence-bounded protocol/device identity surfaces |

Historical decisions and supersession are recorded in the [ADR index](../adr/README.md). Product scope is defined by [P0-WATER](../poc/WATER-WASTEWATER-POC.md); verification criteria are defined by [TEST-AND-ACCEPTANCE.md](../poc/TEST-AND-ACCEPTANCE.md).

Do not copy exact grant algorithms, packet templates, deployment diagrams or current capability status into overview/test/commercial documents when a link to the relevant authority is sufficient.

Return to the [documentation index](../README.md).
