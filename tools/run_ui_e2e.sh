#!/usr/bin/env bash
set -euo pipefail

gradle --no-daemon --stacktrace :capture-broker:installDebug
gradle --no-daemon --stacktrace :case-app:connectedDebugAndroidTest :network-broker:connectedDebugAndroidTest
mkdir -p build/emulator-screenshots
adb pull /sdcard/Pictures/AtlasOT build/emulator-screenshots/
test -n "$(find build/emulator-screenshots -type f -name '*.png' -print -quit)"
