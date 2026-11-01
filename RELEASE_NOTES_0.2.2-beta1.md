# Veyra 0.2.2-beta1 · build 58

**Testing preview · Based on stable 0.2.1**

## Search on your terms

Opening Search from the bottom navigation, a Home shortcut or your startup-page preference no longer opens the keyboard. Tap the search field when you want to type. Returning through the navigation preserves your query and results. The old automatic-keyboard option is retired, including for imported preferences.

## Prepare, then listen

On the first activity launch of a new build, an optional preparation screen shows actual stages for installed themes, overlays, bundled fonts and the image loader. It shows elapsed time, offers Continue immediately, and lets preparation finish in the background. A slow device gets a longer-running explanation; a failed preparation offers Retry or Continue. Successful completion is remembered for that build. There is no artificial countdown or required minimum wait.

This prepares appearance resources; it does not replace Android's runtime optimization or guarantee a particular frame rate.

## Less work behind the effects

- The tonal navigation pill updates travel and squash in its graphics layer, keeping its existing effect without recomposing the whole bar on each travel frame.
- The folding navigation accessory fades in its graphics layer, reducing composition work during transitions.
- Navigation Studio loads visible design previews as you scroll, with two columns where space and text size allow and one column for narrower/large-text layouts.
- Startup restores the selected theme and overlay before the other installed packs. Inactive pack fonts no longer fill and evict the active font cache; they are parsed before Apply instead.
- Installed-file integrity checks stream bounded chunks instead of allocating whole artwork/font files. Size and SHA-256 checks remain enforced.

All stable 0.2.1 fixes remain, including the lazy immersive picker, shared Music/Veyra downloads and app updates. Catalogs and bundled fallbacks remain on `preview/0.2.2-beta1`. Stable/main stays at 0.2.1 build 56; completed older beta branches are unchanged.

[Verification and phone checklist](verification/BETA-0.2.2-beta1.md). Source ZIPs are Android Studio projects. Signed APK availability is separate from a source push.
