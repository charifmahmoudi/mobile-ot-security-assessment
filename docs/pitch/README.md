# Atlas OT Scout pitch and demonstration deck

The editable ten-slide deck is designed for industrial buyers, qualified audit providers and technical decision makers evaluating a controlled P0-WATER pilot.

## Narrative

1. Define the field problem as a professional decision problem.
2. Show the complete assessment journey from context to controlled handoff.
3. Demonstrate site setup, passive-first collection, active guardrails, inventory reasoning and report readiness with original emulator screenshots.
4. Separate what automated emulation proves from the physical-hardware validation still required.
5. Close with a bounded, witnessed pilot proposal.

## Files

- [Atlas-OT-Scout-Pitch-and-Demo.pptx](Atlas-OT-Scout-Pitch-and-Demo.pptx) — editable presentation with speaker notes and source references.
- [Atlas-OT-Scout-Pitch-and-Demo.pdf](Atlas-OT-Scout-Pitch-and-Demo.pdf) — portable ten-page export of the same deck.
- [Live emulator demonstration video](../demo/atlas-ot-scout-emulator-demo.mp4) — continuous Android API 35 screen recording, not a screenshot slideshow.
- [Video storyline and live-capture instructions](../demo/VIDEO-SCRIPT.md) — provenance, talk track and reproducible `adb screenrecord` workflow.

## Claim boundary

All application screens come from the Android API 35 emulator. Automated CI covers API 29 and API 35, passive protocol fixtures, native capture tests and active interoperability with PyModbus, modbus-tk and Conpot. The deck does not claim validation against physical Samsung hardware, USB Ethernet, a physical TAP, real PLC firmware or a production OT segment.
