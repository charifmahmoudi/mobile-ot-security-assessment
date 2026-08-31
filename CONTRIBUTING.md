# Contributing

Atlas OT Scout is an executable research prototype with safety-critical OT boundaries. Contributions must preserve evidence integrity, authorization controls, and conservative claims.

## Workflow

1. Open an issue for a material defect, claim, design decision, or feature.
2. Use a focused branch and a descriptive commit.
3. Add or update tests for behavioral changes.
4. Update the canonical status, requirements, architecture, or roadmap document affected by the change.
5. Run the relevant verification commands.
6. Submit a pull request using the repository template.

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

- Use [docs/README.md](docs/README.md) to identify the canonical document before adding a new file.
- Mark external claims as verified, corroborated, inferred, unknown, or contradicted.
- Cite primary sources near the claim and update the source register when appropriate.
- Keep current capability in `IMPLEMENTATION.md` and planned capability in `ROADMAP.md`.
- Prefer stable workflow links over hard-coded CI run numbers in overview pages.
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
