# Veyra 0.1.0-beta12 (brand header, welcome and navigation motion)

- Use the latest default black-and-white waveform icon and supplied Super Malibu Regular font in a shared Home/Welcome brand composition. Bundle the exact TTF locally following the owner's permission confirmation; keep Manrope elsewhere.
- Give Home a compact branded header with a short once-per-retained-state reveal, readable listening heading and quiet divider. Keep the decorative brand distinct from the existing working top-bar actions.
- Replace Welcome's two long looping flourishes with one 620 ms reveal and static charcoal artwork. Shorten headline and step transitions, keep name entry and optional sign-in, stop decorative motion in background/while typing and shorten final dismissal.
- Add a consistent directional motion policy for pushes/returns, short tab fades and Settings/Explore sub-screen transitions. Keep opaque destination surfaces during transitions and retain root-page saveable state without accumulating transient detail states.
- Preserve existing player docking/back handlers, playback, downloads, account flows and beta11 Build Fixed tool versions. No dependencies, application identity or signing configuration changed.
- 46 focused tests pass, including four navigation policy regressions; see `verification/BETA12-RESULTS.md` for the JitPack compile blocker and unrun device checks. No APK task was run.

# Veyra 0.1.0-beta11 (Home banners and cover tiles)

- Alternate a featured banner carousel, three-cover mosaic pages and compact shelves; preserve Recently Played's chosen layout and stable recommendation positions when history loads.
- Add a featured banner/tile switch with reading-direction-aware sliding/fading transitions and position retention, manual paging arrows and localized page announcements.
- Preserve every shelf item, tap/hold/context actions and current-playback indicators. Reuse existing image sizes/cache, typography, colors and rounded artwork treatment.
- Use readable rows on narrow screens or with large text, 48dp controls and reduced-animation handling. Animate page scale/opacity in graphics layers; no automatic carousel timer or new dependency.
- Add six paging regression tests; 42 focused tests pass. Full Android build remains blocked by JitPack HTTP 403. Device visual review and profiling remain required; see `verification/BETA11-RESULTS.md`.
- Retain beta10 welcome/Explore caching and the supplied beta8/beta9 additions. Phone-only source project; versionCode 11.

# Veyra 0.1.0-beta10 (flowing welcome and persistent Explore artwork)

- Reworked first-install onboarding with a flowing letter wordmark, staggered localized headlines, a small animated ribbon, progress treatment and coordinated page transitions.
- Kept name entry and optional sign-in/guest flow. Added scrolling for short windows/large fonts, RTL-aware transitions/back navigation, full-heading accessibility semantics, adaptive controls and reduced-motion handling.
- Ambient motion is finite, foreground/keyboard-aware and reads animation state in drawing/graphics layers. No full-screen animated blur, video, downloaded hero artwork or new dependency.
- Persist Explore's category-to-image URL lookups instead of browsing every category again after process restart. Restore cached URLs before displaying the grid and reuse Coil's disk/memory image cache.
- Add stale/offline fallback, manual refresh, expiry, negative caching, same-key coalescing, atomic writes and bounded disk storage. Keep artwork lookups to two concurrent requests with per-request timeouts and reject stale account/locale/generation results.
- Added 13 real filesystem/coroutine cache regressions; 36 focused tests pass. Android build/device limits and online references: `verification/BETA10-RESULTS.md`.
- Beta8/beta9 source additions remain; their supplied update notes are retained.

# Veyra 0.1.0-beta7 (phone-only project and visual polish)

- Removed TV and Wear OS module sources and Gradle includes, the phone's bundled-watch APK task and shared watch-protocol source path.
- Removed watch pairing/ADB dependencies, wearable services, watch audio-output integration and watch Settings navigation. Cast, Bluetooth and phone playback remain.
- Removed unused watch-screen labels and TV/watch CI build steps; kept the phone's two required shared library modules.
- Moved NewPipe archive reads into a cacheable task action; verified the real task with an isolated fixture and successful dependency-free dry run.
- Enabled Gradle task-output caching for repeat builds and updated Android Studio instructions for the phone-only project.
- Made Home's Google sign-in invitation compact and softened the player control deck by removing its border and using a darker, subtler gradient.
- See `verification/BETA7-RESULTS.md` for current validation and the remaining Android compilation blocker.

# Veyra 0.1.0-beta6 (Home and playback preparation)

- Replaced Home's large header card with a compact title, brand mark, supporting caption and accent rule.
- Cancel superseded Home loads/pages, reject stale pagination results, bound optional recent-history/discovery requests and settle empty initial feeds with a retryable message.
- Preserve feed content during refresh; pause pagination while replacing the continuation and restore it if refresh fails. Clear refresh/loading indicators in finally blocks.
- Failed pagination exposes a Retry action and blocks automatic retry loops. Explore loads on its first tab visit instead of competing with Home at startup.
- Reuse media-item conversions for queue startup and shuffle restoration, avoiding repeated URI/download-file checks.
- Warm the next queue URL after 1.5 seconds independently of addon lookup; retain the later-URL and bulk-download delay. Propagate cancellation and skip duplicate/pinned IDs.
- Added five coroutine warm-up regression tests. Validation and the JitPack Android-build blocker: `verification/BETA6-RESULTS.md`. No device speed measurement or verified beta6 APK is claimed.

