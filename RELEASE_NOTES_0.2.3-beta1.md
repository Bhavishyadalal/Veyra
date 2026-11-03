# Veyra 0.2.3-beta1 · build 60
### Your look. Your pace.

**Testing preview · Based on stable 0.2.2 build 59**

## A lighter visual profile

New **Settings → Performance → Reduce visual effects** replaces blur and glass with opaque surfaces, removes decorative shadows, uses static backgrounds and simplifies motion. Animated Canvas cover videos and lyric blur/glow are suppressed while the profile is on. Album covers, readable lyrics, theme colors, fonts, icons and playback controls remain.

The profile also skips backdrop sampling, decorative background image/mesh preparation and legacy palette work that would otherwise continue behind a solid surface. Standard control feedback, scrolling and functional lyric/progress updates remain available.

## Choose after welcome

After welcome setup has finished, Veyra offers **Reduced effects · Recommended** and **Full effects**. The choice is saved and does not repeat after a normal restart. Existing installations receive the suggestion once when upgrading to this feature. Back/outside dismissal keeps the current profile and records that the suggestion was seen.

You can switch profiles at any time in Settings. The reduced profile preserves your individual appearance preferences; switching it off restores those choices. Full effects allows your selected settings rather than forcing every optional effect on. Conflicting individual effect switches are disabled while the master override is active.

## Refresh rate stays independent

High performance mode requests the selected supported refresh rate without switching blur or animations back on. Reduced effects and a 60/90 Hz preference can be used together. Changing individual blur/motion settings also no longer disables the refresh-rate preference.

## Testing track

Includes stable 0.2.2's Search keyboard, startup recovery and navigation-gallery fixes. Stable `main` / `stable/0.2.2` remains build 59. Catalogs and bundled fallbacks target `preview/0.2.3-beta1`; completed older betas remain unchanged.

[Verification and device checklist](verification/BETA-0.2.3-beta1.md) · [Source project](downloads/Veyra_Android_Source_0.2.3-beta1.zip) · [SHA-256](downloads/Veyra_Android_Source_0.2.3-beta1.zip.sha256)

The owner runs GitHub workflows manually. This push does not publish an APK release. Reduced rendering work is verified in code/offscreen tests; sustained phone frame rates and glitch-free operation are not established by those checks.
