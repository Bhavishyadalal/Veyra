# Beta11 Home banners and cover tiles — 2026-10-18

## Home changes

Home recommendations now combine a wide featured banner carousel, three-cover mosaic pages (one large cover with two stacked covers), and existing compact shelves. The pattern uses existing theme colors, Manrope typography, rounded artwork, contrast scrims and playback icons. Recently Played retains its saved grid/list presentation; recommendation numbering excludes it so late history arrival does not change which recommendation is featured.

The featured section has a banner/tile toggle with a 260 ms horizontal slide/fade and animated size change. The direction follows RTL. Switching retains the exact banner item within its tile group; if the user browses to another group, switching back opens that group's first item. All shelf items remain available, including partial final pages. Manual swipe, previous/next controls, localized page descriptions and disabled end controls provide navigation without automatic rotation.

Page scale/alpha read scroll state inside graphics layers, avoiding frame-by-frame recomposition for those effects. Only visible/adjacent pager pages are composed by the existing lazy pager. No video, continuous decorative animation, new network metadata fetch or new dependency was added. Existing resized artwork requests and image caching remain in use. This reduces avoidable work but is not a measured frame-rate improvement.

Controls have 48dp targets. Tile pages become readable rows for font scale above 1.3 or page widths below 280dp; titles support two lines. Banner height adapts to font scale. Reduced-animation preference removes decorative transforms and toggle/page-button animation. Tap, long press, context actions, collection navigation and current-song indicators retain their existing callbacks. Loading placeholders match the featured banner footprint.

Beta10 first-run welcome and persistent Explore artwork cache and beta8/beta9 additions are retained. The project remains phone-only: app, shared and sharedUi; no TV/Wear OS module is included. VersionCode is 11, versionName 0.1.0-beta11.

## Internet design references

Consulted online on 2026-10-18. No external design assets or new libraries were imported:

- [Material carousel guidance](https://github.com/material-components/material-components-android/blob/master/docs/components/Carousel.md): hero/multi-browse hierarchy, image emphasis and manual browsing.
- [AndroidX Pager samples](https://github.com/androidx/androidx/blob/androidx-main/compose/foundation/foundation/samples/src/main/java/androidx/compose/foundation/samples/PagerSamples.kt): fixed page sizing, page offsets and explicit animated navigation.
- [Material motion](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md): coordinated, restrained directional transitions.

## Checks passed

- **42 focused Kotlin/JUnit tests, zero failures**, including six new tests of the actual production paging helper: every item across shelf lengths 1–200, exact-item round trips, changed groups, empty/negative counts, feed shrinkage, partial pages and integer bounds. The other 36 existing tests also pass.
- Reproduce after Gradle bootstrap with `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py`. This harness compiles the selected real production helpers independently of the Android dependency graph; it is not the full Android test suite.
- Kotlin PSI syntax parsing: **52 changed/new Kotlin files**, zero syntax errors relative to beta1. This does not type-check Compose/Android APIs.
- Resource audit: **114 XML documents, 22,613 localized entries, eight font references, zero errors**. Three new labels are present in all 18 shared UI catalogs with valid page-format arguments.
- Import inspection and `git diff --check` passed.

## Android build and remaining device checks

Attempted `:app:compileProdDebugKotlin :app:testProdDebugUnitTest :app:assembleProdDebug` with installed JDK 17/SDK, normal proxy and CA trust, and two workers. It fails at `:app:stripNewPipeExtractorUtils`: JitPack returns **HTTP 403** resolving `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`, before Android Kotlin compilation. Allow `jitpack.io` in cloud environment network settings to unblock the build; no dependency or verification was bypassed.

**No successful full Android type check, full Android test suite, beta11 APK, emulator/phone visual review or measured smoothness is claimed.** Historical build reports apply only to their stated revisions.

After dependency access is restored, run the build and test on a phone: guest and signed-in feeds, Recents arriving before/after recommendations, empty/single/partial/long collections, refresh removing items, rapid toggles/swipes, navigation away/back, playback and long-press actions, large text, narrow/landscape screens, RTL, TalkBack, reduced motion/system animations off, and slow/offline artwork. Profile scrolling and first render on the target device before claiming a performance gain.

The source ZIP excludes downloads, TV/Wear OS sources, build outputs, toolchains, caches and local/private configuration. Extract the entire Veyra folder and open it in Android Studio.
