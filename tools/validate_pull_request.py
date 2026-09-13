#!/usr/bin/env python3
import json
import os
import subprocess
import sys

REQUIRED_SECTIONS = [
    '## Contract',
    '## Dependencies',
    '## Failure behavior',
    '## Tests and evidence',
    '## Evidence artifact',
    '## Documentation impact',
    '## Human validation still required',
    '## Acceptance-criterion mapping',
]


def missing_sections(body, required_sections=REQUIRED_SECTIONS):
    return [section for section in required_sections if section not in body]


def load_pull_request_body(pull_request, repo, body_override=None):
    if body_override is not None:
        return body_override
    if not pull_request:
        raise SystemExit('pull request number is required when PR_BODY is not provided')
    data = json.loads(subprocess.check_output(['gh', 'pr', 'view', pull_request, '--repo', repo, '--json', 'title,body'], text=True))
    return data.get('body') or ''


def main():
    pull_request = sys.argv[1] if len(sys.argv) > 1 else os.environ.get('PR_NUMBER')
    repo = os.environ.get('GITHUB_REPOSITORY', 'charifmahmoudi/mobile-ot-security-assessment')
    body = load_pull_request_body(pull_request, repo, os.environ.get('PR_BODY'))
    missing = missing_sections(body)
    if missing:
        raise SystemExit('pull request contract is incomplete; missing: ' + ', '.join(missing))
    print(f'pull request #{pull_request or "body"}: PASS')


if __name__ == '__main__':
    main()
