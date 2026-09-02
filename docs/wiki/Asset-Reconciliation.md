# Asset Reconciliation

Reconciliation is the core Atlas decision model. It compares the customer's expected asset state with the evidence collected during the assessment without silently converting weak observations into accepted inventory.

## Reconciliation states

| State | Use when |
|---|---|
| **Confirmed** | Evidence and review support the expected/observed match |
| **Probable** | A likely match exists, but important identity evidence is incomplete |
| **Conflict** | Material expected and observed attributes disagree |
| **Unexpected** | An observed OT endpoint has no accepted expected-record match |
| **Not observed** | An expected record has no adequate supporting evidence in the available sample |
| **Unresolved** | Available evidence does not support a defensible decision |

## Decision rules

A reconciliation decision should be explainable from the evidence.

Strong identity evidence can include exact serial numbers or protocol device identifiers. MAC address, model combinations and other stable attributes can support a match. IP address, hostname, OUI and service presence are useful candidate signals, but are weak identity evidence on their own.

Do not overwrite a customer record because a new observation disagrees with it. Preserve the conflict until it is reviewed.

## Example

| Expected record | Observed evidence | Decision |
|---|---|---|
| PLC-01 — Schneider M340 | Exact device identity agrees | **Confirmed** |
| PLC-02 — Schneider M340 | IP and vendor agree; exact model missing | **Probable** |
| RTU-04 — Model A | Protocol identity reports Model B | **Conflict** |
| HMI-03 | No supporting observation in the available sample | **Not observed** |
| — | OT endpoint observed with no accepted baseline match | **Unexpected** |

The key distinction is that **not observed does not mean absent**. A bounded capture can establish what was seen under the available visibility; it usually cannot prove that an expected asset does not exist.

## When to collect more evidence

Collect more only to answer a specific unresolved question.

For example, if a Modbus controller is probable because vendor and address agree but exact identity is missing, an explicitly authorized Device Identification request may provide stronger evidence. The result becomes additional evidence; the assessor still reviews the match before confirming it.

## What belongs in the customer result

The customer should be able to see:

- the expected record;
- the observed evidence;
- the reconciliation state;
- the reason for that state;
- material conflicts;
- unresolved questions;
- evidence references and limitations.

The normative assessment semantics are defined in the repository [Assessment Method](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/ASSESSMENT-METHOD.md).