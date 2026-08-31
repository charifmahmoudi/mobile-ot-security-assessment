# Atlas OT Scout prospect pitch

This presentation is designed to secure agreement to a **free, bounded P0-WATER pilot** and make the commercial model unambiguous: Atlas delivers a configured appliance, guides the first assessment, leaves the device with the customer, and lets the customer decide from its own experience whether to acquire it or have it collected. Consulting and support packages are optional.

## Narrative

The 14-slide deck follows one customer from problem to improved operating state:

1. **Offer:** try the appliance before deciding to buy it.
2. **Current problem:** the customer has records, but not enough confidence that they still match field reality.
3. **Desired state:** a defensible baseline, explicit limitations, prioritized next actions and a repeatable assessment method.
4. **Commercial model:** deliver, use, decide, support.
5. **Guided workflow:** scope, collect, review, reason and hand off.
6. **Customer case:** one bounded water-treatment segment and one decision question.
7. **Passive first:** select the least intrusive evidence method.
8. **Review before mutation:** observations do not silently become inventory.
9. **Reconciliation:** expected, observed, missing and conflicting identities become explicit.
10. **Bounded active check:** one exact identity question, with out-of-scope rejection.
11. **Honest handoff:** findings and readiness blockers stay linked to evidence.
12. **Benefit:** move from “we have an inventory” to “we can show the evidence and know what to do next.”
13. **What exists / what the pilot proves:** software behavior is separated from physical and operational field qualification.
14. **Close:** select one bounded segment for the free pilot; acquire the appliance only after it proves useful.

## Files

- [PowerPoint](Atlas-OT-Scout-Pitch-and-Demo.pptx) — editable 14-slide prospect deck with the guided customer-story MP4 embedded.
- [PDF](Atlas-OT-Scout-Pitch-and-Demo.pdf) — portable 14-page export.
- [Guided customer-story video](../demo/atlas-ot-scout-emulator-demo.mp4) — real Android footage with explanatory captions, click indicators and paced holds.
- [Video story and provenance](../demo/VIDEO-SCRIPT.md) — exact narrative, business model and proof boundary.
- [`tools/build_pitch_deck.js`](../../tools/build_pitch_deck.js) — reproducible PptxGenJS source.
- [`tools/compose_guided_customer_story.py`](../../tools/compose_guided_customer_story.py) — reproducible customer-story video composition.

## Claim boundary

All application footage and screenshots come from Android CI/emulation. The active identity journey uses the signed Case App to Network Broker path against the controlled PyModbus target. The deck and video do not claim physical hardware, production-network or release qualification; the free pilot exists to test those field questions with the customer.
