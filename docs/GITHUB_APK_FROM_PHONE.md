# Build Veyra from your phone — existing workflow

This guide uses the current **Android Release APK** workflow in `.github/workflows/android.yml`. It builds repository code selected by branch; it does **not** accept a source ZIP URL. No build has been triggered by adding this guide.

Want interactive architecture/signing/source options instead? Use the separate
[Colab Backup Builder 3](COLAB_BUILDER.md). Its options do not change this workflow.

## One-time private signing setup

The owner supplied a private backup whose selected certificate matches the published 0.1.5 APK. Its public SHA-256 is `22b5a611d9c62ded21114c28074f4f25c378c2ee70866740623e49221198cb9a`. Private key bytes and credentials are not included here. Matching the certificate is separate from successfully validating the private-key password during a build.

1. Keep your private backup ZIP on your own device. No manual extraction is needed.
2. Download `tools/signing-secret-helper.html` from this repository. Save it and open the local file in a browser, then select the private backup ZIP directly. ZIP support is bundled in the HTML (fflate 0.8.2, MIT); no external script is fetched. It reads only the signing keystore and credential entries, encodes the JKS bytes, and provides Copy name / Copy value buttons for each secret. Values start masked, with Show/Hide and Clear controls. It does not upload or persist the inputs; no online converter is needed.
3. In GitHub open **Bhavishyadalal/Veyra → Settings → Secrets and variables → Actions**. Use a mobile browser’s Desktop site option if Settings is hidden.
4. Add four **repository secrets**, copying each helper value into the matching name: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. If a secret already exists, update it rather than inventing another name. Never commit the files or passwords. These are the names the existing workflow reads; their presence in repository Settings has not been independently checked.
5. Close the helper after setup and keep the private backup in your own protected storage.

`KEYSTORE_BASE64` is the encoding of the keystore file, **not the ZIP**. The remaining three values come from JSON fields `store_password`, `alias` and `key_password`. Preserve their exact values. `LASTFM_API_KEY` and `LASTFM_SECRET` are separate optional feature credentials, not signing passwords.

## Each build

1. Open **Veyra → Actions → Android Release APK**.
2. Tap **Run workflow**. Under **Use workflow from**, choose `main` for stable 0.1.6. Choose `preview/0.1.7-beta1` for Theme Studio beta testing. Start a new run to build the latest branch commit.
3. Tap the final **Run workflow** button once. GitHub runs `:app:assembleProdRelease` on its own Ubuntu runner; you can close your browser. R8 can take time. The source’s beta/stable version labels are preserved.
4. Return to the run. Wait for a green completion check, then scroll to **Artifacts → veyra-prod-release-signed**. Download the artifact ZIP while signed into GitHub.
5. Extract the artifact ZIP. For the owner’s ARM64 phone, choose the APK whose filename contains `arm64-v8a`. The current source also creates other ABI and universal outputs. Install it using Android’s package installer; allow installation from the browser/file manager if asked. Do not uninstall the existing app just to work around a signing error.

A red run means inspect the failed step’s log. A downloaded artifact is not automatically a GitHub Release. The current workflow has no ZIP URL/architecture form and does not automatically compare the finished APK certificate with the public fingerprint above. It is the existing basic build path, not the fully hardened reusable ZIP builder described in the earlier handoff. Missing secrets, signing credentials, dependency access (including JitPack), runner resources and job limits can still prevent completion. No finish time or quota allowance is guaranteed.
