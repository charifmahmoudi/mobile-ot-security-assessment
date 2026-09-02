# Executable baseline

`IMPLEMENTATION.md` is the canonical statement of what this repository executes today. Design documents may describe the P0 target; [ROADMAP.md](ROADMAP.md) records planned gates. When another document conflicts with this file about current capability, this file wins until the implementation or this record is changed.

## Current software boundary

The repository contains three Android application boundaries plus a native passive-capture daemon:

| Component | Current executable role |
|---|---|
| Case App | Guided site workflow, passive import/review, inventory reasoning, findings/report-readiness UI; no Android `INTERNET` permission |
| Network Broker | Separately privileged active-network service for the compiled Modbus identity operation |
| Capture Broker | Separate passive-capture Binder/FD boundary; debug builds stream a labeled CI fixture and release builds fail closed until the native backend is integrated |
| Parser worker | Isolated-process parsing boundary for untrusted captures |
| `atlas_capture` | Native `AF_PACKET` receive-only daemon compiled and tested on Linux virtual Ethernet; not yet integrated and qualified on the target phone image |

The authoritative topology and privilege model are in [System and deployment](docs/architecture/SYSTEM-AND-DEPLOYMENT.md). Exact packet-producing and packet-receiving behavior is defined in [Network execution](docs/architecture/NETWORK-EXECUTION.md).

## Implemented behavior

| Area | Current behavior | Verification route |
|---|---|---|
| Site workflow | Three-step site creation and a persistent Overview → Collect → Assets → Findings → Report shell | Android instrumentation |
| Local prototype state | Site and working inventory state persist locally for the current PoC workflow | Android tests |
| Passive import | Bounded PCAP/PCAPNG import, hashing, metadata extraction, parsing and explicit observation review before inventory mutation | JVM + Android tests with sourced captures |
| Passive protocol parsing | Modbus/TCP, DNP3, IEC-104, BACnet/IP, EtherNet/IP, S7comm, IEC 61850 MMS candidate, OPC UA and PROFINET framing; sourced CI fixtures currently exercise a subset | Parser/unit/UI tests |
| Capture Broker boundary | Signature-protected Binder service exposes interface inspection, bounded start and stop, and streams bytes through a file descriptor | API 29/35 emulation |
| Native passive daemon | `AF_PACKET` receive on one interface, promiscuous membership, bounded classic PCAP output and no packet-send calls in the daemon source | Native compile, veth capture, symbol/syscall gate |
| Active operation | One Modbus/TCP Read Device Identification request, FC `0x2B` / MEI `0x0E`, basic objects only, to one authorized IPv4 target on TCP/502 | Codec, policy and end-to-end tests |
| Grant signing | Case App uses an Android Keystore EC key on `secp256r1`; grants are signed and verified with `SHA256withECDSA` | Domain tests + Android journey |
| Grant policy | Maximum 60-second grant lifetime; exact target must be inside an authorized CIDR and outside exclusions; unit ID 0–247; one or two packet budget, ≤512 response bytes, ≤1 retry, ≤1500 ms timeout, concurrency one | `GrantPolicy` tests |
| Replay state | Consumed nonces are persisted by the Network Broker in private `SharedPreferences` before an allowed execution proceeds | Broker/domain tests |
| Interface use | Active socket is explicitly bound with Android `Network.bindSocket` before connect | Network Broker code + device tests |
| Emergency stop | Network Broker closes active Modbus sockets; Capture Broker cancels its active capture worker | Code path + tests |
| Analyst boundary | Imported/passive observations require explicit selection before they affect the working inventory | UI tests |

## What CI proves

The Android safety workflow exercises builds, architecture checks, JVM tests, lint, API 29/35 instrumentation, passive capture imports, the Capture Broker file-descriptor journey, active Modbus behavior against PyModbus/modbus-tk/Conpot, and the native receive-only capture gate.

The exact verification topology and retained artifacts are documented in [Testing](docs/testing/README.md). CI proves those software paths; it does not qualify a physical OT appliance or production network.

## Not implemented or not yet qualified

The following are not current field-ready capabilities:

- integration of `atlas_capture` into the signed LineageOS appliance image with its final SELinux/init policy;
- physical phone, powered hub, USB-Ethernet and SPAN/TAP qualification;
- encrypted professional case database/artifact vault and production key lifecycle;
- durable multi-user authorization/reviewer workflow;
- deterministic signed HTML/PDF/JSON/CSV final assessment package;
- production inventory/CMMS/CMDB connectors;
- production Wi-Fi, BLE or serial collection packs;
- OPC UA active discovery;
- broad active discovery, subnet/port/unit-ID sweeps, register reads/writes, credentials, exploitation or control actions;
- complete P0-WATER independent rehearsal and production release qualification.

## Current-versus-target interpretation

- [Requirements](docs/REQUIREMENTS.md) state what the product must eventually satisfy.
- [P0-WATER](docs/poc/WATER-WASTEWATER-POC.md) defines the first product contract.
- [Architecture](docs/architecture/README.md) defines the intended design boundaries.
- [Roadmap](ROADMAP.md) records the remaining gate sequence.

Those documents must not be read as evidence that a target capability already executes.
