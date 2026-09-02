# ADR 0003: Use an external receive-only path for whole-segment capture

- Status: **Superseded by [ADR 0007](0007-dedicated-android-passive-capture.md)**
- Date: 2026-08-30

## Context

A USB Ethernet adapter on ordinary Android supports IP networking but does not establish promiscuous whole-segment visibility. A switched network sends third-party traffic to a phone only when the network provides a mirror/TAP path, and an ordinary app cannot assume raw Ethernet capture privileges.

## Historical decision

The initial P0 design used:

- H1 direct USB Ethernet for approved socket-based active identity;
- an external receive-only capture appliance connected to customer SPAN/TAP traffic for H2;
- H3 imported PCAP/PCAPNG when live capture was unavailable.

The proposed external appliance had no addressed/routed OT interface and sent captured data to Android over a separate management link.

## Why it was superseded

The dedicated Android architecture later introduced a separate Capture Broker and confined native `AF_PACKET` receive daemon, allowing H2 capture to live inside the dedicated appliance without giving the Case App raw-packet privilege. [ADR 0007](0007-dedicated-android-passive-capture.md) records that replacement.

The original visibility conclusion remains valid: neither root nor a USB Ethernet adapter defeats switch forwarding; third-party visibility still requires approved SPAN/TAP delivery.
