# Veyra source downloads

Current stable and the three newest preview archives are retained here. Each is a complete Android Studio project with licenses, backend provenance and a matching SHA-256 sidecar. These are **not APKs**.

| Track | Version | Source ZIP | Checksum |
| --- | --- | --- | --- |
| Current stable · `main`, `stable/0.1.8` | 0.1.8 / code 28 | [Download](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.8_Stable.zip) | [SHA-256](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.8_Stable.zip.sha256) |
| Current preview · `preview/0.1.9-beta1` | 0.1.9-beta1 / code 30 | [Download](Veyra_Android_Source_0.1.9-beta1.zip) | [SHA-256](Veyra_Android_Source_0.1.9-beta1.zip.sha256) |
| Completed preview · `preview/0.1.8-beta1` | 0.1.8-beta1 / code 27 | [Download](Veyra_Android_Source_0.1.8-beta1.zip) | [SHA-256](Veyra_Android_Source_0.1.8-beta1.zip.sha256) |
| Historical preview | 0.1.7-beta1 / code 25 | [Download](Veyra_Android_Source_0.1.7-beta1.zip) | [SHA-256](Veyra_Android_Source_0.1.7-beta1.zip.sha256) |

Stable 0.1.8 is promoted from completed preview commit `646e34b56295e1fbfaddcb3cbdc71fa5038eb196`. It includes all shipped beta features and the final Settings/transition refinements, 46 published themes and 74 launcher choices. Theme packs still support stable 0.1.7; rebuilding is necessary for new app features and launcher aliases.

[Release notes](../RELEASE_NOTES_0.1.8.md) · [Promotion verification and limits](../verification/STABLE-0.1.8-PROMOTION.md) · [Theme collection](../themes/README.md)

The 0.1.9-beta1 preview starts from completed stable commit `6e37aa4615bad01b895dc547454f79218d94175e`; the current preview includes the [listening and personalization update](../verification/UX-0.1.9-beta1.md). [Preview notes](../RELEASE_NOTES_0.1.9-beta1.md).

The rotated 0.1.6-beta1 archive remains available through [its original retained snapshot](https://github.com/Bhavishyadalal/Veyra/tree/6e37aa4615bad01b895dc547454f79218d94175e/downloads). Stable 0.1.8 and the two retained historical preview ZIPs preserve their exact bytes.

## Historical stable sources

- Stable 0.1.7 / code 26: [source ZIP](https://github.com/Bhavishyadalal/Veyra/raw/5d989869addce3183b3b78f80813cd6dc6086210/downloads/Veyra_Android_Source_0.1.7_Stable.zip) · [checksum](https://github.com/Bhavishyadalal/Veyra/raw/5d989869addce3183b3b78f80813cd6dc6086210/downloads/Veyra_Android_Source_0.1.7_Stable.zip.sha256). Android source remains on `stable/0.1.7`.
- Stable 0.1.6: [source ZIP](https://github.com/Bhavishyadalal/Veyra/raw/466433e6e8f29666af1cfb012b14b4a8c77b7ad7/downloads/Veyra_Android_Source_0.1.6_Stable.zip) · [checksum](https://github.com/Bhavishyadalal/Veyra/raw/466433e6e8f29666af1cfb012b14b4a8c77b7ad7/downloads/Veyra_Android_Source_0.1.6_Stable.zip.sha256). Android source remains on `stable/0.1.6`.

Only branch-tip archive retention changes; no Git history, tags or release assets are deleted. Retained preview ZIP bytes are unchanged.

## Open and verify

Extract the entire `Veyra` folder, then open it in Android Studio. Verify the sidecar and the ZIP's `SOURCE_MANIFEST.json`. Source packages omit credentials, local configuration, signing keys, APKs and generated study images; renderer sources and theme assets remain included.

Stage source before packaging:

```sh
python3 scripts/package_source.py Veyra_Android_Source_0.1.8_Stable.zip
python3 scripts/verify_archives.py
```

Rooms, invites and presence use **https://veyra-listen-together.onrender.com**. Intentional custom servers remain supported. [Backend provenance](../docs/BACKEND_SYNC.md) · [External cron setup](../backend/CRON_SETUP.md).

Website source is maintained separately in [Veyra-website](https://github.com/Bhavishyadalal/Veyra-website). Source promotion does not build, sign, publish or deploy an APK.

Current beta source includes the [Margin Notes sketch engine/theme](../themes/MARGIN_NOTES.md), build code 30. Duplicate runtime images are stored once in included theme packs; restore optional editable images with `python3 scripts/restore_theme_sources.py`. App build inputs and theme data remain included.

The latest beta source also includes [five Illustrated Worlds packs](../themes/ILLUSTRATED_WORLDS.md) and their generation/gallery sources. Existing build-30 APKs can download the packs through Studio Refresh.
