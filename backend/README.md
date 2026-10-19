# Veyra Backend · 0.1.5

An independently hosted **Listen Together and app-presence service for Veyra**. Deploy this source in your own repository and Render account. It does not call the original app's backend.

The app migration is deliberately a second step: deploy this server, verify its HTTPS URL, then wire **stable 0.1.5** and **preview 0.1.5-beta1** to it. Current app source/version/defaults have not been switched by this backend preparation.

## What this service does

- Creates six-character listening rooms; guest and optional account-profile identities work without Google credentials at the server.
- Issues separate random membership tokens; private REST and WebSocket actions require those tokens.
- Synchronizes playback position, server-clock anchors, sequence numbers, shared queues and membership over REST + WebSockets.
- Supports host-only controls, member limits, queue deltas, reconnect grace and room expiration.
- Supplies Veyra presence open/close counts, public invite previews and Veyra-branded invite landing pages.
- Optionally serves Android App Links verification from public signing-certificate fingerprints.

It does **not** relay audio, implement WebRTC, fetch music streams or need TURN/STUN credentials. Each app streams its own music using its existing sources. The supplied relay screenshots are not configuration for this protocol; no relay credentials are bundled.

## Deploy on Render — separate repository

1. Extract `Veyra_Backend_0.1.5_Source.zip`. Upload the **contents of `Veyra-Backend`** to your new repository root: `go.mod`, `main.go`, `render.yaml`, packages and the remaining files should be directly there.
2. In Render choose **New → Web Service**, connect that repository, then use:

| Field | Value |
| --- | --- |
| Runtime | Go |
| Root Directory | Leave blank |
| Build Command | `go build -trimpath -ldflags="-s -w" -o bin/veyra-server .` |
| Start Command | `./bin/veyra-server` |
| Health Check Path | `/healthz` |
| Instance count | **1** |
| `GO_VERSION` | `1.27.2` |
| `JAM_TRUST_PROXY` | `true` |

3. After Render assigns the service URL, set **`JAM_PUBLIC_ORIGIN`** to that exact `https://your-service.onrender.com` origin, without a path/query/fragment, and redeploy. Do not use an invented domain. Until configured, invite links derive the origin from the request/proxy headers.
4. Set **`JAM_ALLOWED_ORIGINS`** to exact browser origins that should access the API. The supplied blueprint uses your current website, `https://veyra-website-mauve.vercel.app`. Native Android clients send no Origin and work without a browser entry. Do not use `*`.
5. Open `/healthz`: it should report `ok: true`, service `veyra-listen-together`, version `0.1.5` and protocol `veyra.v1`.
6. Run the deployment probe below, then send the service URL for app integration.

Alternatively, **New → Blueprint** can read the included `render.yaml`. It is designed for a repository containing the extracted backend at its root. If deploying from the Android monorepo instead, manually set Root Directory to `backend`; do not copy monorepo directory settings into the standalone deployment.

Render free services can sleep, cold-start and restart. Because rooms live in memory, every restart or deployment loses rooms and live counts. Use an always-on plan if uninterrupted listening is required. This implementation is single-instance; it does not provide shared state across replicas or persistent rooms.

## Verify your deployment

Install Go 1.27.2 or a compatible newer Go toolchain. From the backend repository root:

```sh
go mod verify
go vet ./...
go test -race -count=1 ./...
go run ./cmd/veyra-smoke -server https://YOUR-ACTUAL-SERVICE.onrender.com
```

The probe checks the Veyra deployment identity before creating one temporary room. It drives **two real WebSocket clients**, checks guest access, authorization, queue broadcast/reorder, track state, host-only enforcement, seek synchronization, queue resync and presence, then leaves its own memberships. It prints no membership tokens. Repeated probes are subject to the same creation limits as clients; wait for the rate-limit window if necessary. It does not test actual Android audio playback or mobile networks.

Local development:

```sh
go run .
# In another terminal:
go run ./cmd/veyra-smoke -server http://127.0.0.1:8000
```

The server reads process environment. `.env.example` documents settings but is **not automatically loaded**. Set values in Render's Environment panel or in your local shell. Keep local `.env` files untracked. No database, account password, private signing key or TURN credential is required.

## API and synchronization

[Protocol reference](PROTOCOL.md) explains every route, frame, control and clock/queue rule. [Android migration checklist](APP_MIGRATION_0.1.5.md) records all app endpoints that still need moving after deployment. [Verification](VERIFICATION.md) records what was actually exercised locally.

