# Stable 0.2.2 promotion · code 59

Explicitly promoted at the owner's request from tested `preview/0.2.2-beta1` build 58 (`25e90637`). Main and `stable/0.2.2` become stable 0.2.2; the completed preview keeps its name, code, catalogs and original source archive. Historical stable/preview branches remain unchanged. Stable catalogs and bundled fallbacks route to main; runtime pack ZIPs remain byte-identical.

## Included improvements and review fixes

All beta search-focus, optional preparation, layer-only navigation animation, lazy Navigation Studio and bounded startup file/font improvements are included. See BETA-0.2.2-beta1.md for scope.

Review found that a failed saved-state read left PreparationRunner.checking true while also setting failed. The screen checks checking first, so it could remain blank without Retry/Continue. The failure path now clears checking; a regression exercises failed preference reading and successful retry. The navigation gallery now subtracts its 40 dp horizontal padding before choosing two columns, preserving usable card width on narrow phones and the existing large-text single-column behavior.

Build code 59 permits upgrades from beta 58 and stable 56. The beta suffix is removed. Existing Music/Veyra downloads, export, update validation, reduced motion and playback ownership remain.

## Source packaging

Retain stable 0.2.2 and previews 0.2.2-beta1, 0.2.1-beta1 and 0.2.0-beta1 at this branch tip. Stable 0.2.1 source remains in its historical branch/history. The packager chooses the smaller standard-DEFLATE level (1 or 9) for large source images. Extracted image bytes, source file modes and the runtime library format are unchanged; all member lengths/hashes and native inputs remain verified. No APKs or signing material enter Git.

## Validation

- Stable release Kotlin compilation/resource processing pass.
- Full Android release unit suite: 1,305 tests, zero failures/errors, one existing skip.
- Full shared UI JVM suite: 93 tests, zero failures/errors.
- The new unreadable-preferences regression verifies visible recovery and successful retry.
- Website production build and TypeScript checks pass. All 77 applicable cases pass (75 in the full run plus two focused accessibility reruns); five device-specific cases are skipped. The five-audit accessibility test has a per-test slow budget, retaining every assertion.
- Stable catalog/bundled copies match and all 72 theme/32 overlay routes target main. Source archive retention, member hashes, native inputs, original pack bytes and the 40-file backend mirror pass verification. The beta's signed release build passed: https://github.com/Bhavishyadalal/Veyra/actions/runs/41298494301. Stable has its own BuildConfig, catalogs and signed build.

No physical phone or emulator is attached. The owner uses a realme Narzo 60 5G, 90 Hz. Offscreen/local checks do not establish sustained device FPS, thermals, every keyboard implementation or installer behavior. Source promotion, signed Actions artifacts and published GitHub APK Releases are separate outcomes.

## Phone checks

Install stable 59 over stable 56 and beta 58 using the same certificate; confirm version 0.2.2 and preserved data. Open Search from every entry point, tap the field to type, switch tabs and confirm the query/results survive. Import an old keyboard preference. Observe first-build preparation, Continue, rotation, backgrounding and retry, including unavailable/full storage. Scroll all navigation designs on a narrow phone and at large text/RTL, then compare navigation and immersive playback at the same 90 Hz preference/effects. Confirm Music/Veyra downloads and export still open in another player.
