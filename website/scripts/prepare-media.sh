#!/usr/bin/env bash
set -euo pipefail

SOURCE="docs/demo/atlas-ot-scout-emulator-demo.mp4"
OUT="website/public/media"

if [[ ! -f "$SOURCE" ]]; then
  echo "Missing demo source: $SOURCE" >&2
  exit 1
fi

command -v ffmpeg >/dev/null
command -v ffprobe >/dev/null

mkdir -p "$OUT"
cp "$SOURCE" "$OUT/atlas-ot-scout-demo.mp4"

duration="$(ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 "$SOURCE")"

extract_frame() {
  local name="$1"
  local fraction="$2"
  local timestamp
  timestamp="$(awk -v d="$duration" -v f="$fraction" 'BEGIN { printf "%.3f", d * f }')"
  ffmpeg -hide_banner -loglevel error -y \
    -ss "$timestamp" -i "$SOURCE" -frames:v 1 \
    -vf "scale=1280:-2:force_original_aspect_ratio=decrease" \
    -q:v 2 "$OUT/$name.jpg"
}

# Sample the composed, CI-recorded application story at three distinct stages.
extract_frame "atlas-collect" 0.22
extract_frame "atlas-reconcile" 0.55
extract_frame "atlas-handoff" 0.82

printf 'Prepared Atlas website media from %s (duration %ss)\n' "$SOURCE" "$duration"
