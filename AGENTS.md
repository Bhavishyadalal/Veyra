# Veyra maintenance rules

- Current stable Android source: `main` and `stable/0.1.8`, version 0.1.8 / code 28.
  Promoted from preview commit `646e34b56295e1fbfaddcb3cbdc71fa5038eb196` at the owner's request.
  See `verification/STABLE-0.1.8-PROMOTION.md`. Historical stable 0.1.7 remains code 26 on `stable/0.1.7`.
  Includes Theme Studio, forty-six published themes and seventy-four optional launcher choices.
  `preview/0.1.7-beta1` remains 0.1.7-beta1 / code 25 as a historical preview.
  `stable/0.1.6` remains historical 0.1.6 / code 22; older branches retain their Android code and versions.
  Keep future preview features out of stable until the owner explicitly promotes them.
  Current development: `preview/0.1.9-beta1`, version 0.1.9-beta1 / code 35,
  based on completed stable commit `6e37aa4615bad01b895dc547454f79218d94175e`.
  Keep 0.1.9 changes out of main/stable until explicit promotion.
  Build code 32 adds five Punk Worlds full themes (62 beta packs, 123 roles each),
  original-color icon rendering, four pressed transport skins, colored display
  headings and bounded punk materials/seeks/motion. See `themes/PUNK_WORLDS.md`.
  Build code 35 completes missing theme/asset launcher editions (95 choices),
  all 62 theme bindings plus five asset bindings, an Asset packs filter and Home's
  live selected-icon display. See verification/LAUNCHER-COMPLETION-0.1.9-beta1.md.
  Existing theme/asset ZIPs stay immutable; Android aliases remain bundled.
  Source ZIPs keep native resources/masters and omit duplicate promotional media-kit images.
  Build code 34 reduces asset rendering overhead: shared bounded glyph requests,
  small two-worker warmup, off-main cached font families, native container outlines,
  remembered control geometry and single-page transitions. See
  verification/ASSET-PERFORMANCE-0.1.9-beta1.md; device FPS is not established.
  Build code 33 adds independent asset-pack overlays, separate GitHub catalog/storage,
  category switches and five Layer Lab packs (119 roles each). See asset-packs/README.md.
  Existing 62 theme packs are unchanged; asset overrides apply after custom-style
  isolation and preserve theme artwork/palette/selection. Keep catalogs distinct.
  Source packages omit reproducible Home/Settings preview studies as well as theme/
  asset gallery renders; optional asset font/image sources restore from their packs.
  Optional non-build theme/punk-library fonts may be deduplicated into included
  packs by source ZIP metadata; all Gradle font-resource inputs stay expanded.
  Preserve checksum/path validation and user-edit protection in the restorer.
  Build code 31 adds five Advanced Worlds packs (57 beta packs, 119 roles each),
  bounded native material/seek/motion presets and expanded semantic icon routing.
  See `themes/ADVANCED_WORLDS.md`; schema 4 requires minAppCode 31 and keeps
  immutable old packs, old-schema file limits and all byte/path/contrast checks.
  Build code 30 adds Margin Notes and the bounded sketch preset (52 beta packs,
  launcher aliases unchanged). Five data-only illustration worlds are documented in
  `themes/ILLUSTRATED_WORLDS.md`; preserve supported preset limits and immutable packs.
  See `themes/MARGIN_NOTES.md`. Beta catalogs use their
  own preview branch; strict pack/preview URL allowlisting remains in force.
  Source ZIP manifests may map identical runtime images to included theme packs.
  `scripts/restore_theme_sources.py` restores them without overwriting user edits;
  preserve expanded font/license inputs needed by Gradle and retain full Git assets.
  Preview UX additions are recorded in `verification/UX-0.1.9-beta1.md`: theme artwork
  overrides preserve pack files and custom-style isolation; pagination errors require
  explicit retry, and delayed playback recovery must still own the failed item/error.
  `preview/0.1.8-beta1` remains historical, code 27, with 74 launcher choices.
  Five Keepsake packs remain compatible with stable code 26, including its original
  256 px brand-icon validation. Their launcher aliases and UI polish are included in stable 0.1.8.
  Stable 0.1.8 also has independent fonts, the All icon filter and revised scene/preview
  rendering: see `verification/APPEARANCE-0.1.8-beta1.md`. The actual Android
  destination background-layer correction is recorded in `verification/BACKGROUNDS-0.1.8-beta1.md`;
  use the real retained wrapper when checking scene visibility. Deep sheets, artist-header
  blending and single-frame connected controls are recorded in
  `verification/DEEP-THEME-POLISH-0.1.8-beta1.md`. Font resources and OFL
  assets are generated from existing licensed theme sources; keep those sources
  in source ZIPs and avoid duplicating font binaries.
  Stable 0.1.8 also has three optional app styles alongside Original; see
  `verification/APP-STYLES-0.1.8-beta1.md` and its refinement
  `verification/APP-STYLES-POLISH-0.1.8-beta1.md`. Custom styles suspend downloaded
  theme/font visuals without replacing their saved selections. Preserve the
  generated-resource font/license inputs under `design/app-styles/`.
  Experience stage 1 adds the immersive player, five widgets, personalization,
  imported fonts, network controls and backup preview; see
  `verification/EXPERIENCE-STAGE-1-0.1.8-beta1.md`. The approved roadmap is
  `docs/roadmap/BETA-0.1.8-EXPERIENCE.md`; preserve every pending item and never
  report the whole roadmap complete based on this first checkpoint.
  Stable 0.1.8 and the completed preview include the theme brand-artwork bounds correction;
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
