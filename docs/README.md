# Documentation index

This page is the canonical map for Atlas OT Scout documentation. The root [README](../README.md) is the product landing page; this index routes each reader to the authoritative document and complete section index.

## Choose by audience

| Audience or decision | Start here | Supporting section |
|---|---|---|
| Customer or prospect | [Prospect pitch](pitch/README.md) | [Guided demo](demo/README.md) |
| Business developer | [Morocco business development](business-development/README.md) | [Evidence-only diligence](diligence/README.md) |
| Assessor or presenter | [User guide](user-guide/README.md) | [Assessment method](poc/ASSESSMENT-METHOD.md) |
| Product owner | [P0-WATER specification](poc/WATER-WASTEWATER-POC.md) | [Product design](product/README.md) |
| Engineer or security reviewer | [Executable baseline](../IMPLEMENTATION.md) | [Architecture](architecture/README.md) and [ADRs](adr/README.md) |
| Test or release reviewer | [Testing and evidence](testing/README.md) | [Roadmap](../ROADMAP.md) |
| Appliance integrator | [Appliance](appliance/README.md) | [Dedicated Android appliance](architecture/DEDICATED-ANDROID-APPLIANCE.md) |
| New contributor | [Contributing](../CONTRIBUTING.md) | [Governance](../GOVERNANCE.md) and [Security](../SECURITY.md) |

## Document authority

When documents overlap, use this order:

1. [P0-WATER specification](poc/WATER-WASTEWATER-POC.md) for the first product pack and its acceptance criteria.
2. [Requirements baseline](REQUIREMENTS.md) for stable normative requirements.
3. [Implementation status](../IMPLEMENTATION.md) for current executable behavior.
4. [Roadmap](../ROADMAP.md) for planned work; it must not be read as implemented capability.
5. Accepted decisions in the [ADR index](adr/README.md).
6. Section-specific research and diligence documents for evidence, hypotheses and commercial context.

## Section map

Every maintained section has one landing page. A documentation change is incomplete until its nearest landing page links the new, renamed or moved document.

| Section | Scope |
|---|---|
| [Architecture decisions](adr/README.md) | Numbered decisions, status and consequences |
| [Appliance](appliance/README.md) | Rooted proof-of-concept boundary and physical compatibility evidence |
| [Architecture](architecture/README.md) | Deployment, component contracts, evidence model, network execution, protocol catalog and threat model |
| [Business development](business-development/README.md) | Morocco target companies, public employee profiles, outreach method, ecosystem and source data |
| [Demo](demo/README.md) | Guided customer-story video, script and provenance |
| [Diligence](diligence/README.md) | Evidence controls, market assumptions, competition, risks and validation |
| [Pitch](pitch/README.md) | Prospect deck, PDF, video linkage and claim boundary |
| [P0 product pack](poc/README.md) | Water/wastewater scope, method, capture accessory, backlog and acceptance |
| [Product design](product/README.md) | User stories, UX implementation and presenter script |
| [Research sources](research/README.md) | Shared technical and normative sources |
| [Testing](testing/README.md) | End-to-end architecture, testbeds, screenshots and evidence retention |
| [User guide](user-guide/README.md) | Operator workflow and screenshot-backed manual |

## Top-level documents

- [Requirements baseline](REQUIREMENTS.md)
- [Executable baseline](../IMPLEMENTATION.md)
- [Roadmap](../ROADMAP.md)
- [Contributing](../CONTRIBUTING.md)
- [Governance](../GOVERNANCE.md)
- [Security policy](../SECURITY.md)

## Maintenance contract

- Keep current capability statements in `IMPLEMENTATION.md`; link to them instead of copying mutable status into multiple overview documents.
- Keep planned capability in `ROADMAP.md` and the P0 backlog; never describe a roadmap item as executable.
- Keep all company, employee and outreach material in `docs/business-development/`.
- Add every document to its nearest section `README.md`, and add every new section to this page.
- Use stable workflow links in overview documents. Pin an individual run only in a provenance document that explains why that snapshot matters.
- Use relative links for repository files and place citations next to external factual claims.
- Label commercial evidence as verified, corroborated, inferred, unknown or contradicted.
- Run `python3 tools/verify_documentation.py` before committing documentation changes.
