#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
out_dir="$repo_dir/docs/demo"
work_dir="$repo_dir/build/live-demo"
remote_video="/sdcard/Movies/atlas-ot-scout-live-demo.mp4"
raw_video="$work_dir/atlas-ot-scout-live-demo-raw.mp4"
final_video="$out_dir/atlas-ot-scout-emulator-demo.mp4"

mkdir -p "$out_dir" "$work_dir"

gradle --no-daemon --stacktrace \
  :capture-broker:installDebug \
  :network-broker:installDebug \
  :case-app:installDebug \
  :case-app:installDebugAndroidTest

adb shell am force-stop com.atlasot.scout
adb shell rm -f "$remote_video"
adb shell settings put system show_touches 1

recorder_pid="$(adb shell "screenrecord --size 720x1280 --bit-rate 6000000 --time-limit 180 '$remote_video' >/dev/null 2>&1 & echo \$!" | tr -d '\r')"
test -n "$recorder_pid"
sleep 2

test_status=0
adb shell am instrument -w \
  -e class com.atlasot.scout.LiveDemoCaptureTest \
  com.atlasot.scout.test/androidx.test.runner.AndroidJUnitRunner || test_status=$?

adb shell kill -2 "$recorder_pid" 2>/dev/null || true
for _ in $(seq 1 30); do
  if ! adb shell kill -0 "$recorder_pid" 2>/dev/null; then
    break
  fi
  sleep 1
done
adb shell test -s "$remote_video"

adb pull "$remote_video" "$raw_video"
ffmpeg -hide_banner -loglevel error -y -i "$raw_video" \
  -map 0:v:0 -c:v libx264 -preset medium -crf 21 -pix_fmt yuv420p \
  -movflags +faststart -an "$final_video"

ffprobe -v error \
  -show_entries format=duration,size:stream=codec_name,width,height,r_frame_rate \
  -of default=noprint_wrappers=1 "$final_video"

exit "$test_status"
