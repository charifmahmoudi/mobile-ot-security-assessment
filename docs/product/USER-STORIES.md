# Evidence-backed user stories and acceptance outcomes

_Reviewed: 2 September 2026._

These stories translate the role archetypes in [PERSONAS.md](PERSONAS.md) into professional decisions and outcomes. They intentionally avoid treating public prospect research as proof of private customer pain or buying behavior.

The structure is:

- **persona and situation** — who is making a decision and in what professional context;
- **story** — the outcome the person needs, independent of a particular screen or Android implementation;
- **acceptance outcomes** — observable product behavior from that person's point of view;
- **evidence status** — what is grounded today versus what still requires practitioner/customer validation;
- **traceability** — the normative requirements and verification level that constrain the story.

Exact network mechanics belong in [NETWORK-EXECUTION.md](../architecture/NETWORK-EXECUTION.md), assessment semantics in [ASSESSMENT-METHOD.md](../poc/ASSESSMENT-METHOD.md), test detail in [TEST-AND-ACCEPTANCE.md](../poc/TEST-AND-ACCEPTANCE.md), and current implementation status in [IMPLEMENTATION.md](../../IMPLEMENTATION.md).

## Journey 1 — prepare and authorize

### US-PREP-001 — bind evidence to one operational context

**Primary personas:** P-01 industrial audit practitioner, P-02 commissioning engineer.

**Story.** As a professional preparing an assessment, I need every observation tied to one legal entity, site and process area so evidence from different engagements or operating areas cannot be mixed.

**Acceptance outcomes.**

- Collection cannot begin without a selected case/site/process context.
- The context identifies the bounded assessment unit and remains visible while evidence is collected/reviewed.
- Existing customer context can be imported or entered without turning vendor/site declarations into discovery claims.
- Evidence created or imported under one case cannot silently migrate into another case.

**Evidence status.** The need for bounded site/process scope is established by the P0 method and professional audit context. Whether the present site-onboarding interaction is the preferred workflow remains a UX hypothesis.

**Traceability:** POC-001, POC-002, DATA-005 · T1/T4.

### US-PREP-002 — approve exact collection authority

**Primary persona:** P-05 OT/security approver.

**Story.** As the security approver, I need to see the exact interfaces, active targets, permitted methods, time window, retention and export constraints before approving collection so Atlas cannot gain broader authority than I intended.

**Acceptance outcomes.**

- Protected collection remains unavailable until the required authorization exists.
- Authorization states scope, exclusions, approved methods, time window and data-handling constraints.
- Active operations are represented as exact bounded operations rather than generic network access.
- Expired, out-of-scope or excluded operations are refused without fallback expansion.
- Approval and subsequent packet-producing actions are auditable.

**Evidence status.** Security/governance roles are publicly observable and the P0 method normatively requires this approval. Whether Atlas's authorization representation is sufficient for real customer governance remains to be validated.

**Traceability:** SAFE-002, SAFE-003, SAFE-006, DATA-004, POC-004 · T0/T1/T3/T4.

### US-PREP-003 — protect operating conditions and stop authority

**Primary persona:** P-03 water operations/maintenance owner.

**Story.** As the operational owner, I need the proposed assessment window, exclusions and stop conditions made explicit and enforceable so evidence collection does not take priority over process safety or continuity.

**Acceptance outcomes.**

- Operating exclusions and stop conditions are recorded before protected collection.
- A local stop action is available without internet access.
- Stop, expiry, device detach or invalid scope does not trigger alternative discovery behavior.
- The final case records why collection stopped and what evidence may be incomplete as a result.

**Evidence status.** Operational continuity and stop authority are normative P0 requirements; the practical adequacy of the stop workflow must be witnessed with operators.

**Traceability:** SAFE-005, COL-004, COL-005, PLAT-003 · T1/T2/T3/T4.

## Journey 2 — establish the expected state

### US-BASE-001 — preserve the customer's expected baseline

**Primary personas:** P-02 commissioning engineer, P-04 GIS/patrimony owner.

**Story.** As the person responsible for project or asset records, I need the customer's expected inventory preserved as a declared source so field observations can be compared with it without overwriting the original record.

**Acceptance outcomes.**

- Imported/entered expected records preserve source and mapping provenance.
- Normalization does not destroy the original customer-declared values.
- Expected state and observed evidence remain distinguishable throughout review.
- The case can show which records were in scope, excluded or unresolved.

**Evidence status.** Public project/GIS roles and the assessment method establish the existence of asset/design records. The degree of pain caused by current reconciliation practices remains unvalidated.

