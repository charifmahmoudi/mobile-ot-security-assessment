# Atlas OT Scout guided customer-story video

## What the video is

`atlas-ot-scout-emulator-demo.mp4` is a paced prospect story built from a continuous `adb screenrecord` capture of the real Android API 35 application.

The application footage is not mocked or replaced. The final composition adds only explanatory material around it:

- current-state, guided-action and desired-state captions;
- a visible five-step journey indicator;
- labelled click pulses at the important interactions;
- deliberate holds so a prospect has time to read the screen;
- a final explanation of the try-before-you-buy appliance model.

The workflow keeps the uncomposed application capture in the GitHub Actions artifact for provenance.

## Customer story

The customer starts with an inventory but cannot confidently show whether it still matches the bounded water-treatment segment. Broad discovery is not an acceptable default.

Atlas guides the customer through five decisions:

1. **Collect:** choose the least intrusive useful method and verify the receive-only SPAN/TAP path.
2. **Review:** turn packet evidence into observations, without silently changing inventory.
3. **Reconcile:** compare expected assets with observed reality and keep conflicts visible.
4. **Reason:** when passive evidence leaves one identity gap, authorize one exact target. The application blocks an out-of-scope target and permits only the corrected bounded identity request.
5. **Handoff:** connect findings to reviewed evidence and show what is ready, what needs action and what still blocks sign-off.

The story finishes in the desired state: an evidence-backed baseline, explicit unknowns, prioritized follow-up and a repeatable offline workflow.

## Commercial story

Atlas is delivered as a configured assessment appliance.

1. We deliver and configure the device for the agreed scope.
2. We guide the first assessment and leave the appliance with the customer.
3. The customer uses it during the free bounded pilot and judges the value in its own environment.
4. If the customer finds it useful, it acquires the appliance and keeps the capability on site.
5. If not, we collect the appliance.
6. Consulting and support packages can be added when the customer needs specialist help, operating support or assessment assistance.

## Reproduce it

The live-demo workflow:

1. records the deterministic Android user journey;
2. preserves that raw application capture in `build/live-demo/atlas-ot-scout-app-capture.mp4`;
3. extracts evidence frames for the deck;
4. runs `tools/compose_guided_customer_story.py` to add captions, pacing and click indicators;
5. validates the complete MP4 by decoding it end to end;
6. embeds the same MP4 in the PowerPoint and publishes both versions in the workflow artifact.

The final media profile is H.264 constrained-baseline, `yuv420p`, constant 24 fps, AAC-LC and MP4 fast-start.

## Proof boundary

The video demonstrates application behavior in Android emulation against a controlled PyModbus target. It does not prove a qualified physical appliance, USB-Ethernet adapter, physical SPAN/TAP, real PLC compatibility, production packet rates or production release readiness. Those remain explicit free-pilot and release gates.
