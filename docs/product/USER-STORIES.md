# Field-assessor user stories and acceptance criteria

## 1 — establish site context

**As an assessor, I need to choose an existing site or create a new one before collecting evidence, so observations cannot float without an operating context.**

- Existing sites show name, location/process area, industry, asset count and sample status.
- Creation is a three-step wizard: required site/process/industry, optional multi-vendor context, then settings review.
- Vendor choices are explicitly described as context, not discovery claims.
- The user can skip vendor context without losing required site data.
- The selected site remains visible through dashboard, collection and inventory screens.

## 2 — understand the current network picture

**As an assessor resuming work, I need an immediate assessment snapshot, so I can choose the next action based on gaps rather than run another indiscriminate scan.**

- Dashboard presents one context-aware primary action before technical summaries.
- Status separates known assets, represented protocols and records awaiting review.
- Evidence coverage summarizes role distribution and uncertainty without claiming completeness.
- Collection and inventory remain grouped secondary shortcuts.

## 3 — choose a safe collection method

**As an assessor, I need passive and active methods visibly separated, so I know whether an action transmits before entering technical details.**

- Passive import is the safe default and says no packets are sent.
- Active identity explains its exact bounded operation and authorization requirement.
- Planned Wi-Fi/Bluetooth packs are disabled and visually separate from working functions.

## 4 — analyze a capture

**As an assessor supplied with PCAP/PCAPNG, I need to analyze it offline and review proposed observations before they affect inventory.**

- Real content-URI import validates PCAP/PCAPNG bounds and framing.
- Results show filename/hash, window, packet counts, supported protocols, endpoints, inferred role and confidence.
- The app labels the capture a visibility sample, never a complete inventory.
- Malformed, truncated, oversized and unsupported inputs fail safely.
- Adding observations is a distinct analyst decision.

## 5 — perform constrained active identity

**As an authorized assessor, I need to identify one known Modbus controller without reading or changing process registers.**

- Work order, process area, exact IPv4, CIDR and unit ID remain visibly labeled.
- CIDR membership is checked before the broker is contacted.
- Authorization checkbox gates a one-use signed grant.
- Broker binds the Android network and enforces target, port, unit, time, packet, byte, retry and replay limits.
- Result distinguishes identity confirmation from service-only confirmation.

## 6 — investigate the asset inventory

**As an assessor, I need to navigate, filter and inspect the network model, so evidence leads to decisions rather than a flat list of addresses.**

- Search covers address, name, vendor, protocol and role.
- Filters cover review queue, controllers, HMI/clients, gateways and evidence provenance.
- Each record exposes identity, role, protocol, confidence, provenance, evidence and next decision.
- Review status is analyst workflow, not an automatically asserted vulnerability.
- Accepted active/passive observations update the selected site's inventory.

## 7 — recover safely

**As an assessor facing invalid scope, malformed evidence or unreachable equipment, I need an actionable stop state without silent scan expansion.**

- Errors stay within the current workflow and preserve context.
- No fallback sweep, register read or scope expansion occurs.
- A valid Modbus exception confirms only the service, not vendor or model.

## Automated acceptance mapping

| Story | JVM/corpus | Android API 29 + 35 | Live OT emulator |
|---|---:|---:|---:|
| Three-step site onboarding/persistence | Repository model | Site → technology → review → workspace | — |
| Dashboard/inventory reasoning | Filtering/model tests | Search, filter and navigation | — |
| Passive capture analysis | Four protocols + malformed corpus | Real content-URI import | — |
| Active identity | Grant/policy/codec tests | Authorization → broker → result | PyModbus, modbus-tk, Conpot |
| Safe failure | Replay, scope and parser tests | Local scope-stop screen | Service-only and failure paths |
