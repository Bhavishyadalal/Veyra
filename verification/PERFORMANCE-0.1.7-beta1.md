# Veyra 0.1.7-beta1 — Performance and theme polish

**Track:** preview/0.1.7-beta1 · **Version:** 0.1.7-beta1 · **Code:** 25

## Changes and rationale

| Area | Previous behavior | Updated behavior |
| --- | --- | --- |
| Home supplements | Two independent endpoints waited for one another | At most two requests run together; results publish in provider order after the core feed, with existing timeouts, listener guards and structured cancellation |
| Explore categories | An untracked old response could replace a newly opened category | Opening another category or going Back cancels the old job; generation, selection, listener, region and language guards reject obsolete results |
| Download recovery | Every queue mutation serialized the full recovery list on its calling thread, under the queue lock | An immutable snapshot enters a conflated, single IO writer; JSON serialization and preference updates run outside the queue lock; later writes survive a prior write failure |
| Theme artwork | Painter requests decoded original-size local artwork even for small cards | Coil AsyncImage resolves displayed constraints for collection rows, preview artwork, atmosphere banners and scene/backdrop images |
| RTL banner text | The contrast scrim always darkened the left edge | The scrim reverses with layout direction to stay behind start-aligned text |

No version promotion, APK generation, backend change or published theme-pack rewrite.
The completed seven-area reliability work remains in place. Google accounts,
guest rooms, real music artwork, queue actions and optional launchers remain.

## Focused verification

- Android production-debug Kotlin/Java/resource compilation through `:app:testProdDebugUnitTest` succeeded. No APK task was run.
- **27 tests passed:** PreviewPerformanceTest (6 new), PreviewReliabilityTest (4), HomeExperienceTest (17).
- New scheduler tests verify overlap with stable order, a two-request concurrency cap, cancellation of every in-flight batch request, bulk recovery coalescing, write ordering during a blocked write, and recovery after a failed write.
- The synthetic overlap test completes two simulated requests of 8 s and 1 s in 8 s. This verifies scheduling only; it is **not a measured Android startup or network speed improvement**.
- Website type checking and production build passed. The focused external-download/link test passed on mobile and desktop Chromium (2 tests).
- Native Compose component studies rendered and were inspected: Theme Studio at 390 dp, at 320 dp / 150% text, and at 320 dp / 150% text in RTL; plus the RTL atmosphere banner at 320 dp / 150% text. Text wraps and theme images remain visible.

[Normal Studio](performance-preview/studio.png) · [Large text](performance-preview/studio-large-text.png) · [RTL large text](performance-preview/studio-rtl-large-text.png) · [RTL banner](performance-preview/atmosphere-rtl-large-text.png)

These use actual shared components in a desktop Compose harness with an illustrative
surrounding layout. They are **not Android screenshots** or complete navigation tests.
The English sample strings exercise RTL layout, not translated-content correctness.

## Limits before stable promotion

No real-device startup, frame-time, memory or network benchmarking was performed.
Actual loading time depends on the provider, device and connection. The category
race fix was reviewed and compiled; its complete Android navigation path was not
instrumented. Real-device playback, network handovers, TalkBack and launcher
switching remain unverified. This focused pass does not certify every app screen.
Background recovery writes can still be interrupted by immediate process death;
SharedPreferences persistence is asynchronous, as before. A storage write failure
is logged and later snapshots remain eligible to persist.

The preview stays separate from stable 0.1.6. APK publication and promotion need
the owner's explicit request and device validation.
