# Scroll-player animations · stable 0.1.8

**22 October 2026 · stable source maintenance · version 0.1.8 / code 28**

- Settings → Appearance → **Scroll player animations** is saved independently of Liquid Glass and enabled by default.
- Enabled: the shared navigation/mini-player layout folds on downward page scroll and expands on upward scroll, with either glass or solid themed material. The preference selects the integrated layout; stacked-only mini-player/navigation design choices apply when it is off.
- Disabled: the existing stacked mini-player and navigation return, whether Liquid Glass is on or off. App/system reduced motion and subtle-motion preferences also choose the stacked layout.
- The scroll connection belongs to the page container, covering retained main destinations, artist/release details and Settings subpage overlays without consuming their scroll. Detached modal windows retain their own scroll behavior.
- Glass-off and unsupported-device paths use tonal fills and a flat selection indicator. They skip the glass lens and its tab-row backdrop recording. Whole-page backdrop recording remains tied to actual glass rendering.
- Changing the preference resets the folding state to expanded. Existing player controls, navigation destinations and playback ownership remain intact.

Verification: production-release Kotlin compilation and focused review of preference initialization/reload/persistence, glass-on/off material branches, nested-scroll placement and reduced-motion precedence. No broad app suite, APK assembly, device FPS measurement or screenshot matrix. Website build/type and existing desktop/mobile download-link checks cover the updated source links. Source archive integrity is checked separately.

The original stable promotion remains recorded in [STABLE-0.1.8-PROMOTION.md](STABLE-0.1.8-PROMOTION.md). Preview and historical stable branches are unchanged by this maintenance update.
