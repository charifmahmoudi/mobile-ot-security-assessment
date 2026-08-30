# ADR 0002: Separate the network broker from the case application

- Status: accepted for P0
- Date: 2026-08-30

## Context

A policy engine inside an Internet-capable Android application is a software convention, not a strong enforcement boundary. UI, report, import and third-party library code would share the ability to open sockets.

## Decision

Ship two Android packages signed by the same release certificate:

- `com.atlasot.scout`: customer data, UI, evidence and reports; no `INTERNET` permission.
- `com.atlasot.netbroker`: network permission, no case database/UI/storage; exposes only a signature-protected bound service accepting signed one-use grants for compiled operations.

The broker verifies Binder caller UID/certificate, grant signature, nonce, time, interface, target and limits. Its API accepts no arbitrary bytes, URL, command or port range.

## Consequences

Positive: Android UID/permission separation makes accidental or compromised UI networking materially harder and keeps the network-capable code small enough to review.

Cost: two APKs, cross-package IPC, coordinated signing/update, broker journal and additional instrumentation tests.

Rejected: one APK with a Kotlin policy module; root helper; generic shell/tool runner; VPN-based interception.
