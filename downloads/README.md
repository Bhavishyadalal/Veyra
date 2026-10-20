# Veyra source downloads

Only the current stable Android source and the newest three preview source ZIPs
are retained here. All include the current Veyra backend snapshot under `backend/`,
its licenses, manifest and cron helpers. Each has a `.zip.sha256` checksum beside it.

| Track | Version | Source ZIP | Branch |
| --- | --- | --- | --- |
| Current stable | 0.1.6 / code 22 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.6_Stable.zip) | `main` |
| Current preview | 0.1.7-beta1 / code 25 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.7-beta1/downloads/Veyra_Android_Source_0.1.7-beta1.zip) | `preview/0.1.7-beta1` |
| Earlier preview | 0.1.6-beta1 / code 21 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.6-beta1/downloads/Veyra_Android_Source_0.1.6-beta1.zip) | `preview/0.1.6-beta1` |
| Earlier preview | 0.1.5-beta1 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.5-beta1/downloads/Veyra_Android_Source_0.1.5-beta1.zip) | `preview/0.1.5-beta1` |

Extract the entire `Veyra` folder and open it in Android Studio. These are source
projects, not APKs. Keep stable and preview directories separate. Earlier previews
retain their historical app versions/UI and now have the backend migration;
prefer current stable for normal use. Preview 0.1.7-beta1 adds [Theme Studio, Playtime Club, Web Slinger, Halloween branding and native previews](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/themes/README.md). Stable 0.1.6 does not include the installer. No new APK is produced here.

Rooms, invites and presence use **https://veyra-listen-together.onrender.com**.
Custom room servers remain supported. Retired operator hostnames exist only in
migration rejection policy/tests, never as active Android destinations.

[Backend provenance](../docs/BACKEND_SYNC.md) · [External cron setup](../backend/CRON_SETUP.md)
· [Maintenance verification and build limits](../verification/BRANCH-MAINTENANCE.md)

Website source is maintained separately in
[Bhavishyadalal/Veyra-website](https://github.com/Bhavishyadalal/Veyra-website).
Artwork remains browsable in this repository's [media kit](../media-kit/README.md).
Old stable/candidate, website, media-kit and standalone-backend ZIPs and their
checksum sidecars have been removed from all app branch tips. Their source code,
artwork and legal notices are preserved. Git history and release assets are unchanged;
this cleanup reduces branch checkouts, not historical repository storage.

For future refreshes, stage source changes and run from the matching branch:

```sh
python3 scripts/package_source.py Veyra_Android_Source_VERSION.zip
```

Verify the package's `SOURCE_MANIFEST.json` and checksum before publishing. Copy
identical refreshed archives across branch catalogs to avoid divergent packages.

Current preview source includes [listening continuity, recovery and accessibility improvements](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/verification/RELIABILITY-0.1.7-beta1.md). Stable Android source is unchanged; source ZIPs are not APKs.

The current preview also includes [Astral Tide](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/themes/README.md#astral-tide--drift-deeper-hear-everything), an original ocean theme and optional manta-waveform launcher. Version remains 0.1.7-beta1 / code 25.

The latest preview adds [Midnight Express](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/themes/README.md#midnight-express--your-next-stop-another-song), an original night-train theme and optional ticket-waveform launcher. Stable 0.1.6 / code 22 source ZIP remains unchanged for release builds.

The refreshed preview includes [bounded Home loading, Explore request cancellation, background download recovery writes and theme image/RTL polish](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/verification/PERFORMANCE-0.1.7-beta1.md). It remains 0.1.7-beta1 / code 25; stable source and published theme packs are unchanged.

The newest preview includes [six reference-inspired themes](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/themes/README.md#reference-collection--six-more-worlds), bringing the catalog to eleven, with six optional bundled launcher marks. [Verification and compatibility limits](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/verification/REFERENCE-COLLECTION-0.1.7-beta1.md). Version remains 0.1.7-beta1 / code 25; stable 0.1.6 and previously published packs remain unchanged.

The latest source refresh includes [six AI-edited Glass Noir theme icons](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/verification/GLASS-ICONS-0.1.7-beta1.md), keeping Veyra's current black-and-white glass waveform identity across Home and optional launchers. Collection packs are now v2; published v1 packs and stable 0.1.6 remain unchanged.

The refreshed preview adds [Glass Atelier: twelve standalone glass launcher icons](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/design/launcher/atelier/README.md), in a separate picker collection independent of themes. [Verification](https://github.com/Bhavishyadalal/Veyra/blob/preview/0.1.7-beta1/verification/ATELIER-ICONS-0.1.7-beta1.md). App remains 0.1.7-beta1 / code 25; no APK is included.

The latest preview adds five [Wayfarer themes](../themes/WAYFARER.md) and themed bottom navigation. Source ZIPs retain Android/backend source, theme packs/artwork/fonts and preview renderers; generated images under `themes/previews/` stay available in the GitHub gallery instead of being duplicated in every source download.

The current preview also includes five [Dreamscape themes](../themes/DREAMSCAPE.md), matching optional launcher/Home icons and custom navigation/transport glyphs. [Verification](../verification/DREAMSCAPE-COLLECTION-0.1.7-beta1.md). Version remains 0.1.7-beta1 / code 25.
