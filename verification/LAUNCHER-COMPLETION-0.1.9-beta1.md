# Matching icon completion · beta build 35

- 16 theme-specific and five asset-specific launcher choices, 95 total.
- All 62 theme IDs resolve only to bundled aliases; five asset IDs use a separate
  allowlisted mapping. Unknown IDs cannot select arbitrary Android components.
- New native icon artwork retains the current connected-wave brand identity;
  existing choices/aliases and all immutable theme/asset ZIPs are preserved.
- Home uses the actual selected launcher painter rather than always taking a
  theme's brandIcon. Selection flows update only after successful package-manager
  changes. Startup/resume discovery runs off the UI thread. Typography/layout is
  unchanged, and existing independent font choices remain intact.
- Home and picker use shared, bounded 256 px asynchronous resource requests; no
  bitmap resource decoding is added to the UI-thread composition path.
- App icon picker includes Asset packs; downloaded asset cards have an explicit
  matching-icon action. Icon selection remains independent of theme application.

Focused verification covers catalog/binding/resource completeness, density/image
bounds, adaptive/monochrome references, default-enabled alias uniqueness, style
filter counts and a production Kotlin compile. Website build/type/mobile/desktop
links and source ZIP metadata/checksums are checked. No broad device test, APK
publication or guaranteed OEM launcher-cache refresh is claimed.
