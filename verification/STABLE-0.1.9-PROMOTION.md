# Stable 0.1.9 promotion

Owner-authorized promotion from completed preview `cc1da14c0ebb14af31335bd1f5c657855037b19d`. Stable version is **0.1.9 / code 36**, with an empty beta suffix. `main` and `stable/0.1.9` receive the same release commit; completed preview remains code 35 and historical stable branches remain unchanged.

## Release fixes

- Rewrite both remote and bundled catalogs to the stable main root. Strict repository URL validation remains unchanged. Stable authoring tools also emit main URLs.
- Serialize theme startup cleanup, install/index publication, activation and removal with one coroutine mutation lock. Asset installs publish their index inside their existing mutation lock. Saved theme manifests use the same 64 KiB bounded read as incoming manifests.
- Preserve every theme/asset ZIP. Rotate only the current stable source archive, retaining the three preview archives and their exact bytes.
- Update release notes, Android Studio/Colab entry points, source retention verification and website stable source/checksum/note links.

## Focused verification

See the final recorded results below. These checks cover production compilation, release identity, stable catalog routing/integrity, installed pack/layer/icon invariants, retained source packages and website links/layout. No broad device suite, signed APK assembly/publication, backend deployment, live Vercel deployment or real-device frame-rate claim is made.

### Recorded results

- Production release Kotlin and Java compilation passed; debug/release generated BuildConfig both report version 0.1.9 / code 36. Removed the three compiler warnings encountered in the reviewed repository/artist response code.
- `CheckStableCatalogs.java` exercised both production repository parsers: 62 themes and five asset packs accepted with identical bundled fallbacks; beta-root catalogs rejected on stable.
- All 67 catalog ZIP sizes/hashes, preview files and minimum app codes checked. Published packs and all three preview archives match completed beta bytes.
- Five production asset installers passed checksum/exact-member/font/image/role/preset/version checks; 310 theme/asset combinations preserved theme identity/artwork/palette and layer isolation.
- 95 enum/manifest launcher choices, all 62 theme and five asset bindings, picker filters and default uniqueness checked. All 21 new masters and 210 density exports passed resource checks.
- Website production build and TypeScript checks passed. Chromium at 390px and 1366px verified stable ZIP/checksum/notes, retained preview, Layer Lab links, viewport bounds and no page errors.
- Source ZIP CRC, per-file hashes, pack mappings, backend snapshot and four-archive retention are checked by `scripts/package_source.py` and `scripts/verify_archives.py` before publication.

Device install/playback, sensor behavior, visual smoothness and a full lint suite remain unverified for this promotion. No signed APK was produced.
