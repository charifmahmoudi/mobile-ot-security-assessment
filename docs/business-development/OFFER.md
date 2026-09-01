# Atlas device evaluation and services offer

## Free device evaluation

A suitable prospect may receive a configured Atlas OT Scout device for **30 calendar days free of charge**. The period may be extended once for another 30 days, making **60 days the maximum free evaluation period**.

The purpose is to determine whether Atlas produces a useful result for one bounded water, wastewater or industrial OT area. Suitable cases include:

- a telemanagement or remote-station group;
- a pumping station, reservoir or lift station;
- a treatment or desalination process area;
- an EPC commissioning or handover package;
- an inventory, GIS or maintenance-record reconciliation;
- a bounded OT networking or security review.

The evaluation scope, device custodian, permitted evidence and return arrangements are agreed before the device is handed over. The evaluation may begin with approved inventories, as-built material, configuration exports or imported PCAP/PCAPNG files; broad production-network access is not required.

## After the evaluation

### Build an independent device

The organisation may use [this public repository](../../README.md) to build and maintain its own implementation and purchase a compatible Samsung phone and peripherals independently.

The repository currently identifies specific Galaxy S20 Exynos variants as candidates in the [compatibility matrix](../appliance/COMPATIBILITY-MATRIX.md). Exact model identity matters. These models remain proof-of-concept candidates until the physical hardware and receive-only acceptance tests documented in the repository are completed.

There is no software licence fee for the self-build option. The organisation is responsible for hardware sourcing, build, flashing, testing and maintenance unless it separately requests assistance.

### Keep the evaluation device

The organisation may keep the supplied device by paying:

- the actual cost of the phone and supplied accessories; and
- a small installation, preparation and configuration fee.

Any additional integration or consulting is agreed separately. The commercial value is the prepared hardware and service, not a compulsory proprietary licence.

### Arrange setup on a customer-owned device

The organisation may supply a compatible, dedicated Samsung device and schedule an on-site setup session. The session may cover model verification, operating-system installation, Atlas installation, configuration and a basic functional check.

This setup requires bootloader unlocking and flashing a custom operating-system image. The process erases the device. Samsung also documents that unofficial software can permanently trip the [Knox Warranty Bit](https://docs.samsungknox.com/admin/knox-platform-for-enterprise/faq/), after which Samsung Pay, Secure Folder and other Knox-dependent functions may stop working. The device should therefore be dedicated to Atlas rather than used as a personal or enterprise-managed phone.

LineageOS publishes [Galaxy S20 `x1s` build instructions](https://lineageos.github.io/lineage_wiki/devices/x1s/build/), while this repository defines the narrower model and acceptance requirements used by Atlas.

## OT networking and security consulting

The team is available for consulting requested directly by a prospect, with or without a device evaluation. Relevant services include:

- OT network architecture and segmentation;
- passive visibility and packet-capture planning;
- SCADA, PLC, RTU and industrial-protocol review;
- asset inventory and evidence governance;
- commissioning and handover support;
- remote-site and telemanagement architecture;
- OT cybersecurity risk assessment;
- incident readiness and evidence preservation;
- vendor, integrator and project technical review;
- Atlas deployment, integration and training.

Consulting scope and commercial terms are agreed separately from the free device evaluation.

## End of the free period

At the end of 30 days, or at the end of the approved extension, the prospect selects one of these outcomes:

- return the device;
- build an independent implementation;
- keep the prepared evaluation device;
- arrange setup on a compatible device;
- request consulting services.

A free evaluation ends after a maximum of 60 days unless the device has been purchased or another separately agreed engagement has begun.

Return to the [business-development guide](README.md).
