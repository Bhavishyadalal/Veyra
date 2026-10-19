# Home refinement — preview 0.1.4-beta1

19 October 2026. Branch `preview/0.1.4-beta1`; version remains **0.1.4-beta1 / code 17**. Stable main remains 0.1.3 / code 16 and its original source ZIP is unchanged. No APK, signing, release or stable merge.

## Changes

- Stronger native heading hierarchy with the existing app icon, Super Malibu wordmark, waveform accent and one-time reduced-motion-aware introduction. Header copy follows system text size.
- A neutral charcoal/light account prompt replaces Home's purple gradient. The Google icon, wrapping text, 48dp+ target, button semantics and clear pressed state retain the same host sign-in callback. No credential fields or mandatory sign-in are introduced. Other screens' shared account banner is unchanged.
- Banner and tile cards share a deeper artwork scrim, native loading/error fallback, consistent rounded edges, bold captions and current-track outline/badge. Existing Coil artwork sizing/cache and song/collection tap, hold and context-menu callbacks remain.
- New paging controls have bounded progress, tabular counter, 48dp arrows, RTL mirroring and boundary/scroll disabled states. They use settled pages; layout switching still preserves the existing item-to-tile anchor conversion. Press and icon feedback is finite and respects reduced motion. Compact row menu targets increase from 36dp to 48dp and remain visible but disabled in outgoing layouts.
- Fading outgoing banner/tile content cannot receive card taps, holds, context clicks or pager swipes, and is removed from accessibility discovery. Its incoming counterpart retains the existing actions.
- A successful but empty feed now has an explicit refresh action, disabled while refreshing. Empty shelf headings are skipped; duplicate upstream shelf titles receive unique per-title occurrence keys without moving recommendations when Recents arrives.
- Featured loading placeholders match the revised card radius and navigation footprint. Existing skeleton shimmer, retry, retry-more, pull refresh, continuation loading, recents view persistence and current-track matching remain.

## Executed validation

- **17 native Home component checks pass**, with nine native review boards. Banners, tiles, dark/light, missing artwork, empty state, RTL and enlarged text were visually reviewed. Fixtures use production primitives but do not reproduce the full Android screen, live feed or playback.
- **61 focused Kotlin/JUnit regressions pass**, including four new production feed-identity cases: repeated headings, late Recents insertion, separator collisions and Turkish device locale. Existing carousel anchor tests remain.
- Kotlin PSI source audit retains **18 existing call contracts and 41 original named function interfaces**, compares untouched function bodies, and byte-compares **223 player/playback/shared/auth/host files** to the starting preview. The audit is not runtime proof.
- Six Home/skeleton Kotlin files parse with zero syntax errors. Resource audit parses 114 XML files, checks 22,638 localized entries and resolves eight XML font references, with zero errors. `git diff --check` passes.

## Android validation limit

Full Android compilation remains blocked before Kotlin compilation at `:app:stripNewPipeExtractorUtils`: JitPack returns HTTP 403 for `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. No dependency substitution or TLS/verification bypass was used. Native Desktop component compilation and source parsing do not establish full Android compilation, actual feed behavior or device performance. No APK was generated and no device/emulator verification is claimed.

Before promoting: build with normal dependency access; test guest/Google entry, successful/empty/error feeds, pull refresh and pagination retry, late history arrival, every card tap/hold/menu, banner/tile switching mid-swipe, long/duplicate headings, shrinking/refreshed collections, navigation/scroll restoration, current/paused tracks, compact landscape, RTL, enlarged text, TalkBack and reduced motion. Playback and Android host sources are unchanged, but integration still needs that device pass.

Reproduce [native checks](home-preview/README.md), [preservation audit](home-audit/README.md), `python verification/run_focused_kotlin_tests.py` and `python verification/audit_branding.py` with JDK 17 and the bootstrapped Gradle cache. The updated preview ZIP contains these sources and review boards, with CRC/hash validation; it excludes build caches, local settings, APKs and signing keys.
