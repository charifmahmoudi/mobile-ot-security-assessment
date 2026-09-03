# Atlas OT Scout

[Project website](https://charifmahmoudi.github.io/mobile-ot-security-assessment/) · [Source repository and README](https://github.com/charifmahmoudi/mobile-ot-security-assessment) · [Current implementation](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md)

Atlas OT Scout helps an authorized assessor reconcile a customer's expected OT asset inventory with bounded field evidence and produce a reviewed, traceable assessment result.

The first product focus is water and wastewater control environments.

The purpose is not inventory accuracy for its own sake. Atlas is intended to reveal discrepancies before a maintenance shutdown, while the responsible team can still confirm the target, adapt the planned procedure and resources, or postpone the action before taking equipment out of service. The [project website](https://charifmahmoudi.github.io/mobile-ot-security-assessment/#case) presents this as the “Maintenance starts tomorrow” case.

## The question Atlas answers

**Does the available evidence support the asset baseline the customer currently relies on, and which discrepancies require action?**

Atlas is designed for situations where commissioning records, maintenance inventories, drawings and the installed environment may no longer agree.

## What the customer receives

For one defined process area, Atlas distinguishes:

| Result | Meaning |
|---|---|
| **Confirmed** | Evidence supports the expected asset record |
| **Probable** | A likely match exists, but important identity evidence is still missing |
| **Conflict** | Expected and observed identity attributes materially disagree |
| **Unexpected** | An observed OT endpoint has no accepted match in the expected baseline |
| **Not observed** | An expected record was not supported by the available evidence sample |
| **Unresolved** | The available evidence does not support a defensible decision |

The objective is a **reviewed asset reconciliation and exception set**, not a list of discovered IP addresses.

## Assessment flow

1. **Define and authorize** the customer, site, process area, scope and permitted evidence methods.
2. **Load the expected baseline** without treating customer-declared data as discovered fact.
3. **Collect or import evidence** using the least intrusive approved method.
4. **Reconcile expected and observed state**, preserving conflicts and uncertainty.
5. **Review and deliver** the accepted findings, limitations and assessment package.

See [Assessment Workflow](Assessment-Workflow.md) for the operating sequence and [Asset Reconciliation](Asset-Reconciliation.md) for the decision model.

## Evidence methods

The P0-WATER workflow is intentionally bounded:

- approved PCAP/PCAPNG analysis;
- qualified receive-only SPAN/TAP capture;
- exact Modbus Device Identification for one explicitly authorized target when an identity gap justifies it.

Atlas is not designed as a general-purpose network scanner, exploitation framework or continuous-monitoring platform.

## Customer pilot

A pilot focuses on one control area and one measurable question. The customer provides the expected baseline and authorized access to suitable evidence; Atlas returns the reconciled state, material exceptions, evidence references, limitations and reviewed output.

See [Customer Pilot](Customer-Pilot.md).

## Safety and technical detail

For collection boundaries, visibility limits and links to the exact engineering specifications, see [Safety & Technical Boundaries](Safety-and-Technical-Boundaries.md).

Current executable capability is maintained in [`IMPLEMENTATION.md`](https://github.com/charifmahmoudi/mobile-ot-security-assessment/blob/main/IMPLEMENTATION.md). Repository navigation, build instructions and the complete documentation map begin in the [main README](https://github.com/charifmahmoudi/mobile-ot-security-assessment#readme).
