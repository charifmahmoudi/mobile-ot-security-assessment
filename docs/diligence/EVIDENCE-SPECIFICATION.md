# Evidence specification for method review

This is the concrete artifact to place before a qualified industrial-audit provider.

## Case record

Required: case ID, contracting/authorizing entity, operator, scope, excluded targets, time window, interfaces, capture visibility, retention, emergency contact, policy-pack version and signatures/approvals.

## Visibility declaration

The report must state one or more modes:

- imported capture;
- local-origin traffic;
- broadcast/multicast delivered to interface;
- TAP/SPAN mirrored wired traffic;
- connected Wi-Fi metadata;
- raw Wi-Fi capture through separately supported hardware;
- BLE advertisements;
- BLE active connection.

It must never translate “USB Ethernet connected” into “segment observed.”

## Observation

An observation is immutable collected evidence, not an asset conclusion. Fields include time, interface, source/destination, protocol, extracted field, raw-evidence reference, parser/version and collection mode.

## Identity claim

An identity claim points to observations and contains subject, attribute, asserted value, confidence method, rule/version, contradictions and review state. OUI alone cannot support a model claim.

## Active execution

Every active execution records case authorization, signed profile ID/hash, operator approval, destination, request hash/bytes, response reference, start/end, packet/retry count, stop reason and errors.

## Export acceptance questions

A reviewer should answer:

1. Are scope and authority fields sufficient for the audit method?
2. Does visibility disclosure prevent overclaiming?
3. Can observations and claims be independently reproduced?
4. Are active execution records sufficient to review tool behavior?
5. Which fields contain sensitive or personal data?
6. Which data must remain in Morocco or at the customer?
7. Which formats can enter current working papers/inventory tools?
8. What evidence is missing for industrial audit qualification?
9. May the tool be used by a qualified provider without being independently qualified?
10. What retention and signing requirements apply?

Machine-readable drafts are in `schemas/`.
