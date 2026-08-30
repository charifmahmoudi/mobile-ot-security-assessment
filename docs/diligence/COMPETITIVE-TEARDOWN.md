# Competitive teardown

## The actual alternatives

The customer can buy a continuous OT platform, run an active-discovery product, use general security/network tools, commission a consultant, or keep a spreadsheet. The product must beat the last two on evidence and repeatability without pretending to replace the first.

## Capability comparison

Scores: 0 absent, 1 limited/indirect, 2 material capability, ? not publicly verifiable. Scores summarize public product information and require hands-on validation.

| Alternative | Portable one-time | Passive evidence | OT identity | Android field UI | Offline case | Active safety governance | Inventory export | Public entry price |
|---|---:|---:|---:|---:|---:|---:|---:|---|
| Claroty Edge | 2 | 1 | 2 | 0 | 1 | 2 | 2 | quote |
| Dragos Platform/SiteStore | 1 | 2 | 2 | 0 | 1 | 2 | 2 | quote |
| Nozomi Guardian/Arc | 1 | 2 | 2 | 0 | 1 | 2 | 2 | quote; Arc per asset |
| runZero | 2 | 0/1 | 1 | 0 | 1 | 1 | 2 | free community up to 100 assets |
| Tenable Nessus Pro | 2 | 0 | 1 | 0 | 2 | 0/1 | 2 | $4,790/year (official 2026 page) |
| Nmap | 2 | 0 | 1 | 0 | 2 | 0 | 1 | no fee; NPSL |
| Wireshark | 2 | 2 | 2 dissectors | 0 | 2 | n/a | 1 | GPL/no fee |
| Malcolm + ICSNPP | 0/1 | 2 | 2 | 0 | 2 | n/a | 2 | no fee; substantial server stack |
| PCAPdroid | 2 | 1 local-device | 0 | 2 | 2 | n/a | 2 | GPL/no fee |
| Spreadsheet + consultant | 1 | ? | 0/1 | 1 | 2 | human | 2 | project-specific |
| Atlas target | 2 | 2 with correct visibility | 2 | 2 | 2 | 2 | 2 | MAD-local tiers proposed |

## Competitor-by-competitor implications

### Claroty Edge

Claroty describes Edge as a Windows/Linux one-time agentless executable delivering CPS visibility in minutes without lower-level sensors. This directly validates the portable-baseline job. Atlas cannot win by claiming “portable discovery” alone. It must win on Android field ergonomics, offline case evidence, low-cost channel packaging, BLE/Wi-Fi workflow and transparent safety.

### Dragos

Dragos is a mature OT security platform and strong threat-intelligence brand. It is an enterprise complement/competitor. Atlas should export evidence toward such platforms and target sites or engagements not yet instrumented.

### Nozomi

Nozomi promotes passive and active asset discovery; Arc is an endpoint sensor priced by monitored assets. Atlas differs as an assessor-controlled field case, not continuous endpoint protection.

### runZero

runZero is the closest product-category substitute for active asset inventory. It offers a free community edition up to 100 assets. Atlas must demonstrate superior OT safety, packet provenance, disconnected operation and mobile multi-interface collection. Price alone will not win.

### Tenable Nessus

Nessus Professional has a transparent $4,790 annual list price in 2026. It establishes an upper reference for a professional general scanner, but active vulnerability scanning is not the desired OT safety posture. Atlas Professional must be materially cheaper locally or deliver an OT-specific outcome.

### Open-source stack

Nmap/Wireshark/Zeek/ICSNPP/Malcolm can produce excellent technical evidence in expert hands. Atlas adds governed workflow, packaging, identity confidence, local reports and support. It must not misrepresent open-source work as proprietary invention.

## Positioning

Do not position as “cheaper Claroty.” Position as:

> The controlled field baseline and evidence handoff for sites not yet covered continuously, and for professionals who assess many sites.

## Defensible proof comparison

Before launch, publish:

- tested protocol-family count, with exact identity depth;
- assets/hour on a reproducible lab;
- false merge/split and model-identification rates;
- RAM, battery and storage behavior;
- packets sent per active profile;
- supported Android/NIC/TAP matrix;
- report reconciliation time versus Wireshark/Nmap/spreadsheet workflow;
- export acceptance by at least two downstream systems.

## Competitive response risks

| Response | Probability | Counter |
|---|---|---|
| Incumbent ships mobile collector | medium | country/channel packs and field evidence workflow |
| runZero expands OT/mobile | high | operational safety and passive multi-interface specialization |
| integrator scripts own toolkit | high | signed knowledge, QA, reporting and support |
| Android OEM restrictions worsen | medium | capture accessory abstraction and optional rugged Linux companion |
| free tools remain sufficient | certain | sell repeatability, governance and service margin—not packets |
