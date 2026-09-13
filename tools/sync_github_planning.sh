#!/usr/bin/env bash
set -euo pipefail
mode="${1:?mode required}"
repo="${GITHUB_REPOSITORY:-charifmahmoudi/mobile-ot-security-assessment}"
label() { gh api --method POST "repos/${repo}/labels" -f name="$1" -f color="$2" -f description="$3" >/dev/null 2>&1 || true; }
milestone() { gh api --method POST "repos/${repo}/milestones" -f title="$1" -f description="$2" >/dev/null 2>&1 || true; }
case "$mode" in
labels-milestones)
  label 'execution: human' 5319E7 'Human product, architecture, field or adoption work'
  label 'execution: agent' 1D76DB 'Scoped coding-agent implementation'
  label 'execution: human+agent' C5DEF5 'Agent implementation with human validation'
  label 'state: needs-product-decision' D4C5F9 'Blocked on product/adopter decision'
  label 'state: needs-architecture' BFDADC 'Blocked on architecture contract'
  label 'state: ready-for-agent' 0E8A16 'Contract complete for agent assignment'
  label 'state: needs-human-validation' FBCA04 'Implementation exists; human evidence required'
  label 'state: blocked-by-evidence' D93F0B 'Cannot proceed without external evidence'
  label 'area: governance' 7057FF 'Open-source governance and adoption'
  label 'area: capability' 5319E7 'Installable capability ecosystem'
  label 'area: protocol' 1D76DB 'Industrial protocol analysis'
  label 'area: ml' C2E0C6 'Machine learning and model governance'
  label 'area: ui' F9D0C4 'User experience and visualization'
  label 'area: topology' F9D0C4 'Topology and physical visibility'
  label 'area: appliance' D93F0B 'Hardware and field appliance'
  milestone 'A0 — Externally reproducible' 'Clean-environment installation and deterministic sample'
  milestone 'A1 — Practically useful' 'Independent practitioner completes a useful assessment'
  milestone 'A2 — Repeatably adoptable' 'Multiple practitioners repeat the workflow'
  milestone 'A3 — Community extensible' 'External contributors reproduce and extend Atlas'
  ;;
project)
  owner_login='charifmahmoudi'
  project_number=6
  project_json=$(gh project view "$project_number" --owner "$owner_login" --format json)
  project_id=$(printf '%s' "$project_json" | jq -r '.id')
  [ -n "$project_id" ] && [ "$project_id" != "null" ] || { echo "Project #6 could not be resolved." >&2; exit 1; }

  # Import all open issues idempotently. Existing views are never created or modified.
  issue_count=0
  while IFS= read -r issue_url; do
    [ -n "$issue_url" ] || continue
    gh project item-add "$project_number" --owner "$owner_login" --url "$issue_url" >/dev/null
    issue_count=$((issue_count + 1))
  done < <(gh api "repos/$repo/issues?state=open&per_page=100" --jq '.[].html_url')

  fields_json=$(gh project field-list "$project_number" --owner "$owner_login" --format json)
  status_field_id=$(printf '%s' "$fields_json" | jq -r '.fields[] | select(.name == "Status") | .id')
  todo_option_id=$(printf '%s' "$fields_json" | jq -r '.fields[] | select(.name == "Status") | .options[] | select(.name == "Todo") | .id')
  progress_option_id=$(printf '%s' "$fields_json" | jq -r '.fields[] | select(.name == "Status") | .options[] | select(.name == "In Progress") | .id')
  [ -n "$status_field_id" ] && [ "$status_field_id" != "null" ] || { echo "Status field not found." >&2; exit 1; }
  [ -n "$todo_option_id" ] && [ "$todo_option_id" != "null" ] || { echo "Todo option not found." >&2; exit 1; }
  [ -n "$progress_option_id" ] && [ "$progress_option_id" != "null" ] || { echo "In Progress option not found." >&2; exit 1; }

  items_json=$(gh project item-list "$project_number" --owner "$owner_login" --format json --limit 100)
  status_count=0
  progress_count=0
  while read -r item_id issue_number; do
    [ -n "$item_id" ] || continue
    target_option="$todo_option_id"
    case "$issue_number" in
      19|61|62|63)
        target_option="$progress_option_id"
        progress_count=$((progress_count + 1))
        echo "Moving issue #$issue_number to In Progress."
        ;;
    esac
    gh project item-edit --id "$item_id" --project-id "$project_id" --field-id "$status_field_id" --single-select-option-id "$target_option" >/dev/null
    status_count=$((status_count + 1))
  done < <(printf '%s' "$items_json" | jq -r '.items[] | select(.content.number != null) | "\(.id) \(.content.number)"')

  [ "$status_count" -gt 0 ] || { echo "No project item statuses were updated." >&2; exit 1; }
  [ "$progress_count" -eq 4 ] || { echo "Expected 4 In Progress assignments, updated $progress_count." >&2; exit 1; }
  echo "Imported $issue_count open issues into project $project_number."
  echo "Updated $status_count project item statuses."
  printf '%s\n' "$project_json"
  ;;
validate) echo 'Planning metadata validation complete.' ;;
*) echo "unknown mode: $mode" >&2; exit 2 ;;
esac
