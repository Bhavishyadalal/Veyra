# Midnight Express v1 — preview 0.1.7-beta1 / code 25

Made by Bhavishya Dalal. Prepared 21 October 2026.

## Design and integration

Original rainy night-train artwork, amber carriage light, dark coal-blue surfaces,
pale-blue detail and static weight-700 Rajdhani headings evoke a railway timetable.
Manrope retains familiar body text. Thirteen weighted glyphs cover navigation,
transport, queue, lyrics, Settings, Back and download. Ticket-like corner geometry,
the existing parallel-strand/diamond progress style and an original Veyra
ticket-waveform mark extend the theme into the player and optional launcher.

The pack uses existing schema 2, mist scene and web-style seek rendering with a
minimum app code of 25. No engine schema, timer, permanent animation loop or app
dependency was added. Real music covers and all playback, queue, account and
completed reliability behavior remain unchanged. The matching launcher remains
an explicit opt-in and also appears in the grouped Themes picker. The new alias
ships disabled; LauncherDefault remains the only enabled default.

Version remains 0.1.7-beta1 / code 25. Earlier code-25 builds can use compatible
theme data but need the refreshed source for the new bundled launcher alias.
No APK is built or published by this update.

## Assets and provenance

- Pack: `themes/packs/midnight-express-v1.zip`, **692,115 bytes**.
- SHA-256: `b97f7c3ee58de56a35e9294b04abf4f5a3cd93911e84221cd47fbf27d3392377`.
- Original AI-assisted imaginary railway illustrations and original vector
  glyphs/mark, made for Veyra by Bhavishya Dalal. No film scene, character or real
  railway logo is included. Artwork notice is included in the pack.
- Rajdhani Bold and Manrope retain their upstream SIL Open Font License notices.
  The upstream static bold font is used directly; source notes are included.
- Rebuild with `python3 themes/build_midnight.py` using existing theme-builder
  dependencies and checked-in assets. No network/generation service is required.
  The complete theme builder includes the new pack. Published pack versions are
  immutable; all six previously published theme ZIPs remain byte-for-byte unchanged.

## Minimum meaningful verification completed

- Android resources/manifest and Kotlin/Java compilation passed through
  `:app:testProdDebugUnitTest`, selecting only ThemePackTest and ThemeLauncherTest.
- **16 focused tests passed**: 13 pack tests and 3 launcher tests, zero failures/errors.
  The actual new ZIP installs through ThemePack.install with outer/inner hashes,
  palette contrast, geometry, safe bounded assets and minimum-version checks.
  Font/seek/scene roles are asserted. Launcher coverage checks the new mapping,
  one enabled default and all **21** grouped icon choices.
- Six new native studies: Home, Home at 320 dp / 150% text, player, Studio,
  Studio at 320 dp / 150% text, Settings. Text/controls inspected visually.
  The actual shared themed transport callback was invoked once through semantics;
  its interaction target measured at least 48 dp.
- Gallery asset references checked. Website type check/build and its focused
  external-download-link test passed on desktop/mobile Chromium (2 browser tests),
  including the new theme, source and verification links.
- Source staged before packaging. `scripts/verify_archives.py` checks all four
  retained source archives, manifests/hashes, backend snapshots and inner theme
  ZIPs. The refreshed preview archive/checksum/catalog is mirrored across the
  eight maintained app branch catalogs; other branches' Android source is unchanged.

## Requested stable release-build source

The existing public `Veyra_Android_Source_0.1.6_Stable.zip` was downloaded and
verified, not regenerated from preview. It is **42,796,227 bytes**, version
**0.1.6 / code 22**, with an empty beta suffix. SHA-256:

`4f062857d87810cf207e3cd27b66b4eba8a9876ff57eac5fe87cd733ad24c7f3`

[Stable Android Studio source ZIP](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.6_Stable.zip)
· [Checksum](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.6_Stable.zip.sha256).
It remains unchanged. A source ZIP is not an APK: build/sign with the intended
release key, then upload the resulting APK to the appropriate release.

## Limits

[Native studies](../themes/previews/midnight-express/) import actual shared UI
components into illustrative desktop Compose layouts. They are not Android
window/emulator/device screenshots. Study cover images are demonstration art;
production song covers stay unchanged. Actual launcher switching/caching,
whole-window font scaling/RTL and TalkBack still need Android device verification.
No APK build, Actions dispatch, release creation or observed Vercel deployment
is claimed. Push commits use `[skip ci]`.
