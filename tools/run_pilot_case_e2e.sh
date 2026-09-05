#!/usr/bin/env bash
set -euo pipefail

api_level="$(adb shell getprop ro.build.version.sdk | tr -d '\r')"
evidence_dir="build/pilot-e2e/api-${api_level}"
mkdir -p "$evidence_dir"
cp -R testdata/golden-customer-assessment "$evidence_dir/fixture-inputs"
python3 tools/verify_golden_fixture.py | tee "$evidence_dir/fixture-verification.log"

sudo -E python3 -u tools/golden_ot_emulator.py >"$evidence_dir/ot-emulator.log" 2>&1 &
ot_emulator_pid=$!
cleanup() {
  status=$?
  if [ "$status" -ne 0 ]; then
    adb logcat -d >"$evidence_dir/logcat-failure.txt" 2>&1 || true
  fi
  sudo kill "$ot_emulator_pid" 2>/dev/null || true
  wait "$ot_emulator_pid" 2>/dev/null || true
  exit "$status"
}
trap cleanup EXIT

ready=false
for attempt in $(seq 1 30); do
  if nc -z 127.0.0.1 502 && [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; then
    ready=true
    break
  fi
  sleep 1
done
if [ "$ready" != true ]; then
  echo "Android emulator or deterministic OT emulator did not become ready" >&2
  exit 1
fi

gradle --no-daemon --stacktrace \
  :network-broker:installDebug \
  :capture-broker:installDebug \
  :case-app:installDebug \
  :case-app:installDebugAndroidTest

adb shell pm clear com.atlasot.scout
adb shell am instrument -w \
  -e pilotPhase create \
  -e class com.atlasot.scout.GoldenCasePilotE2ETest#createAndAuthorizeFromEmptyApplicationState \
  com.atlasot.scout.test/androidx.test.runner.AndroidJUnitRunner \
  | tee "$evidence_dir/instrumentation-create.txt"
grep -q "OK (1 test)" "$evidence_dir/instrumentation-create.txt"

adb shell am force-stop com.atlasot.scout
adb shell am instrument -w \
  -e pilotPhase resume \
  -e class com.atlasot.scout.GoldenCasePilotE2ETest#resumeAuthorizedCaseAfterHostForcedProcessRestart \
  com.atlasot.scout.test/androidx.test.runner.AndroidJUnitRunner \
  | tee "$evidence_dir/instrumentation-resume.txt"
grep -q "OK (1 test)" "$evidence_dir/instrumentation-resume.txt"

mkdir -p "$evidence_dir/screenshots"
adb pull /sdcard/Pictures/AtlasOT/. "$evidence_dir/screenshots/"
test -n "$(find "$evidence_dir/screenshots" -type f -name '1*-golden-case-*.png' -print -quit)"
