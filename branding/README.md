# Veyra icon assets

`veyra-icon.png` is the full-color option 3 artwork. `veyra-foreground.png` is its transparent Android foreground. The launcher uses a 14 dp inset inside the 108 dp adaptive layer to keep the ribbon within the mask. Circle, rounded and squircle mask checks clipped zero foreground pixels above alpha 32 in the 324 px test render.

The full-color PNGs were prepared with the built-in image generation tool. Small native/system monochrome icons and the web SVG use a simplified vector V. Alternate Android launcher foregrounds now use the same Veyra ribbon, with their existing backgrounds retained.

Foreground extraction prompt:

> Use case: background-extraction. This is an Android adaptive launcher icon foreground export of the supplied selected Veyra icon. Remove ONLY the purple square background; retain the exact folded V ribbon shape and its cream, peach and lavender colors, contours and internal shading. Center the ribbon, preserve proportions, make its width about 55 percent of the full square canvas and height about 40 percent, with generous even transparent margin so all of it fits within a circle mask. Genuinely transparent background, no shadow outside the ribbon, no checkerboard, no text, no new shapes. Preserve the selected identity, no redesign.

The generated foreground retained a wider mark; native insets provide the required safe area. No runtime image generation or external font download is needed.
