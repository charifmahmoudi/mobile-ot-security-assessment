#!/usr/bin/env python3
"""Fail CI when the Android package boundary drifts from the safety design."""

from pathlib import Path
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ANDROID = "{http://schemas.android.com/apk/res/android}"


def manifest(module: str) -> ET.Element:
    path = ROOT / module / "src/main/AndroidManifest.xml"
    if not path.is_file():
        raise AssertionError(f"missing manifest: {path.relative_to(ROOT)}")
    return ET.parse(path).getroot()


def permissions(root: ET.Element) -> set[str]:
    return {
        node.attrib[f"{ANDROID}name"]
        for node in root.findall("uses-permission")
    }


def main() -> int:
    case = manifest("case-app")
    broker = manifest("network-broker")

    assert "android.permission.INTERNET" not in permissions(case), (
        "case-app must remain network-unprivileged"
    )
    assert "android.permission.INTERNET" in permissions(broker), (
        "network-broker is the only module expected to own sockets"
    )

    parser = case.find("./application/service[@android:name='.ParserService']", {"android": ANDROID[1:-1]})
    assert parser is not None, "isolated parser service is missing"
    assert parser.attrib.get(f"{ANDROID}isolatedProcess") == "true"
    assert parser.attrib.get(f"{ANDROID}exported") == "false"

    permission = broker.find("permission[@android:name='com.atlasot.permission.BIND_NETWORK_BROKER']", {"android": ANDROID[1:-1]})
    assert permission is not None, "broker signature permission is missing"
    assert permission.attrib.get(f"{ANDROID}protectionLevel") == "signature"

    service = broker.find("./application/service[@android:name='.BrokerService']", {"android": ANDROID[1:-1]})
    assert service is not None, "broker service is missing"
    assert service.attrib.get(f"{ANDROID}permission") == "com.atlasot.permission.BIND_NETWORK_BROKER"

    print("architecture invariants: PASS")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (AssertionError, ET.ParseError) as error:
        print(f"architecture invariants: FAIL: {error}", file=sys.stderr)
        raise SystemExit(1)
