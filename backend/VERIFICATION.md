# Backend 0.1.5 verification

19 October 2026. Prepared on `backend/0.1.5`, separately from the stable and preview app release branches.

## Executed locally

- Go **1.27.2**: dependency checksums verified with `go mod verify`; `go vet ./...` passes.
- **54 Go tests pass**, zero failures/skips, with `go test -race -count=1 ./...`. The race detector is enabled. Existing room limits, guest membership, queue ordering/deltas, host permissions, origin checks, token transport, invite behavior, clock/state data and presence cases remain; new cases exercise deployment identity, shared route coverage, public-preview data boundaries, optional App Links, reconnect cleanup, leave revocation and configured default capacity.
- Production server builds with `go build -trimpath`. A separately running executable passes **15 black-box protocol checks with two real WebSocket clients**: Veyra identity, clock, guest creation/join, invalid-token rejection, public preview, authenticated welcomes, ping/pong, queue broadcast, track/state sync, queue move, host-only rejection, host seek/sequence agreement, queue resync, presence open/close and clean leave.
- Local server processes exit **0** after SIGTERM; no background test server is left running. HTTP shutdown explicitly closes WebSockets.
- The standalone ZIP is extracted outside a Git checkout and builds with the same Render build command; its independently built server and smoke tool also pass all 15 protocol checks. ZIP CRCs and source-manifest hashes are verified.
- Render YAML parses; standalone root paths, build/start commands, native Go runtime, health route and one-instance configuration are present. Legacy shell helpers parse with `bash -n`, but are not executed or included in the standalone Render ZIP; legacy Python probes are omitted too.
- **1,114 app/shared/UI/web/build/existing-source-archive files** byte-match the starting preview `b070e59`. Current app versions, endpoints, stable source and all existing Android ZIPs are unchanged. The stable main branch is not merged or rewritten.
- The business implementations in protocol, party, presence, codes and clock match the reviewed upstream commit after normalizing only Go module import paths. See UPSTREAM.md and the machine-readable verification-results.json.

## Corrections made during review

1. Shared production/test routing includes `/api/parties/{code}/preview`; it was missing from the previous test-only router, leaving the real client route uncovered.
2. Socket replacement is registered atomically with connection state. Old cleanup cannot mark the replacement disconnected. A regression test waits for the original handler to finish before checking the surviving member and ping response.
3. Replaced/revoked sockets cannot submit buffered controls. Membership is revalidated after WebSocket upgrade, and leaving sends the existing `bye: left` reason then closes the live socket; its token no longer authorizes snapshots.
4. Room creation uses `JAM_MAX_MEMBERS` when no explicit capacity is supplied, rather than ignoring it in favor of a hardcoded five.
5. Service identity/version and protocol are visible in health; private JSON defaults to `Cache-Control: no-store`. Optional public certificate associations explicitly set their own short cache lifetime.
6. Go module ownership, server docs, tooling defaults, browser origin and deployment configuration are Veyra-specific. The old probe's automatic remote-server target is replaced by localhost.

## Limits and next checks

The Render service has **not** been created or deployed here. Native Render runtime/toolchain availability, cold-start behavior, assigned domain/TLS, proxy headers and production WebSocket persistence must be verified after deployment. The packaged `go run ./cmd/veyra-smoke -server https://ACTUAL-HOST` performs the protocol checks against that real service.

No Android app was rebuilt, relinked, signed or released in this stage. Actual two-phone playback timing, cross-network reconnection, background audio and device App Links remain unverified. Existing Android test-module visibility and dependency-access limitations are not solved by a Go server package. Do not describe the app as migrated until both endpoint changes and device verification are complete.

The server remains an in-memory single-instance service. Restarts lose rooms and counts. Google/display-name assertions are not verified account identities; server-issued tokens protect membership. The provided TURN/STUN credentials are not used because the current client protocol contains no WebRTC audio/P2P exchange. Required upstream legal attribution remains.
