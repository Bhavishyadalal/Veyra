# Settings control renderer and interaction checks

This independent verification project compiles **the production** `SettingsControls.kt`, `SettingsDialogControls.kt`, `PillTextField.kt`, `VeyraSwitchColors.kt` and `SettingsIcons.kt` with Kotlin 2.3.20 and Compose Desktop. It is not included in the Android application's Gradle module list and adds no runtime dependency to the app.

From the Veyra project root, with JDK 17 available:

```sh
./gradlew -p verification/settings-preview run --max-workers=2
```

The verified host is Linux x64. The harness currently targets that host's Compose/Skia runtime. Dependencies must be available from Maven Central/Google on the first run; after caching, `--offline` works. Normal HTTPS certificate verification remains enabled. No emulator, display server, APK, network music service or credentials are required.

The command renders nine PNGs into `design/settings-preview/` and fails if any component check fails. Checks exercise switch roles and callbacks, disabled states, separate editor/toggle actions, radio touch sizes, selection callbacks, accessible slider adjustment, drag/release persistence, dialog actions, labeled text entry, editor size and password semantics. The font family and type/color declarations in the harness mirror the current app theme; refresh those declarations if the app theme changes.

These are representative component arrangements with sample copy and states, **not screenshots of the complete Android app**. They deliberately cover dark/light themes, 320dp width at 1.6× text size, RTL layout, selectors, fields, dialog actions and icon choices. The icon images and fonts are actual bundled app assets. The full app's search/navigation shell, Android IME, document pickers, source servers, launcher alias changes and playback integration still require Android integration/device checks.

`ImageComposeScene` renders on Skia and exposes the real Compose semantics tree; the runner invokes those actions and sends pointer events to the slider. It does not replace UI controls with HTML or raster mockups.

The Google account preview now uses the same `SettingsAccountSummary` called by production `AccountCard`. An additional check invokes its host callback and verifies that the entry contains no editable fields. The separately titled Last.fm scene demonstrates the existing optional service credentials. Google, Spotify and Discord browser/session flows are not reproduced by this renderer.
