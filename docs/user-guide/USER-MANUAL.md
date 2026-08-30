# Atlas OT Scout — guided user manual

Atlas OT Scout is a site-centered OT discovery PoC. The working journey is:

**Prepare → Collect → Review → Reason → Report.**

This manual covers the implemented passive PCAP/PCAPNG analysis and the explicitly authorized, single-target Modbus/TCP identity check. It does not describe an unrestricted scanner or a complete certification audit.

Every screenshot below is an unmodified Android 15 emulator output from green [CI run #28](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/runs/33335842915) at commit [`f9807de`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/commit/f9807de7635656b2b5d4d86e585bdd515b9cf1ec). That run also passed Android 10, PyModbus, Modbus-TK, Conpot, passive research captures and the rooted AF_PACKET zero-transmission gate.

## 1. Start in the correct site

![Choose an existing site or create a new one](screenshots/01-site-selection-api35.png)

The first screen establishes the operating context before any evidence is collected.

- Tap an existing site to continue its assessment.
- Tap **Create a new site** when the location is not listed.
- The included North Water Treatment Plant is clearly marked **SAMPLE**. It is demonstration data, not field evidence.

Confirm the site and process area before continuing. Evidence assigned to the wrong site can produce a misleading inventory.

## 2. Create a site

![New-site guided form](screenshots/02-new-site-api35.png)

Enter the site information once so it remains visible throughout the assessment:

1. Enter a distinctive **site name**.
2. Enter the **location / process area** at the level covered by the authorization.
3. Choose the **industry** from the dropdown.
4. Select any known **main technology vendors**. Multiple vendors may be selected.
5. Choose the report language: French, Arabic or English.
6. Choose the local evidence-retention period.
7. Tap **Create site workspace**.

Vendor selections are context and filtering hints—not claims that equipment has been discovered. A vendor becomes an asset fact only when supported by evidence and analyst review.

PoC note: site and inventory state is persisted locally using application preferences. Encrypted professional case storage, access control and export are later milestones.

## 3. Read the site dashboard

![Site assessment dashboard](screenshots/03-site-dashboard-api35.png)

Use the dashboard as the assessment home:

- **Assets** shows the working inventory count.
- **Protocols** counts distinct protocols currently represented.
- **To review** highlights observations needing analyst attention.
- **Network picture** summarizes roles and the next priority.
- **Recent assets** gives quick access to the latest evidence-backed records.

Tap **Open asset inventory** when you need to investigate the current model. Tap **Collect evidence** when you know what evidence gap you intend to close.

Inside a site, the bottom navigation is persistent:

| Destination | Use it for |
|---|---|
| **Overview** | Progress, coverage and the recommended next action |
| **Collect** | Passive, imported and explicitly authorized active evidence |
| **Assets** | Observation review, inventory filtering and the process-zone model |
| **Findings** | Evidence-linked draft conditions and required validation |
| **Report** | Readiness gates and draft report preview |

## 4. Choose the collection method

![Passive and active collection methods](screenshots/04-collection-methods-api35.png)

Choose the method that matches both visibility and authorization.

| Situation | Method | Network effect |
|---|---|---|
| An approved PCAP/PCAPNG is available, or the phone cannot observe the switched segment | **Analyze PCAP / PCAPNG** | No packets transmitted |
| One controller and exact CIDR are explicitly authorized | **Identify one known controller** | One bounded Modbus identity request |
| Target or scope is unknown | Stop | Obtain authorization or a capture |

Wi-Fi and Bluetooth appear under **Planned collection packs** and cannot be mistaken for working capability.

### Use the dedicated rooted capture path

Choose **Observe a SPAN / TAP interface** only when the dedicated appliance image and a receive-only interface are available. Confirm that the app reports the Capture Broker as ready before attaching the approved SPAN/TAP feed.

| Capture broker ready | Passive result from the live stream |
|---|---|
| ![Rooted capture broker ready](screenshots/05-live-span-ready-api35.png) | ![Live SPAN result](screenshots/06-live-span-result-api35.png) |

The capture service writes a bounded PCAP through an isolated file-descriptor boundary. It does not expose a packet-send operation. CI compiles the native AF_PACKET daemon, captures injected Ethernet on a virtual SPAN link and traces the daemon to prove that it calls no `send`, `sendto` or `sendmsg` syscall. This validates the software boundary; it does not replace qualification on the selected phone, USB adapter and physical TAP.

## 5. Analyze a passive capture

### Prepare the evidence

Obtain the original PCAP or PCAPNG, capture start/end time, collection point, operator and authorization to handle the file. A capture may contain sensitive addressing and process communications.

### Import and review

1. Tap **Analyze PCAP / PCAPNG**.
2. Select the approved file in Android's document picker.
3. Review the filename, SHA-256 prefix, packet counts, time window and protocol summary.
4. Review every proposed endpoint, inferred role and confidence.
5. Select **Accept into site inventory** only for observations supported by the capture and site context.
6. Tap **Add selected observations**. Unselected records remain outside inventory.

![Passive Modbus observations awaiting review](screenshots/08-passive-modbus-api35.png)

The screen deliberately says **observed**, **candidate** and **to review**. A capture is a visibility sample: absence from the file is not proof that an asset is absent from the site.

The research corpus also exercises DNP3, IEC 60870-5-104 and BACnet/IP:

| DNP3 | IEC 60870-5-104 | BACnet/IP |
|---|---|---|
| ![DNP3 result](screenshots/08-passive-dnp3-api35.png) | ![IEC-104 result](screenshots/08-passive-iec104-api35.png) | ![BACnet result](screenshots/08-passive-bacnet-api35.png) |

Malformed, truncated, oversized or unsupported captures stop safely rather than being partially trusted.

## 6. Identify one authorized Modbus device

Before starting, obtain written authorization, a case reference, process area, one controller IPv4 address, its approved CIDR, Modbus unit ID and approved time window. Do not guess a broader CIDR or sweep targets.

![Active authorization and exact scope form](screenshots/05-active-authorization-api35.png)

1. Enter the work-order or case reference and process area.
2. Enter the exact target IPv4, authorized CIDR and unit ID.
3. Read the displayed limit: one FC 43 / MEI 14 request on TCP/502, 1.5-second timeout, no register reads or writes.
4. Compare the values with the written authorization.
5. Select the authorization checkbox and tap **Authorize and identify device** once.

If the target is outside the entered CIDR, the Case App stops before contacting the Network Broker:

![Out-of-scope target blocked locally](screenshots/06-out-of-scope-blocked-api35.png)

Do not enlarge the scope to make validation pass. Correct transcription errors only from the authorization record.

### Interpret the result

![PyModbus controller identity result](screenshots/09-active-pymodbus-api35.png)

| Result | Meaning | Decision |
|---|---|---|
| **Identity confirmed** | Valid vendor, product or revision identity objects were returned | Corroborate, then add the asset |
| **Service confirmed** | Modbus responded, but reliable device identity was not returned | Record the service only |
| **Action required** | The request failed safely or the broker rejected it | Record the message; do not broaden scope |

The modbus-tk emulator demonstrates the conservative service-only case:

![modbus-tk service-only result](screenshots/09-active-modbus-tk-api35.png)

Conpot provides an independent ICS-honeypot implementation and must also remain service-only unless supported identity objects are returned:

![Conpot service-only result](screenshots/09-active-conpot-api35.png)

## 7. Navigate and reason about the asset inventory

![Searchable and filterable asset inventory](screenshots/07-asset-inventory-api35.png)

The inventory is a working evidence model, not a flat scan result.

- Search by address, name, vendor, protocol or role.
- Filter all assets, review items, controllers, HMI/clients, gateways, passive evidence or active evidence.
- Use the network insight card to see protocol concentration, identified vendors and unresolved observations.
- Tap an asset to inspect identity, provenance, confidence, supporting evidence and the recommended next decision.
- Treat **Review** as a queue for analyst work, not a vulnerability label.

After adding passive or active evidence, return here to ask: Is this a new asset, corroboration of an existing one, a role conflict, or an observation that needs physical/inventory validation?

Tap **Zone map** to group the evidence model into supervisory, control, integration and field functions. This view is intentionally not a raw IP-node graph. Missing traffic is not interpreted as proof that a connection is absent.

## 8. Review findings and report readiness

Open **Findings** after reviewing the inventory. Draft findings keep confidence separate from consequence and state what still needs validation. Protocol presence alone does not become an exploitability or business-impact claim.

Open **Report** to inspect the finalization gates. The PoC checks site context, evidence-backed inventory and unresolved identities, while clearly blocking final issue until authorization records, an independent reviewer, encrypted case storage and deterministic export are implemented.

![Report readiness with explicit blockers](screenshots/10-guided-report-readiness-api35.png)

## 9. Safe-stop rules

Stop and escalate when authorization, target, scope, unit ID, maintenance window or intended interface is uncertain; when the result conflicts with a physical label or authoritative inventory; or when capture visibility is insufficient for the requested conclusion.

The PoC never falls back to an address sweep, port sweep, unit-ID sweep, register read, credential attempt or write operation.

## 10. Five-minute demonstration

Use [the presenter script](../product/DEMO-SCRIPT.md) for a coherent customer demonstration. The shortest storyline is:

1. Show site selection and explain why evidence must have site context.
2. Open the sample water site and read its current assessment snapshot.
3. Open inventory, filter **Needs review**, and inspect the unresolved HMI observation.
4. Return to **Collect evidence** and explain the passive-safe default.
5. Import the Modbus PCAP and review its four proposed assets.
6. Show the authorized active path and its exact safety limit.
7. Finish in inventory: evidence becomes useful only after it supports an analyst decision.

## 11. Current PoC boundary

Implemented and green in CI run #28: site onboarding, persisted local site/inventory state, five-stage guided shell, dashboard, passive PCAP/PCAPNG analysis, Modbus/DNP3/IEC-104/BACnet decoders, rooted capture-broker journey, constrained Modbus identity, inventory search/filter/detail, findings drafts and report-readiness blocking.

Not yet implemented: physical rooted-device SPAN/TAP and USB-Ethernet qualification, general active scanning, encrypted multi-user case storage, durable merge/reject audit events, inventory connectors, reviewer signatures and final professional report export. The current Findings and Report destinations are guided PoC workflows, not completed audit issuance.
