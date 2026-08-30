#!/usr/bin/env bash
set -euo pipefail

fixture_dir="${1:-testdata/research}"
mkdir -p "$fixture_dir"

curl --fail --location --silent --show-error \
  https://raw.githubusercontent.com/ITI/ICS-Security-Tools/master/pcaps/ModbusTCP/modbus_test_data_part1.pcap \
  --output "$fixture_dir/modbus.pcap"
curl --fail --location --silent --show-error \
  https://raw.githubusercontent.com/ITI/ICS-Security-Tools/master/pcaps/dnp3/dnp3_test_data_part1.pcap \
  --output "$fixture_dir/dnp3.pcap"
curl --fail --location --silent --show-error \
  https://raw.githubusercontent.com/ITI/ICS-Security-Tools/master/pcaps/IEC60870-5-104/TestDissectIec104.pcap \
  --output "$fixture_dir/iec104.pcap"
curl --fail --location --silent --show-error \
  https://raw.githubusercontent.com/ITI/ICS-Security-Tools/master/pcaps/BACnet/bacnet_test.pcap \
  --output "$fixture_dir/bacnet.pcap"

sha256sum --check <<EOF
94942b3d014810710f50836c95d3faf6df6e6370a6560bae541397c1df50213d  $fixture_dir/modbus.pcap
71779cac342a37b412df5bb6372ec22c35b5127ad2bfed4447d7ab6b92ebb4bf  $fixture_dir/dnp3.pcap
292c18a8765db3b1bcaa9bd0b8455e4e61b8366cc5910a7363b7381eb11441b8  $fixture_dir/iec104.pcap
973724800eef38e1131368d4ebce0eafc6593ccb4763916b8ee69b3b1328d8ff  $fixture_dir/bacnet.pcap
EOF
