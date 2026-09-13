# GitHub planning automation

Atlas uses GitHub issues, labels, milestones, Projects and coding agents as a human/agent delivery system.

## Ownership

Agents implement scoped code, tests, documentation, fixtures and CI. Humans own product/adopter decisions, architecture approval, licensing, OT authorization, security sign-off, physical qualification, practitioner validation and release approval.

Agents must not merge, close issues, claim customer or physical qualification, or convert probabilistic observations into accepted inventory.

## Workflows

`planning-bootstrap.yml` creates missing labels and adoption milestones and attempts to create the user-owned **Atlas Product Delivery** Project. Run it manually after configuring Project credentials.

`agent-dispatch.yml` validates an issue contract when it changes. A pass means an issue is eligible for explicit agent assignment; it does not automatically authorize code changes.

`repository-governance.yml` validates pull-request contract sections on PR open/edit/synchronize events. A pass means the PR body exposes the expected implementation contract and remaining human validation; it does not claim physical/customer qualification.

## Secrets and permissions

Repository operations use `GITHUB_TOKEN` where possible. Project creation and mutation may require an `ATLAS_PROJECTS_TOKEN` with Project write access. Use a GitHub App or fine-grained token with the smallest required permissions. Never put customer evidence, credentials or sensitive packet data in issues or logs.

## Labels and milestones

Execution labels: `execution: human`, `execution: agent`, `execution: human+agent`.

Readiness labels: `state: needs-product-decision`, `state: needs-architecture`, `state: ready-for-agent`, `state: needs-human-validation`, `state: blocked-by-evidence`.

Only `execution: agent` and `execution: human+agent` issues in `state: ready-for-agent` may be assigned to a coding agent. `state: needs-product-decision`, `state: needs-architecture`, and `state: blocked-by-evidence` are not agent-assignable. `state: needs-human-validation` means implementation may exist, but the remaining proof or approval is human-only before closure.

Milestones: A0 externally reproducible, A1 practically useful, A2 repeatably adoptable, A3 community extensible.

## Project fields

Create fields for Status, Execution owner, Readiness, Product area, Evidence required, Adoption gate, Risk and Release. Recommended views are Executive roadmap, Agent queue, Human validation, Safety/release gates and Capability catalog.

## Readiness contract

An agent issue must state outcome, scope, dependencies, failure behavior, non-goals, acceptance criteria, tests/evidence, human validation, and completion/reporting criteria. The corresponding pull request must state contract, dependencies, failure behavior, evidence artifact, documentation impact, tests/evidence, and human validation still required.

## Safe operation

The bootstrap is designed to tolerate already-existing labels and milestones. Review workflow changes like production code. Automation may organize work and validate metadata, but it must not approve product priorities, OT safety, security, physical qualification or adoption claims.
