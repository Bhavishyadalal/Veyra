# 0.1.6 native Home component review

From the Android project root with JDK 17 and the usual Gradle dependencies cached:

```sh
./gradlew :sharedUi:generateComposeResClass :sharedUi:generateResourceAccessorsForCommonMain
./gradlew -p verification/home016-preview run --max-workers=2 --console=plain
```

This standalone renderer is not added to Android settings. It compiles real HomeExperience.kt, HomeLayout.kt, Models.kt and generated shared string resources. The settings flow/page gutter are host adapters; offline and saved-content messages and editor row bodies are production code. Bundled Manrope is used. The editor is rendered inline for inspection, not inside a platform bottom-sheet window.

Seven semantics interactions check reorder, visibility, reset, Customize Home, both offline shortcuts and the disabled first move-up control. Six component-only boards cover offline/saved headers and editor normal, large text (320dp/1.6x), RTL and light modes. They do not render the complete HomeScreen or test ViewModel, Android lifecycle, navigation, connectivity or playback.

[Boards](../../design/home016-preview/README.md) · [Current results/limitations](../HOME-0.1.6-beta1-RESULTS.md).