**Traceability:** DATA-005, ID-001, POC-003 · T0/T1/T4.

### US-BASE-002 — understand uncertainty before collecting more

**Primary personas:** P-01 audit practitioner, P-03 operations owner.

**Story.** As a practitioner resuming a case, I need to understand what is known, what remains unsupported and what requires review so I choose the next evidence action rather than defaulting to another broad scan.

**Acceptance outcomes.**

- The case separates accepted inventory, raw/proposed observations, conflicts and unresolved records.
- Evidence coverage is represented without claiming complete network visibility.
- The next action can be chosen because of a defined evidence gap.
- Absence from a sample is never represented as proof that an asset is absent.

**Evidence status.** This follows directly from Atlas's evidence/reconciliation hypothesis and P0 limitation rules; whether this materially improves practitioner workflow is a commercial/product hypothesis.

**Traceability:** POC-005, COL-005, ID-001, ID-002 · T0/T1/T4.

## Journey 3 — collect bounded evidence

### US-COL-001 — choose the least intrusive adequate method

**Primary personas:** P-01 audit practitioner, P-05 security approver.

**Story.** As an authorized assessor, I need to understand the network effect and visibility limits of each available evidence method so I can choose the least intrusive method that can answer the documented question.

**Acceptance outcomes.**

- Passive/imported and packet-producing methods are clearly distinguishable before execution.
- Atlas defaults to passive/offline methods where they can answer the question.
- Each method states what it can and cannot observe.
- Planned or unavailable methods cannot be mistaken for working field capability.

**Evidence status.** This is a supported product principle from diligence and a normative P0 requirement. Practitioner preference among methods still needs field evidence.

**Traceability:** SAFE-001, POC-004, COL-001, COL-002, COL-005 · T1/T2/T4.

### US-COL-002 — analyze customer-supplied capture evidence offline

**Primary personas:** P-01 audit practitioner, P-02 commissioning engineer.

**Story.** As a practitioner supplied with PCAP/PCAPNG evidence, I need to analyze it offline with source provenance intact so I can use existing capture evidence without transmitting packets or losing its evidentiary context.

**Acceptance outcomes.**

- PCAP/PCAPNG can be ingested without network transmission.
- Source, cryptographic hash, collection context and visibility limitations are retained.
- Malformed, truncated, oversized or unsupported input fails safely.
- Parser failure cannot silently mutate accepted inventory.
- Parsed observations remain proposals until reviewed.

**Evidence status.** Offline analysis and provenance are strongly supported by the P0 method. Customer willingness to provide captures and the frequency of this workflow remain evaluation questions.

**Traceability:** COL-003, COL-004, DATA-001, DATA-005, QUAL-001 · T0/T1/T4.

### US-COL-003 — collect live passive evidence only from an approved source

**Primary personas:** P-01 audit practitioner, P-03 operations owner, P-05 security approver.

**Story.** As an authorized field practitioner, I need live passive evidence to come only from an approved capture path with explicit visibility limits so I do not confuse an ordinary access-port view with a meaningful segment sample.

**Acceptance outcomes.**

- The capture source/interface is explicit and auditable.
- Atlas does not claim whole-segment visibility from an ordinary switched-network attachment.
- Capture duration, source and loss/limitations are retained with the artifact.
- Supported field capture depends on a qualified hardware combination rather than an assumed Android capability.

**Evidence status.** Switched-network visibility constraints are established technical facts; physical usefulness across customer environments remains a field-validation gate.

**Traceability:** POC-004, COL-002, COL-004, COL-005, PLAT-002, PLAT-004 · T2/T3/T4.

### US-COL-004 — resolve one identity gap with bounded active identity

**Primary persona:** P-01 audit practitioner; secondary P-02 commissioning engineer.

**Story.** As an authorized practitioner facing one documented identity gap, I need to query exactly one approved Modbus target without reading or changing process registers so I can improve identity evidence without turning the assessment into broad discovery or control activity.

**Acceptance outcomes.**

- The requested target and operation are inside recorded authorization and scope.
- Only the admitted Modbus basic device-identification operation can execute for initial P0 active identity.
- No subnet, port or unit-ID sweep occurs.
- No register read/write, credential action, exploit, fuzz or generic socket command occurs.
- The result distinguishes supported identity evidence from service-only confirmation or failure.
- The packet-producing action is recorded with its profile/version and result.

**Evidence status.** The operation and safety boundary are normative. Real-device usefulness and safety across model/firmware combinations remain release/evaluation evidence requirements.

**Traceability:** SAFE-002, SAFE-003, SAFE-004, SAFE-006, ID-004, QUAL-002 · T0/T1/T3/T4.

