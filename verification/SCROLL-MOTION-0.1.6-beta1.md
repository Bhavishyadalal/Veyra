# Veyra 0.1.6-beta1 · Scroll motion

Preview-only refinement; stable 0.1.5 code is unchanged.

## Fix

The shared overscroll effect previously waited for `performFling` to finish
before starting its return spring. A released edge pull could therefore remain
offset during decay. Stretched axes now start returning immediately and pass
zero velocity to the list; unstretched axes keep their normal fling behavior.
New touch input cancels existing return jobs while retaining their current offset.
Springs explicitly finish at zero. The rubber-band curve now uses the intended
initial resistance instead of canceling its resistance factor.

The shared provider disables decorative overscroll for Subtle/Off motion and
when Android disables animations. Translation stays in the existing graphics
layer; no per-frame layout measurement or composition was added. This applies
to descendants using the shared Compose overscroll factory, including Settings.
Other independently implemented animations are not replaced by this change.

## Verification

Android release Kotlin compilation and the eight existing motion-policy and
navigation-transition JVM tests passed. Website build/type checks and its two
mobile/desktop download-link checks passed. Source ZIP integrity and source
hash checks passed. No APK assembled, no Actions build dispatched.

No device frame profiling or emulator was available. A physical-device check
should cover dragging both list edges, releasing a fast fling, grabbing a
returning bounce, horizontal shelves, and Subtle/Off motion. JVM tests validate
the existing motion policy, not real-device scroll frame times.
