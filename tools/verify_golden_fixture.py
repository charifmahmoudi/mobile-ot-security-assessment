#!/usr/bin/env python3
import csv
import hashlib
import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
FIXTURE = ROOT / "testdata" / "golden-customer-assessment"


def read_json(name: str):
    with (FIXTURE / name).open(encoding="utf-8") as source:
        return json.load(source)


case = read_json("case.json")
passive = read_json("passive-evidence.json")
outcomes = read_json("expected-outcomes.json")
with (FIXTURE / "expected-inventory.csv").open(encoding="utf-8", newline="") as source:
    inventory = list(csv.DictReader(source))

assert case["assessment_pack"] == "P0-WATER"
assert case["scope_cidrs"] == ["10.0.2.2/32"]
assert case["allowlisted_modbus_target"] == {
    "ipv4": "10.0.2.2",
    "port": 502,
    "unit_id": 1,
    "operation": "MODBUS_DEVICE_ID_BASIC",
}
assert len(inventory) == 4
assert sum(row["scope_status"] == "excluded" for row in inventory) == 1
assert len(passive["observations"]) == 3
canonical = json.dumps(
    passive["observations"], sort_keys=True, separators=(",", ":"), ensure_ascii=False
).encode("utf-8")
assert hashlib.sha256(canonical).hexdigest() == passive["sha256"]
assert outcomes["reporting"]["ready"] is False
assert len(outcomes["reporting"]["blockers"]) == case["expected_outcomes"]["report_blockers"]
print("Golden Customer Assessment fixture verified")
