# Professional case model

_Status: normative case-lifecycle/domain contract for P0-WATER. Current executable coverage is reported only in [IMPLEMENTATION.md](../../IMPLEMENTATION.md). Physical persistence, artifact encryption and report materialization are specified separately in [EVIDENCE-DATA-MODEL.md](EVIDENCE-DATA-MODEL.md)._

The professional case is the aggregate root for an Atlas assessment. A site is context inside a case; it is not the professional record by itself. One case binds the assessment question, legal/physical context, scope, approvals, evidence methods, data handling, professional decisions, audit history and final revision.

## 1. Aggregate boundary

```text
AssessmentCase
├── identity: case ID, case number, revision, superseded snapshot
├── context: legal entity, site, process area, assessment pack
├── objective: question, requested decision, stakeholder, success criteria, evidence needs
├── scope: CIDRs, exclusions, methods, operations, interfaces/capture points/physical areas
├── data policy: classification, payload/raw-capture policy, export destination, deletion date
├── stop conditions
├── authorization: artifact hash, window, exact scope/data-policy fingerprints, approvals
├── review decision
├── append-only audit trail
└── finalized snapshot reference/material hashes
```

Evidence records are case-owned but are separate semantic layers. `CaseRecords.kt` provides typed records for sealed artifacts, expected records, observations, identity claims, reconciliation decisions, findings and object review decisions. A later layer does not overwrite an earlier one.

## 2. Typed identity

The domain uses distinct typed identifiers for cases, actors, authorizations, snapshots, artifacts, expected records, observations, claims, reconciliation decisions, findings and reviews. Storage/export adapters may serialize these as strings, but application code must not treat identifiers for different professional objects as interchangeable.

Every case has:

- a stable `caseNumber` representing the engagement lineage;
- a positive `revision`;
- a unique `CaseId` for that revision;
- for revision >1, the `SnapshotId` it supersedes.

A correction does not rewrite a finalized revision.

## 3. Lifecycle

```mermaid
stateDiagram-v2
  [*] --> DRAFT
  DRAFT --> PREPARED
  PREPARED --> AWAITING_AUTHORIZATION
  AWAITING_AUTHORIZATION --> AUTHORIZED
  AUTHORIZED --> COLLECTING
  COLLECTING --> PAUSED
  PAUSED --> COLLECTING
  COLLECTING --> EVIDENCE_REVIEW
  PAUSED --> EVIDENCE_REVIEW
  EVIDENCE_REVIEW --> RECONCILING
  RECONCILING --> ASSESSING
  ASSESSING --> REVIEW_PENDING
  REVIEW_PENDING --> ASSESSING: changes required
  REVIEW_PENDING --> READY_TO_FINALIZE: accepted
  READY_TO_FINALIZE --> FINALIZED
  FINALIZED --> SUPERSEDED: new revision created
```

`CANCELLED` and `EXPIRED` are terminal stop states for unfinished work. `FINALIZED` cannot resume collection or semantic mutation. Supersession appends lineage metadata to the previous case and creates a new `DRAFT` revision; the previous finalized snapshot remains unchanged.

## 4. Transition guards

The domain layer, not the UI, owns lifecycle guards.

### Prepare

A P0 case cannot become `PREPARED` unless it has:

- legal entity, site, process area and assessment pack;
- a decision-oriented assessment objective;
- at least one explicit collection boundary;
- at least one evidence method;
- explicit stop conditions;
- a data classification/policy;
- for H1, an exact CIDR scope and at least one admitted operation;
- for H2, an approved capture point.

For `P0-WATER`, the domain currently refuses active-operation scope containing anything other than `MODBUS_DEVICE_ID_BASIC`.

### Authorization

Authorization is a first-class professional object. It records:

- authorization ID and immutable authorization-artifact SHA-256;
- validity window;
- exact scope fingerprint;
- exact data-policy fingerprint;
- operational and security approvals with actor identities/roles/times.

A case cannot become `AUTHORIZED` if the authorization is expired, contains future approvals, or is bound to a different scope or data policy.

### Collection

Collection may start only in `AUTHORIZED` or `PAUSED` state, by an assessor, inside the authorization window. The case-domain operation guard additionally requires:

- `COLLECTING` state;
- operation admitted by case scope;
- target inside an allowed CIDR;
- target outside explicit exclusions.

This guard complements, rather than replaces, the signed execution-grant enforcement in [NETWORK-EXECUTION.md](NETWORK-EXECUTION.md).

### Review/finalization

The assessor moves reviewed evidence through reconciliation and assessment. Finalization requires:

