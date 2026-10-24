# Veyra 0.2.0-beta1

**Preview development · Android version code 43**

This development cycle starts from the user-tested **stable 0.1.9 build 40**.

## Navigation studio · build 43

- Choose **Floating, Docked, Minimal, Capsule, Ribbon or Orbit**. The folding bar now honors the chosen design, fixing shape selections that previously only affected the separate navigation layout.
- Choose **Flow, Glide, Lift, Soft Fade or Elastic** for scroll-player transitions. The existing master switch still works without Liquid Glass, and reduced motion keeps the static navigation/mini-player layout.
- Preview navigation shapes and themed glyphs in Settings → Appearance → Navigation. Scroll transition choices are also next to the existing Scroll player animations switch.
- Preserve theme colors, asset-pack icons and fonts, custom destinations/labels and Liquid Glass. Explicit bar designs retain their geometry even with decorative asset materials.
- Save choices across restarts, expand navigation when presentation changes and keep old/future settings safe through existing JSON fallback behavior.

[Design, previews and checks](verification/NAVIGATION-STUDIO-0.2.0-beta1.md). Rebuild/install **code 43**; no signed APK is published by this source update.

## Liquid Glass + themes · build 42

Liquid Glass now takes priority over opaque theme/asset navigation fills in both navigation layouts. Themed ornaments, colors, icons and fonts remain; the separate mini-player honors the toggle too. Glass tint follows the effective palette and control opacity. Disabling glass retains the original themed material. Android support gates, reduced dynamic blur and the existing 0.33 sampling resolution remain unchanged.

[Implementation and verification](verification/LIQUID-GLASS-0.2.0-beta1.md). Rebuild/install **code 42** for this fix.

## Initial baseline · build 41

- Development branch: `preview/0.2.0-beta1`.
- Stable `main` and `stable/0.1.9` remain at build 40.
- Retains the immersive poster gallery, glass launcher editions, playback/Home fixes, 62 themes, 15 independent asset packs and 105 launcher choices.
- Beta theme/asset catalogs and bundled fallbacks use this preview branch; stable catalogs stay isolated on `main`.
- Pack ZIP bytes and existing app preferences remain unchanged. The app keeps its existing package identity and signing setup; installing a beta requires a compatible signature.

[Source downloads](downloads/README.md) · [Baseline verification and limits](verification/UX-0.2.0-beta1.md).

The source archive is an Android Studio project. An APK has not been signed or published by this change.
