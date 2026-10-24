# Beta build 44 · reliability review

Applies to `preview/0.2.0-beta1`, Android code 44. Stable build 40 remains unchanged.

## Confirmed causes and corrections

| Cause | Correction |
| --- | --- |
| Overlapping Library requests could publish out of order. | A revision gate publishes only the latest active request. Reloads retain successful content. |
| Library mapped results seeded ratings before the owning account was validated. | Validate account/request ownership before ratings and continuation side effects. |
| Cancelled pagination could return from a non-cooperative loader and seed old account likes. | Recheck cancellation after each page and account identity around suspension. |
| Refresh jobs had no ownership when an account reset cleared state. | Cancel tracked jobs on reset; only the current job may clear its indicator. |
| Ratings used non-atomic map read/modify/write. | Atomic state updates preserve concurrent explicit ratings and seed only absent values. |
| Remembered player geometry omitted scene style. | Include the scene style so equal-corner/material theme switches update the button shape. |
| Navigation dragging captured old tab geometry and layout direction. | Restart the gesture handler when its geometry/direction changes. |
| A glass export remained active with no selection lens to consume it. | Export only when the glass selection pill is active. |
| Artwork-header action expansion always used a spring. | Use an immediate transition when reduced motion is active. |

Home and Explore already guard result ownership; their existing protections remain.
Mini-player gestures already use updated callbacks and are intentionally retained.
Theme/asset ZIP bytes, catalogs, launcher resources and stable source archives are unchanged.

## Focused verification

- Five regression tests: out-of-order/invalidation ownership; cancelled non-cooperative results; cancelled liked pagination; concurrent explicit ratings; a rendered player shape switching mist → web → mist.
- Android production Kotlin compilation and debug version metadata generation.
- Website production build/typecheck and mobile/desktop preview notice and source-link checks.
- Source archive integrity/checksum/retention and backend snapshot verification.

These checks cover the corrected causes. They do not establish device frame rates,
full-device playback/network behavior or the absence of every app defect. No APK
is assembled or published by this source update. Install a build with code 44 to
receive the fixes; report any remaining reproducible device issues separately.
