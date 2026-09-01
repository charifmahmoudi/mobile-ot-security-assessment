# Representative evidence-package sample

_Status: customer-facing example for problem validation. No customer data, live-system observation or production-access claim is represented._

This example shows the type of reviewed output proposed by the [Atlas Evidence Baseline Pilot](OFFER.md). It is a conversation aid, not proof that Atlas has assessed the recipient's environment.

## Page 1 — Expected baseline and review question

### Bounded area

**Representative area:** Drinking-water pumping station PS-04  
**Decision:** Is the available asset record sufficiently supported for project handover?  
**Evidence boundary:** Approved inventory extract, as-built diagram and a representative imported packet capture.  
**Excluded:** Vulnerability testing, broad discovery, configuration changes and claims about equipment not visible in the approved evidence.

### Expected records

| Record | Approved source | Expected identity | Review state before evidence |
|---|---|---|---|
| PLC-PS04-01 | Commissioning inventory v3 | Controller for pump group A | Expected; not corroborated |
| HMI-PS04-01 | As-built drawing AB-17 | Local operator interface | Expected; not corroborated |
| RTU-PS04-01 | Telemanagement schedule TS-09 | Remote telemetry unit | Expected; identifier incomplete |
| SW-PS04-01 | Network diagram ND-04 | Industrial access switch | Expected; model not recorded |

### Review questions

- Which records are supported by an approved observation?
- Which identifiers conflict between sources?
- Which items remain unresolved?
- Who is authorized to accept or reject each proposed inventory change?

## Page 2 — Evidence-linked delta

| Record | Representative evidence | Proposed state | Reviewer decision required |
|---|---|---|---|
| PLC-PS04-01 | Imported traffic contains repeated communication from the expected address and a matching approved device identifier | Corroborated within the evidence boundary | Accept or request additional commissioning evidence |
| HMI-PS04-01 | No approved evidence source contains an HMI identity | Unresolved; not declared missing from the site | Assign owner and evidence source |
| RTU-PS04-01 | Approved inventory and representative evidence use different identifiers for the same expected address | Conflict | Confirm authoritative identifier before handover |
| SW-PS04-01 | Network diagram expects a switch; the imported evidence does not expose a model identity | Expected but unsupported | Keep as expected or obtain an approved source |

### Handoff summary

- **Corroborated:** 1
- **Conflicting:** 1
- **Unresolved:** 2
- **Unexpected assets:** none asserted
- **Unsafe or out-of-scope actions:** none performed
- **Decision supported:** the handover record is not ready for unconditional acceptance until the RTU conflict and evidence ownership are resolved.

### Limitations

- Absence from the imported evidence is not proof that a device is absent.
- A network address is not, by itself, a durable asset identity.
- The example does not establish vendor, firmware, vulnerability or configuration state.
- Production use would require the customer's written scope, evidence-custody rules and authorization.

## Discussion prompt

> Would a reviewed delta like this reduce rework or ambiguity in one current handover, GIS update or maintenance-baseline decision? Which fields, reviewers or acceptance rules would need to change for your process?

Return to the [outreach sprint](OUTREACH-SPRINT.md).
