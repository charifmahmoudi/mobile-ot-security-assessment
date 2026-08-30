# ONEE account map

_Research baseline: 30 August 2026. ONEE is a public critical-infrastructure operator; public role data is incomplete and every technical approach requires formal authorization._

## Operating structure

ONEE combines two major branches that should be treated as separate markets:

- **Electricity Branch (ONEE-BE):** generation, transmission, distribution, dispatch, metering and interconnection.
- **Water Branch (ONEE-BO):** drinking-water production, treatment, pumping, transfer, distribution, sanitation and telemanagement.

Tarik Hamane has led ONEE since 2024. Board and financing material describes large grid, renewable-integration, water-production and modernization programs. Regional directorates and individual plants/stations create the actual field-authorization layer.

## Reconstructed OT-relevant organization

| Public node | Person | Source | Relevance |
|---|---|---|---|
| Director General / Group CEO | Tarik Hamane | Public profile and official/public events | Strategic sponsorship across both branches |
| Electricity production direction | Houssine Naji | Public LinkedIn title “Directeur Production chez ONEE-BE” | Generation operations; exact current mandate to confirm |
| Electricity technical division | Omar Benemmane | Public LinkedIn title | Technical/substation context; profile mentions IEC 61850 |
| Electricity smart metering architecture | Mohamed Bahid | Public LinkedIn title, AMI/Smart Metering Architect | EnergyIP/metering architecture and cyber requirements |
| Electricity SCADA administration | Ismail N. | Public LinkedIn title | Operational SCADA administration; not necessarily decision authority |
| Network and security engineering | Mohamed-Yacine Chahdaoui | Public LinkedIn profile | Enterprise/network security review |
| Water automation, instrumentation and telemanagement team | Abderrahim Samir | Public LinkedIn title | Direct Water Branch field-use audience |
| Water SCADA/automation training | Sami Laknizi | Public LinkedIn title | Training and controlled validation route |

These profiles establish functions, not a formal hierarchy. ONEE's authoritative organization and current appointments must come from the Office.

### Probable authorization chain

For a Water Branch pumping or treatment station: regional/site operations → automation/instrumentation/telemanagement → branch IT/security → central governance/procurement. For an Electricity Branch substation or plant: production/transmission asset owner → protection/control/SCADA → network/cybersecurity → branch and central approval.

No active protocol query should be proposed until the protection/control owner approves it. A portable tool must never conflate smart-meter systems, enterprise IT and protection-control networks.

## LinkedIn and public presence

- ONEE has public corporate and branch references, while LinkedIn employees commonly name the precise branch in their profiles.
- Search results expose production, technical-division, SCADA, smart-metering, network-security and water-telemanagement roles.
- Public intern/project profiles repeatedly mention pumping-station telemanagement, treatment SCADA and integration of communicating switches into ONEE supervision. These are secondary capability signals; they should not be treated as official architecture disclosures.

The public employee surface is sufficient to identify role families, but not to build a complete named management chart. Outreach should go through official procurement, company contact and professional introductions, not guessed addresses.

## Technology, suppliers and procurement

### Confirmed projects

- Atos and Siemens announced an ONEE smart-grid project using Siemens EnergyIP to process data from more than 100,000 smart meters. Atos was responsible for IT infrastructure and security consistency.
- Public ONEE procurement covers maintenance of automata at the Mohammedia fuel-loading station.
- An EBRD procurement record covers Water Branch SCADA acquisition.
- Public professional profiles refer to ONEE SCADA integration, water telemanagement, PLC supervision and IEC 61850 technical work.

### What these signals mean

ONEE's estate is not one protocol list. Smart metering, generation controls, substation automation and water telemanagement have different technologies and risk. The first product profile should be chosen from an actual tender specification or lab configuration. Likely standards such as IEC 60870-5-104, IEC 61850, Modbus and DLMS/COSEM should remain research priorities, not claims about every ONEE site.

## Events and external activity

