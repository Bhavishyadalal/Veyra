# Native Home component review

From the repository root with JDK 17:

```sh
./gradlew -p verification/home-preview run --max-workers=2 --console=plain
```

Append `--offline` after caching dependencies. This standalone development runner uses Kotlin 2.3.20, Compose Desktop 1.10.3 and Material 3 Desktop 1.7.3. It is not added to the Android project settings and does not generate an APK.

The runner compiles the **production HomeComponents.kt and HomeFeedIdentity.kt**, alongside the actual VeyraIcons. It uses bundled Manrope/Super Malibu fonts, the real Google vector paths and existing campaign artwork. The brand slot is a fixture that follows the existing brand sizing; Android still uses the unchanged VeyraBrand/VeyraWaveform. Sample titles and artwork are illustrative, not real recommendations.

17 checks exercise the Google host callback/no editable fields, pager boundaries, disabled arrows during scrolling, 48dp navigation targets, layout selection semantics, empty-state refresh and duplicate-request blocking, distinct card click/hold callbacks and reduced-motion duration. Nine review boards cover banners, tiles, current item, missing artwork, empty feed, light theme, RTL, reduced motion and 320dp width at 1.6× text scale.

These are **component arrangements, not complete Android Home screenshots**. The live feed, actual Coil requests/cache, long-press origin capture, Android navigation/scroll restoration, pull refresh, pager swipes and playback need integration/device verification. The fixture invokes native Compose semantics and pointer events; it does not replace controls with HTML.

[Review boards](../../design/home-preview/README.md) · [Results and limits](../HOME-REFINEMENT-RESULTS.md) · [Source preservation](../home-audit/README.md).
