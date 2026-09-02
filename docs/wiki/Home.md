# Atlas OT Scout

Atlas OT Scout is a portable, offline-oriented OT evidence and reconciliation tool for bounded industrial assessments. The first product focus is water and wastewater control environments.

Atlas is designed around a simple professional question:

> **Does the equipment and evidence observed in this authorized control area support the asset baseline the customer currently relies on, and which discrepancies still require action?**

Rather than treating network discovery as an inventory by itself, Atlas keeps customer declarations, raw evidence, observations, identity claims, reconciliation decisions and findings distinct. The assessor can therefore explain not only *what* Atlas concluded, but *why*.

## Pilot workflow

A bounded assessment follows this path:

**Prepare and authorize → import the expected inventory → collect or import evidence → review observations → reconcile expected and observed state → resolve selected identity gaps safely → review findings and limitations → finalize and export.**

Start with the [Pilot Assessment Walkthrough](Pilot-Assessment-Walkthrough.md) for the complete story.

## What Atlas is intended to deliver

For one authorized process area, the customer should be able to see:

- which expected assets are corroborated by evidence;
- which matches remain probable rather than confirmed;
- which attributes conflict;
- which observed endpoints are unexpected;
- which expected records were not observed in the available evidence;
- what remains unresolved;
- which bounded active identity operations were performed;
- what limitations affect the conclusions;
- which findings were accepted after review.

The final goal is a reviewed assessment package, not a scanner device count.

## Choose your path

**Assessors and operators**

- [Getting Started](Getting-Started.md)
- [Preparing and Authorizing a Case](Preparing-and-Authorizing-a-Case.md)
- [Collecting Evidence](Collecting-Evidence.md)
- [Reconciling Assets](Reconciling-Assets.md)
- [Reviewing Findings and Limitations](Reviewing-Findings-and-Limitations.md)

**Customers and evaluators**

- [Pilot Assessment Walkthrough](Pilot-Assessment-Walkthrough.md)
- [Evidence and Provenance](Evidence-and-Provenance.md)
- [Pilot Evaluation Guide](Pilot-Evaluation-Guide.md)
- [FAQ and Glossary](FAQ-and-Glossary.md)

**Technical readers**

- [Technical Overview](Technical-Overview.md)
- [Field Setup and Safety](Field-Setup-and-Safety.md)

## Important status boundary

The Wiki explains the product and intended pilot workflow. It is **not** the authority for what is implemented or field-qualified today.

Before using Atlas in a demo, lab or field setting, check the repository's current [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md) and measured [`COMPATIBILITY-MATRIX.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/appliance/COMPATIBILITY-MATRIX.md).

For exact product and safety contracts, use the repository documentation linked from [Technical Overview](Technical-Overview.md).
