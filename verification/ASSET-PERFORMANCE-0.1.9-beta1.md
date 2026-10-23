# Asset-pack rendering / beta build 34

This is a focused code review and hotspot fix, not a claim of measured device FPS.
All theme/asset catalogs and immutable ZIPs are preserved. Stable is not promoted.

## Changes

- Local glyphs now use remembered, explicit 128/256 px requests and common immutable
  path/size cache keys. Glyph requests disable bitmap crossfades and redundant disk
  cache writes. Normal icon/transport requests and warmup use the same builder.
- Only common navigation/settings glyphs and transport/pressed skins are warmed
  before activation. At most 24 requests and two background warmup decoders; no
  eager full-pack/wallpaper decoding. Prefetch failure retains native fallbacks.
- A bounded eight-entry Typeface/FontFamily cache loads files off the UI thread.
  Composition uses cache lookups or keyed asynchronous loading, including the font
  preview. Font creation does not hold the cache lookup monitor while doing IO.
- Large Material and Settings containers use native rounded outlines. Custom pack
  geometry remains on actual player controls; their shape instances are remembered.
  Pressed skins display directly rather than running a second bitmap crossfade on
  every down/up event. Existing scale/press feedback and reduced motion remain.
- Pack tab navigation uses the lightweight tab fade. Forward/deep transitions keep
  the underlying page stationary and opaque until the entering page completes,
  avoiding simultaneous full-screen fade layers. Subtle/reduced motion still win.
- Cancelled warmup must not persist a new selection before publishing it. Apply
  warms first, checks cancellation, then commits/publishes without a suspension.

## Focused validation

Production `:app:compileProdReleaseKotlin` and `CheckPackPerformance.java` verify
compilation, the small decode budget, no warmup of wallpaper/fonts, deduplication,
pressed-state resolution and separate cache keys for paths/revisions/sizes.
Website build/type and mobile/desktop performance-note/source links are checked.
Source packaging verifies included bytes and checksums. No broad test suite, signed
APK generation, emulator/device benchmark or Vercel deployment claim is included.
The user should install a rebuilt build-34 beta APK to evaluate actual scrolling,
apply latency and frame pacing on their device. Cosmetic source-only changes do
not update an already installed APK.
