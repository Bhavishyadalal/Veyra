# Veyra 0.1.9-beta1

**Preview Android source · version code 29 · based on completed stable 0.1.8 / code 28**

## Listening and personalization update

- **Playback:** delayed error recovery checks the failed player, item and error before discarding bytes, seeking or skipping. Reconnecting reads the actual player error; disconnected sessions clear stale UI state.
- **Theme artwork controls:** tune background visibility, extra dimming, softness on Android 12+, and themed navigation/player control opacity. Settings save when a slider gesture finishes; Restore defaults resets only these overrides. Downloaded pack files and custom app styles stay separate.
- **Queue:** upcoming song/time summary, explicit confirmation before clearing added songs, existing safe Undo, accessibility actions for moving songs within a section, and a Now playing shortcut. Track changes preserve deliberate queue browsing.
- **Home:** choose featured or compact cards, hide the sign-in card, and choose 3/6/9/12 speed dial entries. Speed dial follows the personalized section order and stops scanning once enough items are found. Existing section ordering/visibility, greeting and pinned header controls remain available.
- **Search:** source selector stays available with a typed query; failed first pages and continuations offer Retry, existing rows remain visible after a continuation failure, repeated pages cannot request-loop, and empty results offer category reset or query clearing. Category headings use string resources.

[Update verification and device-check limits](verification/UX-0.1.9-beta1.md)

## Initial preview checkpoint

- Starts from stable commit `6e37aa4615bad01b895dc547454f79218d94175e`, including the saved Pin Home header setting, theme-matched listening cards and independent scroll-player animation toggle.
- Version name becomes **0.1.9-beta1** for production debug/release variants; version code increases to **29**.
- App behavior, theme engine, theme packs, launcher choices and backend are unchanged at initialization. The subsequent update is described above.
- Stable `main` and `stable/0.1.8` remain **0.1.8 / code 28**. Historical preview branches remain separate.

[Initialization verification](verification/PREVIEW-START-0.1.9-beta1.md) · [Stable feature baseline](RELEASE_NOTES_0.1.8.md) · [Source downloads](downloads/README.md)

Source ZIPs are Android Studio projects, not APKs. APK build/signing/publication are separate steps. Back up app data before switching release tracks; installing a higher-code preview may require uninstalling it to return to an older-code stable APK.
