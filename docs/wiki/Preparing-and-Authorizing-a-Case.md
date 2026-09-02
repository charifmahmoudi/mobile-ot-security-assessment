# Preparing and Authorizing a Case

A professional Atlas assessment begins with a bounded question and explicit authority. Collection should never be the first step.

## Define the operational context

Record the customer/legal entity, physical site and named process area before assigning evidence to the case. The process area should be specific enough that an operator can understand which equipment and operating context are included.

Good examples:

- Booster Pumping — PLC/HMI control segment
- Chlorination Skid — local control network
- Wastewater Lift Station 4 — control cabinet and telemetry interface

Avoid broad labels such as “plant network” unless the authorization and P0 assessment unit genuinely support that scope.

## State the assessment question

A useful objective is decision-oriented. Examples:

- Does installed equipment support the accepted commissioning inventory?
- Which inventory records remain unsupported or conflicting after this evidence sample?
- Can one documented controller identity gap be resolved safely?

The objective should determine what evidence is needed. Do not collect broadly and invent the question afterward.

## Define scope and exclusions

Before protected collection, record the relevant boundaries, including where applicable:

- CIDR or exact active targets;
- excluded addresses/assets;
- approved capture source/interface;
- physical/process area;
- permitted evidence methods;
- operating window;
- stop conditions.

Exclusions are part of the assessment contract, not optional notes.

## Define data handling

Agree how evidence may be retained and exported. Consider:

- whether raw captures may leave the customer site;
- whether raw captures may be included in the final package;
- expected retention/deletion date;
- classification or handling restrictions;
- approved export destination.

## Roles

The P0 professional model distinguishes responsibilities such as:

- **Assessor** — prepares the case, collects/imports evidence and proposes professional decisions.
- **Operational approver** — approves process scope, operating conditions and stop authority.
- **Security approver** — approves interfaces, active targets, retention and export constraints.
- **Reviewer** — independently accepts, rejects or returns material claims/findings before finalization.

A person may hold more than one role only where policy permits; Atlas should retain which role was used for each action.

## Authorization changes

If a material scope, method, exclusion, time window or data-policy decision changes, do not assume the previous approval still applies. Re-establish authorization as required by the professional case contract.

## Stop conditions

Collection must stop or pause when the agreed operational or security boundary is no longer valid. Examples include process instability, network instability, route/interface changes, approver request, device detach or expiration of authority.

A failure is not permission to fall back to a broader discovery method.

## Exact authority

For the normative lifecycle, authorization fingerprinting and role gates, use the repository's [Professional Case Model](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/PROFESSIONAL-CASE-MODEL.md). For exact packet-producing authority, use [Network Execution](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md).

Next: [Importing the Expected Inventory](Importing-the-Expected-Inventory.md).
