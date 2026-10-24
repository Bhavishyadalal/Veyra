# Pocket Riot · release completion

Preview checkpoint: **0.2.0-beta1 / code 46**. Stable promotion: **0.2.0 / code 47**.

## Delivered

- Ten separate overlays, 1,190 runtime asset roles, distinct cute/punk frames, fonts, progress treatments and separate pressed transports.
- Twenty-seven reviewed AI glass waveform masters, 270 native density exports and adaptive/monochrome resources. Explicit launcher bindings for all 72 themes and all 32 overlays; 132 total choices, correct picker groups and shared Home/picker rendering.
- Fourteen missing functional vector aliases mapped to existing semantic roles. Old packs gain coverage without modifying their published bytes; artwork/logos retain identity.
- Asset Studio search plus All/Downloaded filters; sheet-hidden guards during apply in both Studios.

[Actual skins](../asset-packs/previews/pocket-riot/collection.webp) · [Actual glass masters](../design/launcher/pocket-riot/previews/glass-editions.webp)

## Concrete corrections

1. A cancelled player hold could leave its click-suppression flag set, particularly when hold support changed. Reset on interaction lifecycle changes; retain suppression for a completed hold's release.
2. Ignoring a sheet-dismiss callback while Apply ran did not stop Material's sheet from becoming hidden. Both Studios now reject Hidden through their retained sheet-state callback, using current busy state. Done is disabled during Apply.
3. Direct asset-catalog parsing now enforces the same 64 KiB bound as HTTP/bundled reads. The 32-pack cap, ID uniqueness, canonical URLs, checksums and data-only validators remain.
4. A font shared by new overlays and themes could overwrite the authoring-font restore index with the wrong namespace. Separate theme/asset font pools preserve strict restoration paths. The public asset preview alias now has an explicit, tightly bounded restore rule for the offline specimen. Optional old authoring fonts also deduplicate into included immutable packs; all Gradle font inputs remain expanded.

5. Twelve legacy glass families shared byte-identical foreground images at all five densities. Eleven Android mipmap aliases now point to the retained canonical set, removing 55 duplicate files without changing a pixel, family mask, resource name or launcher choice. Master images remain.

## Focused verification

- Production Android Kotlin/resources, launcher manifest processing and version metadata.
- New pack production installer tests including fonts/images, role counts, checksums and distinct pressed states; catalog bound/duplicate rejection.
- Every theme/overlay maps to a bundled, correctly grouped launcher. Manifest aliases are unique; only the original default ships enabled. All 270 native image dimensions, master SHA-256 provenance and shared monochrome resources checked.
- Real Compose pointer regression: cancelled hold followed by a normal tap after removing hold support, and completed hold release without an extra click. Existing request/account and theme-shape regressions retained.
- Static galleries and website phone/desktop link/layout/image checks; website build/typecheck.
- Source archives, backend manifests and lossless pack/font restoration verified offline; existing edits protected. Original 104 prior/new catalog pack bytes validate; older published packs remain unchanged.

These checks do not prove device frame rates, every playback provider/device path or zero remaining defects. No signed APK is published by source promotion. Install code 47 for new launcher aliases and native fixes. The previous stable/0.1.9 branch and historical previews remain intact.
