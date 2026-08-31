# Atlas OT Scout - actual user-story screen recording

## What this video is

`atlas-ot-scout-emulator-demo.mp4` is a **continuous `adb screenrecord` capture of the running Android API 35 application**. It is not a slideshow, screenshot animation or mock UI.

The interaction is automated only so the same buyer story can be reproduced in GitHub Actions. Touch indicators are enabled during capture. The active identity step connects through the real signed app/broker path to a controlled PyModbus testbed exposed to the emulator at `10.0.2.2:502`.

The final MP4 is normalized for broad playback compatibility: **H.264 constrained-baseline, constant 30 fps, `yuv420p`, AAC-LC audio and MP4 fast-start**. The PowerPoint deck embeds this file directly on its demo slide.

## User story captured on screen

The recording follows one sample water-treatment assessment from evidence question to handoff decision:

1. Open the **North Water Treatment Plant** sample workspace.
2. Choose **Collect evidence** and review the available passive and active methods.
3. Open the dedicated **SPAN/TAP** path and wait for the emulated receive-only capture capability.
4. Start the bounded passive sample.
5. Review the observed OT assets, explicitly select the observations to accept, and add them to inventory.
6. Open **Findings** to show that conclusions are derived from reviewed evidence rather than device counts.
7. Open the bounded Modbus identity workflow.
8. Enter `192.0.2.5` against the authorized `10.0.2.0/24` scope and show the local **out-of-scope rejection before execution**.
9. Correct the target to the controlled `10.0.2.2` PyModbus testbed and run the exact authorized basic identity request.
10. Show the **Controller identified** evidence result and add it to inventory.
11. Return to findings and finish on **Report readiness**, where unresolved authorization/reviewer gates remain visible.

The recording is deliberately fast enough for a buyer meeting, but every state transition is the application itself.

## Reproduce it

Prerequisites are Docker, JDK 17, Gradle 8.13, the Android SDK, `ffmpeg`/`ffprobe` and a booted Android API 35 emulator reachable through `adb`.

From the repository root:

```bash
bash tools/record_live_demo.sh
```

The script:

- starts the pinned PyModbus CI target;
- installs the Case App, Network Broker, Passive Capture Broker and instrumentation APK;
- starts Android `screenrecord` at a tall phone aspect ratio;
- executes `LiveDemoCaptureTest.recordWaterAssessmentUserStory`;
- trims launch-only frames without replacing application states;
- transcodes to a conservative PowerPoint/browser-compatible H.264/AAC MP4;
- validates codec, pixel format, constant frame rate, audio stream and fast-start ordering;
- records video metadata and the PyModbus service log for provenance.

Output:

```text
docs/demo/atlas-ot-scout-emulator-demo.mp4
```

The [Live emulator demo workflow](../../.github/workflows/live-demo.yml) reproduces this process, rebuilds the prospect deck from the same evidence, and refreshes the generated MP4/PPTX/PDF assets on `main`.

## Proof boundary

This recording demonstrates **application behavior in emulation**:

- guided water-assessment workflow;
- review-first passive evidence handling;
- local scope rejection;
- signed bounded Modbus basic identity execution against PyModbus;
- inventory, finding and report-readiness behavior.

It does not prove a physical Samsung image, USB-Ethernet adapter, physical SPAN/TAP, production packet rate, real PLC firmware, or production OT compatibility. Those remain field-qualification gates.

## Suggested presenter introduction

> This is the actual Android workflow, not a screenshot reel. Watch it start with passive evidence, require an analyst decision before inventory changes, block an out-of-scope active target, identify one authorized Modbus device through the controlled broker path, and finish by showing exactly what still blocks a formal handoff.
