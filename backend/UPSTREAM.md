# Provenance and compatibility review

Reviewed repository: https://github.com/kushagrasinghx/BitChord

Reference commit: `edc1e6f36febf71a118e649411d2fc21b70ead45`

Veyra already included a Go room/presence backend inherited from the app base, but its deployment guidance and app defaults still referenced another operator. This package prepares that implementation for your own Veyra deployment rather than routing requests through their running service.

After normalizing only local Go module import paths, these production implementations match the reviewed upstream source:

- `protocol/protocol.go`
- `party/party.go`
- `presence/presence.go`
- `codes/codes.go`
- `clock/clock.go`

That retains the queue, permissions, state and clock semantics expected by the app. Veyra's existing browser subprotocol and custom link scheme (`veyra.v1`, `veyra.token.`, `veyra://party`) are kept; clients of the original application's browser protocol are not advertised as interchangeable.

Veyra preparation adds the standalone Go module identity, Render/root packaging, licence notices, versioned health identity, shared production/test router, optional Android App Links association and deployed-service probe. It corrects replaced-socket cleanup, rejects revoked/replaced socket controls, revalidates membership after upgrade, closes sockets on leave, honors the configured default room capacity and avoids caching private JSON responses.

Legal attribution is retained in NOTICE and LICENSE. Runtime pages, logs and deployment defaults use Veyra. No dependency on the upstream operator's running servers is present in backend production Go code. App endpoint migration is staged separately until your service is deployed.
