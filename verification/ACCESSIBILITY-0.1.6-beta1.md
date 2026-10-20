# Veyra 0.1.6-beta1 · Readability and compact layouts

Preview-only follow-up to the [interface/motion update](UI-POLISH-0.1.6-beta1.md).
Version remains **0.1.6-beta1 / code 21**; stable app code stays unchanged.

## Player buffering fix

The shared player control's crossfade previously wrapped its content. During a
play-icon → spinner transition, the old larger icon could keep the crossfade's
bounds larger than the spinner, while the new content was positioned at the
start. Both states now occupy the same full control area and center their
contents explicitly. The main play control uses a 36 dp indicator; smaller
controls use 24 dp. The reduced-motion arc uses the same geometry.

## Readability and space

- Settings main/subrows stack their trailing controls based on available width
  **and font scale**, including wide phones with large system text.
- Song-action values move below their label when text-scaled width is limited;
  labels and values no longer compete for a narrow horizontal strip. Download,
  cancel/retry/delete and all existing action callbacks remain.
- Player quality information gets its own line sooner on compact/large-font
  layouts. Timestamps have separate bounded columns, single-line ellipsis and
  higher opacity instead of growing into one another.
- Transport buttons adapt to narrow player content. Skip targets remain at least
  48 dp; the central control uses the available width instead of assuming a fixed
  92 dp target everywhere. This does not claim support below 144 dp content width.
- The bundled Manrope body-large weight is now Medium rather than Regular.
  Existing font families/system scaling remain; there are no extra font downloads.
- Original Settings vector strokes increase from 2 to 2.25 units; matching SVG
  design sources are updated. Transport icons retain their existing solid glyphs.

## New options: Settings → Appearance

**Stronger text:** increases body and selected heading weights using bundled
fonts. Does not override Android's text-size setting or scale text down to fit.

**Higher-contrast player controls:** adds subtle player button backgrounds and
outlines over artwork. Existing selected/disabled states, labels and actions
remain. This is a readability treatment, not a WCAG contrast certification.

Both options default off, apply through the app theme, persist in SharedPreferences,
and use the existing generic preference backup/restore path. The player contrast
preference is collected once by the theme rather than independently per button.

## Verification and limits

Production-release Kotlin compilation and **15 focused Android unit tests passed**
(zero failures/errors/skips): ReadableLayoutTest (3), SettingsSearchTest (5),
NavigationTransitionsTest (3) and PlayerQualityMenuTest (4). Layout policy
regressions cover narrow widths, 120–200% font scale and invalid measurements.
String XML parsed without duplicate names; SVG stroke definitions match the
native vectors. Website build/type checks and desktop/mobile notes-link checks
passed. Source archives are checked by manifest/hash/CRC and backend verification.

There is no Android emulator/device render in this environment. Compilation and
layout-policy tests do not establish zero overlap across every screen, language,
font size or device. Check 320/360/400 dp windows, Android text at 100/130/200%,
RTL and TalkBack on a real phone before promoting the preview. In particular,
verify buffering/play/pause centers at rest and during crossfade; test paused,
buffering, disabled and selected controls with contrast on/off. Test appearance
preference persistence after force-stop and after backup restore.

No APK, R8/package/signing validation or frame-time benchmark is produced here.
Made by Bhavishya Dalal.
