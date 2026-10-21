# Theme backgrounds · beta 0.1.8 correction

**Preview 0.1.8-beta1 · code 27 · 21 October 2026**

## Cause and correction

The Android navigation wrapper painted an opaque background over the outer `ThemeScene`. Home, Search and Library were transparent themselves, but could never reveal that image. Settings drew its own scene above the cover, explaining the difference in the owner's screenshots. Earlier illustrative component studies did not include the real wrapper and missed this defect.

`RetainedRootPage` now uses the shared `ThemePageSurface`: an opaque base, the static contrast-protected scene, then destination content. The base remains opaque so incoming/outgoing navigation pages do not expose text from the page beneath. Tab/Settings saveable-state behavior is preserved. The redundant outer and Settings scenes were removed.

Explore/category transitions, artist/album/playlist detail backgrounds, artist shelf grids and Account/Sources/Listen Together/Equalizer/Replay Settings subpages use the same surface. Real artist photographs, album covers, existing controls and player artwork remain intact. External sign-in WebViews retain their provider pages. Default appearance without an installed theme retains its normal solid background.

No new themes, characters, animations, dependencies or version changes. Stable branches, the stable ZIP and published pack bytes are unchanged. This is app code: receiving the fix requires rebuilding/installing the preview APK; redownloading a theme cannot patch an installed APK.

## Actual checks

- `:app:compileProdReleaseKotlin` **passed**. Existing unrelated deprecation/layout warnings remain; this is compilation, not an APK build or a claim of clean lint.
- `verification/theme-preview:probePageBackground` **passed**: seven cases use the exact shared retained wrapper now called by Android, including nested Explore/detail/Settings surfaces, RTL and 150% font scale. Comparing each with the same scene minus its header artwork found **27,794/30,000** changed sampled pixels below the illustrative labels. A no-pack check retained the solid default; an intentional old-style opaque cover hid the artwork, confirming the regression control.
- [Native wrapper captures and result record](../themes/previews/background-0.1.8/RESULTS.txt). The captures contain illustrative labels, not full Android screens. The artwork was inspected in a rendered wrapper capture.
- Website build/type checks **passed**; both focused desktop/mobile download-link tests **passed**.

The probe initially compared the default background to a themed scene's decorative ornaments; its control was corrected to compare the actual palette background color. No app change was needed for that test-harness assertion.

## Limits

No real-device screenshots, device navigation/performance measurements, playback, TalkBack or network tests were run. The native wrapper checks establish layer visibility and fallback, not full-screen device layout or 60 fps performance. No APK was built, no Actions were dispatched and no stable promotion occurred. Website Git push does not establish a successful Vercel deployment.

The preview source ZIP is regenerated after staging source and checked with `scripts/verify_archives.py`. Its checksum is published alongside the download; final delivery metadata records the new repository commits and public checksum separately to avoid a self-referential archive hash.
