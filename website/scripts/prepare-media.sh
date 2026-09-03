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

extract_at() {
  local name="$1"
  local timestamp="$2"
  ffmpeg -hide_banner -loglevel error -y \
    -ss "$timestamp" -i "$SOURCE" -frames:v 1 \
    -vf "scale=1280:-2:force_original_aspect_ratio=decrease" \
    -q:v 2 "$OUT/$name.jpg"
}

# Stable moments in the composed 109-second customer story.
# These are intentionally explicit rather than percentage-based so the labels
# keep matching the product stage if the final video duration changes slightly.
extract_at "atlas-collect" 24
extract_at "atlas-reconcile" 40
extract_at "atlas-handoff" 94

printf 'Prepared Atlas website media from %s (duration %ss)\n' "$SOURCE" "$duration"
