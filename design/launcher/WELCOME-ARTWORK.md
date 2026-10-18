# Welcome campaign artwork

The first-install welcome uses the same original liquid-glass campaign backgrounds as Veyra's website media kit. These contain no baked-in copy: Android renders the actual app icon, brand font, localized headings and controls independently.

- `app/src/main/res/drawable-nodpi/welcome_glass_portal.webp`: 960 × 540, landscape glass portal.
- `app/src/main/res/drawable-nodpi/welcome_glass_frequency.webp`: 600px wide portrait crystal waveform.
- Full-resolution originals: `media-kit/artwork/campaign-landscape-source.png` and `campaign-portrait-source.png`. Both were generated for Veyra on 18 October 2026.

The two Android WebPs total about 131KiB. They are bundled locally and decoded by the existing Coil image loader; welcome does not fetch campaign images from a server. Landscape welcome art fills its bounded frame; portrait art uses Fit to retain the complete sculpture. Artwork has no TalkBack content because the surrounding heading and controls provide the meaning. A short graphics-layer reveal follows the existing reduced-motion/foreground policy; there are no new infinite particle loops, blur or shader effects.

The Android launcher icon and the owner's authorized Super Malibu wordmark are unchanged. The campaign accent applies to Welcome only; the rest of the app continues to honor the user's selected appearance.
