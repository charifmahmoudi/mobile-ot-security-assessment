#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
shot_dir="$repo_dir/docs/user-guide/screenshots"
out_dir="$repo_dir/docs/demo"
work_dir="$repo_dir/tmp/demo-video"
font_regular="/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
font_bold="/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"

mkdir -p "$out_dir" "$work_dir"
truncate -s 0 "$work_dir/concat.txt"

make_card() {
  local index="$1" duration="$2" eyebrow="$3" title="$4" body="$5"
  local title1 title2
  IFS='|' read -r title1 title2 <<< "$title"
  ffmpeg -hide_banner -loglevel error -y \
    -f lavfi -i "color=c=0x071A2D:s=1920x1080:d=${duration}:r=30" \
    -f lavfi -i "anullsrc=channel_layout=stereo:sample_rate=48000:d=${duration}" \
    -vf "drawbox=x=0:y=0:w=22:h=1080:color=0x2D63E2:t=fill,
         drawbox=x=116:y=176:w=116:h=8:color=0x2D63E2:t=fill,
         drawtext=fontfile=${font_bold}:text='${eyebrow}':fontcolor=0x7FA5FF:fontsize=29:x=116:y=116,
         drawtext=fontfile=${font_bold}:text='${title1}':fontcolor=white:fontsize=72:x=116:y=224,
         drawtext=fontfile=${font_bold}:text='${title2}':fontcolor=white:fontsize=72:x=116:y=316,
         drawtext=fontfile=${font_regular}:text='${body}':fontcolor=0xC7D2E0:fontsize=30:line_spacing=15:x=120:y=650,
         drawtext=fontfile=${font_regular}:text='ANDROID 15 EMULATOR  •  API 35  •  CONTROLLED LAB':fontcolor=0x8DA0B5:fontsize=21:x=120:y=978,
         fade=t=in:st=0:d=0.6,fade=t=out:st=$(awk "BEGIN {print ${duration}-0.6}"):d=0.6" \
    -c:v libx264 -preset medium -crf 18 -pix_fmt yuv420p -c:a aac -b:a 128k -shortest \
    "$work_dir/${index}.mp4"
}

make_scene() {
  local index="$1" image="$2" eyebrow="$3" title="$4" body="$5" decision="$6"
  local duration=18
  local body1 body2 body3
  IFS='|' read -r body1 body2 body3 <<< "$body"
  ffmpeg -hide_banner -loglevel error -y \
    -f lavfi -i "color=c=0xEAF0F6:s=1920x1080:d=${duration}:r=30" \
    -loop 1 -t "$duration" -i "$shot_dir/$image" \
    -f lavfi -i "anullsrc=channel_layout=stereo:sample_rate=48000:d=${duration}" \
    -filter_complex "[1:v]scale=-2:930,setsar=1[phone];
      [0:v]drawbox=x=0:y=0:w=24:h=1080:color=0x2D63E2:t=fill,
      drawbox=x=88:y=72:w=530:h=44:color=0xDCE7FF:t=fill,
      drawtext=fontfile=${font_bold}:text='${eyebrow}':fontcolor=0x2457D6:fontsize=22:x=110:y=82,
      drawtext=fontfile=${font_bold}:text='${title}':fontcolor=0x0B1F33:fontsize=55:line_spacing=12:x=88:y=170,
      drawtext=fontfile=${font_regular}:text='${body1}':fontcolor=0x334155:fontsize=27:x=92:y=440,
      drawtext=fontfile=${font_regular}:text='${body2}':fontcolor=0x334155:fontsize=27:x=92:y=484,
      drawtext=fontfile=${font_regular}:text='${body3}':fontcolor=0x334155:fontsize=27:x=92:y=528,
      drawbox=x=88:y=755:w=940:h=146:color=white@0.95:t=fill,
      drawbox=x=88:y=755:w=8:h=146:color=0x167A5A:t=fill,
      drawtext=fontfile=${font_bold}:text='OPERATOR DECISION':fontcolor=0x167A5A:fontsize=21:x=122:y=784,
      drawtext=fontfile=${font_regular}:text='${decision}':fontcolor=0x0B1F33:fontsize=27:line_spacing=10:x=122:y=828,
      drawtext=fontfile=${font_regular}:text='Emulated evidence • no physical PLC hardware':fontcolor=0x64748B:fontsize=20:x=92:y=1006[base];
      [base][phone]overlay=x=1370:y=75:format=auto,
      fade=t=in:st=0:d=0.45,fade=t=out:st=17.55:d=0.45[v]" \
    -map "[v]" -map 2:a -c:v libx264 -preset medium -crf 18 -pix_fmt yuv420p \
    -c:a aac -b:a 128k -shortest "$work_dir/${index}.mp4"
}

