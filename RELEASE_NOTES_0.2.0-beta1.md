# Veyra 0.2.0-beta1

**Preview development · Android version code 42**

This development cycle starts from the user-tested **stable 0.1.9 build 40**.

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
