# Commercial diligence summary

_Reviewed: 1 September 2026._

This page is the retained synthesis of the former diligence section. It combines the strongest source-backed facts with the analysis that follows from them. It is not a market forecast and it does not convert open questions into claims.

## Bottom line

The evidence supports continuing Atlas OT Scout as a **research prototype aimed first at qualified industrial auditors and industrial automation/service providers in Morocco**. It does not yet support a revenue forecast, a defensible TAM/SAM/SOM, a proven ROI claim, a specific product price, or a claim that Atlas is uniquely differentiated.

The opportunity is therefore **credible but commercially unvalidated**.

The strongest case is not that Moroccan plants broadly “need a mobile cybersecurity scanner.” The stronger case is narrower: Morocco has a documented industrial-audit regime, a small identifiable group of qualified industrial-audit providers, recurring automation and telemanagement work, and public evidence of heterogeneous PLC/SCADA environments. That creates a plausible need for controlled, repeatable evidence collection. Whether Atlas is the right tool for that work still has to be demonstrated with practitioners.

## What the evidence establishes

### A real audit and assurance channel exists

DGSSI treats industrial-systems audit as a qualification domain and evaluates qualified providers on their methods and tools. The reviewed DGSSI table listed eight qualified audit providers, three of which visibly carried industrial-systems-audit qualification: NEAR SECURE, DATAPROTECT and Techso Group. Morocco's Law 05-20 and related framework also create periodic cybersecurity-audit obligations for applicable vital entities and sensitive information systems.

