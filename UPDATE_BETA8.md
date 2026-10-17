# Veyra 0.1.0-beta8 · Glass identity update

## What changed

- Glass Noir is the default launcher icon, based on the approved monochrome waveform with a liquid glass frame.
- Added White Frost and Glass Orbit. All 13 previous icon designs remain available, including Original Veyra. There are 16 choices in total.
- Appearance settings now contain a responsive icon grid with clear selection borders, accessible radio-button selection, and an icon-change confirmation.
- Launcher icon switching uses Android's atomic component update on Android 13+, and enable-first switching with rollback on older versions. Existing alias names are preserved for upgrades.
- Navigation order: Home, Search, Explore, Library. The sign-in callback now uses the Library destination constant instead of an old numeric index.
- Settings categories and the creator link use custom SVG path icons, compiled as native Compose vector graphics. Existing setting-row glyphs are native vectors too. Editable SVG originals are in design/settings-svg.
- Settings includes the Instagram camera icon and creator link for @bhavishya_dalal_. The link opens Instagram or the installed browser; an unavailable handler produces a message.
- A 520 ms foreground entrance animates the new brand mark with a fade/scale; it does not delay backend loading or music playback. It can be dismissed by tapping, and skips when Reduce animation or system-disabled animations is active.
- Android 12+ starting windows use a matching monochrome waveform. The new artwork includes adaptive masks and themed-icon resources.

## Build / update

Import this ZIP into your personal Veyra Builder. Keep your current Tools folder or import your existing signing backup, use Prod / Release, ARM64, Java 17 and one worker. Version code is 8; app version is 0.1.0-beta8 because this visual update still needs testing on your actual phone. Removing a beta label does not certify runtime stability.

To update your existing installed Veyra without losing its data, use the same signing key as that installation. An APK signed with a different key cannot replace it. Rebuilding this source through your own builder uses your own key. Do not uninstall your current app just to work around a key mismatch; rebuild with the matching key instead.

In Android Studio, open the extracted Veyra folder containing settings.gradle.kts. Existing build instructions in START_HERE.md apply. No Android Studio dependency or toolchain versions were changed by this update.

## Scope and practical limits

Playback, downloads, music sources, library database and network integrations were retained. The release build and automated unit tests are checked separately. Launcher refresh timing, OEM icon masks, permission dialogs, background/resume behavior and animation smoothness must also be checked on a real Android device. Neither a successful build nor JVM tests guarantees every phone's runtime behavior.

## Assets

The generated brand art and its matching variants are in design/launcher. Android density versions are in app/src/main/res/mipmap-*; full-size design PNGs are for future edits and are not bundled in the APK. Existing artwork and notices remain in the source.

The retained build toolchain emits Kotlin metadata compatibility diagnostics from R8 and lint. They did not prevent the release APK build; lint coverage for newer Kotlin metadata may be limited. This visual update does not change Kotlin, AGP or R8 versions.
