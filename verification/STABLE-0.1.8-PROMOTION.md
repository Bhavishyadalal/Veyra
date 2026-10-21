# Stable 0.1.8 · source promotion

**21 October 2026 · version 0.1.8 / code 28 · `main` and `stable/0.1.8`**

Promoted at the owner's explicit request from completed preview commit `646e34b56295e1fbfaddcb3cbdc71fa5038eb196`. Previous `main` commit `5d989869addce3183b3b78f80813cd6dc6086210` is already an ancestor, so normal fast-forward publication preserves both histories without conflict resolution or history rewriting.

## Release changes

- Version code increases from 27 to **28**; the `beta1` suffix is cleared for debug and release variants. Version name remains **0.1.8**.
- All shipped beta app code, assets, themes and launcher choices are preserved. ThemeRepository and bundled/public catalog URLs already target `refs/heads/main/themes/`; no routing rewrite or theme repack is necessary.
- Stable release notes, Android Studio entry points, source index, theme documentation/gallery, Colab builder default and website stable links now point to 0.1.8.
- The current stable source ZIP replaces the old 0.1.7 stable ZIP in the four-archive set. The three preview ZIPs retain their exact bytes. Historical stable 0.1.7 source and archive remain available on its stable branch and immutable historical URL.
- The completed preview branch retains version 0.1.8-beta1 / code 27. Older stable/preview branches, tags and release assets are preserved.

## Focused verification

Production-release Kotlin compilation, production-debug BuildConfig generation, and the existing ThemePackTest / ThemeLauncherTest suites check stable metadata and compatibility with the published theme catalog. Generated debug and release BuildConfig must both report version name `0.1.8`, version code `28`, without a beta suffix.

Archive verification checks all retained source manifests, asset hashes, SHA-256 sidecars and backend provenance. Website verification covers production build, type checking and the two existing desktop/mobile download-link checks. Actual results: production-release Kotlin compilation passed; debug/release BuildConfig both report `0.1.8` / `28`; ThemePackTest passed **17** cases and ThemeLauncherTest passed **5** cases. Website build/type checks and **2** link checks passed. Public archive checksum is recorded in the delivery record.

No APK assembly, signing, Actions dispatch, tag/release publication or backend deployment is part of this source promotion. Prior verification documents remain historical evidence; promotion does not establish a fresh full app audit, clean lint, device FPS/battery behavior, Android launcher validation or Vercel deployment. Remaining future roadmap entries are preserved.

[Release notes](../RELEASE_NOTES_0.1.8.md) · [Downloads](../downloads/README.md) · [Latest beta refinement](SETTINGS-MOTION-POLISH-0.1.8-beta1.md)
