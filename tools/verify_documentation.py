#!/usr/bin/env python3
"""Fail CI when repository Markdown has broken structure or local references."""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
FILES = [ROOT / "README.md", ROOT / "IMPLEMENTATION.md", *sorted((ROOT / "docs").rglob("*.md"))]
LINK = re.compile(r"!?(?:\[[^\]]*\])\(([^)]+)\)")
errors: list[str] = []

for path in FILES:
    text = path.read_text(encoding="utf-8")
    relative = path.relative_to(ROOT)

    if text.count("```") % 2:
        errors.append(f"{relative}: unbalanced fenced code block")

    for raw_target in LINK.findall(text):
        target = raw_target.strip().split(maxsplit=1)[0].strip("<>")
        if not target or target.startswith(("#", "http://", "https://", "mailto:")):
            continue
        local = target.split("#", 1)[0]
        if local and not (path.parent / local).resolve().exists():
            errors.append(f"{relative}: missing local reference {target}")

if errors:
    print("Documentation validation failed:", file=sys.stderr)
    for error in errors:
        print(f"- {error}", file=sys.stderr)
    raise SystemExit(1)

print(f"documentation: PASS ({len(FILES)} Markdown files)")

