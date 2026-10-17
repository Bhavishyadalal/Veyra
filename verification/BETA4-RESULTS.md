# Beta4 design and validation — 2026-10-17

## Visible changes

- The main player uses an 80dp filled lavender play/pause button (64dp in compact layout) with a contrasting glyph. Previous/next are distinct circular controls. Loading keeps the same primary button footprint.
- The portrait control deck has a 30dp rounded surface, static translucent gradient and a fine border. The song title is 26sp, and the credit band adapts to system font scaling. Artwork corners are 24dp when presented as a card; existing full-bleed/canvas behavior is retained.
- Home opens with a 30dp rounded tonal hero containing the Veyra mark, page heading and localized supporting caption.
- Settings opens with a tonal introduction and two-column category cards when the inner width is at least 328dp and font scale is at most 1.2. Otherwise it uses full-width category rows. Search, all category destinations and the original controls remain.
- Regular bottom navigation has a rounded outer surface, solid tonal selection capsule, stronger labels and selectable-tab semantics. Glass navigation uses matching selection colors and an accented selected Search button.

These changes add no image/font dependency, continuous background animation or new blur pass. Existing guest Listen Together and home-loading changes are retained.

## Passed here

- **15 focused Kotlin/JUnit tests** against real production helpers: five navigation geometry tests, five guest identity tests and five settings search tests. Reproduce with `GRADLE_USER_HOME=<cache> python verification/run_focused_kotlin_tests.py` after bootstrapping the project wrapper. This harness uses Gradle 8.11.1's Kotlin 2.0.20 compiler and JUnit 4.13.2, independently of the Android dependency graph.
- Navigation tests cover swipe direction, limits, right-to-left indicator placement, missing/invalid measurements and invalid selections. The regular bar reads the latest selection/callback during a gesture and places its indicator from the physical left.
- Kotlin PSI syntax parsing: **24 changed/new files, zero errors**, compared with the original beta1 archive. Parsing is not Compose/Android type checking.
- Resource audit: **137 XML documents, 23,151 localized entries, eight font references, zero errors**.
- The Settings caption is present once in all **19** Android catalogs, and the Home caption is present once in all **18** shared UI catalogs.
- Git whitespace validation passed. Duplicate imports in modified Kotlin files were removed.

## Full Android build remains blocked

Attempted:

```sh
./gradlew :app:compileProdDebugKotlin :app:testProdDebugUnitTest :app:assembleProdDebug --no-daemon --max-workers=2
```

Gradle failed before Kotlin compilation because the cloud proxy returned **HTTP 403 for jitpack.io** when resolving NewPipeExtractor v0.26.3. The existing JitPack requirement in environment settings must be applied before retrying. Verification was not disabled, and dependencies were not removed or substituted to make checks pass.

**No beta4 APK, full Android compile, complete Android unit suite, lint result, emulator screenshot or physical-device result is claimed.** The successful Android Studio build report imported with the user's latest beta3 ZIP describes beta3 only. Backend code is unchanged in beta4, and its tests were not rerun in this revision.

## Device review before release

Build with an environment that can reach the required repositories, then review:

1. Player: portrait, compact landscape, artwork/canvas, light/dark app themes, playback loading, queue/lyrics overlays, volume, seeking, title links and larger fonts.
2. Home: the new top card with signed-in and guest feeds, long translated headings, refresh and scroll.
3. Settings: grid at normal font size, full-width rows at large font size/narrow widths, every destination, search and Back.
4. Navigation: regular and glass modes, taps, swipes, selection after state changes, RTL/Hebrew layout, TalkBack and reduced animation.
5. Existing guest parties, playback and reconnection behavior. Profile on a phone before claiming any frame-rate or startup improvement.

The source ZIP uses versionCode 4 and versionName 0.1.0-beta4. Open the extracted Veyra root as described in START_HERE.md. Old downloadable archives, machine configuration, caches, SDKs, private keys and build outputs are excluded from the source ZIP.