1. case-level review requested;
2. an independent actor acting as `REVIEWER` records `ACCEPTED`;
3. the same reviewer finalizes the accepted revision;
4. finalization material identifies professional object hashes, tool build and content-pack hashes;
5. a `CASE_FINALIZED` audit event is appended;
6. the snapshot records the resulting audit-chain head.

A review returned as `CHANGES_REQUIRED` moves the case back to `ASSESSING`; it does not create a clean final report by deleting the review history.

## 5. Professional roles

The executable domain roles are:

| Role | Domain authority |
|---|---|
| `ASSESSOR` | Create/prepare case, request/record authorization, collect, reconcile, assess, request review, create successor revision |
| `OPERATIONAL_APPROVER` | Provide required authorization approval; may stop/pause collection |
| `SECURITY_APPROVER` | Provide required authorization approval; may stop/pause collection |
| `REVIEWER` | Accept/return the professional case review and finalize an accepted revision |
| `PACK_ADMINISTRATOR` | Supporting system/content role; not a substitute for assessor/approver/reviewer authority |

One human may hold more than one role when customer policy permits, but every professional action records the role under which it was performed.

## 6. Assessment objective

A professional case explicitly records the decision it is intended to support:

```text
question
requestedDecision
stakeholderRole
successCriteria[]
evidenceNeeded[]
```

This keeps collection tied to an evidence question rather than a generic scan. The objective is product/domain context; whether Atlas materially improves the customer's workflow remains a hypothesis to measure in evaluations.

## 7. Append-only audit chain

Material lifecycle actions create canonical audit events containing:

```text
sequence
case_id
UTC time
actor_id + actor_role
event type
object type + object id
details hash
previous event hash
event hash
```

`event_hash` is SHA-256 over a length-prefixed canonical representation of the event plus the previous hash. `AuditTrail.restore()` refuses a sequence/hash/previous-hash mismatch. The implemented chain detects alteration of retained/exported history; it does not by itself prevent physical deletion of the local database. External anchoring belongs to the signed finalized package.

## 8. Evidence/decision separation

The core domain now has separate typed records for:

```text
SealedArtifact
ExpectedRecord
ObservationRecord
IdentityClaim
ReconciliationDecision
FindingRecord
ObjectReviewDecision
```

Important invariants include:

- expected customer fields are preserved separately from normalized values;
- byte-range provenance cannot exist without an artifact reference;
- an identity claim cannot exist without evidence;
- claim confidence is explicit and separate from review state;
- reconciliation decisions retain rationale and actor role;
- finding confidence is separate from consequence/exposure;
- a finding requires evidence and does not become accepted merely because a rule emitted it.

Persistence, candidate scoring and reviewer UI remain separate implementation work.

## 9. Finalized snapshot and revision lineage

Finalization creates a `FinalizedSnapshot` that binds:

- case ID/number/revision;
- finalization time;
- authorization artifact hash;
- exact scope hash;
- exact data-policy hash;
- audit-chain head;
- hashes for the professional case material included in the snapshot;
- tool build identity;
- active content-pack identities;
- a deterministic snapshot content hash.

The report/export layer must render from this frozen snapshot/materialized representation rather than mutable working state. Signed JSON/CSV/HTML/PDF generation and external verification remain M6 work.

## 10. Storage boundary still to implement

The core-domain model deliberately has no Android, SQLCipher or filesystem dependency. Remaining M1 work is to persist and enforce the model through adapters that implement the [evidence/data architecture](EVIDENCE-DATA-MODEL.md), including:

- SQLCipher-backed professional case repository with optimistic version checks;
- Android Keystore-backed database/per-case key lifecycle;
- content-addressed encrypted artifact vault;
- durable restoration with audit-chain verification;
- migration/corruption/tamper handling;
- Case App integration for role-aware authorization/reviewer actions;
- materialized finalized snapshots/export views.

Until those adapters are integrated, the professional case lifecycle is an executable **domain invariant layer**, not yet a durable field-ready case store.

## 11. Domain acceptance invariants

The `core-domain` tests must maintain at least these properties:

1. complete authorized lifecycle can reach a finalized snapshot;
2. final state cannot restart collection;
3. operational and security approvals are both required;
4. authorization is bound to exact scope and data policy;
5. P0 cannot silently expand to undeclared active operations;
6. active targets must remain in scope and outside exclusions;
7. reviewer acceptance is required before finalization;
8. audit restoration rejects tampered history;
9. supersession creates a new revision linked to the previous snapshot;
10. evidence/claim/finding records enforce their provenance/evidence constraints.

Current executable coverage is summarized in [IMPLEMENTATION.md](../../IMPLEMENTATION.md); release-level proof remains governed by [TEST-AND-ACCEPTANCE.md](../poc/TEST-AND-ACCEPTANCE.md).
