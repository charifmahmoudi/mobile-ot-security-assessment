# ADR 0001: Android-first and offline-first

- Status: proposed
- Date: 2026-08-30

## Decision

Prototype on Android with local encrypted cases and no required cloud service.

## Why

Android supports a broad price range and USB host/BLE/network APIs. Offline behavior matches plant connectivity and data-control constraints.

## Consequences

OEM/kernel USB-NIC differences require a compatibility matrix. Raw capture and Wi-Fi monitor capabilities cannot be assumed. iOS is not in prototype scope.
