# ADR 0003: Use an external receive-only path for whole-segment capture

- Status: accepted for P0
- Date: 2026-08-30

## Context

A USB Ethernet adapter on ordinary Android supports IP networking but does not establish promiscuous whole-segment visibility. A switched network sends third-party traffic to a phone only when the network provides a mirror/TAP path, and an ordinary app cannot assume raw Ethernet capture privileges.

## Decision

Use:

- H1 direct USB Ethernet for approved socket-based A1 requests;
- H2 capture appliance connected to a customer SPAN/TAP for passive PCAPNG;
- H3 imported PCAP/PCAPNG when live H2 is unavailable.

The H2 OT-facing interface has no address, forwarding or bridge and must emit zero frames. Android receives signed chunks over an isolated authenticated management link.

## Consequences

The report can state exactly what was visible. The kit gains an accessory and provisioning burden. Raspberry Pi 4 is the controlled PoC reference, not the final commercial hardware.

Rejected: claiming passive capture from an ordinary RJ45 dongle; rooted production phone; inline transparent bridge; silent packet-capture degradation.
