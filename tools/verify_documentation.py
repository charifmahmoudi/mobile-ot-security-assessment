#!/usr/bin/env python3
"""Fail CI when repository documentation has broken structure or local references."""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
FILES = sorted(ROOT.glob("*.md")) + sorted((ROOT / "docs").rglob("*.md"))

MARKDOWN_LINK = re.compile(r"!?(?:\[[^\]]*\])\(([^)]+)\)")
INLINE_REPO_PATH = re.compile(
    r"`("
    r"(?:docs|tools|schemas|\.github)/[A-Za-z0-9_./-]+\.(?:md|csv|json|ya?ml|py|sh|kt|java|c|xml|pptx|pdf|mp4|png)"
    r"|(?:README|IMPLEMENTATION|ROADMAP|SECURITY|CONTRIBUTING|GOVERNANCE)\.md"
    r")`"
)
FENCED_BLOCK = re.compile(r"```.*?```", re.DOTALL)

errors: list[str] = []


def clean_target(raw_target: str) -> str:
    """Return the path portion of a Markdown target."""
    target = raw_target.strip().split(maxsplit=1)[0].strip("<>")
    return target.split("#", 1)[0]


def check_local_reference(source: Path, target: str, *, repo_root: bool = False) -> None:
    if not target or target.startswith(("http://", "https://", "mailto:", "#")):
        return
    if any(token in target for token in ("*", "{", "}")):
        return

    candidate = (ROOT / target) if repo_root else (source.parent / target)
    if not candidate.resolve().exists():
        relative = source.relative_to(ROOT)
        errors.append(f"{relative}: missing local reference {target}")


for path in FILES:
    text = path.read_text(encoding="utf-8")
    relative = path.relative_to(ROOT)

    if text.count("```") % 2:
        errors.append(f"{relative}: unbalanced fenced code block")

    visible_text = FENCED_BLOCK.sub("", text)

    for raw_target in MARKDOWN_LINK.findall(visible_text):
        check_local_reference(path, clean_target(raw_target))

    for raw_target in INLINE_REPO_PATH.findall(visible_text):
        check_local_reference(path, clean_target(raw_target.rstrip(".,;:")), repo_root=True)

if errors:
    print("Documentation validation failed:", file=sys.stderr)
    for error in sorted(set(errors)):
        print(f"- {error}", file=sys.stderr)
    raise SystemExit(1)

print(f"documentation: PASS ({len(FILES)} Markdown files)")
