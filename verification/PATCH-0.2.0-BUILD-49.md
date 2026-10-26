# Stable 0.2.0 · build 49 review and fixes

Source review and focused regressions on build 48. Version name remains **0.2.0**;
Android version code is **49**, so a same-key APK can update the existing app.

## Issues fixed

| Area | Fault | Correction |
| --- | --- | --- |
| Volume | A drag can finish between the device's discrete volume steps. The reported value may not change, so the slider never reconciles after release. | Observe drag state as well as system volume; reconcile on release, snap with reduced motion, and release state on disposal. |
| Seeking | A new track initially shares the previous track's drag/pending seek; a replacement playhead is not observed by an existing settlement effect. | Remember state by track, observe the current position source, and cancel retired state. |
| Canvas | A non-cooperative lookup can return after animation/data settings disabled its request. | Check coroutine cancellation before adopting the result. |
| Player swipe | Long-lived pointer input retains old skip callbacks; sleeve settling ignores reduced motion. | Use updated callbacks, density-aware thresholds and reduced-motion snapping. |
| Mini-player pull | Removing the mini player during a pull can skip its release callback, leaving the player sheet partially held. | Release in `finally`, including pointer-coroutine cancellation, and reset the abandoned visual lift. |
| Queue/lyrics | Closing a panel while it scrolls can leave the parent player's gesture suppression set. | Report scrolling through the latest callback and always report false on cancellation/disposal. |
| Detail pages | A late artist/playlist result or Spotify match can replace a new visit to the same browse ID. | Give each network visit a load ID, publish only to that visit, cancel its work on back/tab/account changes, and limit continuation results to their original visits. |
| History/menu | Repeated history loads can arrive out of order. A cancelled song-menu result can repaint the wrong menu or seed another account's rating. | Gate history by latest request, cancel/invalidate on account changes, and check menu cancellation before publishing. Library-removal updates only touch the matching song's menu. |
| Ratings | An earlier failed player/notification request can undo a newer rating, including a return to the same value or a new account session. Multiple failed rapid edits can also roll back to an unconfirmed value. | Share monotonic edit tokens, invalidate on account clear, roll back only the current edit to the last confirmed state, and serialize sends while skipping obsolete queued edits. Read optimistic state synchronously for the next tap. |
| Video switch | `ConcurrentHashMap<String, Song?>` throws when a no-match lookup tries to store `null`. | Use explicit result wrappers in a synchronized 128-entry LRU; cache genuine misses for ten minutes and leave failed/offline searches retryable. |
| Home | Continuation shelves bypass the blank-tile filtering used by the initial feed. | Apply the same usable-content filter before merging later shelves. |
| Studio | Slow startup validation can finish after an online catalog refresh and overwrite it with an older bundled catalog. | Atomically publish the fallback only while the catalog is empty, for both themes and overlays. |
| Search | A pending focus request does not rerun if a hidden search field becomes visible. | Include field visibility in the focus effect's keys. Existing media/suggestion deduplication was reviewed and retained. |
| Immersive layout | Large fonts on a short landscape screen can push transport controls outside the available deck. | Bound the footer's height and let its content scroll. Existing auto-hide, cancellation protection and lyric-space reservation remain. |

## Review scope

Reviewed the shared player/immersive controls, gesture and seek lifetimes,
queue presentation, Home refresh/paging/cache paths, search publication/focus,
detail navigation, history/account transitions, song menus and rating updates,
theme/overlay catalog initialization, font/glyph cache paths and download work
ownership. This patch preserves the existing design rather than introducing new
navigation styles or visual packs.

The collection stays at **72 themes, 32 overlays and 132 native launcher choices**.
Published pack bytes, launcher resources, completed previews, audio decoding/DSP
and backend source are unchanged. The build-48 foreground-icon and immersive
footer fixes are retained.

## Verification

- `:app:compileProdReleaseKotlin` passed. Generated production debug and release
  metadata both report version **0.2.0 / code 49**.
- **40 focused tests passed**, zero failures/errors/skips: two shared cache
  tests and 38 shared-UI/state tests. These include five new player-state
  lifecycle checks, rating request ownership, actual short-screen/large-font
  immersive rendering, recurring auto-hide, cancelled seeks, buffering transport,
  cancelled holds, queue identities/history, Home identities/blank tiles and
  theme-control/glass behavior.
- The new volume check uses a quantized system-volume fixture. The Canvas check
  deliberately returns after cancellation. The scroll check retires a real
  scrolling list and verifies cleanup uses the latest parent callback.
- The two-module test runs are focused; unrelated test suites were not rerun.
  An initial offline attempt needed one uncached test dependency; dependency
  retrieval used the configured proxy and verified CA trust without changing
  project dependencies.
- Backend snapshot manifest verified: **40 files**. The stable source ZIP is
  regenerated with the existing packaging script and checked with all retained
  archives/checksums before publication.
- Matching website source uses build 49 and this report; production build,
  typecheck and local mobile/desktop metadata/link/layout checks precede its push.

These are source, compilation and offscreen checks. No physical Android device,
end-to-end live provider session, device FPS benchmark, signed APK assembly,
R8 release package or live website deployment is established by this patch.
It does not certify the entire app free of every possible bug. Device follow-up
should exercise rapid navigation/rotation, volume-route changes, notification and
player rating taps, connection loss/recovery and the supported theme/asset choices.
