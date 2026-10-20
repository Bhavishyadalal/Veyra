# Veyra 0.1.7-beta1 · Listening continuity and eleven worlds

**Preview source · code 25 · Made by Bhavishya Dalal**

Stable **0.1.6 / code 22** remains separate. This source update does not publish
an APK or change the Listen Together backend.

## Glass Noir theme icons · pack v2

- Six AI-edited launcher icons preserve Veyra's current silver waveform and black glass case, with each theme's lighting and details.
- Home branding, icon-picker previews and launcher assets now share the same design. Monochrome mode retains the official waveform silhouette.
- Update the six reference themes to pack v2 and rebuild the refreshed source for their bundled launcher icons. Selection remains optional; the default Veyra icon is unchanged.
- Published v1 packs remain available. App version stays **0.1.7-beta1 / code 25**.
- [Icon study and verification](verification/GLASS-ICONS-0.1.7-beta1.md).

## Six reference-inspired themes

- **Pumpkin Purr:** cream ghost kittens, pumpkin lanterns, cocoa and rounded Fredoka headings.
- **Sunset Crew:** violet coastal cars, honey sunset and unofficial SpongeBob / Patrick fan-art cameos with attribution.
- **Starry Spirits:** gentle ghosts, a swirling teal-and-gold painted sky and starlit details.
- **Analog Reverie:** smoked olive, walnut, vinyl and a luminous swallow with restrained bold typography.
- **Petal Promise:** ivory tulips, satin bows and watercolor hearts over readable plum-rose controls.
- **Moonlit Herbarium:** lavender botanicals, ivory moths, a crescent moon and a quiet garden spirit.
- Banner contrast now covers the full text column over bright artwork, including large text and RTL.
- Every pack includes artwork, fonts, functional icons, an original brand mark, control shapes and timeline styling. Six matching launcher choices remain explicit and optional.
- All previous themes and completed reliability/performance work remain. Version stays **0.1.7-beta1 / code 25**; no APK publication or stable promotion.
- [Downloads and native previews](themes/README.md#reference-collection--six-more-worlds) · [Verification and limits](verification/REFERENCE-COLLECTION-0.1.7-beta1.md).

## Performance and theme polish

- Independent extra Home feeds load two at a time, keeping their existing display order and cancellation guards.
- Switching or closing an Explore category cancels its old request; late responses cannot replace a newer category or another listener's view.
- Download recovery serializes on a single background writer, coalescing queued snapshots during bulk changes and preserving write order.
- Theme Studio previews, banners and scene backgrounds decode for their displayed size instead of requesting original-size bitmaps.
- Theme banner contrast follows start-aligned text in RTL layouts.
- [Focused checks, native component studies and remaining device limits](verification/PERFORMANCE-0.1.7-beta1.md).

## Listening continuity and recovery

- Saved Home content shows when it was saved; restored playback stays paused.
- Artwork prefetch follows nearby shelves and cancels obsolete requests.
- Interrupted downloads return as explicit retries, with Retry failed and available storage/destination feedback.
- Listen Together recovers on network changes and silent sockets, with host-disconnection feedback.
- Theme apply offers an explicit, unchecked matching launcher option while preserving existing previews and compact browsing.
- Queue removal and Clear user queue offer guarded Undo without starting playback.
- Larger download action targets, track-specific TalkBack labels, growing status text, accessibility-aware undo timing and calmer reduced-motion feedback.

[Implementation, validation and device-check limits](verification/RELIABILITY-0.1.7-beta1.md).
Version remains **0.1.7-beta1 / code 25**. No new installer is published.

## Midnight Express

- Original rain-soaked night-train scenes, amber carriage light and coal-blue surfaces.
- Bold Rajdhani timetable-style headings, thirteen weighted glyphs, ticket-like corners and a parallel route timeline.
- Optional original ticket-waveform launcher, offered explicitly during theme application.
- Uses the existing engine and reduced-motion behavior; real music artwork and all completed reliability features remain.
- Version stays 0.1.7-beta1 / code 25. Theme pack needs code 25; its bundled launcher needs the refreshed source.
- [Native previews, validation and limits](verification/MIDNIGHT-EXPRESS-0.1.7-beta1.md). No APK publication.

## Astral Tide

- Original bioluminescent manta-ray ocean artwork, sea-glass mint controls and champagne starlight.
- Bold geometric Space Grotesk headings, custom navigation/player/action glyphs and a luminous mint timeline.
- Optional bundled manta-waveform launcher icon, offered explicitly during theme apply.
- Compact Theme Studio, real song covers, existing playback/reliability behavior and reduced-motion support remain.
- Data-only schema-2 pack; minimum app code 25. Rebuild the refreshed preview source for its optional launcher icon.
- [New-theme previews and verification](verification/ASTRAL-TIDE-0.1.7-beta1.md). No APK publication.

## Playtime Club

- Unofficial cartoon music-party artwork featuring Doraemon, Shin-chan and Oggy.
- Rounded Fredoka headings, a smiling Veyra waveform icon and a blue/yellow/mint palette.
- Puffy button shapes, thick rounded glyphs, bubble timeline and subtle backdrop details.
- Gentle spring feedback on player controls, with reduced-motion support.
- Optional matching launcher icon; compact Theme Studio and all previous themes remain.
- Requires code 25; older clients show Update Veyra first. Character-owner credits are
  shown in theme details and included in the pack. No affiliation or endorsement.

## Web Slinger

- Original midnight-city and crimson-glass artwork with cobalt web details.
- Bold Barlow Condensed headings and a matching Veyra icon/wordmark on Home.
- Custom navigation and transport glyphs, angular action/player surfaces and
  a web-pattern timeline with a diamond playhead.
- A matching Android launcher icon, chosen separately in Theme Studio or the
  existing App icon picker.

## Halloween Nocturne v3

- Moonlit forest, amber details, gothic headings and an ember timeline.
- Matching Veyra branding and an optional Halloween launcher icon.
- Earlier v1/v2 packs remain supported; published archives are preserved.

## A better Theme Studio

- Larger artwork previews, a themed backdrop and clearer collection panels.
- All/Downloaded filters, installed palette swatches and clear download states.
- Progress, cancellation, offline packs and confirmation before applying a theme.
- Grouped app-icon selection, including the standard Veyra icon.
- Theme application does not silently replace a manually selected launcher icon.

## Familiar controls, a different atmosphere

The listener's actual music covers, account rules, menu actions, playback controls,
queue/lyrics behavior and saved appearance preferences remain. Shared typography,
artwork frames, menu colors and native press feedback follow the active theme.
Large text and reduced-motion settings remain supported.

Playtime Club requires **the latest preview / code 25**. Earlier themes still support code 24. Older preview builds show
Update Veyra first. Original appearance and previously installed themes remain
available offline. Music playback is not stopped by confirmed interface recreation.

[Themes, downloads and native component studies](themes/README.md)
· [Checks and physical-device follow-up](verification/THEMES-0.1.7-beta1.md)

Android compilation and 16 focused pack/launcher tests passed for this update; see the verification record for exact scope. Native studies
include 320 dp / 150% text layouts; they are not full-device screenshots. Launcher
switching, shortcuts and full Android layout still need device verification.

## Compact controls and smoother theme changes

- Theme Studio is a compact collection with a separate detail page and fixed actions.
- Appearance shows one App icon row; a grouped chooser keeps all 19 options available.
- Apply themes live while preserving your place; optional screen refresh remains.
- Icon switching runs off the UI thread and serializes changes across rotation.
- Lazy lists, shared catalog refreshes and fewer duplicate installation checks reduce
  avoidable work. No new permanent animation or network loop is added.
