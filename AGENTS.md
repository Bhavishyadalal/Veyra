# Veyra maintenance rules

- Current stable Android source: `main` and `stable/0.1.7`, version 0.1.7 / code 26.
  Promoted from preview commit `466433e6e8f29666af1cfb012b14b4a8c77b7ad7` at the owner's request.
  Includes Theme Studio, forty-six published themes and sixty-nine optional launcher choices.
  `preview/0.1.7-beta1` remains 0.1.7-beta1 / code 25 as a historical preview.
  `stable/0.1.6` remains historical 0.1.6 / code 22; older branches retain their Android code and versions.
  Keep future preview features out of stable until the owner explicitly promotes them.
  `preview/0.1.8-beta1` is 0.1.8-beta1 / code 27 with 74 launcher choices.
  Five Keepsake packs remain compatible with stable code 26, including its original
  256 px brand-icon validation. Their new launcher aliases and UI polish are preview-only.
  Preview also has independent fonts, the All icon filter and revised scene/preview
  rendering: see `verification/APPEARANCE-0.1.8-beta1.md`. The actual Android
  destination background-layer correction is recorded in `verification/BACKGROUNDS-0.1.8-beta1.md`;
  use the real retained wrapper when checking scene visibility. Deep sheets, artist-header
  blending and single-frame connected controls are recorded in
  `verification/DEEP-THEME-POLISH-0.1.8-beta1.md`. Font resources and OFL
  assets are generated from existing licensed theme sources; keep those sources
  in source ZIPs and avoid duplicating font binaries.
  Preview also has three optional app styles alongside Original; see
  `verification/APP-STYLES-0.1.8-beta1.md`. Custom styles suspend downloaded
  theme/font visuals without replacing their saved selections. Preserve the
  generated-resource font/license inputs under `design/app-styles/`.
  Stable and this new preview include the theme brand-artwork bounds correction;
  see `verification/THEME-INSTALL-0.1.7.md` before changing theme validation.
  Promotion does not establish APK publication, device validation or clean lint: see
  `verification/STABLE-0.1.7-PROMOTION.md` and the preserved submission-review inventory.
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
  Generated images under `themes/previews/` remain in Git and the gallery; omit
  those study renders from source ZIPs, retaining their renderer and theme assets.
- Check all remote branch heads explicitly (`git ls-remote --heads origin`). A
  restricted fetch refspec may expose only `origin/HEAD` by default.
- Archive cleanup removes files from branch tips, not Git history. Do not force
  push, rewrite history, delete tags or remove release assets without explicit scope.
- Record verification limits honestly: focused Kotlin tests and local WebSocket
  probes do not prove a full Android build, real device behavior or live Render uptime.

- Owner delegates ongoing GitHub storage housekeeping: set new Actions artifacts
  to 7-day retention, keep APKs out of Git commits, and follow the existing
  stable/preview source-ZIP cap. Preserve private signing backups outside Git.
  Prefer Release assets for published APKs. Do not delete release assets, tags,
  current user downloads, or rewrite Git history under general housekeeping.
  Retention changes affect new artifacts; do not claim old artifacts were purged
  or account-level storage was checked without direct evidence.
