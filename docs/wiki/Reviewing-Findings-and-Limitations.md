# Reviewing Findings and Limitations

Atlas findings are professional conclusions built from reviewed evidence. They are not raw parser alerts and they should not exaggerate what the available evidence can establish.

## Finding quality

A useful finding should answer:

- What condition was observed?
- Which asset, endpoint or assessment object is affected?
- Which evidence supports the conclusion?
- How confident is the evidence interpretation?
- What important limitations apply?
- What operational consequence is known or supplied by the customer?
- What follow-up action is reasonable?

## Confidence is not consequence

Evidence confidence answers:

> **How strongly does the evidence support this technical conclusion?**

Operational consequence answers:

> **What would this condition mean for the actual process if it matters?**

They are different concepts. Atlas should not infer process safety, production impact or business severity from network traffic alone.

## Examples of bounded pilot findings

The initial water/wastewater pilot focuses on conditions that are directly supported by the evidence/reconciliation workflow, such as:

- an expected asset without adequate supporting observation;
- an unexpected OT endpoint;
- a material identity conflict;
- a stale accepted record when the required customer context is available;
- relevant cleartext Modbus communication observed within scope;
- evidence visibility insufficient for the requested conclusion.

A visible service or protocol is not automatically a vulnerability.

## Independent review

Material findings and identity claims should be challengeable by a reviewer who can inspect the underlying evidence and decision history.

A reviewer should be able to:

- accept the conclusion;
- reject it;
- return it or request more evidence;
- preserve a material conflict or limitation rather than allowing it to disappear for a cleaner report.

Rejected findings must not contribute to final executive totals.

## Limitations belong in the result

Examples of material limitations include:

- capture represented only one approved collection point;
- passive collection ran for a bounded duration;
- packet drops or incomplete capture affected visibility;
- an expected device was not observed but could not be declared absent;
- active identity was authorized only for selected targets;
- no vulnerability exploitation, credential testing or process register operation was performed.

Limitations should remain visible in both detailed and executive-facing output where they materially affect interpretation.

## Exact assessment semantics

For evidence thresholds, confidence, reportability and review requirements, use the repository's [Assessment Method](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/ASSESSMENT-METHOD.md) and [P0-WATER contract](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/WATER-WASTEWATER-POC.md).

Next: [Finalizing and Exporting](Finalizing-and-Exporting.md).
