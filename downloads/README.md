> **Active preview: 0.2.0-beta1 / build 46** · [Baseline notes](../RELEASE_NOTES_0.2.0-beta1.md). Stable remains 0.1.9 build 40 on its existing branches.

# Veyra source downloads

**Stable 0.1.9 · code 40** adds [six immersive presentations](../verification/IMMERSIVE-WORLDS-0.1.9.md) with direct player access. It includes 62 themes, 15 independent asset packs and 105 launcher choices. These complete Android Studio projects include licenses and backend provenance. **Source ZIPs are not APKs.**

| Track | Version | Source ZIP | Checksum |
| --- | --- | --- | --- |
| Current stable · `main`, `stable/0.1.9` | 0.1.9 / code 40 | [Download](Veyra_Android_Source_0.1.9_Stable.zip) | [SHA-256](Veyra_Android_Source_0.1.9_Stable.zip.sha256) |
| Active preview · `preview/0.2.0-beta1` | 0.2.0-beta1 / code 46 | [Download](Veyra_Android_Source_0.2.0-beta1.zip) | [SHA-256](Veyra_Android_Source_0.2.0-beta1.zip.sha256) |
| Completed preview · `preview/0.1.9-beta1` | 0.1.9-beta1 / code 35 | [Download](Veyra_Android_Source_0.1.9-beta1.zip) | [SHA-256](Veyra_Android_Source_0.1.9-beta1.zip.sha256) |
| Historical preview · `preview/0.1.8-beta1` | 0.1.8-beta1 / code 27 | [Download](Veyra_Android_Source_0.1.8-beta1.zip) | [SHA-256](Veyra_Android_Source_0.1.8-beta1.zip.sha256) |

[Stable release notes](../RELEASE_NOTES_0.1.9.md) · [Promotion checks and limits](../verification/STABLE-0.1.9-PROMOTION.md) · [Completed beta notes](../RELEASE_NOTES_0.1.9-beta1.md)

## Open and verify

Verify the matching SHA-256 sidecar, extract the entire `Veyra` folder and open it in Android Studio. Source packages omit secrets, local configuration, signing keys, APKs and duplicate promotional/gallery images. Native build resources, launcher masters, fonts/licenses and all current runtime packs remain included. Optional authoring files restore from checked pack mappings with `python3 scripts/restore_theme_sources.py`; existing edits are never overwritten.

Stage source before packaging:

```sh
python3 scripts/route_catalogs.py
python3 scripts/package_source.py Veyra_Android_Source_0.2.0-beta1.zip
python3 scripts/verify_archives.py
```

## Preserved history

[0.1.7-beta1 source archive](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/preview/0.1.7-beta1/downloads/Veyra_Android_Source_0.1.7-beta1.zip) remains on its completed preview branch.

Stable 0.1.8 / code 28 remains on [stable/0.1.8](https://github.com/Bhavishyadalal/Veyra/tree/stable/0.1.8), including [its original ZIP](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/stable/0.1.8/downloads/Veyra_Android_Source_0.1.8_Stable.zip). Stable 0.1.7 / code 26 and 0.1.6 / code 22 remain on their respective stable branches. Only current stable and the three newest preview ZIPs are retained at this branch tip; all preview ZIP bytes are unchanged. No history, tags or release assets are removed.

Rooms, invites and presence use **https://veyra-listen-together.onrender.com**; intentional custom servers remain supported. [Backend provenance](../docs/BACKEND_SYNC.md). Website source is maintained separately in [Veyra-website](https://github.com/Bhavishyadalal/Veyra-website). APK publication is separate.

The latest preview stores runtime theme/overlay ZIPs in an included lossless `SOURCE_DATA_PACKS.tar.xz` library. Restore them offline with `python3 scripts/restore_source_packs.py`. Android build inputs and native icon masters remain expanded. This avoids duplicated pack bytes pushing the source download above GitHub’s 100 MiB limit; original pack bytes and checksums are preserved.
