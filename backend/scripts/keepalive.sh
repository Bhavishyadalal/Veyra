#!/usr/bin/env bash
# Run on an external scheduler, not inside the Render service that may sleep.
set -euo pipefail

script_dir="$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
health_url="${VEYRA_HEALTH_URL:-https://veyra-listen-together.onrender.com/healthz}"
response_file="$(mktemp)"
trap 'rm -f "$response_file"' EXIT

curl --fail --silent --show-error \
  --retry 2 --retry-all-errors --retry-delay 5 --retry-max-time 180 \
  --connect-timeout 15 --max-time 60 \
  "$health_url" --output "$response_file"
python3 "$script_dir/verify_health.py" "$response_file"
