# Rooted Android PoC platform decision

Status: **selected for laboratory implementation; not yet hardware-qualified**

## Decision

Use a dedicated Samsung laboratory phone running a custom **LineageOS 23.2 / Android 16 `userdebug` build**, with the Atlas capture daemon installed as a platform component. Build the same pinned LineageOS source branch for an x86_64 Android Emulator target and for one exact Samsung device target.

Selected physical target:

- Samsung Galaxy S20 4G/5G Exynos family, Lineage codename `x1s`;
- exact accepted models: `SM-G980F`, `SM-G980F/DS`, `SM-G981B`, `SM-G981B/DS`;
- other S20 model numbers are incompatible unless separately qualified.

Selected virtual target:

- LineageOS `sdk_phone_x86_64` `userdebug` image in the Android Emulator;
- Cuttlefish `aosp_cf_x86_64_only_phone-userdebug` remains the lower-level kernel/SELinux test fallback.

The emulator and Samsung artifacts come from the same pinned platform branch and Atlas source revision, but they are device-specific images. **Never flash the emulator image to the Samsung phone.**

## Why LineageOS and this Samsung target

- LineageOS publishes current build and installation instructions for `x1s`.
- LineageOS publishes an emulator/AVD build target, so platform services, SELinux policy and application integration can be tested before using hardware.
- The S20 generation is widely available used, supports USB host mode and has sufficient CPU, memory and storage for the PoC.
- Exact supported international model numbers can be enforced during device enrollment.
- It avoids relying on an unmaintained phone or an unofficial GSI with unknown device integration.

Sources:

- LineageOS S20 device: https://wiki.lineageos.org/devices/x1s/
- LineageOS S20 installation: https://wiki.lineageos.org/devices/x1s/install/
- LineageOS S20 build: https://wiki.lineageos.org/devices/x1s/build/
- LineageOS emulator build: https://wiki.lineageos.org/emulator
- AOSP Cuttlefish: https://source.android.com/docs/devices/cuttlefish/get-started

## Root model

The Case App does not receive root and continues to have no Android `INTERNET` permission. Root is an operating-system integration capability, not an application mode.

| Component | Identity | Additional privilege |
|---|---|---|
| Case App | ordinary dedicated UID | none |
| Parser | isolated UID/process | none |
| Network Broker | separate signed UID | Android network sockets only |
| Passive Capture Broker | separate signed UID | Binder access to capture daemon only |
| `atlas_capture` daemon | init-managed SELinux domain | raw packet receive on allowlisted interface |
| Maintenance shell | disabled in field mode | enabled only on laboratory `userdebug` image |

Magisk is not required in the intended build because `atlas_capture` is part of the system image and its privilege is described by init, Linux capabilities and SELinux policy. Magisk may be used temporarily to prove a driver or adapter on stock/Lineage hardware before the custom image is ready, but such a device is labeled **LAB-ROOTED**, stores no customer cases and is never used as evidence of the final security boundary.

## Security invariants

1. The UI never executes `su`, shell commands or arbitrary native binaries.
2. The capture AIDL exposes interface inspection, bounded start and stop only.
3. The passive daemon contains no packet-send operation.
4. Passive capture is limited by interface ID, duration and byte count.
5. The capture interface has no IPv4 or IPv6 address.
6. Android connectivity management must not claim the capture interface.
7. Egress policy and interface TX counters are checked before and during capture.
8. Raw packets enter an isolated parser before reaching the case database.
9. Active scanning remains in the separately authorized Network Broker.
10. Emulator, rooted laboratory and hardware-qualified evidence are visibly distinguishable.

## Benefits delivered by the rooted/custom platform

- Direct `AF_PACKET` Ethernet capture from supported USB NICs.
- Promiscuous capture of frames delivered by a SPAN port or network TAP.
- Layer-2 evidence: VLAN tags, MAC addresses, ARP, LLDP and industrial discovery traffic.
- Continuous PCAP creation without first transferring a file from another laptop.
- Real-time asset and communication-relationship updates.
- OS-level control over interface addressing, DHCP, IPv6 and egress.
- Ability to qualify external Wi-Fi adapters for monitor mode later.
- Better device attestation, kiosk operation and controlled update policy than an ordinary app install.

Root does not defeat a switch. An ordinary access port still exposes only frames delivered to that port. Whole-segment visibility requires an approved SPAN/mirror port or TAP. Root also does not guarantee monitor mode on the internal Wi-Fi chipset; driver, firmware and HAL support remain hardware-specific.

## Costs and risks

- Bootloader unlocking wipes the device and reduces boot-chain assurance.
- Samsung Magisk installation permanently trips the Knox Warranty Bit.
- Some USA/Canada Samsung variants cannot be bootloader-unlocked; model enrollment must reject them.
- Magisk-rooted Samsung firmware loses the ordinary OTA path and requires manual patched-image upgrades.
- Custom OS maintenance becomes part of the product security lifecycle.
- USB NIC, power, thermal, suspend and packet-loss behavior require physical qualification.

Official Magisk Samsung guidance: https://topjohnwu.github.io/Magisk/install.html#samsung-devices

## PoC build sequence

1. Pin a specific `lineage-23.2` manifest revision and Atlas commit.
2. Build `sdk_phone_x86_64-userdebug` and boot it in Android Emulator.
3. Integrate the three APK boundaries plus `atlas_capture` into the image.
4. Run package, SELinux, raw-capture and zero-egress tests in the emulator/virtual network.
5. Acquire an exact `x1s` supported model and record model, bootloader, firmware and Knox state.
6. Build and flash the device-specific `x1s-userdebug` image using the official Lineage installation path.
7. Qualify one powered USB hub, one USB Ethernet NIC and one passive TAP/SPAN topology.
8. Run the physical acceptance matrix in `COMPATIBILITY-MATRIX.md`.
9. Only then change status from laboratory to supported PoC hardware.

## Exit from PoC root

The PoC may operate with an unlocked bootloader. A commercial appliance must move to hardware that supports product-controlled Verified Boot keys and a relocked bootloader, or provide an equivalent measured-boot and tamper-control story. Samsung `x1s` is therefore a PoC target, not automatically the production hardware choice.
