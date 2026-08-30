# Roadmap

The roadmap is now an engineering gate sequence for one product: the [P0-WATER professional assessment](docs/poc/WATER-WASTEWATER-POC.md). Cross-industry expansion is blocked until that assessment passes independent rehearsal.

| Gate | Deliverable | Exit evidence |
|---|---|---|
| M0 Build | Reproducible Kotlin/Rust Android build | Signed debug APK, SBOM, unit tests |
| M1 Offline case | Authorization, encrypted case, CSV/artifact import, audit and export | State, integrity and offline tests |
| M2 Passive analysis | PCAPNG parsing and water asset reconciliation | Golden corpus and fuzz baseline |
| M3 Field evidence | Physical photos, Wi-Fi and BLE observations | Permission/privacy tests |
| M4 Safe A1 | Modbus device ID and OPC UA discovery | Independent packet trace proves limits |
| M5 Live passive | SPAN/TAP capture accessory integration | 100 Mbps for 30 minutes with drops reported |
| M6 Professional report | Water rules, review, signed HTML/PDF/JSON/CSV package | Evidence traceability review |
| M7 Rehearsal | Independent four-hour lab assessment | All definition-of-done gates pass |

Detailed tickets, dependencies and staffing boundaries are in [Implementation Backlog](docs/poc/IMPLEMENTATION-BACKLOG.md).

## Release boundary

P0 ends with a professional assessment of one water/wastewater segment. It does not include cloud sync, enterprise dashboards, broad port scanning, credentials, exploits, PROFINET/S7 active discovery, EtherNet/IP active identity, serial buses or customer API connectors.

## After P0

Only measured gaps from the witnessed assessment may justify P1. Candidate work is:

1. customer CMMS/CMDB import/export connector;
2. second water protocol selected from observed evidence;
3. field-hardened capture accessory;
4. multi-case consolidation;
5. one new industry pack.

A new industry requires its own process model, asset taxonomy, evidence method, protocol threat review, golden corpus, assessment rules and independent acceptance rehearsal. It is not a configuration flag.