| Event/relationship | Public activity | Relevance |
|---|---|---|
| Powering Africa Summit 2026 | Tarik Hamane spoke about renewables, grid resilience, investment and water–energy nexus | Executive priority and partner ecosystem |
| Morocco Energy & Sustainability Week | Advertises roundtables with ONEE, Masen and ANRE | Route to project developers, EPCs and policy stakeholders |
| EBRD digitalization engagement | EBRD publicly described work with ONEE | Modernization partner and procurement context |
| GITEX Africa / smart-grid ecosystem | Relevant suppliers attend; direct ONEE attendance must be checked per edition | Supplier mapping, not assumed account participation |

Technical users are more likely reached through approved engineering contractors, public tenders and training centers than through a broad technology conference.

## What outside sources say

Siemens and Atos describe the smart-meter project as Africa's largest joint smart-grid project at announcement and emphasize security, scalability and changing utility operating models. EBRD frames digitalization as an enabler of energy transition and security. Public ONEE professionals emphasize long-lived telemanagement and SCADA responsibilities. This points to an organization already modernizing at scale while retaining legacy and regional operational complexity.

## Opportunity designs

### Water telemanagement inventory

Select one noncritical pumping/treatment training system or planned maintenance scope. Import the existing PLC/RTU/communications list, collect passive observations and issue a reviewed discrepancy report.

### Contractor delivery pack

An approved automation contractor uses the mobile workflow during telemanagement maintenance and delivers evidence in the tender-required format. This is commercially more realistic than direct enterprise software adoption.

### Training-center protocol validation

ONEE trainers validate identity-only profiles against representative IEC/Modbus/DLMS devices without production risk.

## Product implications

- Separate electricity, water, site and regional directorate tenants/scopes.
- Full offline operation and controlled export are core requirements for remote sites.
- Query profiles must distinguish monitoring/metrology from protection/control.
- Capture timestamps, interface identity, authorization and operator must be tamper-evident.
- Import/export should support public-procurement deliverables and common engineering formats.
- Arabic/French field terminology and French reporting are important for Morocco rollout.

## Unknowns

- Official current branch organization and named cybersecurity/OT-security owners.
- Which ONEE systems are designated vital information infrastructure and what additional rules apply.
- Current approved audit firms, integrators and hardware.
- Exact vendor/protocol inventory at a candidate station.
- Procurement threshold and framework route for a prototype.
- Whether packet evidence can leave a regional site and who holds encryption keys.

## Sources

- [ONEE board reporting — Ministry of Finance](https://www.finances.gov.ma/en/Pages/detail-actualite.aspx?fiche=7799)
- [Atos–Siemens ONEE smart-grid project](https://press.siemens.com/global/en/pressrelease/atos-and-siemens-support-morocco-smart-management-its-power-grid)
- [ONEE automata-maintenance notice](https://www.one.org.ma/FR/pages/aofiche.asp?id=18772)
- [Historical Water Branch SCADA procurement](https://www.developmentaid.org/tenders/view/253523/onee-water-supply-supervisory-control-and-data-acquisition-scada)
- [Tarik Hamane public profile](https://ma.linkedin.com/in/tarik-hamane-71bb37125)
- [Electricity Production Director](https://ma.linkedin.com/in/houssine-naji-b40766152)
- [Electricity technical-division profile](https://ma.linkedin.com/in/omar-benemmane-8635bb45)
- [AMI/Smart Metering Architect](https://ma.linkedin.com/in/mohamed-bahid-00a78b31)
- [ONEE-BE SCADA administrator](https://ma.linkedin.com/in/inassereddine)
- [ONEE network/security engineer](https://ma.linkedin.com/in/chahdaoui)
- [Water automation/instrumentation/telemanagement team lead](https://ma.linkedin.com/in/abderrahim-samir-a34a87140)
- [Water SCADA/automation trainer](https://ma.linkedin.com/in/sami-laknizi-523b45b4)
- [Powering Africa Summit participation](https://www.linkedin.com/posts/mgl-network_mrtarik-hamane-ceo-of-moroccos-national-activity-7440447394611105792-naU-)
- [Morocco Energy & Sustainability Week](https://www.netzerocircle.org/event/morocco-energy-week)
