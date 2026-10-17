# Beta3 validation — 2026-10-17

## What changed

This update refines the existing phone player and home layout. It adds a clearer credit hierarchy and timestamp contrast, larger menu touch targets, transport press feedback, a credit band that grows with system text size, accessible seek/volume actions, and reduced-motion handling for marquee text. Home uses stable shelf keys, two-line card titles and a larger view toggle. Loading placeholders share one animation clock and cached gradients; optional home requests follow the core response. Cosmetic version updates retain image/audio caches.

Listen Together now supports guests throughout Android creation, joining, invitation previews and server switching. A random installation ID persisted locally derives a pseudonymous guest ID; an optional nickname replaces its generated guest label. Nicknames are capped at the protocol's 80 UTF-8-byte limit without splitting emoji. Signed-in profiles remain available. Account sign-in is not a prerequisite, while membership tokens, party limits, kicks and host controls remain in place.

## Checks that passed

- **10 focused Kotlin/JUnit tests**, including five guest identity tests and the five existing settings-search tests. They compile the real Android-independent production classes using Gradle 8.11.1's bundled Kotlin 2.0.20 compiler and run JUnit 4.13.2. Reproduce with `GRADLE_USER_HOME=<your-gradle-cache> python verification/run_focused_kotlin_tests.py` after bootstrapping `./gradlew --version`.
- **47 Go test cases with the race detector**, including REST creation/joining without account credentials and rejection of missing/invalid party tokens. Ran `go test -race -json ./...` in `backend` using Go 1.27.2. All packages with tests passed; clock/hub packages contain no tests.
- `go vet ./...` passed in `backend`.
- Kotlin PSI parsing: **20 changed/new Kotlin files, zero syntax errors** compared with the uploaded beta1 archive. Syntax parsing does not validate Android/Compose types.
- `python verification/audit_branding.py`: **137 XML files, 23,114 localized entries and eight font references, zero audit errors**. Both new accessibility labels exist in all 18 shared UI resource catalogs.
- `git diff --check` passed.

The backend run initially exposed shared test quota state: the third localhost party-creation test received HTTP 429 from earlier tests. Each test server now initializes its own creation limiter. The complete race-enabled suite passed after this fixture correction; production rate limiting is unchanged.

Machine-readable summary: `beta3-checks.json`.

## Android build blocker

Attempted `:app:compileDevDebugKotlin :app:testDevDebugUnitTest` using the project Gradle wrapper, SDK 36/37.0 and NDK. Configuration reached dependency resolution but the cloud proxy rejected `jitpack.io` with HTTP 403 while fetching NewPipeExtractor v0.26.3. InnerTubeX also depends on JitPack. TLS/checksum verification has not been disabled.

The JitPack domain requirement was previously saved to environment settings; a draft save alone does not activate it. Apply the setting through the environment UI, publish as required, and rerun:

```sh
./gradlew :app:compileDevDebugKotlin :app:testDevDebugUnitTest :app:assembleDevDebug
```

**No full Android compile, complete Android unit suite, lint run, APK assembly, or physical-device/emulator validation has passed for beta3.** Historical beta1/beta2 reports do not validate this update. No APK is bundled.

## Remaining device validation

The early-lag report has not been profiled on the user's phone, and no measured speedup or claim that all lag is fixed is made. The changes remove concrete sources of loading-time work. Compare cold and warm launches in an optimized build; a debuggable build can also spend time on JIT compilation.

1. Compare startup and first-scroll frame timing before/after on the same phone, cache state and network. The existing `devBenchmark` variant uses release optimization under a separate package (`./gradlew :app:assembleDevBenchmark`); the `verifyDevInstall` task can preverify an installed dev build.
2. Check Home loading, recent-history insertion, pull-to-refresh, pagination, layout toggle, long translated titles and service cards.
3. Check player artwork, title/artist links, seek/volume gestures and TalkBack actions, track changes during interaction, loading, queue/lyrics sheets, large text, landscape and reduced motion.
4. With two signed-out installations, create/join a party, test invitation links and nicknames, kill/reopen a process, then check host controls, kicks and reconnection. The local source server passed the guest REST test; the hosted production server and two-phone synchronization were not exercised here.
5. Before release, build and test the production signing/optimization path, plus TV/Wear compatibility if shipping those modules.

## Open the source

Extract the beta3 ZIP and open its `Veyra` root in Android Studio. Read `README.md` for JDK/SDK requirements. Select the `app` module and `devDebug` for development. The downloadable ZIP contains source and required project assets; local SDKs, machine paths, caches, keys, build outputs and older downloadable ZIPs are excluded.