make_card 00 12 "ATLAS OT SCOUT" "From bounded evidence|to a reviewable OT inventory" "A four-minute product walkthrough for a water and wastewater assessment."
make_scene 01 "01-site-selection-api35.png" "1  •  ESTABLISH CONTEXT" "Start with the operating site" "Open a known site or create a bounded workspace.|The assessment stays attached to a real process area—|not a generic scan." "Choose the site whose network and authorization boundary you can defend."
make_scene 02 "02-new-site-api35.png" "2  •  DEFINE THE SITE" "Record the operational boundary" "Capture the site name, process area and industry.|These fields drive the assessment pack|and its terminology." "Make the scope understandable to operations, security and the final reviewer."
make_scene 03 "02b-new-site-vendors-api35.png" "3  •  ADD CONTEXT" "Use vendor context as a clue" "Select vendors expected from drawings, contracts|or interviews. Expectations never become identified|assets without evidence." "Separate expectations from evidence-supported identity."
make_scene 04 "03-site-dashboard-api35.png" "4  •  ORIENT THE ASSESSOR" "See the next defensible action" "The workspace leads with the unresolved decision,|then shows current evidence and assessment status.|The next action is explicit." "Resolve identity uncertainty before making risk conclusions."
make_scene 05 "04-collection-methods-api35.png" "5  •  CHOOSE EVIDENCE" "Passive collection is the default" "Import PCAP, observe a SPAN/TAP feed, or use a|narrowly authorized identity check. Each method|states what it can establish." "Choose the least intrusive method that can answer the assessment question."
make_scene 06 "06-live-span-result-api35.png" "6  •  REVIEW PASSIVE RESULTS" "Traffic becomes reviewable observations" "Protocol and endpoint observations include provenance|and confidence. Nothing enters inventory until|an assessor reviews it." "Accept supported observations; leave ambiguous identities unresolved."
make_scene 07 "05-active-authorization-api35.png" "7  •  AUTHORIZE ACTIVE WORK" "Constrain every active request" "A work order, exact target, CIDR and signed|authorization gate the broker. The PoC permits|identity requests—not broad scanning." "Confirm scope and time window before the network broker can transmit."
make_scene 08 "06-out-of-scope-blocked-api35.png" "8  •  ENFORCE THE BOUNDARY" "Unsafe scope fails closed" "A target outside the authorized CIDR is blocked|before transmission. The interface explains the stop|instead of silently expanding scope." "Correct the authorization record; never work around the control."
make_scene 09 "09-active-pymodbus-api35.png" "9  •  VALIDATE IDENTITY" "Exercise the bounded test path" "The Android emulator sends one constrained Modbus|identity request to a CI testbed. The result returns|through the isolated broker boundary." "Use active evidence only to close a named identity gap."
make_scene 10 "07-asset-inventory-api35.png" "10  •  REASON ABOUT THE NETWORK" "Inventory is a decision surface" "Filter by zone, protocol, vendor, confidence and|review status. Each asset stays connected to|the evidence that supports it." "Investigate uncertainty and coverage gaps—not just asset counts."
make_scene 11 "10-guided-report-readiness-api35.png" "11  •  CONTROL THE HANDOFF" "Readiness is explicit" "The report stays blocked until required context,|reviews and approvals exist. Incomplete professional|work remains visible." "Assign the independent review and close every required readiness check."
make_card 12 12 "CONTROLLED P0-WATER PILOT" "One segment. One bounded question.|One reviewable handoff." "Next step: run a witnessed assessment with an authorized water-sector partner."

for segment in "$work_dir"/*.mp4; do
  printf "file '%s'\n" "$segment" >> "$work_dir/concat.txt"
done

ffmpeg -hide_banner -loglevel error -y -f concat -safe 0 -i "$work_dir/concat.txt" \
  -c copy -movflags +faststart "$out_dir/atlas-ot-scout-emulator-demo.mp4"

ffprobe -v error -show_entries format=duration,size:stream=codec_name,width,height,r_frame_rate \
  -of default=noprint_wrappers=1 "$out_dir/atlas-ot-scout-emulator-demo.mp4"
