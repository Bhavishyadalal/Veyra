# Pinned Home header and integrated artwork · stable 0.1.8

- Settings → Appearance → Home personalization → **Pin Home header** is enabled by default and saved with experience settings/backup. Turn it off to scroll the original header with the feed. Older saved settings default to enabled.
- Original Home keeps its existing brand icon, wordmark, greeting and subtitle above the scrolling feed, separated by one thin themed line. The parent artwork remains continuous; the header adds no opaque panel or duplicate scene.
- Account controls remain in the existing fixed top bar. Refresh, pagination, saved scroll state and feed controls remain attached to the feed below the header.
- Very short windows (under 420 dp available height) retain the scrollable heading so branding cannot consume the entire feed viewport. The three alternate Home styles retain their existing editorial openings.
- Removed the standalone theme showcase cards from Home and Settings. Page background artwork and Theme Studio remain available.
- Library/Replay listening cards now use the active theme's raised/surface colors, subtle artwork, outline and readable text colors. Themed cards skip song-palette extraction and the separate blurred mesh/shadow treatment; unthemed cards retain their existing appearance. Stats, navigation actions and card proportions remain unchanged.

Verification: production-release Kotlin compilation and focused review of header/list separation, padding, short-window behavior and themed/unthemed card branches. No broad test suite, APK build or device render/performance certification. Website build/type and existing desktop/mobile link checks cover updated source links. Source archive integrity is checked separately.
