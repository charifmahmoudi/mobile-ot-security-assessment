# Reconciling Assets

Reconciliation is the core Atlas workflow. It compares what the customer expected with what the assessment evidence supports, while preserving uncertainty and conflicts instead of hiding them inside an automatically merged inventory.

## Reconciliation states

Atlas should make at least these states understandable:

- **Confirmed** — evidence and review support the expected/observed match strongly enough for the accepted professional decision.
- **Probable** — evidence suggests a match, but an important identity element remains insufficient or uncertain.
- **Conflict** — material expected and observed attributes disagree.
- **Unexpected** — an observed OT endpoint has no accepted match in the expected baseline.
- **Not observed** — an expected record did not receive supporting evidence in the available sample.
- **Unresolved** — available evidence does not support a defensible reconciliation decision.

The exact domain statuses are defined in the repository model; the Wiki explains how to interpret them.

## Why `not observed` is not `absent`

A passive capture or bounded collection window cannot usually prove that an asset does not exist. It can only establish that the asset was not supported by the evidence available under the stated method and visibility.

Therefore:

> **Not observed = no adequate supporting observation in the available evidence.**

It does not mean:

> **The equipment is definitely absent from the site.**

That distinction should remain visible in both the working case and the final report.

## Review a proposed match

For each candidate, ask:

1. Which attributes agree?
2. Which attributes conflict?
3. How strong is each source?
4. Is the apparent match based only on weak identifiers such as IP, hostname or OUI?
5. Is there stronger physical, protocol or customer evidence?
6. What remains unresolved?

A useful reconciliation view should show both sides and the reason for the current status rather than only a confidence number.

## Weak identifiers

IP address, hostname, OUI and service presence can help generate candidates, but they do not independently establish precise device identity. IP reuse, duplicate MACs, stale records and renamed assets are common reasons to preserve uncertainty.

## Conflicts

Do not overwrite a customer field merely because a new observation disagrees with it. Preserve the conflict and the evidence behind both claims until a professional decision is made.

Examples:

- expected model differs from protocol identity;
- expected serial differs from a physical or protocol source;
- address matches but device identity does not;
- one observed endpoint appears to match multiple expected records.

## Decisions

Professional reconciliation decisions should retain:

- actor and role;
- time;
- rationale;
- relevant evidence references;
- the resulting status.

An assessor may confirm, reject, leave unresolved, classify an unexpected observation, or preserve a not-observed state. Merge/split decisions require the same level of explicit rationale.

## When to collect more evidence

Collect more only when a specific unresolved question justifies it. For example, a probable Modbus controller may have enough evidence to identify the vendor and address but not the exact model. If the exact target is authorized, a bounded Device Identification request may resolve that specific gap.

Next: [Reviewing Findings and Limitations](Reviewing-Findings-and-Limitations.md).

For normative reconciliation semantics, see the repository [Assessment Method](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/ASSESSMENT-METHOD.md).
