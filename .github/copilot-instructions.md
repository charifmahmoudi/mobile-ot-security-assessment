# Atlas human/agent delivery rules

Use GitHub agents as implementation workers operating through pull requests. Never merge, close an issue, declare a release, or claim physical/customer qualification.

Every issue must identify an execution owner: `agent`, `human`, or `human+agent`. An issue is ready for agent assignment only when it includes outcome, exact scope/modules, dependencies, failure behavior, acceptance criteria, tests/evidence, and the human validation that remains explicitly unproven.

Human-only work includes product decisions, architecture approval, open-source license approval, OT authorization, physical appliance qualification, customer/practitioner validation, security sign-off, and release approval.

Preserve Atlas invariants: the Case App has no INTERNET permission; active traffic uses the signed Network Broker boundary; passive capture uses the Capture Broker/receive-only path; parser and model output creates observations and claims, never accepted inventory; not-observed is not absent; and unproven physical or customer evidence must remain explicitly unproven.

For every implementation pull request, include contract, dependencies, failure behavior, evidence artifact, human validation still required, changed files, commands run, results, unrun validation, residual risks, and the issue acceptance-criterion mapping. Automated tests may prove software/documentation paths only; they must not claim physical or customer qualification.
