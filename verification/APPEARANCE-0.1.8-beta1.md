# Beta 0.1.8 · Theme polish and independent fonts

**Preview only · 0.1.8-beta1 / code 27.** Stable Android, its source ZIP and published theme-pack bytes remain unchanged.

## Delivered changes

- More visible static theme artwork on Home, Search and Library. A palette-aware scrim protects foreground and muted text against even white artwork. Real music covers and existing loading/navigation/playback behavior remain intact.
- Removed Home's decorative trailing waveform. Tightened and centered the wordmark line box beside the icon, including theme fonts and RTL.
- All icon entry points open on **All**, with all 74 choices once and search across the set. Existing collections, glass-family filters, aliases and current-selection feedback remain.
- Studio previews use remembered, revision-aware requests through the existing shared Coil loader. Thumbnail/detail memory identities and bounded decode sizes are separate; encoded disk entries are shared by revision. Cached pixels provide placeholders; preview crossfade is disabled. Installed artwork stays local. A music motif covers cold/error thumbnail states. Revision parameters invalidate reused HTTP preview paths.
- **Appearance → Fonts:** Follow theme, Veyra default and six independent presets. Preview before Apply; manual choices survive theme changes and persist in existing preferences/backup. Appearance reset restores Follow theme. Expressive headings use readable companion body text and platform glyph fallback.
- Reused licensed Manrope, Fredoka, Space Grotesk, Rajdhani, Barlow Condensed and Grenze Gotisch. Gradle generates resources from existing theme sources instead of duplicating font binaries in Git/source ZIPs; six OFL notices are generated into app assets.
- Extended functional-icon mappings, preserved native fallbacks, and corrected themed bitmap icons to Material's **24 dp default** while retaining explicit sizing. Floating Back controls use pack geometry and 48 dp targets. Existing themed transport/queue/lyrics actions remain.

No character, gyro, new animation engine, theme-schema or backend changes were added. Illustrated animals already inside theme artwork are unchanged.

## Actual focused checks

| Check | Result |
| :--- | :--- |
| AppearancePolicyTest | 4 passed: preference fallback/round-trip and theme precedence, preview identity, bright-artwork contrast guard |
| ThemeLauncherTest | 5 passed: all 74 choices, aliases/default, group partition and All independent of glass family |
| Android release Kotlin compilation | Passed after final production changes, including generated font resources |
| Generated fonts/licenses | Six fonts match their original source bytes; six OFL notices present in generated assets |
| Coil 3.0.4 probe | Cold decode, warm memory hit without HTTP, revision invalidation, failure/retry recovery, fresh-loader offline disk hit, offline miss and exact placeholder/cache-key identity passed |
| Native studies | Home, Search at 320 dp/150%, RTL Library, RTL font selection, light font selection and gothic branding; checked targets >=48 dp, callbacks once, default Back glyph <=24 dp |
| Website | Production build/type checks and 2 focused desktop/mobile download-link checks passed |

Android used JDK 17, existing SDK/cached dependencies, offline mode, two workers and in-process Kotlin compilation. Existing warnings remain. The desktop probe reuses the network dependency already used by the app; no app dependency was added.

The [cache probe](APPEARANCE-CACHE-RESULTS.txt) uses real Coil memory/disk caches and a local HTTP server on desktop, not Android lifecycle/network testing. Native studies use actual shared theme, brand, font-choice and preview components in **illustrative layouts, not Android screenshots or full screen captures**. Their sample Back control does not mean Home gained a Back button. Changed studies were visually inspected; the shorter font preview was rechecked at large text and in light mode without an active theme pack.

## Delivery and limits

Stage source before packaging; refresh only the preview ZIP and checksum, then run `scripts/verify_archives.py`. This checks source manifests, retained archives, nested pack hashes and backend provenance. Website links distinguish stable from preview and point to this report. Push with skip-CI commits.

No APK assembly/signing, Actions dispatch, tag/release publication, backend deployment or verified Vercel deployment is included. No real-device tests were performed. Android Studio reopening, font Apply across process restarts, keyboard/small-window behavior, TalkBack, all language fallbacks, Back, launcher switching and playback remain unverified on hardware.

Cache eviction/clearing still requires remote refetches; installed local files remain available offline. The contrast guard checks theme text colors against the brightest background blend, not every overlay/control in the full app. Existing translation/lint backlog remains; new untranslated strings fall back to English. No FPS, startup or battery improvement is claimed.

[Beta notes](../RELEASE_NOTES_0.1.8-beta1.md) · [Earlier Keepsake verification](KEEPSAKE-0.1.8-beta1.md)
