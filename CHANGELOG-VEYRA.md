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
