#!/usr/bin/env bash
set -euo pipefail

target="${1:?usage: run_active_e2e.sh pymodbus|modbus-tk|conpot}"
case "$target" in
  pymodbus)
    docker run --detach --name atlas-pymodbus --publish 502:502 atlas-pymodbus:3.11.3
    ;;
  modbus-tk)
    nohup sudo -E .ot-emulator-venv/bin/python -u tools/modbus_tk_testbed.py >modbus-tk.log 2>&1 &
    ;;
  conpot)
    docker run --detach --name atlas-conpot --publish 502:5020 atlas-conpot-ci:32fc03b
    ;;
  *)
    echo "unknown OT emulator: $target" >&2
    exit 2
    ;;
esac

ready=false
for attempt in $(seq 1 90); do
  if nc -z 127.0.0.1 502 && adb shell 'toybox nc -z -w 2 10.0.2.2 502'; then
    ready=true
    break
  fi
  sleep 1
done
if [ "$ready" != true ]; then
  docker ps --all || true
  echo "Android emulator could not route to the host OT testbed" >&2
  exit 1
fi

gradle --no-daemon --stacktrace :network-broker:installDebug
gradle --no-daemon --stacktrace :capture-broker:installDebug
gradle --no-daemon --stacktrace :case-app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.activeTest=true \
  -Pandroid.testInstrumentationRunnerArguments.expectedIdentity=MODBUS \
  -Pandroid.testInstrumentationRunnerArguments.screenshotPrefix="05-active-${target}"
mkdir -p "build/emulator-screenshots/${target}"
adb pull /sdcard/Pictures/AtlasOT "build/emulator-screenshots/${target}/"
test -n "$(find "build/emulator-screenshots/${target}" -type f -name '*.png' -print -quit)"
