# Emulator acceptance screenshots

These are Android 15 (API 35) instrumentation outputs from [GitHub Actions run #20](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/runs/33326876122), not design mockups. That run passed all six jobs: build/lint/unit/architecture, API 29, API 35, PyModbus, modbus-tk and Conpot.

## Site-centered assessment journey

| Site selection | New-site onboarding | Site dashboard |
|---|---|---|
| ![Site selection](../user-guide/screenshots/01-site-selection-api35.png) | ![New site](../user-guide/screenshots/02-new-site-api35.png) | ![Dashboard](../user-guide/screenshots/03-site-dashboard-api35.png) |

The instrumentation creates a site with an industry dropdown and multiple vendor selections, then reopens it from persisted application state.

## Collection and inventory

| Collection-method decision | Asset inventory |
|---|---|
| ![Collection methods](../user-guide/screenshots/04-collection-methods-api35.png) | ![Asset inventory](../user-guide/screenshots/07-asset-inventory-api35.png) |

The test verifies search, review filtering, asset navigation, and the separation of working methods from planned Wi-Fi/Bluetooth packs.

## Passive research-capture analysis

| Modbus/TCP | DNP3 |
|---|---|
| ![Modbus result](../user-guide/screenshots/08-passive-modbus-api35.png) | ![DNP3 result](../user-guide/screenshots/08-passive-dnp3-api35.png) |

| IEC 60870-5-104 | BACnet/IP |
|---|---|
| ![IEC-104 result](../user-guide/screenshots/08-passive-iec104-api35.png) | ![BACnet result](../user-guide/screenshots/08-passive-bacnet-api35.png) |

Each capture enters through a real Android `content://` upload. Results expose hash, packet counts, time window, endpoints, inferred roles and confidence before observations enter inventory.

## Active authorization and emulator outcomes

| Exact authorization | Out-of-scope stop |
|---|---|
| ![Authorization](../user-guide/screenshots/05-active-authorization-api35.png) | ![Scope stop](../user-guide/screenshots/06-out-of-scope-blocked-api35.png) |

| PyModbus identity | modbus-tk service only |
|---|---|
| ![PyModbus identity](../user-guide/screenshots/09-active-pymodbus-api35.png) | ![modbus-tk service](../user-guide/screenshots/09-active-modbus-tk-api35.png) |

The active jobs execute the actual Case App → signed grant → Binder broker → TCP/502 → emulator → evidence UI path. Conpot also passes; PyModbus and modbus-tk are shown because they demonstrate the two distinct identity and service-only outcomes.

## Reproduction

Instrumentation writes PNG checkpoints through Android MediaStore. `tools/run_ui_e2e.sh` and `tools/run_active_e2e.sh` pull them before emulator shutdown; CI retains screenshots, test reports and logs as artifacts. Documentation copies are reduced to 540 × 1200 pixels and 64 colors without altering their content.
