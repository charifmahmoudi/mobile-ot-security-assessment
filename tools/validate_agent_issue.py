#!/usr/bin/env python3
import json
import os
import subprocess
import sys

REQUIRED_SECTIONS = [
    '## Outcome',
    '## Scope',
    '## Dependencies',
    '## Failure behavior',
    '## Non-goals',
    '## Acceptance criteria',
    '## Tests and evidence',
    '## Human validation still required',
    '## Agent completion report',
]


def missing_sections(body, required_sections=REQUIRED_SECTIONS):
    return [section for section in required_sections if section not in body]


def load_issue_body(issue, repo, body_override=None):
    if body_override is not None:
        return body_override
    if not issue:
        raise SystemExit('issue number is required when ISSUE_BODY is not provided')
    data = json.loads(subprocess.check_output(['gh', 'issue', 'view', issue, '--repo', repo, '--json', 'title,body'], text=True))
    return data.get('body') or ''


def main():
    issue = sys.argv[1] if len(sys.argv) > 1 else os.environ.get('ISSUE_NUMBER')
    repo = os.environ.get('GITHUB_REPOSITORY', 'charifmahmoudi/mobile-ot-security-assessment')
    body_override = os.environ.get('ISSUE_BODY') or None
    body = load_issue_body(issue, repo, body_override)
    missing = missing_sections(body)
    if missing:
        raise SystemExit('issue is not ready for agent assignment; missing: ' + ', '.join(missing))
    print(f'issue #{issue or "body"}: PASS')


if __name__ == '__main__':
    main()
