# App migration plan — 0.1.5, after backend deployment

**Status: pending your actual deployed server URL.** Backend source and deployment files are prepared first. This stage does not bump either app version, change its default server, publish an APK or claim that existing installs are already independent of upstream services.

## Two app tracks

| Track | Starting point | Planned version | Planned version code |
| --- | --- | --- | --- |
| Preview | Current 0.1.4-beta1 UI/source | `0.1.5-beta1` | `18` |
| Stable | Existing stable 0.1.3 source, with the backend migration | `0.1.5` | `19` |

Preview is validated before stable is promoted. Higher stable code permits a normal upgrade from preview. Preview UI refinements are not silently merged into the stable baseline. Existing package identity, signing key, downloaded music and preferences must be preserved. Version labels alone do not establish stable readiness.

## First-party connections found

| Location in Android project | Existing connection | Required migration |
| --- | --- | --- |
| `app/build.gradle.kts` | Old Render default (`bitchord-listen-together.onrender.com`) | Actual Veyra Render HTTPS origin |
| `shared/.../data/presence/Presence.kt` and Android presence initialization | Independent `api.bitchord.kushagrasingh.in/api/presence` constant | Derive Veyra presence from its configured official backend, not from an arbitrary party custom-server choice |
| Android `data/listentogether/JamInviteLink.kt` | Old public invite origin/host | Veyra invite origin; retain `veyra://party/<CODE>?server=...` behavior |
| Shared `data/listentogether/JamInvite.kt` | Same old invite origin | Match Veyra invite construction/parsing |
| `app/src/main/AndroidManifest.xml` | Old verified invite host | Actual Veyra invite host, backed by correct public signing-certificate association |
| `web/app/src/party/listenTogether.ts` and `invite.ts` | Old backend and invite defaults | Veyra equivalents if the bundled web player is used |

`ListenTogether.init()` also restores a saved custom server before probing and clears/releases a stale saved membership. Changing a Gradle constant alone is insufficient: a previous managed address stored in preferences could still be used. Migrate only recognized old managed defaults, clear their stale code/token/health state without contacting the retired server, and preserve deliberate user-selected self-hosted servers. An old room cannot move between independent in-memory servers; listeners must create/join a fresh Veyra room.

Audit fallback, cached server status, reconnect jobs, release-stale-slot cleanup and incoming `server=` invite parameters. Default/fallback paths must no longer reach the old operator. Do not rebind an old room code to an unrelated new room silently. Legacy invite handling should explain that a new invite is required rather than sending requests to the retired host.

## What changes after you send the URL

1. Run the packaged `veyra-smoke` against your deployed HTTPS service. Confirm health identity, REST/WS paths, guest join, state/queue synchronization, host permissions, presence and valid public invite origin.
2. Apply the same endpoint/config/preference migration to both app tracks. Keep existing playback and optional Google account paths.
3. Update tests for invite parsing and URL normalization, preference migration/idempotence, stale-token cleanup, offline/fallback behavior and presence destination. Resolve the preview's documented cross-module test-location issue properly before claiming the full Android test suite passes.
4. If verified HTTPS App Links are wanted, configure the public production signing-certificate fingerprint on Render and verify `/.well-known/assetlinks.json`; update the app's manifest to the matching real host. Custom-scheme links remain useful independently.
5. Re-run production endpoint/branding checks, compile both Android tracks, and test real phones on separate networks: create/join, queue order, play/pause/seek, clock drift, host lock, reconnect, background/resume, leave/kick, Render restart and expired room handling.
6. Package separately labeled source ZIPs. Publish stable only after those checks; the current stable release remains available meanwhile.

## Branding and external services

Runtime app/service naming is Veyra. Existing package `app.veyra.music`, update repository and creator links are already Veyra-owned. The endpoint audit identifies remaining **app-operator services** to migrate; it does not imply that YouTube/Google, JioSaavn, lyrics providers, music artwork services, optional scrobbling/Discord integrations or user-added sources can be replaced by this room server. Those are distinct third-party product integrations, not dependencies on another music app's backend.

Original GPL/copyright attribution and dependency notices must remain in source/legal documentation. Renaming a fork or independently hosting its backend does not erase its origin. No old brand is introduced into new user-facing Veyra server pages.

## TURN/STUN screenshots

No current Android Listen Together client or reviewed backend implements WebRTC signaling/media. It uses HTTPS/WSS and each device obtains its own audio. The provided Metered/ExpressTurn credentials are neither required nor bundled, and cannot improve this protocol by being inserted into configuration. A future P2P audio feature would require a separate client protocol, signaling design and short-lived authenticated relay credentials; it is outside this compatible-backend migration.
