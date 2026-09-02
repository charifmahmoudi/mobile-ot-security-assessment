# ADR 0007: Integrate live passive capture into the dedicated Android appliance

- Status: Accepted for P0
- Date: 2026-09-01
- Supersedes: [ADR 0003](0003-external-passive-capture.md)

## Context

ADR 0003 selected an external receive-only Raspberry Pi capture appliance because ordinary Android application networking cannot provide whole-segment Ethernet visibility. Subsequent implementation established a narrower dedicated-Android design: a separate Capture Broker boundary plus a platform `AF_PACKET` receive-only daemon can keep raw capture privilege outside the Case App while eliminating the separate management-network accessory.

The underlying visibility constraint has not changed. A phone or USB Ethernet adapter connected to an ordinary switched access port still cannot see arbitrary third-party unicast traffic. Whole-segment evidence requires an approved SPAN/mirror or passive TAP source.

## Decision

For the P0 target architecture:

- retain H1 active identity in the separately constrained Network Broker;
- implement H2 live passive capture inside the **dedicated Android appliance** using an allowlisted USB Ethernet interface connected to approved SPAN/TAP traffic;
- expose passive capture to the Case App only through the bounded Capture Broker AIDL contract;
- confine raw `AF_PACKET` receive privilege to the `atlas_capture` platform daemon;
- keep H3 imported PCAP/PCAPNG as the universal non-live fallback;
- require no-address/no-egress and physical compatibility evidence before H2 is claimed as field-supported.

## Consequences

The field kit can be physically simpler than the former phone-plus-Raspberry-Pi design, but custom Android image maintenance, SELinux integration and USB/NIC qualification become part of the product boundary.

The native daemon and Capture Broker must remain independently reviewable and must not evolve into general-purpose root/network command interfaces.

## Rejected alternatives

- claiming whole-segment passive visibility from an ordinary Android Ethernet connection;
- exposing root or `AF_PACKET` directly to the Case App;
- keeping the external Raspberry Pi as the canonical P0 architecture after the dedicated-appliance implementation was selected;
- using an inline bridge as the default assessment topology.

Current architecture: [Dedicated Android appliance](../architecture/DEDICATED-ANDROID-APPLIANCE.md) and [Network execution](../architecture/NETWORK-EXECUTION.md).
