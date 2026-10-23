> **Immersive Worlds · build 37:** Classic plus Artwork Cinema, Theme World, Lyrics Focus, Minimal Night and Landscape Stage. Direct player access, a six-mode picker and seekable playback dock. [Details](verification/IMMERSIVE-WORLDS-0.1.9.md).

> **Five new illustrated themes:** [Illustrated Worlds](themes/ILLUSTRATED_WORLDS.md) · [Preview collection](themes/previews/illustrated-worlds/collection.png). Compatible with the existing beta engine build 30.

> **Stable 0.1.9 / code 37:** 62 themes, five independent Layer Lab asset overlays, 95 matching launcher choices, live Home icon updates, theme artwork controls, queue/Home/search improvements and lighter asset rendering. [Release notes](RELEASE_NOTES_0.1.9.md) · [Promotion checks and limits](verification/STABLE-0.1.9-PROMOTION.md) · [Source downloads](downloads/README.md).

> Completed preview **0.1.9-beta1 / code 35** remains on `preview/0.1.9-beta1` with its original source ZIP. Stable 0.1.8 / code 28 remains available on `stable/0.1.8`.

![Veyra — Your music. Another dimension.](media-kit/banners/veyra-app-banner.png)

> **Source tracks:** current stable is on `main` and `stable/0.1.9`. Historical stable 0.1.7 remains on `stable/0.1.7`; the completed `preview/0.1.8-beta1` remains code 27. Stable 0.1.9 includes all 62 themes, five asset packs and 95 launcher choices. [Downloads](downloads/README.md) · [Backend policy](docs/BACKEND_SYNC.md).

> Open the complete project using [START_HERE.md](START_HERE.md). Source ZIPs are Android Studio projects, not APKs; promotion does not publish an installable APK.

# Veyra

A personal Android music player with Manrope typography, selectable launcher styles and a dark listening interface.

## Website and artwork

