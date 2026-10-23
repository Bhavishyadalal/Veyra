# Veyra / Connected-wave icon editions

21 new selectable launcher icons: all 16 previously missing theme editions and
five independent Layer Lab asset-pack editions. These extend the original 74
choices to **95**, without removing or renaming an existing alias.

All editions use the current Veyra connected **three-bar waveform**. Their AI
material edits use `design/launcher/wave_glass.png` as their reference, not the
older V-shaped branding. Names, kind, art direction and saved-generation provenance
are recorded in `catalog.json`. The individual checked-in 512 px WebP masters are
the real artwork used for Android exports, not preview-only concepts.

- [Actual icon contact sheet](previews/collection.png)
- [Offline gallery](previews/index.html)
- Rebuild density/adaptive resources with `python3 scripts/build_collection_icons.py`.

Theme editions appear in **App icon → Themes** and the All filter. Theme Studio
can opt into the matching bundled launcher. Asset editions appear in **App icon →
Asset packs**, All, and the installed asset-pack card's Matching app icon action.
Theme/asset application never silently replaces an independently selected icon.

Home uses the actual launcher selection with immediate updates after successful
changes and a background resync when the app resumes. It keeps the existing
wordmark size/layout. Root icon state supplies a single remembered, asynchronously decoded 256 px
resource painter, shared with the picker through a versioned cache. Home does not
repeatedly query PackageManager or decode theme brand art.

Each icon has five density exports, adaptive-icon XML and the same official
monochrome waveform. Android/OEM launcher masking and icon caches remain platform
behavior; no arbitrary component names or executable code are downloaded.
Bundled Android components require rebuilding the beta APK (build 35+).

Existing 62 themes and five asset ZIPs are unchanged. None needs a repack to gain
its corresponding selectable launcher. The beta source ZIP retains native build
resources and masters, omitting duplicate promotional media-kit image exports;
those originals remain in Git. No APK, key or signing backup is included.
