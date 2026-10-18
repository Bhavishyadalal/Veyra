# Veyra 0.1.2 source update — welcome artwork and creator links

Based on the existing 0.1.1 stable source. Version name 0.1.2, version code 15, no beta suffix. This is a source delivery, not a signed release or APK.

## Changes

- Welcome uses the original website campaign's glass portal and crystal waveform, optimized to two local WebPs totaling 133,420 bytes (about 131 KiB). The existing Coil loader decodes them asynchronously and caches them; no campaign-image network fetch, additional dependency, video, live blur or particle loop is introduced.
- A restrained champagne accent, deep neutral background, actual app icon/Super Malibu wordmark and short artwork settle animation unify the welcome presentation. Decorative artwork is omitted from TalkBack. Existing reduced-motion/system animation/foreground handling remains; revealing artwork uses graphics layers rather than per-frame bitmap generation.
- The name page hides its brand/artwork while the keyboard is up. Windows below 800dp use shorter artwork frames and tighter spacing to leave more room for actions. Both pages remain vertically scrollable for short screens and large fonts. Existing name limits, trimming, back handling, RTL page slides, account callbacks and optional guest completion are retained.
- Default English copy clarifies that account linking is optional and Listening Together works without an account; existing localized text remains in other locales. Primary labels are Continue and Start listening. No onboarding reset or forced repeat for existing users is added.
- Settings overview and global search now expose the owner's GitHub profile, app source and website source. Links use ordinary browser intents with the existing unavailable-handler message. The new source-code glyph matches the native settings icon style. Existing Instagram link is retained.
- The full standalone website source, assets, media gallery, notices and CI/Vercel configuration are pushed to https://github.com/Bhavishyadalal/Veyra-website. No website deployment was requested. The website production URL remains unset until supplied.

## Checks actually completed

- 50 focused Kotlin/JUnit regressions passed, zero failures. They exercise existing source migration, cache, navigation, paging, queue and other pure production helpers; they are not welcome UI tests.
- Kotlin PSI syntax parsed the changed welcome, Settings, native icon source and version build script: four files, zero syntax errors. PSI parsing is not Compose/Android type checking.
- Resource/branding audit: 114 XML documents, 22,619 localized entries and eight font references checked, zero errors.
- Android SDK 36 AAPT2 compiled all `app/src/main/res` resources successfully, including both WebPs and new strings. This is compilation of resources, not final Android resource linking.
- `git diff --check` passed. The source ZIP passes CRC and includes the Gradle wrapper, app/shared/sharedUi source and optimized welcome assets; downloaded archives, caches, build outputs and private SDK/signing configuration are excluded.
- Separate website checkout: locked dependency install, TypeScript check and production build passed. Production browser smoke covered main page, footer media gallery, category filtering, image-dialog dismissal, credits and privacy, with zero runtime errors. The identical website application code previously passed 54 browser tests with two device-specific skips; that full suite was not repeated for this repository-only copy.

## Android build blocker and practical limits

Attempted `:app:compileProdDebugKotlin :app:testProdDebugUnitTest` using installed JDK 17/Android SDK, two Gradle workers and the normal HTTPS proxy/CA trust. Dependency resolution stops at `:app:stripNewPipeExtractorUtils`: JitPack returns HTTP 403 for `com.github.TeamNewPipe:NewPipeExtractor:v0.26.3`. This occurs before Kotlin compilation. No dependency verification, TLS, repository or source bypass was used. The full Android compile and unit suite therefore remain unverified.

No APK/assemble task, emulator/physical-device UI test, native welcome screenshot or frame-time measurement was run. On-device follow-up should cover clean-install first run, name entry with the keyboard, long names, guest completion, optional sign-in, back/rotation, RTL, large fonts, TalkBack, disabled animations, narrow/landscape windows, both artwork loads and the three GitHub links. Existing installed users should retain settings/library and should not see welcome again.
