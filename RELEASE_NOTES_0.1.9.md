# Veyra 0.1.9

**Stable · Android version code 37 · includes the completed 0.1.9-beta1**

## Immersive Worlds · build 37

- Direct Immersive button beside the player menu in portrait and landscape.
- Keep **Classic**, and choose **Artwork Cinema**, **Theme World**, **Lyrics Focus**, **Minimal Night** or **Landscape Stage**.
- A six-mode picker with descriptions and selected state replaces cycling through unlabeled presets. Classic keeps its independent background, artwork-fit and lyric-placement choices.
- Redesigned playback dock with real seeking, elapsed/total time and larger playback controls. It adapts to wide windows; exit and mode selection stay accessible when the dock auto-hides.
- Real current song artwork and shared synced lyrics, themed scenery, missing-art/lyrics fallbacks and saved mode preferences. No second audio player or forced orientation change.

[Implementation, checks and limits](verification/IMMERSIVE-WORLDS-0.1.9.md).

The original stable promotion was build 36, from completed beta build 35.

## Make it yours

- **62 themes**, including Margin Notes, five Illustrated Worlds, five Advanced Worlds and five Punk Worlds added since stable 0.1.8.
- **Five independent Layer Lab asset packs:** Spray Parade, Paper Pop, Ink Rebel, Laser Club and Thread Riot. Layer their icons, fonts, controls and motion over your chosen theme or custom app style; category switches preserve your wallpaper and palette.
- **95 launcher choices.** All 62 themes and five asset packs have matching connected-wave editions. Home shows the icon you actually selected.
- Background visibility, dimming, supported softness and control-opacity adjustments, with a separate reset for these overrides.

## Listening feels clearer

- Playback recovery checks that it still owns the failed song and player error before seeking or skipping.
- Queue summaries, safer clear confirmation, move accessibility actions and a Now playing shortcut preserve deliberate browsing.
- Featured/compact Home cards, optional sign-in card and configurable speed dial counts complement existing ordering and pinned-header choices.
- Search retains its source selector, offers explicit first-page/pagination retry, keeps existing results after continuation failures and avoids repeated-page request loops.

## Lighter rendering and safer installs

- Cached off-main font construction, bounded glyph warmup, shared image requests, simpler container clipping and lighter page transitions reduce rendering work.
- Stable theme and asset catalogs now target **main**, including bundled offline fallback catalogs, while preserving strict URL validation.
- Startup validation, theme installation, activation and removal share a mutation lock. Asset installation publishes its installed index under the same lock. Saved theme manifests use bounded reads.
- All existing published theme/asset ZIPs remain byte-for-byte unchanged. The completed beta branch and its source archive are preserved.

[Source ZIP and SHA-256](downloads/README.md) · [Verification and limits](verification/STABLE-0.1.9-PROMOTION.md) · [Theme guide](themes/README.md) · [Asset-pack guide](asset-packs/README.md)

Source promotion does not build, sign or publish an APK. Rebuild the stable APK for the new app code and bundled launcher aliases. Compilation and focused probes do not establish real-device smoothness or eliminate every possible bug.
