# Veyra 0.1.6-beta1 · Settings refinement

Preview source only, **0.1.6-beta1 / code 21**. Stable Android code remains
0.1.5. Made by Bhavishya Dalal.

## One Back control, consistent navigation

- Removed the duplicate Settings text-and-arrow row beneath search. The top-left
  icon now follows the same search/category Back action as system navigation.
- Settings stays mounted beneath its sub-screens to retain state. Its former
  local Back handler could intercept a gesture while hidden, clearing the
  underlying category instead of leaving the visible page. The host now owns
  Settings Back and enables it only when Settings is the active destination.
- Retained/outgoing detail pages cannot handle search/selection Back while a
  different destination or shelf is visible.
- Toolbar Back from Replay clears both Replay visibility and its Settings route,
  avoiding an extra invisible navigation step.
- Query, category and existing per-category scroll positions remain saveable.
  Back clears active search first, then returns to the category overview, then
  exits Settings. A visible keyboard, chooser or dialog still dismisses first.

## Appearance controls

Theme, accent palette, artwork corners, list spacing, motion and priority player
menu action now use flat, consistent value rows. Their options open in bounded,
scrollable bottom sheets with a selected checkmark, accent samples where useful,
press feedback and accessible selection semantics. Labels wrap and choices have
at least 56 dp height. Existing adaptive Settings rows stack controls on small
screens or with large text. Empty theme subtitles do not reserve a blank line.

All existing preference setters, options, reset confirmations and account rules
remain. This changes presentation and Back ownership, not the available features.
See [customization scope](CUSTOMIZATION-0.1.6-beta1.md) for the underlying options.

## Verification

- Android release Kotlin compilation and 27 focused JVM tests passed: Settings
  navigation/save-restore (4), Settings search (5), motion policy (4), navigation
  transitions (4), customization (3), readable layout (3), player quality menu (4).
- Website production build/type checks and two desktop/mobile download-link
  checks passed after updating its preview notes link.
- Refreshed source archive is checked for ZIP integrity, source hashes and backend
  mirror consistency. No APK was assembled or Actions workflow dispatched.

No device/emulator render was available. These tests do not establish real-device
predictive-back behavior or every layout. Device follow-up: Appearance at large
font size; open/dismiss every chooser; Settings → Sources/Account/Replay → one
Back gesture; search/category/overview Back; and player/detail dialogs over pages.
