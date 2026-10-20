# Veyra 0.1.7-beta1 · A different kind of night

**Preview source · version code 23 · Made by Bhavishya Dalal**

Stable remains **0.1.6 / code 22**. This preview adds the first version of Theme
Studio; it does not change the Listen Together backend or publish an APK.

## Halloween Nocturne

- Original smoked-glass pumpkin artwork, warm amber surfaces and gothic headings.
- Custom navigation glyphs and geometry, player transport icons and framed controls.
- A themed seek bar with an ember playhead, preserving seek and accessibility logic.
- Shared colors/fonts across the app, decorative Home/Settings headers and subtle
  player atmosphere. Existing music artwork and service logos remain original.

## Download, choose, return

Open **Settings → Appearance → Theme studio**. Download with progress/cancellation,
then confirm **Apply & restart UI**. The Activity refreshes without stopping the
playback service. Choose **Later** to keep the current appearance. Installed packs
work offline; **Original Veyra** restores saved base appearance preferences.
Inactive theme files can be removed with confirmation. Theme updates download
separately and cannot activate themselves.

## Compatibility and verification

Data-only packs are restricted to the official repository and validated before
installation: outer/asset hashes, paths, schema/app version, palette contrast,
geometry, unpacked limits, image dimensions and font decoding. Failed validation
leaves the current installed appearance alone. Invalid saved data falls back to
Original Veyra. No executable theme code or replacement action logic is accepted.

[Theme ZIP, native previews, supported components and licenses](themes/README.md)
· [Verification and device follow-up](verification/THEMES-0.1.7-beta1.md)

No APK was assembled or release uploaded during this work. Build the preview by
starting a **new** Android Release APK workflow on `preview/0.1.7-beta1`; re-running
an older workflow run uses its original commit.

## Halloween refinement · theme pack v2

- Persistent moonlit forest atmosphere on the main app surface and Settings.
- Amber artwork frames, warmer Home tile captions and decorative section rules.
- Theme colors now take priority over cover-derived colors in song sheets and
  detail palettes; original Veyra keeps its artwork-color behavior.
- Refined song menu icon housings, track header and action grouping; all original
  callbacks, online/offline rules, sleep timer and quality controls remain.
- Matching Settings icon surfaces and a framed native navigation bar.
- New original artwork; v2 installs alongside v1 until explicitly applied.

Source-only update. Stable 0.1.6 is unchanged; no APK is produced here.
