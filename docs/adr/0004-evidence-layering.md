# ADR 0004: Preserve evidence, claims, assets and findings as separate layers

- Status: accepted for P0
- Date: 2026-08-30

## Context

If parser output directly updates an asset record, or a rule directly creates a final finding, the report cannot explain contradictions or reproduce the conclusion after packs change.

## Decision

Persist immutable layers:

1. sealed raw artifact;
2. normalized observation with artifact and byte range;
3. versioned identity claim with confidence and rule;
4. reviewed asset revision grouping endpoints/claims;
5. candidate finding with evidence;
6. reviewer decision;
7. immutable finalized snapshot;
8. signed report package.

No later layer overwrites an earlier one.

## Consequences

Reports are reproducible and conflicts remain visible. Storage and UI are more complex, and every transformation requires versioning. Finalization must materialize one immutable snapshot.
