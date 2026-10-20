# GitHub storage housekeeping

New APK and test-report artifacts from the maintained Veyra workflows expire
after **7 days** (`retention-days: 7` on each artifact upload).
Download what you need before expiration. This does not change existing artifacts.

## Set the repository default in GitHub

1. Open **Bhavishyadalal/Veyra → Settings → Actions → General**.
2. Find **Artifact and log retention**.
3. Set **7 days** and save.

The repository setting covers logs and sets the default for uploads without an
explicit retention value. An organization policy can impose a lower maximum.
Workflow settings are already committed; the repository-wide UI setting still
needs the owner to save it. The workflow override does not change log retention.

## Existing downloads and usage

- To delete an old artifact: **Actions → completed run → Artifacts → trash icon**.
  Download it first if you still need it. Expiration/deletion removes its download.
- View available Actions storage/billing usage under your account's **Settings →
  Billing & licensing** (wording varies by account). Usage updates can be delayed.
- Gradle caches use Actions cache storage; they have their own eviction policy,
  separate from artifact retention. Inspect **Actions → Caches** when needed.
- The current build artifact contains architecture-specific APKs and a universal
  APK. Retention does not change their contents or reduce a single build's size.

## Repository policy

- Keep only current stable plus the newest three preview source ZIPs in downloads,
  with matching checksums. Do not commit APKs, build outputs or signing backups.
- Keep published APKs as GitHub Release assets. Release assets are separate from
  Actions artifacts; the 7-day rule does not expire them.
- Removing a committed ZIP from a branch does not remove its historical Git
  objects. A later history migration is a separate operation, not routine cleanup.
- No historical artifacts, Release assets, tags or Git history were deleted by
  this retention update. Actual remote storage usage was not measured.
