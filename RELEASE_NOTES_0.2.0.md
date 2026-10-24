# Veyra 0.2.0

**Stable · Android code 47**  
Promoted from completed **0.2.0-beta1 / code 46**.

![Pocket Riot · actual controls](asset-packs/previews/pocket-riot/collection.webp)

## Your music, your details

| Collection | Stable 0.1.9 | Stable 0.2.0 |
| --- | ---: | ---: |
| Full themes | 62 | **72** |
| Independent asset packs | 15 | **32** |
| Bundled launcher choices | 105 | **132** |
| Navigation designs | 3 | **16** |
| Scroll-player motion choices | Original | **15** |

### Pocket Riot

Ten cute, bold overlays: **Kitty Mayhem, Mochi Moshpit, Peach Bruiser, Bunny Voltage, Cherry Bombshell, Teddy Tantrum, Bubble Fang, Sugar Goblin, Cosmic Pudding and Sticker Syndicate**.

Each contains 119 roles, separate pressed transport buttons, licensed fonts, progress/material presets and an offline specimen. Seven Chaos Studio overlays also join stable. Apply an overlay over a supported theme and choose which categories it replaces; the theme's artwork and palette remain.

### Every pack gets a matching icon

![Actual glass waveform editions](design/launcher/pocket-riot/previews/glass-editions.webp)

Twenty-seven new AI glass editions complete explicit launcher bindings for every theme and overlay. All 72 themes and 32 overlays have matching bundled choices, correctly grouped in the picker. Home shows the selected icon. Fourteen deep control icon names now resolve to existing theme assets, including immersive controls, expand arrows and account/edit/delete actions.

The original waveform identity remains. Unknown packs cannot request arbitrary Android components. Eleven aliases share byte-identical legacy foreground resources, removing 55 duplicate images while preserving every pixel, family mask and launcher choice.

### Chaos Studio + Motion studio

Ten original AI wallpaper worlds join stable: Soda Sabotage, Velvet Vandal, Acid Daydream, Peach Panic, Ink and Ivory, Holo Havoc, Denim Disorder, Cherry Voltage, Mint Mutiny and Lunar Lollipop. Each has 123 roles and separate pressed transports.

Choose **Silk, Soft Zoom, Rise, Drift, Reveal, Quick Cut, Velvet, Spring, Diagonal or Shutter**, or retain Theme default. Set Relaxed, Balanced or Quick pace and tap the live preview. Theme default preserves pack motion when pace changes. Navigation, Settings, the account panel and player settling honor the selected profile or timing; native gesture mechanics remain.

Choose among **16 navigation designs** and **15 scroll-player transitions**, with theme/asset glyphs and Liquid Glass compatibility in both navigation layouts. All motion is finite; reduced motion and the existing scroll-animation switch retain priority.

### Reliability and polish

- Liquid Glass composes with theme/asset materials without opaque fills hiding its effect; the separate mini-player honors it too.
- Reject stale/cancelled Library and account results before likes and continuation side effects. Refresh indicators respect request ownership; ratings update atomically.
- Refresh player geometry on scene-style changes and navigation drag geometry on size/direction changes. Skip unused glass exports.
- Clear cancelled-hold click suppression without adding a click after a completed hold.
- Prevent Theme/Asset Studio from hiding mid-Apply. Asset Studio adds search and All/Downloaded filters.
- Preserve existing themes, overlays, queue/Home/search options, immersive modes, widgets, fonts and prior playback fixes. Existing published pack bytes are unchanged.

## Install and source

Build/install **code 47** for native changes and new launcher aliases. Signing identity must match an existing installation for an in-place upgrade. This source promotion does not publish a signed APK.

[Stable source ZIP](downloads/Veyra_Android_Source_0.2.0_Stable.zip) · [SHA-256](downloads/Veyra_Android_Source_0.2.0_Stable.zip.sha256) · [Verification](verification/STABLE-0.2.0-PROMOTION.md) · [Completed beta notes](RELEASE_NOTES_0.2.0-beta1.md)

Source packages include native inputs, icon masters, licenses and backend provenance. Runtime ZIPs are included losslessly in an offline library. `python3 scripts/restore_source_packs.py` restores their exact bytes; `restore_theme_sources.py` also restores optional authoring assets. Font pools and preview aliases have strict path/hash rules, and existing edits are preserved. Generated gallery captures remain in Git and can be reproduced from included renderers.

Stable catalogs target `main`; the completed preview keeps its own URLs and source archive. Historical stable 0.1.9 remains on its branch. Focused compile/regression checks are documented; real-device FPS, every provider/device path and signed-APK publication are separate checks.
