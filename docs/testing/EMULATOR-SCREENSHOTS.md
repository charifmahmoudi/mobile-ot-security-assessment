# Emulator acceptance screenshots

These images are outputs of the Android 15 (API 35) instrumentation journeys in [GitHub Actions run #16](https://github.com/charifmahmoudi/mobile-ot-security-assessment/actions/runs/33322997770), not design mockups. The same run passed Android API 29 and API 35, PyModbus, modbus-tk, and Conpot jobs.

## Collection-mode decision

![Home screen with active and passive choices](../user-guide/screenshots/01-home-api35.png)

The home screen keeps passive import separate from network transmission and states the safety boundary before either workflow begins.

## Passive research-capture analysis

| Modbus/TCP | DNP3 |
|---|---|
| ![Modbus/TCP capture result](../user-guide/screenshots/04-passive-modbus-api35.png) | ![DNP3 capture result](../user-guide/screenshots/04-passive-dnp3-api35.png) |

| IEC 60870-5-104 | BACnet/IP |
|---|---|
| ![IEC-104 capture result](../user-guide/screenshots/04-passive-iec104-api35.png) | ![BACnet capture result](../user-guide/screenshots/04-passive-bacnet-api35.png) |

Each file entered the app as a real Android `content://` upload. The UI shows the capture hash prefix, total and supported OT packet counts, inferred endpoints, roles, confidence, and the evidence supporting classification.

## Active authorization and local scope enforcement

| Authorization checkpoint | Out-of-scope stop |
|---|---|
| ![Active authorization screen](../user-guide/screenshots/02-active-authorization-api35.png) | ![Out-of-scope target stopped](../user-guide/screenshots/03-out-of-scope-blocked-api35.png) |

The action is disabled before authorization. A target outside the entered CIDR is rejected in the Case App before the Network Broker is contacted.

## Active emulator outcomes

| PyModbus identity returned | modbus-tk service only |
|---|---|
| ![PyModbus identity-confirmed result](../user-guide/screenshots/05-active-pymodbus-api35.png) | ![modbus-tk service-confirmed result](../user-guide/screenshots/05-active-modbus-tk-api35.png) |

PyModbus returned vendor, product, and revision objects. modbus-tk returned a valid Modbus exception, so the application confirmed the service without fabricating device identity. Conpot also passed the signed end-to-end job; its screenshot is intentionally omitted because the captured frame contained an unrelated Android launcher dialog.

## Reproduction

The instrumentation test writes PNG checkpoints through Android MediaStore. While the emulator is still running, `tools/run_ui_e2e.sh` and `tools/run_active_e2e.sh` pull them into `build/emulator-screenshots`; the workflow publishes them with the test reports and emulator logs.

Documentation images are reduced to 540 × 1200 pixels and a 64-color palette. Their content is otherwise unchanged from the CI output.
