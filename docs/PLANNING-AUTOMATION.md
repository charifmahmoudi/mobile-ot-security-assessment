# GitHub planning automation

Atlas uses GitHub issues, labels, milestones, Projects and coding agents as a human/agent delivery system.

## Ownership

Agents implement scoped code, tests, documentation, fixtures and CI. Humans own product/adopter decisions, architecture approval, licensing, OT authorization, security sign-off, physical qualification, practitioner validation and release approval.

Agents must not merge, close issues, claim customer or physical qualification, or convert probabilistic observations into accepted inventory.

## Workflows

`planning-bootstrap.yml` creates missing labels and adoption milestones and attempts to create the user-owned **Atlas Product Delivery** Project. Run it manually after configuring Project credentials.

`agent-dispatch.yml` validates an issue contract when it changes. A pass means an issue is eligible for explicit agent assignment; it does not automatically authorize code changes.

## Secrets and permissions

Repository operations use `GITHUB_TOKEN` where possible. Project creation and mutation may require an `ATLAS_PROJECTS_TOKEN` with Project write access. Use a GitHub App or fine-grained token with the smallest required permissions. Never put customer evidence, credentials or sensitive packet data in issues or logs.

## Labels and milestones

Execution labels: `execution: human`, `execution: agent`, `execution: human+agent`.

Readiness labels: `state: needs-product-decision`, `state: needs-architecture`, `state: ready-for-agent`, `state: needs-human-validation`, `state: blocked-by-evidence`.

Milestones: A0 externally reproducible, A1 practically useful, A2 repeatably adoptable, A3 community extensible.

## Project fields

Create fields for Status, Execution owner, Readiness, Product area, Evidence required, Adoption gate, Risk and Release. Recommended views are Executive roadmap, Agent queue, Human validation, Safety/release gates and Capability catalog.

## Readiness contract

An agent issue must state outcome, scope, non-goals, parent/dependencies, architectural contract, affected modules, schema/API changes, safety impact, failure behavior, tests, evidence, human validation and definition of done.

## Safe operation

The bootstrap is designed to tolerate already-existing labels and milestones. Review workflow changes like production code. Automation may organize work and validate metadata, but it must not approve product priorities, OT safety, security, physical qualification or adoption claims.
