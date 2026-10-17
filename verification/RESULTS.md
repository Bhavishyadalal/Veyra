> Historical report from the uploaded beta1 source archive. These results do not validate the beta2 changes. See [POLISH-RESULTS.md](POLISH-RESULTS.md) for current results.

# Verification — 2026-10-17

Android phone build and bundled Wear OS build succeeded with JDK 17, Gradle 8.11.1, the upstream Android Gradle plugin 8.10.1 and Kotlin 2.3.20 toolchain.

Commands completed successfully:

```text
:app:assembleProdDebug :app:testProdDebugUnitTest
```

- Unit suite: 1,074 tests, 1,073 passed, 1 intentionally skipped live Genius service test, zero failures/errors. Missing signature strings in Simplified and Traditional Chinese were completed after the first test run exposed them.
- Source branding audit: 137 XML documents parsed, 23,078 localized string entries checked, eight widget font references resolved, zero audit errors.
- APK: package `app.veyra.music`, version `0.1.0-beta1`, all 90 application labels read Veyra. Debug signature verified by Android apksigner, ZIP integrity passed.
- All 65 alternative launcher foreground/monochrome PNGs in the APK were compared with the updated source pixels. Option 3 is the default icon. Circle, rounded and squircle mask renders clipped zero significant foreground pixels.
- Native CMake builds succeeded for arm64-v8a, armeabi-v7a and x86_64. The distributed arm64 APK contains the renamed analysis, DSD and DST libraries; 20 JNI exports use the new package namespace.
- Font license, original GPL license and source attribution are bundled in the APK assets as well as the source ZIP.
- Git whitespace check passed. See JSON reports in this folder for details and the final APK SHA-256.

## Limits

The distributed APK is a debug-signed test build for arm64 Android phones, not a store release. A stable production signing key and release build are required before publishing. No physical-phone/emulator UI, playback, FPS, battery or permission-flow testing was performed. TV and web sources are retained and rebranded but were not independently built or runtime-tested here. The bundled watch APK compiled; watch installation was not tested.

Original copyright/license notices and existing real service URLs intentionally remain. Some Discord surfaces obtain their application name/icon from its remotely registered ID; configure your own DISCORD_APPLICATION_ID for independent Discord branding. No Veyra hosting or service registration is claimed. Playback/service settings are retained, and the updater stays disabled until you configure a Veyra feed.
