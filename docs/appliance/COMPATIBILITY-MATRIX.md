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

## Tablet screening candidates

The tablets below satisfy the first application-level screen: their shipping Android versions are newer than the Case App's Android 10 (`minSdk 29`) baseline, and Samsung documents a USB-C data interface suitable for later powered-hub and Ethernet testing. They may be used to evaluate the Atlas interface and imported-PCAP workflow on stock Android.

They are **not qualified Atlas appliances**. No device-specific Atlas `userdebug` image, Capture Broker/daemon integration, bootloader path, USB NIC tuple or zero-egress evidence exists for either tablet. Live passive capture must remain unavailable until the complete physical acceptance sequence below passes.

| Tablet | Exact identity to buy | Why it is screened | Atlas status | Purchase links |
|---|---|---|---|---|
| Galaxy Tab Active5 Pro Wi-Fi | `SM-X350`; 128 GB SKU `SM-X350NZGAN20`; 256 GB SKU `SM-X350NZGEN20` | Rugged 10.1-inch field form factor, USB 3.2 Gen 1, removable 10,100 mAh battery | **Preferred tablet qualification candidate**; stock-Android application/imported-PCAP evaluation only; not tested | [Samsung 128 GB](https://www.samsung.com/us/business/tablets/galaxy-tab-active5-pro/buy/galaxy-tab-active5-pro-128gb-wi-fi-sku-sm-x350nzgan20/) · [Amazon 128 GB](https://www.amazon.com/dp/B0F9FJVPQL) |
| Galaxy Tab Active4 Pro Wi-Fi | `SM-T630`; 64 GB SKU `SM-T630NZKAN20`; 128 GB SKU `SM-T630NZKEN20` | Rugged 10.1-inch field form factor, USB 3.2 Gen 1, removable 7,600 mAh battery and no-battery mode | **Secondary tablet qualification candidate**; stock-Android application/imported-PCAP evaluation only; not tested | [Samsung 128 GB](https://www.samsung.com/us/business/tablets/galaxy-tab-active/buy/galaxy-tab-active4-pro-128gb-wi-fi-sm-t630nzken20/) · [Amazon 64 GB](https://www.amazon.com/dp/B0BBT3867D) |

Purchase links are procurement aids, not compatibility evidence or availability guarantees. Confirm the exact model and SKU on the seller page before ordering; do not substitute a cellular, carrier or regional variant. Samsung's technical specifications are the screening sources for the [Active5 Pro Wi-Fi](https://www.samsung.com/ch/business/tablets/galaxy-tab-active/galaxy-tab-active5-pro-sm-x350nzgaeee/) and [Active4 Pro](https://news.samsung.com/global/introducing-the-galaxy-tab-active4-pro-a-ruggedized-device-designed-for-the-new-mobile-workforce). The first acquired tablet must still be enrolled by exact hardware identity and tested with the intended hub, NIC and SPAN/TAP path.

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
