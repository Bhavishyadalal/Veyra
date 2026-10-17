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
