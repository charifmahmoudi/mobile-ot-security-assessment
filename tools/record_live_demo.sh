#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
out_dir="$repo_dir/docs/demo"
work_dir="$repo_dir/build/live-demo"
remote_video="/sdcard/Movies/atlas-ot-scout-live-demo.mp4"
raw_video="$work_dir/atlas-ot-scout-live-demo-raw.mp4"
final_video="$out_dir/atlas-ot-scout-emulator-demo.mp4"
container_name="atlas-pymodbus-demo"
recorder_pid=""

mkdir -p "$out_dir" "$work_dir"

cleanup() {
  if [ -n "${recorder_pid:-}" ]; then
    adb shell kill -2 "$recorder_pid" >/dev/null 2>&1 || true
  fi
  adb shell settings put system show_touches 0 >/dev/null 2>&1 || true
  docker logs "$container_name" >"$work_dir/pymodbus-demo.log" 2>&1 || true
  docker rm -f "$container_name" >/dev/null 2>&1 || true
}
trap cleanup EXIT

docker rm -f "$container_name" >/dev/null 2>&1 || true
docker run --detach --name "$container_name" --publish 502:502 atlas-pymodbus:3.11.3 >/dev/null

ready=false
for _ in $(seq 1 90); do
  if nc -z 127.0.0.1 502 && adb shell 'toybox nc -z -w 2 10.0.2.2 502' >/dev/null 2>&1; then
    ready=true
    break
  fi
  sleep 1
done
if [ "$ready" != true ]; then
  echo "Android emulator could not route to the PyModbus demo target" >&2
  exit 1
fi

gradle --no-daemon --stacktrace \
  :capture-broker:installDebug \
  :network-broker:installDebug \
  :case-app:installDebug \
  :case-app:installDebugAndroidTest

adb shell am force-stop com.atlasot.scout
adb shell rm -f "$remote_video"
adb shell settings put system show_touches 1

recorder_pid="$(adb shell "screenrecord --size 720x1280 --bit-rate 3500000 --time-limit 180 '$remote_video' >/dev/null 2>&1 & echo \$!" | tr -d '\r')"
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
recorder_pid=""
adb shell test -s "$remote_video"

adb pull "$remote_video" "$raw_video" >/dev/null
test -s "$raw_video"

if command -v ffmpeg >/dev/null 2>&1; then
  ffmpeg -hide_banner -loglevel error -y \
    -i "$raw_video" \
    -c:v libx264 -preset veryfast -crf 24 -pix_fmt yuv420p -movflags +faststart \
    -an "$final_video"
else
  install -m 0644 "$raw_video" "$final_video"
fi

test -s "$final_video"
file "$final_video"
if command -v ffprobe >/dev/null 2>&1; then
  ffprobe -v error -show_entries format=duration,size -of default=noprint_wrappers=1 "$final_video" \
    | tee "$work_dir/video-metadata.txt"
fi

exit "$test_status"
