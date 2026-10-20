# Veyra 0.1.7-beta1 · Theme Studio verification

Preview **0.1.7-beta1 / code 23** only. Stable **0.1.6 / code 22** remains separate.
No APK build or Actions dispatch is part of this update.

## Implemented

- Settings → Appearance → Theme studio with a public catalog, themed previews,
  bounded/cancelable downloads and explicit Apply confirmation.
- Data-only ZIP installation in app-private storage. Hashes, paths, schema/app
  version, file/expanded limits, color contrast, shape bounds, Android image
  dimensions and font loading are checked before publishing an installation.
- Installed themes work offline. Corrupt saved packs fall back to Original Veyra;
  failed verification preserves the prior installation. Temporary/old staging
  folders are cleaned on startup. Theme downloads do not activate themselves.
- Updated theme versions remain separate from the active definition until Apply.
  Active packs cannot be removed. Inactive versions can be removed with confirmation.
- Theme activation persists only in its own preference namespace. The Activity
  recreates on confirmation; no playback-service stop or process exit is called.
- Shared native colors, typography, supported icon slots, navigation geometry,
  player button surfaces and an ember timeline. Original music artwork, service
  logos, callbacks, permission/account requirements and touch semantics remain.
- Original Veyra restores the saved base appearance. Existing font-scale,
  high-contrast and reduced-motion preferences continue to work.

The Halloween art is original AI-assisted artwork; all theme vectors are original.
The two font files retain their SIL Open Font License notices in the pack. Theme
assets live outside Android resources, so the artwork/fonts are downloaded on
selection rather than bundled into every APK. Only catalog metadata is bundled.

## Refinement after device feedback

The screenshot showed a burgundy song menu because artwork-derived palettes
bypassed the theme. Active themes now return their validated palette before
cover decoding, and artwork backdrops use the local themed scene. Original mode
retains the existing cover-derived palette. The menu keeps its original actions,
callbacks, enabled/busy rules and saveable sleep-timer/scroll behavior.

Main app and Settings surfaces now draw local forest artwork, a dark readability
scrim and a static sparse constellation. No animations or network requests were
added to these layers. They expose no interactive semantics or pointer handlers.
Shared thumbnail borders and actual HomeArtworkFrame components use amber edges;
Home headings get decorative rules. Settings icons and song menu icons share
sculpted housings. The native glass navigation surface retains its implementation.

The current revision compiles Android release Kotlin and runs 18 focused JVM
tests: pack validation (10), player quality menu (4), Settings navigation (4).
Six native studies cover Home, player, Settings, large-text Settings and normal /
large-text action components. Action rows in these desktop studies are illustrative;
the background and icon housings are the actual shared components. The real Android
sheet is not rendered by this desktop tool. Theme v1 remains immutable; v2 is a
separate checked pack with the same supported manifest schema.

## Initial implementation checks

- Android release Kotlin compilation and **30 focused JVM tests**, no failures:
  theme pack (10), Settings navigation (4), navigation transitions (4), existing
  customization (3), player quality menu (4), Settings search (5).
- Theme tests exercise the actual checked-in pack and rejection of corrupt outer
  data, archive traversal, extra entries, changed asset hashes, incompatible
  versions, unreadable colors, unbounded geometry and oversized streams. A
  decoder-rejection callback leaves an existing installation untouched.
- Four native Compose component studies: Home, player, Settings and 320 dp /
  150% text Settings. The themed transport's real semantics action delivers its
  callback once and retains at least a 48 dp target. Rendering exposed and led
  to fixes for a clipped sample footer and a fixed-height theme-header text crop.
- Offline preview gallery checked through a local HTTP server at desktop/mobile
  viewport sizes: every image loaded, no horizontal overflow or page errors.
- Website build/type checks and both mobile/desktop download-link checks passed.
- Source ZIP integrity, per-file manifests, embedded theme pack/checksum, archive
  retention and the mirrored backend manifest are verified before publishing.

## Limits and device follow-up

The native images are **component studies**, not complete Android screen captures.
They use actual shared components inside illustrative screen compositions; the
sample player's cover is demonstration art. The app keeps the real track cover.
No emulator or physical device test was available for download cancellation,
Android font decoding, Activity recreation during playback or full-screen layout.
JVM tests do not replace those Android integration checks or frame profiling.

On the next signed beta, check: download/cancel/retry; install while a song plays;
Later versus Apply; rotate/background/kill/reopen; offline activation; restore
Original; remove an inactive theme; navigate every main tab and Settings category;
queue, lyrics and locked Listen Together controls; large fonts and RTL; and
motion Off. Google/Spotify web sign-in pages retain the providers' own UI.
