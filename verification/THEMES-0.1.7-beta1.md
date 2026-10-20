# Veyra 0.1.7-beta1 · Theme Studio verification

Current preview **0.1.7-beta1 / code 25**. Stable **0.1.6 / code 22** remains separate.
No APK assembly or Actions dispatch is part of this update.

## Current implementation

- Web Slinger v1: original midnight-city/crimson-glass artwork, Barlow Condensed
  headings, custom navigation/transport glyphs, angular action/control surfaces,
  web details, diamond playhead and themed Veyra wordmark/icon.
- Halloween Nocturne v3 retains forest/amber/gothic styling and adds matching Veyra
  branding and a bundled launcher icon. Published v1/v2 archives remain unchanged;
  installed legacy packs still load through schema-1 support.
- Schema 2 adds bounded native style/tagline fields and a validated 256-pixel
  branding slot. Minimum app code 24 gates new packs from older code-23 clients.
  Catalog entries retain the old schema, allowing older apps to show Update first.
- Studio has shared native artwork/collection panels, All/Downloaded filters,
  installed swatches, progress/cancel, explicit apply, offline packs and removal.
- Optional launcher choices extend the existing AppIcon enum, manifest aliases and
  picker. Only known theme IDs map to bundled aliases. A ZIP cannot name components.
  Theme application does not change the launcher icon automatically or kill playback.
- Launcher changes retain the existing atomic Android-13+ update and enable-first
  older-device path with rollback. Default remains the only shipped enabled alias.
  Theme activation and restoring Original do not overwrite a manual launcher choice.
- Branding uses the real shared Veyra layout with a native icon fallback, responsive
  wordmark sizing and a single accessible Veyra description.
- Artwork-derived palettes honor active theme colors. Real music artwork, service
  logos, original menu actions, offline/account rules, sleep-timer state, player
  locking and native interaction callbacks remain.

## Compact management and application refinement

- Studio now uses a lazy collection of 88 dp minimum theme rows. Details are a
  separate internal page; Back returns to the collection. Done stays fixed, and
  Download/Apply/Cancel stay below the detail scroll area.
- Appearance now has one App icon Settings row. The chooser groups all 19 choices
  into Veyra, Glass and Themes; each Glass family shows four variants. A coverage
  test proves every existing icon appears exactly once across these groups.
- Chooser uses a lazy adaptive-column grid with scrollable group headers and hints.
  Labels grow with font scale. Selection and PackageManager writes run on IO;
  synchronized reads/switches avoid a partial alias snapshot on rotation.
- Theme application defaults to a live Compose update and closes only Studio,
  preserving the existing screen state. Optional interface recreation is retained.
  Integrity checks, user confirmation, playback and saved base preferences remain.
- Recent successful catalog data is reused for ten minutes; concurrent refreshes
  coalesce. Explicit Refresh bypasses the time cache. Failed requests do not
  replace the offline catalog or extend its successful-refresh timestamp.
- Redundant post-install decoding/hash verification was removed. Atomic installation
  still validates before publication, and applying rechecks stored assets on IO.

## Earlier compact-Studio checks (code 24)

- Android release resources and Kotlin compilation via focused unit-test tasks;
  **24 JVM tests**, no failures: theme packs (13), launcher mapping/manifest/group coverage (3),
  player quality menu (4), Settings navigation (4).
- Both actual packs install with exact declared assets and outer/inner hashes.
  Tests cover corruption, traversal, extra entries, tampered assets, unsupported
  schema/app/scene requirements, unsafe paths, oversized streams, unreadable
  colors, unbounded shapes/taglines and decoder rejection preserving a previous
  installation. A legacy schema-1 pack validates in code 24.
- Launcher tests verify known mappings, unknown/null ID rejection, unique enum
  aliases, manifest entries and exactly one default-enabled launcher component.
  They do not execute Android PackageManager or a real launcher.
- Eleven Compose studies per theme: Home, large-text Home, player, Settings,
  large-text Settings, actions, large-text actions, Studio, large-text Studio, icon
  choices and large-text icon choices.
  Normal width 390 dp; enlarged layouts 320 dp / 150% text.
- Actual themed transport semantics deliver the callback once with at least a
  48 dp hit target. Studies use real shared branding, Home frames, Settings rows,
  timeline/transport and Studio panels inside illustrative compositions.
- Deterministic regeneration preserves current pack hashes. Changed bytes at an
  existing version are refused unless explicitly marked as an unpublished draft.
  Previously published v1/v2 ZIPs are preserved.
- Offline gallery checked over ordinary local HTTP at desktop/mobile sizes for
  complete images, page errors and horizontal overflow.
- Separate website build/type checks and desktop/mobile source/theme-link checks.
- Source ZIP per-file manifests, nested packs/checksums, archive cap and backend
  mirror verified before publication. Stable Android code remains unchanged.

## Rendering and integration limits

These images are **native component studies, not full Android window captures**.
Action rows, Studio surrounding content and navigation arrangement in the desktop
studies are illustrative. Sample covers are demonstration art; production uses the
listener's actual music artwork. No emulator/device session was available for the
full Android Studio sheet, PackageManager switching, launcher cache/shortcuts,
Android font loading, Activity recreation during playback or frame pacing. JVM
checks and desktop renders do not replace those integration tests.

On the next signed preview: upgrade from code 23; retain/apply a legacy pack;
download/apply both new packs; test Later, cancel/retry and offline restoration;
change both launcher icons and restore Default; inspect the existing icon picker;
check shortcuts/widget entry points; reopen/rotate/background; inspect Home, queue,
lyrics, actions and every Settings category; verify large text, RTL and reduced
motion. Music should continue through confirmed UI recreation.

Google/Spotify sign-in pages retain provider interfaces. Full atmosphere/fonts
remain downloadable; catalog and launcher artwork are bundled. Original native
glyphs remain available when a supported theme icon fails to draw.

These changes remove identified UI-thread work and eager list/image composition.
They are not a claim of zero dropped frames across devices. Real-device frame
profiling, live theme activation and launcher switching remain follow-up checks.

## Playtime Club update · code 25

- Schema 3 adds bounded playful scene and bubble timeline styles, requiring code 25.
  Schema 1/2 compatibility remains. Old clients cannot download this new pack.
- New generated fan artwork, original smiling waveform launcher, 13 original rounded
  glyphs, Fredoka headings and Manrope body text; each font license is bundled.
- Character credits appear in Studio details, README and the downloadable pack.
- Player controls use a finite damped spring only for this theme; reduced motion
  retains the static endpoint. No permanent animation timer is added.
- **16 focused JVM tests passed**: theme packs (13) and launcher coverage (3),
  with Android debug resource/Kotlin compilation and no APK assembly. Actual three-pack installation/compatibility/integrity suite
  and launcher mapping/manifest/group coverage. Existing pack hashes are unchanged.
- Seven new native studies: Home, Home at 320 dp / 150% text, player, Studio, Studio
  at 320 dp / 150% text, Settings and actions. The player study checks its native
  callback and 48 dp minimum target. Existing theme studies remain from code 24.
- No APK assembly, broad unrelated test suite, device launch or Actions dispatch.
