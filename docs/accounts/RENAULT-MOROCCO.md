# Renault Group Morocco account map

_Research baseline: 30 August 2026. Public professional titles and plant responsibilities require revalidation before use._

## Operating footprint and legal scope

Renault's Moroccan industrial platform consists principally of the Tangier plant and SOMACA in Casablanca, alongside Renault Commerce Maroc, the IFMIA training institute and a dense supplier/logistics ecosystem. Renault reported 394,465 vehicles produced in Morocco in 2025, nearly 10,000 employees, 82% of production exported to 63 destinations and 87 tier-1 suppliers supporting the platform.

Tangier and SOMACA should be treated as separate sites with different production history, equipment generations and local management. A country-level agreement is not enough to connect to either plant.

## OT-relevant organization map

| Publicly visible node | Person | Source quality | Relevance |
|---|---|---|---|
| Managing Director / CEO Renault Group Morocco | Mohamed Bachiri | Renault corporate article and public LinkedIn content | Country sponsorship, government and supplier ecosystem |
| Regional CIO | Nadia Tazi | Public LinkedIn profile and CIOmove interview | Regional IT, connectivity, cybersecurity and transformation |
| IT shared-services management | Hassan Elmoumen | Public LinkedIn profile | Operational IT delivery; exact current scope must be confirmed |
| Tangier engineering-process management | Sidi Omar El Kettani | Public LinkedIn profile | Plant engineering and process-change context |
| Tangier maintenance team leadership | Aziz Nasset | Public LinkedIn profile | Production-equipment maintenance workflow |
| Morocco facilities/services procurement | Nihad Lahit | Public LinkedIn profile | Supplier/procurement route for facilities and services |
| Product/process assembly engineering | Malak Dayf | Public LinkedIn profile | Industrialization and cell/process ownership |
| Automation/robotics execution | Mostafa Yaakoub | Public LinkedIn profile | Field user and technology-validation audience |
| Corporate technical leadership | Ali Benjeddi | Public LinkedIn profile | Technical standards and industrial project context |

### Reconstructed working group

Public material supports the existence of country management, regional IT, plant engineering, maintenance, purchasing and automation roles. It does not show their full reporting lines. A plausible pilot path is:

1. plant engineering or maintenance defines one cell and maintenance window;
2. regional/plant IT and cyber-defense owners approve interfaces and data handling;
3. automation personnel or an approved integrator execute the collection;
4. purchasing establishes the supplier vehicle;
5. country management sponsors expansion after operational results.

This is a buying-group hypothesis, not a declared Renault organigram.

## What Renault and others say publicly

The most informative source is a CIOmove interview with Nadia Tazi. It describes Tangier's evolution from having no independent internet connection to relying on connectivity, and says the plant has a comprehensive cyber-defense program that is regularly audited and meets strict European standards. That changes the sales argument: Renault does not need a basic scanner. Any portable tool must complement established controls, operate under group policy and produce outputs useful to an existing audit program.

Renault's 2026 corporate account of Morocco emphasizes increasing digitalization, supply-chain resilience, low-carbon manufacturing and the importance of its 87 tier-1 suppliers. It also says IFMIA has delivered 3.2 million training hours in automation, robotics and electrification, with 30% of activity serving the wider supplier ecosystem.

Public engineering and employment material names PLC, HMI/SCADA, Modbus, PROFIBUS and EtherNet/IP. Individual public profiles at Tangier mention Siemens, ABB, KUKA and FANUC. These signals justify protocol and vendor research, but they do not prove that every platform is currently deployed across both plants.

## LinkedIn and employee surface

