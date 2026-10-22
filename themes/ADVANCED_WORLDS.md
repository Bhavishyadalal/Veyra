# Advanced Worlds — five complete control themes

**Veyra 0.1.9-beta1 · Android build 31 · theme schema 4**

[Compare the collection](previews/advanced-worlds/collection.png)

| Theme | Material / progress / motion | Pack | Screen study | Every asset |
| --- | --- | --- | --- | --- |
| **Switchback Radio** | Pixel-cut controls · road markings · brisk lateral reveal | [ZIP](packs/switchback-radio-v1.zip) | [Home / Player / Settings](previews/switchback-radio/screens.png) | [119 roles](previews/switchback-radio/all-assets.png) |
| **Komorebi Hour** | Soft botanical corners · leaf vine · gentle upward reveal | [ZIP](packs/komorebi-hour-v1.zip) | [Home / Player / Settings](previews/komorebi-hour/screens.png) | [119 roles](previews/komorebi-hour/all-assets.png) |
| **Rooftop Reverie** | Art Deco cut frames · skyline · restrained depth reveal | [ZIP](packs/rooftop-reverie-v1.zip) | [Home / Player / Settings](previews/rooftop-reverie/screens.png) | [119 roles](previews/rooftop-reverie/all-assets.png) |
| **Apex Telemetry** | Asymmetric instrument panels · sector ticks · fast lateral reveal | [ZIP](packs/apex-telemetry-v1.zip) | [Home / Player / Settings](previews/apex-telemetry/screens.png) | [119 roles](previews/apex-telemetry/all-assets.png) |
| **Cinder Kata** | Flame-leaf corners · ember ribbon · short depth reveal | [ZIP](packs/cinder-kata-v1.zip) | [Home / Player / Settings](previews/cinder-kata/screens.png) | [119 roles](previews/cinder-kata/all-assets.png) |

The screen studies use actual artwork, glyphs, fonts and palettes in browser compositions. **They are design previews, not Android screenshots.** Song titles, cards and artwork placements in these studies are illustrative; the app retains its real screens and music content.

## Compatibility

Build and install the updated **build-31 beta APK once** to enable this expanded engine. Build 30, stable 0.1.8 and stable 0.1.7 cannot install these schema-4 packs. The catalog's minimum-version filter and installer enforce that boundary. An APK has not been assembled or published by this change.

The previous 52 packs and their ZIP bytes remain unchanged. Earlier stable-compatible packs retain their original minimum versions. After build 31 is installed, additional packs using its supported roles/presets can be downloaded without rebuilding the APK. New engine features and launcher aliases still need an APK rebuild.

## What changes

Each theme supplies **119 asset roles**, with **113 transparent functional glyphs**, an in-app Veyra brand mark, a heading font, Manrope body font, and Header/Player/Studio artwork roles intentionally sharing one original illustration. Each ZIP contains 121 declared files plus `theme.json`, including four provenance/font notices. Original SVG masters are in `source-icons/<theme>/`.

- **Player:** play/pause, previous/next, queue, lyrics, shuffle, repeat/repeat-one, favorites, output, volume, account/group actions, overflow, downloads and surrounding native frames. Progress and volume use the real playback/input fraction with theme-specific native track decoration. Player press feedback retains fixed touch regions; flame controls add a short tilt.
- **Home:** navigation, account, customization, layout, history, favorites, add/download, play actions and chevrons use the semantic glyph registry. Shared action/account cards receive theme geometry; fonts, colors and scene artwork remain coherent.
- **Settings:** category/action glyphs, chevrons, on/off switch thumb glyphs, slider thumb, slider track and section-group frames. Material switches retain their native track/input behavior; their palette and glyphs change with the theme.
- **Deep screens:** shared native icon call sites across dialogs, sheets, artist/library/search screens, immersive player and Theme Studio use the same semantic lookup and existing theme artwork wrapper. Static edge ornaments vary by world.
- **Motion:** bounded native page presets select lateral, upward or depth reveals. Reduced-motion and subtle-motion preferences win; no scripts, shaders, looping background tickers or reactive characters are installed by a pack.

The reviewed registry maps **170 native/custom vector names** to semantic roles. `ThemeGlyphs.key` preserves the native glyph when a semantic role is unavailable or an image fails. A new app feature with a new semantic action must add a reviewed mapping/asset; there is no promise that every future icon will magically acquire bespoke artwork.

Service logos (Google, Spotify, Dolby and similar), account photos, real song/artist artwork, launcher aliases, platform permission/browser interfaces, OS widgets and system media controls retain their identity and appropriate native behavior. The new brand marks are **in-app**, not five new launcher aliases. Optional custom Home styles intentionally suspend downloaded-theme visuals, as before. Independent user font/artwork overrides remain available.

## Authoring contract

1. `theme.json`: schema 4, `minAppCode >= 31`, bounded `materialStyle` (`classic`, `pixel`, `botanical`, `deco`, `telemetry`, `flame`) and `seekStyle` (legacy presets or `road`, `vine`, `skyline`, `telemetry`, `flame`). `sceneStyle` remains a supported legacy scene preset; these packs use `mist` with native material ornaments.
2. `artwork/preview.webp`: original production artwork, shared across the three artwork roles; `artwork/brand.png`: 256 px themed Veyra mark.
3. `icons/<role>.png`: transparent 128 px functional glyphs. Native tint, labels, selection/error states and accessible touch behavior remain app-owned.
4. `fonts/*.ttf`, `licenses/*`: unmodified licensed fonts and original-artwork provenance. Keep upstream notices intact.
5. `files`: exact relative paths and SHA-256 for every runtime file; outer ZIP also has a checksum sidecar and catalog byte count/hash.
6. Schema 4 allows at most 128 declared files / 129 archive entries. Schemas 1–3 keep the 48-file limit. Existing 12 MiB ZIP / 32 MiB total / 8 MiB individual limits, strict URLs, path/extension checks, image bounds and contrast rules remain enforced. No executable assets, downloaded layouts or arbitrary motion code.

`advanced-glyphs.json` is the original path/alias source. Run `python3 themes/generate_advanced_registry.py` to generate the explicit Kotlin role registry; `build_advanced_worlds.py` builds packs with CairoSVG. Production WebP artwork must already exist; use `scripts/restore_theme_sources.py` after extracting a compact source ZIP if you need editable image files. `render_advanced_worlds.py <output>` generates offline HTML asset galleries and screen studies. Browser screenshots are a separate optional step. Published ZIP versions are immutable; bump a pack version before changing its bytes.

Art direction came from the five owner-provided references: a pixel night road, golden-hour neighborhood, coral-cloud metropolis, endurance racer and anime fire. The delivered images are new AI-generated compositions; no supplied photos, recognizable franchise characters, badges or sponsor logos are redistributed. Original full-resolution generations remain in the workspace; the app packs use optimized WebP encodings. Fonts keep their upstream OFL notices.

[Focused verification and device limits](../verification/ADVANCED-WORLDS-0.1.9-beta1.md)
