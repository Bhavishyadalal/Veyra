# Veyra 0.1.3 source — silver waveform welcome

Based on the existing 0.1.2 app source. Version name 0.1.3, version code 16, no beta suffix. This is source for Android Studio, not a generated or verified installable release.

## Welcome behavior and design

- Add a dedicated landing before name entry: actual Veyra icon/wordmark, generated silver liquid-glass waveform, bold native Your music / Your space heading, supporting copy and a prominent silver Get started capsule with a native directional arrow. Restore-backup UI is not added.
- Three optional previews cover music, lyrics and Listening Together. Swipe the artwork, tap its chip or use the 48dp page indicators. Get started always goes directly to name entry, regardless of the selected preview. There is no auto-advance timer or mandatory tour completion.
- Seven new paging regressions cover wraparound, swipe threshold, LTR/RTL direction, previous navigation and invalid gesture input. The helper is production code used by the welcome gesture handler.
- The waveform is local, asynchronously decoded by the existing Coil loader and alpha-masked on all four edges. A finite initial reveal and small preview-triggered movement respect reduced motion, system animation scale and foreground state. No live blur, video, image server, new library or endless animation is added. The old two framed welcome-art resources are removed.
- Native text and controls provide the actual UI; the generated bitmap contains no typography or controls. Actual launcher artwork and the owner's authorized brand font remain unchanged.
- Keep saveable page/name/preview state, trimmed nonempty names, keyboard-aware compact layout, scrolling for short screens/large text, RTL transitions, explicit back controls and optional account linking/guest completion. Existing users are not forced to repeat onboarding.

## Home and Settings

Home's title gains stronger weight and a small native waveform accent that follows the existing finite reveal. Feed contents, artwork caching, playback and account actions are retained. Settings gains a clearer heading with heading semantics, aligned introductory copy and stronger category destinations. Existing plain rows, global search, category navigation, GitHub links and preferences remain. Their appearance still follows the selected theme.

## Checks completed

- 57 focused Kotlin/JUnit tests passed, zero failures, including the seven new actual paging-helper tests. This is not the full Android unit suite or a Compose UI suite.
- Kotlin PSI parsed seven changed/new Kotlin/build files, zero syntax errors. Parsing does not establish Android/Compose type correctness.
- Resource audit: 114 XML documents, 22,632 localized entries, eight font references, zero errors. Android SDK 36 AAPT2 compiled all app resources after the unused-art cleanup, including the new WebP and strings.
- Source/import review and git whitespace checks passed. The generated waveform asset was visually inspected. No native UI render or screenshot is claimed.
- Android Studio ZIP is CRC checked and compared with the current source. It retains the Gradle wrapper and phone app/shared/sharedUi modules, without TV/wear modules, recursive downloads, build/cache outputs or private SDK/signing configuration.

## Build limitation

Retried :app:compileProdDebugKotlin and :app:testProdDebugUnitTest with installed JDK 17/Android SDK, two workers, normal HTTPS proxy and CA trust. It stops at :app:stripNewPipeExtractorUtils before Kotlin compilation: JitPack returns HTTP 403 for com.github.TeamNewPipe:NewPipeExtractor:v0.26.3. No TLS, artifact-verification or dependency-source bypass is used. Full Android compilation and integration/UI tests remain unverified.

No APK/assemble task, signing, release publication, emulator/physical-device UI test or frame-time measurement was run. Device follow-up: first run, all three tap/swipe previews, cancelled/short/RTL gestures, Get started from each preview, name entry/IME, Back, rotation, large fonts, TalkBack, disabled animations, guest completion and optional sign-in. Check Home header wrapping and Settings category/search/links on narrow and landscape screens. Existing installed users should retain library/preferences and avoid a forced welcome reset.
