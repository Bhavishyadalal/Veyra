# Branch and archive maintenance verification

## Scope

All six app branches receive the same 41-file backend snapshot from standalone
`Veyra-backend` main commit `6fdbd5082d79f179c49fb75c59c55c7695c7fd0a`:
`main`, `preview/0.1.4-beta1`, `preview/0.1.5-beta1`, `preview/0.1.6-beta1`,
`stable/0.1.5` and `backend/0.1.5`.

The two older branches (`preview/0.1.4-beta1` and `backend/0.1.5`) also receive the
existing Veyra Android/client migration: default, presence, invites, saved-server
migration and retired-token guards. Their Android version declarations and UI
features are preserved. On the other four branches Android/shared/sharedUi files
are unchanged from their previous heads. No new APK/version/release is created.

## Checks

- Backend source bytes and executable modes match the canonical repository on all
  six branches. Each nested manifest verifies 40 source files plus the matching
  manifest itself.
- All six branches pass the Android production endpoint/retirement-guard audit.
  The old host strings are kept only in the rejection policy and regression tests.
- **14 migration/invite JUnit tests pass per branch** (84 executions). These compile
  real policy/invite sources with narrow Intent and CODE_LENGTH adapters, not the
  complete Android app or its network stack.
- **54 Go race test cases pass**, module verification/vet/build pass on the shared
  backend source. **15 two-client WebSocket checks pass** against a local server
  built from this source. The external cron helper passes against that server,
  and the server exits normally after SIGTERM.
- **12 web endpoint/invite checks pass** on the common helper sources.
- **16 extension configuration checks pass**: local development, explicit own
  production origins, missing configuration, retired hosts, localhost store builds,
  credentials, paths, queries and non-local plain HTTP.
- The web sample app download link points to Veyra releases. The extension no
  longer defaults to upstream sites; production packaging requires an explicitly
  configured Veyra web-player origin. The marketing website is not that player.
- Four refreshed source archives are generated from their own version branches.
  Each has CRC, complete file manifest/hash validation, matching SHA-256 sidecar,
  current backend bytes/manifest, Gradle wrapper and Android Studio project files.
  Identical archive copies are distributed across branch tips.

## Retention and limits

Only stable 0.1.5 and previews 0.1.4-beta1, 0.1.5-beta1 and 0.1.6-beta1 remain as
ZIPs. Old stable/candidate, website, media-kit and standalone-server ZIPs and their
checksums are removed from branch tips. Source and licensing are retained.

Git history, tags and release assets are not rewritten/deleted. Old ZIP blobs
remain in Git history, so this is checkout cleanup, not a claim that historical
GitHub repository storage has been reclaimed. Future large distributions should
prefer release assets over repeatedly committing ZIPs.

Full Android compilation remains blocked by the existing JitPack HTTP 403. No new
Android device run, full browser extension build or live Render deployment/uptime
verification is claimed. The standalone backend repository retains deployment
ownership and its existing 12-minute best-effort scheduler; no duplicate schedule
is added to the app branches.

## Repeatable checks

```sh
python3 scripts/verify_backend_mirror.py
python3 scripts/verify_app_backend.py
python3 scripts/verify_archives.py
GRADLE_USER_HOME=/path/to/gradle-cache python3 verification/backend-migration/run.py
cd backend
go mod verify
go vet ./...
go test -race -count=1 ./...
```

With Node.js and TypeScript available, the web helper check accepts `TSC_BIN`
(the compiler executable) and the extension check accepts `TYPESCRIPT_PATH`
(the TypeScript JS compiler API module). See the scripts in
`verification/backend-migration/`. These are targeted checks, not complete app builds.
