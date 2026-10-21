# Keepsake · Beta 0.1.8-beta1 verification

## Delivered scope

- Preview version **0.1.8-beta1 / code 27**, based on stable 0.1.7 with its theme-install correction.
- Five original AI scenes and five AI-edited Glass Noir waveform designs, reviewed before asset compilation. Scene masters are retained in the execution workspace; compact scene and icon WebP files are checked in. No user reference image is redistributed.
- Five data-only packs target **stable code 26**, use existing schema 2/3 capabilities and 256×256 brand images. All earlier published pack/sidecar bytes remain unchanged. Catalog grows from 41 to 46 within its existing 64-entry / 65,536-byte limits.
- Five beta-only launcher mappings, 50 density resources, five adaptive resources and five monochrome waveform resources. Beta has 74 choices; stable keeps 69. Matching-icon application stays explicit and optional.
- Beta-only theme name/description search, per-collection icon search with empty-state/clear controls, current-icon feedback, landscape Studio thumbnails and larger selected-icon previews. Natural-height text, one accessible selection target, detail Back state, existing download/apply/remove/cancel actions and collection filters are preserved.

## Focused checks

| Check | Actual result |
| :--- | :--- |
| Android release Kotlin compilation | Passed; no APK assembly or signing |
| ThemePackTest | 17 passed: all 46 packs, checksum/path/schema validation, image bounds and explicit code-26 installation of the five new packs |
| ThemeLauncherTest | 4 passed: mappings, aliases/default state, all existing groups and 74 total choices |
| New-theme native studies | Five Home, five player and five RTL navigation studies rendered; each player callback fired once with a target at least 48 dp; each navigation study retained 16 tab targets, four selected states and one callback |
| Shared Studio/icon studies | Studio, 320 dp / 150% text icon picker and RTL counterpart rendered; five targets at least 48 dp and one callback each |
| Visual inspection | Reviewed the five generated scenes, icon sheet, representative Home/player/navigation studies and the changed Studio/picker components. Shortened the icon-search label after large-text review and rerendered only the affected three UI studies |
| Website | Production build and TypeScript passed; 2 focused desktop/mobile FAQ/download checks passed, including the five stable theme links and updated preview source/notes |

Android checks used the existing JDK 17 / SDK, cached dependencies, offline mode, two workers and in-process Kotlin compilation. Existing warnings remain. No dependency was added. Native studies use actual shared components in illustrative layouts; they are **not Android screenshots** and do not validate the complete Activity, keyboard, system Back or device launcher.

## Delivery and preservation

Stable changes are limited to theme/catalog data, documentation, tests and source packaging. Its Android Kotlin, shared UI, launcher manifest/resources, version 0.1.7 and code 26 are unchanged from `becb3f02eee0c44b1e07ba4c08715224740109bf`. Historical stable/preview branches remain unchanged.

Source is staged before packaging. Current catalog theme ZIPs and the legacy v2 test fixture remain inside source downloads; superseded pack ZIP copies stay available in Git rather than being duplicated in every source archive. All extracted theme assets, fonts, licenses and pack compiler sources remain included. This keeps the downloads under GitHub's 100 MiB per-file limit. Retention keeps stable plus preview 0.1.8, 0.1.7 and 0.1.6 ZIPs; 0.1.5 remains available on its historical preview branch.

`scripts/verify_archives.py` verifies source manifests, sidecars, backend provenance and nested theme hashes. App/website pushes use skip-CI commit messages; no Actions dispatch or APK build is authorized by this change.

## Limits

The stable-compatibility check exercises archive validation and PNG/VP8 image-header bounds with code 26; it does not execute Android BitmapFactory/Typeface decoding. Real-device download → Apply → restart/offline use, TalkBack, keyboard layouts, launcher masks/switching and playback remain unverified. Existing translation/lint backlog remains; new UI strings use English fallback where translations are absent. No performance benchmark, APK publication or Vercel deployment is claimed.

[Collection and downloads](../themes/KEEPSAKE.md) · [Stable compatibility](KEEPSAKE-STABLE-0.1.7.md)
