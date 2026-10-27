# Stable 0.2.0 · build 51

Navigation/rendering review following the reported Sources → Playback Settings
glitch. Version name remains **0.2.0**; Android version code is **51**.

## Sources → Back: two related faults

Settings sub-screens are overlays above a retained Settings page. Their old
animation reused the full-page navigation transform. With custom motion or
downloaded materials, Back faded/moved an **empty incoming slot**, held Sources
on screen for the transition duration, then removed it abruptly.

`pageOverlayTransition` now treats dismissal as an outgoing-panel operation:
no incoming animation, the Sources panel exits above its retained parent,
and viewport size interpolation stays disabled. Custom motion/tempo, RTL,
subtle motion and reduced motion remain supported. Original full-page route
transitions are preserved.

The retained Settings page also kept collecting its many state streams and
drawing its theme behind an opaque Sources page. `PageLifetime` now caps covered
and outgoing pages at `CREATED`, so lifecycle-aware collectors stop. Outgoing
pages lose pointer/accessibility actions. Their UI state stays mounted.
Fully covered Settings is hidden only **after** overlay entry finishes and is
shown again **before** Back dismissal begins. That preserves scroll/category
state without a blank parent or a fresh Settings entrance animation.

The content backdrop capture now encloses both route content and Settings
overlays once. Top/bottom floating controls remain outside the capture. Glass
therefore samples the actual foreground page instead of the covered Settings
page, without registering competing capture layers.

## Other concrete fixes

- Source health/manifest probes stop when their page is inactive or its lifecycle
  is below `STARTED`, including during dismissal. A late discovered add-on name
  merges into the **current** config, preserving changed enable/download/lossless
  switches. Replaced URLs and newer labels reject the old name result.
- Welcome artwork height adapts to available viewport height. The existing
  silver artwork is feathered with foreground gradients instead of a large
  offscreen alpha-mask layer. One heading reveal replaces separate staggered
  jobs per word; natural text wrapping/accessibility remain.
- Welcome stage changes prevent outgoing-page input and avoid viewport-size
  interpolation. The landing preview text has a stable minimum area, and account
  optionality is explained alongside Get started. Short/large-text screens can
  still scroll; name validation and optional Google sign-in are preserved.
- Music pages leave composition while welcome or the sign-in WebView covers
  them, reusing the existing saveable-page wrapper. They return when uncovered.
- Immersive mode no longer requests the unused ordinary-player artwork mesh,
  avoiding its extra image decode and pixel readback on track changes. All
  twelve immersive modes and their actual playback callbacks remain intact.

## Checks and limits

Focused tests cover all motion styles/tempos, RTL, representative theme materials
and reduced motion; rendered intermediate/final dismissal pixels; retaining the
same Settings instance; stopping/resuming lifecycle collectors; outgoing-input
blocking and reactivation; source-name merge races; navigation policy and Settings
Back/save-restore behavior; welcome preview paging; saved covered-page UI; and
buffering transport behavior. Android production Kotlin compilation and production
debug/release version metadata are checked. Test dependency cache misses were
resolved through the configured proxy and verified trust store; no dependency
versions or TLS verification settings were changed in the project.

**29 focused tests passed** with zero failures/errors/skips: 23 Android unit tests
and six shared-UI/offscreen checks. Production Kotlin compilation passed;
production debug and release metadata both identify **0.2.0 / code 51**.

The source ZIP is regenerated and all four retained archives/checksums/backend
snapshots verified before publication. Matching website source/build/typecheck
and mobile/desktop links are updated. Existing theme/overlay pack bytes, native
launcher resources, audio engine, backend, preview branches and release assets
are unchanged.

This is a source review with focused unit/offscreen checks, not a physical-device
frame-time benchmark, Android onboarding screenshot run, signed APK assembly or
proof that every possible app bug is eliminated. Device follow-up should exercise
Sources → Back repeatedly with the affected theme/motion/glass settings, fast Back
reversals, foreground/background changes, rotation, large fonts and onboarding
with the keyboard. Rebuild/install code 51 with the existing signing key.
