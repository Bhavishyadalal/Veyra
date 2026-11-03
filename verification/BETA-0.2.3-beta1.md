# Beta 0.2.3-beta1 · code 60

Created from stable 0.2.2 build 59 (`07399a08`) at the owner's explicit request. Stable and completed preview branches are unchanged. No GitHub workflows are dispatched or rerun by the assistant; the owner will run them. Push commits use [skip ci] to avoid automatic push checks.

## Preference and lifecycle behavior

A saved master preference resolves effective animation, dynamic-blur, Liquid Glass, Canvas and lyric-blur state from the original saved preferences. The master never overwrites those individual choices. Reload/backup application re-resolves the same policy. Turning the master off restores the saved selections; appearance resets continue to respect the master. Subtle-motion state follows the effective animation policy. Refresh-rate preference is independent in setters and settings restoration; its previous forced animation/blur behavior is removed.

The post-welcome suggestion waits until onboarding has completed its exit and sign-in is absent. It offers two explicit choices. Back/outside dismissal preserves the current mode and remembers the suggestion. Existing users see it once, and new users see it after welcome. Update dialogs wait until the appearance suggestion has been handled. No activity restart or playback command is used to change the profile.

## Rendering paths

- The master CompositionLocal reaches Android routes, shared UI, sheets and popups through VeyraTheme. Existing typography, theme palette, icons and account/download/player data remain selected.
- The shared Haze wrapper returns an opaque material surface and Haze sources stop recording while reduced effects is active. Liquid Glass exits before allocating its blur/lens/highlight pipeline; its lightweight and rim variants also respect the override.
- Compose blur and decorative shadow modifiers route through master-aware helpers, retaining shadow clipping semantics. Full-effects mode delegates to the original modifiers with the original parameters.
- Theme and custom-style scene backdrops become opaque palette floors. Themed control opacity is 1; decorative artwork is hidden. Party-avatar glow clocks/layers stop. Context-menu plates become fully opaque.
- Player mesh, full-artwork blur and legacy palette preparation are skipped when unused in reduced mode. Expanded immersive scenes keep a legible opaque floor and stop decorative motion. Animated Canvas is disabled through the existing lifecycle-aware setting, which retires video requests/players. Audio playback remains owned by the unchanged playback service.
- Functional progress, synchronized lyric timing, standard controls and scrolling remain. This is not a promise that every Android system animation or platform control effect is disabled.

## Local verification

- Release Kotlin/resource compilation passes.
- Full Android release unit suite: 1,310 tests, zero failures/errors, one existing skip.
- Full shared UI JVM suite: 98 tests, zero failures/errors, including live profile toggling and shadow clipping.
- Website production build and TypeScript checks pass; 14 desktop/mobile resource, accessibility, page-load and download-route tests pass.
- Archive/member checksums, retained set, native inputs and backend provenance are verified before push.

 Focused preference tests use real AppSettings setters with in-memory SharedPreferences to cover override restoration, persisted choice reload, dismissal and refresh-rate independence. Pure policy checks cover all 32 combinations of saved effect flags. Native shared-UI render tests verify opaque Haze/theme fallback, sharp content, live blur restoration/removal and shape clipping after shadow removal.

All 17 offered UI languages and Traditional Chinese aliases have the new strings and revised refresh-rate description. Translations are machine-assisted; human wording review remains useful. No runtime translation service is added.

Source retention: stable 0.2.2 plus previews 0.2.3-beta1, 0.2.2-beta1 and 0.2.1-beta1. Prior 0.2.0-beta1 source remains in its frozen branch/history. Native inputs, source modes, backend provenance and published theme/overlay ZIP bytes remain verified. Catalogs and bundled fallbacks use this preview branch.

No physical phone or emulator is attached. APK assembly/signing/installation is left to the owner's manual workflow. The target phone is a realme Narzo 60 5G with a 90 Hz display; local/offscreen tests do not measure sustained FPS, battery or thermal behavior.

## Phone checklist

1. Finish welcome on a clean install; confirm the choice appears afterward. Choose each profile, dismiss once, rotate and restart; verify the suggestion does not repeat after being handled. Repeat after upgrading an already configured app.
2. Enable reduced effects while audio plays. Check Home, Search, Library, Settings subpages, dialogs, song context menus, navigation variants and player/lyrics/queue/immersive views. Surfaces should be opaque with no decorative blur/glass/shadow/video; covers, lyrics and controls remain usable.
3. Select 90 Hz while reduced effects stays on; restart and verify both preferences. Change themes and styles, then turn the master off and confirm the saved individual effects return. Test full → reduced → full during player opening and sheet dismissal.
4. Check large text, narrow/landscape layouts, RTL and the scrollable suggestion body. Exercise focus/Back, sign-in and update prompts so setup dialogs never stack.
5. Compare frame timing and sustained playback on the same phone at the same refresh-rate preference. Check downloads, notification controls, Bluetooth and interruption recovery. No performance guarantee is made without device measurements.
