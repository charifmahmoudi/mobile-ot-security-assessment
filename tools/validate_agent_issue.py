#!/usr/bin/env python3
import json
import os
import subprocess
import sys

issue = sys.argv[1] if len(sys.argv) > 1 else os.environ.get('ISSUE_NUMBER')
if not issue:
    raise SystemExit('issue number is required')
repo = os.environ.get('GITHUB_REPOSITORY', 'charifmahmoudi/mobile-ot-security-assessment')
data = json.loads(subprocess.check_output(['gh', 'issue', 'view', issue, '--repo', repo, '--json', 'title,body'], text=True))
body = data.get('body') or ''
required = ['## Outcome', '## Acceptance criteria', '## Tests and evidence']
missing = [section for section in required if section not in body]
if missing:
    raise SystemExit('issue is not ready for agent assignment; missing: ' + ', '.join(missing))
print(f'issue #{issue}: PASS')
