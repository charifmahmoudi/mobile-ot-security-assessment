#!/usr/bin/env python3
from pathlib import Path

required = [
    Path('.github/copilot-instructions.md'),
    Path('.github/ISSUE_TEMPLATE/agent-implementation.md'),
    Path('.github/ISSUE_TEMPLATE/human-validation.md'),
    Path('.github/ISSUE_TEMPLATE/capability-pack.md'),
    Path('docs/PLANNING-AUTOMATION.md'),
]
missing = [str(path) for path in required if not path.exists()]
if missing:
    raise SystemExit('missing planning files: ' + ', '.join(missing))
print(f'planning configuration: PASS ({len(required)} files)')
