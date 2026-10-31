# Immersive rendering, updates and interaction fixes · build 55

Preview: `preview/0.2.1-beta1`, version 0.2.1-beta1 / code 55. Stable remains 0.2.0 / code 53.

## Immersive rendering

- Expanded backdrops record their gradients, paths, stars and tunnel geometry once, then animate cached layer transforms. The scene phase no longer invalidates full-screen backdrop drawing every frame. Vinyl/paper backdrops do not subscribe to that phase at all.
- Lyric focus uses a per-document sorted timestamp/interval index. It preserves multiple overlapping voices, duplicate timestamps, reverse seeks and the existing fallback for unordered provider data. Normal documents avoid repeated scans over every earlier lyric on each frame. Empty/untimed documents do not run the lyric frame clock.
- Immersive lyric depth uses contrast and alpha instead of per-row blur/glow render targets. Classic artwork is bounded to a 960-pixel request and uses a direct gradient fade, removing its full-screen offscreen alpha-mask buffer. Normal player effect preferences are preserved.
- Existing lifecycle, pause and reduced-motion gating remains. Animation speed and playback ownership are unchanged. These are concrete rendering reductions, not a measured device FPS claim.

## App updates

Settings → Advanced → App updates provides manual checking and opt-in automatic downloads. Checks use published stable GitHub Releases; a source-branch push is not an APK release. The canonical Git checkout configures the official feed by default; forks/extracted projects need `VEYRA_UPDATE_FEED_URL` explicitly configured in local.properties/environment.

Automatic transfer starts on an unmetered connection while Veyra is foreground, including when network availability arrives after launch. A foreground data-sync service owns the transfer and stops automatic transfers when the connection becomes metered/unavailable. Manual transfers remain available. Cancellation cancels the network call and removes the partial file. Completed APKs are retained across launches and revalidated. Android's installer still requires user approval; returning from the per-app installation-source permission screen resumes the requested installer action.

Asset selection prefers an explicit universal APK, then a legacy generic APK, then a supported ABI split; hyphenated and underscored names both work. It rejects debug/dev/TV/Wear artifacts and distinguishes x86 from x86_64. Downloads require HTTPS, cap transfer size at 512 MiB, check content length, check the GitHub SHA-256 digest when supplied, then verify APK package, a higher build code and the installed signing certificate/valid rotation history. Invalid files never reach Ready.

The live stable release inspected during development was v0.2.0, with arm64 and universal release assets. This preview correctly remains ahead of that release and must not offer a downgrade. A future stable APK must have a build code greater than 55 and the correct signing identity to update this beta installation.

## Notification, Settings and welcome

The shared notification small-icon resource now uses the canonical three-bar waveform silhouette. Android tints notification small icons; launcher glass colors are not a supported small-icon format. Playback, downloads, export and APK transfer use the shared resource.

Quality, download-quality and automix selectors share an expanded sheet that completes dismissal before applying the selected policy. Repeated taps are guarded; interrupted dismissal permits retry. Category entry begins at its correct initial alpha/offset instead of flashing the replacement content before the animation resets. Folder selection saves its URI only after persistent access succeeds. App updates appears in the Advanced overview/search.

Welcome completion is guarded against duplicate sign-in/Skip/Next callbacks. Outgoing-page Next events are ignored. Pasted control characters become spaces, and the name limit preserves whole Unicode code points. Existing keyboard, responsive scrolling, RTL, accessibility and reduced-motion behavior remains.

## Verification

- `:app:compileProdReleaseKotlin`: passed on the final source.
- `:app:testProdReleaseUnitTest` filtered to update-asset/version, welcome and downloads tests: 45 passed, zero failures/errors.
- `:sharedUi:jvmTest` filtered to immersive lifecycle/motion and lyric focus/index: 14 passed. A further focused cached-backdrop render test passed, verifying that changing the production scene layer transform does not re-record its vector drawing (60 focused tests in total).
- Website: production build, TypeScript check and four desktop/mobile page-load/external-link tests passed.
- Source packaging is checked with `scripts/verify_archives.py`, including SHA-256, retained archive set, native inputs and mirrored backend provenance. Catalog routing and published pack bytes are unchanged in this build.

 Physical-device validation is still required: compare every immersive scene with playback/lyrics active, check scrolling and lyric sync under load, inspect notification glyphs, exercise Settings Back/drag/double-tap behavior, and test upgrade/cancel/network changes/process death/permission denial on signed APKs. No emulator or physical Android device was attached. Local/offscreen tests do not prove phone FPS, device installer behavior, full APK assembly or a live website deployment.
