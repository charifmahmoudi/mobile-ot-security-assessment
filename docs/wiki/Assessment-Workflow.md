# Assessment Workflow

Use Atlas for a **bounded assessment question**, not for open-ended discovery.

A strong starting question is:

> **Does the installed control equipment in this process area match the inventory currently accepted by operations?**

## 1. Define and authorize the case

Before collection, record:

- customer and site;
- process area;
- assessment objective;
- scope and exclusions;
- permitted evidence methods;
- operating window and stop conditions;
- operational and security approvals;
- data retention and export constraints.

If the scope, target or authorization is unclear, do not collect.

## 2. Load the expected baseline

Import the customer inventory or other accepted source material.

Preserve it as **customer-declared state**. Normalization may support matching, but the original values remain distinguishable from observations collected during the assessment.

Useful fields include asset ID, name, vendor, model, serial, IP/MAC, process area and control role. Missing fields are acceptable; uncertainty is part of the assessment.

## 3. Collect the least intrusive useful evidence

Choose the evidence method that answers the documented gap:

| Situation | Method |
|---|---|
| Approved capture already exists | Analyze PCAP/PCAPNG offline |
| Approved SPAN/TAP visibility is available | Receive-only passive capture |
| One known Modbus asset needs stronger identity evidence | Exact authorized Device Identification |
| Evidence or authorization is insufficient | Stop and retain the gap |

Do not broaden collection simply because the first evidence source is incomplete.

## 4. Reconcile expected and observed state

Review observations before accepting identity conclusions.

For each expected record, determine whether the evidence supports a **confirmed**, **probable**, **conflicting**, **not-observed** or **unresolved** result. Observed endpoints with no accepted expected match remain **unexpected**.

The reconciliation should answer:

- what agrees;
- what conflicts;
- which evidence supports the decision;
- what remains uncertain;
- whether additional evidence is justified.

See [Asset Reconciliation](Asset-Reconciliation.md).

## 5. Review and deliver

Before final handoff:

- review material identity decisions;
- review evidence-linked findings;
- record visibility and method limitations;
- confirm any active operations performed;
- preserve unresolved questions rather than forcing closure;
- finalize the assessment revision and export the customer package.

A strong customer result is concise: **what was corroborated, what disagreed, what was unexpected, what was not observed, what remains unresolved, and why.**

## Stop conditions

Stop and escalate if:

- authorization or scope is uncertain;
- the intended target is outside the approved boundary;
- passive visibility is insufficient for the requested conclusion;
- observed behavior indicates operational instability;
- a safer or less intrusive evidence source should be used instead.

For exact network constraints, see [Safety & Technical Boundaries](Safety-and-Technical-Boundaries.md).