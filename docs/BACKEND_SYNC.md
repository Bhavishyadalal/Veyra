# Backend source mirror

The app repository's `backend/` directory is a byte-for-byte snapshot of all
41 tracked files from [Veyra-backend](https://github.com/Bhavishyadalal/Veyra-backend)
at commit [`6fdbd5082d79f179c49fb75c59c55c7695c7fd0a`](https://github.com/Bhavishyadalal/Veyra-backend/commit/6fdbd5082d79f179c49fb75c59c55c7695c7fd0a).
It replaces the older backend previously retained here. Licensing and provenance
remain included in the mirror.

## Deployment ownership

- **Render source:** `Bhavishyadalal/Veyra-backend`, branch `main`.
- **Service:** https://veyra-listen-together.onrender.com
- **Health:** https://veyra-listen-together.onrender.com/healthz
- **Scheduled keep-alive:** the standalone backend repository's GitHub Actions
  workflow runs every 12 minutes on a best-effort basis. External HTTP cron
  configuration is documented in [`backend/CRON_SETUP.md`](../backend/CRON_SETUP.md).

Updating this mirror does not redeploy Render or start another schedule. Files
under `backend/.github/workflows/` preserve the standalone repository's workflows;
GitHub does not execute nested workflows in this app repository. The app's active
`Check` workflow builds and tests `backend/` using its own working-directory setting.
The obsolete Oracle deployment workflow and VM deployment helpers were removed.

The mirrored `render.yaml` assumes a standalone repository root. Continue using
the existing standalone deployment. To intentionally deploy from this monorepo
instead, configure the Render service's Root Directory as `backend`.

## Verify locally

From the app repository root:

```sh
python3 scripts/verify_backend_mirror.py
cd backend
go mod verify
go vet ./...
go test -race -count=1 ./...
go build ./...
```

Use Python 3.9+ and Go 1.27.2. The source manifest checks every mirrored source
file. The Go checks exercise the backend independently of Android Studio.

Future backend edits should be developed and deployed in `Veyra-backend` first,
then mirrored here with a new commit reference and refreshed manifest. This is a
snapshot, not an automatic cross-repository deployment pipeline.

## Android tracks

This synchronization keeps stable Android **0.1.5 / code 20** unchanged. The
**0.1.6-beta1 / code 21** Android work remains on `preview/0.1.6-beta1`.
Both Android tracks already target the same Veyra service. No APK or release is
created by this backend source update. The stable source ZIP is refreshed to include
this same backend snapshot; historical source archives are preserved.
