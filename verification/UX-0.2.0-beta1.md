> Current preview: **build 43** adds [Navigation studio](NAVIGATION-STUDIO-0.2.0-beta1.md). Earlier build notes follow.

> Current preview: **build 42** adds the [Liquid Glass/theme fix](LIQUID-GLASS-0.2.0-beta1.md). The initial build 41 baseline is recorded below.

# Preview 0.2.0-beta1 · baseline build 41

Starts from completed stable 0.1.9 build 40 at `a82d7228ac7cd2c3652ba384447b7691972c01a3`. Future work belongs on `preview/0.2.0-beta1` until the owner explicitly requests stable promotion.

## Track setup

The base version is **0.2.0**, suffix **beta1**, code **41**, producing **0.2.0-beta1** for both production release and debug builds. The package identity and signing configuration are retained.

Both theme and asset repositories derive their allowed root from the effective version name. All 62 theme and 15 asset catalog entries, including bundled fallbacks, now point to `preview/0.2.0-beta1`. Strict path/checksum/minimum-version checks remain enabled. Run `python3 scripts/route_catalogs.py` after authoring a new catalog to keep generated URLs on this build's track; it does not modify pack ZIPs.

The current stable source archive remains byte-for-byte unchanged. The newest three preview archives are 0.2.0-beta1, 0.1.9-beta1 and 0.1.8-beta1. The 0.1.7-beta1 archive remains available on its historical branch and in Git history.

## Focused verification

- Production release Kotlin/resources compile and release/debug build metadata generation.
- Production repository parsers accept all 77 preview listings and bundled fallbacks, and reject URLs from the other release track.
- Existing pack checksums and launcher masters remain unchanged.
- Source archive membership, source/pack hashes, backend snapshot and checksum sidecars are verified; no APKs or signing secrets are included.
- Website build/typecheck and mobile/desktop links distinguish stable 0.1.9 from preview 0.2.0-beta1.
- Before/after remote-head checks preserve every pre-existing stable/preview branch.

This baseline adds no new product behavior. Broad UI/performance tests were not rerun; it inherits the user-tested stable UI. No signed APK, real-device benchmark or stable promotion is claimed.
