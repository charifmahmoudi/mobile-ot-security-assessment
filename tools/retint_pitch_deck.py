#!/usr/bin/env python3
"""Normalize Atlas PPTX drawing colors to the application's palette.

Images and embedded video are left untouched. Only DrawingML/theme colors are
rewritten. The application source owns the palette; this script imports it via
brand_palette.py so regenerated slides cannot drift to an independent brand.
"""
from __future__ import annotations

import argparse
import colorsys
import re
import tempfile
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

from brand_palette import load_app_palette

DRAWING_NS = "http://schemas.openxmlformats.org/drawingml/2006/main"
ET.register_namespace("a", DRAWING_NS)


def hex_rgb(value: str) -> tuple[int, int, int]:
    return tuple(int(value[i:i + 2], 16) for i in (0, 2, 4))  # type: ignore[return-value]


def semantic_color(value: str, p: dict[str, str]) -> str:
    """Map an arbitrary slide color into an application-owned semantic token."""
    r, g, b = hex_rgb(value)
    mx, mn = max(r, g, b), min(r, g, b)
    delta = mx - mn
    light = (mx + mn) / 510.0

    # Neutral colors are mapped by luminance to the app's text/surface scale.
    if delta <= 22:
        if light >= 0.95:
            return p["WHITE"]
        if light >= 0.86:
            return p["SURFACE"]
        if light >= 0.70:
            return p["BORDER"]
        if light >= 0.38:
            return p["MUTED"]
        if light >= 0.22:
            return p["SLATE"]
        return p["NAVY"]

    h, s, v = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)
    hue = h * 360.0

    # Red / magenta -> danger. The app has no pale-danger token, so very light
    # danger fills become white while strokes/text remain DANGER after mapping.
    if hue < 18 or hue >= 338:
        return p["WHITE"] if v > 0.93 and s < 0.28 else p["DANGER"]

    # Orange / yellow -> review/warning.
    if 18 <= hue < 72:
        return p["PALE_AMBER"] if light > 0.78 else p["AMBER"]

    # Green -> confirmed/success.
    if 72 <= hue < 175:
        return p["PALE_TEAL"] if light > 0.78 else p["TEAL"]

    # Cyan / blue / violet -> the app's blue family. AQUA is deliberately a
    # light-blue accent in the application, not a cyan primary action color.
    if light > 0.86:
        return p["PALE_BLUE"]
    if light > 0.68:
        return p["AQUA"]
    return p["BLUE"]


def recolor_xml(data: bytes, palette: dict[str, str]) -> tuple[bytes, int]:
    text = data.decode("utf-8")
    changed = 0

    def replace_srgb(match: re.Match[str]) -> str:
        nonlocal changed
        old = match.group(2).upper()
        new = semantic_color(old, palette)
        if new != old:
            changed += 1
        return match.group(1) + new + match.group(3)

    # DrawingML and ChartML both use srgbClr; match by local tag text so prefix
    # changes do not matter.
    text = re.sub(
        r"(\bsrgbClr\b[^>]*\bval=[\"'])([0-9A-Fa-f]{6})([\"'])",
        replace_srgb,
        text,
    )

    def replace_last(match: re.Match[str]) -> str:
        nonlocal changed
        old = match.group(2).upper()
        new = semantic_color(old, palette)
        if new != old:
            changed += 1
        return match.group(1) + new + match.group(3)

    text = re.sub(
        r"(\blastClr=[\"'])([0-9A-Fa-f]{6})([\"'])",
        replace_last,
        text,
    )
    return text.encode("utf-8"), changed


def rewrite_theme(data: bytes, palette: dict[str, str]) -> bytes:
    root = ET.fromstring(data)
    scheme = root.find(f".//{{{DRAWING_NS}}}clrScheme")
    if scheme is None:
        return data

    mapping = {
        "dk1": "NAVY",
        "lt1": "WHITE",
        "dk2": "SLATE",
        "lt2": "SURFACE",
        "accent1": "BLUE",
        "accent2": "TEAL",
        "accent3": "AQUA",
        "accent4": "AMBER",
        "accent5": "DANGER",
        "accent6": "PALE_BLUE",
        "hlink": "BLUE",
        "folHlink": "TEAL",
    }
    for slot in list(scheme):
        local = slot.tag.rsplit("}", 1)[-1]
        token = mapping.get(local)
        if not token:
            continue
        for child in list(slot):
            slot.remove(child)
        color = ET.SubElement(slot, f"{{{DRAWING_NS}}}srgbClr")
        color.set("val", palette[token])
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def verify_palette(pptx: Path, palette: dict[str, str]) -> None:
    allowed = set(palette.values())
    unexpected: set[str] = set()
    with zipfile.ZipFile(pptx) as archive:
        for name in archive.namelist():
            if not name.endswith(".xml"):
                continue
            text = archive.read(name).decode("utf-8", errors="ignore")
            for value in re.findall(r"\bsrgbClr\b[^>]*\bval=[\"']([0-9A-Fa-f]{6})[\"']", text):
                if value.upper() not in allowed:
                    unexpected.add(value.upper())
            for value in re.findall(r"\blastClr=[\"']([0-9A-Fa-f]{6})[\"']", text):
                if value.upper() not in allowed:
                    unexpected.add(value.upper())
    if unexpected:
        raise SystemExit("Presentation contains colors outside the application palette: " + ", ".join(sorted(unexpected)))


def retint(pptx: Path) -> int:
    palette = load_app_palette()
    changed = 0
    with tempfile.TemporaryDirectory(prefix="atlas-pptx-") as temp_dir:
        temp = Path(temp_dir) / pptx.name
        with zipfile.ZipFile(pptx, "r") as src, zipfile.ZipFile(temp, "w", compression=zipfile.ZIP_DEFLATED) as dst:
            for item in src.infolist():
                data = src.read(item.filename)
                if item.filename.endswith(".xml"):
                    if item.filename.startswith("ppt/theme/"):
                        data = rewrite_theme(data, palette)
                    data, count = recolor_xml(data, palette)
                    changed += count
                dst.writestr(item, data)
        temp.replace(pptx)
    verify_palette(pptx, palette)
    return changed


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("pptx", type=Path)
    args = parser.parse_args()
    if not args.pptx.is_file():
        raise SystemExit(f"PPTX not found: {args.pptx}")
    changed = retint(args.pptx)
    print(f"Retinted {args.pptx} using application palette ({changed} explicit color values normalized).")


if __name__ == "__main__":
    main()
