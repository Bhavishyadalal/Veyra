# Veyra 0.1.9

**Stable · Android version code 39 · includes the completed 0.1.9-beta1**

## Night Vault glass editions · build 39

The ten Night Vault launcher icons now match Veyra's earlier glass editions: raised translucent waveforms, deep reflections and sculpted frames. Each uses distinct pack materials, from sapphire/ruby and amethyst to pink jelly glass, gold, acid green and midnight-blue crystal.

[See all ten actual icons](design/launcher/night-vault/previews/glass-editions.webp) · [Implementation and checks](verification/GLASS-ICONS-0.1.9-BUILD-39.md).

The matching-icon actions, App icon picker and selected Home icon use these replacements. All existing themes, asset ZIPs and other launcher choices are preserved. Rebuild and install **code 39** for this bundled artwork; this source update does not publish an APK.

## Reliability + Night Vault · build 38

- Fix Play/Pause presentation and commands during buffering; keep the toggle actionable and separate playback intent from the real audio clock.
- Recover empty Home feeds, bound core refresh, preserve useful content and show artwork fallbacks with explicit refresh/retry behavior.
- Add native preview cards, selected-mode summary, adaptive columns and grouped More controls to the immersive menu.
- Suspend covered app pages and their effects while immersive mode is open, retaining saved UI/scroll state and essential playback services.
- Add **ten Night Vault asset packs** and **ten matching Veyra waveform icons**. Veyra now offers **62 themes, 15 independent asset packs and 105 launcher choices**.
- Preserve existing theme/asset packs and the completed beta. Rebuild/install code 38 for the app fixes and new launcher choices; these source changes do not publish an APK.

[Actual assets and icon previews](asset-packs/NIGHT_VAULT.md) · [Verification and limits](verification/PATCH-0.1.9-BUILD-38.md).

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
- **105 launcher choices.** All 62 themes and 15 asset packs have matching connected-wave editions. Home shows the icon you actually selected.
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
