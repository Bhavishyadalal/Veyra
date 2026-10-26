# Stable 0.2.0 — build 48

Focused patch on stable 0.2.0 build 47. The version name remains 0.2.0;
Android version code increases to 48. Install an APK signed by the same key
as your existing installation for an in-place update.

## Immersive footer: repeating hide/reveal cycle

The 5.5-second auto-hide removed `ImmersiveTimeline`. Its disposal callback
called `onScrubbing(false)`, but the parent unconditionally set `controls=true`.
Hiding the footer therefore revealed it again and restarted the timeout.

Only an actual gesture start now reveals controls. End/cancel/disposal only
clear scrub state. Transport and lyric interactions renew the timeout. A bounded
160 ms opacity transition replaces the hard appearance change; reduced motion
uses zero animation duration. The measured lyric reservation is retained, so
hiding the footer does not jump the content. No extra repeating ticker, backdrop
capture or decoder is introduced.

Timeline cleanup uses the latest callback and retires drag state when song,
duration or lock changes. Material can call drag-stop while removing a node;
pending seeks now commit only from a surviving composition. A retired timeline
cannot dispatch an unfinished seek. The next completed gesture still commits
exactly once through the existing playback callback.

## Selected icon in the foreground signature

`LaunchSignature` previously always rendered `R.drawable.veyra_launch_art`.
It now uses `AppIcon.selected` and the bounded, cache-keyed native-resource
painter already used by Home and the icon picker. The signature waits for the
chosen image to decode, preventing a default/placeholder flash. Missing or failed
images do not block app content. The 520 ms entrance, foreground-generation
behavior, tap dismissal and reduced-motion/system animation preferences remain.
This changes the app's foreground animation, not the OS-owned splash screen.

## Verification and release scope

Focused checks exercise the production Compose footer across multiple former
hide periods in Classic and Minimal Night, normal/reduced motion, playhead
updates and background taps. Pointer checks cover cancellation, latest cleanup
callbacks and the next completed seek. Existing buffering transport and native
launcher/resource checks remain in scope. Android compilation, version metadata,
source-archive/backend/checksum checks, and matching website build/typecheck plus
phone/desktop metadata/link checks are performed before pushing.

The prior runtime's local patch was not restored; these fixes are reapplied to
the verified build-47 base. Theme/asset packs, launcher aliases and the audio
pipeline are unchanged. Completed previews and historical stable branches stay
intact. No signed APK is built or published here, and offscreen checks are not a
real-device FPS benchmark or proof that all unrelated bugs are fixed.
