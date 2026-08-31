# Governance

The repository owner is the final decision maker during the prototype phase.

## Decision process

- Material architecture and safety decisions require an architecture decision record.
- The normal path for code and safety-policy changes is a focused pull request with test evidence.
- Packet-producing behavior, authorization policy, parser isolation, cryptography, evidence integrity, and release controls require a second technical review and an explicit safety review.
- The owner may commit low-risk documentation, navigation, and administrative maintenance directly to `main`.
- Current capability is authoritative only in [IMPLEMENTATION.md](IMPLEMENTATION.md); planned capability belongs in [ROADMAP.md](ROADMAP.md).

## Maintainer responsibilities

Maintainers are responsible for operational safety, evidence integrity, privacy, dependency and license review, reproducible verification, vulnerability handling, release signing, and respectful review.

No release may claim physical compatibility, complete visibility, compliance, exploitability, or customer validation without the corresponding acceptance evidence.

## Licensing and contributions

A commercial/open-source licensing decision is intentionally deferred. Contributions are not accepted under an implied license; establish a `LICENSE` file and contribution terms before accepting implementation contributions.
