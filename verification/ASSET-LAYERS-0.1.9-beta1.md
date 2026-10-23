# Layer Lab · beta 0.1.9 build 33

Five genuine asset overlays ship separately from the 62 unchanged full themes.
Independent catalog/storage/preferences, reset, category switches and custom-style
layering are implemented. The original ThemeRepository and full-theme installer
are not replaced. Settings → Appearance → Asset packs is the entry point.

## Focused verification

- `:app:compileProdReleaseKotlin` passed with existing warnings; no APK assembly.
- `CheckAssetPacks.java` against production Kotlin classes installed all five real
  ZIPs with outer checksum/byte and inner exact-entry/hash validation, decoded
  all PNG/font roles, checked offline-preview inclusion and rejected old app codes,
  invalid roles and unsupported motion presets. 119 roles per pack; pressed
  transport states have different hashes from their normal skins.
- `CheckAssetLayers.java` ran the production merge for every 62×5 theme/pack pair,
  including representative custom-scene markers. Identity, all palette values,
  header/player/preview/brand artwork are preserved; disabling categories restores
  the base and font-only overrides do not leak pack icons. The actual root merges
  after `effectiveStyleVisuals`, so custom app styles retain their own backdrop.
- All 62 existing theme ZIPs and both theme catalogs remain byte-identical to the
  preceding commit. Stable branches are not promoted or edited.
- Website build/type checks passed. Playwright at 390/1366 px confirmed all seven
  Layer Lab links, build-33/preview URLs and no horizontal overflow. Offline review
  gallery loaded five actual specimens and five complete asset sheets.
- Source archive packaging retains required Gradle font inputs, omits generated
  visual studies and deduplicates optional asset/font authoring copies into their
  included immutable packs. Restorer keeps namespace/path/hash checks and user edits.

## Limits

No physical device, APK installation, playback/gyro simulation or 60-FPS benchmark
was performed. AI material board is explicitly separate from real vector/PNG/font
specimens. Native supported shapes/seeks/finite transitions are implemented; no
physical paper peel, liquid simulation or arbitrary downloaded animation code.
App-owned platform inputs, service logos and unmatched glyphs retain native fallbacks.
No launcher aliases, mascot or stable promotion were added. Git push does not prove
that Vercel deployment or APK publication succeeded.
