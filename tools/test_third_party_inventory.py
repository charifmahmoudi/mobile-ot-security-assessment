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

    def test_committed_sbom_matches_generated_content(self):
        inventory = third_party_inventory.load_inventory()
        generated = third_party_inventory.format_json(third_party_inventory.build_sbom(inventory))
        committed = third_party_inventory.SBOM_PATH.read_text(encoding="utf-8")
        self.assertEqual(committed, generated)

    def test_extract_ci_assets_requires_balanced_url_output_pairs(self):
        script = '''
curl --fail --location --silent --show-error \\
  https://example.test/a.pcap \\
  --output "$fixture_dir/a.pcap"
curl --fail --location --silent --show-error \\
  https://example.test/b.pcap \\
sha256sum --check <<EOF
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa  $fixture_dir/a.pcap
EOF
'''
        with self.assertRaises(ValueError):
            third_party_inventory.extract_ci_assets(script)

    def test_extract_ci_assets_uses_output_name_and_sha(self):
        script = '''
curl --fail --location --silent --show-error \\
  https://example.test/a.pcap \\
  --output "$fixture_dir/custom-name.pcap"
sha256sum --check <<EOF
aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa  $fixture_dir/custom-name.pcap
EOF
'''
        self.assertEqual(
            {"ci-asset:custom-name.pcap@aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"},
            third_party_inventory.extract_ci_assets(script),
        )

    def test_extract_ci_assets_requires_sha_for_each_output(self):
        script = '''
curl --fail --location --silent --show-error \\
  https://example.test/a.pcap \\
  --output "$fixture_dir/a.pcap"
sha256sum --check <<EOF
bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb  $fixture_dir/other.pcap
EOF
'''
        with self.assertRaises(ValueError):
            third_party_inventory.extract_ci_assets(script)

    def test_extract_apt_packages_from_multiline_install_block(self):
        workflow = '''
run: |
  sudo apt-get install -y \\
    ffmpeg \\
    python3-numpy
'''
        self.assertEqual(
            {"apt:ffmpeg@runner-default", "apt:python3-numpy@runner-default"},
            third_party_inventory.extract_apt_packages(workflow),
        )

    def test_license_block_formats_spdx_and_custom_names(self):
        self.assertEqual(
            [{"license": {"id": "Apache-2.0"}}],
            third_party_inventory.license_block("Apache-2.0"),
        )
        self.assertEqual(
            [{"license": {"id": "GPL-2.0-only"}}],
            third_party_inventory.license_block("GPL-2.0-only"),
        )
        self.assertEqual(
            [{"license": {"expression": "MPL-2.0 AND LGPL-3.0-or-later"}}],
            third_party_inventory.license_block("MPL-2.0 AND LGPL-3.0-or-later"),
        )
        self.assertEqual(
            [{"license": {"name": "Ubuntu package metadata"}}],
            third_party_inventory.license_block("Ubuntu package metadata"),
        )


if __name__ == '__main__':
    unittest.main()
