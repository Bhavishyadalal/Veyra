# Faster artwork loading

Artwork uses a dedicated OkHttp dispatcher with 16 concurrent requests per host
(previously the default 5), capped at 48 total. Playback transport stays separate.
The existing image disk cache directory is preserved and its maximum increases
from 100 MiB to 384 MiB; decoded-memory cache remains bounded at 20 percent.
The reveal fade is shortened from 200 ms to 80 ms.

Home preloads up to 12 distinct covers from the first three shelves at display
resolutions, using the same singleton image loader and caches as visible cards.
Obsolete requests are disposed when the covers change or Home leaves composition.
Beta skips this prefetch while its offline state is active. No Wi-Fi-only or
data-saver gate is added; existing source/audio quality preferences are preserved.

These are targeted source changes, not a measured device-speed guarantee. Kotlin
syntax and the pinned Coil request API are checked. Full Android compilation is
still blocked in this environment by JitPack HTTP 403; no APK is generated.
