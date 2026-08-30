# ADR 0003: Explicit capture accessory boundary

- Status: proposed
- Date: 2026-08-30

## Decision

Describe capture modes honestly. USB Ethernet provides connectivity; third-party wired traffic requires TAP/SPAN or an approved capture accessory.

## Consequences

The field kit must name tested NIC/TAP combinations. The UI must show current visibility and must never imply whole-segment capture from local-origin traffic.
