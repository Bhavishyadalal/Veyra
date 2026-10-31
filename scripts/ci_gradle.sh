#!/usr/bin/env bash
# Retry dependency transport/resolution failures once, never hide build failures.
set -uo pipefail
log_file="$(mktemp "${RUNNER_TEMP:-${TMPDIR:-/tmp}}/veyra-gradle.XXXXXX")"
trap 'rm -f "$log_file"' EXIT
./gradlew --console=plain --stacktrace "$@" 2>&1 | tee "$log_file"
result=${PIPESTATUS[0]}
if [[ "$result" -eq 0 ]]; then exit 0; fi
if grep -Eq 'Could not resolve all (artifacts|files)|Could not (GET|HEAD)|Read timed out|Connection reset|Received status code (429|50[234])' "$log_file"; then
    echo "Dependency resolution failed; retrying once with refreshed repository metadata."
    ./gradlew --console=plain --stacktrace --refresh-dependencies "$@"
    exit $?
fi
exit "$result"
