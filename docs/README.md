# Documentation index

This is the canonical map and ownership contract for Atlas OT Scout documentation. Each material fact, requirement, design decision or mutable status has one authoritative home. Other documents may summarize it for their audience, but should link rather than maintain a second definition.

**Authority is not minimalism.** A document must still contain the audience-specific detail needed to do its job. Centralize the underlying truth, not the useful context. For example, the prospect directory should contain named people and account intelligence; the marketing playbook should contain role-specific messages and conversation starters; the user guide should contain executable operator steps; testing should contain concrete verification evidence.

## Authority by question

| Question | Authority | Rule |
|---|---|---|
| What is Atlas? | [Root README](../README.md) | Product-level description only. |
| What must the product do? | [Requirements](REQUIREMENTS.md) | Stable normative `MUST/SHOULD/MAY` requirements and IDs. |
| What exactly is P0-WATER? | [P0 specification](poc/WATER-WASTEWATER-POC.md) | Scope, permitted modes, checks, deliverable and definition of done. |
| How is an assessment performed and judged? | [Assessment method](poc/ASSESSMENT-METHOD.md) | Authorization, evidence hierarchy, reconciliation, finding and review method. |
| What executes today? | [Implementation](../IMPLEMENTATION.md) | The only maintained implemented/deferred capability matrix. |
| What is planned next? | [Roadmap](../ROADMAP.md) | Milestone status and sequencing. |
| What work items implement the roadmap? | [P0 backlog](poc/IMPLEMENTATION-BACKLOG.md) | Tickets and dependencies; it does not maintain a second milestone-status table. |
| What is the deployment topology? | [System and deployment](architecture/SYSTEM-AND-DEPLOYMENT.md) | Packages, processes, privilege and trust boundaries. |
| What network traffic can Atlas produce or receive? | [Network execution](architecture/NETWORK-EXECUTION.md) | Exact grants, active operation, passive capture path and stop behavior. |
| How are evidence and reports represented? | [Evidence data model](architecture/EVIDENCE-DATA-MODEL.md) | Artifact, observation, claim, asset, finding, review, snapshot and export model. |
| What are the security threats and controls? | [Security and threat model](architecture/SECURITY-AND-THREAT-MODEL.md) | Security argument, threat register and residual risks; links to architecture for mechanics. |
| Why was an architecture choice made? | [ADR index](adr/README.md) | Historical decision record. Superseded ADRs remain history, not current design. |
| What does CI prove? | [Testing](testing/README.md) | Verification evidence and proof boundary, not product definition. |
| How does a user operate the current application? | [User guide](user-guide/README.md) | Concrete task instructions and safety decisions; capability status links to `IMPLEMENTATION.md`. |
| Where is reader-facing product guidance? | [Wiki source](wiki/README.md) | Derivative knowledge base that explains the authoritative repository docs without redefining them; CI publishes the approved source set to the live GitHub Wiki. |
| What does commercial evidence imply? | [Commercial diligence summary](business-development/DILIGENCE-SUMMARY.md) | Source-backed facts, analysis, risks and commercial unknowns. |
| Where should Atlas compete in Morocco? | [Morocco market guide](business-development/MOROCCO-MARKET-GUIDE.md) | Segmentation, buying situations and stakeholder map derived from diligence. |
| Which organizations and people should be approached? | [Prospect intelligence directory](business-development/PROSPECT-DIRECTORY.md) | Account facts, current triggers, named public professionals, role relevance, public activity and contact routes. |
| How should outreach be executed? | [Marketing playbook](business-development/MARKETING-PLAYBOOK.md) | Role motivations, account-specific conversation starters, messages, channels and campaign execution. |
| What exactly is offered commercially? | [Evaluation and services offer](business-development/OFFER.md) | Evaluation duration, adoption choices, setup and consulting terms. |

## Anti-duplication rules

1. **Mutable implementation language belongs in `IMPLEMENTATION.md`.** Other documents should not maintain lists of what is implemented, working, supported, deferred or production-ready.
2. **Future-status language belongs in `ROADMAP.md`.** The backlog contains tickets, not a competing milestone-status summary.
3. **Exact network and cryptographic mechanics belong in `NETWORK-EXECUTION.md`.** Requirements state outcomes; security and test documents reference the network contract.
4. **Assessment semantics belong in `ASSESSMENT-METHOD.md`.** User and demo documents explain how to apply them, not redefine evidence levels, confidence or reportability rules.
5. **Commercial evidence and analysis belong in `DILIGENCE-SUMMARY.md`.** Market, prospect and marketing documents consume those conclusions instead of reproducing the source case.
6. **Commercial terms belong in `OFFER.md`.** Other commercial material may name the evaluation but should link for duration, acquisition and service terms.
7. **ADRs explain why.** Current architecture is maintained in the architecture section; superseded ADR text is not silently rewritten into a new decision.
8. **Testing documents describe proof.** They may restate an invariant briefly to identify exactly what a test verifies and must retain the concrete environment, method, expected result and residual proof boundary.
9. **Do not delete unique audience intelligence in the name of deduplication.** A prospect page needs people and triggers; a playbook needs motivations and message examples; a user manual needs step-by-step operation; an architecture contract needs implementable interfaces; a test plan needs actual acceptance conditions.
10. **The Wiki explains Atlas; the repository defines Atlas.** Wiki pages are task-oriented derivative guidance and must link to repository authorities for mutable status, normative semantics, exact safety mechanics and release evidence.

