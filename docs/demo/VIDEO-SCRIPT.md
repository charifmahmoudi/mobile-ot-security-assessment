# Atlas OT Scout guided customer-story video

This document owns the **video composition and provenance** for `atlas-ot-scout-emulator-demo.mp4`. It does not own current capability status or commercial terms.

## What the video is

The video is composed from a continuous Android application recording. The composition adds explanatory captions, journey markers, click indicators and pacing around the recorded application footage; it does not replace application screens with mock screens.

The raw application capture is retained by the live-demo workflow as provenance.

## Story represented

The customer starts with an expected inventory but cannot confidently show whether it matches the bounded water-treatment segment. The video follows five decisions:

1. **Collect** — choose the least intrusive useful evidence method.
2. **Review** — convert packet evidence into observations without silently changing inventory.
3. **Reconcile** — compare expected records and observations while preserving conflicts.
4. **Reason** — when passive evidence leaves one identity gap, authorize one exact bounded active check.
5. **Handoff** — show evidence-linked conditions, open decisions and readiness blockers.

This is the narrative the media asset demonstrates; normative assessment semantics are maintained in [ASSESSMENT-METHOD.md](../poc/ASSESSMENT-METHOD.md).

## Commercial reference

The ending may point the viewer to a bounded evaluation. Exact duration, keep/return/self-build/setup choices and consulting terms are maintained only in [OFFER.md](../business-development/OFFER.md) and should not be independently maintained in this script.

## Reproduction

The live-demo workflow:

1. records the deterministic Android journey;
2. preserves the raw application capture;
3. extracts evidence frames used by presentation assets;
4. runs `tools/compose_guided_customer_story.py` to add explanatory composition;
5. validates the final MP4 by decoding it end to end;
6. makes the generated media available to the pitch/demo workflow.

The final media profile is H.264 constrained-baseline, `yuv420p`, constant 24 fps, AAC-LC and MP4 fast-start.

## Proof boundary

The video proves only the behavior visible in its recorded software environment. The authoritative current capability statement is [IMPLEMENTATION.md](../../IMPLEMENTATION.md), and the exact CI/test proof boundary is [E2E-ACCEPTANCE.md](../testing/E2E-ACCEPTANCE.md). Do not use this provenance file to claim physical hardware, production-network or release qualification.
