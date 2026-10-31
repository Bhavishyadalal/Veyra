# Veyra 0.2.1-beta1 · build 54

Testing preview on `preview/0.2.1-beta1`. Stable stays 0.2.0 / code 53.

- New downloads automatically use **internal shared storage → Music → Veyra**. Android creates the folder on the first successful download. No broad file-access permission is needed on Android 10+. The existing private-storage switch remains available.
- **Export all downloads** in Settings → Downloads and the Downloads sheet copies existing private audio into Music/Veyra. Progress, cancellation, completed/previously shared/failed counts and safe retry work across screen dismissal. Original downloads are preserved; export requires additional free space.
- Supported offline FLAC/M4A packages become ordinary audio files, with available lyrics/artwork embedded. Older WebM/Opus downloads are remuxed to Ogg without re-encoding on Android 10+ (Android 8/9 copies the original WebM). Unsupported formats/devices report an error and keep their originals.
- Exact-folder duplicate checks ignore unfinished/empty MediaStore rows. Recording-specific filenames prevent different songs with matching display names from adopting one another. Unicode names stay under filesystem byte limits.
- Byte progress updates are bounded to eight publications per second, avoid NaN for unknown lengths and animate smoothly while respecting reduced motion. Free space works before the Music folder exists. Data-sync services stop on Android's foreground-service timeout.

The folder is managed automatically; this beta does not add a custom-folder picker. Export copies do not replace Veyra's original download records. Deleting an in-app original does not delete its exported copy. Existing public downloads are counted as already shared.

[Verification and device test checklist](verification/DOWNLOADS-0.2.1-beta1.md). Source ZIPs are Android Studio projects, not APKs. This preview does not establish device performance, universal codec support or completion of the entire long-term roadmap.
