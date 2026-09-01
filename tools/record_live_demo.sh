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

for tool in ffmpeg ffprobe; do
  if ! command -v "$tool" >/dev/null 2>&1; then
    echo "$tool is required to produce and validate the buyer demo" >&2
    exit 1
  fi
done

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

# Record the real phone UI at a native-like portrait ratio. Post-processing only
# removes deterministic startup frames and normalizes the media profile; it does
# not replace or synthesize application states.
recorder_pid="$(adb shell "screenrecord --size 720x1600 --bit-rate 4000000 --time-limit 180 '$remote_video' >/dev/null 2>&1 & echo \$!" | tr -d '\r')"
test -n "$recorder_pid"
sleep 2

test_status=0
adb shell am instrument -w \
  -e recordLiveDemo true \
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

# Produce a deliberately conservative playback profile for PowerPoint, common
# browsers and standard desktop players: H.264 constrained-baseline, constant
# 30 fps, yuv420p, AAC-LC and the MP4 moov atom at the front of the file.
ffmpeg -hide_banner -loglevel error -y \
  -ss 11.5 -i "$raw_video" \
  -f lavfi -i anullsrc=channel_layout=stereo:sample_rate=48000 \
  -map 0:v:0 -map 1:a:0 \
  -vf "fps=30,format=yuv420p" \
  -c:v libx264 -profile:v baseline -level:v 4.0 -preset medium -crf 22 -tag:v avc1 \
  -c:a aac -profile:a aac_low -b:a 96k \
  -shortest -movflags +faststart \
  "$final_video"

test -s "$final_video"
file "$final_video"

video_codec="$(ffprobe -v error -select_streams v:0 -show_entries stream=codec_name -of default=nw=1:nk=1 "$final_video")"
video_profile="$(ffprobe -v error -select_streams v:0 -show_entries stream=profile -of default=nw=1:nk=1 "$final_video")"
pix_fmt="$(ffprobe -v error -select_streams v:0 -show_entries stream=pix_fmt -of default=nw=1:nk=1 "$final_video")"
avg_rate="$(ffprobe -v error -select_streams v:0 -show_entries stream=avg_frame_rate -of default=nw=1:nk=1 "$final_video")"
audio_codec="$(ffprobe -v error -select_streams a:0 -show_entries stream=codec_name -of default=nw=1:nk=1 "$final_video")"

[ "$video_codec" = "h264" ]
case "$video_profile" in *Baseline*) ;; *) echo "Unexpected H.264 profile: $video_profile" >&2; exit 1 ;; esac
[ "$pix_fmt" = "yuv420p" ]
[ "$avg_rate" = "30/1" ]
[ "$audio_codec" = "aac" ]

python3 - "$final_video" <<'PY'
from pathlib import Path
import sys
p = Path(sys.argv[1])
data = p.read_bytes()
moov = data.find(b'moov')
mdat = data.find(b'mdat')
if moov < 0 or mdat < 0 or moov > mdat:
    raise SystemExit('MP4 fast-start validation failed: moov atom is not before mdat')
PY

ffprobe -v error \
  -show_entries format=duration,size,start_time:stream=index,codec_name,profile,codec_type,pix_fmt,width,height,avg_frame_rate \
  -of default=noprint_wrappers=1 "$final_video" \
  | tee "$work_dir/video-metadata.txt"

exit "$test_status"
