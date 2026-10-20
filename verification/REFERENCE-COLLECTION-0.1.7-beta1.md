# Six reference-inspired Veyra themes

**Preview:** 0.1.7-beta1 / code 25 · **Catalog:** eleven themes

## Delivered collection

| Theme | Palette and typography | Details |
| --- | --- | --- |
| Pumpkin Purr | Cocoa, pumpkin cream, sage / bold Fredoka | Ghost kittens, lanterns, rounded controls, bubble timeline, cat-waveform mark |
| Sunset Crew | Twilight violet, honey, coral / bold Rajdhani | Coastal cars, unofficial SpongeBob / Patrick cameos, angular controls, road-like timeline, sunset-waveform mark |
| Starry Spirits | Ink teal, moon gold, ivory / bold Space Grotesk | Original swirling painted sky, gentle ghosts, starlit timeline, spirit-waveform mark |
| Analog Reverie | Smoked olive, walnut, parchment / bold Manrope | Vinyl, swallow, restrained corners and timeline, record-waveform mark |
| Petal Promise | Plum rose, blush, ivory / bold Fredoka | Tulips, bows, watercolor hearts, rounded controls, bubble timeline, bow-waveform mark |
| Moonlit Herbarium | Aubergine, lilac, antique gold / bold Space Grotesk | Botanical engravings, moths, moon, quiet garden spirit, moon-waveform mark |

Every pack includes one AI-generated scene shared by preview/banner/player assets,
an original brand mark, thirteen functional icon assets, licensed fonts, surface
colors, readable text colors, row/button/navigation corners and timeline settings.
Six optional adaptive launchers include density assets and monochrome resources.
The collection uses existing static scene decorations and reduced-motion handling;
no new animation loops, shaders, executable theme content or runtime dependencies.

Petal Promise keeps the light ivory artwork over dark plum-rose controls, matching
the current engine's contrast contract. No new light-theme engine was introduced. The shared banner scrim now covers the
whole text column (including large text and RTL) so pale artwork cannot wash out
titles or descriptions. This improves banner readability without changing pack bytes.
Original music covers and existing playback/queue/account behavior are preserved.

## Compatibility and preservation

- All six packs require **app code 25**; stable **0.1.6 / code 22 has no theme installer**.
- The refreshed source is needed for the six optional bundled launcher aliases.
  An earlier code-25 build can install compatible data packs without those aliases.
- Launcher selection remains explicit and unchecked by default during application.
  Unknown packs cannot supply arbitrary launcher aliases. Only LauncherDefault ships enabled.
- Previous five catalog packs, old pack versions, stable Android source, version name,
  version code and backend provenance remain unchanged. No APK or Actions dispatch.
- Sunset Crew is unofficial fan artwork. SpongeBob SquarePants / Patrick Star belong
  to their respective owners, including Nickelodeon / Paramount; no affiliation or
  endorsement. Credit appears in its description and pack license. User reference
  images are not redistributed. All bundled font licenses remain included.

## Checks performed

- Android production-debug Kotlin/Java/resource compilation succeeded through
  `:app:testProdDebugUnitTest`; no APK task was run.
- **17 focused tests passed:** ThemePackTest (14), ThemeLauncherTest (3).
  They cover installation and integrity of all eleven catalog packs, compatibility,
  the actual runtime catalog URL policy, palette contrast thresholds, safe archive paths, legacy pack support, launcher
  mappings, exactly one enabled default, and grouped-picker completeness.
- All six new WebP scenes decoded successfully in a local image decoder.
- Each new theme rendered Home, player and a **320 dp / 150% text / RTL banner**:
  **18 native component studies**, visually inspected. Every player's native Pause
  callback fired exactly once and its touch target remained at least 48 dp.
- The initial renderer assumed separate artwork filenames; it was corrected to
  resolve the actual manifest asset mapping. Final study typography follows the
  Android heading scale and weights. These were harness issues, not app runtime failures.
- Website type checking and production build passed. The focused download/link test
  passed on desktop and mobile Chromium (2 tests), including expansion of the new
  grouped download section and all six exact pack links.

[Download links and all native studies](../themes/README.md#reference-collection--six-more-worlds).

The studies use actual shared Compose components with illustrative surrounding
layout. They are **not Android screenshots**, real playback tests or complete app
navigation tests. Sample artwork in the player studies demonstrates the theme;
the production player continues to use the song's own artwork. Final studies are
compressed WebP artifacts to keep the source archive manageable. English samples
exercise RTL layout, not translation correctness. Real-device launcher switching,
TalkBack, playback, performance and network handovers remain unverified.

## Rebuild

`python3 themes/build_reference_collection.py` uses checked-in scenes, existing OFL
fonts and original SVG sources. It writes deterministic packs and manifest-derived
renderer projections, refuses to rewrite a differing existing pack version, and
synchronizes the public and bundled catalogs. Generate studies using:

```sh
./gradlew -p verification/theme-preview run -Ptheme=pumpkin-purr
```

Replace the ID for the other new themes. Future published changes need a pack
version bump. Source packaging follows AGENTS.md, with staging before packaging,
archive verification and archive-only catalog maintenance on historical branches.
