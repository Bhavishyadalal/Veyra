# Dreamscape collection — verification

**0.1.7-beta1 / code 25.** Chrome Vesper, Window Wish, Cassette Corner, Azure Bloom and Strawberry Daydream. [Artwork, downloads and native studies](../themes/DREAMSCAPE.md).

## Delivered

Five new immutable v1 theme packs with ten original AI scenes/icon edits. Each includes banner/player/preview mapping, matching Veyra Home mark, readable palette, licensed bold fonts, thirteen functional glyphs, corners and timeline styling. Navigation and play/pause glyphs vary with the theme; Cassette Corner also has a cassette queue emblem. Matching icons preserve the official Glass Noir waveform with more detailed thematic framing.

Five bundled launcher aliases add optional matching choices. The catalog now has 21 themes and the picker 49 icons; all prior choices, twelve standalone Atelier icons and previous pack versions remain intact. Original is still the only initially enabled launcher. Existing navigation material, reduced-motion behavior, native accounts/playback/queue and real album artwork remain unchanged.

The offline compiler now finds original font license notices in either the upstream fonts directory or the newer licenses directory, allowing the existing Gothic font to retain its exact notice. Generated Python bytecode is kept local and removed from tracking; source scripts are retained.

## Actual checks

| Check | Result |
| --- | --- |
| Android resource/manifest processing and production-debug Kotlin compilation | Passed through the focused unit-test task; no APK assembled |
| `ThemePackTest` | 14 passed, including real catalog pack installation, inner/outer checksums, runtime URL policy, palette contrast and legacy/security cases |
| `ThemeLauncherTest` | 4 passed: fixed aliases, original default state, complete nonduplicated grouping, new theme mappings and Atelier independence |
| New asset decoding | 50 density WebP resources at expected sizes, 65 nonempty 128 px functional PNG glyphs and 10 scene/brand images decoded |
| Native Compose studies | 15 rendered and visually inspected: Home, player and RTL navigation at 320 dp / 150% text for each theme |
| Navigation assertions | Each sheet retains sixteen tab targets ≥48 dp, four selected states and exactly one activation callback |
| Player assertions | Each themed pause target ≥48 dp and exactly one native callback |
| Website | Type check, production build and 2 focused desktop/mobile download-link checks passed |
| Preservation | All sixteen previous catalog entries, existing published packs, default/previous launcher masters, Atelier assets, backend and app version configuration unchanged |

Commands:

```sh
./gradlew :app:testProdDebugUnitTest --tests app.veyra.music.ThemePackTest --tests app.veyra.music.ThemeLauncherTest
./gradlew -p verification/theme-preview run -Ptheme=<dreamscape-id> -Pstudy=home,player,navigation-rtl-large-text
npm run typecheck
npm run build
npx playwright test tests/showcase.spec.js -g 'FAQ and external download links'
```

Gradle used the existing offline cache, JDK 17 and configured Android SDK. The existing experimental lint-version warning remains. No unrelated test suites or earlier-theme renders were repeated.

## Limits

These are shared native Compose component studies with illustrative surrounding layouts, **not Android screenshots or real-device tests**. They do not instrument the glass folding host, platform launcher masks, launcher cache/switching or TalkBack. Extreme device/font/localization combinations and previous playback/network handover checks remain unverified on hardware. No benchmark, APK, Actions dispatch, stable promotion or live Vercel deployment is claimed.

The new aliases require rebuilding the latest preview source. Theme ZIPs are data packs, not APKs; they cannot add aliases by themselves. Stable 0.1.6 cannot load these min-code-25 packs. Generated studies stay in Git/gallery; source ZIPs include renderers and all theme assets/packs while omitting duplicate images under `themes/previews/`.
