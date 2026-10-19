# Veyra source downloads

Only the current stable Android source and the newest three preview source ZIPs
are retained here. All include the current Veyra backend snapshot under `backend/`,
its licenses, manifest and cron helpers. Each has a `.zip.sha256` checksum beside it.

| Track | Version | Source ZIP | Branch |
| --- | --- | --- | --- |
| Current stable | 0.1.5 / code 20 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.5_Stable.zip) | `main` |
| Current preview | 0.1.6-beta1 / code 21 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.6-beta1/downloads/Veyra_Android_Source_0.1.6-beta1.zip) | `preview/0.1.6-beta1` |
| Earlier preview | 0.1.5-beta1 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.5-beta1/downloads/Veyra_Android_Source_0.1.5-beta1.zip) | `preview/0.1.5-beta1` |
| Earlier preview | 0.1.4-beta1 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.4-beta1/downloads/Veyra_Android_Source_0.1.4-beta1.zip) | `preview/0.1.4-beta1` |

Extract the entire `Veyra` folder and open it in Android Studio. These are source
projects, not APKs. Keep stable and preview directories separate. Earlier previews
retain their historical app versions/UI and now have the backend migration;
prefer current stable or current preview for testing. No new APK is produced here.

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
