# Atlas OT Scout emulator demonstration

## Purpose

This walkthrough demonstrates the implemented P0-WATER user journey as a continuous live screen recording of the Android API 35 emulator in GitHub Actions. It is intended for an industrial buyer, qualified audit provider or technical evaluator.

The recording is deliberately labelled **emulated evidence**. It demonstrates application behaviour and the controlled CI testbed; it does not claim validation against physical PLC hardware, a production network or a flashed Samsung device.

## Presenter storyline

1. **Establish context.** Choose an existing operating site or create a bounded workspace.
2. **Define the site.** Record the process area and industry so the assessment has an operational boundary.
3. **Add vendor context.** Treat expected vendors as context—not as discovered assets.
4. **Orient the assessor.** Use the dashboard's next action to move the assessment forward.
5. **Choose evidence.** Prefer PCAP import or SPAN/TAP capture; use active identity checks only when authorized and necessary.
6. **Review passive observations.** Promote only supported observations into inventory.
7. **Authorize active work.** Bind the request to a work order, exact target, CIDR and time window.
8. **Fail closed.** Show that an out-of-scope target is blocked before transmission.
9. **Validate identity.** Demonstrate a single bounded Modbus identity request against the CI emulator.
10. **Reason through inventory.** Filter by zone, protocol, vendor, confidence and review state.
11. **Control the handoff.** Keep the report blocked until required reviews and approvals are complete.

## Build

Run from the repository root while an Android API 35 emulator is connected through `adb`:

```bash
bash tools/record_live_demo.sh
```

The output is `docs/demo/atlas-ot-scout-emulator-demo.mp4` (720×1280, H.264, silent). It is a continuous `adb screenrecord` capture of the running application; it is not assembled from screenshots. The [Live emulator demo workflow](../../.github/workflows/live-demo.yml) reproduces and publishes the MP4 as a GitHub Actions artifact.

## Recommended live introduction

> Atlas OT Scout is a dedicated field instrument for controlled OT asset assessment. This demonstration follows one water-treatment segment from site setup to a reviewable inventory and report-readiness decision. The application is running in an Android 15 emulator, and its active Modbus path connects only to controlled CI testbeds.
