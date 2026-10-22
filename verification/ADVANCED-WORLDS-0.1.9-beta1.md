# Advanced Worlds · focused verification

Scope: preview/0.1.9-beta1, build code 31. Stable branches and backend are outside this change.

- Android production release Kotlin compilation passes (`:app:compileProdReleaseKotlin`, JDK 17, offline cached dependencies). Existing compiler warnings remain. This is compilation, not an assembled/signed APK.
- `CheckAdvancedWorlds.java` runs against actual compiled production ThemePack/serializer/ThemeGlyphs classes. All five ZIPs install through the real installer with catalog outer byte counts/hashes, exact manifest membership and inner hashes. All 119 mapped assets exist; every image passes real size policy. All five packs reject build 30. The previous 52 manifests still validate on build 31.
- ThemePackTest expectations now cover the 57-pack catalog and schema-4/version/preset/role rejection. The broad unit/device suites were not run; the focused production installer probe was used for this asset/engine change.
- Before publication, all 52 existing pack ZIPs and checksum sidecars are compared with parent HEAD; their bytes are unchanged. Catalog copies are byte-identical and remain under the existing 64 KiB limit.
- HTML preview rendering verifies every referenced image loads, then renders the collection overview and all ten screen/asset sheets. These are browser design studies using pack assets. Gallery layouts were visually reviewed; they are not evidence of Android rendering, accessible-text scaling or device performance.
- Source ZIP is regenerated from staged sources, with matching SHA-256, current catalog pack inclusion, checksummed reversible image deduplication and the existing four-archive retention policy. Native font/license build inputs stay expanded. Archive verification checks hashes, backend provenance, pack contents and new schema-dependent file limits.
- Website build/typecheck and focused desktop/mobile theme-link checks run separately. Only preview links change; no APK publication or Vercel deployment claim follows from Git push.

Native bounds remain app-owned: no downloaded scripts/layouts; fixed touch targets, real playback/input fractions, finite page transitions, reduced/subtle-motion priority and old-schema limits remain intact. No reactive characters are added. A future new semantic action retains its native fallback until explicitly mapped. Some Material control internals (for example switch track behavior) remain native while taking theme colors and thumb glyphs.

Required device follow-up: install build 31, download/apply/switch all five themes, inspect Home/Search/Library/artist/Settings/player/immersive scenes, test seek/volume and RTL, switch font/artwork overrides, verify custom-style suspension and restart persistence, check large text and enabled/disabled/error glyph states, then measure actual frame times on representative hardware. No 60-fps or glitch-free claim is made without those measurements.
