# Veyra source downloads

## Website — 4.1.0 and Media kit — 1.0.0

[Download updated website source](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Website_Source_4.1.0.zip) · [SHA-256](Veyra_Website_Source_4.1.0.zip.sha256)

[Download artwork / media kit](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Media_Kit_1.0.0.zip) · [SHA-256](Veyra_Media_Kit_1.0.0.zip.sha256) · [Browse images on GitHub](../media-kit/README.md)

The website update adds static link-sharing metadata, browser icons and an animated artwork gallery. The separate media ZIP includes twelve exports, two original generated backgrounds, an image manifest and a local gallery: extract it and double-click `index.html`, with no npm install needed. The same files are inside the website ZIP at `public/media-kit/`. Actual app icon and Super Malibu wordmark are preserved.

The website URL is intentionally unset until deployment. Once known, set `siteUrl` in `social.config.mjs` (or build variable `SITE_URL`) and rebuild. Live messaging previews can then be checked. For website preview, extract `Veyra-Website`, run `npm ci --include=dev`, then `npm run dev`, or use the included Windows launcher. No APK/release or website deployment was performed.

## Previous website — 4.0.0 Refraction Edition

[Download the complete website source ZIP](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Website_Source_4.0.0.zip) · [SHA-256](Veyra_Website_Source_4.0.0.zip.sha256)

A standalone React/Vite showcase with an obsidian and smoked-chrome palette, four original generated artworks, liquid-glass controls, Motion transitions, eight public React Bits components, layered glass particles, a pointer-responsive camera, Syne/Bodoni display typography, an original interactive reel, phone preview and secret frequency dial. The ZIP includes its own README, local assets, source, tests, licenses, Windows launcher and Vercel configuration.

Extract the ZIP, open `Veyra-Website`, install Node.js 24 LTS, then double-click `START-WEBSITE.cmd` on Windows. Alternatively:

```sh
npm ci --include=dev
npm run dev
```

This is the website source for local review and later deployment to your separate website repository. It is not an Android Studio project or APK. The Pro vertical-reel source is not bundled; the reel is original Motion-based website code. Full provenance and validation reports are inside the archive.

[Download the full-resolution regenerated gateway artwork](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Glass_Gateway_4.0.png)

## Android — 0.1.1 Stable source

[Download the Android Studio source ZIP](https://github.com/Bhavishyadalal/Veyra/raw/refs/heads/main/downloads/Veyra_Android_Source_0.1.1_Stable.zip)

Extract the entire project and follow the repository's [Android Studio setup guide](../START_HERE.md). A source ZIP is not an installable APK. Published application builds, when available, belong under [GitHub Releases](https://github.com/Bhavishyadalal/Veyra/releases).

Earlier source archives remain available in this directory.
