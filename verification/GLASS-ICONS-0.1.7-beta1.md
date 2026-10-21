# Glass Noir theme icon refresh

**App:** 0.1.7-beta1 / code 25 · **Updated packs:** six reference themes, v2

## Correct brand reference

The reference is Veyra's current **black-and-white Glass Noir icon**, supplied by
`design/launcher/wave_glass.png` and used by the default launcher. Its connected
silver three-lobed glass waveform, tall center and black glass case define all
six replacements. AI image editing used that exact checked-in image as input.

| Theme | Glass details |
| --- | --- |
| Pumpkin Purr | Warm amber reflections, small pumpkin and paw details |
| Sunset Crew | Violet and honey light, coastal sunset reflections |
| Starry Spirits | Teal and moon-gold glass, small stars and gentle ghosts |
| Analog Reverie | Champagne highlights, vinyl grooves and a swallow etching |
| Petal Promise | Rose glass reflections, satin bow and tulip petals |
| Moonlit Herbarium | Lilac glass, engraved botanicals and crescent moon |

The six reviewed masters live in `themes/launcher-art/`. The builder derives Home
brand images and five launcher-density sets from those masters. High-quality WebP
keeps launcher resources compact. Adaptive resources retain their component names;
Android monochrome mode now uses the official Veyra waveform vector. The default
Glass Noir assets, other launcher choices and the five earlier themes are unchanged.

## Pack integrity and updating

- These are **v2 theme packs**, with the app version unchanged. All published v1
  archives and sidecars remain byte-for-byte intact.
- Comparing each v1/v2 archive shows only three changed entries: `artwork/brand.webp`,
  `licenses/ARTWORK.txt`, and `theme.json` (version and integrity hashes).
- Scene art, fonts, functional glyphs, palettes, geometry, motion, playback and
  account behavior are unchanged by this refresh.
- Refresh Theme Studio, open a theme, choose **Download theme update**, then Apply.
  Choose the matching launcher only if desired. Rebuild the latest source to get
  the updated bundled launcher artwork; downloaded branding can update separately.
- Stable 0.1.6 / code 22 has no theme installer and is unchanged. Backend provenance,
  font licenses and Sunset Crew character attribution are preserved.

## Verification

- Android production-debug Kotlin/Java/resource compilation and **17 focused tests**
  passed: ThemePackTest (14), ThemeLauncherTest (3). This covers current v2 pack
  installation, hashes, runtime catalog URLs, compatibility, safe paths, grouped
  icon coverage, alias mappings and exactly one enabled launcher default.
- Following the density resource encoding change, `:app:processProdDebugResources`
  passed again; the Kotlin/Java code did not change. No APK task was run.
- All density assets were decoded and their expected pixel dimensions checked.
- **Seven native component studies** rendered and were visually inspected: six
  refreshed Home studies, plus all six themed icons and the unchanged default
  in rounded-square/circle masks at 48 dp.
- Website type checking, production build and the focused desktop/mobile
  download-link checks passed (2 browser tests), including the v2 pack URLs.

[48 dp mask comparison](../themes/previews/pumpkin-purr/glass-icons.webp) ·
[Updated Home studies and downloads](../themes/README.md#reference-collection--six-more-worlds)

These are desktop Compose component/mask studies, **not actual Android launcher
screenshots**. They do not prove launcher switching, cache refresh timing, TalkBack
or playback on a real device. No APK publication, stable promotion or Vercel
production deployment is claimed. The icon selection remains optional.
