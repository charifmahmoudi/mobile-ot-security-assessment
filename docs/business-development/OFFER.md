# Atlas evaluation and services offer

Atlas can be evaluated as a prepared field device, independently built from the public repository, installed on compatible customer-owned hardware, or supported through a separately scoped technical engagement.

The recommended starting point for a new organization is a bounded evaluation around one real OT evidence question.

## Device evaluation

A suitable prospect may receive a configured Atlas OT Scout device for **30 calendar days free of charge**. The evaluation can be extended once by mutual agreement for a further 30 days, making **60 days the maximum free evaluation period**.

The purpose is to determine whether Atlas produces useful, defensible evidence for one authorized water, wastewater or industrial OT workflow. Typical evaluation questions include:

- reconciling an expected asset list with field/network evidence;
- reviewing evidence around commissioning or handover;
- establishing a bounded working baseline before maintenance;
- assessing whether imported/passive evidence is sufficient for an inventory or architecture question;
- testing the Atlas evidence/provenance model inside an audit or engineering methodology.

Before the device is handed over, the parties agree the evaluation scope, responsible custodian, permitted evidence sources, authorization conditions, data-handling expectations and return arrangements.

Network collection is used only where the agreed method, site authorization and current product capability permit it. Approved documents or imported packet evidence may be used when they are sufficient for the evaluation question.

## Evaluation outcome

At the end of the agreed evaluation period, the organization may:

1. return the evaluation device;
2. build and maintain Atlas independently from the public repository;
3. keep the prepared device and pay the agreed hardware and preparation cost;
4. request setup on a compatible dedicated customer-owned device;
5. agree a separate support, integration or consulting engagement.

A successful evaluation does not create an automatic subscription or proprietary software-license obligation.

## Independent self-build

Atlas is available through the [public repository](../../README.md). An organization can source compatible hardware, build the software and maintain its own implementation.

There is no compulsory proprietary software-license fee for this option. The organization is responsible for:

- hardware sourcing;
- operating-system and application build;
- installation and configuration;
- compatibility testing;
- updates and maintenance;
- internal security and deployment approval.

Support can be contracted separately if required.

## Prepared Atlas device

If the organization chooses to keep a supplied evaluation device, the commercial price is based on:

- the agreed phone and accessory cost; and
- an agreed preparation, installation and configuration fee.

Any integration, training, support or consulting beyond the prepared-device scope is quoted separately unless it is explicitly included in the agreement.

Hardware represented as supported should match the evidence in the [compatibility matrix](../appliance/COMPATIBILITY-MATRIX.md).

## Setup on customer-owned hardware

Atlas can be prepared on a compatible **dedicated** customer-owned device.

A setup engagement may include:

- hardware/model verification;
- operating-system installation where appropriate;
- Atlas installation and configuration;
- accessory configuration;
- basic functional verification;
- handover of the prepared device.

A personal or normally managed employee phone is not an appropriate target. Bootloader unlocking and custom-image installation can erase the device and may permanently alter vendor security or warranty features. Samsung, for example, documents that unofficial software can permanently trip the [Knox Warranty Bit](https://docs.samsungknox.com/admin/knox-platform-for-enterprise/faq/), after which Knox-dependent services may stop working.

Current platform and hardware restrictions are described in the [appliance documentation](../appliance/README.md).

## Technical services

Technical services can be contracted independently of a device evaluation.

Possible engagements include:

- OT network architecture and segmentation review;
- passive visibility and packet-capture planning;
- SCADA, PLC, RTU and industrial-protocol review;
- asset inventory and evidence-governance design;
- commissioning and handover support;
- remote-site and telemanagement architecture;
- OT cybersecurity risk assessment;
- incident-readiness and evidence-preservation planning;
- vendor, integrator or project technical review;
- Atlas deployment, integration and training.

Each service engagement is scoped separately with agreed objectives, deliverables, authorization, responsibilities and commercial terms.

## What Atlas is not sold as

Atlas is not offered as:

- a legally qualified industrial-audit provider;
- a certification service;
- a penetration-testing or exploitation platform;
- a guarantee of complete network visibility;
- a replacement for SCADA, GIS, CMMS or engineering systems;
- a claim of field capability beyond the current tested implementation.

For technical due diligence, use the [current implementation](../../IMPLEMENTATION.md), [P0 product contract](../poc/WATER-WASTEWATER-POC.md) and [architecture documentation](../architecture/README.md).

## Starting an evaluation

A useful evaluation begins with five points:

1. the operational or professional question to answer;
2. the exact site/process scope;
3. the people responsible for technical evaluation and authorization;
4. the evidence/collection methods that are permitted;
5. the result that would justify continuing, changing or stopping the use of Atlas.

The goal is a controlled decision about usefulness, not a broad technology trial without an owner or success criterion.
