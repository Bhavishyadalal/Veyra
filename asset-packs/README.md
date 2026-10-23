> **Stable 0.1.9 / build 38:** [ten Night Vault gothic/punk overlays and matching icons](NIGHT_VAULT.md) join the original five Layer Lab packs. All 15 can layer over existing themes.

# Veyra / Layer Lab

**Five independent asset packs · beta 0.1.9 build 33+**

These are overlays, not themes. All 62 existing themes remain available and their
published ZIPs are unchanged. Select any theme or custom app style, then open
**Settings → Appearance → Asset packs**. Download a pack and choose **Apply assets**.
Your selected theme/style keeps its background artwork, identity and palette.
Remove the override with **Restore theme assets**; this never removes a theme.

| Pack | Lettering | Signature |
|---|---|---|
| Spray Parade | Bangers | Graffiti paint stamps, torn edges, lime/pink ink |
| Paper Pop | Lilita One | Scalloped paper controls, offset collage shadows |
| Ink Rebel | Rock Salt | Dry-brush washi stamps and handwritten display type |
| Laser Club | Audiowide | Beveled neon arcade keys and chrome outlines |
| Thread Riot | Bungee | Denim patches, stitched frames and varsity lettering |

Each pack has **113 semantic glyphs, four pressed transport skins and two fonts**.
Player transports are 256 px; other glyphs 128 px; original SVG masters are included
under `vectors/`. Native seek, shapes, finite press feedback and page transitions
use supported presets. Pack page transitions respect reduced/subtle motion.
No continuous shader/ticker, remote executable code or launcher aliases are added.
Fonts are licensed, unmodified sources; body text stays Manrope. A chosen/imported
font takes priority; choose **Follow theme** for pack fonts. Custom app styles also
accept overlays. Their layout and background remain intact.

Independent switches control icons, fonts, controls/progress, pack page motion and
heading ink. Heading ink falls back to theme text when its contrast against theme
surfaces is insufficient. Some app-owned platform controls, service logos,
media/notification widgets and unmatched glyphs retain their functional native
appearance. This does not replace Android launcher icons.

## Actual assets versus art direction

- [AI concept sheet](previews/art-direction.png): illustrative material direction.
- Each `previews/<id>/specimen.png` uses the **real PNGs and real font**.
- Each `previews/<id>/all-assets.png` previews all 119 runtime roles.
- Embroidered fabric, paint texture and animated physical peeling in concept art
  are not claimed as live simulations. Actual controls use the supplied vector
  designs and bounded native motion. Device/FPS validation remains pending.

## Preview every shipped asset

- [Spray Parade](previews/spray-parade/all-assets.png)
- [Paper Pop](previews/paper-pop/all-assets.png)
- [Ink Rebel](previews/ink-rebel/all-assets.png)
- [Laser Club](previews/laser-club/all-assets.png)
- [Thread Riot](previews/thread-riot/all-assets.png)
- [Collection of actual specimens](previews/collection.png)

## File contract

GitHub `catalog.json` is independent of `themes/catalog.json`; the bundled
`app/src/main/assets/asset-catalog.json` works as an offline listing fallback.
Each immutable `packs/<id>-v1.zip` contains `assets.json`, `icons/*.png`,
`fonts/*.ttf` and license text. `previews/specimen.webp` is also included for reliable offline previews after installation;
remote versions use the existing versioned Coil disk/memory cache.

`assets.json` schema 1 includes ID/name/author/version/minAppCode, asset role map,
SHA-256 per file, materialStyle, seekStyle/height, corners, headingInk and motionStyle.
It contains **no theme palette or background roles**. SHA-256, exact archive member
sets, decoded bounds/fonts, size caps, path rules and supported preset validation
run before atomic installation. Active selection and layer switches use separate
preferences/storage from themes and survive restarts. Downloads survive configuration
changes. Selection/reset never stops playback or mutates the selected theme.

One updated APK is needed to receive the build-33 engine. Subsequent compatible
asset packs are downloaded as data; this is not a signed APK or local ZIP importer.
New runtime behavior still requires an app update.

## Rebuild previews and packs

`PYTHONPATH=/tmp/veyra-theme-build-deps python3 asset-packs/build_collection.py`
requires Python Pillow and CairoSVG. On a downloaded source ZIP, run
`python3 scripts/restore_theme_sources.py` first to restore optional authoring
images/fonts. Existing editable files are never overwritten. Published ZIP bytes
are immutable; change a pack version before altering any shipped asset.

Build 34 optimizes runtime glyph/font caching, container outlines and transitions without modifying these ZIPs. See [performance notes](../verification/ASSET-PERFORMANCE-0.1.9-beta1.md).
