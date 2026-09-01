# Synthetic evidence-package example

_Status: fictional illustration only._

All identifiers, records, observations and outcomes below are synthetic. This document contains no customer data, no live-system observation and no evidence of Atlas performance in a production environment. It must not be presented as a completed assessment.

The example illustrates the proposed structure of an [Atlas Evidence Baseline Pilot](OFFER.md).

## Page 1 — Expected baseline and review question

### Illustrative boundary

**Fictional area:** Drinking-water pumping station PS-04  
**Illustrative decision:** Is the available asset record sufficiently supported for project handover?  
**Illustrative evidence boundary:** Approved inventory extract, as-built diagram and a synthetic imported packet capture.  
**Excluded:** Vulnerability testing, broad discovery, configuration changes and claims about equipment outside the stated evidence.

### Synthetic expected records

| Record | Fictional approved source | Expected identity | Illustrative review state |
|---|---|---|---|
| PLC-PS04-01 | Commissioning inventory v3 | Controller for pump group A | Expected; not corroborated |
| HMI-PS04-01 | As-built drawing AB-17 | Local operator interface | Expected; not corroborated |
| RTU-PS04-01 | Telemanagement schedule TS-09 | Remote telemetry unit | Expected; identifier incomplete |
| SW-PS04-01 | Network diagram ND-04 | Industrial access switch | Expected; model not recorded |

### Review questions

- Which records are supported by an approved observation?
- Which identifiers conflict between sources?
- Which items remain unresolved?
- Which authorized function may accept or reject each proposed inventory change?

## Page 2 — Synthetic evidence-linked delta

| Record | Fictional evidence | Illustrative state | Reviewer decision required |
|---|---|---|---|
| PLC-PS04-01 | Synthetic traffic contains repeated communication from the expected address and a matching fictional device identifier | Corroborated within the example boundary | Accept or request additional evidence |
| HMI-PS04-01 | The synthetic evidence set contains no HMI identity | Unresolved; not declared absent from the site | Assign an authorized evidence source |
| RTU-PS04-01 | The fictional inventory and synthetic evidence use different identifiers for the same expected address | Conflict | Confirm the authoritative identifier |
| SW-PS04-01 | The fictional network diagram expects a switch; the synthetic evidence does not expose a model identity | Expected but unsupported | Retain as expected or obtain an approved source |

### Illustrative handoff summary

- **Corroborated:** 1
- **Conflicting:** 1
- **Unresolved:** 2
- **Unexpected assets:** none asserted
- **Unsafe or out-of-scope actions:** none represented
- **Illustrative decision:** the fictional handover record would require additional review before unconditional acceptance.

### Limitations

- Absence from an evidence set is not proof that a device is absent.
- A network address is not, by itself, a durable asset identity.
- The example does not establish vendor, firmware, vulnerability or configuration state.
- Production use would require written scope, evidence-custody rules and authorization.
- The example does not demonstrate product readiness, field validation or customer acceptance.

## Discussion prompt

> Would a reviewed delta in this format help a real handover, GIS update or maintenance-baseline decision? Which fields, reviewers or acceptance rules would be required?

Return to the [engagement guide](ENGAGEMENT-GUIDE.md).
