# Stable 0.2.0 · twelve immersive worlds

**Build 50** adds six modes alongside the original six. Enter Immersive from
the player, then tap the mode pill at the top right. **New worlds** appears
first; **Original worlds** remains below it. Selection uses the existing saved
experience preference and never changes the selected theme or asset pack.

![Actual production-UI renders of the six new modes](../themes/previews/immersive-worlds/expanded-six.webp)

| Mode | Scene | Motion and function |
| --- | --- | --- |
| Aurora Drift | Original AI northern lights above an alpine lake; a framed sleeve and luminous lyrics | Gentle sleeve drift and sparse stars; existing synchronized/seekable lyrics |
| Vinyl Lounge | A grooved record, artwork label, stationary tone arm and warm listening-room light | The record turns only during active playback; sleeve and metadata come from the playing song |
| Neon Tunnel | Pink/violet portals over a perspective midnight grid | Slow grid travel and a tilted sleeve; existing synchronized lyrics |
| Paper Reverie | Warm ruled paper, taped album sleeve and a serif editorial title | Deliberately still, with the shared transport/seek controls |
| Orbital Atlas | A circular album planet, moon and star atlas | Slow moon orbit; the ring reflects actual playback position, including seeks |
| Prism Bloom | Eight colored glass-like petals around a sculpted album sleeve | Slow petal movement and synchronized lyrics |

Each new deck has a matching dark material and accent. The existing six modes,
Classic options, foreground-icon fix, auto-hide/cancelled-seek fix and build-49
reliability changes remain. Lyrics source/timing actions appear only for modes
that display lyrics. All twelve retain exit, previous/play/next and seeking through
the original playback callbacks; none creates a second audio player.

## Rendering and resources

- One bounded scene phase is read by drawing and graphic layers, not by the
  full-screen composition. It stops on pause/inactivity, user reduced motion,
  system animation disabling and subtle-motion preferences. Paper stays still.
  Resume does not accumulate a large background-time jump.
- The gallery uses the same static native geometry and sleeve rendering as the
  live scenes, without preview animation clocks. It uses bounded 192 px image
  requests; full scenes share a remembered 720 px artwork request.
- Petal paths/brushes are cached by size/style. No FFT simulation, video loop,
  animated GIF, screen-sized blur or extra provider polling is introduced.
- The original Aurora illustration is bundled offline as a **1024×1536 WebP**;
  the native file is 167,778 bytes. Artwork/provenance is recorded in
  [design/immersive-worlds/README.md](../design/immersive-worlds/README.md).
  Other new scenery is native vector geometry. Missing song artwork uses a
  music-glyph fallback rather than an unrelated person's image.
- Enum additions are appended; original names/order and Classic defaults remain.
  Unknown future settings still decode to defaults. No pack/schema, launcher
  alias, theme/overlay bytes, backend or audio-pipeline change is required.

## Verification and scope

Focused checks cover persistence/unknown values; production rendering of all
12 modes; each new mode at portrait, wide and large-text/RTL sizes; all 12 gallery
selections across three gallery sizes; reachable exit/transport; buffering intent;
short-screen footer scrolling; cancelled seeks; auto-hide stability in Classic,
Minimal Night and all six new worlds; and a frame-clock check that motion advances
without recomposing the scene root, stops when inactive and resumes without a jump.
All **10 focused tests passed**, with zero failures, errors or skips. Rendering
checks include 26 scene configurations and three gallery configurations.

Production Android Kotlin compilation and generated debug/release version metadata
are checked for **0.2.0 / code 50**. Source archive/backend/checksums and matching
website build/typecheck/mobile-desktop links are checked before pushing. Generated
study screenshots stay in Git and are omitted from the compact source ZIP; native
resources, implementation, tests and provenance stay included.

These checks do not establish physical-device FPS, live provider playback or a
signed/R8-optimized APK. Rebuild code 50 from main with your existing signing key
to install these native modes; source publication does not publish an APK.
