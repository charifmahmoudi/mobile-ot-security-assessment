# Contributing

Atlas OT Scout is an executable research prototype with safety-critical OT boundaries. Contributions must preserve evidence integrity, authorization controls, conservative claims, and the documentation graph.

## Workflow

1. Open an issue for a material defect, claim, design decision, or feature.
2. Use a focused branch and a descriptive commit.
3. Add or update tests for behavioral changes.
4. Update the canonical status, requirements, architecture, ADR, or roadmap document affected by the change.
5. Add every new or renamed document to its nearest section `README.md`.
6. Run the relevant verification commands.
7. Submit a pull request using the repository template.

Repository-owner documentation maintenance may be committed directly, but packet-producing behavior, authorization policy, parser trust boundaries, cryptography, or release controls require independent technical and safety review.

## Required local checks

```bash
python3 tools/verify_documentation.py
python3 tools/verify_architecture.py
bash tools/test_capture_daemon.sh

gradle --no-daemon \
  :core-domain:test \
  :case-app:testDebugUnitTest \
  :network-broker:testDebugUnitTest \
  :capture-broker:testDebugUnitTest \
  lintDebug assembleDebug
```

## Documentation and research rules

- Start at [docs/README.md](docs/README.md) to identify the canonical document and section index.
- Every documentation directory containing Markdown must have a `README.md` that links its Markdown files and child documentation sections.
- ADR numbers must be unique, and the filename number must match the first heading.
- Mark external claims as verified, corroborated, inferred, unknown, or contradicted.
- Cite primary sources near the claim and update the source register when appropriate.
- Keep current capability in `IMPLEMENTATION.md` and planned capability in `ROADMAP.md`.
- Use stable workflow links in overview pages. Pin an individual run only in a provenance document.
- Never publish credentials, packet payloads, customer data, private contact details, guessed email addresses, or unverified vulnerabilities.

## Active OT behavior

Any packet-producing change must define:

- written authorization and exact scope;
- protocol operation and risk class;
- target, interface, rate, retry, timeout, concurrency, and byte/packet budgets;
- cancellation and emergency-stop behavior;
- golden-packet, replay, scope-escape, and cancellation tests.

Writes, control actions, exploitation, credential attacks, and broad scanning are outside P0.

## Licensing

No project license or contributor agreement has been selected. Do not submit implementation contributions under an assumed license; contribution terms must be established first.