| Route | Purpose |
| --- | --- |
| `GET /`, `GET /healthz` | Service identity, version and health |
| `GET /api/time` | Server clock sample |
| `POST /api/parties` | Create room and host membership |
| `POST /api/parties/{code}/join` | Join/reclaim a device slot |
| `GET /api/parties/{code}` | Token-authenticated room snapshot |
| `GET /api/parties/{code}/preview` | Public limited member preview |
| `POST /api/parties/{code}/leave` | Revoke membership and close socket |
| `GET /ws/parties/{code}` | Authenticated WebSocket synchronization |
| `GET /invite/{code}` | Veyra invite page and `veyra://party` link |
| `POST /api/presence` | App-open heartbeat/close |
| `GET /api/stats/live`, `/api/stats/live/badge.svg` | Public aggregate counts |
| `GET /.well-known/assetlinks.json` | Optional Android App Links association |

## Environment reference

| Variable | Default | Meaning |
| --- | --- | --- |
| `PORT` | `8000` | Render supplies its own port automatically |
| `JAM_PUBLIC_ORIGIN` | Derived | Actual HTTPS origin for invite links |
| `JAM_TRUST_PROXY` | `false` | Enable only behind your trusted TLS proxy; Render blueprint sets true |
| `JAM_ALLOWED_ORIGINS` | Empty | Comma-separated exact browser origins |
| `JAM_MAX_MEMBERS` | `5` | Default devices per room; host can choose 2–10 |
| `JAM_MAX_PARTIES` | `50` | Concurrent room ceiling |
| `JAM_MAX_UPCOMING_QUEUE` | `25` | Upcoming-track ceiling |
| `JAM_MAX_QUEUE_LENGTH` | `26` | Total queue ceiling |
| `JAM_STATE_HEARTBEAT_MS` | `5000` | State heartbeat interval; minimum effective interval is 1s |
| `JAM_PLAY_LEAD_MS` | `350` | Future anchor lead for play/seek |
| `JAM_DISCONNECT_GRACE_MS` | `45000` | Dropped-socket slot grace |
| `JAM_EMPTY_PARTY_TTL_MS` | `120000` | Empty-room expiration |
| `JAM_PARTY_MAX_AGE_MS` | `43200000` | Maximum room lifetime |
| `JAM_CREATE_RATE_PER_MINUTE` | `2` | Room creations per client IP |
| `JAM_RATE_LIMIT_MAX_ENTRIES` | `10000` | Creation limiter IP-table ceiling |
| `JAM_CONTROL_RATE_PER_SECOND` | `25` | Per-member control budget |
| `JAM_FRAME_RATE_PER_SECOND` | `30` | Per-member frame budget, including ping/sync |
| `JAM_REQUEST_MAX_BYTES` | `16384` | REST request-body limit |
| `JAM_WEBSOCKET_MAX_BYTES` | `16384` | WebSocket frame limit |
| `JAM_CONNECTION_IDLE_MS` | `900000` | Application-frame idle timeout |
| `JAM_PRESENCE_INTERVAL_SEC` | `300` | Suggested app-open ping interval |
| `JAM_PRESENCE_GRACE_SEC` | `90` | Presence expiration grace |
| `JAM_PRESENCE_MAX_ENTRIES` | `200000` | Presence record ceiling |
| `JAM_PRESENCE_PINGS_PER_MINUTE` | `20` | Presence IP request budget |
| `JAM_PRESENCE_RATE_LIMIT_MAX_IPS` | `100000` | Presence limiter IP-table ceiling |
| `ANDROID_APP_SHA256_CERT_FINGERPRINTS` | Empty | Optional comma-separated **public** production signing-certificate SHA-256 fingerprints |

Numeric settings should remain positive; keep defaults until measured load justifies changes. Display names/user IDs are client assertions, not verified Google identities. Membership tokens authorize room actions. Public room codes are invitations; never treat a code as a private account credential.

## Android App Links

Once the real Render origin is known, the app manifest must declare that host. To enable verified HTTPS opening, set `ANDROID_APP_SHA256_CERT_FINGERPRINTS` to the public SHA-256 fingerprint of the certificate that signs your production APK, then verify `/.well-known/assetlinks.json`. The package is `app.veyra.music`. Get this **certificate fingerprint**, not the APK file's SHA-256, from your existing signing certificate (for example with Android SDK `apksigner verify --print-certs`). Do not send a private keystore or signing password to the backend.

When fingerprints are unset or invalid, that route returns 404; the Veyra custom-scheme invite remains available. Actual HTTPS App Links also need Android verification and cannot be proven before the final app/host/certificate are configured.

## Source and licence

Veyra backend modifications are by **Bhavishya Dalal**. This is a GPL-3.0 derivative of the existing synchronization backend; see [NOTICE](NOTICE), [LICENSE](LICENSE) and [upstream review](UPSTREAM.md). Runtime service names, invite pages, Go module and deployment defaults use Veyra. Required upstream attribution remains in legal documentation. Gorilla WebSocket's licence is in `licenses/`.

The standalone Render ZIP omits legacy VM provisioning helpers under `deploy/` and the older Python probes under `tools/`; they are not required for this deployment. Use the included Go smoke command. No binaries, APKs, private credentials or app-source ZIPs are included.
