# Importing the Expected Inventory

Atlas treats the customer's expected inventory as a declared source, not as discovered truth. The purpose of import is to preserve what the customer currently relies on so it can be compared with independent evidence.

## What to import

A pilot baseline may include fields such as:

- customer asset ID or tag;
- name/description;
- asset type or control role;
- vendor;
- model;
- serial or other identifier;
- IP and MAC address where known;
- process area/location;
- source notes or freshness information.

Do not require fields the customer does not have. Missing data is useful context because it identifies evidence gaps.

## Preserve the original declaration

The imported source should remain immutable. Normalized values may be created to support comparison, but they must remain distinguishable from the customer's original values.

For example:

| Customer declaration | Normalized comparison value |
|---|---|
| `Schneider BMX-P34 2020` | vendor=`Schneider Electric`, model=`BMX P34 2020` |

The normalized value helps matching; it does not rewrite the source record.

## Validate before acceptance

A professional import flow should provide:

- file identity and hash;
- preview;
- explicit column mapping;
- per-row validation errors;
- clear handling of rows outside scope;
- preservation of the source/mapping provenance.

A malformed row should not invalidate unrelated valid records unless the assessor chooses to reject the whole import.

## Scope the baseline

Only records relevant to the authorized assessment unit should enter the working comparison set. Preserve excluded/out-of-scope rows or their source context as required, but do not imply they were assessed.

## After import

The expected inventory answers:

> **What does the customer currently expect to exist?**

It does not answer:

> **What did Atlas observe?**

That distinction remains visible through the entire assessment.

Next: [Collecting Evidence](Collecting-Evidence.md) and [Reconciling Assets](Reconciling-Assets.md).

For normative data semantics, use the repository's [Evidence Data Model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/EVIDENCE-DATA-MODEL.md).
