# Beta 0.1.8 experience roadmap

Owner-approved scope, recorded 2026-10-21. Work belongs only to preview/0.1.8-beta1; stable remains unchanged.

Every item requires working UI/logic and appropriate verification before it is marked complete. Existing features are extended, never duplicated or silently replaced. No character/gyro animations. No unmeasured FPS, battery or device-compatibility claims.

| Area | Approved scope | Status |
| --- | --- | --- |
| Immersive | Song, theme, blended and minimal backgrounds; cover sizing; positioned/full lyrics; gestures; controls; exit; accessibility; lifecycle | Implemented in stage 1; Android window/lifecycle and physical gestures require device verification |
| Home | Speed Dial, shortcut tiles, greeting, Quick Picks radio option | Implemented in stage 1; Speed Dial uses recent/provider shelves, not a new predictive model |
| Navigation | Ordered/hidden tabs, startup destination, local tab, bar/label styles, hide on scroll | Stage 1 adds ordered/hidden destinations, startup, labels and non-glass bar shapes; configurable hide-on-scroll remains pending |
| Mini player | Card/slim/pill; skip buttons; progress variants | Stage 1 implements non-glass mini-player shapes, Next-button visibility and progress; glass retains its integrated player; additional skip layouts pending |
| Player | Cover sizing, transport/scrubber styles, output visibility, pause artwork treatment; seek gestures and duration display | Stage 1 implements transport/scrubber choices, caption/pause treatment, seeking and duration; normal-player cover presets and physical gesture verification pending |
| Typography | Independent imported app/lyrics fonts, lyrics sizing/alignment, headline/card sizing | Stage 1 adds bounded validated font imports, lyrics sizing/centering; headline/card presets and font imports on devices pending |
| Smart offline | Opt-in automatic picks, storage and network policy, rotation protecting manual saves | Pending |
| Offline management | Automatic/manual views, bulk retry/cancel, lyrics repair, saved motion covers | Pending |
| Data Saver | Smaller images, bounded prefetch, cellular motion control | Implemented in stage 1; shared stream resolutions needed by playback are retained |
| Preload | Opening/whole next/next several with cellular permission | Implemented in stage 1 with serial byte fetching, existing source-pinning rules and network policy; live network measurements pending |
| Local music | Existing groups extended with favourites, recency, section order, writing lyrics to existing files | Pending |
| Playlists | Genre filter/group, local covers, description/privacy editing, bulk actions | Pending |
| Artist/album | Per-artist title typography, optional page treatment, reliable popular-track marks | Pending |
| Podcasts | Shows/episodes, follows, resume and episode playback controls | Pending |
| Sharing | Consistent song/album/artist/playlist cards and private-playlist handling | Pending |
| Nearby listening | Optional LAN/hotspot discovery alongside internet rooms | Pending |
| Widgets | Compact/artwork/disc/lyrics/large layouts, theme-aware appearance, resize/accessibility, cached event-driven updates | Five stage-1 providers registered: Sleeve, Vinyl, Studio Strip, Lyrics, Listening Studio; bounded caches and conflated updates; Android launcher/resize/restart/battery verification pending |
| Android Auto | Browse/action parity; opt-in parked/passenger lyric cards with normal metadata | Pending |
| Convenience | Search keyboard, content-country override, import guidance and refresh | Stage 1 adds keyboard preference and country override/refresh; import guidance expansion pending |
| Backup | Imported fonts/pictures/appearance; restore preview; exclude account secrets | Stage 1 adds font assets, appearance preferences, bounded schema-2 documents and hash-checked restore preview; custom picture assets pending |

## Quality gates

- Preserve accounts, queue, offline files, existing themes, internet rooms and Original's saved preferences.
- New features are optional and have safe defaults/fallbacks. Reuse shared implementations.
- Verify cancellation, offline states, rotation, large text, rapid switching and process restoration where test infrastructure supports them.
- Device-only claims remain pending until measured. No workflow dispatch or APK publication is implied by source compilation.
- Inspect third-party licensing before reuse. Prism is a reference, not an architecture drop-in.

## Stage 1 delivery

This checkpoint implements the shared appearance/player foundation and five new widget providers. **It does not complete the entire roadmap.** Smart Offline, richer offline tools, local-file lyric writing, playlist/artist extensions, podcasts, sharing cards, nearby sessions and Auto extensions remain pending as shown above. Existing implementations in those areas are preserved; no placeholder switches claim otherwise.

See [stage-1 verification](../../verification/EXPERIENCE-STAGE-1-0.1.8-beta1.md). Later stages should continue from this checklist rather than reconstructing the conversation.

Reference reviewed: [Prism](https://github.com/Yigder/Prism/tree/6e6d7642646e167ad64eec8dd0d80e97fc3d0608). New implementation is integrated with Veyra's existing playback, theme, lyric and widget infrastructure.
