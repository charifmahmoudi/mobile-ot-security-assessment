# Collecting Evidence

Atlas uses the least intrusive evidence method that can answer the documented assessment question. Collection is always subordinate to authorization, process safety and evidence need.

## Choose the method deliberately

| Situation | Preferred method | Network effect |
|---|---|---|
| Customer already has an approved PCAP/PCAPNG | Offline import | No packet transmission |
| A qualified SPAN/TAP source is available | Live passive capture | Receive-only on the approved capture path |
| One known Modbus controller has an unresolved identity gap | Exact active identity | One bounded Device Identification request to the approved target |
| Scope, visibility or authority is unclear | Stop | No collection until the boundary is resolved |

Do not escalate from passive evidence to active identity simply because more data would be interesting. Active evidence should answer a specific documented gap.

## Offline capture analysis

Before import, preserve what is known about the source:

- original file;
- source/collector;
- collection point;
- collection time/duration;
- authorization/handling context;
- known visibility limits.

A PCAP is a sample of what was observable at its collection point. Absence from a capture is not proof that an asset or communication is absent from the process area.

## Live passive collection

A meaningful live passive assessment requires an approved capture source such as a SPAN/TAP and a supported field configuration. The capture record should preserve the interface/source, duration, stop reason, available packet/drop information and known visibility limits.

An ordinary access port on a switched network does not provide arbitrary segment visibility.

Emulator and virtual-network tests can prove software integration; they do not qualify physical USB Ethernet, power, driver or SPAN/TAP behavior. Use the measured [Compatibility Matrix](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/appliance/COMPATIBILITY-MATRIX.md) for field support claims.

## Exact Modbus identity

When one reconciliation item has a documented identity gap and the target is explicitly authorized, Atlas's initial active P0 method is a bounded Modbus Device Identification request.

Before execution, the assessor should be able to see:

- exact target;
- authorization reference;
- operation being requested;
- expected network effect;
- that no subnet scan, port scan, unit-ID sweep or register read/write is being performed.

The response becomes evidence. It does **not** automatically convert the related asset into a confirmed record; the assessor still reviews the evidence and makes the reconciliation decision.

## Failure behavior

If a target is unavailable, evidence is malformed, authority expires or the route/interface changes, preserve the failure and limitation. Do not silently expand scope or substitute a broader method.

## Exact network contract

This page intentionally does not reproduce grant lifetimes, packet budgets, socket-binding rules or protocol constants. Those are defined in the repository's [Network Execution contract](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/docs/architecture/NETWORK-EXECUTION.md).

Next: [Reconciling Assets](Reconciling-Assets).
