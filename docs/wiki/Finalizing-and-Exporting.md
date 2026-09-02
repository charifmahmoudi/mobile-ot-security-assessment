# Finalizing and Exporting

Finalization converts a reviewed working case into an immutable assessment revision. The final package should be generated from that frozen state, not from mutable screens or working tables.

## Before finalization

Confirm that:

- collection has stopped;
- the authorization and scope used for the assessment are retained;
- material reconciliation decisions are reviewed;
- important conflicts and unresolved items remain visible;
- required findings have reviewer decisions;
- limitations are complete enough to prevent overinterpretation;
- the audit trail and professional case state are valid.

Finalization should be blocked when a required professional gate is incomplete.

## Immutable revision

Once a case revision is finalized, later corrections should create a new revision that supersedes the previous one. The delivered record should not silently change in place.

This provides a clear answer to:

> **Which exact assessment did the customer receive?**

## Customer package

The pilot target is a package that can be consumed without the Android application. It is expected to contain forms such as:

- human-readable assessment output;
- machine-readable assessment data;
- reconciled asset state;
- exception list;
- manifest/hashes and verification information;
- the exact revision/build/pack identities needed for reproducibility.

The authoritative P0 deliverable is defined in the repository [P0-WATER contract](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/WATER-WASTEWATER-POC.md). Current export capability is defined only in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md).

## What the customer should be able to understand

A useful final assessment clearly answers:

1. What question was assessed?
2. What was in scope and excluded?
3. Which evidence methods were used?
4. What expected-vs-observed discrepancies were found?
5. Which findings were accepted?
6. Which questions remain unresolved?
7. What limitations constrain the conclusions?
8. What packet-producing actions were performed?
9. Which assessor/reviewer approved the final revision?
10. How can package integrity be checked?

## Raw evidence

Raw captures or other sensitive artifacts should be included only when the case data policy and customer authorization permit export. A report can reference retained artifact hashes without automatically embedding every raw source file.

## Verification

The pilot target includes external package verification so a recipient can detect missing or modified files and validate the delivered revision without needing the Atlas application.

Do not treat a hash displayed in a report as equivalent to a complete release verification design; use the repository implementation and testing documents for the exact supported verification mechanism.

See also [Evidence and Provenance](Evidence-and-Provenance.md) and [Pilot Evaluation Guide](Pilot-Evaluation-Guide.md).
