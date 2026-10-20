# Astral Tide v1 — preview 0.1.7-beta1 / code 25

Made by Bhavishya Dalal. Prepared 21 October 2026.

## Design and implementation

Original bioluminescent ocean/celestial artwork: translucent manta rays, a moonlit
surface, suspended starlight and distant submerged arches. Petroleum teal surfaces,
sea-glass mint controls, champagne secondary accents and static weight-700 Space
Grotesk headings distinguish it from the three existing themes. Manrope remains
the body font. Thirteen original, consistently weighted glyphs cover navigation,
transport, queue, lyrics, settings, back and download. An original Veyra
manta-waveform mark is used for in-app branding and the optional bundled launcher.

The data-only pack uses the existing schema-2 engine, mist scene and ember-style
luminous track. Minimum app code is 25; version stays 0.1.7-beta1 / code 25. No
new shaders, timers, dependencies or permanent motion loops were added to the app.
Real song covers, actions, playback/state behavior and all completed reliability
work remain unchanged. Theme application keeps its explicit matching-icon opt-in;
manual grouped icon selection also remains available. Exactly one launcher alias
ships enabled: LauncherDefault. The new alias ships disabled.

Older code-25 app builds can install compatible theme data but do not contain the
new bundled launcher alias. Rebuild the refreshed preview source for that optional
icon; no installer is published here.

## Assets, licenses and reproducibility

- Theme ZIP: `themes/packs/astral-tide-v1.zip`, 521,115 bytes.
- SHA-256: `399985796c9caec40ec4e2f2f1d12136cc30a3424dbac798f2c2f22f1d436d22`.
- Generated illustrations are original AI-assisted Veyra artwork, without an
  official film/comic character or franchise logo. Original vector sources are in
  `themes/source-icons/astral-tide/`. Artwork notice is included in the pack.
- Space Grotesk static weight-700 instance and Manrope keep their original SIL
  Open Font License notices. Source and instance notes are included in the pack.
- `python3 themes/build_astral.py` rebuilds from checked-in assets with the existing
  theme-builder dependencies. No AI service or network is required. The complete
  builder calls it after the existing packs. Published pack versions remain immutable.
- Existing published theme ZIPs are byte-for-byte unchanged.

## Focused checks completed

- Android resources, manifest and Kotlin/Java compilation passed through
  `:app:testProdDebugUnitTest` with only ThemePackTest and ThemeLauncherTest selected.
- **16 focused JUnit tests passed** (13 pack, 3 launcher), zero failures/errors.
  The actual new ZIP installs through ThemePack.install, including outer/inner
  hashes, palette contrast, geometry, bounded assets and minimum-version checks.
  Existing packs and legacy compatibility remain covered. The new theme's font,
  seek and scene roles are checked. Alias mapping, one enabled default and all
  **20** grouped icon choices are covered.
- Six new native component studies: Home, Home at 320 dp / 150% text, player,
  compact Studio, Studio at 320 dp / 150% text, Settings. Text and controls were
  visually inspected. The actual shared themed transport callback was invoked
  once through semantics; its target measured at least 48 dp.
- The renderer's new-theme Material container colors were corrected to match the
  Android palette before final visual inspection.
- Local gallery image references resolve. Website build/type checks and the
  focused external-download-link test passed on mobile and desktop Chromium
  (2 browser tests), including the Astral Tide download.
- Source staged before packaging; all four retained archives checked using
  `scripts/verify_archives.py`, including manifests/hashes and backend snapshots.
  Identical refreshed preview archive/checksum and catalog are mirrored across
  maintained branches without promoting preview Android source.

Repeat relevant checks:

```sh
./gradlew :app:testProdDebugUnitTest --tests app.veyra.music.ThemePackTest --tests app.veyra.music.ThemeLauncherTest
./gradlew -p verification/theme-preview run -Ptheme=astral-tide
python3 scripts/package_source.py Veyra_Android_Source_0.1.7-beta1.zip
python3 scripts/verify_archives.py
```

## Limits

The [native studies](../themes/previews/astral-tide/) import actual shared UI
components into illustrative desktop Compose layouts. They are not full Android
window/emulator/device screenshots. Study music covers use demonstration artwork;
production music covers are unchanged. Full TalkBack traversal, whole-window
large-text/RTL behavior and launcher alias/cache behavior need real Android
verification. No new performance/battery benchmark, APK build, Actions dispatch
or observed Vercel deployment is claimed. All push commits use `[skip ci]`.
