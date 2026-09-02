# ADR 0006: Make the capture-visibility boundary explicit

- Status: Accepted for P0
- Date: 2026-08-30

## Context

USB Ethernet connectivity and whole-segment packet visibility are different capabilities. A switched network does not deliver arbitrary third-party unicast frames to an ordinary access port.

## Decision

Every collection mode and report must describe the actual visibility source. USB Ethernet may provide active connectivity; third-party wired traffic requires an approved SPAN/mirror or passive TAP path. Imported captures retain their supplied capture context.

The implementation of the live passive path is defined separately by [ADR 0007](0007-dedicated-android-passive-capture.md).

## Consequences

- UI and reports must show capture/visibility type and limitations.
- Compatibility evidence must name the tested phone/NIC/TAP or mirror configuration.
- “Not observed” cannot be converted into “absent” without adequate visibility and an explicit verification method.
- Product material must never imply whole-segment visibility merely because Ethernet is connected.
