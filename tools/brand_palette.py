#!/usr/bin/env python3
"""Single source of truth checks for Atlas visual palette.

The application owns the color tokens. Website and presentation tooling must
match the RGB values defined in MainActivity.kt rather than maintaining a
separate brand palette by memory.
"""
from __future__ import annotations

import argparse
import re
from pathlib import Path

APP_SOURCE = Path("case-app/src/main/kotlin/com/atlasot/scout/MainActivity.kt")

TOKEN_NAMES = (
    "NAVY", "MUTED", "SLATE", "TEAL", "SUCCESS", "AQUA", "BLUE", "AMBER",
    "DANGER", "SURFACE", "WHITE", "BORDER", "PALE_TEAL", "PALE_AMBER", "PALE_BLUE",
)

CSS_TOKEN_MAP = {
    "navy": "NAVY",
    "text": "NAVY",
    "muted": "MUTED",
    "slate": "SLATE",
    "teal": "TEAL",
    "success": "SUCCESS",
    "aqua": "AQUA",
    "blue": "BLUE",
    "amber": "AMBER",
    "danger": "DANGER",
    "surface": "SURFACE",
    "white": "WHITE",
    "border": "BORDER",
    "pale-teal": "PALE_TEAL",
    "pale-amber": "PALE_AMBER",
    "pale-blue": "PALE_BLUE",
    "pale": "PALE_BLUE",
}


def rgb_hex(r: int, g: int, b: int) -> str:
    return f"{r:02X}{g:02X}{b:02X}"


def load_app_palette(path: Path = APP_SOURCE) -> dict[str, str]:
    text = path.read_text(encoding="utf-8")
    palette: dict[str, str] = {}
    for name, r, g, b in re.findall(
        r"private\s+val\s+([A-Z_]+)\s*=\s*Color\.rgb\((\d+),\s*(\d+),\s*(\d+)\)", text
    ):
        palette[name] = rgb_hex(int(r), int(g), int(b))
    if re.search(r"private\s+val\s+WHITE\s*=\s*Color\.WHITE", text):
        palette["WHITE"] = "FFFFFF"

    missing = [name for name in TOKEN_NAMES if name not in palette]
    if missing:
        raise SystemExit(f"Missing application color tokens: {', '.join(missing)}")
    return palette


def verify_website(css_path: Path, palette: dict[str, str]) -> None:
    css = css_path.read_text(encoding="utf-8")
    actual = {
        name: value.upper()
        for name, value in re.findall(r"--color-([a-z0-9-]+):\s*#([0-9a-fA-F]{6})", css)
    }
    errors: list[str] = []
    for css_name, app_name in CSS_TOKEN_MAP.items():
        expected = palette[app_name]
        got = actual.get(css_name)
        if got != expected:
            errors.append(f"--color-{css_name}: expected #{expected} from {app_name}, got {('#' + got) if got else 'missing'}")
    if errors:
        raise SystemExit("Website palette drifted from the application:\n" + "\n".join(errors))


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--verify-website", type=Path)
    parser.add_argument("--print", action="store_true", dest="print_palette")
    args = parser.parse_args()

    palette = load_app_palette()
    if args.verify_website:
        verify_website(args.verify_website, palette)
    if args.print_palette:
        for name in TOKEN_NAMES:
            print(f"{name}=#{palette[name]}")


if __name__ == "__main__":
    main()
