# Stable 0.2.1 promotion · code 56

The owner explicitly requested promotion to `main` and `stable/0.2.1`. Based on completed `preview/0.2.1-beta1` (`de41cee2`, code 55), with the picker/storage fixes below. The completed preview retains its suffix, catalogs and original source ZIP. Previous stable branches/history remain untouched.

## Reported problems and fixes

Opening the top-right immersive style button mounted twelve illustrated cards in an eager scroll Column and used intrinsic row measurement while live scene motion continued. Picking a style rebuilt the scene before the sheet disappeared. The gallery now uses keyed lazy rows, no intrinsic measurement, fixed-size lightweight preview slots during entry, illustrated previews only after expansion, and pauses scene/Canvas/lyric motion underneath. A guarded dismissal coroutine runs the selected action only after the sheet is hidden; cancellation unlocks retry. Mode titles, selection semantics, pinned Close, classic customization and all twelve choices remain reachable through scrolling.

An old `export_downloads=false` preference could survive the earlier one-time migration, including through backup import. Shared saving was still optional and DownloadStore still contained a private write branch. The preference is now retired, removed on settings reload and exposed only through an immutable shared-destination state. The switch is replaced by a truthful destination label. DownloadStore only creates shared Music/Veyra destinations; preparation always chooses portable rendition/container behavior. Existing app-private originals are not deleted or silently reclassified: Export all downloads copies them to the shared folder. Android 8/9 still asks for shared-storage permission; Android 10+ uses MediaStore without broad filesystem access.

## Broader checks and follow-up fixes

The full suite exposed three stale filename expectations from before recording identity suffixes, plus sixteen locale-completeness failures. Filename tests now retain their sanitization/order/extension checks with the collision-safe suffix. Locale XML is declared as a Gradle unit-test input, so resource-only edits cannot silently reuse stale completeness results. The existing full gallery test now scrolls the lazy list and selects every style on phone, wide and large-text RTL layouts instead of requiring every offscreen poster to be mounted at once. A new opening test verifies that only a subset of posters is mounted.

Missing UI resources (270 keys per offered non-English locale) are completed with machine-assisted Google translations of public UI text. Branded collection/style names are kept verbatim, indexed format-argument sets are preserved, XML compiles, and arrays retain order/length. Traditional Chinese script/region aliases share the completed traditional strings. This is not a human translation-quality certification. No runtime translation service or new app dependency was added.

## Verification

- Release Kotlin compilation and resource processing pass.
- Full Android release unit suite: 1,296 tests, zero failures/errors, one existing skipped test.
- Full shared UI JVM suite: 92 tests, zero failures/errors. Includes actual lazy-picker reachability at phone/wide/large RTL sizes, cached scene rendering, motion lifetimes, lyrics, controls, settings/theme layout and existing rendering checks.
- Website: production build, TypeScript check and four desktop/mobile page-load/download-link checks pass. Stable points to 0.2.1; preview links remain on the frozen beta.
- Stable catalogs and bundled fallbacks are routed to `main`. Published theme/overlay ZIPs are unchanged. Generated offscreen study captures are restored after testing; renderers/tests remain in source.
- Source archive retention, checksums, native build inputs and backend provenance are checked with `scripts/verify_archives.py` before push. Only current stable and the newest three preview ZIPs remain at this branch tip. Prior stable source remains on its frozen branch/history.

No Android emulator/physical device was attached. Installer permission behavior, actual MediaStore output on a phone, FPS, thermal behavior and battery use remain device checks. Signed APK assembly is verified by the CI run below. APK publication and live website deployment are separate from Git push.

## Device checklist

Open/close the immersive picker repeatedly during timed lyrics/Canvas playback; select and reach all twelve styles, including large text/RTL/landscape; interrupt dismissal with a drag and retry. Confirm playback continues while visual motion pauses behind the sheet. Upgrade with old private preferences, import an old backup, download fresh audio and open it in another player from Internal storage → Music → Veyra. Export existing private audio, retry and cancel, test full storage, and check legacy permission denial/grant. Install stable build 56 over beta 55 using the same signing identity.

## CI recovery

The signed workflow run 41214044225 failed before compilation while resolving standard Maven Central artifacts; Check run 41211872335 requested the retired SDK package `tools`. Build repositories now also use Sonatype's alternate Central endpoint (`repo1.maven.org`), with unchanged dependency versions. CI retries dependency transport/resolution failures once with refreshed metadata; compiler/build errors retain their exit status. Both SDK setup paths request platform-tools, and a release upload fails if no APK exists. Runtime version/code are unchanged. Four isolated retry tests and refreshed Gradle configuration pass. Signed runner verification passed, including APK upload: https://github.com/Bhavishyadalal/Veyra/actions/runs/41227117260.
