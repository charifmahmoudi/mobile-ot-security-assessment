# Atlas OT Scout guided user manual

This manual covers the implemented P0-WATER discovery slice: offline PCAP/PCAPNG analysis and one explicitly authorized Modbus/TCP identity check. It does not describe an unrestricted network scanner or a complete certification audit.

The screenshots were captured automatically from the Android 15 (API 35) emulator acceptance tests in [CI run #16](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/runs/33322997770). Test values such as `E2E-WATER-001`, `CI treatment cell`, and `10.0.2.2` are examples, not recommended production values.

## 1. Before going on site

For passive work, obtain:

- a PCAP or PCAPNG exported from an approved SPAN port, network TAP, capture appliance, or packet-analysis workstation;
- the capture start/end time, collection point, and responsible operator;
- permission to handle the capture, because it may contain sensitive addresses and process communications.

For active work, obtain all of the following before opening the active workflow:

- written operational and security authorization;
- a work-order or case reference;
- the site and process area;
- one exact controller IPv4 address;
- the approved CIDR containing that address;
- the Modbus unit ID;
- an approved maintenance window;
- the matching signed Network Broker application.

Do not guess a broader CIDR, sweep unit IDs, or substitute another target when a check fails.

## 2. Choose the collection mode

![Atlas OT Scout home screen](screenshots/01-home-api35.png)

The home screen presents two distinct workflows.

| Situation | Choose | Network effect |
|---|---|---|
| You were supplied a PCAP/PCAPNG, or the phone cannot observe the switched segment | **Analyze an existing capture** | No packets transmitted |
| You have written authorization for one known controller | **Start an authorized assessment** | One bounded Modbus identity request |
| You do not know the target or approved scope | Neither | Stop and obtain the missing authorization data |

The Case App has no Internet permission. Active network access is delegated to the separately signed and constrained Network Broker.

## 3. Passive identification from PCAP or PCAPNG

### Step 1 — select the capture

1. From the home screen, tap **Analyze an existing capture**.
2. In Android's document picker, select the approved `.pcap` or `.pcapng` file.
3. Wait while the app validates the container and analyzes supported OT framing locally.

The app does not require storage-wide permission. A malformed, truncated, oversized, or unsupported capture is stopped rather than partially trusted.

### Step 2 — verify the capture summary

![Passive Modbus capture result](screenshots/04-passive-modbus-api35.png)

Before reviewing assets, compare the displayed capture name and SHA-256 prefix with the file you intended to analyze. Then review:

- **Capture packets** — all packets present in the file;
- **OT packets** — packets matching supported, validated OT framing;
- **Duration** — time between the first and last accepted packet timestamps;
- **Protocols** — supported OT protocols observed in this capture;
- **Assets** — endpoints supported by packet evidence, not a complete site inventory.

Protocol examples exercised by the research corpus are shown below.

| Capture result | Example |
|---|---|
| Modbus/TCP | ![Modbus passive result](screenshots/04-passive-modbus-api35.png) |
| DNP3 | ![DNP3 passive result](screenshots/04-passive-dnp3-api35.png) |
| IEC 60870-5-104 | ![IEC-104 passive result](screenshots/04-passive-iec104-api35.png) |
| BACnet/IP | ![BACnet passive result](screenshots/04-passive-bacnet-api35.png) |

### Step 3 — review each proposed asset

For every proposed asset, review the address, protocol, inferred role, confidence, and evidence statement. Confirm the observation against the collection point and any approved inventory available to you.

Interpret the result conservatively:

- a detected asset means that supporting traffic appeared in this capture;
- no detected asset does **not** mean the segment has no OT assets;
- missing vendor/model data does not reduce a protocol observation to zero evidence;
- unsupported link types and parser warnings must be recorded as visibility limitations.

### Step 4 — accept or reject the observation

Tap **Save assets to case** only after the observations have been reviewed. In the current PoC this action confirms the decision only for the running session; durable encrypted case storage and export are not yet implemented. Record the original capture and its full SHA-256 in the controlled assessment workspace outside the PoC.

## 4. Authorized active Modbus identification

### Step 1 — record the authorization context

From the home screen, tap **Start an authorized assessment**.

![Active assessment authorization screen](screenshots/02-active-authorization-api35.png)

Complete the three sections in order:

1. **Record the work order** — enter the real case reference and site/process area.
2. **Confirm the exact target and scope** — enter one canonical IPv4 address, its authorized CIDR, and a unit ID from 0 to 247.
3. **Confirm authorization** — compare the values on screen with the written authorization, then select the checkbox.

The button remains disabled until authorization is confirmed. The screen states the operation before execution: one Modbus Device Identification request on TCP/502, a 1.5-second timeout, and no register writes.

### Step 2 — respond to a scope error

![Out-of-scope target blocked](screenshots/03-out-of-scope-blocked-api35.png)

If the target is outside the entered CIDR, the app stops before contacting the Network Broker. Correct a transcription error only by referring to the authorization document. Do not enlarge the CIDR merely to make validation pass.

### Step 3 — run the bounded check

1. Recheck the target, CIDR, unit ID, work order, and site.
2. Select the authorization checkbox.
3. Tap **Authorize and identify device** once.
4. Keep the application open while the signed one-use grant is processed.

The broker enforces the target, TCP port, unit, interface, time window, byte/packet limits, retry limit, and replay nonce. It does not expose a generic scanning socket to the Case App.

### Step 4 — interpret the result

![PyModbus identity-confirmed result](screenshots/05-active-pymodbus-api35.png)

| Result | Meaning | Assessor action |
|---|---|---|
| **IDENTITY CONFIRMED** | The service returned Modbus identity objects such as vendor, product, or revision | Compare values with the device label and approved inventory before saving |
| **SERVICE CONFIRMED** | A valid Modbus response or exception confirmed the service, but reliable vendor/model identity was not returned | Save only the protocol/service observation; do not invent device identity |
| **ACTION REQUIRED** | The request failed safely, the broker is unavailable, or the response is invalid | Record the message and troubleshoot without expanding scope |

An independent service-only result from modbus-tk shows how the UI avoids inventing vendor/model identity:

![modbus-tk service-confirmed result](screenshots/05-active-modbus-tk-api35.png)

Tap **Save identified asset** only after comparing the evidence with the physical device and authorized inventory. As with passive saving, current PoC state is not durable after the process ends.

## 5. Safe-stop rules

Stop and escalate instead of improvising when:

- the authorization does not identify the target and scope;
- the controller address is outside the approved CIDR;
- the correct Modbus unit ID is unknown;
- the matching signed broker is missing;
- the maintenance window has closed;
- the interface shown in the result is not the intended assessment interface;
- the result conflicts with the physical label or authoritative inventory;
- a PCAP warning makes visibility insufficient for the requested conclusion.

The PoC does not fall back to a port sweep, address sweep, unit sweep, register read, credential attempt, or write operation.

## 6. Troubleshooting

| Message or symptom | What it means | Safe response |
|---|---|---|
| Capture could not be analyzed | File validation, size, framing, or read failed | Verify the original file and hash; export again without modifying the original |
| No supported OT protocol evidence | Nothing supported was observed in this visibility sample | Check collection point, duration, link type, and whether relevant traffic occurred |
| Target outside authorized CIDR | Local preflight blocked the request | Compare target and scope with the written authorization |
| Network broker unavailable | Matching broker is not installed or cannot bind | Install/verify the signed broker; do not use an unrestricted scanner |
| Device identification failed safely | Timeout, routing, interface, invalid response, or policy rejection | Record the exact message and verify cabling/routing/window with operations |
| Service confirmed; vendor/model blank | Device did not return supported identity objects | Report service evidence only and corroborate identity physically |

## 7. What this version does not yet provide

- live whole-segment sniffing from an ordinary phone;
- physical SPAN/TAP or USB-Ethernet acceptance;
- general subnet or multi-protocol active scanning;
- durable encrypted cases or chain-of-custody storage;
- inventory reconciliation and findings workflow;
- signed professional report generation.

Use imported PCAP/PCAPNG for passive identification when the phone cannot observe the segment. Treat this PoC as a controlled evidence-collection component, not as a complete professional assessment deliverable.
