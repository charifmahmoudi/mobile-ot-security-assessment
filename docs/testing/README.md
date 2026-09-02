# Testing and verification evidence

This section records **what a test environment proves and what evidence it retains**. It does not define product behavior: current capability belongs in [IMPLEMENTATION.md](../../IMPLEMENTATION.md), target requirements in [P0-WATER](../poc/WATER-WASTEWATER-POC.md), and exact architecture in [Architecture](../architecture/README.md).

| Document | Purpose |
|---|---|
| [End-to-end test architecture](E2E-ACCEPTANCE.md) | CI topology, exercised journeys, retained evidence and proof limits |
| [Research testbeds](RESEARCH-TESTBEDS.md) | Protocol/emulator testbed provenance, versions and expected fixture behavior |
| [Emulator screenshot provenance](EMULATOR-SCREENSHOTS.md) | Historical screenshot sources and reproduction paths |

The executable CI source is [.github/workflows/android-ci.yml](../../.github/workflows/android-ci.yml). Pin a specific workflow run only when documenting provenance for an artifact produced by that run.

Return to the [documentation index](../README.md).
