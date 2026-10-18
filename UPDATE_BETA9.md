# Veyra 0.1.0-beta9 · Switch visibility and welcome-screen performance

## Fixes

- All 45 phone switches now share explicit checked, unchecked and disabled colours. Off-state thumbs use the theme's high-contrast foreground instead of the dark outline colour. Tracks and borders remain visible in both light and dark themes. Disabled switches stay distinguishable and do not appear enabled.
- The first-launch name and sign-in screens now use a static cached gradient instead of a continuously orbiting, palette-cycling, full-screen blurred mesh. Brushes are reused until the drawing size changes, avoiding persistent background animation while typing.
- Name validation, saving, keyboard behaviour, account connection and page transitions are retained. Switch states, click handlers and stored preferences are unchanged.
- The beta8 icon choices, Instagram link, navigation order and launch signature are retained.

## Source-only delivery

This update intentionally contains no APK. Validation runs Kotlin compilation and existing unit tests only, without an APK assembly, dexing, minification, packaging or signing task. These checks do not measure frame times on a physical phone; please test the welcome screen and switches after your next build.

Import this ZIP into your personal Veyra Builder when you want to build it. Keep the same Tools/signing folder or restore your own signing backup to update your current installation. Version code is 9 and the version label is 0.1.0-beta9.

Previous beta8 build reports and documentation are retained as historical records. VERIFIED_BETA9.json records the checks for this source-only update; it does not claim an APK has been built.
