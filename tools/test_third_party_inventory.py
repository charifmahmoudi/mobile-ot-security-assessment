#!/usr/bin/env python3
import sys
import unittest
from pathlib import Path

TOOLS_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(TOOLS_DIR))

import third_party_inventory


class ThirdPartyInventoryTest(unittest.TestCase):
    def test_discovered_inputs_match_tracked_inventory(self):
        inventory = third_party_inventory.load_inventory()
        discovered = third_party_inventory.discover_repository_inputs()
        self.assertEqual(discovered, third_party_inventory.tracked_keys(inventory))

    def test_inventory_has_no_validation_errors(self):
        inventory = third_party_inventory.load_inventory()
        discovered = third_party_inventory.discover_repository_inputs()
        errors, _ = third_party_inventory.validate_inventory(inventory, discovered)
        self.assertEqual([], errors)


if __name__ == '__main__':
    unittest.main()
