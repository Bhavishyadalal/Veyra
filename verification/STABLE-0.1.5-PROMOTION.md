# Stable 0.1.5 · preview promotion

19 October 2026. The owner explicitly requested promotion of preview/0.1.5-beta1 into stable main, including its Home, player and Settings refinements.

## Result

- Version name **0.1.5**, version code **20**, empty beta suffix for both production debug and release variants.
- Code 20 is higher than preview code 18 and previous stable code 19, allowing in-place upgrades with the same production package and signing key.
- All **865** tracked app/shared/sharedUi files from preview 8f5795750cddd253ec455bef40d3ff83467fa0fa are present and byte-identical. This includes every promoted UI component and the Home Locale import fix. Complete trees are checked, not only files that Git's merge automatically considered changed.
- Main retains the active Veyra backend, guest access, server preference migration, public invite host, app presence and external cron/12-minute keep-alive guide. No backend deployment or scheduler configuration is changed by this promotion.
- Preview history is preserved. Merge conflicts from the former stable-source revert are resolved without force-pushing or dropping required preview controls. Package identity, signing configuration, authentication routes, playback implementation and preferences are preserved from preview.

## Executed checks

- **68 focused Kotlin/JUnit tests passed.** These exercise selected real production sources, not the complete Android test suite.
- **14 backend migration/invite JUnit tests passed.** Seven policy tests overlap the focused suite. The parser harness uses narrow compile-time Android Intent/CODE_LENGTH adapters; it does not test Android activity dispatch or real network sockets.
- Settings preservation audit passed: **131 original functions**, original parameter order, **160 existing named UI action/value contracts**, and byte-identical authentication sources, MainActivity routing, preference persistence and launcher apply/rollback logic relative to the audited stable baseline.
- Production debug and release BuildConfig generation passed: VERSION_NAME 0.1.5, VERSION_CODE 20 and LISTEN_TOGETHER_SERVER https://veyra-listen-together.onrender.com.
- Complete app/shared/sharedUi trees are byte-compared to preview. Manifest XML, Git whitespace/conflict-marker checks and source ZIP integrity are checked before delivery.

## Limits

Full Android compilation was attempted but failed before Kotlin compilation at :app:stripNewPipeExtractorUtils because JitPack returned HTTP 403 for NewPipeExtractor v0.26.3. No TLS/checksum verification was bypassed, no dependency was removed, and no full Android build, complete Gradle unit suite, APK, signing, emulator/device review or GitHub release is claimed here.

The owner's prior successful 0.1.5 test and room-code generation report concerned the earlier build. That report is not treated as independent verification of every promoted preview UI, two-phone playback, upgrade or reconnection scenario. The source promotion is explicitly authorized; production release APK validation remains the owner's release step.

## Publishing

Build/sign with the existing production key. When creating the GitHub release, use tag **v0.1.5**, target **main**, and leave the prerelease option disabled. [Prepared release notes](../RELEASE_NOTES_0.1.5.md) can be pasted into its description. The source ZIP is an Android Studio project, not an installable APK.
