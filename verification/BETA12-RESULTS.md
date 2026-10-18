# Beta12 brand header, welcome and navigation motion — 2026-10-18

## Baseline and scope

Based on the owner's beta11 Build Fixed ZIP, adopted at commit b6ab495. Preserve its R8 9.1.56, lint 9.4.1 and other build-tool versions. The supplied BUILD_FIX_BETA11.md and BUILD_VERIFICATION_BETA11.json remain historical reports for that uploaded revision, not verification of this update.

Only the Home header, Welcome presentation, brand resources, navigation transitions and related UI state retention change. Existing playback, downloads, library, playlists, account/sign-in, settings actions, Home banner/tile collections and Explore artwork caching remain. Application/package identity and signing configuration are unchanged. VersionCode is 12; versionName is 0.1.0-beta12. The Android project remains phone-only (app, shared, sharedUi).

## Branding and Home

The shared brand composition copies the exact `mipmap-xxxhdpi/ic_wave_glass.png` asset used by AppIcon.Default. This is the current black-and-white glass waveform, not the older colored V logo. A byte comparison confirms the copied shared resource is identical. Existing alternate launcher choices are unaffected.

The owner supplied SuperMalibu-Wp77v.ttf and confirmed app-embedding and public-source redistribution permission. Its exact bytes are bundled as an Android font resource, with Regular weight and FontSynthesis.None. Only the Home/Welcome wordmark uses it; supporting UI stays in Manrope. See licenses/Super-Malibu-Provenance.md. No licence file was supplied; that provenance note does not invent an upstream licence.

The wordmark is one fully shaped “Veyra”, preserving the font's authentic proportions and kerning. Measured font advance is about 3.214 em; a width allowance of 3.4 em plus a 1.45 line-height leaves room for its bearings/metrics. Available width sizes the brand like a logo, while body/form text follows system font size. The complete mark is announced once by accessibility, with the adjacent decorative image excluded.

Home uses a compact brand row, a responsive listening heading, supporting caption and quiet divider. It has one 300 ms reveal per retained Home state, marks the intro spent before animating, and does not restart it when returning from a pushed screen. Scrolling carries the header away naturally without resizing the list or adding a scroll-driven animation. No decorative element is given a fake button action; existing top-bar actions remain.

## Welcome and motion cost

The old presentation already used a cached static background, but ran two 5.2-second decorative loops. This update removes those loops, per-letter motion and the rainbow wordmark. A single 620 ms icon/word reveal and short ribbon settling motion replace them. The backdrop is a cached charcoal gradient with a restrained static highlight, not an animated background. Phase is read in graphics/drawing layers. Keyboard visibility, backgrounding and reduced motion settle the reveal; it does not replay on resume.

Localized headline words enter over 280 ms with at most 120 ms of staggering. Step transitions use a 260 ms reading-direction slide/fade; final dismissal uses a short slide/fade and respects reduced motion. Name field state, validation, keyboard actions, scrollable/inset-aware layout, two-step progress, optional sign-in/skip and first-run completion retain their callbacks. The existing onboarding consists of name entry and optional sign-in; this update does not introduce new permission/import steps or change those separate flows.

## Navigation

A destination's key is independent of its depth. Forward pages enter from the reading direction; Back returns reverse the movement. Detail depth also accounts for Library grid/Explore context and Settings above a detail. Tab changes use a 140 ms incoming fade, and destination surfaces remain opaque to prevent overlapping text and a dip through a dim background. Explore categories and Settings overlays use the same 240–260 ms pair. Outgoing detail, grid and category data are held while leaving instead of disappearing when the underlying selection clears.

Root tab state and Settings saveable state (including its search query) are retained across pushes using a SaveableStateHolder; transient detail pages do not accumulate saved slots there. Hoisted list positions, search state and existing detail list ownership are retained. The custom full-player/mini-player docking and existing back/gesture handling remain unchanged; no new predictive-back implementation is claimed. Animation specs obey Compose's system duration scaling, and the app's reduced-animation preference uses no enter/exit transition. No mandatory decorative delay, extra network lookup, continuous timer or new rendering dependency was added.

## Actual checks

- **46 focused Kotlin/JUnit tests passed, zero failures**, compiling the real selected production helpers. Four new navigation policy tests cover all tab pairs, push/pop reversal through opaque detail IDs, nested Settings context and avoiding re-entry animation for an unchanged retained key. The existing 42 regression tests also passed.
- Reproduce after Gradle wrapper bootstrap: `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py`. This is an independent helper harness, not the complete Android unit suite.
- Kotlin PSI parsing and resource/import/whitespace audits are recorded in beta12-checks.json. Parsing is not Android/Compose type checking.
- Font identity/metrics and glyph coverage for “Veyra” checked directly from the TTF; copied icon identity checked against the actual launcher bitmap.

Attempted **`:app:compileProdDebugKotlin :app:testProdDebugUnitTest`** using JDK 17, installed SDK and normal CA/proxy settings. It stopped at `:app:stripNewPipeExtractorUtils` before Android Kotlin compilation: JitPack returned **HTTP 403** resolving `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. This is the existing cloud access blocker; no dependencies or security checks were bypassed. Allow jitpack.io in the cloud network settings to complete Android compilation/testing.

**No assemble/package/APK task was run. No full Android type check, complete Android test suite, actual app screenshot, emulator/device visual validation or measured FPS is claimed.** There is no installed emulator system image available here. Font metrics and source inspection are not a substitute for viewing the app on the target phone.

## Remaining phone checks

On the Realme Narzo 60 5G, check first/returning launch, font rendering/baselines, small widths/large text, light/dark themes, RTL, TalkBack, name validation, keyboard dismissal, rotation, Back through both welcome steps and optional sign-in. Check Home scrolling, retained banner/tile mode, tab switching, category/grid/detail push and return, nested Settings and rapid back/taps, player open/close and uninterrupted playback. Repeat with animations disabled. Existing permission/import acceptance, denial, skip and error paths should be included in the device regression pass. Profile frame pacing on the device before claiming 90 Hz smoothness.
