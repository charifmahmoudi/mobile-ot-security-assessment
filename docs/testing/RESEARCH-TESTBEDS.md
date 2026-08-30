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

## Active emulators

| Emulator | What it represents | Use in acceptance testing |
|---|---|---|
| [PyModbus](https://pymodbus.readthedocs.io/en/v3.11.3/) | Standards-oriented Modbus server/simulator | Positive FC 43/MEI 14 device-identity path with vendor/product/revision |
| [Conpot](https://github.com/mushorg/conpot) | Multi-protocol ICS honeypot (GPL-2.0) | Modbus service compatibility and unsupported-identification behavior; its official compose mapping exposes container port 5020 as host 502 |
| [OpenPLC Runtime](https://github.com/thiagoralves/OpenPLC_v3) | IEC 61131-3 soft PLC with Modbus/TCP | Candidate hardware-in-the-loop substitute after register-safe probes are approved |
| [Snap7](https://github.com/SCADACS/snap7) | Siemens S7 client/server library | Candidate for the S7 identification milestone; not exercised by the Modbus-only active slice |

An emulator passing is evidence of protocol interoperability, not evidence that every physical PLC behaves identically. Hardware validation remains a separate release gate.
