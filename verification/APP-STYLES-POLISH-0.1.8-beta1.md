# App-style composition refinement

**Preview 0.1.8-beta1 / code 27 · 21 October 2026**

## What changed

The three Home styles no longer share the same recommendation grid treatment:

- **Music Editorial** alternates larger portrait covers against square covers, with baseline-aligned captions, burgundy section rules and a small magazine masthead. All items remain in their original order across four-item horizontal pages.
- **Listening Room** uses measured cut-paper title tabs instead of full-width slabs, static semi-transparent tape details on framed sleeves and an italic serif subtitle. Frames/tape stay decorative; card hit regions and menus are unchanged.
- **After Dark** gives each recommendation page a full-width feature with a dark footer scrim and three numbered thumbnail rows. Numbering follows the actual page offset. The record has an inset accent ring, and a new 48 dp arrow opens the same existing player action as the record image. Opening it does not request playback.

Current/paused/playing feedback and all tap/long-press callbacks remain. Large text keeps a simple ordered grid instead of asymmetry or small track rows. The existing navigation sizing, RTL behavior, style palettes, Original-theme suspension/restoration, fonts, accounts, refresh, offline guards, pagination and player state are unchanged. No new assets, dependencies, background loops or version changes were introduced. The other screens retain their coordinated app-style surfaces.

## Actual focused verification

- Android `:app:compileProdReleaseKotlin` passed after final source changes. No APK assembly; existing unrelated compiler warnings remain.
- The native style renderer passed and refreshed Home, representative secondary and 320 dp / 200% font / RTL component studies for each style. Three new shelf-only cases check **four item callbacks, one explicit long-press menu and the existing Play callback**; every item/menu remains available. Initial rendering triggers no action callbacks.
- The After Dark cover-swap probe changed **893 sampled pixels** in the record region. Idle and missing-file abstract fallback regions matched; artwork changes triggered no additional playback/action callbacks.
- Inspected the three new shelf captures. They use actual shared style components with local theme-art fixtures. They are **native component studies, not Android screenshots or live song-data tests**.
- Website build/types and two focused mobile/desktop download-link checks passed.

[Editorial shelf](../themes/previews/app-styles-0.1.8/music_editorial-shelves.webp) · [Listening Room shelf](../themes/previews/app-styles-0.1.8/listening_room-shelves.webp) · [After Dark shelf](../themes/previews/app-styles-0.1.8/after_dark-shelves.webp) · [Native result record](../themes/previews/app-styles-0.1.8/RESULTS.txt)

The previous four style-policy tests were not repeated: persistence/palette/suspension policy did not change. No broad audits or unrelated checks were run. Source is staged before packaging; all retained archives are checked with `scripts/verify_archives.py`. Final delivery metadata records actual repository commits and the public source ZIP hash separately.

## Limits

Real-device layouts, TalkBack, gestures, artwork/network timing, playback continuity and FPS remain unverified. These are responsive native implementations, not a guarantee of identical pixels on every device. Rebuild/install the preview APK to receive code changes. No APK built, no Actions dispatched, no stable promotion; stable source/archive and published packs remain unchanged. Website Git push does not establish a Vercel deployment.
