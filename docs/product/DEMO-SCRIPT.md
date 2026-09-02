# Atlas OT Scout demonstration script

Use this as the live presenter guide. It owns **demo sequencing and presenter language**, not product status or architecture definitions. Before presenting, verify [IMPLEMENTATION.md](../../IMPLEMENTATION.md) and do not demonstrate a path that is not available in the chosen environment.

## Setup

- Use matching application/broker artifacts from the selected successful test build.
- Keep the approved sample capture available.
- If demonstrating active identity, use the controlled target and exact scope prepared for the demo.
- Reset to the site-selection screen.

## 1. Establish context

Open the sample water site.

Say: “OT evidence is only useful when it belongs to a known site, process area and decision. We start with context, not a subnet sweep.”

Show that sample data is visibly distinguished from field evidence.

## 2. Show the working model

On **Overview**, point to the five work areas and the recommended next decision.

Say: “The application separates what is known from what still needs review. The next action is driven by an evidence gap, not by a generic scan button.”

Open one inventory review item, show provenance/confidence, then return.

## 3. Choose evidence deliberately

Open **Collect**.

Say: “The assessor chooses the least intrusive method that can answer the question. Imported evidence does not transmit; an active identity check is tied to one exact authorization.”

Do not describe disabled/planned controls as available capability.

## 4. Passive evidence

Import the sample capture and show:

- source/hash and time/packet context;
- proposed observations;
- protocol/role evidence;
- explicit analyst selection before inventory mutation.

Say: “These are observations from this visibility window, not a claim about the entire network.”

Add only the reviewed sample observations.

## 5. Exact active identity

If the active demo path is available in the chosen environment, open the exact-target identity workflow and show the authorization/scope values before execution.

Say: “The user does not get a generic scanner. The active path asks the Network Broker for the single operation defined by the network contract.”

Show an out-of-scope rejection if that is part of the prepared deterministic demo, then use the correct controlled target.

Exact cryptographic/packet details are defined in [NETWORK-EXECUTION.md](../architecture/NETWORK-EXECUTION.md); do not improvise different limits in presenter narration.

## 6. Reason rather than count

Return to **Assets** and inspect how evidence changed the working model.

Say: “The useful output is not a device count. It is a reviewable model showing what was observed, what supports an identity, what conflicts and what remains unresolved.”

## 7. Handoff

Open **Findings** and **Report**.

Say: “A condition stays linked to evidence and review. The product should not issue a professional handoff merely because collection completed.”

Use report-readiness blockers to close on the remaining decisions.

## Common questions

| Question | Presenter answer |
|---|---|
| Does Ethernet let the phone sniff an entire switched network? | No. Whole-segment third-party visibility depends on an approved SPAN/mirror or passive TAP source; see the canonical capture architecture. |
| Does active mode scan a subnet? | No. P0's initial active contract is one exact authorized Modbus basic identity operation. |
| Is Atlas a certification or penetration-test product? | No. The P0 product contract explicitly excludes those claims. |
| Are inferred roles or identities automatically facts? | No. Observations/claims require review under the assessment method. |
| Is the physical appliance qualified? | Answer from [IMPLEMENTATION.md](../../IMPLEMENTATION.md) and the [compatibility matrix](../appliance/COMPATIBILITY-MATRIX.md), not from memory or this script. |
| What are the evaluation terms? | Use the canonical [evaluation and services offer](../business-development/OFFER.md). |

## Presenter rule

If a question asks **what currently works**, open [IMPLEMENTATION.md](../../IMPLEMENTATION.md). If it asks **what P0 is supposed to do**, use the [P0 contract](../poc/WATER-WASTEWATER-POC.md). If it asks **how packets are constrained**, use [NETWORK-EXECUTION.md](../architecture/NETWORK-EXECUTION.md).
