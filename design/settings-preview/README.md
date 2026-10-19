# 0.1.4-beta1 settings previews

Rendered from the actual reusable Compose controls, using Veyra's bundled fonts, icon artwork and matching theme declarations. These are component review scenes, not complete Android screen captures.

| Preview | Purpose |
| --- | --- |
| [Overview](overview-390w-1.0x.png) | Open category destinations, stronger typography and quiet icon accents |
| [Dark controls](controls-390w-1.0x.png) | Toggle, disabled reason, slider, current value, selector and destructive action |
| [Light controls](controls-light-390w-1.0x.png) | Contrast and selection treatment in the light theme |
| [Large text](controls-320w-1.6x.png) | 320dp layout with 1.6× text; controls move below their labels |
| [RTL controls](controls-rtl-390w-1.0x.png) | Mirrored layout, slider and navigation chevrons; sample copy stays English |
| [Choices](choices-390w-1.0x.png) | Radio selection with context retained for every option |
| [Google account entry](account-390w-1.0x.png) | Actual shared production entry: tap to open Google/YouTube Music sign-in; no local credentials |
| [Signed-in account](account-signed-in-390w-1.0x.png) | Illustrative account data with channel switching and sign-out |
| [Last.fm and icon choices](lastfm-390w-1.0x.png) | Existing optional Last.fm credentials, explicitly separate from Google sign-in; actual icon artwork |

Reproduce with the [component renderer](../../verification/settings-preview/README.md). No generated bitmap is used as a button, label or control in the app. There are no new illustrations, image downloads, live blur effects or perpetual decorative animations in this settings update.

The old `dialog-390w-1.0x.png` fixture incorrectly labeled a generic username/password editor as “Connect your account.” That fixture is removed. The new account scene uses the same `SettingsAccountSummary` rendered by the app's `AccountCard`; it is a host sign-in action, not a local authentication form. Last.fm remains an existing optional service.
