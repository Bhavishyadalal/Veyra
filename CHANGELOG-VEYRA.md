# Veyra 0.1.6-beta1 · saved, personal and offline Home

- Restore a bounded per-listener Home snapshot before network refresh; preserve useful content during refresh/failure. No auth cookies, stream URLs or paging tokens are persisted in snapshots.
- Add native section ordering, visibility and reset controls with saved per-listener preferences, accessible actions and reduced-motion handling.
- Add offline Home messaging and existing Downloads/Local Music entry points; downloaded cached tracks use verified local files and existing playback guards.
- Keep source/runtime compatibility with Veyra's independently hosted backend and shared external cron. Stable playback/download/Settings/Listen Together sources remain unchanged.
- Preview 0.1.6-beta1/code 21 on its own branch. No APK or stable merge. See verification/HOME-0.1.6-beta1-RESULTS.md for 84 focused tests, native component checks and the remaining full Android build/device limits.

# Veyra 0.1.5 · preview refinements promoted to stable

- Promote the complete preview Settings, player and Home presentation to stable main, including reusable controls, pressed/loading feedback, seek cancellation, reduced-motion behavior, artwork fallbacks and stable feed identities.
- Preserve optional Google/YouTube Music sign-in, guest Listen Together, integrations, playback callbacks, downloads and saved preferences. No replacement Google username/password form.
- Keep Veyra-owned room synchronization, invites, presence and the shared external cron/12-minute keep-alive setup.
- Remove the beta suffix. Version is 0.1.5, code 20, allowing upgrades from beta code 18 and earlier stable code 19 with the same package/signing identity.
- Keep historical preview branches and source archives. See verification/STABLE-0.1.5-PROMOTION.md for current checks and build limitations. No APK or release is published by this source promotion.

# Veyra 0.1.5 (independently hosted Listen Together)

- Move room synchronization, public invites, Android invite host and presence to Veyra's own Render service.
- Migrate old managed server preferences before probing. Discard old room codes/tokens and obsolete kick state without contacting the retired service or sending old tokens to the new service. Preserve deliberate custom servers.
- Refuse retired-server invites with a new-invite message; pin official invites to the official server and reject malformed explicit server parameters.
- Preserve guest access, optional Google profiles, package identity, signing configuration and the stable 0.1.3 UI foundation. Preview UI changes remain separate on preview/0.1.5-beta1.
- Promote source to main after the owner reported testing 0.1.5 successfully, including Listen Together room-code generation. This report does not independently establish every playback/reconnection/upgrade scenario.
- The separate backend repository now includes a best-effort 12-minute health/keep-alive workflow. GitHub scheduling and Render's free plan do not guarantee continuous availability; restarts still lose rooms.
- Local migration tests and focused checks pass as documented. Full Android compilation and independent live-server checks remain blocked in this environment. No new APK, signing or GitHub release is performed here.

# Veyra 0.1.3 (silver waveform welcome)

- Add a genuine welcome landing inspired by the owner's reference: original silver-wave artwork, actual app branding, bold native copy, selectable/swipeable previews and a silver Get started action. No restore-backup UI.
- Get started skips straight to name entry from any preview. Preserve optional account linking, guest completion, name validation, Back, compact/scrolling layouts, RTL and reduced motion.
- Replace the old framed welcome illustrations with one smaller local waveform image and cached alpha masks. No continuous effects or new dependency.
- Refine Home's header and native waveform accent; strengthen Settings heading/category hierarchy while retaining plain rows, search, links and existing actions.
- 57 focused tests pass (seven new gesture/paging cases), plus AAPT2 resource compilation and seven-file syntax parsing. Full Android compilation remains blocked by JitPack HTTP 403; device UI checks remain pending. No APK tasks run.
- Stable-series source version 0.1.3, code 16. Package identity, signing, player and dependency versions retained.

# Veyra 0.1.2 (welcome campaign and creator links)

- Integrate the original glass portal and crystal-waveform campaign images into both welcome steps as small local WebP assets, decoded by the existing Coil loader.
- Polish welcome's neutral/champagne palette, finite artwork reveal and clear optional account-linking copy. Keep keyboard-aware name entry, guest completion, RTL, large-font scrolling and reduced-motion support.
- Add searchable Settings links to Bhavishyadalal's GitHub profile, Veyra app repository and Veyra-website source repository, using native icons and the existing link error handling.
- Publish the complete website project in its separate GitHub repository. No website deployment or APK generation.
- 50 focused Kotlin tests, resource audit, AAPT2 compilation and four-file syntax parsing pass. Full Android Kotlin compilation remains blocked by JitPack HTTP 403; on-device UI verification remains required. See verification/STABLE-0.1.2-RESULTS.md.
- versionCode 15, stable versionName 0.1.2; package identity, signing and dependencies unchanged.

# Veyra 0.1.1 (JioSaavn defaults and player refinement)

- Enable JioSaavn on fresh installs and replace the old forced-off migration with a one-time enabled-default correction. Persist source state and the new marker together; preserve subsequent off/on choices, addons, source order, matching and quality gates.
- Clarify player titles/artists, retain outgoing recording metadata and links during 220 ms credit fades, improve control-area contrast and keep the established bold white transport glyphs with pressed feedback.
- Give seek, volume and bottom actions 48dp targets, preserve the hidden-volume footprint and align timing labels to the enlarged seek target. Clean up interrupted drags and ignore disabled slider gestures.
- Respect reduced motion in credit changes, queue capsules and slider effects. Use a static loading treatment instead of an animated sheen in that mode; shorten the queue capsule slide and remove slider-height overshoot.
- 50 focused tests pass, including four new production source-policy tests. Existing SourcesTest integration cases are updated; the full suite remains blocked by JitPack HTTP 403. Details: verification/STABLE-0.1.1-RESULTS.md. No APK task run.
- Based on the user-supplied 0.1.0 Stable source; versionCode 14, stable versionName 0.1.1. Build tools, application identity, data and signing configuration are retained.

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
