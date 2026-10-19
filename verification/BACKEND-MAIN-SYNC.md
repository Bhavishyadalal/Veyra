# Backend mirror verification

The app `main` backend snapshot was synchronized with standalone
`Bhavishyadalal/Veyra-backend` commit
`6fdbd5082d79f179c49fb75c59c55c7695c7fd0a`.

## Results

- All 41 tracked standalone files match byte-for-byte, including executable modes.
- The nested manifest verifies its 40 source entries; the manifest itself matches
  the standalone repository.
- Go 1.27.2: module verification, vet, race tests and server/probe builds passed.
- **54 test cases passed**, with no failing cases.
- **15 black-box checks passed** against the server built from the app's backend
  directory, using two authenticated WebSocket clients. These cover guest rooms,
  token rejection, queue broadcast/reorder/resync, playback metadata, host-only
  controls, presence and clean leave.
- The cron helper succeeded against that local server when invoked from `/tmp`.
- The test server shut down normally with exit code 0.
- All 865 tracked Android/shared/sharedUi files remain byte-identical to app
  `main` commit `49eaeaac8f4f147c4ed8434dec6c9612324c316d`.
- Stable remains **0.1.5 / code 20**. Preview 0.1.6 remains on its separate branch.

## Scope and limits

The obsolete Oracle workflow and VM/Python deployment helpers were removed.
The active app Go CI now checks the snapshot manifest, verifies modules and runs
race tests. Nested standalone workflows are reference files, not additional app
GitHub schedules.

These checks ran locally. They do not establish that GitHub Actions has run,
that Render stayed awake, or that Android audio playback was tested. No Render
deployment, APK build, release, signing change or Android version bump occurred.
