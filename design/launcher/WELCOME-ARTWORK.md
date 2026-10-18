# Welcome / Silver waveform

The 0.1.3 welcome follows the owner's supplied visual reference: actual Veyra branding, an unboxed silver waveform, large native headings, three preview indicators and a silver Get started button. There is no restore-backup entry on Welcome.

- Production artwork: `app/src/main/res/drawable-nodpi/welcome_silver_wave.webp`, optimized to 1120px width and about 53KiB, decoded by the existing Coil loader.
- Original generated artwork: [welcome-silver-wave-original.png](welcome-silver-wave-original.png). Created from the owner's visual direction on 18 October 2026; no text, UI, buttons or imitation logo is baked into the artwork.
- App icon and authorized Super Malibu wordmark remain the actual app assets. Manrope handles native headings and controls.

The artwork uses a bounded offscreen layer with cached alpha-gradient masks, so it dissolves into the native background on all sides instead of ending in a framed card. A finite reveal and small gesture-triggered position change use graphics layers. No infinite animation, video, live blur, network image request or new dependency is added. The old two welcome campaign WebPs were removed after their code references were removed.

The small waveform on Home and the preview chip is native Canvas geometry (`VeyraWaveform.kt`). It does not decode another image. Home and Settings continue to honor the user's existing appearance.

The generated artwork was inspected. No Android screenshot is supplied or claimed: complete Android compilation is blocked by JitPack dependency access in this environment. See the 0.1.3 validation report before device verification.
