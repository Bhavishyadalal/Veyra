# Beta 0.1.9 · build 33 / independent asset packs

- Five Layer Lab overlays: Spray Parade, Paper Pop, Ink Rebel, Laser Club, Thread Riot.
- Apply fonts, icons, pressed skins, native controls/progress and motion over any
  selected theme or custom app style. Individual layer switches and one-step reset.
- Theme selection, wallpaper and palette remain intact; all 62 existing themes preserved.
- Licensed display fonts, readable Manrope body text and adaptive heading ink.
- Independent GitHub catalog, bounded checksum-verified atomic installs and offline selections.
- Requires rebuilt beta APK build 33; source ZIP and asset ZIPs are not APKs.
- Focused production compilation/install/layer verification; device/FPS checks pending.

# Veyra 0.1.9-beta1

## Punk Worlds · five full-color themes · build 32

- Riot Candy, Chrome Venom, Bubble Riot, Xerox Afterparty and Cosmic Vandal: original material-collage wallpapers, 113 colored functional glyphs, four player pressed-state variants, themed in-app branding and licensed display/body fonts. Each full theme has **123 asset roles**.
- Original-color glyph rendering preserves disabled alpha, error tint and visible native fallbacks. Player normal/pressed states preload together. Bright display headings, gel/riveted/sticker/paper/chrome frames, real-fraction progress treatments and finite page/press motion match each world.
- Requires a rebuilt **build-32 beta APK**. Theme Studio now lists 62 packs on the current beta; earlier immutable packs, stable branches and launcher aliases stay unchanged.
- Five AI direction boards are separate from the actual runtime asset/font sheets. Native transitions implement bounded scale/slide/fade; illustrated physical effects on concept boards are not claimed as running simulations. Independent asset-pack layering remains planned.
- Source ZIPs can restore optional non-build authoring fonts from checksummed included packs while preserving all Gradle font inputs and existing user edits.

[All five themes, actual assets, AI direction and compatibility](themes/PUNK_WORLDS.md)


## Advanced Worlds · five complete control themes · build 31

- Switchback Radio, Komorebi Hour, Rooftop Reverie, Apex Telemetry and Cinder Kata: original AI artwork, **119 asset roles per pack**, 113 functional glyphs, themed in-app brands and licensed fonts.
- Expanded semantic icon coverage across Home, Player, Settings, immersive playback, deep sheets and dialogs. Native control geometry, Settings switch/thumb glyphs, slider decorations, world-specific progress tracks and finite page-motion presets accompany each pack.
- Theme schema 4 requires **Android build 31**; install the rebuilt beta APK once. Older schemas keep their original bounds and immutable packs. Theme Studio now lists 57 packs on the current beta.
- Custom Home-style isolation, independent fonts, artwork overrides, accessible controls, real song artwork and service identities remain supported. Launcher aliases are unchanged. Browser previews are design studies, not device screenshots or FPS measurements.

[All five themes, screen studies, every asset and compatibility](themes/ADVANCED_WORLDS.md)


**Preview Android source · version code 30 · based on completed stable 0.1.8 / code 28**

## Five new illustrated worlds

- Lantern Letters, Velvet Orbit, Moss & Mechanism, Sugar Static and Tidal Origami each add original illustration, thirteen themed glyphs, an in-app brand badge and a distinctive heading font.
- Five complete nineteen-role asset galleries, an overview and browser component studies are included. Native sketch/playful presets are reused; no new engine rebuild is needed when build 30 is already installed.
- Beta Theme Studio grows to 52 packs. Stable catalogs, earlier immutable packs and launcher choices are unchanged.

[Previews, downloads and compatibility](themes/ILLUSTRATED_WORLDS.md)

## Margin Notes · first special sketch theme

- Original AI pencil backgrounds, 13 transparent hand-drawn glyphs, an in-app Veyra brand mark, Patrick Hand headings and Manrope body text: 19 mapped asset roles.
- Sketch-specific uneven Material/control shapes, pencil-hatched timeline, finite player-button tilt and paper-slide page transitions. Animation-off and subtle-motion settings keep their supported fallbacks.
- Requires refreshed beta **build code 30**. Code 29 and stable 0.1.8 cannot install this new preset; all previous packs retain their existing minimum versions. Launcher aliases are unchanged.
- Source ZIPs store duplicate runtime images once inside their included theme ZIPs, with checksummed restoration metadata. Fonts/licenses needed for app builds stay expanded. `python3 scripts/restore_theme_sources.py` restores optional editable images without overwriting existing files.

[Every asset and motion study](themes/previews/margin-notes/index.html) · [Theme notes](themes/MARGIN_NOTES.md)

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
