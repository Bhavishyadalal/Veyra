# Beta10 welcome and Explore artwork — 2026-10-18

## Welcome changes

The two first-install steps use cream Manrope type, lilac/peach accents, large Veyra lettering whose letters gently move in sequence, staggered headline words and a small three-path ribbon. Page transitions follow reading direction. Progress, Back, name entry and optional Google sign-in/skip retain their existing callbacks and completion flag.

The ambient phase runs for two 5.2-second loops and settles, stops while the keyboard is open or the app backgrounds, and is disabled by the app's reduced-animation preference or a zero Compose motion-duration scale. Letter movement is read in graphics layers and the ribbon phase in drawing, avoiding full-page recomposition at every animation frame. There is no animated full-screen blur, video or downloaded welcome image.

The page scrolls when content exceeds available height, including with the keyboard or large fonts. The decorative wordmark scales to available width; body/form text follows system font size. Buttons and the text field have minimum rather than fixed heights. Localized headlines remain full strings for accessibility; only their visual word entrance is staggered. Back and transitions respect RTL. Onboarding still appears only where the existing first-run flag requires it.

## Explore diagnosis and changes

The app already had Coil's 100 MiB persistent image cache. The repeated work was rediscovering each category's artwork URL by browsing its playlist shelves after every process restart. Without the URL, the UI could not ask Coil for the already-cached image.

`ExploreArtworkCache` persists successful public image URLs in the app cache directory, keyed by browse ID, params, region and language. It restores them on IO before publishing the category grid. Fresh metadata skips the category-artwork network lookup; Coil then reuses its existing bytes. Seven-day positive entries and six-hour empty answers avoid repeated work. Expired entries can refresh while their existing cover remains visible, and lookup failures/empty refreshes preserve the last good cover. Explicit pull-to-refresh bypasses freshness and renews the shared browse result as well. The in-process shelf cache is now locale-aware.

Writes are atomic; malformed entries are treated as misses; invalid/non-web/credential-bearing URLs are rejected; failed/cancelled requests do not become cached failures. Same-key requests coalesce, metadata is capped at 256 entries, lookups are limited to two concurrent requests with eight-second deadlines, and late results from superseded loads/accounts/locales are ignored. A storage failure still allows fresh network art to display. A failed category-list refresh retains the current successful grid.

Explore image requests explicitly enable Coil memory/disk caching and omit crossfades so a local cover does not fade in as though freshly downloaded. The first successful lookup warms this metadata cache. Android cache eviction, cleared/missing image bytes, an expired entry, changed URL or manual refresh can still require the network. This does not cache the entire category list or promise offline browsing when that initial list is unavailable.

## Online design references consulted

Accessed 2026-10-18; implementation and artwork are original:

- [Material motion](https://github.com/material-components/material-components-android/blob/master/docs/theming/Motion.md): shared-axis direction, easing/duration hierarchy and restrained transitions.
- [Material typography](https://github.com/material-components/material-components-android/blob/master/docs/theming/Typography.md): emphasized editorial headings and clear type hierarchy.
- [AndroidX AnimatedContent samples](https://github.com/androidx/androidx/blob/androidx-main/compose/animation/animation/samples/src/main/java/androidx/compose/animation/samples/AnimatedContentSamples.kt): coordinated incoming/outgoing content transitions.

## Checks passed here

- **36 focused Kotlin/JUnit tests**, zero failures: the existing 23 plus **13 new cache tests**. These compile the real cache production class and exercise actual temporary files, process-instance reuse without another lookup, expiry, offline fallback, explicit refresh, negative-result expiry, cancellation, concurrent same-key calls, corruption recovery, key separation, invalid URLs, empty refresh fallback, disk-write failure, and bounded eviction that retains the newest write.
- Reproduce with `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py` after wrapper bootstrap. This harness uses the Gradle distribution's compiler and JUnit independently of the Android graph; it is not the full Android test suite.
- Kotlin PSI parsing: **49 changed/new source files**, zero errors, relative to original beta1. Parsing is not Compose/Android type checking.
- Resource audit: **114 XML documents, 22,559 localized entries, eight font references, zero errors**.
- Duplicate-import inspection and `git diff --check` passed. The project remains phone-only (`app`, `shared`, `sharedUi`).

## Full build and device limits

Attempted `:app:compileProdDebugKotlin :app:testProdDebugUnitTest :app:assembleProdDebug` with the verified local JDK 17, installed SDK, normal session CA trust/proxy and two workers. The build stops at `:app:stripNewPipeExtractorUtils`: **HTTP 403 for jitpack.io** resolving `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`, before Android Kotlin compilation. Required JitPack access must be enabled in cloud environment settings to complete it. No dependency or verification was bypassed.

**No successful complete Android type check, full Android unit suite, beta10 APK, emulator/phone screenshot, visual/device review or measured performance gain is claimed.** Build reports supplied in earlier ZIPs describe earlier revisions only. Backend code is unchanged.

Before release, verify first install, blank/valid/long names, IME Next, Back, skip/sign-in/already-signed-in completion, TalkBack, large fonts, short landscape windows, RTL, system animations off, app reduced motion, background/resume and return from sign-in. For Explore, compare first load and process restart, inspect browse/image requests, test refresh and stale/offline fallback, clear/evict image bytes, switch accounts/locales during lookups and open a category while its cover loads. Profile on the target phone before claiming frame-rate or startup gains.

VersionCode is 10; versionName is 0.1.0-beta10. The source ZIP excludes older downloads, caches, SDK/JDK files, build outputs and private machine configuration.
