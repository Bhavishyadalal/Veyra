# Player refinement — preview 0.1.4-beta1

19 October 2026. Branch: `preview/0.1.4-beta1`. Version remains **0.1.4-beta1 / code 17**. Stable main remains 0.1.3 / code 16; its original ZIP is unchanged. No APK, signing, release or merge into stable.

## Changes

- A shared native surface gives play/pause a subtle framed charcoal treatment, fixed 92dp target (76dp compact), original bold transport glyphs and a finite 150ms icon crossfade. Skip targets are 56dp. Loading retains the same control footprint and focus target, announces indeterminate loading, and rejects duplicate transport taps.
- Menus, like, lyrics, queue and capsule segments share consistent visual press feedback without scaling their hit regions. Existing haptics, shuffle/autoplay tap windows and host callbacks remain. Mode segments expose selection; outgoing queue/output controls become inert during their transitions. Queue close has an explicit accessible label.
- Seek completion and cancellation are separate. Track changes, permission locks or a disappearing deck cancel an unfinished seek rather than submitting it. The final release coordinate is respected even when the input device delivers no intervening move. Track changes clear stale scrub state. The locked-release fallback clears the visual target without sending a seek.
- Seek/volume input has finite/clamped accessible values and mirrored RTL input/fill. Time labels use tabular figures; quality moves to its own line on narrow screens or enlarged text, avoiding timestamp overlap. Existing volume completion, loading sheen and Automix draw-state behavior remain.
- Returning deck fade is 280ms instead of 700ms; hero reveal is 320ms. Shared control feedback, icon fades, deck transitions and queue placement honor reduced motion. Reduced-motion loading is static. No new production dependencies, generated artwork or always-running decorative effects.

## Executed checks

- **22 native Compose component checks passed**: taps and exact callback counts; button role and fixed target; busy/disabled/selected semantics; seek tap, cancellation on track/lock/deck changes, release coordinate, accessible reject/clamp, animated press geometry, RTL, invalid geometry, reduced duration and scrub cancellation/pending state.
- **Ten native component boards rendered** using the actual production primitives and app icon paths. Playing, paused, buffering, queue, compact and 320dp/1.6× text boards were visually reviewed. These are component fixtures, not full Android app screenshots.
- Kotlin PSI audit: **27 existing named control-call contracts and 109 named function interfaces retained**. **194 shared/playback/auth/Android-host source files** byte-match the starting preview. This is source-preservation evidence, not runtime proof.
- Existing settings audit passes: **160 named call contracts and 131 named interfaces**, with authentication, MainActivity, AppSettings and AppIcon unchanged against stable.
- **57 focused Kotlin/JUnit regressions pass**. Resource audit parses 114 XML files, checks 22,635 localized entries and resolves eight XML font references, with zero errors. Eight changed/new player Kotlin files parse with zero syntax errors. `git diff --check` passes.

## Android validation limit

Full `:app:compileProdDebugKotlin` remains blocked before Kotlin compilation at `:app:stripNewPipeExtractorUtils`: JitPack denies `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3` with HTTP 403. The existing proxy and CA trust remain enabled; no replacement artifact or verification bypass was used. Desktop component compilation and syntax checks do **not** establish full Android compilation, device performance or integration correctness. The full Android test suite and device rendering are unverified.

Before promoting this preview, build with normal dependency access and test a phone plus compact landscape: rapid play/pause and track changes, loading/failure/retry, scrubbing during a track/party-lock change, volume persistence, lyrics/queue/back/rapid reversal, reordering/removal, output routing, listen-together/member entry, repeat/shuffle/autoplay, TalkBack, enlarged text, RTL and reduced motion. Confirm actual audio seeking and Automix transitions. This preview is not marked stable.

Reproduce: [native checks](player-preview/README.md), [player preservation](player-audit/README.md), [settings preservation](settings-audit/README.md), `python verification/run_focused_kotlin_tests.py`, and `python verification/audit_branding.py` with JDK17 and the bootstrapped Gradle cache.
