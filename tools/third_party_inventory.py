#!/usr/bin/env python3
"""Refresh and verify Atlas third-party license inventory + CycloneDX SBOM."""

from __future__ import annotations

import argparse
import json
import re
import sys
import uuid
from dataclasses import dataclass
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
INVENTORY_PATH = REPO_ROOT / "compliance" / "third_party_inventory.json"
SBOM_PATH = REPO_ROOT / "compliance" / "atlas-third-party-sbom.cdx.json"

GRADLE_COORD_RE = re.compile(r'"([A-Za-z0-9_.-]+:[A-Za-z0-9_.-]+:([^"@]+))(?:@[^\"]+)?"')
PLUGIN_RE = re.compile(r'id\("([^"]+)"\)\s+version\s+"([^"]+)"')
KOTLIN_JVM_PLUGIN_RE = re.compile(r'kotlin\("jvm"\)\s+version\s+"([^"]+)"')
KOTLIN_DEP_RE = re.compile(r'kotlin\("([a-z-]+)"\)')
WORKFLOW_USE_RE = re.compile(r'uses:\s*([A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+@v?[0-9A-Za-z_.-]+)')
PIP_PIN_RE = re.compile(r'pip install[^\n]*\s([A-Za-z0-9_.-]+)==([0-9A-Za-z_.-]+)')
NPM_PIN_RE = re.compile(r'npm install[^\n]*\s([@A-Za-z0-9_.\-/]+)@([0-9A-Za-z_.-]+)')
APT_BLOCK_RE = re.compile(r'apt-get install[^\n]*(?:\\\n\s*[^\n]+)*', re.MULTILINE)
SHA_LINE_RE = re.compile(r'^([0-9a-f]{64})\s+\$fixture_dir/([^\s]+)$', re.MULTILINE)
GRADLE_VERSION_RE = re.compile(r'gradle-version:\s*"?([0-9A-Za-z_.-]+)"?')
JAVA_VERSION_RE = re.compile(r'java-version:\s*"?([0-9A-Za-z_.-]+)"?')
NODE_VERSION_RE = re.compile(r'node-version:\s*"?([0-9A-Za-z_.-]+)"?')
NPM_PM_RE = re.compile(r'package-manager:\s*(npm@[0-9A-Za-z_.-]+)')

ALLOWED_REDIS = {"allowed", "review-required", "prohibited"}
UNKNOWN_LICENSE_VALUES = {"", "UNKNOWN", "NOASSERTION"}


@dataclass(frozen=True)
class Discovered:
    key: str


