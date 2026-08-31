# Security policy

## Current status

The repository produces executable debug builds for controlled research and CI, but no production-supported release exists. Do not deploy the prototype on operational systems or use it to generate traffic without explicit written authorization.

## Reporting a vulnerability

Use GitHub private vulnerability reporting when available. Otherwise contact the repository owner through their GitHub profile without publishing exploit details.

Include:

- affected commit or artifact;
- security and operational impact;
- reproduction in a controlled environment;
- relevant logs or packet traces with secrets and customer data removed;
- a proposed mitigation when known.

Do not test against public or customer systems, publish credentials, or include real customer data.

## Supported versions

| Version | Support |
|---|---|
| `main` research prototype | Best-effort security review; not production supported |
| Tagged production releases | None |

A dedicated security mailbox, supported-version policy, release signing, and disclosure timeline must be established before the first production release.

## Security and OT-safety issues

Report unintended writes, unbounded or replayed traffic, scope escape, authorization bypass, signature or nonce failures, secret leakage, evidence tampering, parser memory-safety failures, privilege-boundary violations, or inaccurate safety-state reporting as security vulnerabilities.

The active boundary is intentionally limited to one approved identity operation. Requests to add control functions, exploits, credential attacks, or broad discovery are outside the P0 security model.
