# Atlas OT Scout prospect pitch

This deck is designed for a prospect conversation with one objective: **secure agreement to a no-fee, bounded P0-WATER field pilot**.

It is not a feature catalogue. The presentation first explains the problem and the project, then shows what is implemented, how the assessment works, why the safety architecture is deliberately constrained, what the prototype proves today, and why a field pilot is the logical next step.

## Buyer narrative

1. **Free pilot proposition** - one bounded treatment or pumping segment, passive first, no control writes.
2. **Why this project exists** - inventories drift, intrusive discovery is hard to justify, and scanner output alone is not a defensible handoff.
3. **What Atlas OT Scout is** - an offline-first Android field instrument for authorized water/wastewater OT assessment.
4. **What exists today** - Case App, Capture Broker, isolated parser, Network Broker, guided UI, passive evidence handling and one-target Modbus identity.
5. **How an assessment works** - scope, collect, review, reconcile, close one evidence gap if necessary, then hand off with explicit limitations.
6. **Passive-first workflow** - sealed evidence, isolated parsing and analyst acceptance before inventory changes.
7. **Bounded active workflow** - exact target and CIDR, one compiled identity request, local fail-closed behavior and no discovery sweep.
8. **Pilot output** - reconciled inventory, evidence provenance, draft findings and readiness/gap view.
9. **Safety architecture** - least-authority separation prevents the general app from becoming an unrestricted network client.
10. **Real workflow video** - the compatible MP4 is embedded directly in the PowerPoint; the PDF shows its cover frame.
11. **Proven vs. field qualification** - software evidence is separated from the physical/operational questions the pilot must answer.
12. **Proposed free pilot** - what the customer provides and what they receive.
13. **Success criteria / close** - run one pilot, then decide from the evidence rather than from promises.

## Files

- [Atlas-OT-Scout-Pitch-and-Demo.pptx](Atlas-OT-Scout-Pitch-and-Demo.pptx) - editable prospect presentation with the actual user-story MP4 embedded on the demo slide.
- [Atlas-OT-Scout-Pitch-and-Demo.pdf](Atlas-OT-Scout-Pitch-and-Demo.pdf) - portable 13-page export; video becomes a static cover frame.
- [Live user-story screen recording](../demo/atlas-ot-scout-emulator-demo.mp4) - continuous Android API 35 screen capture of the running application.
- [Video storyline and provenance](../demo/VIDEO-SCRIPT.md) - exact recorded behavior, proof boundary and reproduction workflow.
- [`tools/build_pitch_deck.js`](../../tools/build_pitch_deck.js) - reproducible PptxGenJS source.

## Claim boundary

All application imagery comes from Android CI/emulation. The active identity screen is produced by the signed Case App to Network Broker journey against the controlled PyModbus target. CI also exercises modbus-tk and Conpot.

The presentation does **not** claim physical Samsung/USB-Ethernet/SPAN-TAP qualification, production packet-rate performance, real-PLC compatibility, encrypted production case storage, reviewer signatures or a production signed-report package. Those are explicit field/release gates and are part of the rationale for the pilot.
