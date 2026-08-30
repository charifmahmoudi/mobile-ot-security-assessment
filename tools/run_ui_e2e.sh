#!/usr/bin/env bash
set -euo pipefail

gradle --no-daemon --stacktrace connectedDebugAndroidTest
mkdir -p build/emulator-screenshots
adb pull /sdcard/Pictures/AtlasOT build/emulator-screenshots/
test -n "$(find build/emulator-screenshots -type f -name '*.png' -print -quit)"
