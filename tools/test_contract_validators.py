#!/usr/bin/env python3
import sys
import unittest
from pathlib import Path

TOOLS_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(TOOLS_DIR))

import validate_agent_issue
import validate_pull_request


REPO_ROOT = TOOLS_DIR.parent


class ContractValidatorTest(unittest.TestCase):
    def test_agent_issue_template_satisfies_required_sections(self):
        body = (REPO_ROOT / '.github/ISSUE_TEMPLATE/agent-implementation.md').read_text()
        self.assertEqual([], validate_agent_issue.missing_sections(body))

    def test_pull_request_template_satisfies_required_sections(self):
        body = (REPO_ROOT / '.github/PULL_REQUEST_TEMPLATE.md').read_text()
        self.assertEqual([], validate_pull_request.missing_sections(body))

    def test_issue_validator_reports_missing_failure_behavior(self):
        body = '\n'.join(section for section in validate_agent_issue.REQUIRED_SECTIONS if section != '## Failure behavior')
        self.assertEqual(['## Failure behavior'], validate_agent_issue.missing_sections(body))

    def test_pull_request_validator_reports_missing_evidence_artifact(self):
        body = '\n'.join(section for section in validate_pull_request.REQUIRED_SECTIONS if section != '## Evidence artifact')
        self.assertEqual(['## Evidence artifact'], validate_pull_request.missing_sections(body))


if __name__ == '__main__':
    unittest.main()
