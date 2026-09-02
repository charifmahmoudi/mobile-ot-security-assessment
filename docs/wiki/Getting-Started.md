# Getting Started

Atlas assessments are deliberately bounded. Before touching a customer network, establish the assessment question, authorized process area and permitted evidence methods.

## Before you begin

Confirm that you have:

- an identified customer/legal entity and site;
- one named process area or control segment;
- a concrete assessment question;
- the expected inventory or other approved baseline material, if available;
- operational and security approval appropriate to the planned methods;
- agreed exclusions, stop conditions, retention and export rules;
- a supported Atlas build and, for field collection, a qualified hardware combination.

If any of these are unclear, preparation is not complete.

## Recommended first assessment question

A strong first pilot question is narrow and decision-oriented, for example:

> **Does the installed control equipment in Pumping Area A match the inventory currently accepted by operations?**

That is better than a vague goal such as “scan the OT network.”

## Assessment sequence

1. **Prepare** — define customer/site/process context, objective, scope, exclusions, methods and data handling.
2. **Authorize** — obtain the required operational and security approvals.
3. **Establish the expected state** — import or enter the customer baseline without converting it into discovered fact.
4. **Collect evidence** — use the least intrusive approved method that can answer the documented gap.
5. **Review observations** — raw observations do not automatically become accepted assets.
6. **Reconcile** — explicitly handle confirmed, probable, conflicting, unexpected, not-observed and unresolved records.
7. **Assess** — create findings only when evidence is sufficient.
8. **Review and finalize** — independent review precedes immutable finalization and export.

See the [Pilot Assessment Walkthrough](Pilot-Assessment-Walkthrough) for a worked example.

## Evidence methods

Atlas's P0 model distinguishes three evidence routes relevant to the first customer pilot:

- **Offline capture analysis** — customer- or lab-supplied PCAP/PCAPNG; no packet transmission.
- **Approved live passive capture** — receive-only evidence from a qualified SPAN/TAP path.
- **Exact Modbus identity** — one bounded Device Identification request to one explicitly authorized target when a documented identity gap justifies it.

Do not broaden collection because the initial evidence is incomplete. Insufficient evidence is a valid assessment result.

## Current capability

The application and pilot workflow are evolving. Do not infer field readiness from this Wiki page. Current executable behavior is maintained in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md), and physical support is established only by the measured [compatibility matrix](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/appliance/COMPATIBILITY-MATRIX.md).

## Next steps

- [Prepare and authorize a case](Preparing-and-Authorizing-a-Case)
- [Import the expected inventory](Importing-the-Expected-Inventory)
- [Collect evidence](Collecting-Evidence)
- [Reconcile assets](Reconciling-Assets)