def _read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def discover_repository_inputs() -> set[str]:
    discovered: set[str] = set()

    root_gradle = _read(REPO_ROOT / "build.gradle.kts")
    kotlin_version = None
    for plugin, version in PLUGIN_RE.findall(root_gradle):
        discovered.add(f"gradle-plugin:{plugin}@{version}")
        if plugin == "org.jetbrains.kotlin.android":
            kotlin_version = version
    for version in KOTLIN_JVM_PLUGIN_RE.findall(root_gradle):
        discovered.add(f"gradle-plugin:org.jetbrains.kotlin.jvm@{version}")
        kotlin_version = kotlin_version or version

    for gradle_file in REPO_ROOT.glob("**/*.gradle.kts"):
        text = _read(gradle_file)
        for coord, _ in GRADLE_COORD_RE.findall(text):
            group, artifact, version = coord.split(":", 2)
            discovered.add(f"maven:{group}:{artifact}@{version}")
        if kotlin_version:
            for dep in KOTLIN_DEP_RE.findall(text):
                if dep in {"stdlib", "test-junit"}:
                    discovered.add(f"maven:org.jetbrains.kotlin:kotlin-{dep}@{kotlin_version}")

    package_json = json.loads(_read(REPO_ROOT / "website" / "package.json"))
    for name, version in sorted(package_json.get("dependencies", {}).items()):
        discovered.add(f"npm:{name}@{version}")

    for path in [
        REPO_ROOT / "tools" / "pymodbus-ci.Dockerfile",
        REPO_ROOT / "tools" / "conpot-ci.Dockerfile",
        REPO_ROOT / ".github" / "workflows" / "android-ci.yml",
    ]:
        text = _read(path)
        for name, version in PIP_PIN_RE.findall(text):
            discovered.add(f"pypi:{name.lower()}@{version}")

    workflow_dir = REPO_ROOT / ".github" / "workflows"
    for wf in workflow_dir.glob("*.yml"):
        text = _read(wf)
        for action in WORKFLOW_USE_RE.findall(text):
            discovered.add(f"github-action:{action}")
        for pkg, version in NPM_PIN_RE.findall(text):
            discovered.add(f"npm:{pkg}@{version}")
        for block in APT_BLOCK_RE.findall(text):
            tokens = [t for t in re.split(r"\s+", block.replace("\\", " ")) if t]
            for token in tokens:
                if token.startswith("-") or token in {"apt-get", "install", "&&"}:
                    continue
                if token.endswith(";"):
                    token = token[:-1]
                discovered.add(f"apt:{token}@runner-default")
        for version in GRADLE_VERSION_RE.findall(text):
            discovered.add(f"tooling:gradle@{version}")
        for version in JAVA_VERSION_RE.findall(text):
            discovered.add(f"tooling:temurin-java@{version}")
        for version in NODE_VERSION_RE.findall(text):
            discovered.add(f"tooling:node@{version}")
        for pm in NPM_PM_RE.findall(text):
            discovered.add(f"tooling:{pm}")

    android_ci = _read(REPO_ROOT / ".github" / "workflows" / "android-ci.yml")
    clone_match = re.search(r'git clone --filter=blob:none\s+(https://[^\s]+)\s+([^\n]+)', android_ci)
    checkout_match = re.search(r'git -C\s+conpot-upstream\s+checkout\s+([0-9a-f]{40})', android_ci)
    if clone_match and checkout_match:
        repo_url = clone_match.group(1)
        commit = checkout_match.group(1)
        discovered.add(f"git-source:{repo_url}@{commit}")

    fetch_script = _read(REPO_ROOT / "tools" / "fetch_research_pcaps.sh")
    urls = re.findall(r'^\s*(https://[^\s\\]+)\s*\\$', fetch_script, re.MULTILINE)
    output_names = re.findall(r'--output "\$fixture_dir/([^"]+)"', fetch_script)
    sha_by_file = {filename: sha for sha, filename in SHA_LINE_RE.findall(fetch_script)}
    for url, filename in zip(urls, output_names):
        sha = sha_by_file.get(filename, "missing-sha")
        discovered.add(f"ci-asset:{filename}@{sha}")

    return discovered


def load_inventory() -> dict:
    return json.loads(_read(INVENTORY_PATH))


def tracked_keys(inventory: dict) -> set[str]:
    return {c["key"] for c in inventory["components"] if c.get("tracked_from_repo", False)}


def validate_inventory(inventory: dict, discovered: set[str]) -> tuple[list[str], list[str]]:
    errors: list[str] = []
    flagged: list[str] = []

    component_keys: set[str] = set()
    for component in inventory.get("components", []):
        key = component.get("key", "")
        if not key:
            errors.append("component missing key")
            continue
        if key in component_keys:
            errors.append(f"duplicate component key: {key}")
        component_keys.add(key)

        license_expr = str(component.get("license", "")).strip()
        if license_expr in UNKNOWN_LICENSE_VALUES:
            errors.append(f"{key}: license metadata is unknown")

        redis = component.get("redistribution_status", "")
        if redis not in ALLOWED_REDIS:
            errors.append(f"{key}: redistribution_status must be one of {sorted(ALLOWED_REDIS)}")
        if redis != "allowed":
            flagged.append(f"{key} [{redis}]")

        if not str(component.get("usage_boundary", "")).strip():
            errors.append(f"{key}: usage_boundary is required")

    tracked = tracked_keys(inventory)
    missing = sorted(discovered - tracked)
    stale = sorted(tracked - discovered)
    if missing:
        errors.append("inventory missing tracked components: " + ", ".join(missing))
    if stale:
        errors.append("inventory has stale tracked components: " + ", ".join(stale))

    unresolved = inventory.get("unresolved_licensing_questions", [])
    if not isinstance(unresolved, list):
        errors.append("unresolved_licensing_questions must be a list")

    return errors, sorted(flagged)


