# Wiki source

This directory contains the maintained source for the Atlas GitHub Wiki.

The Wiki is intentionally concise. It provides product orientation, operating guidance, reconciliation interpretation, pilot structure and safety boundaries. Exact requirements, implementation status, architecture, network behavior and verification evidence remain in the versioned repository documentation.

## Published pages

- [Home](Home.md)
- [Assessment Workflow](Assessment-Workflow.md)
- [Asset Reconciliation](Asset-Reconciliation.md)
- [Customer Pilot](Customer-Pilot.md)
- [Safety & Technical Boundaries](Safety-and-Technical-Boundaries.md)
- [Sidebar](_Sidebar.md)

`README.md` is repository-only maintenance documentation and is not published to the Wiki.

## Automated publication

The live GitHub Wiki is published by [`.github/workflows/wiki.yml`](../../.github/workflows/wiki.yml).

Publication runs when Wiki source or the publication workflow changes on `main`, and can also be started manually with `workflow_dispatch`.

The workflow:

1. checks out the repository;
2. runs `python3 tools/verify_documentation.py` and stops on validation failure;
3. stages only the published pages listed above;
4. clones `mobile-ot-security-assessment.wiki.git` using the repository-scoped `GITHUB_TOKEN`;
5. replaces the Wiki working tree with the staged source;
6. commits only when the published content changed; and
7. pushes the resulting commit to the Wiki `master` branch.

The publication is a mirror. A page present in the live Wiki but absent from the approved source set is removed on the next successful publication. Do not maintain production Wiki content directly in the GitHub Wiki UI; make the change under `docs/wiki/`, validate it in the repository, and let CI publish it.

No personal access token or separate deployment secret is required while the repository-scoped Actions token retains permission to write the associated Wiki repository.

## Verification

A successful `Publish Wiki` workflow run proves that the documentation verifier passed and that the approved source set was pushed to the GitHub Wiki. Publication failures must be treated as documentation delivery failures; the source under `docs/wiki/` remains authoritative until the workflow succeeds.
