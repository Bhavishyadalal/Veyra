# Settings refinement — 19 October 2026

Source update on the 0.1.3/code 16 base. This does not publish a new binary release or alter package/signing identity.

## Design and interaction changes

- Extract the reusable settings controls into `SettingsControls.kt`. Apply stronger page/section headings, open full-width rows, natural text wrapping, quieter separators, readable current values, status capsules, rounded chevrons and consistent icon spacing across settings destinations. No card around every setting, image-backed control, live blur, new app dependency or endless decorative animation.
- Migrate 42 row switches across Settings, Account, Discord, Equalizer and Listen Together. Simple toggles have one full-row switch action; four integration rows deliberately retain separate editor/account actions and labeled switch callbacks. Disabling an integration does not become signing out, and editing a Discord button does not become a visibility toggle.
- Preserve the existing setters, confirmation gates, guest listening-together entry, source order/quality rules, defaults, preference keys and existing data. Supported/unsupported states keep their explanations readable. Destructive cache/sign-out actions use error color, with existing confirmations retained.
- Rebuild the three quality/performance option sheets around selectable radio groups and a common header. They scroll on short screens, retain cost/context for unselected choices, and preserve immediate selection and dismissal.
- Six slider callers show a local draft while dragging and persist once on release. This includes crossfade, cache size, source matching and the three scrobbling timing controls. Accessibility adjustment also commits successfully. Values still use the original units, limits, steps and setters.
- Selectors stack vertically at narrow widths or enlarged text instead of clipping labels. Main/sub rows move trailing controls below their copy when the narrow, enlarged-text layout needs it. Navigation chevrons mirror in RTL.
- App-icon options use outlines around the artwork and a clear selection mark instead of boxed tiles. Columns adapt to width and font scale; existing launcher aliases, apply/rollback logic and feedback remain.
- Shared alert frames use a 340dp maximum requested width, constrained by screen gutters, with 24dp corners. Shared alert actions are at least 56dp tall and grow with text. Account/source forms scroll above the keyboard; language lists take remaining height and expose radio selection semantics. Account channel lists scroll. Syllable synchronization exposes switch semantics.
- Extract and refine the shared `PillTextField`: a 52dp minimum editable area, natural height growth, focused outline, accessible label retained after typing, native IME options and password masking. Backup import/export show progress text, prevent concurrent operations while working and restore enabled state in `finally`.

## Verification

- The standalone Compose renderer compiles actual production controls and runs **16 component interaction checks**. It renders seven scenes: dark, light, RTL, 320dp/1.6× text, overview, radio choices, and forms/icon selection. These were visually reviewed; review exposed and corrected narrow large-text wrapping, RTL chevron direction and the editor semantics target size. The scenes are not complete Android app screenshots.
- **57 existing Android-independent Kotlin/JUnit regressions passed**, including Settings search and source enablement policy. This is not the full app unit suite.
- Kotlin PSI syntax parsing: 17 changed/new Kotlin files, zero syntax errors. Syntax parsing does not establish full Android type correctness.
- Android SDK 36 AAPT2 resource compilation passed. Resource audit: 114 XML files, 22,633 localized entries and eight font references; zero errors. New backup progress copy falls back to English where not translated.
- Kotlin PSI comparison against baseline `8a03bb7` verified that all 88 existing SettingsRow title/action pairs across six screens are unchanged. The four independently actionable integration switches were additionally reviewed against their original callbacks. This is a source-preservation check, not proof that external services work.
- Git whitespace checks passed. Component rendering and checks are reproducible with `./gradlew -p verification/settings-preview run` on the verified Linux x64 host.

## Remaining Android checks

The full `:app:compileProdDebugKotlin` attempt stops before Kotlin compilation at `:app:stripNewPipeExtractorUtils`: JitPack returns HTTP 403 for `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. The environment's HTTPS proxy and trusted CA were used; no artifact substitution, TLS bypass or disabled verification was used. Full app compilation, Android UI/instrumentation tests and a physical-device run remain unverified. No APK/assemble/sign/release tasks were run.

Device follow-up: navigate every category and global search; test TalkBack/keyboard traversal and enlarged text; toggle disabled/available features; confirm high-performance and cache prompts; check manual/automatic audio modes; exercise Wi-Fi/download quality sheets; source enablement, matching and reordering; Last.fm/ListenBrainz/Discord edit versus enable behavior; icon changes and rotation; IME and scrolling in source/account dialogs; successful, failed and cancelled backup pickers. Check slider behavior during actual playback and persistence across restart. Network service responses and file/launcher/system-settings integrations are not proven by the component harness.
