# Veyra 0.1.7

**Stable Android source · version code 26 · promoted 21 October 2026**

> Promoted from 0.1.7-beta1 at the owner's request. This is an Android Studio source release. No APK, signing operation, GitHub release/tag or deployment is created by this promotion.

## Keepsake · Five new downloadable themes

Patchwork Picnic, Clover Requiem, Sunroom Mixtape, Concrete Afterhours and Matcha Mischief bring the shared catalog to **46 themes**. Their data packs work with stable 0.1.7 / code 26, including its original 256 px brand-icon validator. Refresh Theme studio to discover them.

Stable Android code, UI and its 69 launcher choices are unchanged. The five new matching launcher choices and Studio/icon-picker polish are exclusive to beta 0.1.8.

[Collection and downloads](themes/KEEPSAKE.md) · [Stable compatibility verification](verification/KEEPSAKE-STABLE-0.1.7.md)

## A new atmosphere for your music

### Theme installation correction · 21 October 2026

- Fixed downloaded themes returning to “Download theme” without appearing under Downloaded: 36 published packs use 512×512 brand artwork, which the app previously rejected under its 256×256 control-icon limit.
- Accept brand artwork up to 512×512 while preserving the 256×256 control-icon limit, 2048×2048 scene limit, checksums and archive validation.
- Keep installation feedback beside the Download/Apply controls so failures remain visible without scrolling.
- Existing theme packs and launcher icons are unchanged. This correction requires an updated APK built from the refreshed source; an already installed APK cannot receive this code fix through a theme download.

[Theme installation verification](verification/THEME-INSTALL-0.1.7.md)

- **Theme Studio:** 41 downloadable themes with original artwork, licensed fonts, readable palettes and themed player/navigation controls. Download once and use installed themes offline.
- **69 launcher choices:** the original black-and-white Veyra waveform, matching theme icons and twelve separate Glass Atelier icons. Applying a theme offers an explicit, optional matching launcher selection.
- Stable builds use the catalog on `main`; the historical beta retains its separate catalog. Existing theme-pack bytes, licenses, artwork and launcher identities are preserved.

[Theme collection and previews](themes/README.md) · [Twenty Worlds themes](themes/WORLDS.md)

## Listening continuity and faster useful content

| Area | Changes since stable 0.1.6 |
| --- | --- |
| Home | Saved account-scoped content and freshness feedback; preserve useful content during refresh/failure; bounded overlapping supplement requests |
| Playback | Restore the previous queue and position paused; prefetch nearby artwork while cancelling obsolete work; refresh restart snapshots when a long queue moves beyond the saved window |
| Downloads | Clear queued/running/failed/completed states, progress, explicit interrupted-request retries and storage information; safe cancellation/retry ownership and serial saved-record persistence |
| Listen Together | Network-switch recovery and host-disconnection feedback; preserve local tracks and the current index in personal queue stashes; keep party position out of personal restart state |
| Search | Reject stale first-page, continuation and typeahead results after query/source changes; history and suggestions respect the chosen search source |
| Queue | Guarded removal/clear Undo with persistence and distinct Play next / Add to queue behavior, without starting playback during Undo |
| Accessibility and UI | More comfortable touch targets, TalkBack labels, large-text/RTL layouts, reduced-motion behavior, compact theme controls and configuration-aware labels |
| Compatibility | Guard newer Bluetooth/shader APIs, use Android-8-compatible bounded download format probes, fix literal-percent resource formatting and required Hebrew/Polish plural branches |

## Upgrade and preservation

Version **0.1.7 / code 26** has no beta suffix and is newer than stable 0.1.6 / code 22 and beta 0.1.7-beta1 / code 25. An installed-app update still requires the same signing key and compatible package. Keep private signing material outside Git.

Google account integration, guest-friendly rooms, real music artwork, existing actions and the official Veyra backend remain. Rooms, invites and presence use **https://veyra-listen-together.onrender.com**; intentional custom servers remain supported. Historical stable and preview Android source stays on its branches.

## Verification and known limits

The preceding preview submission pass compiled Android source and passed 68 focused tests. Promotion checks are recorded separately in [STABLE-0.1.7-PROMOTION.md](verification/STABLE-0.1.7-PROMOTION.md); these prior checks are not presented as new device tests.

**Promotion does not resolve the existing lint backlog:** 109 missing-translation errors, 700 warnings and 5 hints remain in the preserved [inventory](verification/submission-lint-summary.json). Missing locale entries fall back to English. No lint baseline, severity downgrade or rule suppression is added.

Real-device playback, network handovers, TalkBack, launcher switching and foreground-service lifecycle remain unverified. Source promotion is not a claim of device certification, APK publication or Vercel deployment.

[Download source and checksum](downloads/README.md) · [Detailed beta change history](RELEASE_NOTES_0.1.7-beta1.md)
