# Liquid Glass with themes and asset packs · beta build 42

Both navigation implementations previously selected their opaque theme surface before checking Liquid Glass. Asset-only overlays also create an effective theme, so they bypassed the enabled glass setting as well.

## Fix

- Liquid Glass now takes priority in both the folding navigation and the separate floating navigation bar.
- Theme navigation adds decoration only over glass, preserving ornaments and borders without repainting the backdrop opaque. The decoration clip matches the glass lens shape. Glass off retains the original themed material and frame.
- The separate mini-player now honors the glass toggle. The folding mini-player continues to share its navigation surface.
- Glass tint uses the effective theme/asset palette; full glass respects theme control opacity. Existing glyphs, fonts and selection colors remain.
- Android support gates remain: glass requires API 31+, lens refraction API 33+. Reduce dynamic blur still disables sampling and selects the solid fallback. The existing 0.33 sampling scale is unchanged; no new full-page capture or live preview player is added.

The version remains **0.2.0-beta1**, now **code 42**. Stable app branches remain at 0.1.9 build 40; all published theme/asset packs are preserved. Rebuild and install the beta app to receive this code fix.

## Verification and recovery

The initial local build 42 commits were not restored into the republished environment. This patch was reconstructed from the documented changes on the published build 41 beta baseline, then checked again before publication.

- Production Android release Kotlin/resources compile and release/debug metadata generation.
- `ThemeNavigationGlassTest` renders the production overlay over a two-color backdrop for classic, sketch, theme-plus-assets and assets-only configurations. Eight glass-on/off cases verify that glass decorations leave interior backdrop pixels visible while glass-off retains its themed fill.
- This native offscreen check verifies overlay compositing, not Android GPU blur/refraction. Real-device appearance and FPS remain unverified.
- Source ZIP membership, hashes and backend provenance are verified; stable and completed beta archives retain their original bytes.
- Matching website text is built/typechecked and its stable/preview links checked at phone and desktop widths.

For visible glass effects on a supported device, enable Liquid Glass and leave Reduce dynamic blur off. The source ZIP is an Android Studio project, not an APK; Git publication does not sign or publish an APK.
