#!/usr/bin/env python3
"""Fail CI when repository documentation is broken, orphaned, or violates authority boundaries."""

from __future__ import annotations

import re
import sys
from collections import deque
from pathlib import Path
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
ROOT_FILES = sorted(ROOT.glob("*.md"))
DOC_FILES = sorted(DOCS.rglob("*.md"))
FILES = ROOT_FILES + DOC_FILES

MARKDOWN_LINK = re.compile(r"!?\[[^\]]*\]\(([^)]+)\)")
INLINE_REPO_PATH = re.compile(
    r"`("
    r"(?:docs|tools|schemas|\.github)/[A-Za-z0-9_./-]+\.(?:md|csv|json|ya?ml|py|sh|kt|java|c|xml|pptx|pdf|mp4|png)"
    r"|(?:README|IMPLEMENTATION|ROADMAP|SECURITY|CONTRIBUTING|GOVERNANCE)\.md"
    r")`"
)
FENCED_BLOCK = re.compile(r"```.*?```", re.DOTALL)
HEADING = re.compile(r"^#{1,6}\s+(.+?)\s*#*\s*$", re.MULTILINE)
ADR_FILENAME = re.compile(r"^(\d{4})-[a-z0-9-]+\.md$")
ADR_HEADING = re.compile(r"^# ADR (\d{4}):", re.MULTILINE)

# These are intentionally narrow semantic guardrails. They catch the kinds of
# duplication that previously caused real drift while avoiding a brittle ban on
# ordinary explanatory words such as "current" or "implemented".
AUTHORITY_RULES: list[tuple[str, re.Pattern[str], set[str]]] = [
    (
        "roadmap milestone status tables belong only in ROADMAP.md",
        re.compile(r"^\|\s*M[0-9]\b", re.MULTILINE),
        {"ROADMAP.md"},
    ),
    (
        "evaluation duration belongs only in the canonical commercial offer",
        re.compile(
            r"\b30[-‑–]day\b|\b60[-‑–]day\b|\b30 calendar days\b|"
            r"\bmaximum free evaluation period\b",
            re.IGNORECASE,
        ),
        {"docs/business-development/OFFER.md"},
    ),
    (
        "obsolete external Raspberry-Pi capture design may appear only in supersession history",
        re.compile(r"Raspberry Pi", re.IGNORECASE),
        {
            "docs/adr/0003-external-passive-capture.md",
            "docs/adr/0007-dedicated-android-passive-capture.md",
        },
    ),
    (
        "the removed CAPTURE-ACCESSORY.md must not be referenced",
        re.compile(r"CAPTURE-ACCESSORY\.md"),
        set(),
    ),
    (
        "execution grants must not be documented as Ed25519",
        re.compile(
            r"Ed25519\s+grant\s+signature|Execution\s+grant\s*\|\s*Ed25519",
            re.IGNORECASE,
        ),
        set(),
    ),
    (
        "exact execution-grant algorithm may be repeated only in its architecture/security/status authorities",
        re.compile(r"SHA256withECDSA|secp256r1", re.IGNORECASE),
        {
            "docs/architecture/NETWORK-EXECUTION.md",
            "docs/architecture/SECURITY-AND-THREAT-MODEL.md",
            "IMPLEMENTATION.md",
        },
    ),
    (
        "mutable current-capability headings belong in IMPLEMENTATION.md",
        re.compile(
            r"^##\s+Current\s+(?:limits|PoC boundary|implemented behavior|verified reference)\b",
            re.IGNORECASE | re.MULTILINE,
        ),
        {"IMPLEMENTATION.md"},
    ),
]

errors: list[str] = []
graph: dict[Path, set[Path]] = {path: set() for path in FILES}
direct_links: dict[Path, set[Path]] = {path: set() for path in FILES}
root_resolved = ROOT.resolve()


def relative(path: Path) -> str:
    return path.relative_to(ROOT).as_posix()


def visible_markdown(text: str) -> str:
    return FENCED_BLOCK.sub("", text)


def clean_target(raw_target: str) -> tuple[str, str]:
    """Return decoded local path and fragment portions of a Markdown target."""
    target = raw_target.strip().split(maxsplit=1)[0].strip("<>")
    path_part, separator, fragment = target.partition("#")
    return unquote(path_part), unquote(fragment if separator else "")


def is_external(target: str) -> bool:
    lowered = target.lower()
    return lowered.startswith(("http://", "https://", "mailto:", "tel:", "data:"))


def markdown_anchor(value: str) -> str:
    value = re.sub(r"<[^>]+>", "", value)
    value = value.replace("`", "").strip().lower()
    value = re.sub(r"[^\w\-\s]", "", value, flags=re.UNICODE)
    value = re.sub(r"\s+", "-", value)
    return re.sub(r"-+", "-", value).strip("-")


_anchor_cache: dict[Path, set[str]] = {}


def anchors_for(path: Path) -> set[str]:
    if path not in _anchor_cache:
        anchors: set[str] = set()
        counts: dict[str, int] = {}
        text = visible_markdown(path.read_text(encoding="utf-8"))
        for heading in HEADING.findall(text):
            base = markdown_anchor(heading)
            if not base:
                continue
            number = counts.get(base, 0)
            counts[base] = number + 1
            anchors.add(base if number == 0 else f"{base}-{number}")
        _anchor_cache[path] = anchors
    return _anchor_cache[path]


