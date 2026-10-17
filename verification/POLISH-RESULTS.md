# Veyra beta2 source validation — 2026-10-17

## Passed in this environment

- Five `SettingsSearchTest` JUnit tests: category gating, global search, multi-word/case-insensitive matching, no-match handling and result tally behavior. Compiled the real production search model and test class with Gradle 8.11.1's bundled Kotlin 2.0.20 compiler and ran JUnit 4.13.2 independently of the blocked Android dependency graph.
- Kotlin PSI syntax parsing of all nine changed/new Kotlin files: zero syntax errors. This is not a full Compose or Android type check.
- `python verification/audit_branding.py`: 137 XML files parsed, 23,078 localized entries checked, eight font resource references resolved, zero audit errors.
- All 47 original settings setter references remain after the restructure.
- No new translation keys were introduced; categories reuse the existing localized labels. Two inherited Chinese regional fallback catalogs still lack 62 base keys each; these were not introduced by this update.
- Active Android/shared/TV/Wear source scan found no UPI payment action or promotional Telegram, Instagram, Twitter or Discord invite links. Discord's service gateway is an integration endpoint, not a promotional link. Removed the inherited repository Ko-fi funding entry.
- Staged source scan found no private-key headers, GitHub token patterns or AWS access-key patterns. Local SDKs, build caches, signing files and machine configuration are excluded from the source package.

## Build attempts and limitation

Gradle 8.11.1 runs. Installed Android SDK platforms 36 and 37.0, build-tools 35/36 and NDK 27.0.12077973 as required by configuration. The supplied AGP 8.10.1 build initially requested the obsolete platform hash `android-37`; app and TV now default to `android-37.0`, with an optional `-PveyraCompileSdk=android-37` override for older installations. Configuration then progressed to dependency resolution.

Attempted:

```sh
./gradlew :app:compileDevDebugKotlin :app:testDevDebugUnitTest
./gradlew :sharedUi:compileDebugKotlinAndroid
```

Both are blocked by the cloud network proxy returning HTTP 403 for `jitpack.io`. Missing dependencies include NewPipeExtractor v0.26.3 and InnerTubeX v0.7.0. The JitPack domain requirement was saved in the environment configuration draft; it is not active merely because the draft is saved. Review/save the setting and publish the environment, then rerun the commands. TLS and artifact verification were retained throughout.

The full Android unit suite, APK assembly, lint and Compose type checking have NOT passed for this revision. No APK is supplied. The earlier beta1 report is historical and must not be treated as evidence for beta2.

## Device checks still required

No emulator or physical phone UI/playback/FPS/battery validation was performed. Before release, check:

- Settings overview, all six categories, global search, clearing search, Back, rotation and process recreation.
- Dark/light themes, large font sizes, TalkBack, narrow screens and RTL languages.
- Home, Explore, Library, Search, album/playlist detail, now playing, queue and lyrics with the shared theme and transitions.
- Reduced animation: no navigation slide or moving playing bars.
- Playback, downloads and service sign-ins. Existing integration hosts and legal attribution are preserved.

Performance changes remove identifiable repeated work; there is no measured speedup or battery-life claim.

## Android Studio

Extract/open the repository root containing `settings.gradle.kts`. Use JDK 17, SDK 36 and 37.0, NDK 27.0.12077973, CMake 3.22.1, and network access to Google/Maven/Gradle/JitPack repositories. Select `app` / `devDebug`. Run `:app:testDevDebugUnitTest :app:assembleDevDebug` after syncing. A phone-only iteration can use `-Pveyra.bundleWear=false`.
