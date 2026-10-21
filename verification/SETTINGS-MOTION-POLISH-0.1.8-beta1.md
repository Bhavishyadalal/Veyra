# Settings and motion refinement · 0.1.8-beta1

Preview-only follow-up to the owner's report of intermittent lag and transition glitches.

- Navigation keeps the parent page stationary instead of moving two full themed pages together. Tabs use a short dissolve. Forward/back slides travel less; reduced motion, RTL, retained scroll state and fixed-size transitions are preserved.
- Account selector uses a stable themed tonal surface instead of sampling the moving screen into an additional blur layer throughout opening/closing.
- Preset pickers lazily compose visible choices instead of constructing every country row up front. Selection waits for the sheet to hide and repeated taps are guarded.
- Experience settings are separated into Player, Mini player, Gestures and Network tabs. Home, navigation and font settings use consistent rounded tonal groups with clear section headings.
- Closed Home/navigation/font launchers no longer collect every experience-settings update. Country display names are cached by locale. Imported-font controls scroll on small screens; duplicate preset value subtitles are suppressed.

Verification: production-release Kotlin compilation and focused diff review; website build, type check and existing mobile/desktop download-link checks. No new tests, broad app suite, APK assembly or screenshot matrix. Source archives retain matching checksums.

These changes address concrete composition/rendering costs. They do not establish that every reported glitch is fixed or guarantee device frame rates; phone profiling remains necessary for any remaining stutter.