# Veyra 0.1.0-beta5 (rendering and interface refinement)

- Removed the colored play/pause disc and skip-button fills; enlarged the bold white transport glyphs.
- Replaced Settings category cards and group boxes with flat rows, descriptions and dividers. Search, category navigation and controls are preserved.
- Removed continuous decorative meshes from the sign-in banner and Home/Library utility cards.
- Pause Canvas decoding when music pauses, the app backgrounds or the player transitions. Apply the same guards to periodic GPU frame captures, and reject invalid refresh intervals.
- Default to reduced dynamic blur only when no preference exists; retain explicit choices and high-performance behavior. Reduced blur/motion skip recurring Canvas palette captures.
- Cache legacy mesh color tuning and geometry; observe color-animation values during drawing; honor reduced blur and stop drift in the background.
- Added decoder/capture policy regression coverage. See `verification/BETA5-RESULTS.md` for checks and the Android build blocker. Improvements remove identified work; device profiling is still needed to establish actual frame-rate gains.

# Veyra 0.1.0-beta4 (visual redesign, source update)

- Player: prominent filled lavender play control, separate circular skip buttons, rounded tonal control deck, larger song title and softer artwork corners.
- Home: branded static gradient hero with a localized supporting caption.
- Settings: tonal overview header and responsive two-column category cards; full-width rows for narrow windows or large text, with global search retained.
- Bottom navigation: stronger selection capsule and labels; coordinated glass-mode colors and search highlight; selectable tab semantics in the regular bar.
- Navigation swipes use current callbacks and measured geometry, clamp at the ends and support right-to-left reading order. Added five regression tests.
- Added the two captions across all existing relevant catalogs; no new fonts or heavy rendering dependency.

Current verification: `verification/BETA4-RESULTS.md`. Full Android compilation/assembly is blocked by this cloud instance's JitPack HTTP 403; no verified beta4 APK is provided.

# Beta3 build setup refresh — 2026-10-17

- Restored Android Studio module discovery, pinned the native toolchain and refreshed the complete checksum-verified Gradle wrapper.
- Added Windows import instructions and an SDK path example.
- Built and signature-verified the beta3 APK; completed the phone unit suite. See verification/ANDROID-STUDIO-BUILD.md.
- Application sources, resources, translations and phone/watch protocol are unchanged from the uploaded beta3.

# Veyra 0.1.0-beta3 (source update)

- Refined the existing player: stronger song/artist hierarchy, clearer secondary text, responsive credit height, larger menu touch areas and transport press feedback.
- Seek and volume sliders expose labeled progress actions to accessibility services and use current gesture callbacks after track changes; seeking respects party host locks.
- Reduced-motion mode stops both title/artist marquees and player-menu crossfades.
- Refined home: two-line shelf titles, a larger layout toggle and stable shelf identities when recent history arrives.
- Shared the home loading clock and cached shimmer brushes; fetch optional shelves after the core feed with bounded concurrency.
- Preserve media caches across cosmetic version updates.
- Allow guest Listen Together creation, joining, invite previews and server switching. Guest IDs are installation-scoped and nicknames fit the server's UTF-8 limit.
- Added guest identity and REST authorization regression coverage. Isolated backend test server rate limits.

Current validation: `verification/BETA3-RESULTS.md`. Full Android build and device performance validation remain blocked/unrun as documented; no verified beta3 APK is included.

# Veyra 0.1.0-beta2 (source update)

- Rebuilt settings navigation around six categories and a persistent global search field.
- Added multi-word settings search, a useful no-results action, and category back navigation.
- Added a complete bold Manrope type scale, lavender theme accents and more readable setting values.
- Enlarged search touch targets, allowed larger text, and marked section headings for accessibility.
- Added subtle navigation transitions that respect reduced animation.
- Moved the playing indicator to draw-only animation with a static reduced-motion state.
- Cached library sorting, shelf matching and song-row metadata; filtered unnecessary settings telemetry updates.
- Removed inherited Ko-fi funding configuration; the supplied Android UI already contained no UPI or promotional social actions.
- Updated current Android SDK package selection and fork-specific GitHub workflow behavior.

Validation and build limitations: `verification/POLISH-RESULTS.md`. This is a source handoff, not a verified release APK.

# Veyra 0.1.0-beta1

- Selected icon 3: cream and peach folded V, lavender wave and purple background.
- Android launcher, themed icon, notifications, widgets, TV banner and web assets updated.
- App branding and translated strings renamed to Veyra.
- Independent application IDs, URI scheme, native library names and JNI exports.
- Manrope typography, stronger compact labels and clearer secondary text in dark mode.
- Original playback, downloads, audio processing and service integrations preserved.
- App update checks require an explicitly configured Veyra release feed.
- Optional Discord application ID override for a separately registered Veyra identity.
- Source audit and build guide included; check verification/RESULTS.md for test status.

Original GPL and third-party notices remain in the source distribution.
