# Margin Notes / native sketch preset · beta build 30

New beta-only pack and bounded native presentation preset. Version stays 0.1.9-beta1; build code increases to 30 so code-29 APKs cannot accept a preset their engine does not support. Stable app branches remain 0.1.8 / code 28.

- Two reviewed OpenAI-generated graphite-paper backgrounds, thirteen original transparent vector glyphs, official-waveform in-app brand treatment, Patrick Hand and Manrope with OFL notices. Nineteen mapped roles, twenty-two hashed files plus manifest.
- Native asymmetric Material/transport/navigation shapes, pencil-hatched timeline, finite player press tilt and paper-like page slides. No downloaded scripts, sprites or idle timers. Native animation preferences and custom-style isolation remain in effect.
- Beta catalog refresh uses `preview/<BuildConfig.VERSION_NAME>`; stable refresh stays on main. Exact pack/preview path allowlisting is preserved. All older pack bytes/minimum versions remain unchanged.
- Complete actual-asset sheets and an interactive browser study are in `themes/previews/margin-notes/`. The study is not an Android device screenshot. Launcher aliases are unchanged.
- To keep the source ZIP below the GitHub size limit as themes grow, the packager stores identical runtime image bytes once inside included data packs and records restoration mappings in SOURCE_MANIFEST. Expanded fonts/licenses and full Git source folders remain. Optional restoration checks hashes and never overwrites existing files.

## Focused checks

Release Kotlin compilation passed with BuildConfig.VERSION_CODE 30. A small JVM harness called the compiled production ThemePack validator and installer: code 30 accepted Margin Notes, code 29 rejected it, all nineteen role files existed after installation, and the real install path verified archive size plus outer/inner hashes. Existing ThemePackTest expectations were updated for the new preset/count; the broad JUnit suite was not run.

The full asset sheet and icon sheet were rendered from actual assets in Chromium and visually reviewed. This establishes asset visibility, not device layout or frame rate. Source archive manifest/checksum/restoration checks and website build/type checks cover the updated delivery links. No APK assembly, device animation benchmark or live website deployment is claimed.
