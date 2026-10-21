# Native Halloween component renderer

This standalone desktop Compose harness imports the actual shared theme asset
components, Settings controls and player control/progress primitives. Screen
compositions are illustrative studies, not the full Android Activity or an
emulator capture. The app's complete Settings/Coil/network/Activity integration
still requires device testing. The player sample uses demonstration cover art;
the real player preserves the actual track artwork.

With JDK 17 and the repository Gradle wrapper:

```sh
./gradlew -p verification/theme-preview run
```

In a managed cloud environment use the configured HTTPS proxy and CA trust for
Gradle, exactly as for the root project. No APK is built. It renders Home, player,
Settings, and 320 dp / 150% font studies into `themes/previews/`, checks the native
transport hit region/semantics and invokes its actual callback once. Desktop
initial-focus scrolling is reset before the initial large-text snapshot.

Check `themes/previews/RENDER-RESULTS.txt`. A successful desktop render is not a
claim of Android GPU frame pacing, real system Back behavior, or playback during
Activity recreation. Re-run after changing shared presentation components.
