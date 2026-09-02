# Atlas Wiki source

This directory is the maintained source for the reader-facing Atlas OT Scout GitHub Wiki.

> **The Wiki explains Atlas. The repository defines Atlas.**

Wiki pages are task-oriented explanations for assessors, evaluators, customers and technical readers. They must not become a second authority for requirements, implementation status, network mechanics, assessment semantics, security controls, release evidence or commercial terms.

## Publishing status

The GitHub Wiki is not initialized yet. These files are therefore maintained in the main repository so they can be reviewed, versioned and published once the Wiki is enabled. `Home.md` and `_Sidebar.md` use GitHub Wiki-compatible page names and links.

## Source-of-truth rules

- Current executable capability: [`IMPLEMENTATION.md`](../../IMPLEMENTATION.md).
- Product requirements: [`docs/REQUIREMENTS.md`](../REQUIREMENTS.md).
- P0-WATER contract: [`docs/poc/WATER-WASTEWATER-POC.md`](../poc/WATER-WASTEWATER-POC.md).
- Assessment semantics: [`docs/poc/ASSESSMENT-METHOD.md`](../poc/ASSESSMENT-METHOD.md).
- Exact packet-producing/receiving behavior: [`docs/architecture/NETWORK-EXECUTION.md`](../architecture/NETWORK-EXECUTION.md).
- Professional case lifecycle: [`docs/architecture/PROFESSIONAL-CASE-MODEL.md`](../architecture/PROFESSIONAL-CASE-MODEL.md).
- Testing/release proof: [`docs/testing/README.md`](../testing/README.md) and [`docs/poc/TEST-AND-ACCEPTANCE.md`](../poc/TEST-AND-ACCEPTANCE.md).
- Physical support claims: [`docs/appliance/COMPATIBILITY-MATRIX.md`](../appliance/COMPATIBILITY-MATRIX.md).

When a Wiki explanation conflicts with one of those authorities, the repository authority wins.

## Page set

- `Home.md`
- `Getting-Started.md`
- `Pilot-Assessment-Walkthrough.md`
- `Preparing-and-Authorizing-a-Case.md`
- `Importing-the-Expected-Inventory.md`
- `Collecting-Evidence.md`
- `Reconciling-Assets.md`
- `Reviewing-Findings-and-Limitations.md`
- `Finalizing-and-Exporting.md`
- `Evidence-and-Provenance.md`
- `Field-Setup-and-Safety.md`
- `Pilot-Evaluation-Guide.md`
- `Technical-Overview.md`
- `FAQ-and-Glossary.md`
- `_Sidebar.md`

## Editorial standard

A Wiki page should answer a reader question, show decisions and limitations, and link to the exact authority when precision matters. Avoid copying mutable status tables, protocol constants, cryptographic details, issue backlogs, prospect intelligence or commercial evidence into the Wiki.
