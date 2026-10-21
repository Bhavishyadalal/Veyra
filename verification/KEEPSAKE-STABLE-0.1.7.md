# Keepsake · Stable 0.1.7 compatibility

The five Keepsake packs—Patchwork Picnic, Clover Requiem, Sunroom Mixtape, Concrete Afterhours and Matcha Mischief—are published in the shared catalog for **stable 0.1.7 / code 26** and beta 0.1.8.

They use existing schema 2/3 fields, fonts, functional glyphs and timeline styles. Every new non-scene image is at most **256×256**, including Home brand artwork. The packs therefore stay within the original stable image-dimension rule as well as the later correction. Each targets minAppCode 26; no beta-only engine or launcher alias is referenced by a pack.

On the existing stable app, open Theme studio and tap **Refresh**, then download and apply. A successful install should offer Apply and appear under Downloaded. These five themes do **not** require rebuilding stable solely to add their data. This is distinct from the [earlier 512 px installation correction](THEME-INSTALL-0.1.7.md), which still needs an updated APK for affected older packs.

**New launcher choices and UI polish are beta-only.** Stable retains all 69 existing launcher choices. The new theme Home artwork works inside the app; no new Android launcher alias or matching-icon prompt is added to stable.

## Verification

- 17 ThemePackTest cases pass on the stable source, covering the 46-entry bundled/published catalog, all pack installs, hashes/schema/path validation and explicit code-26 installation of the new five packs.
- The same test suite on beta checks the five packs with code 26 and the original 256 px non-scene bound. Beta's four launcher tests retain every previous choice and validate 74 total.
- Stable Android Kotlin, shared UI, launcher manifest/resources and version metadata are byte-for-byte unchanged from `becb3f02eee0c44b1e07ba4c08715224740109bf`; only bundled catalog data changes under Android main sources. All 98 previous published pack/sidecar files retain their bytes.
- New-theme native Home/player/RTL studies were produced on beta using the shared engine; these are component studies, not stable device screenshots.

Source ZIPs are refreshed after staging and checked with `scripts/verify_archives.py`. Published theme archives are immutable; source archives omit superseded pack ZIP duplicates while retaining current packs, extracted assets/licenses and the compatibility-test fixture.

No stable APK build, signing, Actions run, device installation or release publication is included. Real-device network download, font/image decoding, Apply, offline persistence, TalkBack and launcher behavior remain unverified. Existing lint/translation limitations from the [stable promotion](STABLE-0.1.7-PROMOTION.md) remain.

[Collection and pack downloads](../themes/KEEPSAKE.md) · [Beta changes and studies](KEEPSAKE-0.1.8-beta1.md)
