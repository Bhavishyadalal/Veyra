# Home · 0.1.6-beta1

Preview branch `preview/0.1.6-beta1`, version **0.1.6-beta1**, code **21**. Based on stable main `49eaeaac8f4f147c4ed8434dec6c9612324c316d`; main is unchanged. No APK, signing, GitHub release or backend deployment is performed.

## Implemented

### Saved Home before refresh

The ViewModel reads a small private snapshot on IO before fetching new Home data. An existing page remains visible during refresh and on failure; recents/supplements do not wipe a useful previous feed before a fresh core succeeds. No network connection is required to read a saved snapshot. First-ever use still needs a successful online feed before there is anything to restore.

Snapshots store only Home shelf/card metadata, not audio, stream URLs, cookies or live continuation tokens. Existing Coil disk caching supplies any retained artwork, with the existing native fallback for missing/evicted images. Saving a recommendation does not download its music. Paging tokens are fetched fresh rather than restored from disk.

Guest, account and channel identities use separate SHA-256 filenames in the app's private no-backup directory. Visible Home is cleared synchronously on identity change; generation/identity checks reject late results. Snapshot reads can run while account session setup waits. Home network work is paused during candidate browser-session validation, and failed/uncommitted candidates restore the active signing session before resuming Home. Google sign-in remains Google's existing browser flow; no new account form is added.

Writes are atomic/coalesced. Each snapshot is limited to 24 non-empty shelves, 20 cards each and 1 MiB; at most eight snapshot identities are retained, always keeping the snapshot just written. Corrupt/unknown-schema data is ignored and oversized/empty writes preserve the previous file. Device-clock changes do not hide otherwise usable content. Snapshot and layout files are not included in the existing AppSettings/listening-history backup or Android auto-backup.

### Customize sections

Home has a Customize Home entry and native bottom sheet. Sections can be moved up/down, hidden/shown and reset to provider order. Preferences are scoped to the listener and stored separately from snapshots. Hidden sections remain editable; hiding every section provides a recovery action instead of an unexplained empty page. Newly encountered sections append without resetting preferences. Identity follows normalized provider headings; a renamed/translated heading is treated as a new section.

The editor uses open rows, 48dp icon controls, labeled switches and reduced-motion-aware placement. Main/desktop callers that do not supply customization callbacks keep the original optional HomeScreen contract.

### Offline Home

A lifecycle-bound default-network callback supplies connectivity without polling or a phone-side keep-alive. Offline Home shows saved content without feed shimmer and provides Downloads/Local Music actions through the existing permission/navigation path. With no saved feed, these actions remain available. Offline taps on cached tracks only start verified downloaded files through the existing guarded queue path; unavailable tracks/online collection pages give an explanation. Normal online taps and long-press callbacks remain. Reconnection starts a quiet refresh. Backend cron behavior is unchanged.

## Executed verification

- **85 focused Kotlin/JUnit tests passed**, including **17 new Home tests** against real models, disk store and layout policy: restart/metadata round-trip, guest/account/channel isolation, hashed filenames, corrupt/unknown schema, atomic failure, bounded retention, empty refresh, layout persistence, hide-all/reset, missing/new section behavior, invalid moves, Turkish locale, clock change and Unicode/null metadata.
- Actual **HomeExperience.kt**, HomeLayout and models compiled with Kotlin 2.3.20 / Compose Desktop 1.10.3 / Material 3 Desktop 1.7.3.
- **7 component interaction checks passed**: move, visibility, reset, edit, both offline shortcuts and disabled edge control. Six component boards rendered; compact/large-text and offline contrast were visually inspected. These use real production component bodies/resources and bundled Manrope, with narrow settings/page-gutter host adapters. They are not complete Android Home screenshots.
- **7 changed/new production Kotlin files parsed with zero PSI syntax errors.** HomeConnectivity separately compiled against the real Android API 36 jar. Syntax parsing is not full Android/Compose type checking.
- Android string XML compiled with AAPT2; both Android and shared resource XML parse without duplicate names. Compose resource generation passed.
- Production debug and release BuildConfig generated as **0.1.6-beta1/code 21**, with the same Veyra backend.
- Existing auth-directory, playback, downloads, AppSettings, Listen Together and player-directory sources and Settings controls byte-match stable. MainActivity and MainViewModel are intentionally changed for Home wiring, not claimed byte-identical.
- Git whitespace/conflict checks and final source ZIP integrity/source hashes are checked before push.

## Limits and device follow-up

Full Android compilation was attempted and is blocked before Kotlin compilation at :app:stripNewPipeExtractorUtils by JitPack HTTP 403 for NewPipeExtractor v0.26.3. No full Gradle unit suite, APK, emulator, measured startup/frame-time gain, real Google login or Android integration/device pass is claimed. The component runner does not exercise live ViewModel networking, Android navigation/permissions, force-stop restoration, account-switch races or actual offline audio. Baseline preservation and pure tests do not replace these checks.

Before promoting this beta:

1. Open Home online and wait for content, force-stop, reopen and confirm cached shelves precede refresh without a blank page.
2. Enable airplane mode and repeat; test both offline shortcuts, a downloaded cached track, a missing/deleted file and an online-only card.
3. Reorder/hide sections, restart, hide all, reset and check new/provider-renamed sections.
4. Switch account/channel and sign out while requests run. Confirm the previous listener's cards never appear for the next listener and cached preferences are isolated; test a rejected browser session too.
5. Test reconnect, failed refresh, paging, scrolling/back navigation, RTL, TalkBack, large fonts, reduced motion and low storage on real Android devices.
6. Verify package/signing identity and upgrade behavior before distributing an APK. Use the existing signing key; stable main remains 0.1.5/code 20.
