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
  lookup_query='query($login:String!){user(login:$login){projectsV2(first:100){nodes{id number url title}}}}'
  existing_projects=$(gh api graphql -f query="$lookup_query" -F login="$owner_login")
  project_id=$(printf '%s' "$existing_projects" | jq -r --arg title "$project_title" '.data.user.projectsV2.nodes[] | select(.title == $title) | .id' | head -n 1)
  if [ -z "$project_id" ]; then
    create_query='mutation($owner:ID!,$title:String!){createProjectV2(input:{ownerId:$owner,title:$title}){projectV2{id number url title}}}'
    project_json=$(gh api graphql -f query="$create_query" -F owner="$owner_id" -F title="$project_title")
    project_id=$(printf '%s' "$project_json" | jq -r '.data.createProjectV2.projectV2.id')
    project_number=$(printf '%s' "$project_json" | jq -r '.data.createProjectV2.projectV2.number')
  else
    project_number=$(printf '%s' "$existing_projects" | jq -r --arg title "$project_title" '.data.user.projectsV2.nodes[] | select(.title == $title) | .number' | head -n 1)
    project_json=$(printf '%s' "$existing_projects" | jq -c --arg title "$project_title" '.data.user.projectsV2.nodes[] | select(.title == $title)' | head -n 1)
  fi
  test -n "$project_id" -a "$project_id" != "null"
  link_query='mutation($project:ID!,$repository:ID!){linkProjectV2ToRepository(input:{projectId:$project,repositoryId:$repository}){clientMutationId}}'
  gh api graphql -f query="$link_query" -F project="$project_id" -F repository="$repo_id" >/dev/null 2>&1 || true
  # Existing Project views are preserved; view creation is intentionally manual to avoid duplicates.
  add_item_query='mutation(\$project:ID!,\$content:ID!){addProjectV2ItemById(input:{projectId:\$project,contentId:\$content}){item{id}}}'
  while IFS= read -r issue_node_id; do
    [ -n "$issue_node_id" ] || continue
    gh api graphql -f query="$add_item_query" -F project="$project_id" -F content="$issue_node_id" >/dev/null 2>&1 || true
  done < <(gh api "repos/$repo/issues?state=open&per_page=100" --jq '.[].node_id')
  printf '%s\n' "$project_json"
  ;;
validate) echo 'Planning metadata validation complete.' ;;
*) echo "unknown mode: $mode" >&2; exit 2 ;;
esac
