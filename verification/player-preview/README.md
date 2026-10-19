# Native player component review

Run from the repository root with JDK 17:

```sh
./gradlew -p verification/player-preview run --max-workers=2 --console=plain
```

This independent development runner is not included in the Android project settings. It uses Kotlin 2.3.20, Compose Desktop 1.10.3 and Material 3 Desktop 1.7.3. Once dependencies are cached, append `--offline`. No Android APK is built.

The runner compiles the **production** PlayerControlPrimitives, PlayerInteractionPolicy and PlayerScrubState files. Original app vector paths are read from the actual transport resources; bundled Manrope fonts and current theme declarations are used. The fixture arranges these controls with sample labels and positions. It is a component study, **not a screenshot of the complete Android player**. Host behavior, the artwork deck, real queue data, Automix, lyrics scrolling, audio routing and playback are not reproduced here.

22 checks exercise pointer taps, callback counts, buffering/disabled/selected semantics, 48dp seek targets, cancellation on track/permission/deck changes, final release coordinates, invalid and clamped accessible seeks, stable hit regions during a real press animation, RTL input and scrub pending-state cleanup. Ten PNG review boards are regenerated under [design/player-preview](../../design/player-preview/README.md), including 320dp width at 1.6× font scale. Reduced-motion loading uses a static arc with indeterminate semantics.

See [validation and Android limits](../PLAYER-REFINEMENT-RESULTS.md) and the complementary [source-preservation audit](../player-audit/README.md).
