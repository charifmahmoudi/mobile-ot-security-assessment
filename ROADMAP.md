# Roadmap

The roadmap is an engineering gate sequence for one product: the [P0-WATER professional assessment](docs/poc/WATER-WASTEWATER-POC.md). Cross-industry expansion remains blocked until that assessment passes an independent rehearsal.

`ROADMAP.md` describes planned work. Current executable behavior is maintained separately in [IMPLEMENTATION.md](IMPLEMENTATION.md).

| Gate | Status | Deliverable | Exit evidence |
|---|---|---|---|
| M0 Safety slice | Complete in CI | Reproducible Kotlin/JVM and native C Android build with isolated network and capture boundaries | Debug APKs, unit tests, architecture checks, and emulated end-to-end journeys |
| M1 Offline case | Planned | Authorization record, encrypted case vault, import, audit, and export | State, integrity, key-lifecycle, and offline tests |
| M2 Passive analysis | Partial | Production PCAP/PCAPNG ingestion and water-asset reconciliation | Golden corpus, malformed-input tests, fuzz baseline, and review workflow |
| M3 Field evidence | Planned | Physical photos plus approved Wi-Fi and BLE observations | Permission, privacy, and evidence-provenance tests |
| M4 Safe active identity | Partial | Modbus device ID plus lab-approved OPC UA discovery | Independent packet traces proving operation and budget limits |
| M5 Live passive | Emulated | Qualified SPAN/TAP capture accessory and appliance integration | Physical compatibility matrix and sustained capture with drops reported |
| M6 Professional report | Planned | Water rules, review, and signed HTML/PDF/JSON/CSV package | Deterministic build and evidence-traceability review |
| M7 Rehearsal | Planned | Independent four-hour lab assessment | Every P0 definition-of-done gate passes |

Detailed tickets, dependencies, and staffing boundaries are in the [implementation backlog](docs/poc/IMPLEMENTATION-BACKLOG.md).

## P0 release boundary

P0 ends with a professional assessment of one water or wastewater segment. It does not include cloud synchronization, enterprise dashboards, broad port scanning, credentials, exploits, PROFINET/S7 active discovery, EtherNet/IP active identity, serial buses, or customer API connectors.

## After P0

Only measured gaps from a witnessed assessment may justify P1. Candidate work includes:

1. a customer CMMS/CMDB import/export connector;
2. a second water protocol selected from observed evidence;
3. a field-hardened capture accessory;
4. multi-case consolidation;
5. one separately governed industry pack.

A new industry requires its own process model, asset taxonomy, evidence method, protocol threat review, golden corpus, assessment rules, and independent acceptance rehearsal. It is not a configuration flag.
