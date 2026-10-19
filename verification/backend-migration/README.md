# Veyra backend migration · 0.1.5

Official service: **https://veyra-listen-together.onrender.com**. Backend source: [Veyra-backend](https://github.com/Bhavishyadalal/Veyra-backend).

Both Android tracks change the room default, public invite host, manifest and shared presence endpoint. Preview UI refinements are now promoted into stable 0.1.5/code 20; the historical preview branch remains available. [Current promotion checks](../STABLE-0.1.5-PROMOTION.md). Package/signing identity stays unchanged. No APK or GitHub release is produced by this source update.

## Promotion and external device report

On 19 October 2026 the owner reported testing 0.1.5 and successfully generating a Listen Together code, then requested promotion to main. That first promotion used stable 0.1.5/code 19. The owner subsequently requested promotion of the preview UI; main now uses stable 0.1.5/code 20, with preview history preserved. The report is not presented as an independently executed full Android build or complete two-phone synchronization/upgrade test.

The separate backend repository adds a scheduled `/healthz` request every 12 minutes, with retries and Veyra identity checks. [Workflow and run history](https://github.com/Bhavishyadalal/Veyra-backend/actions/workflows/render-keepalive.yml). Workflow syntax/response validation was checked locally; scheduled execution and uninterrupted uptime are not independently established. A phone-side repeating job would stop helping when the app closes and would waste battery, so keep-alive belongs outside Android. Render restarts still clear rooms.

## External cron support for both tracks

The public `/healthz` endpoint already supports external HTTP cron with no authentication. [Exact 12-minute setup](../../docs/RENDER_KEEPALIVE.md) is included in both source ZIPs. The backend's GitHub schedule and external-machine cron use the same tested shell helper. This is backend infrastructure shared by stable and preview, not a phone-side timer. The owner's external cron provider still needs configuration and an actual successful run; scheduling/continuous uptime is not claimed as verified.

## Upgrade behavior

- A saved address for the retired managed services becomes the new official default before health probes run.
- A one-time preference marker discards old default room tokens/codes and obsolete kick records without making a leave request to either retired services or an unrelated new room. Deliberate custom-server settings survive.
- Old explicit service addresses are rejected in custom-server validation, room lookup, stale-slot cleanup and switching. Old invites require a new Veyra room; their six-character code is never automatically rebound to the new server.
- Official web invites pin the official backend even if a different custom server is saved. Explicit malformed server parameters are refused rather than silently dropped.
- Presence uses the official Veyra service independently of the user's custom room server. Optional Google profiles and anonymous guests retain their existing client flow.
- The bundled web-player party helpers use the same service and discard old saved server/session destinations. The separate marketing website is not changed or redeployed.

## Checks and limits

Executed in this environment on 19 October 2026:

- Migration/parser harness: **14 JUnit tests passed** against real production Kotlin policy/invite sources. Seven policy tests overlap the focused suite. The harness supplies compile-time Android Intent and CODE_LENGTH adapters; it does not test activity dispatch or sockets.
- Web helpers: **12 checks passed** against compiled production TypeScript. This is not a full web-player build or browser/extension run.
- Production debug and release BuildConfig generation passes with the official Veyra URL and each track's version/code.
- Android manifest XML parses; Git whitespace checks pass.
- Stable candidate: the existing focused suite plus seven migration policy regressions passes **64 tests**.
- Preview-only: the existing focused suite plus seven migration policy regressions passes **68 tests**. The HomeFeedIdentity test now lives in sharedUi's jvmTest source set, where it can legally access the internal sharedUi function. This does not establish a full Gradle unit-suite pass.

**Android compilation is blocked before Kotlin compilation** by HTTP 403 downloading NewPipeExtractor v0.26.3 from JitPack. No full Android type check, APK, physical-phone playback, upgrade test or verified HTTPS App Links is claimed. Stable source 0.1.5 is promoted to main at the owner’s explicit request after their successful device test and room-code generation report. Original 0.1.3 archives remain available. No new stable binary was built or published in this environment.

The owner reports the deployed Render health route returns true. This environment cannot independently connect to that host yet (proxy CONNECT HTTP 403). The previous backend's 54 race-enabled tests and 15 two-WebSocket checks passed locally before deployment; they do not establish that the Render TLS/proxy/deployed instance passed. The network configuration draft adds only the exact Veyra Render host and jitpack.io while preserving existing destinations. It needs review/save/publish in environment settings before affected checks can be retried.

After access is enabled, run the real deployment probe from the separate backend repository:

```sh
go run ./cmd/veyra-smoke -server https://veyra-listen-together.onrender.com
```

Then compile both app tracks and test create/join, queue/play/pause/seek, host lock, reconnect, background/resume, leave/kick and upgrades on two phones on separate networks. Existing rooms cannot survive movement between independent servers. Render's free instance can sleep; restarts lose in-memory rooms.

HTTPS auto-opening additionally needs the **public signing-certificate SHA-256 fingerprint** in Render's ANDROID_APP_SHA256_CERT_FINGERPRINTS setting and Android domain verification. A Veyra custom-scheme invite remains available without that association; do not upload a private signing key.

## Reproduce local parser checks

With JDK 17, a cached Gradle 8.11.1 compiler and GRADLE_USER_HOME set:

```sh
python3 verification/backend-migration/run.py
# TypeScript compiler on PATH, or point TSC_BIN at its executable:
node verification/backend-migration/web-check.mjs
```

The web extension's legacy deployment-origin configuration is a separate, undeployed browser product, not an Android network destination or the party backend; it is not redirected to the marketing website, which does not host that web player. Legal GPL/copyright attribution is retained. Retired domains exist only in migration rejection logic and regression fixtures, not active Android connection defaults.
