# Beta 0.2.2-beta1 · code 57

Created from stable 0.2.1 (`ece8f2b7`, code 56), at the owner's explicit choice. Application behavior is unchanged from that stable revision: the lazy immersive picker, portable shared downloads, localization and update fixes are included. Track metadata, catalog routes and archive retention differ. The verified stable CI recovery is also merged; see STABLE-0.2.1-PROMOTION.md. Completed 0.2.1-beta1 and 0.2.0-beta1 previews remain unchanged.

Stable validation is recorded in STABLE-0.2.1-PROMOTION.md: 1,296 Android tests (one existing skip) and 92 shared UI tests, zero failures/errors. This beta is compiled with its own BuildConfig and its download/update/catalog regressions are rerun before push. The packaged source/checksums are verified by scripts/verify_archives.py. No Android device was attached; source push does not publish an APK or prove physical performance/installer behavior.
