# ADR 0002: Passive first with a deterministic action gate

- Status: proposed
- Date: 2026-08-30

## Decision

All packet-producing actions pass a non-AI policy gate. The prototype implements passive and selected identity-only A1 profiles.

## Consequences

Coverage grows more slowly but is auditable. An LLM may summarize or propose; it may not directly transmit traffic. Exploitation and control writes are out of scope.
