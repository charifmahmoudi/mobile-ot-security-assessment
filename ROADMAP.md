# Roadmap

`ROADMAP.md` is the canonical milestone-status record for P0-WATER. Current executable behavior is maintained in [IMPLEMENTATION.md](IMPLEMENTATION.md). Detailed work items live in the [P0 implementation backlog](docs/poc/IMPLEMENTATION-BACKLOG.md), which references these milestone IDs rather than maintaining a second status table.

| Gate | Status | Product outcome | Exit evidence |
|---|---|---|---|
| M0 Safety slice | Complete | Reproducible Android/JVM/native build with separated active/passive boundaries | Builds, architecture checks and emulated end-to-end journeys |
| M1 Professional offline case | Planned | Authorization, encrypted case vault, audit chain and controlled export | State, integrity, key-lifecycle and offline tests |
| M2 Passive analysis | Partial | Production-grade PCAP/PCAPNG ingestion and water-asset reconciliation | Golden corpus, malformed-input/fuzz gates and reviewer workflow |
| M3 Field evidence | Planned | Physical observations plus approved Wi-Fi/BLE evidence | Permission, privacy and provenance tests |
| M4 Safe active identity | Partial | Qualified Modbus device identification plus separately approved next active profile | Independent packet traces and operation-budget proof |
| M5 Live passive | Partial | Dedicated Android Capture Broker + native daemon integrated with qualified SPAN/TAP hardware | Signed image integration, compatibility matrix, sustained capture/drop and zero-egress evidence |
| M6 Professional report | Planned | Reviewed findings and deterministic signed assessment package | Reproducibility and evidence-traceability review |
| M7 Rehearsal | Planned | Independent end-to-end P0-WATER assessment | Every P0 definition-of-done gate passes without developer intervention |

## P0 release boundary

P0 is complete only when one bounded water/wastewater assessment can be performed and independently reviewed under the [P0 product contract](docs/poc/WATER-WASTEWATER-POC.md). Broad scanning, credentials, exploitation, control operations, cloud synchronization and enterprise portfolio features are outside P0.

## After P0

Post-P0 work is driven by measured gaps from rehearsals or customer evaluations, not by protocol breadth alone. Candidate categories include:

- customer CMMS/CMDB integration;
- additional protocol packs justified by observed evidence;
- hardened production appliance hardware;
- multi-case consolidation;
- separately governed industry packs.

A new industry requires its own process model, evidence method, protocol threat review, test corpus, assessment rules and independent acceptance evidence.
