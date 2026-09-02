# Field Setup and Safety

Atlas field use depends on both software controls and the physical network attachment. A safe software design does not automatically qualify an arbitrary phone, USB adapter, hub or SPAN/TAP arrangement.

## Supported pilot kit

For a customer pilot, use only a hardware combination that has been measured and recorded in the repository's [Compatibility Matrix](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/appliance/COMPATIBILITY-MATRIX.md).

A qualified entry should identify, at minimum:

- phone model;
- exact OS/build;
- powered USB hub where required;
- USB Ethernet NIC and relevant hardware/driver identity;
- approved SPAN/TAP arrangement;
- power/charger configuration;
- known restrictions.

Do not generalize one successful combination into support for all Android phones or USB NICs.

## Passive attachment

For live passive evidence, confirm that:

- the customer has approved the capture point;
- the SPAN/TAP actually delivers the intended traffic;
- the OT-facing interface has the required no-address/no-egress posture;
- Atlas is attached to the intended interface;
- capture duration and visibility are understood;
- packet loss/drop information is recorded where available.

An ordinary switched access port is not equivalent to a SPAN/TAP source.

## Active attachment

Before exact active identity:

- verify the exact target and authorized scope;
- verify exclusions;
- verify operating window and stop authority;
- confirm the selected Android network/interface is the approved path;
- review the displayed operation before execution.

If the target or route is unclear, stop. Do not widen scope to make a test pass.

## Physical conditions to monitor

During field use, watch for:

- USB detach/reconnect;
- unstable Ethernet link;
- unexpected route/address changes;
- storage pressure;
- device thermal or power instability;
- process/network instability;
- operator or approver stop request.

These conditions may require pausing or stopping collection and recording the resulting evidence limitation.

## Emergency and safe stop

Local stop authority must remain available without depending on cloud connectivity. Stopping a method must not trigger an alternative discovery method.

## What emulator CI proves—and does not prove

The project uses Android and OT emulation to verify application, IPC, parser and bounded network behavior continuously. Virtual network tests can also exercise receive-only capture software.

They do **not** prove:

- physical USB power stability;
- NIC driver compatibility on the target phone;
- actual SPAN/TAP visibility;
- field packet-loss performance;
- thermal behavior under customer-site load.

Those require measured physical qualification.

For exact current qualification, use [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md), the [Compatibility Matrix](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/appliance/COMPATIBILITY-MATRIX.md), and the [P0 Test and Acceptance Plan](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/poc/TEST-AND-ACCEPTANCE.md).
