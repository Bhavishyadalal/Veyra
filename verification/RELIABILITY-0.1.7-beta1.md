# Preview reliability update — 0.1.7-beta1 / code 25

Prepared 21 October 2026. Made by Bhavishya Dalal.

## Implementation and preserved behavior

1. **Home and resume:** existing bounded, atomic, account-scoped Home snapshots remain the source of saved content. Saved content now displays its localized save date/time independently of refresh errors. Account transitions clear that label with the previous content. The service explicitly sets restored queues to paused; it does not prepare or resolve a stream during restoration. Existing queue/position and bounded-context persistence remain in place.
2. **Loading:** Home artwork prefetch follows the first visible shelf and the next two, capped at four covers per shelf, rather than always warming the top of a long feed. Requests are deduplicated and disposed when the window changes or the screen leaves composition. Existing Coil memory/disk caches remain. Cancelled music-only rendition selection now propagates cancellation rather than logging it as a source failure and falling through to obsolete warm-up work. Existing source fallback, read-ahead and manual quality controls remain.
3. **Downloads:** outstanding request metadata survives process death and returns as explicit interrupted/retry rows. No automatic background restart, stream URLs, tokens or partial-file success claims are persisted. Retry uses the existing resolver and atomic destination commit/abort path. Metadata includes video conversion, album and quality hints. Success/cancel/explicit manager clear removes the request; merely closing a song menu does not. Interrupted service work becomes retryable. The manager adds Retry failed, available filesystem space, public/private destination feedback, truthful completion labels, indeterminate unknown-length feedback, 48 dp per-item actions and track-specific accessibility labels. Existing cancellation and individual retry remain.
4. **Listen Together:** a default-network change replaces the room socket while retaining membership/server identity. Membership switching remains serialized; a socket mutex ensures old cleanup finishes before a replacement publishes its session. The clock is reset for each connection, LIVE waits for the server welcome, and 45 seconds without incoming application frames causes recovery through the existing jittered backoff. Host disconnection has explicit feedback. Guest access, Veyra defaults, custom-server policy and token isolation remain unchanged. No backend protocol/server edits.
5. **Themes:** the apply confirmation offers an unchecked matching-launcher option only for whitelisted bundled theme IDs. Icon operations run off the UI thread after a successful theme apply. Icon failure reports separately while preserving the applied theme. Existing compact collection/details, previews, original appearance, optional interface recreation and immutable packs remain. Shared apply controls use naturally growing text and one checkbox semantics target per row.
6. **Queue:** single removals and Clear user queue offer Undo above the player queue and in the existing notification strip. Undo reinserts exact MediaItems in original order, retaining entry metadata, without seeking/playing. It is refused after timeline/current-entry changes or a controller/party change. Removing the current track and room queue edits remain supported but deliberately do not offer an unsafe local undo. Clear keeps current/context/autoplay tracks. Existing Play next/Add to queue insertion rules and distinct confirmations remain. Existing service timeline persistence records edits and undo.
7. **Accessibility:** download actions are 48 dp with track-specific labels; status text can grow; unknown-length loading remains static under reduced motion. Download entrance/progress feedback and queue notices honor the shared app/system motion policy; queue auto-positioning avoids animated scrolling with reduced motion. Queue undo timeout follows Android accessibility recommendations. Confirmation surfaces use theme-aware contrast colors. Apply controls support wrapping, RTL and large text.

## Validation

- Android resource generation and Kotlin/Java compilation passed through `:app:testProdDebugUnitTest`.
- 93 focused JUnit tests passed: PreviewReliabilityTest 4, DownloadSessionTest 19, QueueCoordinatorTest 44, QueueUrlWarmupTest 5, ListenTogetherIdleHealthTest 5, ThemePackTest 13, ThemeLauncherTest 3. Zero failures/errors. A final download-admission ordering refinement is additionally checked with the 23 recovery/session tests. The retained Undo callback uses a Compose-updated controller reference; its lifecycle guard is compiled and checked with the 48 reliability/queue tests.
- `python3 scripts/verify_app_backend.py` passed: official Veyra endpoint, migration and token guards present.
- Actual shared `ThemeApplyOptions` rendered at 390 dp, 320 dp/150% text, and 320 dp/150% text/RTL. Images inspected for wrapping and overlap. Each study verifies two checkbox semantics targets, each at least 48 dp high, and delivers the matching-icon callback exactly once. [Studies](reliability-preview/).
- Website: `npm run typecheck`, `npm run build`, and the `FAQ and external download links` Playwright test passed on mobile and desktop Chromium (2 tests).
- Source ZIP generated after staging and checked with `scripts/verify_archives.py`: all four retained archives, manifests, per-file hashes, backend snapshot and theme pack bytes.
- Identical preview archive/sidecar and catalog updates are mirrored to maintained branch tips; no Android source on main/stable/historical branches is promoted. No history rewrite, release/tag deletion or APK build. Push commits include `[skip ci]`; no Actions dispatch.

Repeat focused Android checks with Java 17 and the configured Android SDK:

```sh
./gradlew :app:testProdDebugUnitTest \
  --tests app.veyra.music.PreviewReliabilityTest \
  --tests app.veyra.music.DownloadSessionTest \
  --tests app.veyra.music.QueueCoordinatorTest \
  --tests app.veyra.music.QueueUrlWarmupTest \
  --tests app.veyra.music.ListenTogetherIdleHealthTest \
  --tests app.veyra.music.ThemePackTest \
  --tests app.veyra.music.ThemeLauncherTest
./gradlew -p verification/theme-preview run -Ptheme=playtime-club -Preliability=true
python3 scripts/verify_archives.py
```

The initial restored environment needed missing dependencies downloaded through its proxy and the installed proxy CA trust configured for Java. TLS verification remained enabled. Existing compilation warnings remain; the focused tasks passed.

## Limits requiring Android/device verification

- Full TalkBack traversal, full-window 320 dp/large-text layouts and gesture Back behavior.
- MediaController queue round trips, bounded-queue undo, process death and background playback on Android.
- Real Wi-Fi/mobile handovers, captive portals, doze, server restart and host loss with multiple phones.
- MediaStore interruption/recovery and filesystem capacity on removable/scoped storage.
- Launcher alias switching across Android versions and launcher caches.
- No measured cold-start, playback-latency, frame-time or battery benchmark.

Native studies use actual shared apply controls in illustrative desktop Compose layouts. They are not emulator/physical-device screenshots or proof of full Android lifecycle behavior.
