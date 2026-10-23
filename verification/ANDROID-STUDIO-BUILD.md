> Current stable source: **0.1.9 / code 36**, `main` and `stable/0.1.9`. [Release checks](STABLE-0.1.9-PROMOTION.md).

> Historical beta3 build result supplied in the uploaded archive. See [BETA4-RESULTS.md](BETA4-RESULTS.md) for current revision status.

# Beta3 Android build verification — 2026-10-17

The beta3 phone APK and bundled Wear OS app built successfully using JDK 17 and the supplied Gradle 8.11.1 wrapper.

```sh
./gradlew :app:assembleProdDebug :app:testProdDebugUnitTest --no-daemon --max-workers=2
```

- BUILD SUCCESSFUL. 1084 phone unit tests: 1083 passed, 1 skipped, zero failures or errors (97 suites).
- APK package app.veyra.music, versionCode 3, versionName 0.1.0-beta3, label Veyra. APK signature verifies and matches the previously supplied beta2 APK.
- Native audio libraries built for arm64-v8a, armeabi-v7a and x86_64; watch APK bundled. Downloadable phone APK targets arm64-v8a.
- Resource audit: 137 XML documents, 23,114 localized entries, eight font references; zero errors.
- Every application source/resource/protocol file from the uploaded beta3 is byte-for-byte unchanged. Beta3's features and translations are retained.
- Android modules are declared before SDK discovery, avoiding missing app modules during first import. NDK version is pinned to 27.0.12077973 for app and TV.
- Complete Gradle 8.11.1 wrapper scripts/JAR are included, with distribution SHA-256 verification. Windows script uses CRLF; Unix script is executable.
- Build environment: SDK platforms 36 and 37.0, Build-Tools 35.0.0, NDK 27.0.12077973, CMake 3.22.1. Existing AGP/Kotlin/dependency versions retained.

Read START_HERE.md for Windows Android Studio import and build steps. Extract and open the Veyra project root, select JDK 17, then sync.

This is a test/debug APK. Windows Android Studio's GUI, physical-phone playback, frame rate, TV runtime and production release optimization were not tested. The existing backend reports are retained; backend tests were not rerun during this build-only update. Older validation reports are historical and do not supersede this Android build result.

No local SDK paths, proxies, private signing keys, build caches or APKs are bundled in the source ZIP. Existing server configuration is preserved.
