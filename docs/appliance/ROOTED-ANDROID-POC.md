# Rooted Android PoC platform decision

Status: **selected laboratory platform; not hardware-qualified**

This document owns the **laboratory Android platform and model selection**. Passive-capture security/privilege architecture belongs in [Dedicated Android passive-capture appliance](../architecture/DEDICATED-ANDROID-APPLIANCE.md), and current executable status belongs in [IMPLEMENTATION.md](../../IMPLEMENTATION.md).

## Selected laboratory platform

Use a dedicated Samsung laboratory phone running a custom **LineageOS 23.2 / Android 16 `userdebug` build**, with Atlas platform components integrated into the image. Build the same pinned LineageOS source branch for an x86_64 Android Emulator target and for the exact Samsung device target.

Selected physical family:

- Samsung Galaxy S20 4G/5G Exynos, Lineage codename `x1s`;
- accepted candidate model identifiers: `SM-G980F`, `SM-G980F/DS`, `SM-G981B`, `SM-G981B/DS`;
- other S20 variants require separate compatibility evidence.

Selected virtual target:

- LineageOS `sdk_phone_x86_64` `userdebug` image;
- Cuttlefish `aosp_cf_x86_64_only_phone-userdebug` as a lower-level kernel/SELinux fallback.

Emulator and physical images come from the same pinned source/Atlas revision but are device-specific. Never flash an emulator image to the physical phone.

Sources:

- https://wiki.lineageos.org/devices/x1s/
- https://wiki.lineageos.org/devices/x1s/install/
- https://wiki.lineageos.org/devices/x1s/build/
- https://wiki.lineageos.org/emulator
- https://source.android.com/docs/devices/cuttlefish/get-started

## Why this laboratory target

- LineageOS publishes maintained build and installation paths for `x1s`.
- A published emulator target allows platform integration to be exercised before physical flashing.
- The S20 generation is readily available used and has adequate CPU, memory, storage and USB host capability for PoC work.
- Exact model enrollment can reject incompatible regional variants.
- The platform can host the confined capture daemon without giving the Case App general-purpose root.

This is a **PoC platform choice**, not a commitment to Samsung as production hardware.

## Laboratory root posture

`userdebug` and temporary root techniques are permitted only to develop and validate the platform integration. Field product behavior must preserve the component boundaries in the canonical appliance architecture.

Magisk is not required by the intended image because the capture daemon is integrated as a platform component. It may be used temporarily to prove a driver/adapter on laboratory hardware, but such a device is labeled **LAB-ROOTED**, stores no customer cases and is not evidence of the production boundary.

Samsung-specific bootloader/root work can permanently affect Knox-dependent services. Official Magisk Samsung guidance: https://topjohnwu.github.io/Magisk/install.html#samsung-devices

## Platform-specific risks

- Bootloader unlocking wipes the device.
- Some Samsung regional variants cannot be bootloader-unlocked.
- Laboratory unlocked boot state is weaker than a production verified-boot design.
- Custom OS maintenance becomes part of the product security lifecycle.
- USB NIC, hub, power, thermal, suspend and packet-loss behavior are device-specific and require physical measurement.
- Internal Wi-Fi monitor mode cannot be assumed from root alone; driver/firmware/HAL support is hardware-specific.

## Build and qualification sequence

1. Pin a specific LineageOS manifest revision and Atlas commit.
2. Build and boot the emulator `userdebug` target.
3. Integrate the Case App, Network Broker, Capture Broker, parser boundary and `atlas_capture` platform component.
4. Validate package signatures, SELinux integration, raw receive behavior and zero egress in the virtual environment.
5. Acquire an exact accepted `x1s` candidate and record model, bootloader, firmware and Knox state.
6. Build and flash the device-specific image using the official Lineage installation path.
7. Qualify the selected powered hub, USB Ethernet NIC and SPAN/TAP topology.
8. Record measured results in [COMPATIBILITY-MATRIX.md](COMPATIBILITY-MATRIX.md).

Hardware support is claimed only after the compatibility gates pass.

## Exit from laboratory hardware

A commercial appliance requires a stronger boot/update/tamper story than an unlocked PoC phone. Production hardware must support product-controlled verified boot and relocking or an equivalent measured-boot control accepted by the security design. The `x1s` laboratory choice therefore remains separate from the eventual production hardware decision.
