#!/usr/bin/env bash
# Pulls main and rebuilds. The GitHub deploy key is pinned to this script
# (forced command), so a leaked key can do nothing but redeploy main.
set -euo pipefail
SRC=/opt/veyra-src
SOURCE_REPO="${VEYRA_SOURCE_REPO:?set VEYRA_SOURCE_REPO to your own Veyra repository}"
SOURCE_BRANCH="${VEYRA_SOURCE_BRANCH:-main}"
if [ ! -d "$SRC/.git" ]; then
  git clone --depth 1 --branch "$SOURCE_BRANCH" "$SOURCE_REPO" "$SRC"
fi
git -C "$SRC" fetch --depth 1 origin "$SOURCE_BRANCH"
git -C "$SRC" reset --hard FETCH_HEAD
echo "deploying $(git -C "$SRC" log -1 --format='%h %s')"
DOMAIN="$(cat /etc/veyra-domain)" bash "$SRC/backend/deploy/setup.sh"
