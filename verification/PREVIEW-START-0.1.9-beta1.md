# Preview 0.1.9-beta1 · initialization

Baseline: completed stable v0.1.8 commit `6e37aa4615bad01b895dc547454f79218d94175e`.

This checkpoint creates the 0.1.9 preview track with version name `0.1.9-beta1` and code `29`. Runtime app/shared/UI code, theme data/assets and backend source are unchanged. No preview changes are promoted to main or stable/0.1.8.

Focused verification covers release Kotlin compilation, generated production-debug/release BuildConfig metadata, source archive manifests/checksums and website production build/type/two link checks. No APK assembly, broad app suite, device testing or deployment is implied. Successful push and remote SHA verification are required before reporting publication.

Archive retention is stable 0.1.8 plus previews 0.1.9-beta1, 0.1.8-beta1 and 0.1.7-beta1. Only the oldest ZIP/sidecar is removed from this new branch tip. Existing retained ZIP bytes and Git history remain intact.

The refreshed environment did not retain the earlier unpublished initialization commit, so this setup is recreated directly from the verified stable baseline. Existing local website edits were preserved on a local preservation branch before updating from the actual remote website main. They are not silently overwritten or included as preview changes.
