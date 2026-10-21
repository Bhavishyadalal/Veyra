# Optional app styles · 0.1.8-beta1

**Preview only · code 27 · 21 October 2026**

## Delivered behavior

Settings → Appearance → App style offers Original, Music Editorial, Listening Room and After Dark. Selection is persisted using the existing settings/backup mechanism with a safe Original fallback for unknown values. The picker stages a choice until Apply; cancel does not change the active style. Appearance reset also restores Original.

Custom styles select an exclusive visual system through the app-wide Material palette, typography, geometry and existing theme-surface composition local. The downloaded pack object and independent font choice stay saved. Theme Studio can still browse/download/delete packs, but Apply and theme deactivation are disabled during a custom style, including a final execution-time guard. Mode/accent/artwork-corner controls are inactive while style-owned. Fonts explain that Original must be selected to use independent choices. Launcher icon selection remains independent. Returning to Original resolves the saved pack and font again.

The three Home compositions use the existing live feed and playback data. Editorial adds a serif poster and burgundy feature panel; Listening Room uses a dedicated botanical backdrop and framed sleeve collages; After Dark adds condensed poster lettering and a cobalt-edged record. Recent GRID is compact, LIST/toggle remains available. Recommendation cards retain tap/long-press behavior, current-track feedback and all paginated items. Original's renderer is preserved. Four hoisted/saveable list states keep each Home's scroll separate and let the real top bar observe the active list.

Home uses the same host callbacks for offline guards, local downloads, playback/browse routing, refresh, errors, pagination, Google sign-in and customization. After Dark's record reads the loaded song cover independently of `isPlaying`: paused tracks retain it. Tapping that record opens the existing player without requesting playback. No loaded song and null/loading/error artwork use a static native abstract record. There is no stock portrait fallback.

Search, Explore, Library, detail pages, Settings and shared sheets inherit style palettes, fonts and scene surfaces. The cover/video-led player remains artwork-based; Editorial has a coordinated dark burgundy/cream player environment for legibility. Real artwork, Canvas, functional QR/share content and external provider pages retain their purpose. There is no replacement login or new backend.

## Assets and visual adaptation

[Asset provenance and font license](../design/app-styles/README.md). Listening Room's original OpenAI-generated art is bundled as one optimized ~100 KB WebP and copied on IO to a versioned local file for existing scene rendering; failure falls back to the forest palette. Cormorant Garamond/OFL sources generate the serif resource and packaged license; existing Manrope and Barlow Condensed resources/licenses are reused. Editorial paper and After Dark ambience/fallback are native static drawing.

These layouts follow the approved AI concepts. They are responsive native interpretations, not a pixel-identical screenshot skin: real content, account prompts, status/mini-player insets, missing data, display size and text scale change geometry. Narrow or large-text feature blocks stack; names wrap/ellipsize within bounded cards. English decorative poster text uses explicit LTR paragraph direction within RTL layouts; the side motto is omitted in RTL. Compact navigation labels scale up to 150% to keep all four readable in narrow bars; content/body text continues to follow the full system scale. Existing reduced-motion behavior remains; sleeve rotation and backgrounds are static.

## Actual checks

- **4 focused `AppStylePolicyTest` tests passed**: stored-name fallback/round-trip, downloaded-theme suspension and restoration identity, text/muted/button contrast >=4.5 across all three palettes, and no inherited downloadable portrait/asset in custom styles.
- **Release Android Kotlin compilation passed** after implementation. Existing unrelated compiler warnings remain; no APK assembly or clean-lint claim.
- **Native `renderAppStyles` passed**: three Home compositions, three matching secondary-component studies and three 320 dp / 200% font / RTL studies. Uses actual shared style/header/shelf/retained surface/navigation/control components; fixtures use existing theme artwork, not live music recommendations. Checks four selectable tabs, >=48 dp controls, zero automatic callbacks and one explicit Play callback.
- After Dark's same native scene additionally switches local cover files: **926 sampled pixels changed** in the record region. Its idle and missing-file fallback record regions matched, with no additional playback/action callback. Captures include cover A/B, idle and failed-cover states.
- Native captures were inspected; this caught oversized navigation labels and RTL poster punctuation, which were corrected. An early renderer fixture referenced a nonexistent config and was corrected. A header receiver qualification compile error during polish was corrected before final checks.
- Website build/type checks and **2 focused desktop/mobile download-link tests passed**. The website describes these as preview source features, not an available stable APK.

[Native study result record](../themes/previews/app-styles-0.1.8/RESULTS.txt). These studies are illustrative shared-component compositions, not full Android screen/device screenshots. They omit real account prompts, live data and Android-specific top/mini-player components. Attempts to fetch music-cover fixtures returned HTTP 403 in the environment; no blocked access was bypassed and local theme-art fixtures were used instead.

The preview source is staged before packaging and verified with `scripts/verify_archives.py`; only the permitted stable plus three preview archives are retained. Resulting commits and public source hash are recorded outside the archive to avoid a self-referential checksum.

## Limits

No APK was built; no GitHub Actions dispatched. Rebuild/install the preview APK to receive the styles. Stable branches/source and published packs are unchanged. There is no device proof of real-data rendering, process-restart preference/scroll restoration, TalkBack, gestures, playback continuity, launcher behavior or FPS; these remain device acceptance checks. Local checks are not a Vercel deployment verification or a guarantee of pixel-identical layouts on every device.
