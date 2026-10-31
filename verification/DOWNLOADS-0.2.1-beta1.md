# Download portability · 0.2.1-beta1 / code 54

## Changes

Default shared Music/Veyra storage is migrated once on upgrade; later explicit private/shared choices persist. Bulk export streams one file at a time in a dedicated non-exported foreground data-sync service. Copy buffers are 128 KiB; package fragments are bounded to 32 MiB and checked against local path traversal. No source audio is deleted. New destinations remain pending until complete; cancellation/failure aborts them. Retry skips already completed exact-folder filenames. Output filenames include a stable 12-hex recording identity to distinguish matching titles/artists.

Offline FLAC and supported MP4 audio sample entries assemble through the existing ManifestFile code; sidecar lyrics/artwork are embedded where supported. WebM Opus remux uses Android MediaExtractor/MediaMuxer on API 29+; Android 8/9 copies WebM unchanged. Other unsupported packages fail explicitly. New public downloads continue through the existing portable rendition selection, including AAC fallback where necessary.

Progress callbacks check coroutine cancellation on every invocation, publish no more than eight times per second, clamp valid ranges and always publish completion. The per-row and export progress bars use finite animation with reduced-motion support. Legacy shared writes request storage permission at export and retry entry points. A prepared download snapshots its destination choice alongside its rendition, so a settings change during resolution cannot send a private WebM route into MediaStore.Audio. Data-sync timeout handling covers both transfer services.

## Automated verification

- `:app:compileProdReleaseKotlin`: passed with existing warnings.
- `:app:testProdReleaseUnitTest --tests app.veyra.music.download.*`: 30 tests across 10 suites, zero failures/errors. Includes filename collision/Unicode/path tests, package codec/path/missing-fragment tests, progress bounds/throttling and existing manifest/queue/concurrency tests.
- `python3 scripts/verify_backend_mirror.py`: 40 mirrored files verified.
- Catalogs and bundled fallbacks routed to `preview/0.2.1-beta1`; no published pack bytes changed.
- Source archive retention/checksums/content: `python3 scripts/verify_archives.py`.
- Companion website: production build and TypeScript check passed; desktop/mobile page-load checks passed (2), desktop/mobile external-download-link checks passed (2). Stale version expectations and the theme-group selector in that existing test were corrected.
- No Android device/emulator was attached. Full APK assembly/signing, device MediaStore/Opus behavior, real-device animations/FPS, full lint and live website deployment are not established by these checks.

## Required physical-device testing before stable promotion

1. Upgrade a stable installation containing private FLAC, AAC, Opus and an offline package. Confirm the new default and later opt-out persistence across restart.
2. Export from Settings and from Downloads; verify files in Files → Internal storage → Music → Veyra and open/seek them in another player. Check FLAC/M4A tags, lyrics, cover and Opus duration/seeking.
3. Retry: completed copies should count as already shared. Check different recordings sharing artist/title, long emoji filenames and existing empty/pending rows.
4. Cancel during a large track and close/reopen the sheet; no partial public file should remain. Background the app and confirm notification cancellation. Test process termination/retry and Android data-sync timeout.
5. Test full storage, deleted originals, Android 8/9 permission denial/grant and Android 10+ scoped storage. Unsupported packages must fail visibly without touching originals.
6. Download an album with four workers while playing music. Verify shared outputs, playback continuity, scrolling, progress motion, large text and reduced motion on a real device. No device FPS or glitch-free claim is made by local compilation/tests.

Stable/main and prior previews remain unchanged. Catalog URLs and bundled fallbacks target only this preview branch; published data-pack bytes are unchanged.
