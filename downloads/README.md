# Veyra source downloads

**Stable 0.2.2 · code 59** adds tap-to-type Search, optional appearance preparation and lighter navigation rendering. New downloads continue to use **Internal storage → Music → Veyra**. Retains automatic updates, waveform notifications, 12 immersive modes, 72 themes, 32 overlays and 132 launcher choices. [Release notes](../RELEASE_NOTES_0.2.2.md) · [Verification and limits](../verification/STABLE-0.2.2-PROMOTION.md).

| Track | Version | Source ZIP | Checksum |
| --- | --- | --- | --- |
| Current stable · `main`, `stable/0.2.2` | 0.2.2 / code 59 | [Download](Veyra_Android_Source_0.2.2_Stable.zip) | [SHA-256](Veyra_Android_Source_0.2.2_Stable.zip.sha256) |
| Current preview · `preview/0.2.3-beta1` | 0.2.3-beta1 / code 60 | [Download](Veyra_Android_Source_0.2.3-beta1.zip) | [SHA-256](Veyra_Android_Source_0.2.3-beta1.zip.sha256) |
| Completed preview · `preview/0.2.2-beta1` | 0.2.2-beta1 / code 58 | [Download](Veyra_Android_Source_0.2.2-beta1.zip) | [SHA-256](Veyra_Android_Source_0.2.2-beta1.zip.sha256) |
| Completed preview · `preview/0.2.1-beta1` | 0.2.1-beta1 / code 55 | [Download](Veyra_Android_Source_0.2.1-beta1.zip) | [SHA-256](Veyra_Android_Source_0.2.1-beta1.zip.sha256) |

These are complete Android Studio projects, not installable APKs. Verify the SHA-256 sidecar, extract the entire Veyra folder and open it in Android Studio. Native build resources, launcher masters, fonts/licenses and runtime packs remain included. Restore the lossless runtime library offline with `python3 scripts/restore_source_packs.py`; optional authoring sources restore with `scripts/restore_theme_sources.py` without overwriting edits.

Stage source before packaging:

```sh
python3 scripts/route_catalogs.py
python3 scripts/package_source.py Veyra_Android_Source_0.2.3-beta1.zip
python3 scripts/verify_archives.py
```

Prior stable [0.2.1 / code 56](https://github.com/Bhavishyadalal/Veyra/tree/stable/0.2.1) and its original source ZIP remain on its branch/history. Older stable and preview branches are preserved. Only the current stable and newest three preview ZIPs remain at this branch tip; no history, tags or release assets are deleted.

Backend provenance is included: [sync policy](../docs/BACKEND_SYNC.md). Official rooms/invites use https://veyra-listen-together.onrender.com; intentional custom servers remain supported. Website source is maintained separately in [Veyra-website](https://github.com/Bhavishyadalal/Veyra-website).
