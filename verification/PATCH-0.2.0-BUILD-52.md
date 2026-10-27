# Stable 0.2.0 · build 52

Follow-up source review. Version remains **0.2.0**, Android code **52**.

## Findings and fixes

1. **Concurrent source writes:** UI edits, add-on manifest callbacks on IO threads and name probes could each publish a list read before another edit. Registry read/modify/publish operations now share a reentrant monitor. Client maps are built before config publication; matching reads use the same monitor. The delayed-name merge is inside that transaction. No network request is made while holding this monitor.
2. **Unsaved server policy changes:** candidate tests carried the saved source ID. Their manifests could overwrite the saved source's download/lossless policy before Save, including when testing another endpoint. Registry policy writes now require the exact currently registered client. Replaced clients are rejected too. Candidates read and enforce their own policy without borrowing the saved endpoint's policy.
3. **Retired request work:** candidate clients were discarded without cleanup; client `clear()` only erased answers and did not cancel its independent coroutine scope or blocking HTTP calls. Permanent `close()` cancels tracked HTTP calls and the client scope, then clears caches. Candidate probes release in `finally`; replaced/removed clients use the same disposal. Ordinary caller cancellation still preserves shared requests; cosmetic label updates still retain the warm client.
4. **Duplicate reordered rows:** repeated known IDs in a drag result produced duplicate source rows. Reorder now deduplicates IDs before resolving them, preserving unknown-ID filtering and omitted sources.
5. **Stale local-page refresh:** local-group visits had a shared default load ID, and refreshes updated every matching browse ID after suspending. Local groups now receive unique visit IDs; refresh results only update visits present when the refresh began, so Back/reopen cannot inherit the old request.

## Review scope

Read the source registry, add-on client/source lifetimes, Settings probe/editor calls and local-detail loading. Also inspected theme/asset catalog startup, transfer/install locking and cancellation, download enqueue/cancel/claim flow, media-controller ownership and launcher selection/rollback. This is a targeted follow-up review, not a claim that every project line was audited or every possible defect eliminated.

## Verification

- `:app:testProdDebugUnitTest` targeted selection: **100 tests, 0 failures/errors**: SourceProbeUpdateTest (5), SourceClientLifetimeTest (2), AddonOrderTest (13), AddonSourceTest (59), SharedCallsTest (9), AddonPolicyTest (8), DownloadWorkQueueTest (4).
- New regressions exercise 32 concurrently added/renamed/toggled/policy-updated sources; current/candidate/replaced-client policy ownership; actual HTTP candidate-test isolation; cancellation of a stalled HTTP request on owner close; duplicate reorder IDs.
- `:app:compileProdReleaseKotlin` passed. Local-page refresh ownership is source-reviewed and compiled, not exercised through a real-device Back/reopen test.
- Source ZIP regenerated from staged source; retained archive checksums/content and mirrored backend checked.
- Website build/type checks and mobile/desktop release-link/layout checks passed before push.

## Limits

No signed APK, R8/package build, physical-device UI/gesture/FPS verification or live Vercel deployment is established here. Rebuild/install **code 52** to receive the changes. Existing theme/asset pack bytes and frozen preview branches remain unchanged. A source push is not APK publication.
