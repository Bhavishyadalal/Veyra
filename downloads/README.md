# Veyra source downloads

Current stable Android source and the newest three preview source ZIPs are retained here. These are complete Android Studio projects, **not APKs**. Each ZIP includes licenses, the Veyra backend snapshot/provenance and a SHA-256 sidecar.

| Track | Version | Source ZIP | Checksum |
| --- | --- | --- | --- |
| Current stable · `main`, `stable/0.1.7` | 0.1.7 / code 26 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.7_Stable.zip) | [SHA-256](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.7_Stable.zip.sha256) |
| Current preview · `preview/0.1.8-beta1` | 0.1.8-beta1 / code 27 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.8-beta1/downloads/Veyra_Android_Source_0.1.8-beta1.zip) | [SHA-256](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.8-beta1/downloads/Veyra_Android_Source_0.1.8-beta1.zip.sha256) |
| Historical preview | 0.1.7-beta1 / code 25 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.7-beta1/downloads/Veyra_Android_Source_0.1.7-beta1.zip) | [SHA-256](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.7-beta1/downloads/Veyra_Android_Source_0.1.7-beta1.zip.sha256) |
| Historical preview | 0.1.6-beta1 / code 21 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.6-beta1/downloads/Veyra_Android_Source_0.1.6-beta1.zip) | [SHA-256](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.6-beta1/downloads/Veyra_Android_Source_0.1.6-beta1.zip.sha256) |

Stable 0.1.7 is promoted from preview commit `466433e6e8f29666af1cfb012b14b4a8c77b7ad7`. It includes Theme Studio, now 46 themes, 69 launcher choices and the listening/recovery/search fixes. The preview branch remains version 0.1.7-beta1; promotion does not turn its ZIP into a stable build.

The current stable ZIP also includes the [theme installation correction](../verification/THEME-INSTALL-0.1.7.md): accept the published 512×512 brand artwork while retaining smaller control-icon limits. Rebuild and install an updated APK to receive this fix; published theme-pack bytes remain unchanged.

[Stable release notes](https://github.com/Bhavishyadalal/Veyra/blob/main/RELEASE_NOTES_0.1.7.md) · [Promotion checks and known limits](https://github.com/Bhavishyadalal/Veyra/blob/main/verification/STABLE-0.1.7-PROMOTION.md) · [Theme collection](https://github.com/Bhavishyadalal/Veyra/blob/main/themes/README.md)

The [five Keepsake packs](../themes/KEEPSAKE.md) work on stable 0.1.7, including its original 256 px validator. New launcher icons and searchable Studio remain exclusive to beta 0.1.8. Source packages retain current catalog packs and the legacy test fixture; superseded theme ZIP copies remain in Git.

Current beta sources include [three optional app styles alongside Original](../verification/APP-STYLES-0.1.8-beta1.md), plus the [navigation background-layer fix](../verification/BACKGROUNDS-0.1.8-beta1.md) and [deep sheet/artist-background and connected-control corrections](../verification/DEEP-THEME-POLISH-0.1.8-beta1.md). Rebuild/install a preview APK to receive app-code changes; theme downloads alone do not update the installed app.

Historical preview 0.1.5 source remains available on [its preview branch](https://github.com/Bhavishyadalal/Veyra/tree/preview/0.1.5-beta1/downloads).

## Historical stable 0.1.6

The original stable 0.1.6 archive remains available through an immutable historical commit: [source ZIP](https://github.com/Bhavishyadalal/Veyra/raw/466433e6e8f29666af1cfb012b14b4a8c77b7ad7/downloads/Veyra_Android_Source_0.1.6_Stable.zip) · [checksum](https://github.com/Bhavishyadalal/Veyra/raw/466433e6e8f29666af1cfb012b14b4a8c77b7ad7/downloads/Veyra_Android_Source_0.1.6_Stable.zip.sha256). Historical Android code remains on `stable/0.1.6`. No Git history, tags or release assets are deleted.

## Open and verify

Extract the entire `Veyra` folder and open it in Android Studio. Keep stable and preview folders separate. Verify the sidecar and the ZIP's `SOURCE_MANIFEST.json`. Source packages omit credentials, local configuration, signing material, APKs and duplicated generated component-study images; renderer sources and all theme assets/packs remain included.

Stage source before packaging:

```sh
python3 scripts/package_source.py Veyra_Android_Source_0.1.7_Stable.zip
python3 scripts/verify_archives.py
```

Rooms, invites and presence use **https://veyra-listen-together.onrender.com**. Custom servers remain supported. [Backend provenance](https://github.com/Bhavishyadalal/Veyra/blob/main/docs/BACKEND_SYNC.md) · [External cron setup](https://github.com/Bhavishyadalal/Veyra/blob/main/backend/CRON_SETUP.md).

Website source is maintained separately in [Veyra-website](https://github.com/Bhavishyadalal/Veyra-website). Website/media/backend standalone ZIPs are not reintroduced. No APK build, Actions dispatch, signing operation or deployment is part of this source promotion. Known translation/lint and device-testing limits are preserved in the promotion report.

The refreshed **0.1.8-beta1 / code 27** preview source also includes [theme polish and independent fonts](../verification/APPEARANCE-0.1.8-beta1.md). The stable source archive is unchanged by this preview update.
