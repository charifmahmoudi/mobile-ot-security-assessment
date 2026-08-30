# Dedicated Android PoC compatibility matrix

Compatibility is exact and evidence-based. Family names such as “Galaxy S20” are not sufficient for enrollment.

## Platform matrix

| Target | Exact identity | OS target | Intended test | Current status |
|---|---|---|---|---|
| Android Emulator | x86_64 AVD | LineageOS 23.2 `sdk_phone_x86_64-userdebug` | UI, Binder, SELinux policy, capture stream, parser | Selected; image build pending |
| Cuttlefish | `aosp_cf_x86_64_only_phone-userdebug` | pinned AOSP release | Native daemon/kernel fallback | Supported upstream; Atlas integration pending |
| Galaxy S20 4G | `SM-G980F`, `SM-G980F/DS` | LineageOS 23.2 `x1s-userdebug` | Physical USB Ethernet/SPAN PoC | Candidate; not yet acquired/tested |
| Galaxy S20 5G | `SM-G981B`, `SM-G981B/DS` | LineageOS 23.2 `x1s-userdebug` | Physical USB Ethernet/SPAN PoC | Preferred candidate; not yet acquired/tested |
| Other S20 variants | Any other model | none | none | Rejected until separately qualified |
| USA/Canada carrier variants | model-dependent | none | none | Reject when OEM unlock is absent |

Current Lineage device information and exact models: https://wiki.lineageos.org/devices/x1s/

## Root and image compatibility

| Method | Emulator | Selected Samsung | Security interpretation |
|---|---:|---:|---|
| Lineage `userdebug` platform build | Yes | Yes, device-specific image | Preferred PoC: daemon privilege is built and reviewable |
| Magisk patched image | Possible but unnecessary | Model/market dependent | Adapter feasibility only; not product boundary |
| AOSP GSI | Cuttlefish/AVD | Theoretically Treble-capable, device integration uncertain | Not selected for primary Samsung PoC |
| Stock unrooted Samsung | No equivalent | Yes | Imported-PCAP and bounded app functionality only |

## Peripheral qualification table

No USB or Wi-Fi adapter is marked compatible before physical testing.

| Component | Required properties | Acceptance evidence | Status |
|---|---|---|---|
| USB-C powered hub | stable OTG, external power, no unexpected link reset | 4-hour capture and reconnect tests | Not selected |
| USB Ethernet NIC | kernel driver present, promiscuous receive, VLAN preservation, stable MAC | packet corpus, loss, VLAN and TX-counter tests | Not selected |
| Passive network TAP | receive-only monitoring path, supported speed/duplex | independent zero-transmission test | Not selected |
| SPAN port | correct source ports/VLANs and direction configuration | switch configuration record + known-traffic comparison | Site supplied |
| External Wi-Fi NIC | monitor mode and radiotap support in exact kernel/driver | channel, capture and injection-disabled tests | Deferred |

## Required physical acceptance tests

| Gate | Pass criterion |
|---|---|
| Exact hardware identity | model and board match approved list |
| Boot state | expected unlocked PoC state recorded; no unapproved root modules |
| Build identity | OS manifest and Atlas commit match signed test record |
| USB enumeration | qualified NIC consistently appears as dedicated interface |
| No addressing | capture NIC has no IPv4, IPv6 or default route |
| Receive-only behavior | zero transmitted frames during passive session, independently observed |
| Visibility | known unicast, broadcast, multicast and VLAN fixtures match reference capture |
| Packet loss | within the PoC target at defined rates and capture duration |
| Timestamp behavior | monotonic and within defined reference tolerance |
| Resource behavior | acceptable battery, power, storage and temperature for four hours |
| Disconnect recovery | clean stop, finalized partial evidence and no corrupt case |
| Parser isolation | malformed corpus cannot crash or privilege the Case App/broker |

## Evidence labels

Every capture and report must record one of:

- `EMULATOR_REPLAY` — Android Binder/UI path using a labeled fixture;
- `VIRTUAL_AF_PACKET` — native daemon on virtual Ethernet;
- `LAB_ROOTED_SAMSUNG` — real phone before hardware qualification;
- `QUALIFIED_APPLIANCE` — exact phone/NIC/TAP tuple passed all release gates;
- `IMPORTED_PCAP` — capture supplied from another approved source.

Results from one label may not be silently promoted to another.