def resolve_local_target(
    source: Path, raw_target: str, *, repo_root: bool = False
) -> tuple[Path | None, str]:
    path_part, fragment = clean_target(raw_target)
    if is_external(path_part):
        return None, ""

    if not path_part:
        candidate = source
    elif repo_root:
        candidate = ROOT / path_part
    else:
        candidate = source.parent / path_part
    try:
        resolved = candidate.resolve()
        resolved.relative_to(root_resolved)
    except ValueError:
        errors.append(f"{relative(source)}: reference escapes repository root: {path_part}")
        return None, fragment

    if resolved.is_dir():
        landing = resolved / "README.md"
        if not landing.exists():
            errors.append(
                f"{relative(source)}: directory reference {path_part or '.'} has no README.md landing page"
            )
            return None, fragment
        resolved = landing

    if not resolved.exists():
        errors.append(f"{relative(source)}: missing local reference {path_part or '#'+fragment}")
        return None, fragment

    return resolved, fragment


for path in FILES:
    text = path.read_text(encoding="utf-8")
    visible = visible_markdown(text)
    rel = relative(path)

    if text.count("```") % 2:
        errors.append(f"{rel}: unbalanced fenced code block")

    if not re.search(r"^#\s+\S", visible, re.MULTILINE):
        errors.append(f"{rel}: missing level-one heading")

    for rule_name, pattern, allowed_paths in AUTHORITY_RULES:
        if rel not in allowed_paths and pattern.search(visible):
            errors.append(f"{rel}: authority violation: {rule_name}")

    for raw_target in MARKDOWN_LINK.findall(visible):
        target, fragment = resolve_local_target(path, raw_target)
        if target is None:
            continue

        direct_links[path].add(target)
        if target.suffix.lower() == ".md":
            graph[path].add(target)
            if fragment:
                normalized = markdown_anchor(fragment)
                if normalized not in anchors_for(target):
                    errors.append(
                        f"{rel}: missing anchor #{fragment} in {relative(target)}"
                    )

    for raw_target in INLINE_REPO_PATH.findall(visible):
        target, _ = resolve_local_target(
            path, raw_target.rstrip(".,;:"), repo_root=True
        )
        if target is None:
            continue

# Every documentation directory must expose one landing page and link its
# immediate Markdown files plus child documentation sections.
for directory in sorted(path for path in DOCS.rglob("*") if path.is_dir()) + [DOCS]:
    direct_markdown = sorted(
        path for path in directory.glob("*.md") if path.name != "README.md"
    )
    child_sections = sorted(
        child / "README.md"
        for child in directory.iterdir()
        if child.is_dir() and any(child.rglob("*.md"))
    )
    if not direct_markdown and not child_sections:
        continue

    index = directory / "README.md"
    if not index.exists():
        errors.append(f"{relative(directory)}: documentation directory has no README.md")
        continue

    indexed = direct_links.get(index, set())
    for required in direct_markdown + child_sections:
        if required not in indexed:
            errors.append(
                f"{relative(index)}: does not index {relative(required)}"
            )

# The repository landing page must expose every top-level policy/status document
# and the canonical documentation index.
root_index = ROOT / "README.md"
for required in [path for path in ROOT_FILES if path != root_index] + [DOCS / "README.md"]:
    if required not in direct_links.get(root_index, set()):
        errors.append(f"README.md: does not index {relative(required)}")

# All documents must be reachable from the appropriate landing page, not merely
# exist on disk.
def reachable(start: Path) -> set[Path]:
    seen: set[Path] = set()
    queue: deque[Path] = deque([start])
    while queue:
        current = queue.popleft()
        if current in seen:
            continue
        seen.add(current)
        queue.extend(graph.get(current, set()) - seen)
    return seen


root_reachable = reachable(root_index)
for path in ROOT_FILES:
    if path not in root_reachable:
        errors.append(f"{relative(path)}: unreachable from README.md")

docs_index = DOCS / "README.md"
docs_reachable = reachable(docs_index)
for path in DOC_FILES:
    if path not in docs_reachable:
        errors.append(f"{relative(path)}: unreachable from docs/README.md")

# ADR IDs are part of the public architecture record and must remain unique and
# internally consistent.
adr_ids: dict[str, Path] = {}
for path in sorted((DOCS / "adr").glob("*.md")):
    if path.name == "README.md":
        continue
    filename_match = ADR_FILENAME.fullmatch(path.name)
    if not filename_match:
        errors.append(f"{relative(path)}: ADR filename must start with a four-digit ID")
        continue
    adr_id = filename_match.group(1)
    heading_match = ADR_HEADING.search(path.read_text(encoding="utf-8"))
    if not heading_match:
        errors.append(f"{relative(path)}: missing '# ADR {adr_id}:' heading")
    elif heading_match.group(1) != adr_id:
        errors.append(
            f"{relative(path)}: filename ADR {adr_id} does not match heading ADR {heading_match.group(1)}"
        )
    if adr_id in adr_ids:
        errors.append(
            f"{relative(path)}: duplicate ADR {adr_id}; first used by {relative(adr_ids[adr_id])}"
        )
    else:
        adr_ids[adr_id] = path

if errors:
    print("Documentation validation failed:", file=sys.stderr)
    for error in sorted(set(errors)):
        print(f"- {error}", file=sys.stderr)
    raise SystemExit(1)

print(
    f"documentation: PASS ({len(FILES)} Markdown files; "
    f"{len(adr_ids)} ADRs; all documents indexed, reachable and authority-checked)"
)
