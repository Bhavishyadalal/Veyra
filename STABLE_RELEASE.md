# Veyra 0.1.1 stable source update

Based on the supplied 0.1.0 Stable source. Version code 14, version name 0.1.1, no beta suffix. JioSaavn defaults/migration and player polish are updated; application identity and signing configuration are unchanged.

50 focused Kotlin tests pass. Android compilation/full unit tests are blocked by JitPack HTTP 403 before Kotlin compilation. No APK was built, signed or supplied for this update. Device verification is still required. See [verification/STABLE-0.1.1-RESULTS.md](verification/STABLE-0.1.1-RESULTS.md).

The report below was supplied with the earlier 0.1.0 ZIP and describes that revision only; it is not validation of 0.1.1.

---

## Supplied historical report: Veyra 0.1.0 stable-labelled release

Based on the uploaded beta12 source. App functionality, UI, code and resources are unchanged.

- Version name: 0.1.0 (beta suffix removed).
- Version code: 13 (increased from beta12 code 12).
- ARM64 ProdRelease APK built and signed with the same test signing identity as the preceding delivered APKs. Keep your own signing key when rebuilding locally. Private keys are not included.
- Release build and release vital lint passed.
- Unit tests: 1,119 passed, one skipped, zero failures/errors.
- APK signature, package version and archive integrity verified.

Extract the ZIP and open the Veyra folder in Android Studio, or select the ZIP in your Veyra Builder. Use JDK 17; see START_HERE.md for SDK requirements.

The stable label is a release designation. No additional phone testing was performed for this metadata-only release.
