# Atlas product vision

This document defines the product direction from the first adoptable release to the complete Atlas platform. It complements normative requirements and does not claim future capabilities are implemented. Current behavior remains authoritative in [`IMPLEMENTATION.md`](../../IMPLEMENTATION.md); release sequencing remains in [`ROADMAP.md`](../../ROADMAP.md).

## North star

Atlas is a trusted, offline-first OT discovery and assessment instrument that practitioners can install, use repeatedly, extend and reproduce across bounded industrial environments.

It helps an authorized practitioner answer: **Does the evidence available for this process area support the asset baseline and operational decision the team is relying on, and which discrepancies require action?**

Atlas is not a penetration-testing framework, unrestricted scanner, autonomous network administrator, control-system management console or cloud-only monitoring service.

## First adoptable product

The first release is a complete workflow for one bounded water/wastewater process area:

1. create a case and define authorization, scope, exclusions and decision;
2. import an expected asset baseline;
3. import PCAP/PCAPNG and supporting evidence;
4. passively identify endpoints, protocols, services and relationships;
5. reconcile expected and observed state;
6. show confirmed, probable, conflict, unexpected, not-observed and unresolved results;
7. propose bounded follow-up methods;
8. execute only the currently approved active identity operation when justified;
9. review findings and limitations;
10. export a signed, machine-readable and human-readable package;
11. verify the package independently.

The first useful experience must work without special hardware, cloud services, ML or a capability marketplace. Live capture and active follow-up extend the workflow; they are not prerequisites for adoption.

## Complete product

The complete product adds four connected layers:

### Field connection and capture

The phone becomes a qualified field appliance using a supported USB Ethernet kit, physical TAP or preconfigured SPAN port. Later, allowlisted switch adapters may verify or configure mirror sessions through vendor-specific, previewable, authorized workflows. There is no generic switch configuration mode.

### Governed discovery and scanning

Passive discovery is the default. Active discovery is a set of signed capabilities with explicit target sets, protocol operations, packet/time/byte limits, stop conditions, evidence outputs and approval roles. A deviation produces a scan proposal; it does not trigger an unreviewed scan.

### Capability ecosystem

Parsers, protocol adapters, device catalogs, topology rules, importers and ML models are installable capabilities. Each declares inputs, outputs, permissions, resource limits, license, provenance, version, safety profile and test corpus. Capability output creates observations or claims; it never silently creates accepted inventory.

### Practitioner workspace

The UI shows partial visibility honestly: gray unknowns, confidence-aware observations, conflicts, inferred topology, approximate physical ports, real-time activity and swipe-based asset navigation. Every visual object links to evidence and retains its review state.

## Capability families

Potential families include Modbus, S7comm/S7comm-Plus, DNP3, IEC-104, BACnet/IP, EtherNet/IP/CIP, OPC UA, PROFINET, IEC 61850, EtherCAT, MQTT/Sparkplug and infrastructure protocols such as ARP, DHCP, DNS, NTP, LLDP and CDP. Priority is earned by adopter evidence, not protocol count.

ML may assist device classification, anomaly prioritization, duplicate matching, topology inference and evidence-quality assessment. Models must expose provenance, confidence, calibration, out-of-distribution behavior and human review; they must not establish authoritative identity or vulnerability conclusions by themselves.

## Evidence and trust model

Atlas preserves these layers:

```text
customer baseline → raw artifact → observation → inference/claim → analyst decision → finding → finalized package
```

Evidence is immutable and hashed. Derived or redacted artifacts link to their parent. Not observed is not absent. Inferred topology is not confirmed topology. Active execution is attributable to authorization, capability version, target, operation and result.

## Adoption gates

- **A0 — Externally reproducible:** a fresh user builds or obtains Atlas, runs the sample and verifies the result.
- **A1 — Practically useful:** an independent practitioner completes a bounded assessment and finds the result useful and understandable.
- **A2 — Repeatably adoptable:** multiple practitioners repeat the workflow and feedback produces product improvements.
- **A3 — Community extensible:** external contributors add fixtures or capabilities from the documentation, with review and provenance.

Expansion capabilities should not become critical-path work before the core workflow passes the relevant gate.

## Roadmap interpretation

```text
trusted core instrument → repeatable adoption → governed capabilities → intelligence and visualization → community ecosystem
```

The vision is broad; the first release is narrow enough to prove the core value without making the architecture disposable.
