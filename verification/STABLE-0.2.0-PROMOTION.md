# Stable 0.2.0 promotion

- Stable version: **0.2.0 / code 47**, beta suffix empty.
- Branches: `main` and `stable/0.2.0`.
- Completed preview: `preview/0.2.0-beta1`, **0.2.0-beta1 / code 46**, commit `c1e45617d5a48f55e7bebedfc82e110e8422b67a`.
- Owner explicitly authorized verification and promotion in this session.
- 72 themes / 32 overlays / 132 launcher choices. All old pack bytes remain immutable.
- Stable catalogs and bundled fallbacks route to `main`; preview catalogs remain on their branch. Canonical URL, size, checksum and data-only validation stay enforced.
- Previous `stable/0.1.9`, older stable branches and historical previews remain intact. Current source retention: stable 0.2.0 plus the newest three preview ZIPs. The superseded stable 0.1.9 ZIP remains on its frozen branch/history.

## Verification

Production Android resources/Kotlin, launcher manifest and stable version metadata; runtime stable catalog policy; the new pack/icon tests and real Compose hold lifecycle regressions. Full preview checks and precise changes: [Pocket Riot](POCKET-RIOT-0.2.0.md), [Chaos Studio](CHAOS-STUDIO-0.2.0-beta1.md), [reliability review](RELIABILITY-REVIEW-0.2.0-beta1.md).

Source archive checks cover all manifests/hashes and backend provenance. Complete offline restoration verifies original ZIP bytes, optional fonts/images, strict namespaces and preview aliases, and user-edit protection. Reviewed glass masters/270 native exports and exact legacy resource aliases validate. Website build/typecheck and phone/desktop links/layout/images are checked before publishing its matching links.

No signing material is read or committed. Source promotion is not signed-APK publication, a real-device FPS benchmark or proof that every provider/device flow is defect-free. Rebuild/install code 47 for new native icons and fixes. Existing release assets/tags and historical branches are not removed.