### US-COL-005 — fail closed without expanding scope

**Primary personas:** P-03 operations owner, P-05 security approver.

**Story.** As the person accountable for operational/security risk, I need invalid scope, malformed evidence, unreachable equipment or expired authority to stop locally without triggering a broader alternative action.

**Acceptance outcomes.**

- Failure preserves case context and explains what was not completed.
- No fallback sweep, scope expansion or undeclared protocol operation occurs.
- A valid protocol exception or open service is not inflated into unsupported vendor/model/vulnerability claims.
- Partial evidence is clearly marked when collection stops early.

**Evidence status.** Fail-closed behavior is a normative safety requirement; operator confidence in the behavior must be established through witnessed rehearsal.

**Traceability:** SAFE-002, SAFE-004, SAFE-005, COL-004, QUAL-002 · T0/T1/T3/T4.

## Journey 4 — review and reconcile

### US-REC-001 — review observations before changing accepted inventory

**Primary personas:** P-04 GIS/patrimony owner, P-01 audit practitioner.

**Story.** As an asset-information or assessment owner, I need field observations kept separate from accepted inventory until they are reviewed so unverified discovery cannot silently become authoritative asset data.

**Acceptance outcomes.**

- Raw/proposed observations remain identifiable as observations.
- Accept/reject/reconcile is an explicit analyst decision.
- Accepted inventory retains the evidence and decision provenance supporting the change.
- Weak identifiers alone cannot silently create a strong model-level identity.

**Evidence status.** This directly implements the defensible evidence/reconciliation model. Whether customer asset owners accept the proposed workflow is a key validation hypothesis.

**Traceability:** ID-001, ID-002, ID-003, DATA-005, POC-003 · T0/T1/T4.

### US-REC-002 — reconcile expected and observed state explicitly

**Primary personas:** P-02 commissioning engineer, P-04 GIS/patrimony owner.

**Story.** As a person preparing handover or maintaining an accepted asset baseline, I need missing, unexpected, probable and conflicting records made explicit so unresolved differences cannot disappear inside an automatically merged inventory.

**Acceptance outcomes.**

- Expected records can remain `not observed` without being declared absent.
- Observed candidates with no accepted match remain `unexpected` until resolved.
- Probable matches remain distinguishable from confirmed matches.
- Material conflicts remain visible and reviewable.
- Reconciliation decisions retain evidence/provenance.

**Evidence status.** Project, GIS and asset roles are observable; the hypothesis that this saves material handover/reconciliation effort must be measured in evaluations.

**Traceability:** ID-001, ID-002, DATA-005, POC-003 · T0/T1/T4.

### US-REC-003 — preserve uncertainty when identity evidence conflicts

**Primary personas:** P-01 audit practitioner, P-06 independent reviewer.

**Story.** As a professional reviewer, I need contradictory or weak identity evidence to reduce confidence rather than be hidden so the final assessment does not present certainty the evidence cannot support.

**Acceptance outcomes.**

- Conflicting material attributes remain visible.
- OUI, hostname or open port alone cannot produce model/firmware certainty.
- Strong identity claims retain source, observation time, pack/rule version and confidence.
- An unresolved identity can remain unresolved through finalization when evidence is insufficient.

**Evidence status.** This is normative assessment behavior and a central professional-quality proposition for Atlas.

**Traceability:** ID-001, ID-002, ID-003, ID-005 · T0/T4.

## Journey 5 — assess and explain

### US-FIND-001 — create findings only from sufficient evidence

**Primary personas:** P-01 audit practitioner, P-06 independent reviewer.

**Story.** As an assessor, I need a finding to remain a proposal until its condition, affected object, evidence, confidence, limitations and recommendation are reviewable so a technical observation cannot silently become an exaggerated professional conclusion.

**Acceptance outcomes.**

- Findings link back to specific evidence and method.
- Insufficient evidence prevents a stronger reportable conclusion.
- A service/port alone does not become a vulnerability claim.
- Product/version observations do not become end-of-support/CVE claims without required authoritative applicability evidence.
- Rejected findings do not enter final executive totals.

**Evidence status.** This follows the normative P0 finding-quality gate. Whether it reduces reviewer effort versus existing professional tools remains unvalidated.

**Traceability:** POC-003, POC-005, ID-001 · T0/T4.

### US-FIND-002 — bring operational consequence into the assessment

**Primary persona:** P-03 water operations owner.

**Story.** As the operational owner, I need to contribute or accept the process consequence and operating context of a finding so technical evidence is not converted into severity without understanding the actual water/wastewater process impact.

