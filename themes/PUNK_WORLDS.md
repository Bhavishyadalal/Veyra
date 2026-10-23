# Punk Worlds — five loud little universes

**Veyra 0.1.9-beta1 · build 32 · five full themes · 123 asset roles each**

[Actual runtime asset overview](previews/punk-worlds/collection.png)

| Theme | Materials / real display font | Download | Actual assets | AI direction |
| --- | --- | --- | --- | --- |
| **Riot Candy** | Lime/pink gel, chrome, torn zine · Rubik Wet Paint | [ZIP](packs/riot-candy-v1.zip) | [Specimens](previews/riot-candy/asset-specimens.png) · [All 123](previews/riot-candy/all-assets.png) | [Board](previews/riot-candy/art-direction.png) |
| **Chrome Venom** | Toxic mint, titanium, rivets · Rubik Glitch | [ZIP](packs/chrome-venom-v1.zip) | [Specimens](previews/chrome-venom/asset-specimens.png) · [All 123](previews/chrome-venom/all-assets.png) | [Board](previews/chrome-venom/art-direction.png) |
| **Bubble Riot** | Blue/pink jelly, puffy stickers · Rubik Bubbles | [ZIP](packs/bubble-riot-v1.zip) | [Specimens](previews/bubble-riot/asset-specimens.png) · [All 123](previews/bubble-riot/all-assets.png) | [Board](previews/bubble-riot/art-direction.png) |
| **Xerox Afterparty** | Charcoal/cream, red print accents, concert zine · Lacquer | [ZIP](packs/xerox-afterparty-v1.zip) | [Specimens](previews/xerox-afterparty/asset-specimens.png) · [All 123](previews/xerox-afterparty/all-assets.png) | [Board](previews/xerox-afterparty/art-direction.png) |
| **Cosmic Vandal** | Cyan/pink holographic chrome · Bungee Shade | [ZIP](packs/cosmic-vandal-v1.zip) | [Specimens](previews/cosmic-vandal/asset-specimens.png) · [All 123](previews/cosmic-vandal/all-assets.png) | [Board](previews/cosmic-vandal/art-direction.png) |

## What is installed

Each pack contains **113 normal functional glyphs, four player pressed-state variants, an in-app Veyra brand, two real fonts, and three artwork roles sharing one original wallpaper**. This is 123 roles, 125 declared files, and one manifest. Primary player assets are 256 px; smaller functional glyphs are 128 px; SVG masters remain editable/scalable. Manrope is the readable companion font for song titles, body text, labels and Settings rows.

The new engine preserves authored icon colors (`iconStyle: original`) after successful decoding. Native loading/error placeholders retain their tint. Disabled alpha and destructive/error tint remain app-owned. Player buttons preload the small pressed-state image alongside their normal image and keep the normal image visible until the pressed state is ready. Semantics, fixed touch targets and real playback/seek logic remain native.

Punk heading styles use bright palette accent/secondary colors, with additional heading contrast checks and scene scrims that account for those colors. Imported/independent font choices still work; app styles continue to suspend downloaded-theme visuals as previously designed. The installed fonts are unmodified upstream SIL Open Font License files with included notices and source attribution in `design/punk-fonts/SOURCES.json`.

## Matching native styles

| Theme | Controls | Progress | Motion implemented in the app |
| --- | --- | --- | --- |
| Riot Candy | Rounded gel shapes, cached gloss | Gel dots and star head | Finite spring press/release; short depth reveal |
| Chrome Venom | Riveted cut frames, metal glints | Braided cable | Compression/release; quick lateral reveal |
| Bubble Riot | Asymmetric puffy sticker shapes | Candy beads | Finite spring press/release; gentle upward reveal |
| Xerox Afterparty | Uneven paper frames, stamp edges | Spliced tape markings | Short press tilt/release; paper-like lateral reveal |
| Cosmic Vandal | Asymmetric chrome curves, prism glints | Plasma glints and star head | Short recoil tilt/release; depth reveal |

The material finish uses bounded cached native drawing, not downloaded shaders or code. Tracks follow the actual progress/input fraction and honor RTL. There are no looping background animations or reactive characters. App/system reduced-motion and subtle-motion preferences win.

## AI boards and actual previews are different artifacts

Ten original images were generated: five material-collage wallpapers and five creative asset-direction boards. **AI boards are art direction**, including illustrated lettering and peel/shutter/comet storyboards. Their invented font names and physical effects are not installed fonts or promised simulations. Runtime icons/skins are original functional SVG designs rasterized to PNG, not crops of an AI board. The separate actual-asset specimen sheets show the real files/fonts and native-inspired progress/motion studies. Neither gallery is an Android screenshot or an FPS measurement. Native page transitions use scale, slide and fade; there is no physical paper-peel or steel-shutter simulation in this checkpoint.

The full delivery bundle includes original generated PNG art, optimized runtime wallpapers, every asset, SVG masters, font/provenance notices, installable theme ZIPs and offline HTML galleries. No APK is included.

## Compatibility and scope

Rebuild/install the **build-32 beta APK once**. Builds 31 and earlier reject these packs; catalog minimum versions hide them appropriately. All 57 earlier published packs and minimum versions remain unchanged. The current beta catalog contains 62 packs; stable catalogs and 74 launcher aliases are unchanged. These are in-app brands, not new launcher aliases. Further packs using supported build-32 roles/presets do not need another engine rebuild.

**These are full themes. Independent asset packs that layer over another selected theme remain a separate planned feature.** This change does not claim that overlay system exists. Optional custom Home styles, real music/artist artwork, service logos, OS interfaces/widgets and system media controls retain their existing identities and supported behavior.

Schema 4 adds opt-in `iconStyle` (`tinted`/`original`) and `typeStyle` (`plain`/`punk`). The default values preserve old packs. New `punk_*` materials, seek styles `gel/cable/beads/tape/plasma`, color/type flags and `_pressed` transport roles require `minAppCode >= 32`. Existing file-count, ZIP/uncompressed byte, path/extension, image-bound, palette-contrast, checksum and strict-URL limits stay enforced. Pack size remains bounded for installation safety; the delivered full-resolution art is also available separately.

## Rebuild and authoring

- `themes/build_punk_worlds.py`: reproducible glyph/brand/pack builder (CairoSVG required); original backgrounds must exist first. Published versions are immutable.
- `themes/render_punk_worlds.py <output>`: standalone actual-asset HTML specimens and every-role gallery. Browser screenshots are generated separately.
- `themes/source-icons/<id>/`: full original SVG masters.
- `themes/punk-art-sources.json`: original generated-image filenames; runtime art and AI boards remain under their respective theme/preview folders.
- Compact Android source ZIPs retain all Gradle font-resource inputs and notices. Optional non-build font copies for advanced/punk authoring, including the new punk font library, may be mapped by checksum to their included theme ZIPs alongside editable images. Run `python3 scripts/restore_theme_sources.py` to restore authoring images/fonts; it validates paths/hashes and never overwrites existing files. App builds do not require this restoration.

[Focused verification and device follow-up](../verification/PUNK-WORLDS-0.1.9-beta1.md)
