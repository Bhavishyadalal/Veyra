# Beta6 Home and playback refinement — 2026-10-18

## Changes

Home opens with a smaller, unboxed heading, brand mark and accent rule. Existing localized captions, shelves, playback indicators, long-press actions and signed-in/guest controls remain.

Home reloads cancel the previous load/page job. Generation and listener identity checks reject obsolete first-page and pagination results. Optional history is bounded to six seconds and each discovery supplement to eight; the core feed remains independent and supplements remain sequential. An empty initial feed settles with a retryable message rather than an endless skeleton. Explore fetches on its first tab visit, reducing the startup request burst.

Refresh retains visible content, cancels initial/page work, temporarily disables old continuations, and restores the prior continuation on failure. Pagination failures show a Retry action and block automatic retrial until the user retries or refreshes. Loading and refresh flags have finally cleanup.

Queue startup builds a track's media item once for both playback and canonical shuffle restoration, avoiding repeated URI construction and download-file checks. The existing off-main-thread conversion, queue tiers, selected starting index, stable entry IDs and shuffle restoration remain.

Next-track URL warming starts after 1.5 seconds independently of optional substitute lookup. Later URLs wait an additional 6.5 seconds after the preceding lookup; bulk byte fetching keeps its existing eight-second grace period after substitute selection. Warm-up skips already pinned sources, de-duplicates IDs, continues after ordinary per-track errors and propagates cancellation. It does not write audio bytes or change stream quality, source selection, URL probing or cache keys. Cached warm URLs still pass through the existing resolver's validity and failure handling. Guest Listen Together and beta5 rendering changes remain.

## Passed here

- **23 focused Kotlin/JUnit tests**, zero failures. The five new coroutine tests check early-next/later ordering, duplicate/pinned IDs, per-track failure recovery, cancellation propagation, and cancellation of a pending warm-up before any request. Existing guest identity (5), Settings search (5), navigation geometry (5) and Canvas rendering policy (3) tests also passed.
- Reproduce using `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py` after wrapper bootstrap. It compiles the actual production helpers with the Gradle distribution's compiler; it does not compile the Android UI/service graph.
- **33 changed/new Kotlin files parsed, zero syntax errors**, relative to original beta1. Parsing is not Android/Compose type checking.
- Resource audit: **137 XML documents, 23,151 localized entries, eight font references, zero errors**.
- `git diff --check` and duplicate-import inspection passed.

## Android build blocker

Attempted `:app:compileProdDebugKotlin :app:testProdDebugUnitTest :app:assembleProdDebug` with the project wrapper, installed SDK and two workers. The proxy again returned **HTTP 403 for jitpack.io** resolving `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`, before Kotlin compilation. Required JitPack access must be enabled in cloud environment settings before retrying. No dependency, checksum or TLS verification was bypassed.

**No successful beta6 Android type check, complete Android unit suite, APK, lint run, emulator/phone visual review or measured playback/frame-rate gain is claimed.** The existing beta3 Android Studio report is historical. Backend sources are unchanged.

## Required device checks

Build using START_HERE.md with repository access. Check cold startup, first visit to Explore, Home on slow/offline networks, repeated reload/refresh, changing accounts while requests run, pagination failure/retry, large fonts/RTL and long captions. Check first-song start, quick skips, pause/resume, downloaded songs, large playlists with shuffle on/off, context restoration, addons and guest parties. Compare tap-to-audio and frame timing using the same phone, network and build variant before asserting speed improvements.

VersionCode is 6 and versionName is 0.1.0-beta6. The ZIP is the complete Android Studio source project, with downloads, caches, SDKs, build outputs and private local configuration excluded.
