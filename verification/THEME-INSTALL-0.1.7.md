# Stable 0.1.7 · Theme installation correction

## Report and cause

Downloading a theme reached verification, then returned to Download theme; Apply never appeared and the Downloaded collection stayed empty. The runtime decoder validation allowed only 256×256 images for every non-scene image. Thirty-six of the forty-one current theme packs publish 512×512 `brandIcon` artwork. Those valid packs therefore failed installation before the repository published installed state. Earlier JVM pack tests checked archive integrity without exercising image-dimension policy.

The public `main` catalog and an affected Azure Bloom ZIP were fetched and matched local bytes, byte count and catalog SHA-256. This failure was reproduced from the published asset dimensions; it was not an Android device reproduction.

## Correction

- Share a bounded image-size policy between runtime decoder validation and regression checks: scene artwork 2048 px, brand artwork 512 px, functional glyphs 256 px per dimension. Zero, negative and oversized dimensions remain rejected.
- Exercise that policy against the actual PNG/VP8 headers of all 697 referenced image assets across all 41 published packs during pack-install tests. Boundary tests cover accepted brand artwork and rejected oversized brand, control and scene images.
- Keep install success/failure feedback beside the fixed Download/Apply action area. Failure wording now identifies installation rather than incorrectly suggesting application was attempted.
- Preserve archive/path/hash checks, existing theme ZIP bytes, fonts, launcher resources, optional launcher selection, playback and account functionality.

Version remains **0.1.7 / code 26**. The correction is carried into `preview/0.1.8-beta1`, whose version metadata has not yet been bumped. Historical preview/stable Android branches are unchanged.

## Focused verification · 21 October 2026

- `:app:testProdDebugUnitTest --tests app.veyra.music.ThemePackTest`: passed, including the published-pack image-policy regression and boundary checks.
- `:app:compileProdReleaseKotlin`: passed. The corrected production code also compiled for the debug test variant.
- Website production build and TypeScript checks: passed.
- Website FAQ/download-link Playwright checks: 2 passed, desktop and mobile; the stable verification link now points here.

Android checks used JDK 17, the existing Android SDK, cached dependencies, offline mode, two workers and in-process Kotlin compilation. Existing nonfatal Android analytics, experimental lint and compiler warnings were emitted. No new dependency was added.

Source is staged before packaging. `scripts/package_source.py` refreshes the stable ZIP and checksum; `scripts/verify_archives.py` verifies the retained archives and nested pack integrity. Public branch/source checks are delivery checks, separate from Android behavior testing.

## Installed-app update and remaining limits

**An existing APK needs to be rebuilt and installed to receive this code correction.** Downloaded theme data cannot change its validator. After updating with the same package/signing identity, retry the download; a successful install should offer Apply and appear under Downloaded. Applying still requires the user's explicit choice.

No APK assembly/signing, Actions dispatch, release/tag publication or device installation was performed. Header checks use the production size policy but do not run Android BitmapFactory/Typeface decoding. Full on-device download → Apply → restart/offline persistence, small-screen/large-text layout and launcher behavior remain unverified. No UI screenshot or deployment success is claimed.

The existing translation/lint backlog and other device-testing limitations from the [stable promotion report](STABLE-0.1.7-PROMOTION.md) remain; unrelated checks were not repeated.
