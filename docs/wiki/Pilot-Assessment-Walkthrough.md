# Pilot Assessment Walkthrough

This walkthrough shows the intended shape of one bounded Atlas customer assessment. It is a product explanation, not a claim that every step is field-ready in the current build; verify current capability in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md).

## Scenario

A water operator wants to understand whether the equipment installed in one pumping control area matches the asset inventory currently used by operations and engineering.

The customer provides an expected inventory and authorizes a bounded assessment of one control segment. Atlas is used to compare that declared baseline with approved evidence.

## 1. Define the question

The assessor creates a professional case for:

- Customer: Example Water Utility
- Site: North Pumping Station
- Process area: Booster Control
- Question: **Does installed control equipment match the accepted asset baseline?**

Scope, exclusions, collection methods, operating window, stop conditions and data handling are agreed before protected collection begins.

## 2. Import the expected baseline

The customer supplies an inventory containing 18 in-scope records.

Atlas preserves the source values as customer-declared information. Normalization can help compare records, but it does not rewrite the original declaration.

## 3. Collect or import evidence

The assessor uses approved evidence such as a customer-supplied capture or a qualified passive SPAN/TAP path. Atlas parses the evidence into observations and identity claims without automatically changing accepted inventory.

A representative reconciliation state might be:

| Status | Count | Meaning |
|---|---:|---|
| Confirmed/corroborated | 12 | Evidence supports the expected record strongly enough for the accepted decision |
| Probable | 2 | Evidence suggests a match but is not sufficient for confirmation |
| Conflict | 1 | Material expected and observed identity attributes disagree |
| Unexpected | 1 | An observed OT endpoint has no accepted expected-record match |
| Not observed | 2 | Expected records lacked supporting evidence in the available sample |

These numbers are the project's Golden Customer Assessment test fixture, not a customer benchmark.

## 4. Investigate a specific evidence gap

One probable asset lacks exact identity evidence. If the target is explicitly authorized and the current build admits the operation, the assessor can use Atlas's bounded Modbus Device Identification method.

Atlas shows the exact target and network effect before execution. The result becomes new evidence; it does not silently accept the asset match. The assessor reviews the evidence and then confirms or leaves the reconciliation unresolved.

## 5. Review discrepancies

The customer can inspect why each exception exists:

- **Conflict:** declared model or serial differs from observed evidence.
- **Unexpected:** evidence shows an OT endpoint absent from the expected baseline.
- **Not observed:** the expected asset was not supported by the available evidence sample; this is not proof that the asset is absent.
- **Probable:** available identifiers are suggestive but insufficient for confirmation.

Each professional decision retains evidence and rationale.

## 6. Review findings and limitations

Findings are created only when evidence is sufficient. Confidence in the evidence remains separate from operational consequence.

The assessment also records important limitations, for example:

> Passive evidence represents a bounded SPAN sample. “Not observed” does not mean absent. No broad scan, credential test, exploit or process register operation was performed.

## 7. Finalize and hand off

After independent review, Atlas freezes the assessment revision and produces a customer package containing the reconciled asset state, exceptions, accepted findings, limitations, authorization context, active-operation ledger and integrity information.

The intended customer takeaway is not “Atlas found N IP addresses.” It is:

> **Here is the evidence-backed state of the inventory you started with, the discrepancies we could substantiate, the identity gaps we investigated, and the questions that remain unresolved.**

## Continue

- [Preparing and Authorizing a Case](Preparing-and-Authorizing-a-Case)
- [Reconciling Assets](Reconciling-Assets)
- [Finalizing and Exporting](Finalizing-and-Exporting)
- [Pilot Evaluation Guide](Pilot-Evaluation-Guide)
