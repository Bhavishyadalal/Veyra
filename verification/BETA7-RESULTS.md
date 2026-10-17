# Beta7 phone-only project and polish — 2026-10-18

## Project and dependency changes

`app` is the only installable application. `shared` and `sharedUi` are required internal phone libraries. The TV and Wear OS module directories, Gradle includes, TV workflow and watch release-artifact step were removed.

The phone no longer builds/embeds a watch APK or imports a watch-protocol source directory. Removed wearable/remote-interaction/ADB installation dependencies, watch command service/capability resources, watch pairing and Settings screens, watch playback-output integration and unused watch-screen labels. Cast, Bluetooth, local/streaming audio, guest parties and beta6 queue/loading improvements remain.

NewPipe stripping is now a cacheable typed task with declared inputs/output. It opens the original archive only during execution, avoiding the earlier `zipTree` provider's resolution while building the task graph. It removes only `Utils.class` and nested `Utils$*.class`, retaining the project's existing duplicate-class fix. Task-output caching is enabled. This reduces unnecessary configuration/build work; it does not eliminate the phone's first-sync downloads or its required native audio toolchain.

Home's sign-in invitation is a compact full-width row. The player deck has a subtler dark gradient without the outline. Bold play/pause glyphs, flat Settings and accessibility-sized touch targets remain.

## Checks passed

- **Gradle project listing:** `./gradlew projects` succeeded and lists exactly `:app`, `:shared`, `:sharedUi`; no TV/watch modules. This verifies Gradle configuration, not a complete Android Studio dependency sync.
- **Lazy task discovery:** `:app:stripNewPipeExtractorUtils --dry-run` succeeded without resolving the remote archive. The earlier task-graph path failed on the blocked JitPack request.
- **Production strip-task fixture:** `python verification/verify_newpipe_strip.py` passed using the real production task class, an isolated input and output outside the app build directory. It removed the duplicate and nested Utils classes while preserving the four other entries' bytes, including a similarly named `Utils2.class`, manifest, binary payload and JSON resource. No fixture is substituted into app dependencies.
- **23 focused Kotlin/JUnit tests passed**, zero failures: five queue URL warm-up, five guest identity, five Settings search, five navigation geometry and three Canvas policy tests. Run `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py` after wrapper bootstrap.
- **35 changed/new Kotlin files parsed, zero syntax errors**, relative to beta1. This is parsing, not Android/Compose type checking.
- Resource audit: **103 XML documents, 22,548 localized entries, eight font references, zero errors**. The audit now targets the remaining phone modules.
- Removed-module/source reference check passed. None of the 33 retired base-catalog watch string keys remains referenced in app Kotlin/XML. Duplicate-import and `git diff --check` checks passed.

## Android build and environment limits

The newly lazy build graph exposed a missing compiler in the cloud's Java 21 runtime. Installed the official Temurin JDK 17.0.17+10 archive outside the repository after verifying its published SHA-256; `javac 17.0.17` is available. The final build uses the session's existing CA trust store for the proxy, preserving TLS and dependency verification.

Attempted `:app:compileProdDebugKotlin :app:testProdDebugUnitTest :app:assembleProdDebug`. The remaining blocker is **HTTP 403 from the cloud proxy for jitpack.io** when fetching `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. It now occurs when the strip task executes, rather than during task-graph calculation. Enable the required JitPack access in cloud environment settings to continue the build. Dependencies were not removed/substituted to make it pass.

**No successful complete Android compilation/unit suite, beta7 APK, lint run, emulator/phone visual review or measured Android Studio sync/performance gain is claimed.** The imported beta3 Android Studio report is historical. Backend sources are unchanged.

## Open the new ZIP

Extract beta7 into a **new folder** and open its Veyra root in Android Studio, following START_HERE.md. Select `app` and `devDebug` or `prodDebug`. The two shared modules are libraries, not extra applications. TV/watch folders, their build tasks and watch-install options are absent; no bundle-watch flag is needed.

Before release, complete Android compilation and unit tests with repository access. Review Home sign-in and player controls at large text sizes/RTL, Settings navigation after removing watch destinations, Cast connect/disconnect and volume, phone/Bluetooth playback, lock-screen widgets, playlists/shuffle and guest parties. Measure sync and playback on the target machine/phone before asserting speed gains.

VersionCode is 7; versionName is 0.1.0-beta7. Source archives exclude old downloads, caches, SDKs/JDKs, build outputs and private machine configuration.
