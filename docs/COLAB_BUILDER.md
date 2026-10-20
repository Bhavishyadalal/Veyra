# Veyra Colab backup builder

[Open Builder 3 in Colab](https://colab.research.google.com/github/Bhavishyadalal/Veyra/blob/main/tools/Veyra_Colab_Backup_Builder_3.ipynb)

[Download the notebook](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/tools/Veyra_Colab_Backup_Builder_3.ipynb)

Made by Bhavishya Dalal. This interactive builder is separate from GitHub Actions;
changing its options does not change or interrupt an Actions run.

1. Open the notebook in Colab, save a private copy, and select a CPU runtime.
2. Run the helper code cells above Step 1.
3. In Step 1 select **Preview 0.1.7-beta1** or **Stable 0.1.6**. Defaults are
   Production Release, ARM64, Java 17, two workers, and existing-key signing.
   Read the Android SDK terms and enable the licence checkbox if you agree.
4. Run Step 2 for source retrieval, then Step 3 for tools. You can alternatively
   select a custom public GitHub ZIP URL or upload a source ZIP. Official presets
   enforce their published SHA-256 sidecars; custom sources accept an optional
   expected checksum. Source code executes during the Gradle build: use trusted sources.
5. In Step 4 upload **Veyra_PRIVATE_Signing_Backup.zip** directly. Do not extract
   it, use the old TEST key, or enter passwords into public form fields. No
   GitHub repository secrets are needed for this Colab flow.
6. Run Step 5 to build, then Step 7 to download the APK and results ZIP. Step 6
   optionally copies public results to your Drive. Download before Colab expires.
7. Remove private signing files using the cleanup button. Clear all notebook
   outputs before sharing; keep your signing backup private.

The default expected certificate SHA-256 matches the published 0.1.5 APK:
`22b5a611d9c62ded21114c28074f4f25c378c2ee70866740623e49221198cb9a`.
Existing-key updates must pass that check; do not clear it to force an old TEST
key to work. New-key and unsigned modes require deliberately clearing the check
and cannot produce compatible updates to the existing installation.

The notebook preserves source versions, forces Release R8/resource shrinking,
and checks reports, package/version, native ABI, archive integrity and APK
certificate before marking an artifact ready. Optional tests are reported
separately. Stable 0.1.6 / code 22 can update preview code 21 with the same signing key; Android blocks older version codes, so
use the correct newer build or the separately installed Dev flavor for testing.

Colab signing uploads your key to your remote runtime; it is not offline signing.
Private signing files are excluded from the results ZIP and Drive output copy.
Runtime cleanup is best effort, not a guarantee of secure erasure.

## Verification performed

- All Python cells compile; execution outputs and counters are cleared.
- Current stable and preview version/SDK detection checked against repository code.
- Live preset checksum sidecars match the current source ZIPs.
- Synthetic ZIP checks cover nested backups, malformed fields, duplicate paths,
  traversal, and missing/multiple/empty keys.
- A disposable synthetic JKS exercised actual keytool alias, store-password,
  private-key-password and certificate checks; incorrect certificate rejected.
- No APK build or hosted Colab run was performed. End-to-end Gradle execution,
  R8 integration, ABI output and hosted download widgets still require a real run.

SDK setup requests `platform-tools`, not the retired `tools` package that caused
the earlier Actions failure. Service outages, dependencies, Colab quotas and
runtime memory can still prevent a build; failures are not silently bypassed.
