# Beta 0.1.8 — search lifecycle and rendering efficiency

Preview source only · version 0.1.8-beta1 / code 27 · 2026-10-21

## Findings and changes

Search continuations and top-song prefetch previously launched in the ViewModel's lifetime scope with no query-level owner. Editing/clearing a query, changing filters or switching source invalidated publication but left the background work running. `SearchBackgroundWork` now owns one pagination request and one replaceable prefetch. Each search invalidation cancels both; pagination completion releases loading only when it still owns that state. Cancellation is checked before publishing continuation rows or pinning a warmed substitute. Cached first pages also use the bounded prefetch path.

The stream resolver's existing coalesced, process-owned work is deliberately retained: a canceled search waiter must not cancel resolution needed by playback or another waiter. This patch bounds search-owned work; it does not claim that every already-started extractor HTTP request is aborted.

Compose's keyed `produceState` can retain the prior value until the replacement producer runs. Supplying a retained Listening Room asset map to another custom style previously exposed that map as its artwork. The style policy now accepts bundled artwork only for Listening Room. Original still restores its saved downloadable visuals.

The root theme now remembers color schemes and corner shapes by their actual inputs. Editorial's dark player scheme is likewise remembered instead of recreated on recomposition. Colors and control geometry are unchanged; this reduces allocation work, not an established FPS measurement.

## Review scope

Read the Home artwork-prefetch/disposal path, search publication/pagination, theme installation/activation, music download queue/recovery, queue undo and Listen Together reconnect/cancellation paths. Existing bounded/coalesced work, download persistence, account scoping and room reconnection policies were retained. This was a focused source review and regression pass, not proof that every app path is bug-free.

## Observed verification

- `:app:compileProdReleaseKotlin` — passed.
- `:app:testProdDebugUnitTest` restricted to `SearchBackgroundWorkTest`, `SearchPublicationTest` and `AppStylePolicyTest` — **14 tests passed**.
- New coroutine tests cover duplicate continuation suppression, retry after completion, cancellation of both search jobs, old cleanup finishing after replacement pagination, replacing speculative work and cancellation before dispatch/during a request.
- Style policy regression supplies retained botanical art while switching to Editorial/After Dark; both reject it. Existing Original/default and contrast checks pass.
- Existing unrelated Compose deprecation/redundancy warnings remain. No clean-lint claim.

## Limits

No APK was assembled, no Actions workflow dispatched, no stable promotion and no backend change. No new emulator/device run, network throughput/battery measurement or 60 FPS validation was performed. Live provider behavior and low-memory/background lifecycle still require device testing. Existing app-style render evidence is linked from [the style refinement notes](APP-STYLES-POLISH-0.1.8-beta1.md); it is not a new visual test of this patch.
