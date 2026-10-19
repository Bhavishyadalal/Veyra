# 0.1.4-beta1 — missing Locale import correction

19 October 2026. Preview branch only; version remains **0.1.4-beta1**, code **17**, base **0.1.4**, beta suffix **beta1**, package **app.veyra.music**. Stable 0.1.3, dependencies, signing configuration and application behavior are unchanged.

## Exact production-source change

Add `import java.util.Locale` to `sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens/HomeScreen.kt`. The existing subtitle localization expression `trimmed.lowercase(Locale.ROOT)` is unchanged. The import was mistakenly removed during the previous cleanup; syntax parsing alone did not detect the unresolved name.

The owner's supplied `Veyra_0.1.4-beta1_Fix_Handoff.md` identifies the starting ZIP by SHA-256:

```
7e4bafed407830ea0cc4e67561642e632b6db013ca835baee4ff2c96af42a6a2
```

That SHA matches the preview ZIP before this correction. The corrected HomeScreen is verified byte-for-byte against its archived predecessor after removing only the added import and its blank line. No other application source, resource, dependency, package, version, test or signing file changes in this correction. The updated source ZIP includes this note and updated documentation; its checksum is in the adjacent `.sha256` file.

## Validation in this environment

- Exactly one Locale import; unchanged `lowercase(Locale.ROOT)` expression and remaining HomeScreen bytes. Corrected file parses without Kotlin syntax errors. All other application source, resource, build, test and signing files byte-match preview commit `53e951c9b356a085bdae12f246d199384a1c0dca`.
- `:app:compileProdDebugKotlin` was attempted with JDK 17, the installed Android SDK, the environment HTTPS proxy and system CA trust. It remains blocked **before app Kotlin compilation** at `:app:stripNewPipeExtractorUtils`: JitPack returns HTTP 403 for `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. No TLS bypass or replacement dependency was used. No APK/assemble/sign/release task was run here.
- NDK 27.0.12077973 already has `source.properties` at its correct root with the expected revision. No NDK repair or project toolchain change was necessary.
- The separate test-visibility issue is confirmed with an isolated two-module compilation of the actual `HomeFeedIdentity.kt` and `HomeFeedIdentityTest.kt`: the app test cannot access internal `sharedUi.homeShelfKeys`. Tests do not execute in that check. **This issue remains unchanged**, as requested for the exact handoff fix. No visibility broadening, friend-path override, deleted test or suppressed assertion.
- The earlier 61-test focused runner compiles its selected sources together as one module; its pass does not establish cross-module Gradle test compilation or a passing complete app suite.

## Build outcome reported by the supplied handoff

The handoff reports successful production release assembly and vital lint, package/version and APK signature/archive verification in another environment, following this same import fix. Its delivered APK hash is `d0f3e8fdcfe99e2c319fe02c36dc57a3f0bff434e18656656edac24aa83ffbd1`. Those APK checks were **not independently reproduced here** and no APK is included or uploaded by this correction. The handoff explicitly reports that unit-test compilation remained blocked and physical-device testing was not performed.

On a normally configured machine, production compilation can be retried with `./gradlew :app:compileProdDebugKotlin --max-workers=2` (Windows: `gradlew.bat`). Keep the owner's existing signing identity for any later release build. The full unit suite needs the separately documented module/test visibility repair before it can run successfully. Do not mark this preview stable on the basis of the import correction alone.