def license_block(license_value: str) -> list[dict]:
    if re.fullmatch(r"[A-Za-z0-9-.+]+", license_value) and any(c.isdigit() for c in license_value):
        return [{"license": {"id": license_value}}]
    if re.fullmatch(r"[A-Za-z][A-Za-z0-9-.+]+", license_value) and " " not in license_value and "/" not in license_value:
        return [{"license": {"id": license_value}}]
    return [{"license": {"name": license_value}}]


def build_sbom(inventory: dict) -> dict:
    components = []
    sorted_components = sorted(inventory["components"], key=lambda item: item["key"])
    for component in sorted_components:
        sbom_component = {
            "type": component.get("bom_type", "library"),
            "name": component["name"],
            "version": component["version"],
            "licenses": license_block(component["license"]),
            "properties": [
                {"name": "atlas:key", "value": component["key"]},
                {"name": "atlas:source", "value": component["source"]},
                {"name": "atlas:notice_required", "value": str(bool(component["notice_required"])).lower()},
                {"name": "atlas:usage_boundary", "value": component["usage_boundary"]},
                {"name": "atlas:redistribution_status", "value": component["redistribution_status"]},
                {"name": "atlas:tracked_from_repo", "value": str(bool(component.get("tracked_from_repo", False))).lower()},
            ],
        }
        if component.get("purl"):
            sbom_component["purl"] = component["purl"]
        if component.get("copyright"):
            sbom_component["copyright"] = component["copyright"]
        components.append(sbom_component)

    key_material = "\n".join(component["key"] for component in sorted_components)
    serial = f"urn:uuid:{uuid.uuid5(uuid.NAMESPACE_URL, key_material)}"
    return {
        "bomFormat": "CycloneDX",
        "specVersion": "1.6",
        "serialNumber": serial,
        "version": 1,
        "metadata": {
            "component": {
                "type": "application",
                "name": "atlas-ot-scout",
                "version": "0.1.0-p0",
            },
            "properties": [
                {"name": "atlas:inventory_source", "value": INVENTORY_PATH.relative_to(REPO_ROOT).as_posix()},
                {"name": "atlas:unresolved_licensing_questions", "value": str(len(inventory.get("unresolved_licensing_questions", [])))},
            ],
        },
        "components": components,
    }


def format_json(data: dict) -> str:
    return json.dumps(data, indent=2, sort_keys=False) + "\n"


def check_mode() -> int:
    discovered = discover_repository_inputs()
    inventory = load_inventory()
    errors, flagged = validate_inventory(inventory, discovered)
    generated = format_json(build_sbom(inventory))
    if SBOM_PATH.exists() and SBOM_PATH.read_text(encoding="utf-8") != generated:
        errors.append(
            f"{SBOM_PATH.relative_to(REPO_ROOT)} is out of date. Run: python3 tools/third_party_inventory.py --refresh"
        )
    if not SBOM_PATH.exists():
        errors.append(f"missing {SBOM_PATH.relative_to(REPO_ROOT)}")

    if errors:
        print("third-party inventory: FAIL", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    print(
        "third-party inventory: PASS "
        f"({len(inventory['components'])} components, {len(flagged)} review-required/prohibited)"
    )
    if flagged:
        print("flagged components:")
        for item in flagged:
            print(f"- {item}")
    return 0


def refresh_mode() -> int:
    discovered = discover_repository_inputs()
    inventory = load_inventory()
    errors, flagged = validate_inventory(inventory, discovered)
    if errors:
        print("third-party inventory refresh: FAIL", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    SBOM_PATH.parent.mkdir(parents=True, exist_ok=True)
    SBOM_PATH.write_text(format_json(build_sbom(inventory)), encoding="utf-8")
    print(f"wrote {SBOM_PATH.relative_to(REPO_ROOT)}")
    if flagged:
        print("flagged components:")
        for item in flagged:
            print(f"- {item}")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--refresh", action="store_true", help="write the CycloneDX SBOM from compliance/third_party_inventory.json")
    parser.add_argument("--check", action="store_true", help="verify tracked dependencies, licensing metadata and committed SBOM")
    args = parser.parse_args()

    if args.refresh and args.check:
        parser.error("choose exactly one of --refresh or --check")
    if not args.refresh and not args.check:
        parser.error("one of --refresh or --check is required")

    return refresh_mode() if args.refresh else check_mode()


if __name__ == "__main__":
    raise SystemExit(main())
