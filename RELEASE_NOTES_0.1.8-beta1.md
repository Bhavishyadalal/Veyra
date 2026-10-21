# Veyra 0.1.8-beta1

**Preview Android source · version code 27 · based on stable 0.1.7**

## Background visibility correction

- Fixed the opaque navigation layer that hid theme artwork on Home, Search and Library.
- Applied the same readable scene to Explore/category pages, album/artist/playlist details and Settings subpages. Real artist photos and album covers remain intact.
- Each transitioning page retains its own opaque base, preventing underlying page content from showing through. No background animation loop was added.

[Background fix verification](verification/BACKGROUNDS-0.1.8-beta1.md)

## Theme polish and independent fonts

- More visible themed backgrounds across Home, Search and Library, with a contrast-aware scrim and static artwork.
- Cleaner Home branding: removed the decorative waveform and tightened icon/wordmark alignment.
- **All** opens first in the icon picker, with all 74 choices searchable together; existing filters remain.
- Cached, revision-aware Studio previews with decoded-image placeholders, bounded thumbnail/detail sizing and no unnecessary preview fade-in.
- **Appearance → Fonts:** Follow theme, Veyra default and six licensed independent presets, with preview before Apply and saved choices across theme changes.
- Consistent themed glyphs and Back-button geometry; corrected bitmap glyph sizing while retaining accessible targets and existing actions.
- No character/gyro additions. Stable Android and published packs remain unchanged.

[Verification and known limits](verification/APPEARANCE-0.1.8-beta1.md)

## Keepsake collection

Five new themes bring the catalog to **46**: Patchwork Picnic, Clover Requiem, Sunroom Mixtape, Concrete Afterhours and Matcha Mischief. Each combines original AI artwork, licensed typography, readable palettes, coordinated transport/navigation glyphs and existing reduced-motion-aware theme behavior.

Their downloadable packs also work on **stable 0.1.7 / code 26**, including its original 256 px brand-icon validator. Stable receives the shared catalog and pack data; the following Android changes remain exclusive to this preview.

## Theme Studio and launcher selection

- Search theme names and descriptions while preserving All/Downloaded filtering and detail-page Back state.
- Browse larger landscape thumbnails in compact rows with natural-height text.
- Search the active icon collection; switching collection or glass family clears the previous query.
- See the current launcher selection, larger 64 dp previews and an explicit selected checkmark.
- Choose five new matching Glass Noir icons, bringing the beta to **74 choices**. Stable remains at 69.
- Preserve optional matching-icon selection, all existing aliases/actions, real music artwork, account integration and playback behavior.

Includes the stable theme-installation correction. Existing published packs remain unchanged. No APK, signing operation, Actions dispatch or stable UI promotion is included.

[Theme collection and downloads](themes/KEEPSAKE.md) · [Verification and remaining limits](verification/KEEPSAKE-0.1.8-beta1.md)
