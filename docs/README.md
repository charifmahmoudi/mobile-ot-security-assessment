# Documentation index

This page is the canonical map for Atlas OT Scout documentation. The root [README](../README.md) is the product landing page; this index routes readers to the authoritative document for each decision.

## Start here

| Goal | Read first | Then |
|---|---|---|
| Understand the product and its limits | [P0-WATER specification](poc/WATER-WASTEWATER-POC.md) | [Requirements baseline](REQUIREMENTS.md) |
| See what is executable today | [Implementation status](../IMPLEMENTATION.md) | [End-to-end acceptance](testing/E2E-ACCEPTANCE.md) |
| Understand the security architecture | [Technical architecture](wiki/Technical-Architecture.md) | [Threat model](architecture/SECURITY-AND-THREAT-MODEL.md) |
| Use or demonstrate the application | [User manual](user-guide/USER-MANUAL.md) | [Demo script](demo/VIDEO-SCRIPT.md) |
| Plan engineering work | [Roadmap](../ROADMAP.md) | [Implementation backlog](poc/IMPLEMENTATION-BACKLOG.md) |
| Review Morocco commercial research | [Water-sector accounts](accounts/README.md) | [Evidence-only diligence](diligence/README.md) |
| Contribute safely | [Contributing](../CONTRIBUTING.md) | [Governance](../GOVERNANCE.md) and [Security policy](../SECURITY.md) |

## Document authority

When documents overlap, use this order:

1. [P0-WATER specification](poc/WATER-WASTEWATER-POC.md) for the first product pack and its acceptance criteria.
2. [Requirements baseline](REQUIREMENTS.md) for stable normative requirements.
3. [Implementation status](../IMPLEMENTATION.md) for current executable behavior.
4. [Roadmap](../ROADMAP.md) for planned work; it must not be read as implemented capability.
5. Architecture decision records in [adr/](adr/) for accepted design decisions.
6. Research and diligence documents for evidence, hypotheses, and commercial context.

The `docs/wiki/` directory is an orientation layer. Detailed architecture, product, testing, and diligence documents remain canonical in their dedicated directories.

## Product and operator documentation

- [P0-WATER professional assessment specification](poc/WATER-WASTEWATER-POC.md)
- [Assessment method](poc/ASSESSMENT-METHOD.md)
- [Test and acceptance criteria](poc/TEST-AND-ACCEPTANCE.md)
- [Implementation backlog](poc/IMPLEMENTATION-BACKLOG.md)
- [Capture accessory design](poc/CAPTURE-ACCESSORY.md)
- [User manual](user-guide/USER-MANUAL.md)
- [Product UX and open-source implementation](product/OPEN-SOURCE-AND-UX-IMPLEMENTATION.md)
- [User stories](product/USER-STORIES.md)
- [Product demonstration script](product/DEMO-SCRIPT.md)

## Architecture and security

- [System and deployment](architecture/SYSTEM-AND-DEPLOYMENT.md)
- [Component contracts](architecture/COMPONENT-CONTRACTS.md)
- [Network execution](architecture/NETWORK-EXECUTION.md)
- [Evidence data model](architecture/EVIDENCE-DATA-MODEL.md)
- [Security and threat model](architecture/SECURITY-AND-THREAT-MODEL.md)
- [Dedicated Android appliance](architecture/DEDICATED-ANDROID-APPLIANCE.md)
- [Rooted Android proof of concept](appliance/ROOTED-ANDROID-POC.md)
- [Hardware compatibility matrix](appliance/COMPATIBILITY-MATRIX.md)
- [Architecture decision records](adr/)

## Verification and evidence

- [End-to-end acceptance architecture](testing/E2E-ACCEPTANCE.md)
- [Research testbeds](testing/RESEARCH-TESTBEDS.md)
- [Emulator screenshot provenance](testing/EMULATOR-SCREENSHOTS.md)
- [Assessment evidence schema](../schemas/assessment-evidence.schema.json)
- [Signed query-profile schema](../schemas/query-profile.schema.json)
- [Research sources](research/Sources.md)

## Morocco water-sector business research

- [Target-account index](accounts/README.md)
- [Portfolio synthesis](accounts/PORTFOLIO-SYNTHESIS.md)
- [Contact plans](accounts/CONTACT-PLANS.md)
- [Engagement playbook](accounts/ENGAGEMENT-PLAYBOOK.md)
- [Evidence-only diligence index](diligence/README.md)
- [Morocco water-sector dossiers](diligence/MOROCCO-SECTOR-DOSSIERS.md)
- [Customer organization and outreach](diligence/CUSTOMER-ORGANIZATION-AND-OUTREACH.md)
- [Source register](research/Sources.md)

## Maintenance rules

- Keep current capability statements in `IMPLEMENTATION.md`; link to them instead of copying them into multiple documents.
- Keep planned capability in `ROADMAP.md` and the backlog; never describe a roadmap item as executable.
- Prefer stable workflow links and badges over hard-coded CI run numbers in overview pages.
- Use relative links for repository files and place citations next to external factual claims.
- Label commercial evidence as verified, corroborated, inferred, unknown, or contradicted.
- Run `python3 tools/verify_documentation.py` before committing documentation changes.
