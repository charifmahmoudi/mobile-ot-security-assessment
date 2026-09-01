# Sales playbook

This playbook governs evidence quality, pipeline stages and record integrity. It does not assign responsibilities or claim that commercial activity has occurred.

## Definitions

- **Public evidence** — a dated external source supporting a specific statement.
- **Commercial hypothesis** — a proposition that has not been confirmed by a customer.
- **Research lead** — a person or function identified from public information; not a relationship or verified contact.
- **Interaction** — an actual outbound or inbound communication through a known channel.
- **Customer-confirmed fact** — a statement made or formally provided by the customer or an authorized representative.
- **Opportunity** — a customer-confirmed problem with consequence, timing and an agreed next step.

## Pipeline stages

| Stage | Minimum evidence | Prohibited interpretation |
|---|---|---|
| Research | Public account signal and explicit hypothesis | Not a lead relationship or opportunity |
| Prepared | Current source and recipient/function verified; message prepared | No outreach has occurred |
| Contacted | Actual message or official-channel request logged | A sent message is not customer interest |
| Discovery | A response or meeting discusses the problem | Discussion is not qualification |
| Qualified | Confirmed problem, consequence, timing, relevant functions and plausible evaluation route | No value or probability without commercial evidence |
| Pilot design | Scope, prerequisites, success measures and commercial terms are under discussion | No authorization to access systems |
| Pilot | Written authorization, scope and prerequisites are complete | Activity remains bounded by the authorization |
| Commercial decision | Customer is deciding whether to acquire, extend or stop | No implied win before agreement |
| Won / Lost / Parked | Final disposition and reason are recorded | No unsupported forecast |

An account must remain at `Research` when the only evidence is public information.

## Evidence hierarchy

Use sources in this order:

1. official government, operator, corporate or development-finance source;
2. official procurement portal or official procurement notice;
3. established reporting attributable to a primary source;
4. public-tender mirror that identifies the official record;
5. contextual analysis.

A lower-grade source may create a research question but must not be presented as a verified operational fact when a primary source is available.

## Record-integrity controls

- Record only events that occurred.
- Keep `activity-log.csv` append-only after the first real activity is entered.
- Do not record source review, drafting or internal planning as customer interaction.
- Leave owner and date fields blank unless responsibility was explicitly accepted.
- Do not infer current employment, decision authority, consent or relationship from a public profile.
- Do not infer installed technology, vulnerability, network access or procurement intent from a project notice.
- Preserve the distinction between evidence, inference, customer statement and unknown.
- Correct inaccurate records rather than adding a second contradictory record.
- Do not assign probability or value before customer-confirmed commercial evidence exists.

## Qualification score

Score only with evidence. Each dimension is 0, 1 or 2.

| Dimension | 0 | 1 | 2 |
|---|---|---|---|
| Problem | Public hypothesis only | Indirectly corroborated | Confirmed by customer |
| Consequence | Unknown | General impact described | Specific impact confirmed |
| Timing | No event | General programme timing | Dated decision or handover |
| Relevant function | Unknown | Function identified | Engaged relevant participant |
| Economic route | Unknown | Plausible function identified | Named and involved decision route |
| Evaluation route | None | Representative artifact or lab appears possible | Approved bounded route |
| Procurement | Unknown | General route understood | Confirmed process and owner |

An account cannot be `Qualified` below 9/14. Public research alone should normally remain at 4/14 or below.

## Contact handling

`contacts.csv` is for verified working contacts, not the entire public lead pool. Add a person only when:

- the current role has been independently checked;
- the source and verification date are recorded;
- the person is relevant to an actual engagement path;
- the record does not imply authority or relationship that has not been established.

The broader public-profile research remains in `research/PUBLIC-PROFILE-LEADS.csv`.

## Discovery standard

A discovery record is incomplete until it distinguishes:

- the exact decision;
- the current method;
- the confirmed problem and consequence;
- the timing event;
- the authoritative record or evidence source;
- the functions involved in technical review, budget and procurement;
- the current alternative;
- the conditions that would make Atlas unsuitable;
- the agreed next step, if any.

Use the [engagement guide](ENGAGEMENT-GUIDE.md) for message and discussion templates.

## Disqualification and parking

Park or disqualify when evidence shows that:

- no bounded decision exists;
- the public trigger is no longer current;
- the operator or authorization boundary cannot be established;
- the expected activity would require unsafe or unauthorized behavior;
- the incumbent process is sufficient and no differentiated value is identified;
- there is no plausible evidence source or representative environment;
- no customer participant confirms the problem;
- timing or procurement makes the hypothesis irrelevant.

Record only the reason supported by evidence. Do not create a loss reason when no customer interaction occurred; use `Research — not advanced` or leave the field blank.

Return to the [business-development index](README.md).
