# Reproducible OT testbeds

## Passive corpus used in CI

The executable test corpus is downloaded at test time from the [Idaho National Laboratory/Idaho State University ICS Security Tools collection](https://github.com/ITI/ICS-Security-Tools/tree/master/pcaps). That repository describes the captures as a community asset for testing protocol analyzers and publishes the repository under CC-BY-4.0. The files are not copied into this repository; URLs and SHA-256 digests are pinned by `tools/fetch_research_pcaps.sh`.

| Protocol | Upstream fixture | CI assertion |
|---|---|---|
| Modbus/TCP | `modbus_test_data_part1.pcap` | MBAP validation and Modbus endpoint attribution |
| DNP3 | `dnp3_test_data_part1.pcap` | TCP/20000 plus DNP3 `0x0564` framing |
| IEC 60870-5-104 | `TestDissectIec104.pcap` | TCP/2404 plus APDU `0x68` framing |
| BACnet/IP | `bacnet_test.pcap` | UDP/47808 plus BVLC `0x81` framing |

The [UNB CIC Modbus Dataset 2023](https://www.unb.ca/cic/datasets/modbus-2023.html) is the scale benchmark: its maintainers describe PCAPs from a simulated Docker substation with IED and HMI containers, benign and attack traffic, and explicit redistribution/citation terms. It is unsuitable for every commit because of its size; it belongs in scheduled benchmark runs after the streaming/PCAPNG parser milestone.

The mobile parser now accepts classic PCAP and PCAPNG. CI converts the pinned Modbus fixture into PCAPNG, verifies that attribution and the uploaded-file digest are preserved, and verifies that a truncated block fails closed. The four upstream files remain the per-commit interoperability corpus; they are small protocol fixtures, not a substitute for the larger UNB benchmark or a real-site acceptance capture.

## Active emulators

| Emulator | What it represents | Use in acceptance testing |
|---|---|---|
| [PyModbus](https://pymodbus.readthedocs.io/en/v3.11.3/) | Standards-oriented Modbus server/simulator | Positive FC 43/MEI 14 device-identity path with vendor/product/revision |
| [modbus-tk](https://github.com/ljean/modbus-tk) | Independent Modbus slave simulator | Valid Modbus exception to the read-device-identification request; the app must report service confirmation without inventing vendor/model |
| [Conpot](https://github.com/mushorg/conpot) | Multi-protocol ICS honeypot (GPL-2.0) | Modbus service compatibility and unsupported-identification behavior; its official compose mapping exposes container port 5020 as host 502 |
| [OpenPLC Runtime](https://github.com/thiagoralves/OpenPLC_v3) | IEC 61131-3 soft PLC with Modbus/TCP | Candidate hardware-in-the-loop substitute after register-safe probes are approved |
| [Snap7](https://github.com/SCADACS/snap7) | Siemens S7 client/server library | Candidate for the S7 identification milestone; not exercised by the Modbus-only active slice |

An emulator passing is evidence of protocol interoperability, not evidence that every physical PLC behaves identically. Hardware validation remains a separate release gate.

## Golden Customer Assessment endpoint

`testdata/golden-customer-assessment/` is the immutable pilot integration fixture. `tools/golden_ot_emulator.py` exposes only the Modbus basic device-identification response named by that case. The API 29/35 harness waits for both the Android emulator and OT endpoint to become ready before installing the real applications, and retains the endpoint readiness/request log even though issue #6 does not require the case-persistence scenario to transmit a packet. The independent active E2E matrix proves the Android-to-OT network path.

The same fixture also carries the customer-declared inventory seed, deterministic passive observations and expected reconciliation/report blockers. Later pilot tests extend these inputs; they must not replace them with developer-local state or an external mutable service.

## Exact end-to-end boundary

The live CI journey is `authorization UI → Android Keystore signature → Binder → broker policy → Android Network binding → TCP/502 → emulator → raw response pipe → identity/service classification → result UI`. It does not bypass the application with a host-side scanner. Passive CI is `content URI → bounded parser → protocol/asset evidence → analyst review UI` and performs no network operation.
