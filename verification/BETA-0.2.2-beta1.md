# Beta 0.2.2-beta1 · code 58

Created from stable 0.2.1 at the owner's explicit choice. Build 57 established the track; build 58 adds the changes below. Stable main/stable/0.2.1 remains code 56. Completed older previews and published theme/overlay ZIPs are unchanged.

## Search focus

Android navigation no longer creates a pending focus request, clears a query on a Search tap, or asks the IME to open. The Search startup page and Home shortcut also enter without requesting focus. Tab selection clears existing focus and hides the IME. The obsolete Settings toggle is removed; its serialized field remains compatible with old backups but is excluded from root behavior. Explicit field taps still use the normal editable field.

## Appearance preparation

A process-owned IO runner waits for local theme/asset restoration, prepares six bundled resource fonts through Android's font cache, and initializes the shared image loader/launcher selection. It runs no network requests and does not initialize every optional visual pack. The first activity launch for an unprepared build displays completed stages and elapsed time, with immediate Continue. Continue/rotation do not cancel process-owned work. Success is persisted only after all stages complete; failure permits retry and continuation. The visible elapsed timer stops when covered/backgrounded or dismissed.

The screen cannot predict a phone's completion time. The copy gives a general expectation of a few seconds and changes after 15 elapsed seconds; this is not a measured duration or countdown. Existing mandatory Application initialization still happens before the activity. This is appearance warming, not ART compilation, and is not evidence of permanent 60/90 fps.

## Runtime work reduced

The standard/tonal bottom-bar pill now reads travel and derives stretch/squash in its graphics layer. A shared production modifier preserves the same translation and scale formulas. A render regression checks that changing travel neither recomposes nor rerecords its content. The folding-bar accessory fade also reads alpha in its graphics layer instead of recomposing the accessory each animation frame.

Navigation Studio uses keyed lazy preview rows. Column count follows sheet width and font scale; motion, destinations, startup-page and label controls remain scrollable. Inactive pack fonts retain file hash/header validation and are fully parsed before activation; only the selected pack warms its typefaces at restoration. This avoids evicting active typefaces with unrelated installed fonts. Installed asset hashes use streaming buffers capped at 64 KiB, retaining file-size and digest checks. The selected folder is scanned first; remaining folders yield between packs.

Source packaging uses compact JSON for SOURCE_MANIFEST.json, preserving the complete fields, file list, hashes and native/authoring inputs while keeping the beta archive below GitHub's 100 MiB limit.

## Verification

- Android release Kotlin compilation and resource processing pass.
- Full Android release unit suite: 1,304 tests, zero failures/errors, one existing skip. Includes preparation completion/progress/idempotence, cancellation, retry/storage failure, streamed-file integrity and legacy preference coverage.
- Full shared UI JVM suite: 93 tests, zero failures/errors, including layer-only pill travel and existing immersive/render/lifecycle/search checks.
- Website production build and TypeScript checks pass; 10 desktop/mobile library tests pass, including beta build information, source/checksum links, search, keyboard navigation, accessibility and narrow-screen layout.

No physical Android device or emulator is attached. Local/offscreen tests cannot establish device frame rates, thermal behavior, every keyboard implementation or cold-launch duration. The owner is testing a realme Narzo 60 5G with a 90 Hz display; sustained 90 fps requires a frame budget of about 11.1 ms. Test on that phone with the existing supported 90 Hz performance preference and the same effects enabled.

## Phone checklist

1. Upgrade stable build 56 with the same signing identity. On first launch, observe preparation, Continue immediately, rotate, background/reopen and confirm completion is not repeatedly shown after success. Repeat with many installed themes and slow/full storage; test Retry/Continue after a preparation failure.
2. Tap Search from each navigation layout, reselect Search while typing, switch tabs and return, and choose Search as startup page. Verify no automatic keyboard, preserved draft/results, and normal typing on a field tap. Import an old backup with searchKeyboard enabled and repeat.
3. Open Navigation Studio at small width, large text, RTL and landscape. Scroll to all 16 designs and to the final controls, apply styles and test dismissal.
4. Compare navigation, scrolling, player opening and all immersive scenes with the same effects on the same phone. Capture Android frame-timing traces at supported 60/90 Hz, with music/lyrics active, and check sustained heat/battery behavior. Refresh-rate preference does not force a frame-rate guarantee.
5. Restart with several installed themes/overlays, apply an inactive one and verify fonts/icons, then verify corrupted files are rejected and existing downloads/playback remain intact.
