# P0-WATER Assessment Method

## Purpose and assurance statement

This method produces a scoped OT asset-inventory and network-exposure assessment for one water/wastewater control segment. It is aligned to the safety principles in [NIST SP 800-82 Rev. 3](https://csrc.nist.gov/pubs/sp/800/82/r3/final) and contributes evidence to NIST CSF 2.0 Asset Management outcomes. It does not certify compliance, prove that unobserved assets are absent, or test exploitability.

## Rules of engagement

The signed authorization must answer all of the following before collection:

| Field | Required content |
|---|---|
| Authority | Legal entity, approver name/role and authorization artifact |
| Site/process | Exact facility and pumping/treatment process area |
| Scope | VLAN/CIDR, target IP/MAC list, capture point and physical areas |
| Exclusions | Safety systems, vendor-managed devices, redundant controllers, time-sensitive equipment |
| Time | UTC start/end and maintenance/operating condition |
| Methods | P0 passive sources and named A1 profiles |
| Limits | Targets, packets, retries, timeouts, concurrency, capture duration and storage |
| Stop | Process alarm, network instability, route change, approver request, device detach, budget/expiry |
| Data | Classification, photos, payload retention, capture inclusion, export destination and deletion date |
| Response | Operational contact, security contact and incident/escalation procedure |

A verbal approval is recorded as context but does not unlock A1 operations.

## Evidence hierarchy

| Level | Evidence | Appropriate claim |
|---|---|---|
| E1 | Exact physical nameplate/asset tag reviewed by assessor | Physical vendor/model/serial/location at observation time |
| E2 | Protocol identity with raw response and specific identifiers | Network identity for that endpoint |
| E3 | Customer inventory/design/CMMS export | Customer-declared identity and ownership |
| E4 | Passive packet metadata and stable protocol behavior | Endpoint, communication and service observation |
| E5 | OUI, hostname, open port, Wi-Fi/BLE name | Candidate attribution only |
| E6 | Assessor interview/note | Context requiring corroboration |

“Confirmed identity” requires E1 or E2 plus an independent compatible source. An OUI never establishes product model or firmware.

## Sampling

The report must disclose whether the case is:

- census: all records in the bounded segment were selected;
- risk-based sample: all critical controllers/gateways plus a documented sample of other classes;
- convenience sample: access-driven, not representative.

For a census, every imported in-scope record must finish as confirmed/probable, not observed, excluded or unresolved. For sampling, population, sample logic and excluded classes are stated.

## Collection controls

### Physical

Photograph only authorized equipment. Capture a wide context image and a close nameplate image when permitted. Strip GPS unless authorized. OCR suggestions are visually checked. Do not open energized cabinets or bypass site electrical/safety procedures.

### Passive network

Record switch, port, SPAN direction, VLAN handling, capture accessory, link speed, capture filter, start/end, drops and clock source. Use no capture filter in the lab; production filters require documented justification. Minimum recommended window is one complete operating cycle or 30 minutes, whichever is longer, but the report must describe what operational states occurred.

### Active identity

A1 is used only to resolve a documented evidence gap. The assessor must capture:

- why the query is necessary;
- exact target and observed/imported basis;
- signed profile version/hash;
- operator confirmation;
- request/response hashes and timestamps;
- packet/retry/timeout counts;
- result and stop reason.

## Analysis workflow

1. Normalize imported records without overwriting originals.
2. Parse artifacts into observations with byte offsets.
3. Form candidate endpoints.
4. Propose asset matches using strong keys first.
5. Review conflicts and ambiguous merges.
6. Add process criticality from the operational owner.
7. Run deterministic water-pack rules.
8. Review every finding and attach an owner/action.
9. Perform report QA against the finalized snapshot.
10. Sign and export.

## Asset status

| Status | Meaning |
|---|---|
| Confirmed | Confidence ≥0.90 and sufficient independent evidence |
| Probable | Confidence 0.70–0.89; no material contradiction |
| Tentative | Confidence 0.40–0.69; not counted as inventory coverage |
| Insufficient | Confidence <0.40 |
| Not observed | In inventory but no adequate observation in this method/window |
| Excluded | Explicitly outside collection, with reason |
| Unexpected | Observed candidate with no accepted inventory record |
| Conflict | Sources disagree on a material identity attribute |

## Architecture review

The assessor defines proposed zones and conduits from customer diagrams and operational review. Atlas summarizes observed communications, but does not infer safety function or trust solely from traffic.

For each endpoint pair record:

- source/destination asset;
- VLAN/IP/port/protocol;
- first/last seen and count;
- declared conduit and policy;
- expected/unexpected/unknown review;
- evidence artifact and offsets.

WAT-ARC-001 fires only after a reviewer marks the flow contrary to the declared architecture.

## Finding quality gate

A finding is reportable only if it includes:

- condition stated without exaggeration;
- affected in-scope asset or architecture object;
- evidence IDs and method;
- observed date/window;
- confidence and limitations;
- consequence assigned or accepted by the operational owner;
- evidence-based exposure score;
- actionable recommendation;
- accountable owner and target date, or explicit “unassigned”;
- reviewer decision.

A port alone cannot prove a vulnerable service. A product/version does not produce an end-of-support finding without a dated vendor source. A CVE is not assigned without an exact product/version match and applicability review.

## Report QA checklist

Before finalization the reviewer verifies:

- authorization hash and dates;
- scope/exclusion consistency;
- capture visibility and loss disclosure;
- inventory denominator and sampling method;
- every metric reproducible from exported JSON;
- high/critical findings have sufficient evidence or say “urgent verification”;
- rejected findings excluded from executive totals;
- active packet ledger complete;
- no credentials or unnecessary payloads in report;
- recommendations preserve safety/availability;
- report and manifest hashes match;
- HTML/PDF content agrees; HTML is normative.

## Customer handoff

Deliver:

1. signed assessment ZIP;
2. executive and technical reports;
3. reconciled asset and exception CSVs;
4. verification instructions;
5. optional raw captures only if retention/export authorization permits;
6. a one-hour review recording decisions and corrections.

Customer acceptance is recorded separately from technical finalization. A correction creates a new case revision and new manifest.
