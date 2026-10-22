# Veyra 0.1.9-beta1 · listening and personalization

Applies only to `preview/0.1.9-beta1`, version code 29. Stable app `main` and `stable/0.1.8` remain unchanged. This extends the initialization checkpoint; it does not mark the earlier experience roadmap complete.

## Delivered behavior

| Area | Changes |
| --- | --- |
| Playback reliability | Delayed recovery must still own the same ExoPlayer, MediaItem and PlaybackException before destructive cache/retry/skip operations. Controller reconnection reads the actual error; disconnection resets stale song/loading state. |
| Theme controls | Settings → Appearance → Theme artwork controls: background visibility, extra dimming, Android 12+ background softness and navigation/player control opacity. Bounded finite values persist outside theme packs. Restore defaults clears only artwork overrides. Custom styles suspend these overrides, preserving their own appearance. Existing automatic text-contrast scrims remain active. |
| Queue | Upcoming count and approximate known duration (a plus indicates unknown durations); confirmation before clearing user-added songs with existing guarded Undo; accessibility reorder actions stay within each queue tier. Now playing returns to the current row. Automatic tracking stops interrupting deliberate queue browsing. |
| Home | Featured/compact card choice, optional sign-in prompt, 3/6/9/12 speed dial entries, and section-order-respecting speed dial generation that stops at the selected limit. Existing hide/reorder, shortcut, greeting and pin controls are retained. |
| Search | Keep sources reachable with a typed query. Retry first-page/continuation errors; preserve successful rows during paging failures. A repeated continuation with no new rows enters an explicit retry state rather than looping. Empty results offer clear query or All categories. Localized category resource IDs replace hard-coded headings. |

## Focused verification

- Production release Kotlin compilation, including shared UI and generated string resources.
- Six standalone checks against the production search publication/paging functions: duplicate page/token refusal; new rows; advanced token; terminal page; superseded request; wrong source. The repeat-page cases are also preserved in `SearchPublicationTest` for the normal test suite; no broad JUnit suite was run for this update.
- Source archive manifest and SHA-256 verification, plus website build/type checks for the updated preview notes and download description.
- `git diff --check` and review of changed callbacks, saved defaults and custom-style isolation.

These checks do not measure frame rate or establish device/network behavior. No APK assembly, device tests, benchmark campaign, unrelated app test suite or live website deployment verification is claimed.

## Device follow-up

On the next preview APK, check interrupted playback followed by manual retry/track change; reopen saved theme and Home preferences; verify theme text contrast on bright packs and older Android; clear/undo and reorder a queue with duplicate songs; scroll away during a track change and return with Now playing; fail/retry search paging and switch sources with a typed query. Profile actual frame times before claiming a particular FPS.
