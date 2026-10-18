# Veyra 0.1.1 source update — 2026-10-18

## JioSaavn default correction

The supplied stable source had two causes of the reported off switch: SourceConfig defaulted JioSaavn to false, and SourceRegistry.init applied a one-time opt-in migration that forced even a stored true state off. Both are corrected. New source configs default to enabled; the old migration no longer runs. On upgrade, a new `jiosaavn_default_on_v2` marker applies a one-time enabled correction to JioSaavn. The corrected serialized sources and marker are written in the same preferences edit. Subsequent launches preserve the stored state, including an explicit off choice after updating.

Older versions did not record whether false came from their forced-off migration or from a manual switch. This requested one-time correction therefore enables legacy JioSaavn entries once, including previously disabled ones. After that first launch, switching it off keeps it off. Addon/custom-module enablement and URLs are preserved. Retired built-in modules are still removed and YouTube remains always available. Source IDs, ranks, recording matching, connection-quality gates and fallback behavior are unchanged. The existing manual re-enable warning is retained.

## Player changes

- Increase title emphasis to 28sp/34sp and artist contrast/line spacing, reserving enough header height for those lines at system font scale. Keep Manrope and the established bold white play/pause/skip glyphs; slightly enlarge portrait skip glyphs and show a subtle pressed state on the existing touch targets.
- Correct a credit crossfade bug: its outgoing slot previously read the current song, so both old/new slots displayed the replacement text. Each slot now keeps the appropriate title, artist credits, explicit badge and album link until it leaves. The fade is 220 ms or instant with reduced motion.
- Slightly strengthen the static dark control-area gradient for separation over artwork, without new blur/shadows or continuous decorative effects.
- Give seek, volume and bottom-row actions at least 48dp touch height. Update timing-label spacing and the hidden-volume reservation to match. Keep the loading and transport footprints stable.
- Settle slider height with a short tween instead of an overshooting spring. Disabled gestures do not begin changes; interrupted drags clear dragging/scrubbing state in finally. Existing playback/volume callbacks remain responsible for applying the result and honoring control locks.
- Shorten the queue capsule slide to 240 ms, remove its size bounce, and respect reduced-animation preferences in the capsule/credits/slider transitions. Reduced motion displays a static loading bar instead of composing the infinite sheen. No new images, shaders, network requests or dependencies are introduced.

The first-run Welcome, Super Malibu branding, Home banners/tiles, Explore cache, player artwork docking, lyrics/queue navigation, playback backend, library/downloads and existing feature callbacks remain. No measured frame-rate improvement is claimed.

## Checks actually run

- **50 focused Kotlin/JUnit tests passed, zero failures**, compiling the actual selected production helpers. Four new SourceEnablementPolicy tests cover the legacy default correction followed by repeated off-state restarts, preservation of user-added enablement, YouTube fallback and retained JioSaavn on state. The previous 46 tests also pass.
- Reproduce after Gradle bootstrap with `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py`. This harness is independent of the Android dependency graph and is not the full Android suite.
- Existing SourcesTest integration cases are updated to exercise fresh defaults, legacy migration, source-ID retention, idempotence and subsequent disabled-state retention. Those full-project tests could not run here; they are distinct from the 50 focused tests.
- Kotlin PSI syntax: **nine changed/new Kotlin files including the build script, zero errors**. This is parsing, not Compose/Android type checking.
- Resource audit: **114 XML documents, 22,613 localized entries, eight font references, zero errors**. Import inspection and `git diff --check` pass.

Attempted `:app:compileProdDebugKotlin :app:testProdDebugUnitTest` with installed JDK 17/SDK and normal proxy/CA trust, two workers. It stops at `:app:stripNewPipeExtractorUtils` before Android Kotlin compilation: **JitPack HTTP 403** for `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. Allow jitpack.io in cloud network settings to complete compilation and the full suite. No dependency or verification bypass is used.

**No APK/assemble/package task was run. No complete Android type check, emulator/phone screenshot, on-device UI/gesture verification or performance measurement is claimed.** Reports supplied with earlier ZIPs apply only to those stated revisions.

## Remaining device validation

Check a clean install and an upgrade from the supplied 0.1.0 source: JioSaavn on after the first launch, toggle off, force-stop/restart and verify it stays off; toggle on through the existing confirmation; verify source order, custom configs and YouTube fallback. Exercise mobile/Wi-Fi quality ceilings and matched/unmatched/unavailable streams.

On the target phone, inspect normal and large fonts, long/explicit titles, rapid track changes, artist/album links during fades, bright artwork, narrow/landscape player layouts, hidden volume, player open/close, lyrics/queue transitions, seeking/tapping/cancelled drags, control locks, loading, reduced motion and TalkBack. Confirm playback and downloads continue normally. Profile frame pacing before making smoothness claims.

The source stays in the stable version series: versionName 0.1.1, versionCode 14, empty beta suffix. Package ID, signing settings, R8 9.1.56 and lint 9.4.1 are unchanged. The ZIP contains the complete phone-only Android Studio source (app/shared/sharedUi), excluding previous downloads, build outputs, toolchains and private local configuration.
