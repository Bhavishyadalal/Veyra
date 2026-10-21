# Worlds collection · 0.1.7-beta1

Twenty new themes: the fifteen proposed Worlds directions and five Quiet Horizons interpretations of the owner's images. See [the illustrated collection](../themes/WORLDS.md) for all twenty packs, artwork, launcher masters and native studies.

## Delivered behavior

- Forty-one catalog themes and sixty-nine optional launcher choices. The original Glass Noir identity, previous packs and separate Glass Atelier icons are preserved.
- Twenty original scenes and twenty AI-edited Veyra icons using the official waveform reference. Two scenes were refined after visual review (42 generation/edit calls total): Pixel Stargazer uses a bare pixel-art summit; Celestial Current uses an abstract painted star-river. Reference text and error-message overlays are omitted.
- Each theme supplies its palette, licensed existing font, geometry, seek styling, brand art and thirteen functional glyphs, including Home, Search, Explore and Library. Launcher selection remains explicit and optional. No new continuous animation or dependency.
- Catalog count limit increases from 24 to 64; its 65,536-byte response bound and validation remain. The current 41-entry payload is 31,036 bytes. Website packs are grouped into four compact sections.
- Preview remains version 0.1.7-beta1 / code 25. Existing APKs with the old catalog limit require a rebuild from this source to load the expanded catalog. No APK was built or Actions dispatched.

## Actual verification

| Check | Result |
| --- | --- |
| Android production-debug Kotlin/Java/resource compilation through focused unit-test task | Passed |
| `ThemePackTest` (15) and `ThemeLauncherTest` (4) | 19 distinct tests passed; zero failures |
| Pack recheck after two artwork refinements | All 15 `ThemePackTest` tests passed again |
| New finite catalog-limit regression | 64 valid listings accepted; 65 rejected |
| Native Home and player studies | 40 final images rendered and visually reviewed, twenty themes at 390 dp |
| Native navigation RTL / 150% text studies | Six final images rendered and visually reviewed at 320 dp; all sixteen tab targets >=48 dp, four selected states, one selection callback per study |
| Transport callback and target assertions | Passed for all twenty studies: one callback and >=48 dp target |
| Final refined scene studies | Five affected studies regenerated and reviewed; 46 final artifacts total |
| New asset decoding | 200 launcher density images, 260 glyph images and 40 scene/brand images decoded; dimensions checked |
| Website typecheck and production build | Passed |
| Website focused desktop/mobile external-link checks | Two passed, including all twenty new pack URLs |
| Preservation | Previous 21 catalog entries and published packs, existing launcher masters, default icon, Glass Atelier, backend and version configuration unchanged |

Commands used (JDK 17, local Android SDK, cached dependencies):

```sh
./gradlew --offline --no-daemon --max-workers=2 :app:testProdDebugUnitTest \
  --tests app.veyra.music.ThemePackTest --tests app.veyra.music.ThemeLauncherTest
./gradlew -p verification/theme-preview --offline --no-daemon --max-workers=2 runWorlds
```

The initial native batch succeeded. Its deprecated Gradle invocation was replaced with standard isolated `JavaExec` tasks; `renderWorld-pixel-stargazer` and `renderWorld-celestial-current` then passed using those tasks. The final pack-only recheck used in-process Kotlin compilation. Android analytics settings were read-only; this emitted a nonfatal warning. Initial Kotlin daemon fallback succeeded; compilation and tests completed successfully.

## Packaging and limits

Source is staged before `scripts/package_source.py`. The package retains all theme assets, fonts, packs, backend provenance and renderer sources; generated study images are available in Git/gallery and omitted from the source ZIP. ZIP compression is lossless. `scripts/verify_archives.py` verifies the retained stable source and three preview source archives, including pack checksums.

These are native Compose component studies with illustrative surrounding screens, **not Android device screenshots**. They do not establish real-device playback, network switching, TalkBack, launcher switching/masking, every font scale, GPU performance or battery behavior. Stable Android source and published theme packs are not promoted or modified. Website Git delivery is not proof of Vercel deployment.
