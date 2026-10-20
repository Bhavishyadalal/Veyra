# Veyra Theme Studio

**Latest preview: 0.1.7-beta1 / code 24. Stable 0.1.6 stays separate.**
Made by **Bhavishya Dalal**.

| Theme | Its own atmosphere | Download |
| --- | --- | --- |
| **Web Slinger v1** | Midnight city, crimson glass, cobalt webs, bold condensed type, angular action details and a web timeline | [Theme ZIP](packs/web-slinger-v1.zip) · [SHA-256](packs/web-slinger-v1.zip.sha256) |
| **Halloween Nocturne v3** | Moonlit forest, smoked glass, pumpkin amber, gothic headings and an ember timeline | [Theme ZIP](packs/halloween-nocturne-v3.zip) · [SHA-256](packs/halloween-nocturne-v3.zip.sha256) |

[Offline preview gallery](index.html) · [Catalog](catalog.json) · [Verification](../verification/THEMES-0.1.7-beta1.md)

## Make it yours

1. Build/install the **latest preview source**. These ZIPs are themes, not APKs.
2. Open **Settings → Appearance → Theme studio**.
3. Browse **All themes** or your **Downloaded** collection. Download a theme;
   progress, Cancel, retry and offline installed themes remain available.
4. Choose **Apply theme → Apply & restart UI**. Later keeps the current appearance.
   The Activity refreshes; the playback service is not stopped.
5. For a matching launcher icon, choose **The finishing touch → Use matching app
   icon**, then confirm. The existing **App icon** picker also includes both themes.
6. Return to **Original Veyra** whenever you want. The separate **Restore original
   app icon** action returns the launcher to Veyra's standard icon.

Applying a theme does **not** silently change the launcher icon. Removing a theme
or choosing Original does not overwrite a manually chosen icon. Android launchers
may cache icon changes or apply their own monochrome themed-icon setting.

## More than a new accent

| Component | Theme presentation | Preserved behavior |
| --- | --- | --- |
| Home branding | Matching Veyra icon and heading font; Web Slinger wordmark treatment | Accessible Veyra identity, responsive logo width and actual Home content |
| Main screens | Darkened local atmosphere, web/ember details, color and typography | Navigation, accounts, search, scroll and saved customization |
| Home artwork | Theme frames, caption gradients and decorative section rules | The listener's real album covers and playback actions |
| Navigation | Theme glyphs, colors, radius and framed native glass surface | Labels, routes, gestures and accessibility roles |
| Player | Custom glyphs, button geometry, themed fill and web/ember playhead | Seek, RTL, elapsed/duration, busy/locked states, haptics and touch targets |
| Menus / Settings | Theme palette and sculpted action icon housings | Existing actions, account/offline requirements and sleep-timer state |
| Theme Studio | Large native previews, collection filter, saved swatches and clear actions | Checksum validation, download/cancel, confirmation, offline use and removal rules |
| Android launcher | Two bundled adaptive icon choices, with legacy fallbacks | One enabled launcher alias; user choice remains separate from theme selection |

Web details are drawn behind content without pointer handlers or continuous
animation loops. Native press feedback and existing reduced-motion preferences
remain. Service logos and specialty glyphs keep their original meaning.

## Native component previews

These are **Compose component studies, not complete Android device screenshots**.
They use real shared branding, Home frames, theme backgrounds, player controls,
Settings rows and Theme Studio preview components in illustrative compositions.
Music-cover examples are original demonstration artwork; the real app retains
its actual music artwork. Menu rows, navigation arrangement and Studio surrounding
content in the studies are illustrative; Android host windows are not reproduced.

| Web Slinger Home | Web Slinger player |
| --- | --- |
| <img src="previews/web-slinger/home.png" width="280" alt="Web Slinger native Home components" /> | <img src="previews/web-slinger/player.png" width="280" alt="Web Slinger native player controls" /> |

| Theme Studio | Studio at 320 dp / 150% text |
| --- | --- |
| <img src="previews/web-slinger/studio.png" width="280" alt="Web Slinger Theme Studio components" /> | <img src="previews/web-slinger/studio-large-text.png" width="280" alt="Large-text Theme Studio" /> |

| Halloween branding | Halloween Theme Studio |
| --- | --- |
| <img src="previews/home.png" width="280" alt="Halloween Veyra branding" /> | <img src="previews/studio.png" width="280" alt="Halloween Theme Studio components" /> |

Additional [Web Slinger studies](previews/web-slinger) and [Halloween studies](previews)
include Settings, actions and large-text layouts.

## Compatibility and validation

The new packs use **schema 2 / minimum app code 24**, so older code-23 preview
builds show **Update Veyra first**. Previously installed Halloween v1/v2 packs
remain supported by the new app. Existing published v1/v2 ZIPs stay immutable.
Theme updates download into a separate version directory and require explicit
Apply; a download never replaces the currently active theme definition.

Packs are data only. No scripts, executable layouts, component names, remote asset
references or arbitrary launcher icons can be supplied by a pack. Bundled icon
mapping recognizes only the two known theme IDs. Unknown themes cannot select an
Android component. Limits remain ZIP 12 MiB, 48 asset files, 8 MiB per file,
32 MiB unpacked, 2048-pixel artwork and 256-pixel glyphs/branding icons.

The catalog and downloads use exact repository HTTPS paths. The outer checksum,
every inner hash, manifest/app compatibility, safe paths, unpacked sizes, contrast,
geometry, image bounds and native font loading are validated before installation.
Failures preserve the previous installation; cancellation cleans temporary data.
Active themes cannot be removed. Original appearance and installed packs work offline.

## Rebuild and provenance

Run `python3 -m pip install -r themes/requirements.txt`, then
`python3 themes/build_themes.py`. Original assets and SVG sources are checked in;
no AI service or network access is needed for rebuilding. Published ZIPs cannot
be overwritten with changed bytes: bump their manifest version first.
`--replace-draft` is only for a theme version that has **not** been published.

The builder produces both packs, checksums, catalog and Android icon densities.
Full atmosphere/fonts remain in downloadable packs; only the catalog and bundled
launcher artwork are added to Android resources. A theme ZIP is not a launcher
icon installation mechanism.

Original AI-assisted abstract artwork and original vector glyphs by Veyra.
Web Slinger is a web-and-city fan theme; no official comic/film artwork or brand
logo is included. Manrope, Barlow Condensed and Grenze Gotisch retain their
included SIL Open Font License notices. [Barlow Condensed source](https://github.com/google/fonts/tree/main/ofl/barlowcondensed)
· [Android adaptive icon reference](https://developer.android.com/develop/ui/views/launch/icon_design_adaptive).