**Acceptance outcomes.**

- Consequence remains distinguishable from technical observation/confidence.
- The operational owner can provide/accept criticality or consequence context.
- Atlas does not infer safety function or business consequence solely from network traffic.
- Recommendations preserve safety and availability constraints.

**Evidence status.** The assessment method requires operational consequence input; the exact collaboration workflow requires field validation.

**Traceability:** POC-003, SAFE-004 · T1/T4.

### US-FIND-003 — understand the limits of the conclusion

**Primary personas:** P-06 reviewer, P-07 evaluation sponsor.

**Story.** As a reviewer or decision-maker reading the assessment, I need to know what Atlas observed, what it could not observe, the sampling method and unresolved evidence so I do not treat a bounded assessment as complete-network coverage or certification.

**Acceptance outcomes.**

- Visibility, sampling, exclusions and evidence gaps are part of the assessment output.
- `Not observed` is never presented as `absent` without an independently justified basis.
- The product does not claim penetration-test, compliance-certification or complete-network coverage.
- Material limitations remain visible in both technical and executive-facing output.

**Evidence status.** This is a normative product boundary and directly supports the diligence posture of avoiding unsupported claims.

**Traceability:** POC-003, POC-005, COL-005 · T0/T4.

## Journey 6 — review, finalize and hand off

### US-REV-001 — independently challenge material claims

**Primary persona:** P-06 independent reviewer.

**Story.** As an independent reviewer, I need every material identity claim and finding linked to its original evidence, confidence, limitations and analyst decision history so I can accept, reject or return it before finalization without relying on the collector's memory.

**Acceptance outcomes.**

- Material claims/finding evidence can be inspected from the review workflow.
- Reviewer decisions are explicit and auditable.
- Rejected findings are excluded from final report totals.
- Conflicts/limitations cannot be silently removed to obtain a clean report.
- Finalization is blocked until required review/QA gates are satisfied.

**Evidence status.** Independent review is part of the normative P0 professional method. Acceptance of Atlas's review representation by qualified practitioners is a major commercial validation gate.

**Traceability:** POC-003, POC-006, ID-001, ID-002 · T1/T4.

### US-OUT-001 — produce an externally verifiable professional package

**Primary personas:** P-01 audit practitioner, P-06 reviewer.

**Story.** As the professional responsible for delivery, I need the finalized package to preserve scope, evidence provenance, decisions, limitations and integrity information so another authorized party can verify what the report is based on without a cloud dependency.

**Acceptance outcomes.**

- Finalized output is deterministic from the reviewed case state except explicitly non-semantic metadata.
- The package includes the required machine-readable and human-readable material.
- Hash/signature verification can be performed externally.
- Raw captures are omitted when retention/export authorization excludes them.
- A finalized case cannot silently resume collection or mutate without a new revision.

**Evidence status.** This is the normative P0 deliverable. Whether customers/auditors consider the format usable within existing handoff processes must be validated.

**Traceability:** POC-006, DATA-001, DATA-003, DATA-004, DATA-006 · T0/T1/T4.

### US-OUT-002 — hand off unresolved decisions without losing them

**Primary personas:** P-02 commissioning engineer, P-03 operations owner, P-07 sponsor.

**Story.** As the recipient of a completed assessment or handover package, I need unresolved exceptions, accepted corrections and limitations preserved so responsibility can pass without pretending every discrepancy was resolved during the field visit.

**Acceptance outcomes.**

- Reconciled assets and unresolved exceptions are separately deliverable.
- The handoff records decisions/corrections independently from technical finalization.
- A correction creates a new case revision/manifest rather than rewriting the finalized evidence history.
- The recipient can trace an exception back to its supporting evidence and review state.

**Evidence status.** Handover/reconciliation contexts are well supported by public project roles and the P0 method; actual customer acceptance workflow remains a hypothesis to validate.

**Traceability:** POC-003, POC-006, DATA-005, DATA-006 · T1/T4.

## Journey 7 — determine whether Atlas creates enough value to continue

### US-EVAL-001 — make a bounded evaluation decision

**Primary persona:** P-07 evaluation sponsor.

**Story.** As an evaluation sponsor, I need a trial to answer one defined operational/professional question and expose measurable effort, limitations and blockers so I can decide whether Atlas merits a paid evaluation, procurement step, broader trial or stop decision.

**Acceptance outcomes.**

