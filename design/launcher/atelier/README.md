# Glass Atelier — 12 standalone Veyra icons

These are additional launcher choices built around Veyra's current **Glass Noir**
silver waveform and black glass case. Each was created with AI image editing using
`design/launcher/wave_glass.png` as its reference.

Open **Settings → App icon → Glass Atelier** and choose one. These choices are
independent of the active theme; they do not replace theme-linked icons or change
Home branding. The collection is included in the refreshed **0.1.7-beta1 / code 25**
source and needs an app rebuild. It is not a downloadable theme pack or an APK.

| Icon | Design | Artwork |
| --- | --- | --- |
| **Aurora Veil** | Emerald and cyan aurora ribbons in dark polar glass. | [View](aurora-veil.webp) |
| **Sakura Pearl** | Pearl silver, blush reflections and delicate cherry blossoms. | [View](sakura-pearl.webp) |
| **Obsidian Gold** | Deep obsidian glass with precise gold Art Deco accents. | [View](obsidian-gold.webp) |
| **Glacier Blue** | Ice-blue refractions and fine frozen crystal details. | [View](glacier-blue.webp) |
| **Ruby Velvet** | Rich ruby glass, crimson edges and garnet glints. | [View](ruby-velvet.webp) |
| **Emerald Dew** | Jade glass, botanical etchings and clear dew droplets. | [View](emerald-dew.webp) |
| **Lunar Eclipse** | Smoked platinum glass with a quiet eclipse halo. | [View](lunar-eclipse.webp) |
| **Cosmic Prism** | Violet, magenta and cyan prism refractions on black. | [View](cosmic-prism.webp) |
| **Rose Quartz** | Blush quartz, mineral striations and champagne highlights. | [View](rose-quartz.webp) |
| **Ember Forge** | Basalt-black glass, copper trim and subtle amber embers. | [View](ember-forge.webp) |
| **Ocean Opal** | Turquoise opal, pearly caustics and tiny glass bubbles. | [View](ocean-opal.webp) |
| **Royal Sapphire** | Sapphire glass, platinum trim and refined faceted corners. | [View](royal-sapphire.webp) |

[48 dp masks · first six](../../../verification/atelier-preview/masks-one.webp) ·
[48 dp masks · next six](../../../verification/atelier-preview/masks-two.webp) ·
[Large text](../../../verification/atelier-preview/picker-large-text.webp) ·
[RTL / large text](../../../verification/atelier-preview/picker-rtl-large-text.webp)

The preview studies use native shared Compose components with illustrative layout.
They are not Android launcher screenshots. [Verification and device limits](../../../verification/ATELIER-ICONS-0.1.7-beta1.md).

The checked-in 512 px WebP masters are compact asset exports of the AI designs.
The native launcher uses five density sets, adaptive resources and the official
Veyra monochrome waveform. Rebuild assets offline with
`python3 scripts/build_atelier_icons.py`; Pillow is already used by existing asset builders.
Original full-size generation outputs remain in the session workspace.

Existing Glass Noir, legacy glass choices, all eleven themes and theme-linked
launcher assets remain unchanged. Stable 0.1.6 is unchanged.
