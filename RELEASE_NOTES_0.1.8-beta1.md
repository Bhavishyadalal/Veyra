# Veyra 0.1.8-beta1

**Preview Android source · version code 27 · based on stable 0.1.7**

## Flowing navigation and account panel

- Account selector slides and scales from the avatar corner, with a fading backdrop and a complete closing animation before account actions or Settings open.
- Back presses, outside taps and repeated actions use one guarded dismissal path.
- Shared pages, tabs and artist shelves use coordinated forward/back slides; Settings categories slide in the navigation direction.
- Reduced motion, subtle motion, system animation scale and right-to-left layouts remain supported. Full-screen lists keep fixed measured sizes during transitions.

[Focused verification and device-testing limits](verification/FLOWING-MOTION-0.1.8-beta1.md)

## Experience foundation — stage 1

- Optional immersive player with song/theme/blended/minimal backgrounds, positioned synced lyrics and accessible controls.
- Five new widgets: Sleeve, Vinyl, Studio Strip, Lyrics and Listening Studio, with cached artwork and event-driven updates.
- Home Speed Dial, shortcut tiles, greeting and Quick Picks behavior; customizable navigation destinations/startup/labels.
- Independent imported app/lyrics fonts, lyric sizing/centering, player/mini-player presets, seeking controls and Data Saver/preload choices.
- Content-country override and a hash-checked backup preview with portable selected font assets.

[Verification and limits](verification/EXPERIENCE-STAGE-1-0.1.8-beta1.md) · [Full roadmap and remaining work](docs/roadmap/BETA-0.1.8-EXPERIENCE.md)

This checkpoint does **not** complete the entire roadmap. Larger offline, podcast, nearby-listening and library/sharing extensions remain tracked for later stages.

## Search lifecycle and rendering efficiency

- Query edits, filter changes and source changes cancel obsolete pagination and speculative top-result work. Only one continuation can run for the current search.
- Loading state clears on completion/cancellation; delayed cleanup cannot hide the next search's spinner. Existing request-ID guards still reject stale results.
- Cached search results also warm their top song. Shared stream resolution remains independent so canceling search cannot cancel a track another playback caller needs.
- Listening Room botanical artwork cannot leak into Editorial or After Dark during a live style switch.
- Theme palettes, corner shapes and Editorial's player palette are reused until their inputs change, reducing repeated object creation.

[Focused verification and limits](verification/LOGIC-PERFORMANCE-0.1.8-beta1.md)

## App-style composition refinement

- **Music Editorial:** alternating portrait/square cover pairs, aligned captions, burgundy section rules and a compact magazine masthead.
- **Listening Room:** smaller cut-paper section tabs, lightly taped sleeve frames and an italic serif subtitle; botanical styling continues throughout.
- **After Dark:** a wide featured cover with a legible image scrim followed by numbered tracks, inset record framing and an explicit player-entry arrow.
- Preserved every shelf item, track/menu callback and current-track feedback. Large text uses the simpler ordered grid instead of the asymmetric layout.

[Refinement verification and native previews](verification/APP-STYLES-POLISH-0.1.8-beta1.md)

## Three optional app styles

Choose **Settings → Appearance → App style**:

| Style | Home and app-wide identity |
| --- | --- |
| **Original** | Existing Home, downloadable themes and independent fonts remain available. |
| **Music Editorial** | Cream paper, burgundy feature panel, large serif typography and cover mosaics. |
| **Listening Room** | Original generated botanical artwork, warm serif headings, compact recent sleeves and framed collages. |
| **After Dark** | Midnight/cobalt palette, condensed poster lettering and a record-shaped image following the loaded song. |

