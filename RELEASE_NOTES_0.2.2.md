# Veyra v0.2.2
### A calmer start. A lighter touch.

**Stable release · Upgrading from v0.2.1 · Android build 59**

Veyra 0.2.2 brings the tested 0.2.2 beta improvements to stable: Search that waits for your touch, clearer startup preparation, and less work behind everyday animations. Your music and your chosen effects stay at the heart of the experience.

---

## 🔎 Search when you are ready

Opening Search no longer brings up the keyboard automatically. Browse your results first, then **tap the search field to type**.

- Enter Search from the bottom navigation, a Home shortcut or your startup-page preference without an automatic keyboard popup.
- Keep your current query and results when returning through the navigation.
- Selecting a navigation tab clears text focus and dismisses the keyboard.
- Older backups cannot restore the retired automatic-keyboard behavior.

## ✨ A clearer first launch

The first launch of a new build can show **Preparing Veyra**, with real stages for appearance resources, fonts and image loading.

- See completed stages and elapsed time instead of an artificial countdown.
- Choose **Continue** immediately; preparation can finish in the background.
- Rotate or leave the screen without cancelling the preparation work.
- Successful completion is remembered for that build.
- Slower preparation gets a clearer explanation. Failures offer **Retry** and **Continue**—including when saved preparation state cannot be read.

This prepares appearance resources. Completion time varies by device; it does not require a fixed waiting period.

## 🌊 Less work behind the motion

The effects remain, with targeted reductions in the work needed to display them.

| Area | What changed |
| --- | --- |
| Navigation selection | Pill travel and squash update in the graphics layer, avoiding whole-bar recomposition on each travel frame. |
| Folding navigation | The accessory fade updates its graphics layer instead of recomposing the accessory each frame. |
| Navigation Studio | Design previews load as you scroll. Column count accounts for available width, sheet padding and large text. |
| Appearance restoration | The selected theme and overlay restore before other installed packs. |
| Pack fonts | Inactive fonts no longer evict active fonts from the warm cache; they are parsed before you apply them. |
| File verification | Bounded streaming replaces whole-file allocations while preserving size and SHA-256 checks. |

## 💜 Everything you already enjoy

The stable 0.2.1 improvements carry forward: shared **Music → Veyra** downloads, bulk export for older private downloads, the lighter immersive picker, waveform notifications, app updates and completed language resources.

Your collection remains **12 immersive modes, 72 themes, 32 independent overlays and 132 launcher choices**. Existing published theme and overlay packs remain unchanged.

---

### Release checks

The stable source passed **1,305 Android unit tests** with no failures and one existing skip, plus **93 shared UI tests** with no failures. Checks include startup recovery, navigation rendering and existing playback/download regressions.

### Updating

Build 59 is newer than stable 0.2.1 build 56 and beta 0.2.2-beta1 build 58. An in-place update requires the same app package and signing identity. The built-in updater follows published stable APK releases; a source push alone does not publish an APK release.

[Stable source project](downloads/Veyra_Android_Source_0.2.2_Stable.zip) · [SHA-256](downloads/Veyra_Android_Source_0.2.2_Stable.zip.sha256) · [Release verification](verification/STABLE-0.2.2-PROMOTION.md)

Rendering improvements reduce specific sources of overhead. Sustained 60 or 90 fps still depends on the phone, effects, workload and system conditions; real-device frame rates have not been measured in these checks.

**Thank you for listening with Veyra—and for helping make each release better.**
