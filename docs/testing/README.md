# Testing and verification evidence

This section explains what CI proves, what evidence is retained, and what remains a physical or release qualification gate.

| Document | Purpose |
|---|---|
| [End-to-end acceptance architecture](E2E-ACCEPTANCE.md) | CI topology, active and passive sequences, acceptance matrix, and physical limits |
| [Research testbeds](RESEARCH-TESTBEDS.md) | Emulator and protocol testbed provenance, versions, and expected behavior |
| [Emulator screenshot provenance](EMULATOR-SCREENSHOTS.md) | Screenshot source, represented journeys, and reproduction path |

The executable source of truth is [.github/workflows/android-ci.yml](../../.github/workflows/android-ci.yml). A green workflow proves the software paths described here; it does not qualify the final appliance or a production OT network.

Return to the [documentation index](../README.md).
