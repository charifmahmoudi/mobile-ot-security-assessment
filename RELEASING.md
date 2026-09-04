# Release policy

Atlas OT Scout has no supported production release. Tags and GitHub Releases must identify tested maturity; they are not activity markers.

## Version progression

| Version | Meaning | Minimum evidence |
|---|---|---|
| `v0.1.0-alpha.N` | Integrated assessment workflow, not field-qualified | Green CI, reproducible build, documented capability and limitations |
| `v0.1.0-rc.N` | Candidate for one bounded customer pilot | Issues #6–#10 complete, supported kit identified, golden assessment and independent rehearsal evidence |
| `v0.1.0` | Qualified P0-WATER customer-pilot package | P0 definition-of-done satisfied, release record reviewed, no blocking security or OT-safety defect |

No `v1.0.0` tag may be used until a production support, compatibility, update, vulnerability-response and licensing policy exists.

## Release record

Every GitHub Release must include:

- product status and intended use;
- exact supported assessment and hardware scope;
- packet-producing behavior and safety boundaries;
- commit and immutable artifact identifiers;
- applicable CI, emulator, packet-trace and physical-kit evidence;
- dependency/SBOM and build provenance status;
- known limitations, unresolved risks and blocking defects;
- security support status and reporting route.

Release artifacts must be derived from a reviewed commit. The release record must link to the authoritative [current implementation](IMPLEMENTATION.md), [roadmap](ROADMAP.md), [compatibility matrix](docs/appliance/COMPATIBILITY-MATRIX.md), [network execution contract](docs/architecture/NETWORK-EXECUTION.md) and [test-and-acceptance plan](docs/poc/TEST-AND-ACCEPTANCE.md).

## Current decision

Do not create a tag or GitHub Release merely to make the repository appear mature. The first `v0.1.0-alpha.1` tag is admitted only when the integrated workflow can be demonstrated from a clean checkout and the corresponding limitations are recorded.
