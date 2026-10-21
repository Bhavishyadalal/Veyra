# Veyra preview · submission review

**21 October 2026 · preview/0.1.7-beta1 · version 0.1.7-beta1 / code 25**

This maintenance pass reviewed the current Home/Explore loading and account boundaries, search pipelines, queue restart persistence, Listen Together reconnection/personal stashes, download workers and records, theme installation/application, launcher switching and Back/layout code. It fixes concrete state and concurrency defects; it does not certify that every screen and device is bug-free.

## Fixed defects

| Trigger | Failure before this pass | Fix |
| --- | --- | --- |
| Clear/edit a search while its first page or continuation is pending | A late reply could repopulate old results | Invalidate the request generation even when no new network request follows; reject obsolete initial and final publication |
| Submit a query or switch sources during typeahead | A late media lookup could reopen the dropdown | Check query, submission gate and YouTube source both before and after suspension |
| Tap history/suggestions while using Addons or Library | `searchFor` always initiated YouTube search | Route the tap through the selected source; cancel obsolete addon jobs and clear their loading state on edits |
| Cancel a download and immediately retry it | Old cleanup could overlap a new transfer, remove its worker slot, or publish stale state | Each transfer has a distinct claim. A retry queues immediately but cannot claim the same destination until old cleanup ends. Publications and finalizers require claim ownership |
| Destroy/restart the download service during cancellation | Cancelled file cleanup could outlive the worker reservation | Cancel/join each transfer in `NonCancellable` cleanup before releasing its claim; unrelated tracks remain eligible |
| Concurrent download completions | Snapshot creation preceded the persistence lock, so an older saved map could reach preferences last | Create both saved and metadata snapshots and persist them within one critical section |
| Dismiss an old failure during a retry | The old check/remove sequence could remove the new running state or recovery entry | Check inside the atomic update; preserve recovery while queued/running |
| Enter a party with local-file songs in the personal queue | Local tracks were filtered out; the current index could point to another track | Preserve the entire local personal queue. This does not publish device files to the party |
| Party playback updates position/index | Scalars could overwrite the personal restart snapshot | Skip personal playback-state persistence while in a party |
| Move outside the serialized restart window without a timeline edit | Restart could clamp to a different saved track | Refresh the bounded snapshot on leaving its known range; ordinary progress samples still update scalars only |
| Older Android versions | Alias lookup needed API 30; download header sniffing used API-33 `readNBytes` | Guard alias access, use a bounded compatible prefix reader, and guard final runtime-shader construction |
| Change export preference after completing a download | Old downloads displayed the new preference as their saved location | Classify each actual saved URI, including private offline packages |
| Recompose settings/menus or change configuration | Some resource reads and state snapshots were not explicitly observed | Use configuration-aware `LocalResources`, lifecycle-observed activity names and remembered initial form/derived-flow snapshots |
| Localized category title/plural selection | A literal percent could be interpreted as formatting; Hebrew/Polish branches were incomplete | Mark the literal title as unformatted across locales and supply missing grammatical quantity branches |

The theme catalog, all 41 published theme entries/packs, 69 launcher choices, original identity, Google account integration, guest-friendly rooms and official backend are preserved. No UI redesign or new dependencies. Configuration-aware resource reads and observed state keep affected labels current. Stable Android source is unchanged.

## Verification

Android production-debug Kotlin/Java/resources compiled successfully through the focused unit-test task. **68 tests passed, zero failures/errors**:

| Suite | Tests |
| --- | ---: |
| SearchPublicationTest | 4 |
| DownloadWorkQueueTest | 4 |
| DownloadRecordConcurrencyTest | 1 |
| DownloadSessionTest | 19 |
| PartyPersonalQueueStashTest | 8 |
| QueueHistoryTest | 6 |
| BoundedContextPersistenceTest | 10 |
| ProbePrefixTest | 3 |
| DownloadLocationTest | 3 |
| BluetoothAudioTrackerTest | 10 |

Seventeen tests are new. They cover obsolete search publication, cancellation before job attachment, immediate same-track retry, other-track progress during cleanup, stale finalizers, service-stop claims, coordinated concurrent saved-record writes, and local/mixed personal queue restoration. The tests use deterministic local state/fake preferences/player interfaces; they are not device playback or provider integration tests. The service window/party scalar guards were reviewed and compiled; full service lifecycle instrumentation remains a device check.

Commands used JDK 17, the local Android SDK and cached dependencies:

```sh
./gradlew --offline --no-daemon --max-workers=2 :app:testProdDebugUnitTest \
  --tests app.veyra.music.SearchPublicationTest \
  --tests app.veyra.music.download.DownloadWorkQueueTest \
  --tests app.veyra.music.download.DownloadRecordConcurrencyTest \
  --tests app.veyra.music.DownloadSessionTest \
  --tests app.veyra.music.playback.PartyPersonalQueueStashTest \
  --tests app.veyra.music.QueueHistoryTest \
  --tests app.veyra.music.playback.BoundedContextPersistenceTest \
  --tests app.veyra.music.download.ProbePrefixTest \
  --tests app.veyra.music.download.DownloadLocationTest \
  --tests app.veyra.music.playback.audio.bluetooth.BluetoothAudioTrackerTest
```

Website typecheck and production build passed. The changed verification/download links passed the focused desktop and mobile Chromium tests (2 tests). The initial browser invocation found no executable at the default cache path; installation to the writable workspace and setting `PLAYWRIGHT_BROWSERS_PATH` resolved it. No layout geometry changed, so previous native component studies were not rerendered. Download completion text now derives from its saved URI rather than the current export preference.

Android analytics emitted its existing nonfatal read-only-settings warning. The first combined lint command had a task-option placement error and was corrected. Offline lint then stopped at an uncached `lint-gradle:32.4.1` dependency; the online retry initially lacked Java proxy properties. The configured-proxy retry completed full analysis and exposed 379 errors / 716 warnings / 5 hints. After fixes, the final offline recheck reports **109 errors / 700 warnings / 5 hints**. All remaining errors are `MissingTranslation`; no API compatibility, Compose resource/state, invalid-format, required Hebrew/Polish quantity or Media3 opt-in errors remain. Canonical icon/theme names and attribution/path strings are marked non-translatable where appropriate; existing localized strings are retained.

**Lint still fails.** New preview functionality has incomplete translations across the existing locales; Android uses the default English strings for those entries. This is not translation completeness or a clean release gate. No baseline, rule disable or severity downgrade was added. Warnings still include other locale plural coverage, style/dependency suggestions and items requiring review. The complete remaining error inventory and category counts are in [submission-lint-summary.json](submission-lint-summary.json); test counts are in [submission-focused-tests.json](submission-focused-tests.json). Natural-language correctness is not established by resource compilation or lint. These results do not justify declaring every app bug fixed.

## Delivery and remaining limits

Source changes are staged before packaging. `scripts/package_source.py` refreshes the preview ZIP; `scripts/verify_archives.py` verifies the stable ZIP and three retained preview ZIPs. Branch maintenance copies only the new preview archive, checksum and download index to historical branches. Theme packs, licenses and backend provenance remain intact. Public source checksum and affected website links are verified after push; Git delivery is not proof of Vercel deployment.

No APK build, Actions dispatch, version bump or stable promotion. Real-device playback, long-running foreground-service lifecycle, network handovers, TalkBack, launcher switching and GPU/frame-time behavior remain unverified. A cancelled download may wait for its provider's blocking IO/abort to finish before the same-track retry starts. SharedPreferences writes remain asynchronous and can be interrupted by immediate process death. This preview still requires device validation before stable promotion.
