# Atlas OT Scout buyer pitch

This ten-slide deck is built around one decision: **can an authorized water-OT assessor turn a bounded evidence window into a handoff they can defend?**

It is intentionally not a feature catalogue. The narrative follows the same user story as the live emulator recording.

## Buyer narrative

1. Start with the cost of uncertainty, not scanner coverage.
2. Frame the assessment as five decisions: context, collect, review, reason and handoff.
3. Show passive evidence entering a review queue before it can change inventory.
4. Show the local fail-closed boundary for an out-of-scope active target.
5. Show one exact authorized Modbus identity request against the controlled PyModbus CI target.
6. Show the resulting inventory and explicit report-readiness blockers.
7. Keep the Morocco commercial scope limited to drinking-water and wastewater operations.
8. State what CI proves and what still requires physical field qualification.
9. Close on one bounded water-segment pilot with observable success criteria.

## Files

- [Atlas-OT-Scout-Pitch-and-Demo.pptx](Atlas-OT-Scout-Pitch-and-Demo.pptx) - editable ten-slide buyer presentation with speaker notes and source references.
- [Atlas-OT-Scout-Pitch-and-Demo.pdf](Atlas-OT-Scout-Pitch-and-Demo.pdf) - portable PDF export of the same deck.
- [Live user-story screen recording](../demo/atlas-ot-scout-emulator-demo.mp4) - continuous Android API 35 `adb screenrecord` capture of the running application.
- [Video storyline and provenance](../demo/VIDEO-SCRIPT.md) - exact recorded behavior, proof boundary and reproduction workflow.
- [`tools/build_pitch_deck.js`](../../tools/build_pitch_deck.js) - reproducible PptxGenJS source for the deck.

## Claim boundary

All application imagery is captured from Android API 35 CI/emulator runs. The active identity screen is produced by the signed application-to-Network-Broker journey against the controlled PyModbus testbed. CI also exercises modbus-tk and Conpot.

The deck does **not** claim physical Samsung, USB-Ethernet, physical SPAN/TAP, real PLC firmware, production-network, encrypted-case-vault or signed-final-report qualification.
