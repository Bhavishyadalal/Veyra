# Flowing motion · 0.1.8-beta1

Preview-only refinement; stable 0.1.7 is unchanged.

## Behavior

- Account avatars open a fading backdrop and a short slide/scale from the logical top-end corner. RTL mirrors the scale origin.
- Back, scrim, profile selection, removal, Add account and Settings use a guarded close request. The selector remains composed until its exit completes, then performs the requested action. Repeated actions cannot queue duplicate operations.
- Shared forward/back navigation uses 20% viewport travel with a smaller retained-parent parallax. Tabs use a restrained slide/crossfade. Artist shelves now share the same navigation policy instead of a separate fade.
- Settings category changes use a 28 dp directional slide with tonal fade; returning to the overview reverses direction. Existing scroll restoration remains intact.
- App/system reduced-motion preferences disable motion. Subtle mode retains its existing restrained behavior. Destination size changes snap rather than animate full-screen list measurements. No continuous animation or new assets/decoders were added.

## Focused verification

- Production-release Kotlin compilation.
- Existing NavigationTransitionsTest and NavigationMotionPolicyTest: eight focused cases covering depth/back policy, RTL/reduced-motion branches, subtle mode, retained layering and fixed-size transitions.
- Manual source review of the account dismissal paths, including rapid actions and actions during entry. This is code review, not a device interaction test.
- Website build/type checks and two existing desktop/mobile download-link checks.

No APK was assembled or published. No broad test suite, image generation or screenshot matrix was run for this change. Real-device frame timing, gestures and visual smoothness still need device testing; this verification does not establish 60 fps on every phone.

[Previous experience checkpoint](EXPERIENCE-STAGE-1-0.1.8-beta1.md) · [Full pending roadmap](../docs/roadmap/BETA-0.1.8-EXPERIENCE.md)
