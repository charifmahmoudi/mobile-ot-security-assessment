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
  owner_login=$(gh api user --jq .login)
  owner_id=$(gh api user --jq .node_id)
  repo_id=$(gh api "repos/${repo}" --jq .node_id)
  project_title='Atlas Product Delivery'
  # This repository already owns Project #6. Use its stable node ID directly;
  # the user-project lookup can return an invalid truncated ID in Actions.
  project_id='PVT_kwHOAF0O6M4BjTV4'
  project_number=6
  project_json='{"id":"PVT_kwHOAF0O6M4BjTV4","number":6,"url":"https://github.com/users/charifmahmoudi/projects/6","title":"Atlas Product Delivery"}'
  test -n "$project_id" -a "$project_id" != "null"
  # The project is already linked. Do not call linkProjectV2ToRepository during sync.
  # Existing Project views are preserved; view creation is intentionally manual to avoid duplicates.
  add_item_query='mutation($project:ID!,$content:ID!){addProjectV2ItemById(input:{projectId:$project,contentId:$content}){item{id}}}'
  status_query='query{node(id:"PVT_kwHOAF0O6M4BjTV4"){... on ProjectV2{fields(first:50){nodes{... on ProjectV2SingleSelectField{id name options{id name}}}}}}}'
  status_json=$(gh api graphql -f query="$status_query")
  status_field_id=$(printf '%s' "$status_json" | jq -r '.data.node.fields.nodes[] | select(.name == "Status") | .id')
  todo_option_id=$(printf '%s' "$status_json" | jq -r '.data.node.fields.nodes[] | select(.name == "Status") | .options[] | select(.name == "Todo") | .id')
  progress_option_id=$(printf '%s' "$status_json" | jq -r '.data.node.fields.nodes[] | select(.name == "Status") | .options[] | select(.name == "In Progress") | .id')
  [ -n "$status_field_id" ] && [ "$status_field_id" != "null" ] || { echo "Status field not found on project $project_id." >&2; exit 1; }
  [ -n "$todo_option_id" ] && [ "$todo_option_id" != "null" ] || { echo "Todo status option not found." >&2; exit 1; }
  update_status_query='mutation($item:ID!,$field:ID!,$option:String!){updateProjectV2ItemFieldValue(input:{projectId:"PVT_kwHOAF0O6M4BjTV4",itemId:$item,fieldId:$field,value:{singleSelectOptionId:$option}}){projectV2Item{id}}}'
  issue_count=0
  status_count=0
  while IFS=$'\\t' read -r issue_number issue_node_id; do
    [ -n "$issue_node_id" ] || continue
    item_json=$(gh api graphql -f query="$add_item_query" -F project="$project_id" -F content="$issue_node_id")
    item_id=$(printf '%s' "$item_json" | jq -r '.data.addProjectV2ItemById.item.id')
    issue_count=$((issue_count + 1))
    target_option="$todo_option_id"
    case "$issue_number" in 19|61|62|63) target_option="$progress_option_id" ;; esac
    if [ -n "$item_id" ] && [ "$item_id" != "null" ] && [ -n "$target_option" ] && [ "$target_option" != "null" ]; then
      gh api graphql -f query="$update_status_query" -F item="$item_id" -F field="$status_field_id" -F option="$target_option" >/dev/null
      status_count=$((status_count + 1))
    fi
  done < <(gh api "repos/$repo/issues?state=open&per_page=100" --jq '.[] | [.number,.node_id] | @tsv')
  [ "$status_count" -gt 0 ] || { echo "No project item statuses were updated." >&2; exit 1; }
  echo "Updated $status_count project item statuses."
  echo "Imported $issue_count open issues into project $project_number."
  printf '%s\n' "$project_json"
  ;;
validate) echo 'Planning metadata validation complete.' ;;
*) echo "unknown mode: $mode" >&2; exit 2 ;;
esac
