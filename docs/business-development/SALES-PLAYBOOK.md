# Sales playbook

This playbook governs stable commercial execution. Product safety and technical behavior remain defined in the product and architecture documentation. Dated campaigns belong in `OUTREACH-SPRINT.md`; do not copy temporary dates or messages into this file.

## Pipeline stages

| Stage | Entry evidence | Required exit |
|---|---|---|
| Research | Public account and trigger identified | Specific problem hypothesis and reachable role |
| Targeted outreach | Named account, owner, primary/backup route and tailored opening | Verifiable contact attempt logged |
| Contacted | Verifiable outbound interaction | Response, referral or completed no-response sequence |
| Discovery | Customer or trusted partner is discussing the problem | Confirmed problem, consequence, timing and agreed next step |
| Qualified | Champion, technical evaluator, decision process and plausible pilot route identified | Written pilot-design meeting |
| Pilot design | Scope, prerequisites, success measures and commercial model under discussion | Approved pilot plan |
| Pilot | Written authorization and prerequisites complete | Reviewed outcome and commercial decision |
| Commercial decision | Pilot outcome accepted | Acquire, extend, return, won, lost or parked |
| Won / Lost / Parked | Final disposition recorded | Reason and future trigger documented |

Public project activity does not move an account beyond `Research`. Selecting a person does not create a relationship. A sent message moves an account to `Contacted`, not to `Discovery`.

## Priority score

Score each account from 0 to 14. Use evidence, not optimism.

| Dimension | 0 | 1 | 2 |
|---|---|---|---|
| Problem | Hypothesis only | Indirectly corroborated | Confirmed by customer |
| Trigger | None | General programme | Dated decision or handover |
| Champion | None | Possible contact | Engaged advocate |
| Technical evaluator | Unknown | Role identified | Engaged and relevant |
| Economic buyer | Unknown | Role identified | Named and involved |
| Pilot access | No route | Plausible route | Approved artifact, lab or site path |
| Procurement | Unknown | Likely route | Confirmed process and owner |

No account can be `Qualified` below 9/14. Public research alone normally remains at 4/14 or lower. A public date can support the trigger score; it cannot support problem, champion, buyer, access or procurement scores.

## Stakeholder roles

Classify contacts by their possible role:

- champion;
- technical evaluator;
- operational approver;
- security/data approver;
- economic buyer;
- procurement/legal;
- external influencer;
- referral candidate;
- unknown.

Do not infer authority from title visibility. Mark the role as `potential` until confirmed through interaction or an official source. A current public employer check is not a relationship or permission to contact through a private channel.

## Controlled outreach sequence

1. Recheck the current public role or company affiliation.
2. Select one primary and one backup route per account.
3. Anchor the initial message in one named public event.
4. Ask one problem-validation question.
5. Request a referral to the functional owner rather than authorization for a pilot.
6. Offer a representative evidence-package review before discussing field access.
7. Log every action immediately.
8. Stop the sequence when a response establishes a different next step.
9. After one unanswered initial message and one concise follow-up, use the backup or official company route.
10. Park the account after the defined sequence unless a new trigger appears.

Do not use inferred emails, scraped phone numbers, bulk messages or tender contacts reused for unrelated marketing.

## Response standards

- Reply within one business day.
- Answer the question asked before proposing a meeting.
- Do not treat courtesy, a connection acceptance or content engagement as discovery.
- Record a referral as a new route, not as a champion.
- Confirm the next action, owner and date in writing.
- When the person declines or identifies no relevant problem, record it without arguing.

## Persona-specific value

| Persona | Lead with |
|---|---|
| Operations | A usable baseline for one area and explicit unresolved ownership |
| Project manager | Fewer handover disputes and evidence-linked deviations |
| Maintenance | A reviewed inventory that supports future work |
| GIS / asset management | Controlled field-to-record changes with provenance |
| Automation / SCADA | Bounded evidence collection and exact authorization |
| Security / data governance | Offline custody, explicit scope and visible limitations |
| Integrator / EPC | A cleaner, reviewable customer handover package |
| Economic buyer | Reduced rework and a fixed evaluation decision |

## Discovery standard

A discovery record is incomplete until it answers:

- What exact decision is difficult?
- What is the current method and who owns it?
- What fails, takes too long or creates disputes?
- What is the operational or project consequence?
- Which event creates urgency, and on what date?
- What process area is small enough for a first pilot?
- Which record is authoritative?
- Who reviews technical evidence?
- Who owns budget and procurement?
- What existing method or supplier addresses the problem?
- What would make the customer reject Atlas?
- Which commercial structure is acceptable?
- What specific next action was agreed, with whom and by when?

Use neutral notes. Separate direct customer statements from interpretation.

## Record architecture

- `activity-log.csv` is append-only history.
- `contacts.csv` contains the current person/relationship state.
- `pipeline.csv` contains the current account/opportunity state.
- `ACTIVE-ACCOUNT-PLANS.md` contains account-specific strategy, not interaction history.
- `research/` contains public facts and sources, not commercial stage.

Do not create separate meeting-summary files that become competing sources of truth. Put the concise factual interaction record in `activity-log.csv` and update current state in the two ledgers.

## Disqualification conditions

Park or close an account when:

- no bounded decision can be named;
- the operator or authorization boundary remains unresolved;
- the customer expects broad discovery or unsafe behavior;
- there is no plausible evidence source or representative environment;
- no owner accepts the next action;
- the incumbent method is sufficient and no differentiating value is identified;
- procurement timing makes the current trigger irrelevant;
- the full outreach sequence completes without engagement.

Return to the [business-development dashboard](README.md).
