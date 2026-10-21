# Keepsake · Five little worlds to live in

**Theme packs: stable 0.1.7 and beta 0.1.8. New launcher choices: beta 0.1.8 only.**

| Theme | Atmosphere | Type & controls | Download |
| :--- | :--- | :--- | :--- |
| **Patchwork Picnic** | A cream puppy, strawberry charms, caramel gingham and faded denim | Fredoka, soft transport, stitched details, bubble timeline | [ZIP](packs/patchwork-picnic-v1.zip) · [SHA-256](packs/patchwork-picnic-v1.zip.sha256) |
| **Clover Requiem** | An original masked meadow spirit and black cat among tiny flowers | Space Grotesk, moss and periwinkle, flower compass, ember timeline | [ZIP](packs/clover-requiem-v1.zip) · [SHA-256](packs/clover-requiem-v1.zip.sha256) |
| **Sunroom Mixtape** | Headphones, vinyl sleeves and late-afternoon light on walnut floors | Bold Manrope, cassette details, warm amber transport | [ZIP](packs/sunroom-mixtape-v1.zip) · [SHA-256](packs/sunroom-mixtape-v1.zip.sha256) |
| **Concrete Afterhours** | Underground streetwear, graphite walls and safety-amber lights | Rajdhani, architectural glyphs, sharp geometry, rail timeline | [ZIP](packs/concrete-afterhours-v1.zip) · [SHA-256](packs/concrete-afterhours-v1.zip.sha256) |
| **Matcha Mischief** | A black cat, fluffy bunny and frog-hood bear in a green paper collage | Fredoka, clover details, cream transport, bubble timeline | [ZIP](packs/matcha-mischief-v1.zip) · [SHA-256](packs/matcha-mischief-v1.zip.sha256) |

Every pack includes an original AI-generated scene, matching Veyra Home branding, licensed fonts, readable dark surfaces and thirteen functional navigation/player glyphs. Real song artwork and existing player actions remain under the app's control. Existing reduced-motion behavior applies; packs add no animation engine or permanent animation loop.

## Stable without a rebuild for these themes

On stable 0.1.7, open **Settings → Appearance → Theme studio**, refresh the catalog, download a Keepsake theme, then choose **Apply theme**. The new packs target code 26, use only existing schema 2/3 capabilities and include **256×256 brand artwork**, within even the original stable validator's limit. They need no new launcher alias to install or apply.

Stable keeps its current launcher choices. Applying a Keepsake theme there does not offer one of the new matching launcher icons. Home branding inside the app is part of the downloaded theme and remains available.

## Beta-only additions

Beta **0.1.8-beta1 / code 27** adds five matching Glass Noir launcher choices: gingham-and-strawberry enamel, moss glass and spirit charm, amber cassette glass, underground chrome, and matcha cat-ear glass. Each keeps the official connected three-lobed Veyra waveform. Matching launcher selection remains optional and unchecked by default.

The beta also adds searchable Theme Studio, larger landscape collection thumbnails, icon search within each group, a current-icon label, larger icon previews and a clearer selected state. Existing collection filters, download/cancel, Apply confirmation, removal and all older icon choices remain.

## Artwork and verification

- [Browse scenes and native component studies](index.html). Studies are illustrative native Compose layouts, **not Android device screenshots**; player studies use demonstration cover art.
- Original image-generation masters remain outside the source packages. Checked-in WebP assets and SVG glyph sources support reproducible pack compilation with `python3 themes/build_keepsake_collection.py`. Only the beta uses `--launcher` to compile new Android launcher resources.
- Packs contain unmodified fonts with their upstream SIL Open Font License notices. User reference images are not redistributed; characters were designed as original figures, with no claimed franchise affiliation.
- [Beta verification](../verification/KEEPSAKE-0.1.8-beta1.md) · [Stable compatibility](../verification/KEEPSAKE-STABLE-0.1.7.md).
