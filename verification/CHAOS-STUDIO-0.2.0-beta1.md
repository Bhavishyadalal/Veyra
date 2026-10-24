# Chaos Studio · beta 0.2.0 build 45

## What ships

- **10 new full themes**, original AI portrait wallpapers, licensed display/body fonts, 123 asset roles each including four separate pressed transports. All prior packs remain byte-identical.
- **7 independent overlays**, 119 roles each, editable semantic SVGs, offline specimens and separate GitHub catalog/storage. Overlays retain the active theme's wallpaper/palette and honor existing category switches.
- **10 new page motion profiles** plus Theme default, three independent paces and an on-demand live settings preview. Navigation, Settings overlays, account panel and player settling use the chosen profile or timing. Native gesture mechanics remain unchanged.
- **10 additional scroll-player transitions**, retaining the existing five. All fifteen remain independent of Liquid Glass through the existing master switch.
- **10 additional navigation designs**, retaining the existing six. Both bar implementations use shared shapes/selected-glyph treatments and keep theme glyphs, labels, destination order, palette and Liquid Glass compatibility.

Settings → Appearance → Navigation contains visual navigation choices and Motion studio. Motion controls are also next to Scroll player animations. Existing selections are not reset by installing this beta. Explicit page-motion choices override theme page presets; Theme default preserves pack motion. Reduced motion wins over every preset. Theme-specific subtle-motion constraints remain respected.

The current beta catalogs contain **72 themes / 22 asset packs**; the **105 bundled launcher aliases** are unchanged. No new launcher aliases are claimed. New theme-brand artwork reuses reviewed official-wave glass editions; wallpapers and seven SVG control treatments are new. Theme packs require the existing code-32 engine; independent overlays require code 33. The new settings require code 45.

[Actual installed assets and wallpapers](../themes/previews/chaos-studio/index.html) · [Theme collection](../themes/CHAOS_STUDIO.md) · [Overlay collection](../asset-packs/CHAOS_STUDIO.md)

## Rendering and persistence

No permanent ticker, particle emitter, image loader or backdrop recorder was added. Page transforms change alpha/position/scale only; viewport measurement remains snapped. Scroll transitions are finite and use existing shared bounds. New navigation accents draw inside existing touch targets. Designs without a travelling selection lens suppress its fill/export. Downloaded packs remain data-only and use existing bounded native material/seek/motion presets.

AppMotionStyle, MotionTempo and appended navigation/scroll enums use existing safe JSON coercion. Legacy settings retain Theme default/Balanced/Flow; unknown future values keep defaults. Remembered distinct preference projections avoid root invalidations for unrelated settings. Theme catalog capacity is raised from 64 to 96 entries so the new 72-entry catalog can load; the 64 KiB byte limit, canonical URLs, unique IDs and per-pack checks remain enforced. No playback/queue/network ownership logic is changed by these presentation choices.

## Complete offline source download

The collection adds enough runtime data to exceed GitHub's single-file limit with a naive ZIP. `package_source.py` therefore stores immutable ZIP bytes losslessly in `SOURCE_DATA_PACKS.tar.xz`, with per-pack length/SHA-256 records. Native build inputs, icon masters, licenses, code and renderer inputs remain expanded. Android Studio can build directly; offline pack authoring runs `python3 scripts/restore_source_packs.py`. `restore_theme_sources.py` automatically restores this library before optional theme sources.

Restoration validates decompression limits, exact membership, paths, sizes and all original ZIP hashes before writing; existing files/edits are preserved. No network is needed. Published runtime pack ZIPs and all older source archives stay unchanged. Source ZIP verification covers the library and each inner pack.

## Focused checks

- Android production Kotlin/resources and debug version metadata.
- Production installer tests for all ten new themes and seven overlays, including original checksums, palettes/presets, image decoding/bounds, font decoding and separate pressed controls.
- Every motion profile/pace/direction respects disabled/reduced navigation and keeps immediate viewport sizing.
- 240 navigation/scroll settings combinations round-trip; unknown/legacy defaults remain safe. Native previews retain selection/actions with theme/asset visuals and large text/RTL; existing glass compositing checks remain.
- Static gallery and website checks at phone/desktop widths; production website build/typecheck.
- Complete archive membership/checksums, offline byte-exact pack restoration, invalid-checksum rejection and user-edit preservation.

These are source/compiler/regression checks, not Android GPU/frame-rate measurements. Test build 45 on a device before judging animation smoothness. No signed APK is published by this source push. Stable main and stable/0.1.9 remain frozen at build 40.
