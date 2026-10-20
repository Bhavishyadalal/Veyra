# Veyra 0.1.6-beta1 · Your Home, ready sooner

## Compact-screen and readability update

- Centered, proportionate buffering indicators in player controls.
- Adaptive Settings controls, song-menu values, timeline labels and transport spacing.
- Stronger Settings icon strokes and a more readable default body weight.
- New saved Appearance options: **Stronger text** and **Higher-contrast player controls**.

[Changes and verification limits](verification/ACCESSIBILITY-0.1.6-beta1.md).

## Interface and motion update

- Matching surfaces and touch feedback across Settings, player drawers and song menus.
- Refined page/section transitions and dependent Settings controls.
- Smoother drawer interaction and coordinated queue/lyrics panes.
- Cached tile artwork as a detail-header loading placeholder.
- Reduced-motion support follows both app and system preferences.

[Changes, checks and device follow-up](verification/UI-POLISH-0.1.6-beta1.md).


## Source build maintenance

The refreshed beta source addresses the uploaded Kotlin warning sites, migrates
clipboard and Media3 APIs, and adds audio reset regressions. Version remains
**0.1.6-beta1 / code 21**. [Build-log review and verification limits](verification/BUILD-WARNINGS-0.1.6-beta1.md).
No new APK or completed full release build is claimed.

- Restore a previously loaded Home from a small local snapshot before refreshing online. Keep useful content visible when refresh fails.
- Customize Home sections: move up/down, show/hide and restore the default layout. Changes are saved separately for each listener.
- Keep Home useful offline with saved recommendations and direct Downloads/Local Music access. Play verified downloaded cards without starting a radio request; explain when an item needs a connection.
- Preserve stable playback, player/Settings refinements, guest Listen Together, Veyra backend and external cron support.

**Preview source**, version 0.1.6-beta1/code 21. Stable 0.1.5/code 20 remains on main. A first online load is needed to populate Home; cached recommendations do not download their music. Existing image caching/fallbacks remain.

[Verification and known build/device limits](verification/HOME-0.1.6-beta1-RESULTS.md). No APK is generated with this source delivery. Production-release Kotlin compilation now passes with the environment proxy/CA configured; the earlier Home-specific verification below records its historical dependency limitation. Device and performance validation remain separate.

Made by Bhavishya Dalal.
