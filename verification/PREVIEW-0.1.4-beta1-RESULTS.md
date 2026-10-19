# Veyra 0.1.4-beta1 preview correction — 19 October 2026

## Stable and preview tracks

- `main` is reserved for stable 0.1.3/code 16. Its original source archive is byte-identical to the saved stable baseline (`8a03bb7`). Stable source is restored through a normal revert, without rewriting repository history.
- `preview/0.1.4-beta1` contains the settings work. Gradle base version is 0.1.4, suffix beta1, code 17. Generated production debug **and** release BuildConfig files both identify `0.1.4-beta1`, code 17. No APK, signing, GitHub release or stable publication.
- The incorrectly named 0.1.3 Settings Polish ZIP is retired. The updated preview ZIP is `Veyra_Android_Source_0.1.4-beta1.zip`. Package IDs and signing configuration remain unchanged; the existing `devDebug` flavor provides the separate Veyra Dev app identity.

## Account correction

The previous component illustration incorrectly called a generic username/password form “Connect your account.” That was misleading and has been removed. It was never wired into the actual Google account route.

Production `AccountCard` still calls its host `onSignIn` callback. MainActivity still sets `WebSessionMode.SIGN_IN`; `YtMusicLoginScreen` still opens `https://accounts.google.com/ServiceLogin` for YouTube Music. Google credentials are entered on Google's page, not in a new local form. Channel switching, sign-out and session capture remain unchanged.

The account card now spells out “Sign in to YouTube Music” and “Tap to sign in with Google,” with text that can wrap rather than truncate. It uses `SettingsAccountSummary`, which is also rendered by the corrected preview. Signed-in avatar/name/email and channel-switching callbacks remain. Last.fm's existing optional username/password login is kept, explicitly titled Last.fm in its separate preview. ListenBrainz keeps token entry; Spotify and Discord keep their existing flows.

## Preservation and verification

- Kotlin PSI comparison with stable: **160 existing named UI action/value contracts retained** (88 SettingsRow, 5 SettingsSubRow, 6 SliderRow, 4 SegmentedControl, 32 AlertAction, 20 TextButton, 4 DestructiveRow and 1 AccountCard). Sliders retain limits, steps and callbacks; selectors retain options, selected indices and callbacks. The four dual-action integration rows retain their separate switch callbacks.
- **131 original named functions retained**, including their original parameter order after reusable controls were moved to dedicated files. Added optional parameters do not remove existing interfaces.
- Complete auth directory, MainActivity, AppSettings and AppIcon byte-match stable: authentication routing, preference persistence and launcher apply/rollback logic were not replaced.
- The native Compose renderer passes the previous **16 component interaction checks**, plus Google-entry checks for a single host callback and absence of username/password inputs. Nine component scenes are rendered. Corrected account and Last.fm scenes were visually inspected.
- **57 focused Kotlin/JUnit regressions pass**. These are not the full Android unit/instrumentation suite.
- Version configuration generation passes for production debug and release. Full Android compilation remains unverified because NewPipe resolution is blocked by JitPack HTTP 403; generating BuildConfig is not compiling or installing the app. No real Google/Last.fm authentication or device run is claimed.

Reproduce [component renders](settings-preview/README.md) and the [preservation audit](settings-audit/README.md). Earlier resource and device-follow-up details remain in [the settings report](SETTINGS-REFINEMENT-RESULTS.md).