- The evaluation starts with a defined question/use case rather than a generic product demonstration.
- Baseline and Atlas-assisted effort can be measured where practical: preparation, field collection, reconciliation, review/report effort, equipment burden, rework/revisit and support needs.
- Technical success is separated from workflow usefulness and procurement willingness.
- Site policy, hardware, data-handling or methodological blockers are recorded even when the software works.
- The outcome can be `continue`, `modify and retest`, `procurement investigation` or `stop`; no outcome is assumed in advance.

**Evidence status.** This story is deliberately a **product-discovery/commercial-validation story**, not a claim that Atlas already produces ROI. The diligence explicitly states that price, ROI, mobile preference and willingness to pay remain unknown.

**Traceability:** commercial diligence §9/§11; P0 definition-of-done independent rehearsal where applicable · controlled customer/practitioner evaluation.

## Supporting system-role story

### US-PACK-001 — update approved knowledge without rewriting finalized cases

**Role:** pack administrator.

**Story.** As the administrator of approved Atlas content, I need signed/versioned knowledge packs to be installable without silently changing finalized case evidence so reproducibility is preserved across content updates.

**Acceptance outcomes.**

- Pack identity/version is retained with affected claims.
- Only approved signed content is admitted to the professional release model.
- Finalized cases retain the content/tool identities used for their conclusions.
- Updating a pack does not rewrite a finalized assessment result in place.

**Evidence status.** This is driven by the normative product/security contract rather than business-development persona evidence.

**Traceability:** ID-001, ID-005, QUAL-003 · T0/T1/T4.

## Traceability summary

| Story | Primary persona(s) | Core requirements / authority | Main verification |
|---|---|---|---|
| US-PREP-001 | P-01, P-02 | POC-001, POC-002, DATA-005 | T1/T4 |
| US-PREP-002 | P-05 | SAFE-002/003/006, DATA-004, POC-004 | T0/T1/T3/T4 |
| US-PREP-003 | P-03 | SAFE-005, COL-004/005, PLAT-003 | T1/T2/T3/T4 |
| US-BASE-001 | P-02, P-04 | DATA-005, ID-001, POC-003 | T0/T1/T4 |
| US-BASE-002 | P-01, P-03 | POC-005, COL-005, ID-001/002 | T0/T1/T4 |
| US-COL-001 | P-01, P-05 | SAFE-001, POC-004, COL-001/002/005 | T1/T2/T4 |
| US-COL-002 | P-01, P-02 | COL-003/004, DATA-001/005, QUAL-001 | T0/T1/T4 |
| US-COL-003 | P-01, P-03, P-05 | POC-004, COL-002/004/005, PLAT-002/004 | T2/T3/T4 |
| US-COL-004 | P-01 | SAFE-002/003/004/006, ID-004, QUAL-002 | T0/T1/T3/T4 |
| US-COL-005 | P-03, P-05 | SAFE-002/004/005, COL-004, QUAL-002 | T0/T1/T3/T4 |
| US-REC-001 | P-04, P-01 | ID-001/002/003, DATA-005, POC-003 | T0/T1/T4 |
| US-REC-002 | P-02, P-04 | ID-001/002, DATA-005, POC-003 | T0/T1/T4 |
| US-REC-003 | P-01, P-06 | ID-001/002/003/005 | T0/T4 |
| US-FIND-001 | P-01, P-06 | POC-003/005, ID-001 | T0/T4 |
| US-FIND-002 | P-03 | POC-003, SAFE-004 | T1/T4 |
| US-FIND-003 | P-06, P-07 | POC-003/005, COL-005 | T0/T4 |
| US-REV-001 | P-06 | POC-003/006, ID-001/002 | T1/T4 |
| US-OUT-001 | P-01, P-06 | POC-006, DATA-001/003/004/006 | T0/T1/T4 |
| US-OUT-002 | P-02, P-03, P-07 | POC-003/006, DATA-005/006 | T1/T4 |
| US-EVAL-001 | P-07 | Diligence validation gates | Controlled evaluation |
| US-PACK-001 | Pack administrator | ID-001/005, QUAL-003 | T0/T1/T4 |

## Product-discovery rule

Do not promote a persona hypothesis into a requirement merely because it appears plausible from public professional data. A hypothesis becomes stronger only when a practitioner/customer evaluation provides direct evidence. In particular, the project must continue to treat the following as unproven until measured or explicitly stated by users:

- preference for a phone/dedicated mobile appliance;
- current time lost to reconciliation/reporting;
- specific percentage of time/cost saved;
- willingness to pay or accepted price;
- engagement frequency/repeat usage;
- preference for Atlas over tools already owned;
- acceptance of Atlas evidence inside a qualified audit or customer methodology.

Return to the [product design index](README.md).
