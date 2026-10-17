# Veyra

A personal music player for Android, with the selected purple, peach and cream V icon, Manrope typography and clearer dark-theme contrast.

## Open and build

1. Extract the entire archive, then open this folder in Android Studio (not just `app`).
2. Install JDK 17, Android SDK platforms 36 and 37.0, NDK 27.0.12077973 and CMake 3.22.1 through Android Studio. Accept SDK licenses. Android 8.0 / API 26 or later is required on the phone.
3. Let Gradle sync complete. The first sync needs internet access for dependencies.
4. Select the `app` module and `devDebug` variant, then Run on a phone or emulator.

Windows terminal alternative:

```powershell
.\gradlew.bat :app:assembleDevDebug :app:testDevDebugUnitTest
```

macOS/Linux:

```sh
./gradlew :app:assembleDevDebug :app:testDevDebugUnitTest
```

For a build labelled simply **Veyra**, use `:app:assembleProdDebug` for a locally testable APK. Debug APKs are for testing; a published release requires your own stable signing key and `:app:assembleProdRelease`.

APKs are written under `app/build/outputs/apk/dev/debug/`. Choose arm64-v8a for most current phones, or universal if unsure. The dev app is named **Veyra Dev**; the production variant is **Veyra**. Both have their own package identity and can coexist with the original app. This new identity does not automatically import the original app's private data.

## Latest update: 0.1.0-beta3

The existing player and home layout are retained and refined:

- Clearer title/artist hierarchy, stronger timestamp contrast and larger player-menu touch targets.
- Press feedback on transport controls, static credits with reduced motion, and accessible seek/volume sliders.
- More room for player credits at large system font sizes, plus two-line shelf titles and a larger home layout toggle.
- One loading-animation clock per home feed, cached shimmer gradients, stable shelf keys and core-feed priority before supplementary requests.
- Artwork and audio caches survive cosmetic app updates.
- **Listen Together works for guests:** create, join or open an invite without signing in. Use a nickname or the generated guest name. An installation pseudonym is retained locally, while server-issued party tokens and host permissions still govern the session.

The six settings categories, global search, bold Manrope type scale and lavender accents from beta2 are preserved. See `verification/BETA3-RESULTS.md` for the exact checks and limits; the startup-lag report still needs a measured comparison on a phone.

Build dependencies require `dl.google.com`, Maven Central, the Gradle plugin portal, and **jitpack.io**. The current SDK package name is `android-37.0`; an older existing SDK can be selected with `-PveyraCompileSdk=android-37`. Do not disable TLS or artifact verification to work around access failures.

GitHub runs the normal checks on `main`. Signed releases, contributor maintenance and backend deployment are manual workflows; configure your own secrets and `BACKEND_HEALTH_URL` before using them.

## What changed

- Veyra branding in app labels, translations, screens, notifications, widgets, sharing text and supporting modules.
- Separate phone, TV, watch and native JNI package identity under `app.veyra.music`.
- Option 3 launcher artwork plus matching monochrome notification and themed icons.
- Manrope font weights, stronger small labels and improved dark secondary-text contrast.
- Independent app-update configuration, preventing the fork from offering the original app's APK.

Music playback, DSP, integrations and existing service addresses are preserved. Online features still depend on the respective providers and servers. The upstream server operators do not operate Veyra.

## Configuration

Android Studio creates `local.properties` with `sdk.dir`. Optional integration settings can be supplied there or through environment variables: `LASTFM_API_KEY`, `LASTFM_SECRET`, `LISTEN_TOGETHER_SERVER`, `DISCORD_APPLICATION_ID`.

Discord’s application ID is retained for compatibility. Some Discord surfaces use its remotely registered name/icon instead of the activity label. Supply your own `DISCORD_APPLICATION_ID` (registered as Veyra in Discord’s developer portal) for fully independent Discord branding. This cannot be changed solely by replacing local text.

Automatic app updates are disabled until you provide `VEYRA_UPDATE_FEED_URL` with your own compatible GitHub-style latest-release JSON feed. Publish Veyra APK assets with the expected `Veyra-` prefix; never point the fork at the original project's release feed. Production APK signing uses your own `keystore.properties`; see the example. Keep the same signing key for future updates.

The web, browser-extension, TV and watch sources are retained. Their public-service/deployment URLs are preserved, not rebranded into nonexistent domains. Configure your own deployment origins and extension identity before publishing a web fork. Android is the primary target of this handoff; see `verification/BETA3-RESULTS.md` for the checks run on this revision. `verification/RESULTS.md` describes the earlier supplied baseline.

## License and attribution

This is a modified GPL-3.0 project. Original attribution is intentionally retained in `NOTICE`, `LICENSE` and existing copyright/third-party notices. Manrope's SIL OFL license is in `licenses/Manrope-OFL.txt`. These legal credits and real service URLs are the intentional remaining references to the original project; ordinary app branding is Veyra.
