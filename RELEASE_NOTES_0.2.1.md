# Veyra 0.2.1 · stable build 56

Promoted to `main` and `stable/0.2.1` at the owner's request. Completed `preview/0.2.1-beta1` stays frozen at build 55.

- **Immersive mode picker:** visible rows load lazily, without intrinsic row remeasurement. Illustrated previews wait until the sheet is expanded. Scene motion, Canvas playback and the lyric frame clock pause while the picker is open. Style/source/timing actions run after dismissal, with repeated taps guarded and interrupted dismissal recoverable.
- **Download destination:** every new audio download uses **Internal storage → Music → Veyra**. The old private-storage preference is retired and ignored even after restoring a backup. DownloadStore has no private-write branch. Unsupported portable manifests fall back to a portable rendition rather than an app-private package. Existing private originals are preserved; use **Export all downloads** once to copy them into Music/Veyra.
- Completed missing Android UI keys across all sixteen offered non-English languages, including download/update controls, Settings, welcome and search. Translations are machine-assisted; brand/style names and indexed format arguments are preserved. Traditional Chinese script/region aliases also receive the completed strings. Human language review remains useful.
- Retains build 55's cached immersive backdrops, indexed overlapping-lyric focus, current waveform notification icon, optional automatic APK downloads with validation/user-approved installation and Settings/welcome fixes. All 12 immersive modes, 72 themes, 32 overlays and 132 launcher choices remain.

[Promotion checks and device-testing limits](verification/STABLE-0.2.1-PROMOTION.md). Source ZIPs are Android Studio projects, not APKs. This source promotion does not publish an installable APK or establish device FPS.
