# Golden Customer Assessment

This directory is the deterministic integration contract for pilot issue #6 and later P0-WATER work. It represents one bounded raw-water intake pumping-control segment and contains no customer data or dependency on an external service.

- `case.json` fixes the assessment question, authority, exact `/32` Modbus target, exclusions, methods, stop conditions and data policy.
- `expected-inventory.csv` is customer-declared baseline data. Tests must preserve it as an expected/source layer rather than treat it as observed identity.
- `passive-evidence.json` is deterministic imported evidence with explicit visibility limits.
- `expected-outcomes.json` fixes the reconciliation and reporting expectations later pilot issues must extend rather than silently replace.

The fixture intentionally includes an excluded engineering workstation, an unobserved VFD and an unexpected endpoint so a passing implementation cannot equate a port list with a reconciled inventory.