Sources: [DGSSI regulated services and qualified providers](https://www.dgssi.gov.ma/fr/prestations-et-produits-reglementes) and [Law 05-20](https://www.dgssi.gov.ma/fr/loi-ndeg-0520-relative-la-cybersecurite).

**Analysis:** this is a better initial route to market than an invented generic plant-manager persona. The first useful customer research set is finite and identifiable. Atlas should be evaluated as a tool that can fit an existing qualified methodology, not as a replacement for a qualified audit provider.

### Industrial automation activity is observable and heterogeneous

Public procurement shows current or planned work around automata maintenance, telemanagement, pumping, surveillance, VTS, access control, electrical systems and network infrastructure. Examples include ONEE's automata-maintenance procurement at Mohammedia and ANP telemanagement or infrastructure programmes at Essaouira, Jorf Lasfar, Agadir and Nador.

Public Morocco-linked job descriptions also name PLC, HMI and SCADA work and technologies including Siemens, Schneider, Rockwell and Omron environments; Modbus, PROFIBUS, PROFINET and EtherNet/IP; and, in some roles, OPC UA, IEC 104 and IEC 61850.

Sources include [ONEE procurement](https://www.one.org.ma/FR/pages/aofiche.asp?id=18772), [ANP Essaouira](https://www.anp.org.ma/fr/services/appels-offres/3836), [ANP Agadir programme](https://www.anp.org.ma/fra/Lists/AppelOffres/Attachments/4307/Programme%20Pr%C3%A9visionnel%20Triennal%202026-2028_DPAR.pdf), [ANP Nador programme](https://www.anp.org.ma/fra/Lists/AppelOffres/Attachments/4342/PP%20DPNR%202026%2C%202027%20et%202028-signed.pdf), and the public employer listings linked in the [prospect directory](PROSPECT-DIRECTORY.md).

**Analysis:** the evidence justifies a multi-vendor, protocol-aware product. It does not justify claiming a protocol's market share or assuming that a job requirement describes a complete installed base. Atlas should prioritize what is repeatedly evidenced rather than trying to support every industrial protocol.

### Morocco is industrially large, but that is not a market-size calculation

The Ministry of Industry reported MAD 898 billion in industrial revenue, MAD 90 billion in investment and 1,038,133 industrial jobs for 2024. Tanger Med Zones reports roughly 1,500 companies and 145,000 jobs across its platform.

Sources: [Ministry of Industry 2024 barometer](https://www.mcinet.gov.ma/fr/actualites/barometre-de-lindustrie-nationale-lindustrie-marocaine-franchit-un-nouveau-cap-en-2024) and [Tanger Med Zones](https://www.tangermedzones.com/).

**Analysis:** these figures establish industrial density and useful prospecting geographies. They do **not** establish the number of sites that need Atlas, assessment frequency, or willingness to pay. The previous numeric market scenarios were correctly withdrawn. Current TAM/SAM/SOM remains unknown.

## Who should be targeted first

### Primary: qualified industrial-audit providers

This is the strongest first segment because the regulatory framework explicitly recognizes industrial-system audits and evaluates provider methods and tooling. A practitioner serving multiple clients also gives Atlas a chance to prove repeatability across engagements rather than relying on one operator's internal process.

The key diligence question is not “do auditors like the idea?” It is whether an auditor will accept Atlas evidence—provenance, packet-safety controls, observations, reconciliation and export—as usable within an approved methodology.

### Secondary: automation integrators and industrial service providers

Public hiring and procurement evidence shows work spanning multiple vendors, protocols, maintenance tasks and customer sites. Portable, repeatable evidence capture could be useful during commissioning, troubleshooting, maintenance, inventory reconciliation and handover.

The proposition remains testable rather than proven: Atlas must show that it reduces equipment burden, reconciliation work or report preparation compared with the provider's existing tools.

### End-user operators

Large industrial and infrastructure operators clearly own relevant systems, but public evidence says little about their buying preferences, mobile-device policies, current tools or procurement process. Direct operator sales should therefore follow evidence from real evaluations rather than persona-driven assumptions.

## What the product should be, based on the diligence

The most defensible product role is a **controlled OT evidence instrument** for authorized professionals. It should help collect, preserve and reconcile evidence while making uncertainty and authorization visible.

That implies several product principles:

- passive evidence before active identity where practical;
- exact-scope, bounded active operations rather than broad discovery;
- strong provenance and explicit separation between observations and accepted inventory;
- offline or minimized-data operation suitable for sensitive environments;
- export into an auditor's or operator's existing workflow rather than assuming Atlas replaces it;
- clear disclosure when traffic visibility is incomplete.

A phone is an implementation choice, not yet a proven customer preference. Likewise, a USB-Ethernet adapter alone does not expose arbitrary third-party traffic on a switched network; meaningful passive observation still depends on correct placement such as SPAN/TAP or an equivalent approved capture path.

## Evidence-driven technical priorities

The old diligence supports this order of investigation:

1. **Modbus** passive identification and a tightly bounded device-identification profile.
2. **EtherNet/IP/CIP** passive evidence and ListIdentity-style lab validation.
3. **PROFINET** passive/DCP evidence and **PROFIBUS** through imported or gateway evidence.
4. **Siemens S7** passive family identification without control interaction.
5. **OPC UA** and common SCADA discovery in controlled lab conditions.
6. **SNMP/LLDP** and normal network-infrastructure evidence.
7. **IEC 104/IEC 61850** passive support for utility and renewable-energy use cases.

Bluetooth, BACnet, ONVIF and other packs should not receive equal priority merely because they are technically possible. The diligence found insufficient Morocco-specific evidence for some of them. They should advance when customer, procurement or site evidence justifies them.

## Competitive reality

The diligence did not find a clean greenfield category.

- [Claroty Edge](https://claroty.com/platform/edge) publicly positions a one-time agentless CPS visibility executable and therefore weakens any claim that portable discovery itself is unique.
- [runZero Community Edition](https://www.runzero.com/platform/community-edition/) provides a free discovery option for environments up to 100 assets, which means basic asset discovery competes with zero-price software.
- [Tenable Nessus Professional](https://www.tenable.com/buy) provides a visible professional-scanner price anchor; the reviewed 2026 page listed USD 4,790/year.
- Wireshark, Nmap and other open tools make raw protocol inspection or network discovery inexpensive for expert users, although they do not provide the same constrained audit workflow.

**Analysis:** “mobile,” “portable,” “cheap” or “discovers assets” are not sufficient differentiation. The differentiation worth testing is the combination of bounded OT-safe execution, offline field use, evidence provenance, explicit analyst review, reconciliation and audit-ready handoff. That advantage must be demonstrated on the same corpus and workflow as alternatives; it should not be asserted from feature descriptions alone.

## Economic reality

No accepted Atlas product price or paid recurring usage has been recorded. There is also no controlled benchmark proving that Atlas saves a specific amount of time or money.

Public tenders show that Moroccan operators spend on adjacent automation, telemanagement, electrical and network work. That establishes procurement activity, not the budget for this product. Public competitor prices provide anchors, not willingness to pay in Morocco.

The economically important measurements are therefore practical:

- preparation and field time per assessment;
- hardware, travel and support cost;
- number of assets requiring manual reconciliation;
- report-production time;
- repeat visits or rework;
- number of assessments per practitioner per year;
- current tools and licences;
- accepted offer, procurement route and payment terms.

Until those are observed in real engagements, pricing and market sizing remain experiments rather than facts.

## Main risks and unresolved questions

The former diligence identified a small set of issues that can invalidate the business even if the prototype works technically:

| Question | Why it matters |
|---|---|
| Can the promised traffic actually be observed at target sites? | A phone/NIC without the right network placement may see little useful third-party traffic. |
| Are active identity operations safe across real device/firmware combinations? | OT devices vary; bounded code still requires device-specific validation. |
| Is identification accurate enough to support decisions? | A misleading identity result is worse than an explicit unknown. |
| Will a qualified auditor accept the evidence schema and method? | Technical output has limited value if it does not fit the professional workflow. |
| Does Atlas duplicate tools the practitioner already owns? | Existing OT and general discovery products may already solve enough of the problem. |
| Will anyone pay enough to cover field hardware, support and catalog maintenance? | Price and support cost are both still unknown. |
| Are phones permitted in the target environment? | Site policy, MDM and sensitive-data rules may block the deployment model. |
| Can capture and case data be handled lawfully and minimally? | OT captures may contain sensitive or personal data and require controlled handling. |
| Can dependencies be licensed and maintained sustainably? | Useful protocol libraries carry different licence and supply-chain obligations. |

These should remain explicit diligence gates rather than being hidden in a roadmap.

## What would materially increase confidence

The next stage becomes commercially meaningful when evidence exists for all of the following:

1. a DGSSI-qualified industrial-audit provider reviews and accepts the evidence format for a controlled evaluation;
2. at least two supported phone/NIC/TAP combinations pass repeatable capture tests;
3. the priority protocols pass a labeled lab corpus with accuracy and safety results;
4. Atlas is compared with existing practitioner tools on the same workflow, not only on feature lists;
5. at least one paid or contractually binding pilot/evaluation exists;
6. the project has a reviewed dependency, licensing and data-handling model;
7. field measurements show whether Atlas reduces equipment burden, reconciliation or reporting effort enough to matter.

Until those conditions are met, the correct business-development posture is disciplined: pursue qualified practitioners and evidence-rich operators, run controlled evaluations, record procurement outcomes, and avoid claims of market size, ROI, uniqueness or customer preference that have not been observed.

Return to the [business-development index](README.md).
