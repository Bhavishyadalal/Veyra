# Veyra 0.2.0-beta1

**Preview development · Android version code 45**

This development cycle starts from the user-tested **stable 0.1.9 build 40**.

## Chaos Studio · build 45

- **10 app transition styles:** Silk, Soft Zoom, Rise, Drift, Reveal, Quick Cut, Velvet, Spring, Diagonal and Shutter. Theme default remains available; choose Relaxed, Balanced or Quick pace and tap a live preview.
- **10 scroll-player transitions:** Zephyr, Cascade, Focus, Snap, Bloom, Sweep, Float, Compress, Unfold and Settle. Five earlier styles remain.
- **10 bottom-bar designs:** Monorail, Harbor, Ticket, Pebble, Prism, Bracket, Halo, Stitch, Pixel and Lozenge. Six earlier designs remain. Both navigation implementations support them with themed glyphs and Liquid Glass.
- **10 full themes:** Soda Sabotage, Velvet Vandal, Acid Daydream, Peach Panic, Ink and Ivory, Holo Havoc, Denim Disorder, Cherry Voltage, Mint Mutiny and Lunar Lollipop. Each includes original AI wallpaper, licensed fonts and 123 asset roles including pressed controls.
- **7 independent overlays:** Gummy Signal, Razor Petal, Disco Chrome, Paper Revolt, Velvet Sigil, Patch Party and Pixel Fizz. Each has 119 roles and can replace enabled asset categories over any supported theme while retaining its artwork and palette.
- Total **72 themes / 22 overlays**. Existing packs and 105 launcher choices remain; no new launcher aliases are added. Theme brand artwork reuses reviewed glass waveform editions.
- All new transitions are finite; reduced motion and the scroll-animation master switch retain priority. Page lists keep immediate viewport sizing.
- Source downloads include a lossless offline runtime-pack library with a checksum-checked restore script; Android build inputs remain expanded.

Settings → Appearance → Navigation includes the new designs and Motion studio. Motion choices also appear beside Scroll player animations. Refresh Theme studio / Asset packs to see the collection.

[Actual asset gallery](themes/previews/chaos-studio/index.html) · [Implementation and checks](verification/CHAOS-STUDIO-0.2.0-beta1.md). Build/install **code 45** for the new settings. Source publication does not publish a signed APK.

## Reliability review · build 44

- Prevent older Library requests from overwriting a newer result; retain loaded content during reloads.
- Reject cancelled/account-stale Library results before seeding likes or starting continuation sync. Account changes cancel refresh ownership and clear stale loading indicators.
- Update ratings atomically so background sync cannot lose a user's concurrent Like/Dislike changes.
- Refresh player button geometry when a theme's scene style changes; update navigation drag geometry after resizing or switching layout direction.
- Skip unused glass exports when the selection lens is disabled and honor reduced motion in artwork-header expansion.

[Review and focused checks](verification/RELIABILITY-REVIEW-0.2.0-beta1.md). Rebuild/install **code 44** for these changes.

## Navigation studio · build 43

- Choose **Floating, Docked, Minimal, Capsule, Ribbon or Orbit**. The folding bar now honors the chosen design, fixing shape selections that previously only affected the separate navigation layout.
- Choose **Flow, Glide, Lift, Soft Fade or Elastic** for scroll-player transitions. The existing master switch still works without Liquid Glass, and reduced motion keeps the static navigation/mini-player layout.
- Preview navigation shapes and themed glyphs in Settings → Appearance → Navigation. Scroll transition choices are also next to the existing Scroll player animations switch.
- Preserve theme colors, asset-pack icons and fonts, custom destinations/labels and Liquid Glass. Explicit bar designs retain their geometry even with decorative asset materials.
- Save choices across restarts, expand navigation when presentation changes and keep old/future settings safe through existing JSON fallback behavior.

[Design, previews and checks](verification/NAVIGATION-STUDIO-0.2.0-beta1.md). Rebuild/install **code 43**; no signed APK is published by this source update.

## Liquid Glass + themes · build 42

Liquid Glass now takes priority over opaque theme/asset navigation fills in both navigation layouts. Themed ornaments, colors, icons and fonts remain; the separate mini-player honors the toggle too. Glass tint follows the effective palette and control opacity. Disabling glass retains the original themed material. Android support gates, reduced dynamic blur and the existing 0.33 sampling resolution remain unchanged.

[Implementation and verification](verification/LIQUID-GLASS-0.2.0-beta1.md). Rebuild/install **code 42** for this fix.

## Initial baseline · build 41

- Development branch: `preview/0.2.0-beta1`.
- Stable `main` and `stable/0.1.9` remain at build 40.
- Retains the immersive poster gallery, glass launcher editions, playback/Home fixes, 62 themes, 15 independent asset packs and 105 launcher choices.
- Beta theme/asset catalogs and bundled fallbacks use this preview branch; stable catalogs stay isolated on `main`.
- Pack ZIP bytes and existing app preferences remain unchanged. The app keeps its existing package identity and signing setup; installing a beta requires a compatible signature.

[Source downloads](downloads/README.md) · [Baseline verification and limits](verification/UX-0.2.0-beta1.md).

The source archive is an Android Studio project. An APK has not been signed or published by this change.
