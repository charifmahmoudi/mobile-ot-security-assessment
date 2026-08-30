#!/usr/bin/env bash
set -euo pipefail

build_dir="${PWD}/build/capture-daemon-test"
mkdir -p "$build_dir"
cc -std=c11 -O2 -Wall -Wextra -Werror appliance/capture-daemon/atlas_capture.c -o "$build_dir/atlas_capture"

if nm -u "$build_dir/atlas_capture" | grep -Eq '[[:space:]](send|sendto|sendmsg)@'; then
  echo "capture daemon links a packet-transmission syscall" >&2
  exit 1
fi

command -v ip >/dev/null || { echo "iproute2 unavailable; compile/static gate passed"; exit 0; }
if [ "$(id -u)" -eq 0 ]; then SUDO=; else SUDO=sudo; fi
capture_file="$build_dir/live-span.pcap"
rm -f "$capture_file"
$SUDO ip link del atlas_tx 2>/dev/null || true
cleanup() { $SUDO ip link del atlas_tx 2>/dev/null || true; }
trap cleanup EXIT
if ! $SUDO ip link add atlas_tx type veth peer name atlas_rx; then
  if [ "${CI:-false}" = "true" ]; then
    echo "CI runner could not create the virtual SPAN link" >&2
    exit 1
  fi
  echo "network namespace lacks CAP_NET_ADMIN; compile/static gate passed"
  exit 0
fi
$SUDO ip link set atlas_tx up
$SUDO ip link set atlas_rx up
before_tx=$(cat /sys/class/net/atlas_rx/statistics/tx_packets)
$SUDO "$build_dir/atlas_capture" --interface atlas_rx --output "$capture_file" --max-bytes 1048576 --duration-ms 2000 >"$build_dir/result.json" &
capture_pid=$!
sleep 0.2
$SUDO python3 - testdata/research/modbus.pcap atlas_tx <<'PY'
import socket, struct, sys, time
data = open(sys.argv[1], 'rb').read()
magic = data[:4]
endian = '<' if magic in (b'\xd4\xc3\xb2\xa1', b'\x4d\x3c\xb2\xa1') else '>'
captured = struct.unpack_from(endian + 'I', data, 24 + 8)[0]
frame = data[24 + 16:24 + 16 + captured]
s = socket.socket(socket.AF_PACKET, socket.SOCK_RAW)
s.bind((sys.argv[2], 0))
for _ in range(4):
    s.send(frame); time.sleep(0.03)
s.close()
PY
wait "$capture_pid"
after_tx=$(cat /sys/class/net/atlas_rx/statistics/tx_packets)
test "$before_tx" = "$after_tx"
$SUDO chown "$(id -u):$(id -g)" "$capture_file"
python3 - "$capture_file" <<'PY'
import struct, sys
data = open(sys.argv[1], 'rb').read()
assert len(data) > 40, len(data)
assert data[:4] == b'\xd4\xc3\xb2\xa1', data[:4].hex()
captured = struct.unpack_from('<I', data, 24 + 8)[0]
assert captured >= 14
assert len(data) >= 24 + 16 + captured
print('live capture daemon: PASS')
PY
cat "$build_dir/result.json"