- [Renault Group company page](https://www.linkedin.com/company/renaultgroup/) is active and publishes Morocco plant content.
- Mohamed Bachiri publishes directly about Morocco's industrial platform, providing a useful view of country priorities.
- Public Tangier profiles cover maintenance, robotics, process engineering, facilities and purchasing. They are useful for mapping functions and vocabulary.
- Employee profiles show a deep in-house automation capability. Outreach should ask who owns inventory reconciliation, not offer to “discover their OT.”

No personal email address or guessed Renault email pattern belongs in the repository.

## Events, training and ecosystem routes

| Route | Public activity | Commercial use |
|---|---|---|
| IFMIA Renault Tangier | Training in automation, robotics and electrification; support to wider suppliers | Safest place to benchmark active identity profiles and training workflow |
| Automotive Industry Competitiveness Show | Moroccan OEMs, suppliers and institutions meet | Identify integrators, purchasing and industrial engineering stakeholders |
| Tanger Med automotive ecosystem | Renault exports and suppliers operate through the port/industrial zone | Channel mapping; not shared network authorization |
| Renault Tech World Tour / plant visits | Renault invites media and industry participants to Tangier | Shows the plant's strategic positioning, but not an open technical pilot program |

## Current activity relevant to Atlas OT Scout

- Morocco is Renault's second-largest industrial country by volume.
- Tangier operates as a high-throughput, export-dependent plant with direct rail/port integration.
- Supplier density creates equipment and handover variation beyond the OEM's own teams.
- Connectivity and audit requirements are explicit in public IT commentary.
- IFMIA provides a controlled environment and a channel to suppliers.

The strongest commercial interpretation is that the first value may be standardizing collection across maintenance contractors and suppliers, not replacing Renault's central cyber tooling.

## Opportunity designs

### IFMIA benchmark

Use a representative PLC/robot cell with a known inventory. Compare the current documentation method with passive capture and approved identity profiles. Renault/IFMIA owns the reference list and acceptance criteria.

### Supplier handover pack

An approved line builder records networked assets during acceptance testing and delivers a reviewer-controlled exception file to Renault. This targets documentation friction without asking Renault to install a permanent platform.

### Maintenance-window reconciliation

On one noncritical Tangier area, maintenance and IT jointly compare the observed inventory with the existing repository. Passive-only collection is the default.

## Product implications

- The application must support strict global policy, role separation and group-approved hardware lists.
- Wi-Fi and Bluetooth functions may need to be completely disabled per area.
- Automotive cell relationships—PLC, HMI, robot controller, drive, safety controller—should be represented without claiming control topology from weak signals.
- Import/export to existing maintenance and enterprise systems matters more than a proprietary dashboard.
- A supplier mode should allow collection by an integrator while Renault retains review and key custody.

## Unknowns to resolve

- Current plant directors and named plant IT/cyber owners for Tangier and SOMACA.
- Which global Renault asset-discovery and CMDB tools are already mandated.
- Whether a mobile device can enter production areas and which radios/cameras must be disabled.
- Exact current automation vendors and protocols by pilot cell.
- Whether purchasing can engage a small vendor directly or requires an approved integrator.
- Data export path to France/group systems and associated classification rules.

## Sources

- [Renault 2026 Tangier/Morocco industrial overview](https://www.renaultgroup.com/en/magazine/our-group-news/tangier-factory-the-industrial-and-logistics-hub-where-everything-comes-together/)
- [Tangier plant profile](https://www.renaultgroup.com/en/group/locations/tangier-plant/)
- [Mohamed Bachiri's Morocco perspective](https://www.linkedin.com/pulse/morocco-perspective-building-global-industrial-powerhouse-r4wte)
- [CIOmove interview with Nadia Tazi](https://www.ciomove.com/news/topics-theses/the-it-evolution-in-renaults-tangier-plant-a-grounded-approach/)
- [Nadia Tazi public profile](https://fr.linkedin.com/in/nadia-tazi-10731013)
- [Tangier engineering-process manager](https://ma.linkedin.com/in/sidi-omar-el-kettani-3299a84b)
- [Tangier maintenance team lead](https://ma.linkedin.com/in/nasset-aziz-7b94a9123)
- [Morocco facilities/services procurement](https://ma.linkedin.com/in/nihad-lahit-850a607b)
- [Assembly product/process engineering](https://ma.linkedin.com/in/malak-dayf-47719311b)
- [Tangier automation/robotics profile](https://ma.linkedin.com/in/mostafa-yaakoub-b1a50a195)
- [IT shared-services profile](https://ma.linkedin.com/in/elmoumenhassan)
- [Public Renault Morocco automation job](https://www.linkedin.com/jobs/view/4209227623/)