- Styles coordinate Search, Explore, Library, Settings, navigation and themed sheets through their own palette, typography and surfaces. Editorial's artwork-led player uses a matching dark burgundy/cream treatment for cover/video contrast.
- Switching styles is live and does not request playback. Original's theme/font choices stay saved; applying downloadable themes is disabled while a custom style is selected. Each Home retains its own scroll state.
- After Dark keeps the loaded song artwork while paused. No loaded song, missing art or a failed request shows a native abstract record; no bundled stock portrait is used.
- Existing Home refresh, saved/offline content, retries, pagination, Google sign-in, customization, recent-list toggle and track actions remain. The new tune button in custom Home opens the existing section editor.
- Layouts stack on narrow screens/large text. Decorative collage rotation is static; no animation engine or permanent effects loop was added.

[App styles: actual verification and limits](verification/APP-STYLES-0.1.8-beta1.md)

## Deeper theme polish and connected controls

- Artist photographs now fade into the theme scene instead of an opaque color strip. Photos and music covers remain real.
- Theme artwork continues into the shared player drawers, song/browse action sheets and Theme Studio/icon/font sheets, with existing scrolling and dismissal behavior preserved.
- Output/Listen Together and shuffle/repeat/autoplay groups now have one clean outline, edge-to-edge segment fills and inset dividers. Removed the mismatched individual rounded frames without changing actions or touch targets.
- Themed drawers skip background blur hidden beneath their local artwork; no new animation loops or dependencies.

[Focused verification and limits](verification/DEEP-THEME-POLISH-0.1.8-beta1.md)

## Background visibility correction

- Fixed the opaque navigation layer that hid theme artwork on Home, Search and Library.
- Applied the same readable scene to Explore/category pages, album/artist/playlist details and Settings subpages. Real artist photos and album covers remain intact.
- Each transitioning page retains its own opaque base, preventing underlying page content from showing through. No background animation loop was added.

[Background fix verification](verification/BACKGROUNDS-0.1.8-beta1.md)

## Theme polish and independent fonts

- More visible themed backgrounds across Home, Search and Library, with a contrast-aware scrim and static artwork.
- Cleaner Home branding: removed the decorative waveform and tightened icon/wordmark alignment.
- **All** opens first in the icon picker, with all 74 choices searchable together; existing filters remain.
- Cached, revision-aware Studio previews with decoded-image placeholders, bounded thumbnail/detail sizing and no unnecessary preview fade-in.
- **Appearance → Fonts:** Follow theme, Veyra default and six licensed independent presets, with preview before Apply and saved choices across theme changes.
- Consistent themed glyphs and Back-button geometry; corrected bitmap glyph sizing while retaining accessible targets and existing actions.
- No character/gyro additions. Stable Android and published packs remain unchanged.

[Verification and known limits](verification/APPEARANCE-0.1.8-beta1.md)

## Keepsake collection

Five new themes bring the catalog to **46**: Patchwork Picnic, Clover Requiem, Sunroom Mixtape, Concrete Afterhours and Matcha Mischief. Each combines original AI artwork, licensed typography, readable palettes, coordinated transport/navigation glyphs and existing reduced-motion-aware theme behavior.

Their downloadable packs also work on **stable 0.1.7 / code 26**, including its original 256 px brand-icon validator. Stable receives the shared catalog and pack data; the following Android changes remain exclusive to this preview.

## Theme Studio and launcher selection

- Search theme names and descriptions while preserving All/Downloaded filtering and detail-page Back state.
- Browse larger landscape thumbnails in compact rows with natural-height text.
- Search the active icon collection; switching collection or glass family clears the previous query.
- See the current launcher selection, larger 64 dp previews and an explicit selected checkmark.
- Choose five new matching Glass Noir icons, bringing the beta to **74 choices**. Stable remains at 69.
- Preserve optional matching-icon selection, all existing aliases/actions, real music artwork, account integration and playback behavior.

Includes the stable theme-installation correction. Existing published packs remain unchanged. No APK, signing operation, Actions dispatch or stable UI promotion is included.

[Theme collection and downloads](themes/KEEPSAKE.md) · [Verification and remaining limits](verification/KEEPSAKE-0.1.8-beta1.md)
