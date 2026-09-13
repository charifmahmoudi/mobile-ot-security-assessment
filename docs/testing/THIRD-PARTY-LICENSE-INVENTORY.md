# Third-party license and SBOM inventory

This document defines how Atlas maintains a reproducible third-party inventory for Android, Python, website, workflow tooling, CI-fetched assets and official capability-package release review.

## Authority and outputs

- Source inventory: [`compliance/third_party_inventory.json`](../../compliance/third_party_inventory.json)
- Machine-readable SBOM (CycloneDX 1.6): [`compliance/atlas-third-party-sbom.cdx.json`](../../compliance/atlas-third-party-sbom.cdx.json)
- Validator/refresh tool: [`tools/third_party_inventory.py`](../../tools/third_party_inventory.py)

Each component record includes dependency name/version/source, license metadata, notice requirement, usage boundary and redistribution status (`allowed`, `review-required`, `prohibited`).

## Refresh from a clean checkout

```bash
python3 tools/third_party_inventory.py --refresh
python3 tools/third_party_inventory.py --check
```

If repository dependency declarations or CI-fetched inputs changed, `--check` fails until `compliance/third_party_inventory.json` is updated and `--refresh` rewrites the CycloneDX SBOM.

## CI enforcement and drift detection

The `License and SBOM inventory` workflow runs on pull requests and pushes that touch dependency or workflow inputs. It:

1. verifies tracked dependency inputs extracted from Gradle/website/workflow/tooling files match the inventory;
2. fails if license metadata is missing/unknown;
3. verifies the committed CycloneDX SBOM exactly matches the source inventory;
4. uploads the SBOM as a CI artifact for release review evidence.

## CI-fetched assets treatment

Hash-pinned packet captures downloaded by `tools/fetch_research_pcaps.sh`, GitHub Actions, Ubuntu runner packages, and workflow-cloned emulator repositories are explicitly listed in `compliance/third_party_inventory.json` with their usage boundary and redistribution status.

## Unresolved licensing questions (human review required)

1. conpot (`GPL-2.0-only`) usage is currently CI-only; maintainers/legal must confirm redistribution policy before any release artifact includes conpot-derived content.
2. ITI/ICS-Security-Tools packet captures require explicit redistribution-rights confirmation before packaging in official capability bundles.
3. Ubuntu runner package obligations for ffmpeg/poppler/libreoffice require maintainer/legal sign-off before redistributing generated demo media as official capability artifacts.

These questions remain intentionally unresolved in software automation and require human maintainer review before release.
