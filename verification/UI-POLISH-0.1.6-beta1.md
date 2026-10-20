# Veyra 0.1.6-beta1 · Interface and motion polish

This is a preview-only update on `preview/0.1.6-beta1`, retaining version
**0.1.6-beta1 / code 21**. Stable app code on `main` stays **0.1.5 / code 20**.
Made by Bhavishya Dalal.

Latest follow-up: [compact layouts, readability and buffering alignment](ACCESSIBILITY-0.1.6-beta1.md).

## What changed

- **Shared surfaces:** Settings, track actions and player drawers share row/sheet
  geometry. Menu controls keep at least 48 dp touch targets. Existing bundled
  fonts, theme colors and artwork palettes remain the visual foundation.
- **Navigation:** short eased forward/back movement, restrained tab fades, and
  correct RTL direction. Full-screen destination size changes snap instead of
  interpolating viewport sizes. Existing opaque surfaces and retained pages stay.
- **Home to detail:** release headers reuse a cached card image as a placeholder
  while loading the larger header image. The request is remembered and decoded
  at the established header size. This is cached artwork continuity, not a
  claim of a new shared-element geometry animation or guaranteed cache hit.
- **Settings:** immediate tonal press feedback and a short section fade/lift.
  The initial Settings composition no longer fades out and back in. Dependent
  crossfade, animated-artwork, signature-API and refresh-rate controls reveal
  with an expansion/fade. Disabled dependent rows are removed immediately.
- **Settings state:** searchable rows have stable identity during insertions and
  filtering. Registration/search matching stays synchronous; only the rendered
  entry animates. Existing search/category filtering and callbacks remain.
- **Song options:** matching press feedback, clearer trailing values, larger popup
  hit targets, and explicit progress while quality upgrades run. Download/cancel,
  retry/delete, queue, playlist/account, upload, album/artist, sleep timer, share,
  original/quality/audio-version and lyrics-offset conditions/callbacks remain.
- **Player drawers:** scrim, entrance and dismissal are coordinated. Back, scrim
  and drag dismissals wait for the actual exit transition to finish and use the
  latest callback; repeated dismissal requests are guarded. Dragging follows the
  finger directly, with a spring only for settling. Panes/headings expose
  accessibility semantics; existing scroll/gesture containment is preserved.
- **Queue and lyrics:** landscape panes crossfade without the previous entrance
  delay or animated viewport sizing. The portrait queue settles immediately
  under reduced motion. Existing lyric timing, queue edits and gestures remain.
- **Motion preference:** Home, navigation, player/queue and drawers honor both
  Veyra's Reduce Animation preference and the system animation-duration scale.
  Song-menu progress uses a static arc under reduced motion.

## Performance scope

The changes avoid interpolating whole-page sizes, keep drawer opacity/position
reads in drawing/layout, reuse the detail image request/cache, and add no looping
backgrounds, extra image assets or permanent particle/blur effects. Existing
reduced-blur support remains. These are targeted cost reductions, not measured
frame-time or all-device smoothness guarantees.

## Verification

Production-release Kotlin compilation passed after using the environment's
supported HTTP proxy and system Java CA trust. No TLS verification was disabled.
This compiles application and shared Compose Kotlin, but does not assemble/sign
an APK or run R8. Existing unrelated compiler warnings remain.

- **16 focused Android unit tests passed**, with zero failures/errors/skips:
  NavigationTransitionsTest (3), NavigationMotionPolicyTest (4), SettingsSearchTest
  (5), and PlayerQualityMenuTest (4). The Gradle test task also compiled the final
  production-release Kotlin sources and test sources successfully.
- Regression checks cover reduced-motion enter/exit behavior (including RTL),
  viewport size changes, forward/back layering, routing, Settings matching and
  quality-menu decisions. Source review also checked existing Settings setter
  references and song-menu action references were retained.
- Website production build/type check and the relevant download/notes-link test
  passed on desktop and mobile Chromium (2 browser checks).
- Source packaging verifies ZIP CRCs, every manifest file hash and the mirrored
  backend before publication. The refreshed archive keeps the existing preview
  filename and checksum sidecar; no additional historical source ZIP is added.

## Device follow-up before promoting

No Android emulator/device render, frame-time benchmark or TalkBack session was
run here. Check the following with the next preview APK:

1. Home → album/playlist → Back; repeat from Search/Library and check retained
   scroll position, cached artwork and large-font layouts.
2. Settings categories/search; toggle Automix, artwork, signature API and high
   performance mode. Check dependent controls, slider persistence and Google
   account navigation. Search matches must not disappear during reveals.
3. Open/close queue and lyrics rapidly, rotate the phone, drag a drawer down and
   cancel, then test Back and scrim dismissal during entrance.
4. Verify every three-dot menu action with online/local/downloaded tracks and
   signed-in/out states, including running download cancellation and quality
   upgrade progress. Buttons must continue to invoke their original actions.
5. Repeat navigation/player interactions with Reduce Animation enabled and with
   system animations disabled; verify RTL, large fonts and screen-reader focus.

No new APK or completed device/performance validation is claimed by this source delivery.
