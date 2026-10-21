# Deep theme surfaces and connected controls

**Beta 0.1.8-beta1 / code 27 · 21 October 2026 · preview only**

## Corrections from the screenshots

The player output/Listen Together and shuffle/repeat/autoplay capsules had two competing shapes: the parent clipped the group into a capsule, while every child drew a theme-rounded background and border. Full-height dividers exposed the mismatch. The shared group now owns the single outline and theme geometry; grouped children have rectangular fills clipped by that parent, with no individual frame or scaling gap. Dividers stop 12 dp short of the top and bottom. Selection, busy/disabled feedback, haptics, throttling, action callbacks, spoken labels and original 64×48 dp segments remain.

Artist/album/playlist routes and nested artist shelves already use the corrected retained scene surface from the [previous background fix](BACKGROUNDS-0.1.8-beta1.md). Artist photographs now additionally fade to transparency at the foot, revealing that scene instead of painting a solid palette wash over it. The original wash remains in default appearance. The mask is cached, preserves the portrait's upper portion and follows the existing scroll offset. Artist photos, album covers, Canvas behavior and music are retained.

Shared player drawers now display local theme artwork behind their existing scrollable content. Their maximum size, drag/nested-scroll handling, title pane, handle, dismissal and navigation-bar padding remain. Themed drawers skip haze hidden by their opaque artwork; default drawers retain their previous material. Song/browse/download/playlist sheets using `ArtworkBackdrop` use the same contrast-protected prominent scene. Theme Studio, launcher-icon and font sheets now have wrap-content theme backdrops, preserving list ownership, height caps and Apply/cancel behavior.

No new themes, launcher aliases, characters, background animation loops, dependencies or version changes. Purpose-specific imagery—real covers, portraits, QR codes, share exports and external sign-in provider content—keeps its original purpose. This change does not replace every card or small popup with a wallpaper.

## Focused verification

- Android `:app:compileProdReleaseKotlin`: **passed after final app changes**. Existing unrelated compiler warnings remain; no claim of clean lint or APK assembly.
- Native `probeDeepTheme`: **passed**. Six cases exercise the exact shared group and control primitives used by the player: two/three buttons, rounded and cut-corner packs, default appearance, RTL at 150% font scale, selected and disabled states. Checks cover >=48 dp targets, action/selection/disabled semantics, callbacks and pixel samples detecting protruding full-height seams. Production segment dimensions are used.
- The same native probe exercises the actual shared artist mask and wrap-content backdrop using a synthetic white photograph. The upper photograph remains unchanged, its foot reveals the local scene, artwork continues beneath it, and a no-pack photograph remains unchanged.
- [Native component captures and result record](../themes/previews/deep-polish-0.1.8/RESULTS.txt). Connected-control and fade captures were inspected. These are component studies, not complete Android screen captures; the white photograph is a deliberate diagnostic source.
- Website build/type checks: **passed**; focused desktop/mobile external-link checks: **2 passed**.

The earlier navigation-wrapper/cache/font checks were not rerun: those behaviors were unchanged. The source ZIP is regenerated after staging source, and all retained archives are verified with `scripts/verify_archives.py`. Public hash and resulting commits are recorded in delivery metadata outside the ZIP.

## Limits and installation

No real-device screenshots, hardware navigation/gesture checks, playback, TalkBack or measured GPU/fps tests. The checks establish shared component rendering/semantics and compilation, not exhaustive device correctness. The artist mask uses one bounded offscreen layer; performance on devices remains unmeasured.

Rebuild/install the beta APK to receive app-code fixes. No APK was built and no Actions were dispatched. Stable 0.1.7, its archive and published packs are unchanged. A website push is not verification of Vercel deployment.
