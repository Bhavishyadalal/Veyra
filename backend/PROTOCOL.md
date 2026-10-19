# Veyra synchronization protocol · veyra.v1

The queue/playback business logic matches the reviewed upstream implementation, with Veyra service/invite identity and deployment-specific fixes. JSON field names remain compatible with Veyra's Android clients. This protocol sends **metadata and commands**, never audio.

## Identity and tokens

Create/join body: `userId`, `deviceId`, `displayName`, optional `avatarUrl`, `maxMembers`, `autoplayEnabled`. Guests supply a stable install pseudonym and optional nickname; Google sign-in is not required. A successful response supplies `code`, `token`, `you`, `party` and `serverMs`. Rejoining the same device replaces its token and reclaims its slot. Device IDs must remain unguessable installation IDs; they are not included in public member payloads.

Private REST and native WebSocket requests use `Authorization: Bearer <token>`. Tokens in URLs are not accepted. A browser uses subprotocols `veyra.v1` and `veyra.token.<token>`; the server selects only `veyra.v1`, never echoes the token, and checks the browser Origin against `JAM_ALLOWED_ORIGINS`. No Origin is normal for the native app.

The server cannot authenticate the claimed Google profile or display name. Tokens grant room membership, not verified real-world identity. Leave/kick revoke membership; replaced sockets cannot continue submitting controls or disconnect their replacement during cleanup.

## Client frames

- `ping {clientMs}` → `pong {clientMs, serverMs}`.
- `sync` → current playback state; `syncQueue` → queue payload.
- `report {positionMs,isPlaying}` is diagnostic; it does not rewrite the authoritative state.
- `control {action,...}` mutates the shared state, subject to token, frame/control budget and host permissions.

| Control action | Important fields |
| --- | --- |
| `play`, `pause` | Optional `positionMs` |
| `seek` | `positionMs` |
| `setTrack` | `track`, optional `positionMs`, `isPlaying`, `queueIndex` |
| `setQueue` | `queue`, `queueIndex` |
| `queueAdd` | `tracks` (or legacy single `track`), `playNext` |
| `queueRemove` | `videoId` or `index` |
| `queueMove` | `fromIndex`, `toIndex`, optional `videoId` for stale-index recovery |
| `queueClear`, `next`, `previous` | No extra fields |
| `setAutoplay` | `enabled` |
| `setHostOnlyControl` | `enabled`; host only |
| `setMaxMembers` | `maxMembers`; host only |
| `kick` | `memberId`; host only |

A track contains `videoId`, `title`, `artist`, optional `thumbnailUrl`, `durationMs`, and `fromAutoplay`. Stream URLs, audio quality, downloads and source selection remain local to each app.

## Server frames

- `welcome {you,party,serverMs}`: complete initial room snapshot, including queue.
- `state {playback,serverMs}`: current track, position/anchor, playing state, attribution and queue version/index/length. **No full queue list**.
- `queue {queue,serverMs}`: `{seq,index,items}`; sent before the corresponding state on queue mutations.
- `members {members,maxMembers,hostOnlyControl,serverMs}`.
- `activity {action,by,detail,atMs}`: room action feedback.
- `error {error,message}`: e.g. `host_only`, `rate_limited`, `queue_full`, invalid arguments.
- `bye {reason}`: `left` or `kicked`.

Playback and queue have **separate sequence counters**. Queue-only mutations do not need to increment playback `seq`. Clients compare the relevant counters, request `syncQueue` when stale, and do not apply older playback states.

## Clock rule

Each device estimates the server offset from ping round trips. For server time `now`, target position is `positionMs + max(0, now - anchorMs)` while playing, otherwise `positionMs`. Future anchors give recipients a buffering lead. Delayed frames still describe the same timeline; the server does not average device-reported playheads. Periodic heartbeats restate the current state.

Socket loss retains the membership for the configured grace interval. Reconnect with the membership token to receive a fresh snapshot. A replacement socket atomically becomes current, so old cleanup cannot mark it disconnected. Server restarts lose in-memory rooms: create a new room rather than expecting an old code/token to survive.

## Public endpoints

The public preview contains room code, host display name, member display/avatar/host flags and capacity, but not tokens, private IDs, queue or playback. The invite landing page is intentionally shareable and may display the current song. Presence endpoints carry a random per-install UUID, platform and open/closed flag; public stats are aggregate counts, not account identity.
