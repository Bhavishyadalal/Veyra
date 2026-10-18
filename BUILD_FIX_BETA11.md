# beta11 build-tool compatibility fix

This copy preserves the beta11 application source, UI, resources, package ID and version. Only build-tool compatibility is changed.

## Changes

- `build.gradle.kts`: D8/R8 override updated from 8.13.23 to 9.1.56. Transitive dependencies contain Kotlin 2.4 metadata; the old optimizer could not read it reliably.
- `gradle.properties`: independently select lint 9.4.1 (Maven lint 32.4.1), which includes a Kotlin 2.4-capable reader. Lint remains enabled.
- Gradle 8.11.1, Android Gradle plugin 8.10.1, Kotlin plugin 2.3.20 and JDK 17 are retained.

The previous APK build succeeded, but emitted Kotlin metadata diagnostics. This is a tooling correction, not a UI or playback change.

## Open/build

Extract the ZIP, then open its `Veyra` folder in Android Studio, or select the ZIP in your Veyra Builder. Continue using JDK 17 and your existing Android SDK and signing key. The first sync downloads the updated R8 and lint artifacts; existing SDK packages do not need reinstalling. See START_HERE.md for SDK versions and build variants.

Do not replace your signing key. No private signing key, machine-specific SDK path, Gradle cache or generated APK is included.

## References

- https://developer.android.com/build/kotlin-support
- https://developer.android.com/develop/ui/compose/tooling/lint

## Verification (18 October 2026)

- ARM64 `:app:assembleProdRelease`: passed, including R8 optimization and release vital lint.
- `:app:testProdReleaseUnitTest`: 1,115 passed, one skipped, zero failures/errors.
- Generated APK signature and ZIP integrity: verified.
- Earlier R8 Kotlin-metadata parsing and lint incompatible-Kotlin diagnostics: absent.
- Application source and resource bytes: unchanged from the uploaded beta11 source.
- Ordinary pre-existing deprecation warnings and tool-option warnings remain. No checks were disabled.

Build verification does not replace testing the app on a phone.