## Audience utility test

Before removing content as "duplicated," ask whether the second document is merely redefining the same truth or using that truth to help a different audience make a decision.

Keep content when it adds one of these:

- a named person/account/project needed for sales execution;
- a role-specific motivation, objection or message;
- an operator action, decision point, warning or recovery path;
- a test environment, fixture, expected result or evidence artifact;
- an architecture interface, dependency, failure mode or operational consequence;
- a product acceptance criterion or concrete definition-of-done item.

Remove or replace with a link when it only copies a mutable status table, exact cryptographic/network definition, generic market evidence, commercial terms or another document's normative semantics.

## Choose by audience

| Audience or decision | Start here | Supporting section |
|---|---|---|
| Customer or prospect | [Prospect pitch](pitch/README.md) | [Wiki source](wiki/README.md) and [Guided demo](demo/README.md) |
| Business developer | [Morocco business development](business-development/README.md) | [Prospect intelligence directory](business-development/PROSPECT-DIRECTORY.md) and [commercial diligence](business-development/DILIGENCE-SUMMARY.md) |
| Assessor | [User guide](user-guide/README.md) | [Wiki source](wiki/README.md) and [Assessment method](poc/ASSESSMENT-METHOD.md) |
| Product owner | [P0-WATER specification](poc/WATER-WASTEWATER-POC.md) | [Requirements](REQUIREMENTS.md) and [Roadmap](../ROADMAP.md) |
| Engineer | [Implementation](../IMPLEMENTATION.md) | [Architecture](architecture/README.md) |
| Security reviewer | [Security model](architecture/SECURITY-AND-THREAT-MODEL.md) | [Network execution](architecture/NETWORK-EXECUTION.md) and [ADRs](adr/README.md) |
| Test or release reviewer | [Testing](testing/README.md) | [P0 test plan](poc/TEST-AND-ACCEPTANCE.md) |
| Appliance integrator | [Appliance](appliance/README.md) | [Dedicated Android appliance](architecture/DEDICATED-ANDROID-APPLIANCE.md) |
| Contributor | [Contributing](../CONTRIBUTING.md) | [Governance](../GOVERNANCE.md) and [Security](../SECURITY.md) |

## Section map

| Section | Scope |
|---|---|
| [Architecture decisions](adr/README.md) | Decision history and supersession |
| [Appliance](appliance/README.md) | Laboratory platform and physical compatibility evidence |
| [Architecture](architecture/README.md) | Current/target design contracts and security boundaries |
| [Business development](business-development/README.md) | Diligence, Morocco strategy, person/account intelligence, marketing and commercial offer |
| [Demo](demo/README.md) | Generated customer-story media and provenance |
| [Pitch](pitch/README.md) | Prospect presentation assets |
| [P0 product pack](poc/README.md) | Product contract, method, acceptance and implementation backlog |
| [Product design](product/README.md) | UX contracts, user stories and presenter guidance |
| [Research sources](research/README.md) | Shared technical and normative references |
| [Testing](testing/README.md) | CI topology, testbeds and retained evidence |
| [User guide](user-guide/README.md) | Operator instructions and screenshot-backed workflow |
| [Wiki source](wiki/README.md) | Reader-facing task guides and explanatory knowledge-base pages; derivative, non-authoritative and automatically published to the live GitHub Wiki |

## Maintenance contract

- Prefer a relative link to the authority document over a copied table or capability list.
- When a summary is necessary, make it audience-specific and useful rather than mechanically shorter.
- Preserve named people, project signals, workflow steps, acceptance evidence and other unique operational intelligence in the document that owns that audience task.
- Keep Wiki pages derivative: explain decisions and workflows, then link to the repository authority for exact or mutable truth.
- Publish Wiki content only from `docs/wiki/`; the `Publish Wiki` workflow validates and mirrors the approved page set to the live GitHub Wiki from `main`.
- Pin individual CI runs only in provenance records that intentionally describe that historical artifact.
- Use external citations next to factual market or technical claims.
- Add every maintained document to its nearest section landing page.
- Run `python3 tools/verify_documentation.py` before committing documentation changes.
