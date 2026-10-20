# Veyra 0.1.6-beta1 · Customization and state

Preview source only, version **0.1.6-beta1 / code 21**. This follows the
[compact-layout update](ACCESSIBILITY-0.1.6-beta1.md). Stable app code remains
0.1.5. Made by Bhavishya Dalal.

## Settings → Appearance

- **Accent palette:** Original Veyra, Teal, Amber or Rose, with paired light/dark
  foregrounds and containers. The player's existing artwork-driven palette is
  preserved rather than adding a second competing artwork-color switch.
- **Artwork corners:** Original, Soft, Rounded or Square on shared shelf/track
  thumbnails, detail covers, player sleeves and mini-player artwork. Artist
  circles/decorative welcome artwork remain intentional shapes. Player docking
  reads the chosen mini-player corner so its morph matches the thumbnail.
- **Song list spacing:** Comfortable or Compact for standard song and queue rows.
  Compact reduces padding, not text size or artwork/touch-target sizes. Large
  system text automatically keeps comfortable spacing.
- **Motion:** Full, Subtle or Off. Subtle preserves brief page fades while quieting
  larger player/decorative motion. Off and disabled system animations take
  priority. The existing Reduce Animation setting stays synchronized with Off.
- **Priority player action:** move Download, Favorite, Sleep timer or Add to
  playlist to the top of the player's three-dot menu, or keep the original order.
  This is one configurable menu slot, not a new transport toolbar. The action
  retains existing account/local-file rules and its original callback. Downloads
  still use the full queue/progress/cancel/retry/delete control. Other actions
  remain available, and a prioritized action is not duplicated lower in the menu.
- Existing stronger-text and higher-contrast player controls remain available.

All new choices persist in the existing preference store and generic
backup/restore path. Unknown enum values in a future/invalid backup fall back to
safe defaults; enum identifiers, not translated labels or ordinal positions, are
stored. Artwork/spacing/motion are supplied by the theme, avoiding preference
collectors on each card/button.

## Reset scope

Both reset rows request confirmation and have normal Cancel/Back dismissal.
**Reset appearance customization** restores the new accent/corner/spacing presets,
stronger text and motion defaults. It does not reset legacy theme/glass settings.
**Reset player customization** restores original menu order and turns off the
higher-contrast control treatment. These functions remove only their listed
preference keys. They do not clear accounts, playlists, downloads, queues or
unrelated playback settings.

## Back and restoration

- Settings stores independent scroll offsets for Overview, each category and
  search. Returning to the parent/category restores its position. A changed
  nonempty query starts its own result list at the top; clearing search does not
  scroll the parent page to zero. Positions are saveable through Activity recreation.
- Back from the player's sleep-timer picker returns to the song menu before
  dismissing the menu/player. The chosen nested page is saveable and keyed to
  the current song. Menu and timer picker have separate saveable scroll states.
- Existing retained Home/Search/Library navigation, player last-panel preference,
  onboarding page/name save state and route Back policy remain; this is not a
  claim of serializing the entire navigation stack through arbitrary process death.

## Welcome name entry

The name-entry step now shows the bundled silver-wave artwork while the keyboard
is closed, in a bounded 96/148 dp region. It uses no network request or additional
asset download. Artwork hides while typing to make room for the input/Next action;
short screens still scroll. The existing saved name, validation and Back actions
remain. No restore-backup link or unrelated login form is added.

## Verification

Production-release Kotlin compilation and **23 focused Android unit tests passed**
(zero failures/errors/skips): CustomizationTest (3), ReadableLayoutTest (3),
NavigationMotionPolicyTest (4), NavigationTransitionsTest (4), SettingsSearchTest
(5), PlayerQualityMenuTest (4). These include invalid/future saved-choice fallback,
choice round trips, compact-spacing safeguards and subtle/off motion precedence.
Resource array counts match preset enum counts. A source-scope check verifies the
exact reset-key allowlists and absence of whole-store clearing. New palette
primary/container text pairs exceed 4.5:1 in the checked light/dark combinations;
this is not a certification of artwork overlays or the entire app.

Website production build/type checks and the relevant link test passed in desktop
and mobile Chromium (2 browser checks). Source packaging checks manifest/hash/CRC
and backend consistency. No APK is generated here.

## Remaining device checks

No Android device/emulator rendering or frame-time benchmark ran here. Verify
appearance switching in light/dark mode; all corner presets during player expand/
collapse; 320–400 dp layouts with large text; persistent choices after relaunch
and backup restore; all priority actions signed in/out and on local tracks; Back
from the sleep picker; Settings scroll restoration and the welcome keyboard.
Full process-death navigation restoration and zero overlap across every language
or screen are not claimed by these source and unit-test checks.
