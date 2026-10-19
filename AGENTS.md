# Veyra maintenance rules

- Current stable Android source: `main`, version 0.1.5 / code 20. Current preview:
  `preview/0.1.6-beta1`, version 0.1.6-beta1 / code 21. Keep preview UI/features out
  of stable until the owner explicitly promotes them. Older branches are historical.
- For app/version/download changes, also update and push the separate website
  repository `Bhavishyadalal/Veyra-website`. This is the owner's standing request.
  Read its `AGENTS.md`; update `src/config.js` source/checksum/change-note links,
  current version labels and any affected external links. Keep stable and preview
  distinct, verify real app branch files, and never claim an unpublished APK exists.
  Website URL: `https://veyra-website-mauve.vercel.app/`. Verify build, type checks
  and relevant mobile/desktop links before pushing; do not claim Vercel deployment
  succeeded solely because Git push succeeded.
- Canonical server source is `Bhavishyadalal/Veyra-backend` on `main`. The app's
  `backend/` is a snapshot, not a separate deployment. Read `docs/BACKEND_SYNC.md`
  and verify its manifest before backend changes. Mirror future canonical fixes
  across every maintained app branch; do not resurrect the old VM deployment.
- Official rooms, invites and presence use
  `https://veyra-listen-together.onrender.com`. Old operator hostnames in the
  retirement policy/tests must remain blocked; never turn them back into defaults.
  Preserve intentional custom servers and never forward old membership tokens to
  a replacement service. TURN credentials do not belong to this REST/WebSocket server.
- Keep only the current stable source ZIP and newest three preview source ZIPs
  in `downloads/`, with matching SHA-256 sidecars. Website source lives in the
  separate `Veyra-website` repository; do not re-add old website/media ZIPs here.
  Generate source packages with `scripts/package_source.py` after staging source
  changes. Include the backend and its manifest, omit local config/secrets/builds.
- Check all remote branch heads explicitly (`git ls-remote --heads origin`). A
  restricted fetch refspec may expose only `origin/HEAD` by default.
- Archive cleanup removes files from branch tips, not Git history. Do not force
  push, rewrite history, delete tags or remove release assets without explicit scope.
- Record verification limits honestly: focused Kotlin tests and local WebSocket
  probes do not prove a full Android build, real device behavior or live Render uptime.
