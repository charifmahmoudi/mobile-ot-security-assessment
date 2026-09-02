# Pilot Evaluation Guide

The purpose of an Atlas customer pilot is not merely to prove that the software runs. It is to determine whether Atlas gives the customer a more trustworthy and usable answer about a bounded OT asset baseline than the process they use today.

## Start with a measurable question

A good pilot begins with one concrete question, for example:

> **Does the installed control equipment in this process area support the inventory currently accepted by operations?**

Avoid making the pilot about “trying the scanner.” The value should be tied to a customer decision or reconciliation problem.

## Record the baseline

Before using Atlas, document the customer's current method where they permit it:

- source inventory or records;
- current tools;
- manual reconciliation steps;
- typical evidence sources;
- known uncertainty or review burden.

Do not invent a monetary ROI baseline if the customer has not supplied one.

## What to measure

Useful pilot measurements can include:

- preparation effort;
- field collection effort;
- reconciliation effort;
- review/report effort;
- equipment burden;
- repeat visits or rework required;
- expected records in scope;
- corroborated records;
- probable matches;
- material conflicts;
- unexpected observations;
- not-observed records;
- identity gaps investigated and resolved;
- unresolved questions;
- deployment or policy blockers.

The goal is to separate technical success from customer usefulness.

## Evaluate four dimensions separately

### 1. Technical execution

Did Atlas complete the authorized workflow safely and retain defensible evidence?

### 2. Methodological acceptance

Did the assessor/reviewer trust the distinction between customer declaration, evidence, reconciliation and findings?

### 3. Workflow usefulness

Did Atlas make it easier to identify and explain discrepancies than the customer's current approach?

### 4. Deployment fit

Were hardware, authorization, policy, support or operational constraints acceptable for real use?

A pilot can pass technically and still fail commercially if the workflow adds no meaningful value.

## End with an explicit decision

The evaluation should record a next step such as:

- **continue** — value is clear enough to proceed with further evaluation;
- **modify and retest** — the use case is promising but specific blockers must be corrected;
- **procurement investigation** — customer wants to evaluate acquisition/support/commercial steps;
- **stop** — the product or use case does not justify further effort.

Record the rationale, not only the label.

## A strong pilot result

A useful outcome sounds like:

> “We started with 18 expected records. Atlas corroborated 12, preserved 2 as probable, surfaced one material identity conflict and one unexpected endpoint, and showed that two expected records were not supported by the available evidence. One documented identity gap was resolved through an explicitly authorized Modbus identity request. The customer could trace each material conclusion to retained evidence.”

That is more meaningful than reporting how many IP addresses were discovered.

For the engineering release gate behind the pilot, see GitHub issue [#11 — customer-pilot-ready P0-WATER epic](https://github.com/charifmahmoudi/mobile-ot-security-assessment/issues/11).
