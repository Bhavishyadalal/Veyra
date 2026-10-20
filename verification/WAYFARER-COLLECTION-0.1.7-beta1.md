# Wayfarer collection and navigation — verification

**0.1.7-beta1 / code 25.** Five new packs: Golden Afterglow, Lilypond Parade, Dustbyte Outpost, Alpine Ember and Starfall Pals. [Downloads, artwork and native studies](../themes/WAYFARER.md).

## Implemented

- Ten AI scene/icon generations/edits informed by the owner's five references. Original animals, photographic meadow, two pixel-art cabin landscapes and celestial storybook art. Official Glass Noir waveform preserved in all five marks; miniature scene/charm details frame it.
- Five schema-2/3 immutable v1 packs using the existing data-only engine. Each includes palette, banner/player/preview mapping, matching Home brand, licensed fonts, thirteen functional glyphs, geometry and timeline styling. The four navigation glyphs differ by theme.
- Five fixed launcher aliases with five density sets and the official monochrome waveform; explicit optional theme-to-launcher selection. Previous 39 choices preserved, now 44. Glass Atelier remains separate.
- Both navigation styles use the shared static tonal material and existing pack palette/geometry. The themed surface bypasses dynamic blur; tiny ornaments use cached drawing without tickers or input interception. Floating themed items share an accessible tab component with two-line labels. Glass tab cache keys include the theme. Affected color/press transitions honor app/system reduced motion.
- Website links grouped under a compact five-theme disclosure. Gallery now derives all sixteen themes from the catalog.
- Source packaging retains source, fonts, theme artwork/packs and renderers. Generated images in `themes/previews/` remain in Git/gallery and are excluded from source ZIP duplication. Nothing is deleted from the repository or release history.

## Actual results

| Check | Result |
| --- | --- |
| Android resource/manifest processing and Kotlin compilation | Passed through focused production-debug unit test task; no APK assembled |
| `ThemePackTest` | 14 passed; catalog/runtime URL policy, real pack installation, inner/outer integrity, contrast validation and legacy/security cases |
| `ThemeLauncherTest` | 4 passed; 44 choices, fixed aliases, original default only initially enabled, grouping, existing Atelier independence and theme bindings |
| `BottomNavigationGeometryTest` | 5 passed; reading order, RTL indicator/drag, bounds and invalid measurements |
| Asset decoding | 50 new density resources at expected sizes; 65 PNG functional glyphs; 10 scene/brand images decoded |
| Native Compose studies | 20 rendered and visually inspected: Home, player, large-text navigation and RTL large-text navigation for each new theme |
| Native navigation assertions | Each navigation sheet has 16 tab targets ≥48 dp (four selection states), four selected tabs and exactly one activation callback; 320 dp / 150% text |
| Native player assertions | Each themed pause target ≥48 dp; exactly one native callback |
| Website | Type check, production build and 2 focused mobile/desktop external-link tests passed |

Commands used:

```sh
./gradlew :app:testProdDebugUnitTest --tests app.veyra.music.ThemePackTest --tests app.veyra.music.ThemeLauncherTest --tests app.veyra.music.BottomNavigationGeometryTest
./gradlew -p verification/theme-preview run -Ptheme=<new-theme-id> -Pstudy=home,player,navigation-large-text,navigation-rtl-large-text
npm run typecheck
npm run build
npx playwright test tests/showcase.spec.js -g 'FAQ and external download links'
```

Builds used the existing offline Gradle cache, JDK 17 and Android SDK. The initial native harness compile missed its new shared source include; that include was corrected before all twenty studies passed. Existing unrelated compiler deprecation warnings remain.

## Limits

Studies use actual shared native components with illustrative surroundings. They are **not full Android screenshots**, glass-folding host instrumentation or launcher-mask emulation. Real-device navigation folding, TalkBack, font/localization extremes, launcher switching/cache/cropping and prior playback/network device checks remain unverified. No device performance benchmark or live Vercel deployment is claimed. No APK build or Actions dispatch. Stable Android source, previous published theme-pack bytes, default icon, existing Atelier assets, backend and app version configuration remain unchanged.

Latest preview source rebuild is required for the new launcher aliases and navigation polish. Theme packs alone cannot install launcher aliases. Stable 0.1.6 cannot load these min-code-25 packs.
