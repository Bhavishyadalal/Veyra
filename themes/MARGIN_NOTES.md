# Margin Notes · special sketch theme 01

**Beta 0.1.9-beta1, build code 30 or newer.** This is a data-only theme pack for the new native sketch preset; it cannot be installed by stable 0.1.8 or the older code-29 beta. Rebuild/install the refreshed beta once. Future packs using this supported preset can be downloaded normally.

Graphite paper, ivory pencil lines, imperfect cartoonish glyphs, quiet mustard stars and sage details. Original AI artwork places its busiest sketches near the edges so real song artwork and text remain functional.

[Theme ZIP](packs/margin-notes-v1.zip) · [SHA-256](packs/margin-notes-v1.zip.sha256) · [Every asset](previews/margin-notes/all-assets.png) · [Icon sheet](previews/margin-notes/icons.png) · [Interactive browser study](previews/margin-notes/index.html)

## All 19 mapped asset roles

| Role | File |
| --- | --- |
| Header / deep-page background | `artwork/preview.webp` |
| Studio preview | Same reviewed background file |
| Player background | `artwork/player.webp` |
| In-app Veyra brand mark | `artwork/brand.png` |
| Headings | `fonts/PatrickHand.ttf` |
| Body text | `fonts/Manrope.ttf` |
| Home, Search, Explore, Library | `icons/nav_home.png`, `nav_search.png`, `nav_explore.png`, `nav_library.png` |
| Play, Pause, Previous, Next | `icons/play.png`, `pause.png`, `previous.png`, `next.png` |
| Queue, Lyrics, Settings, Back, Download | `icons/queue.png`, `lyrics.png`, `settings.png`, `back.png`, `download.png` |

The pack has 22 checksummed files plus its manifest. SVG glyph/brand sources are in `source-icons/margin-notes/`. Backgrounds were generated with OpenAI image generation; functional glyphs and the official-waveform brand treatment are original vector artwork. Patrick Hand and Manrope retain their included OFL notices. The brand mark does not add or change any Android launcher alias.

## Native behavior

- `sceneStyle: sketch` selects the native asymmetric shape for Material defaults and player/navigation frames. Geometry stays within fixed layout and touch bounds.
- Player buttons tilt by 2.5 degrees on press, with a bounded 140 ms tween. Existing press scaling remains finite; no sprite sheets or idle animation loop.
- `seekStyle: sketch` adds bounded pencil hatching to the functional timeline. Seeking, duration, RTL and player states keep their existing behavior.
- Root and Explore navigation use a short paper-like slide/fade. Reduced motion removes it; subtle motion keeps its simpler fade. Custom app styles continue to suspend downloaded-theme visuals.
- The native paper ruling is static, noninteractive and does not add a per-frame timer.

The HTML motion study is an illustrative browser composition using actual assets, not a screenshot of the Android app. Actual frame rate still needs a device check; no 60 FPS guarantee is implied.

[Focused verification](../verification/SKETCH-0.1.9-beta1.md)

## Build / edit

Install CairoSVG for the existing vector asset pipeline, edit the SVG paths or reviewed sources, then run `python3 themes/build_margin_notes.py`. Published v1 bytes are immutable; a changed pack requires a new version and updated catalog. Production WebP files are encodings of the reviewed AI images, not AI-generated interfaces.

The compact source download stores identical runtime image bytes once inside its included theme packs. Optional `python3 scripts/restore_theme_sources.py` restores editable image folders with checksum verification and preserves existing files. App font/license build inputs remain expanded; ordinary app compilation does not require restoration. Git keeps the full source folders.
