# Executive business case

## Investment thesis

Atlas OT Scout is not “Kali Linux on Android.” It is a portable, governed evidence-collection product for industrial asset discovery. Its initial wedge is the moment before an organization can justify or deploy permanent OT monitoring: brownfield baselines, site surveys, contractor handover, audit preparation, M&A/site onboarding and incident triage.

The business case rests on six propositions:

1. **The asset-inventory problem is mandatory and persistent.** CISA and NIST place OT asset knowledge at the foundation of risk management.
2. **Morocco has dense, export-oriented industrial clusters.** The Ministry of Industry reports MAD 898bn of 2024 industrial revenue, MAD 90bn investment and 1,038,133 industrial jobs. Tanger Med Zones reports 1,500 companies and 145,000 jobs.
3. **The incumbent category is optimized for continuous enterprise visibility.** Claroty, Dragos and Nozomi are strong platforms, but a portable baseline is a distinct deployment and buying job.
4. **General scanners are not an OT field workflow.** Nmap, Nessus and Wireshark lack the combined authorization, operational-safety, evidence-confidence and mobile case-management layer.
5. **Android materially changes field economics.** A supported phone and standardized kit can replace travel with laptops/appliances for some survey work, but it cannot bypass network visibility physics.
6. **A channel can reach the market more credibly than direct software sales alone.** Automation integrators, industrial service firms, MSSPs and auditors already possess site trust.

## What is proven

| Claim | Status | Evidence |
|---|---|---|
| Morocco has a large industrial base | Verified | Ministry 2024 barometer |
| Major automotive, phosphate, mining, port, food, cement and aerospace sites exist | Verified | operator and ministry sources |
| Android exposes USB host, BLE and network APIs | Verified | Android documentation |
| USB Ethernet alone does not yield all segment traffic | Engineering fact | switched Ethernet behavior; capture architecture |
| Commercial platforms address OT asset inventory | Verified | vendor product pages |
| Open libraries exist for Modbus, OPC UA and EtherNet/IP | Verified | original repositories |
| Customers will pay proposed prices | Unproven | must be tested through offers/pilots |
| Named Moroccan plants use specific PLC vendors | Mostly unknown | plant-specific evidence is sparse |
| Phone workflow saves enough time to drive purchase | Unproven | prototype benchmark required |

## Strategic customer

The initial economic customer is not “every factory.” It is an organization that repeatedly needs baselines:

- an automation integrator managing multiple client sites;
- an MSSP/audit firm adding OT services;
- a multi-site industrial owner without continuous coverage everywhere;
- an industrial zone or group coordinating supplier/site assurance.

A one-site SME is a secondary customer unless sold through a fixed-scope service.

## Beachhead

Prioritize three clusters:

1. **Tangier/Kenitra automotive and supplier ecosystem:** concentrated plants, integrators and export assurance.
2. **Jorf Lasfar/Safi/Khouribga phosphate, chemicals, power and port corridor:** complex process assets and large asset owners.
3. **Casablanca/Nouaceur regulated and high-value manufacturing:** aerospace, pharmaceuticals, food and corporate buying centers.

## Commercial wedge

Sell an outcome before selling seats:

> “In one controlled visit, establish an evidence-backed OT/IoT baseline, identify what remains unknown, and export a remediation-ready inventory—without deploying a permanent sensor.”

The first paid offer should be a partner-delivered baseline package. Software licensing follows repeat use.

## Defensibility

The defensible assets are not the port scanner:

- signed, safety-reviewed query profiles;
- device identity knowledge with citations and confidence behavior;
- packet/observation corpus and regression tests;
- Android NIC/TAP compatibility evidence;
- local sector and reporting packs;
- integrator workflow and export connectors;
- history of safe assessments and corrections.

## Principal risks

| Risk | Why it can kill the project | Mitigation/gate |
|---|---|---|
| Android capture limitations | product cannot see promised traffic | prove hardware matrix before UI scale-up |
| OT safety incident | destroys trust and creates liability | passive-first; deterministic gate; lab and independent review |
| Weak identity accuracy | inventory is not credible | evidence-linked claims and controlled corpus benchmarks |
| Incumbent adds mobile collector | feature differentiation collapses | channel, price, offline workflow and local packs |
| Support complexity | device/protocol matrix consumes margin | narrow certified matrix; tiered coverage; paid packs |
| No willingness to pay | technical success without business | sell fixed-scope pilot before broad implementation |
| Licensing conflict | cannot commercialize bundle | component boundary and legal review before adoption |

## Go/no-go criteria

Proceed from design to prototype only if:

- at least three protocol identity paths have documented, safe packet definitions;
- an Android phone can ingest mirrored Ethernet evidence through at least two supported kit configurations;
- target-account and channel analysis produces at least ten credible outreach routes;
- a fixed-scope offer and report example are ready;
- project licensing and dependency boundaries are decided.

Proceed to field pilot only if at least two organizations sign a written pilot scope or pay for a lab/site baseline, and safety/legal gates pass.
