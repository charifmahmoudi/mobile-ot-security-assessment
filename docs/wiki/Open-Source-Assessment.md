# Open-source technology assessment

## Recommendation

Use open source as reviewed components, not as a bundled “Kali on a phone.” Build the safety workflow and evidence model as first-class product code.

| Component | Purpose | License | Mobile fit | Decision |
|---|---|---|---|---|
| libpcap | packet capture/filter format | BSD-style | native build possible; capture permissions vary | evaluate |
| PCAPdroid | Android VPN-based app-traffic capture patterns | GPL-3.0 | good Android reference; does not equal third-party LAN capture | study; do not embed without license decision |
| libmodbus | Modbus RTU/TCP client | LGPL-2.1+ | portable C/NDK | lab adapter candidate |
| open62541 | OPC UA | MPL-2.0 | portable C, commercially usable with obligations | strong candidate |
| libplctag | Allen-Bradley and Modbus | MPL-2.0 or LGPL-2+ | portable C/ARM | evaluate identity subset |
| pycomm3 | EtherNet/IP/Allen-Bradley | MIT | Python runtime is awkward in native Android | reference/test oracle |
| Wireshark dissectors | protocol parsing | GPL-2+ | rich but large/copyleft integration implications | offline lab/oracle, not default embed |
| Zeek + ICSNPP | passive OT metadata | permissive/GPL mix by package | server-oriented | backend/reference |
| Malcolm | integrated passive analysis | mixed component licenses | appliance/server footprint | lab corpus and interoperability |
| Nmap | discovery engine | NPSL | licensing and OT-safety profile require care | external lab baseline; no embed decision |
| PentAGI | AI pentest orchestration | MIT repository, dependent-tool licenses vary | container/server assumptions | reuse concepts only in OT-safe planner |
| SQLite/SQLCipher | local evidence store/encryption | public domain/BSD-style variants | mature Android fit | strong candidate |

Licenses must be verified at a pinned commit before inclusion; this table is not legal advice.

## PentAGI position

PentAGI can inform case orchestration, task approval, tool adapters, evidence capture and reporting. It should **not** control production OT scanning. An OT product needs a deterministic policy engine in front of every action:

- signed query profile;
- destination allowlist;
- protocol function allowlist;
- packet/rate/timeout budget;
- precondition and stop rule;
- human approval;
- immutable audit event.

An LLM may propose a plan or summarize evidence, but cannot directly issue packets.

## Android and capture reality

- USB host mode can support class-compliant Ethernet adapters, but OEM/kernel driver coverage must be tested.
- An unrooted app can inspect traffic it originates and can use Android's VPN APIs for device-app traffic.
- Seeing **other devices' wired traffic** requires network visibility: a TAP, switch SPAN/mirror, hub-like accessory, or a purpose-built capture accessory. Merely attaching USB Ethernet does not make the phone a transparent sniffer.
- Wi-Fi monitor mode and raw 802.11 capture are generally unavailable to ordinary Android apps and vary by chipset/firmware.
- BLE advertising and GATT access are feasible with Android permissions, but active connection must be scoped.

## Build/buy rule

Adopt a library only after: license review, maintained-release check, malformed-input fuzzing, timeout/cancellation test, packet golden tests, ARM64 build, SBOM entry and an owner. Otherwise implement the minimum identity parser internally from the public standard.
