# H2 Capture Accessory Reference Design

_Status: PoC reference implementation, not the final commercial hardware. Purpose: make passive SPAN/TAP capture reproducible without rooting Android._

## 1. Hardware bill of materials

| Item | Fixed PoC baseline | Function |
|---|---|---|
| Compute | Raspberry Pi 4 Model B, 2 GB or 4 GB | Capture and PCAPNG streaming |
| Capture port | Pi onboard Gigabit Ethernet | Connect only to receive-only TAP output or switch SPAN destination |
| Phone link | Pi onboard 5 GHz Wi-Fi in local AP mode | Isolated control/data link to Android |
| Storage | High-endurance 64 GB microSD | OS plus encrypted temporary capture spool |
| Power | Official-equivalent regulated 5 V/3 A supply or tested battery | Accessory is never powered by the phone |
| Enclosure | Vented tamper-evident enclosure | Physical control and labeling |
| Cable | Short Cat6 patch cable | SPAN/TAP to capture port |

Raspberry Pi documents the Pi 4’s Gigabit Ethernet, Wi-Fi and hardware baseline ([specification](https://www.raspberrypi.com/products/raspberry-pi-4-model-b/specifications/)). This choice is for availability and reproducibility, not production hardening.

## 2. Network topology

```mermaid
flowchart LR
  OT["OT switch"] -->|"SPAN destination"| ETH["Pi eth0"]
  ETH --> CAP["dumpcap service"]
  CAP --> API["Atlas capture API"]
  API -->|"isolated Wi-Fi + mTLS"| PHONE["Android app"]
```

Hard invariants:

- `eth0` has no IPv4/IPv6 address.
- IP forwarding and bridge modules are disabled.
- No DHCP client, LLDP transmitter, mDNS, IPv6 router solicitation or other service may transmit on `eth0`.
- Wi-Fi AP is a separate local management network with no Internet uplink.
- The accessory never sits inline and has only one OT-facing Ethernet port.
- The external packet-safety test must observe zero frames transmitted by `eth0`.

If a TAP has separate A/B monitor outputs, the accessory connects to an approved aggregated output. It is not a substitute for a certified passive TAP.

## 3. Software image

Baseline:

- pinned Raspberry Pi OS Lite 64-bit image;
- read-only root filesystem after provisioning;
- `dumpcap` granted only capture capability/group permission;
- small Rust `atlas-captured` service;
- nftables default deny;
- SSH, Raspberry Pi Connect, Avahi, Bluetooth, cloud-init-like services and package auto-update disabled in field mode;
- no compiler, browser or general remote shell in the released image;
- chrony/NTP disabled on the OT port; time is set through the authenticated phone link before capture;
- encrypted capture spool using dm-crypt/LUKS, unlocked by an ephemeral key delivered after authenticated pairing;
- signed OS manifest and measured image hash recorded by the app.

Raspberry Pi notes that Raspberry Pi OS does not provide an encrypted root filesystem by default and discusses secure boot separately; therefore image hardening and encrypted spool are explicit work, not assumed platform properties ([security documentation](https://www.raspberrypi.com/documentation/security/security.html), [computer security notes](https://www.raspberrypi.com/documentation/computers/raspberry-pi.html)).

## 4. Pairing

1. Accessory starts an isolated WPA3-SAE AP; WPA2 fallback is lab-only and visibly flagged.
2. Enclosure label contains accessory ID and bootstrap public-key fingerprint.
3. Android scans the QR code and verifies the fingerprint.
4. Android and accessory perform an ephemeral X25519 exchange authenticated by the provisioned Ed25519 device key.
5. Android issues a short-lived client certificate bound to the case ID and authorization window.
6. All API/stream traffic uses TLS 1.3 with mutual authentication.
7. Pairing and certificate hashes enter the case audit log.

The AP password alone never authorizes capture or evidence export.

## 5. Capture API

Base URL: `https://192.168.77.1:8443/v1`. Only the paired Android client is accepted.

| Method/path | Purpose |
|---|---|
| `GET /status` | Identity, image hash, link, storage, clock, temperature and active session |
| `POST /cases/{id}/prepare` | Install case limits and ephemeral spool key |
| `POST /cases/{id}/captures` | Start capture with duration/byte/rotation ceilings |
| `GET /captures/{id}` | Status, packets, bytes, drops, rotations and stop reason |
| `POST /captures/{id}/stop` | Stop and seal |
| `GET /captures/{id}/chunks/{n}` | Download immutable PCAPNG chunk |
| `GET /captures/{id}/manifest` | Signed list of chunk hashes and capture metadata |
| `DELETE /cases/{id}` | Cryptographically erase spool key and remove chunks |

Every mutating request includes case ID, authorization hash, nonce, timestamp and Android signature. Nonces are single-use.

## 6. Capture session

Request fields:

```text
case_id, authorization_hash, interface=eth0,
snaplen=0, direction=in, duration_seconds<=14400,
rotation_seconds<=1800, rotation_bytes<=536870912,
maximum_case_bytes<=8589934592, stop_on_drop_rate,
phone_request_signature
```

PoC runs no BPF filter by default. If storage policy requires a filter, it is recorded verbatim and disclosed as a visibility limitation.

Each PCAPNG section includes:

- accessory ID and OS image hash;
- interface MAC, negotiated speed and duplex;
- capture start/end UTC and monotonic values;
- snap length and filter;
- packet/byte/drop counters;
- case/authorization hashes;
- stop reason.

Chunks are SHA-256 sealed immediately after rotation. The service signs the manifest with the accessory Ed25519 key. Android verifies the signature and every chunk hash before ingest.

## 7. Service implementation

`atlas-captured` has four processes/privilege domains:

| Component | Privilege |
|---|---|
| API front end | Unprivileged; Wi-Fi management socket only |
| Policy/state service | Unprivileged; verifies case request and budgets |
| Capture launcher | Minimal helper permitted to start/stop fixed `dumpcap` command; no shell interpolation |
| Manifest/seal worker | Access only to case spool directory and signing handle |

No API accepts a command, interface name, file path or arbitrary capture expression. All values map to compiled enums and bounded integers.

## 8. Fail-safe behavior

| Event | Behavior |
|---|---|
| Phone disconnect | Capture continues for at most configured grace period, default 60 s, then seals/stops |
| Wi-Fi authentication failure | Rate limit; no case/status details |
| Ethernet link down | Seal current chunk and stop |
| Drop threshold exceeded | Seal and stop; record measured threshold |
| Storage reserve reached | Seal and stop before filesystem exhaustion |
| Power loss | Previously sealed chunks remain verifiable; open chunk is marked incomplete |
| Clock correction | Record old/new wall time and monotonic continuity |
| Temperature limit | Seal and stop |
| Invalid authorization/profile | Refuse before `dumpcap` starts |

A physical recessed stop button is recommended for the commercial accessory; the PoC has Android stop plus accessory power removal.

## 9. Acceptance tests

The reference accessory passes only if:

1. an independent TAP sees zero transmitted Ethernet frames on `eth0`;
2. it sustains a 100 Mbps mixed-packet stream for 30 minutes;
3. packet/drop counts agree with the traffic generator within the test tolerance;
4. every downloaded chunk and manifest verifies;
5. unauthorized Wi-Fi clients cannot obtain status or captures;
6. route/bridge/forwarding tests show no path between OT Ethernet and Wi-Fi;
7. phone loss, link loss, low storage, over-temperature and power loss follow the defined behavior;
8. reimaging with an unsigned/unapproved image is detected by Android;
9. delete removes the ephemeral key and renders the spool unreadable;
10. the complete BOM, image build recipe, configuration and tests are reproducible.

## 10. Productization decision

The Raspberry Pi design is acceptable for an isolated lab and controlled customer PoC only. Before commercial field release choose one:

- build a hardened capture appliance with verified boot, encrypted storage and a single receive-only OT port; or
- qualify a commercially supported capture accessory and implement its documented API.

That decision is an M5 exit requirement. The Android app and evidence contract remain unchanged.
