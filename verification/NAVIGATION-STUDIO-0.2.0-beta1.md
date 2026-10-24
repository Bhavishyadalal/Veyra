# Navigation studio · 0.2.0-beta1 build 43

## Choices and behavior

Settings → Appearance → Navigation now offers visual design cards, scroll-animation controls, destination ordering and the existing label/startup settings. Scroll transition choices are also beside the original Scroll player animations switch.

| Navigation | Presentation |
| --- | --- |
| Floating | Existing split floating bar and detached final destination |
| Docked | Unified bar with no side gutters and squared lower corners |
| Minimal | Compact corner geometry |
| Capsule | One fully rounded group containing all destinations |
| Ribbon | An underline below the selected glyph instead of a travelling fill |
| Orbit | A circular selected-glyph accent instead of a travelling fill |

The folding implementation previously ignored `navigationDesign`; both bar implementations now use the selected geometry. Explicit designs preserve their shapes when a theme/asset material supplies its own decorative frame. Floating retains the original theme frame behavior. Theme glyphs, text/selection colors, custom destinations and Liquid Glass remain supported.

| Scroll transition | Presentation |
| --- | --- |
| Flow | Connected, critically damped shared bounds |
| Glide | Direction-aware horizontal entry/exit with timed bounds |
| Lift | Vertical entry/exit with timed bounds |
| Soft Fade | Crossfade without shared-element travel |
| Elastic | Restrained scale entry and spring bounds |

All transitions are finite. No new image decoder, permanent ticker or backdrop source is added. Ribbon/Orbit suppress the unused travelling selection lens. A remembered, distinct preference projection resets the folding state when presentation changes, without collecting per-frame playback/scroll position at the app root. Navigation changes do not modify playback or queue ownership.

The master scroll-animation switch remains independent of Liquid Glass. Existing reduced-motion policy selects the static mini-player/navigation layout; blur-reduction and Android glass support gates remain intact. Existing selection, drag and tab actions remain in their original navigation components.

## Persistence and compatibility

`NavigationDesign` retains its three original names/order and appends Capsule, Ribbon and Orbit. `ScrollPlayerMotion` is a new defaulted `ExperienceOptions` field. Old settings default to Flow, and unknown future enum values retain existing safe JSON defaults. Reset navigation restores Floating/Flow without changing the user's global animation preference.

All 62 themes, 15 asset-pack ZIPs and 105 launcher choices remain unchanged. Stable app branches remain at 0.1.9 build 40. Source packages retain the current stable plus three newest preview archives.

## Focused verification

- Production Android release resources/Kotlin and debug metadata generation.
- Thirty design/motion settings combinations round-trip; legacy/future settings defaults are checked.
- Native settings preview renders cover six selectable designs with original colors, themes, asset overlays and 1.8× text/RTL. Selection semantics and every design callback are checked.
- The eight existing glass overlay compositing cases are retained.
- Website build/typecheck and phone/desktop release-link checks; source archive membership/checksums and preservation checks.

[Original](../themes/previews/navigation-studio/styles-0.webp) · [Theme](../themes/previews/navigation-studio/styles-1.webp) · [Theme + assets, large text/RTL](../themes/previews/navigation-studio/styles-2.webp)

These are static native preview captures, not an Android GPU or real-device animation benchmark. Smoothness/FPS and physical gesture behavior still require device testing. Build/install **0.2.0-beta1 / code 43** to receive the changes; this source publication does not include a signed APK.
