# Stable 0.1.7 · source promotion

**21 October 2026 · version 0.1.7 / code 26 · `main` and `stable/0.1.7`**

Promoted at the owner's explicit request from preview commit `466433e6e8f29666af1cfb012b14b4a8c77b7ad7`. A normal merge preserves the existing `main` and preview histories. The only merge conflicts were current-status text in `AGENTS.md` and `README.md`; they were resolved to describe the stable promotion and preserved historical branches.

## Promotion changes

- Root version code changes from 25 to **26**; version name remains **0.1.7** and the beta suffix becomes empty for debug and release variants.
- Stable ThemeRepository and both bundled/public catalogs use `refs/heads/main/themes/`. Theme-builder URL defaults and the gallery's current-version text match stable. Published ZIP bytes, artwork, fonts, licenses and all 69 launcher choices are unchanged.
- Release notes, Android Studio opening instructions, source index, source/checksum links, builder default URL and website stable labels now describe 0.1.7. Historical preview links remain available and explicitly archived.
- A new `Veyra_Android_Source_0.1.7_Stable.zip` replaces 0.1.6 in the current four-archive set. The three retained preview archives keep their exact bytes. The old 0.1.6 archive is available by immutable historical URL and remains in Git history; no tags or release assets are removed.
- Historical stable/preview Android source remains unchanged. Maintenance-only commits update archive files, retention verification, current-status rules and affected historical download/tool URLs.

[Stable release notes](../RELEASE_NOTES_0.1.7.md) · [Source downloads](../downloads/README.md)

## Actual checks

| Check | Result |
| --- | --- |
| Production-debug Android resources/Kotlin/Java and focused tests | Passed |
| Production-release Kotlin compilation (`:app:compileProdReleaseKotlin`) | Passed; no APK packaging/signing task |
| Generated prod/debug and prod/release BuildConfig | Both `VERSION_NAME = "0.1.7"`, `VERSION_CODE = 26`; no beta suffix |
| ThemePackTest | 15 passed; all 41 catalog entries installed against the actual stable BuildConfig version code |
| ThemeLauncherTest | 4 passed; all 69 choices and fixed theme mappings preserved |
| Website type checking and production build | Passed |
| Website focused desktop/mobile link checks | 2 passed, including new stable notes, source/checksum and theme links |
| Theme/launcher/asset preservation | Stable app differs from the promoted preview only in root version metadata, catalog URL routing and the current-version test argument; theme packs and assets retain exact bytes |

The Gradle invocation used JDK 17, the local Android SDK, cached dependencies, `--offline --no-daemon --max-workers=2` and in-process Kotlin compilation. Existing nonfatal Android analytics, experimental-lint and compiler/deprecation warnings were emitted. No dependency or toolchain versions were changed.

```sh
./gradlew --offline --no-daemon --max-workers=2 \
  :app:testProdDebugUnitTest \
  --tests app.veyra.music.ThemePackTest \
  --tests app.veyra.music.ThemeLauncherTest \
  :app:compileProdReleaseKotlin
```

Source is staged before packaging with `scripts/package_source.py`; `scripts/verify_archives.py` checks all four retained archives and nested pack hashes. Public branch heads, stable ZIP/sidecars, catalog destinations and affected website links are verified after push. Stable/main share the same promotion commit. These delivery checks are distinct from Android device validation.

## Carried verification limits

The previous preview submission pass compiled Android source and passed 68 focused tests. Those tests and native studies were **not rerun wholesale** for this metadata/catalog promotion. There are no new layout changes or native renders.

The preserved lint inventory still reports **109 missing-translation errors, 700 warnings and 5 hints**. Missing locale entries fall back to English. This source promotion does not claim lint passed, translations were completed, or every warning was resolved; lint was not rerun because this promotion does not address that backlog. No baseline or rule suppression was added.

Real-device playback, network handovers, TalkBack, launcher switching/masking and foreground-service lifecycle remain unverified. Full APK assembly, R8/native packaging, signing and installation were not run. No GitHub Actions dispatch, APK build, GitHub release/tag publication, backend deployment or verified Vercel deployment is part of this promotion. An installed update still needs the same signing key and package identity.

[Previous submission review](SUBMISSION-REVIEW-0.1.7-beta1.md) · [Lint inventory](submission-lint-summary.json)