[Browse the Veyra media kit](media-kit/README.md) · [Website and media-kit ZIP downloads](downloads/README.md). Includes link-sharing artwork, app banners, social formats, original backgrounds and an offline preview gallery. [Visit the website](https://veyra-website-mauve.vercel.app/) · [Website source](https://github.com/Bhavishyadalal/Veyra-website).

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

## External cron and keep-alive

Both stable and preview use the same backend. [Configure your external 12-minute cron job](docs/RENDER_KEEPALIVE.md), including its exact URL, method, timeout and expected response. The backend's existing GitHub schedule remains available as a fallback. No app-side background timer is required.

## Midnight Express — your next stop, another song

An original rainy night-train theme with warm amber carriage lighting, coal-blue
surfaces, bold Rajdhani headings, route-style player details and an optional
Veyra ticket-waveform launcher. [Theme and previews](themes/README.md#midnight-express--your-next-stop-another-song)
· [Verification](verification/MIDNIGHT-EXPRESS-0.1.7-beta1.md).

## Astral Tide — drift deeper, hear everything

An original bioluminescent ocean theme: luminous manta rays, bold Space Grotesk
headings, sea-glass mint controls and an optional manta-waveform launcher icon.
[Theme pack and native studies](themes/README.md#astral-tide--drift-deeper-hear-everything)
· [Verification and limits](verification/ASTRAL-TIDE-0.1.7-beta1.md).

## Theme Studio — introduced in stable 0.1.7

Download Halloween Nocturne from Settings → Appearance → Theme studio. It includes
original artwork, licensed fonts, custom navigation and transport glyphs, themed
native buttons and an ember timeline. Installation is validated and applying asks
before refreshing the UI. Original appearance, music, accounts and existing
controls remain available. [Theme guide and native previews](themes/README.md).

## Previous stable 0.1.6 — Home ready sooner

Restore a previously loaded Home before fetching fresh shelves, keep it visible during refresh/failure, and customize section order/visibility per account/channel. Offline Home provides direct access to existing Downloads and Local Music; cached online-only items are clearly distinguished from downloaded songs. A first successful online load is required to populate the snapshot. Images use existing Coil caching and fallbacks; music is not downloaded automatically.

[Release notes](RELEASE_NOTES_0.1.6.md) · [Native component review](design/home016-preview/README.md) · [Checks and device checklist](verification/HOME-0.1.6-beta1-RESULTS.md). No APK or backend deployment is generated here.

## Previous stable: 0.1.5 — Veyra-owned Listen Together

Rooms, invite links and app presence now use **https://veyra-listen-together.onrender.com**. Saved old managed server addresses migrate safely; old membership tokens are discarded without being sent to another service. Intentional custom servers, guest listening and optional Google profiles remain supported. Old-service rooms require a new invite.

The backend lives in [Veyra-backend](https://github.com/Bhavishyadalal/Veyra-backend). Its [12-minute keep-alive workflow](https://github.com/Bhavishyadalal/Veyra-backend/actions/workflows/render-keepalive.yml) is independent of the phone app. GitHub schedules are best effort; Render restarts still clear in-memory rooms. The backend folder mirrors the current standalone Veyra server; the obsolete Oracle workflow is removed. See [backend provenance](docs/BACKEND_SYNC.md).

This stable source now includes the Home, player and Settings refinements previously developed on preview/0.1.5-beta1. The historical preview branch is preserved. The beta suffix is removed; version code 20 permits upgrades from preview code 18 and the earlier stable code 19. No APK or GitHub release is published by this promotion. Local verification and the owner's device report are recorded separately in [the migration report](verification/backend-migration/README.md).

## Previous update: 0.1.3 — silver waveform welcome

A dedicated welcome landing now follows the supplied design direction: full-width silver waveform artwork, bold native typography, optional swipe/tap previews and a silver Get started button. No restore-backup entry is included. Get started goes directly to name entry; account linking remains optional.

Home has a stronger heading and native waveform accent; Settings has a clearer hierarchy and category emphasis. No player/backend/appearance-setting behavior is changed. See [artwork provenance](design/launcher/WELCOME-ARTWORK.md) and [checks/build limits](verification/STABLE-0.1.3-RESULTS.md).

[Current source downloads](https://github.com/Bhavishyadalal/Veyra/blob/main/downloads/README.md). 57 focused regressions, native resource compilation and syntax checks pass. Full Android compilation is blocked by JitPack HTTP 403; native device rendering is unverified. No APK was generated.

## Previous update: 0.1.2 — welcome artwork and creator links

- **Campaign welcome:** original liquid-glass artwork, warm neutral styling, keyboard-aware name entry and finite reduced-motion-aware transitions. Images are local and use the existing Coil cache.
- **Creator links:** searchable Settings rows for [Bhavishyadalal](https://github.com/Bhavishyadalal), [Veyra source](https://github.com/Bhavishyadalal/Veyra) and the [website repository](https://github.com/Bhavishyadalal/Veyra-website).
- **Optional account linking:** clear Continue/Start listening actions; sign-in remains optional. Playback, data, JioSaavn defaults and the existing player behavior are retained.

[Current source downloads](https://github.com/Bhavishyadalal/Veyra/blob/main/downloads/README.md). See [validation and build limits](verification/STABLE-0.1.2-RESULTS.md). 50 focused regressions and native resource compilation pass; full Android compilation remains blocked by JitPack HTTP 403. No APK was built.

## Previous update: 0.1.1 — JioSaavn default and player refinement

- **JioSaavn starts on:** fresh installs enable it automatically. A one-time correction replaces the old forced-off migration on existing installations. Later switch-off choices survive restarts; YouTube fallback, custom sources, matching and quality rules remain.
- **Clearer player:** stronger song/artist hierarchy, better contrast over artwork, correct outgoing credits during track changes, bold white transport icons and visible pressed feedback.
- **Easier controls:** 48dp seek/volume/bottom-action targets, stable spacing when hiding volume, shorter queue-control transitions and reduced-motion support. Interrupted slider gestures release scrubbing state.
- **Stable baseline retained:** based on your supplied 0.1.0 Stable source, preserving Home banners, the glass waveform/Super Malibu branding, Welcome, Explore artwork caching and R8/lint build fixes.

Download `downloads/Veyra_Android_Source_0.1.1_Stable.zip`, extract the whole folder and open Veyra in Android Studio. The project remains phone-only (app plus shared/sharedUi). See [verification/STABLE-0.1.1-RESULTS.md](verification/STABLE-0.1.1-RESULTS.md) for checks and limits. No APK task was run for this update.

Build dependencies require `dl.google.com`, Maven Central, the Gradle plugin portal, and **jitpack.io**. The current SDK package name is `android-37.0`; an older existing SDK can be selected with `-PveyraCompileSdk=android-37`. Do not disable TLS or artifact verification to work around access failures.

GitHub runs the normal checks on `main`. Signed releases, contributor maintenance and backend deployment are manual workflows; configure your own secrets and `BACKEND_HEALTH_URL` before using them.

## What changed

- Veyra branding in app labels, translations, screens, notifications, widgets, sharing text and supporting modules.
- Phone and native JNI package identity under `app.veyra.music`.
- Option 3 launcher artwork plus matching monochrome notification and themed icons.
- Manrope font weights, stronger small labels and improved dark secondary-text contrast.
- Independent app-update configuration, preventing the fork from offering the original app's APK.

Music playback, DSP, integrations and existing service addresses are preserved. Online features still depend on the respective providers and servers. The upstream server operators do not operate Veyra.

## Configuration

Android Studio creates `local.properties` with `sdk.dir`. Optional integration settings can be supplied there or through environment variables: `LASTFM_API_KEY`, `LASTFM_SECRET`, `LISTEN_TOGETHER_SERVER`, `DISCORD_APPLICATION_ID`.

Discord’s application ID is retained for compatibility. Some Discord surfaces use its remotely registered name/icon instead of the activity label. Supply your own `DISCORD_APPLICATION_ID` (registered as Veyra in Discord’s developer portal) for fully independent Discord branding. This cannot be changed solely by replacing local text.

Automatic app updates are disabled until you provide `VEYRA_UPDATE_FEED_URL` with your own compatible GitHub-style latest-release JSON feed. Publish Veyra APK assets with the expected `Veyra-` prefix; never point the fork at the original project's release feed. Production APK signing uses your own `keystore.properties`; see the example. Keep the same signing key for future updates.

The web and browser-extension sources are retained outside the Android build. Their public-service/deployment URLs are preserved. TV and Wear OS sources were removed in beta7. Android phone is the target of this handoff; see `verification/STABLE-0.1.1-RESULTS.md` for current checks. `verification/RESULTS.md` describes the earlier supplied baseline.

## License and attribution

This is a modified GPL-3.0 project. Original attribution is intentionally retained in `NOTICE`, `LICENSE` and existing copyright/third-party notices. Manrope's SIL OFL license is in `licenses/Manrope-OFL.txt`. These legal credits and real service URLs are the intentional remaining references to the original project; ordinary app branding is Veyra.
