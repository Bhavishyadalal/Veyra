#!/usr/bin/env python3
"""Package staged/tracked project sources, including the mirrored backend."""
import argparse
import hashlib
import json
import re
from pathlib import Path
import subprocess
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("name", help="Output ZIP basename (no directory)")
    args = parser.parse_args()
    if Path(args.name).name != args.name or not args.name.endswith(".zip"):
        parser.error("Use a ZIP basename without directories")
    root = Path(__file__).resolve().parents[1]
    subprocess.run(["python3", str(root / "scripts/verify_backend_mirror.py")], check=True)
    paths = subprocess.check_output(["git", "ls-files", "-z"], cwd=root).decode().split("\0")
    excluded = {"downloads", "site", ".git", ".gradle", ".kotlin", "build", "bin",
                "node_modules", "dist", ".idea", "__pycache__"}
    manifest = []
    # Keep source downloads below GitHub's per-file limit without recompressing
    # or changing published packs. Superseded ZIP copies stay in Git; the legacy
    # v2 fixture remains available for the source project's compatibility test.
    catalog = json.loads((root / "themes/catalog.json").read_text())
    included_packs = {f'{entry["id"]}-v{entry["version"]}.zip' for entry in catalog["themes"]}
    included_packs.add("halloween-nocturne-v2.zip")
    # Runtime images already live byte-for-byte inside the included data packs.
    # Retain font/license build inputs; record reversible image deduplication.
    packed_images = {}
    packed_fonts = {}
    # Only optional authoring copies may be deduplicated. Every Gradle-generated
    # font input stays expanded; new punk authoring fonts can be restored from packs.
    gradle_fonts = {"themes/"+v for v in re.findall(r'"([a-z0-9-]+/fonts/[^"/]+\.ttf)"', (root/"app/build.gradle.kts").read_text())}
    for entry in catalog["themes"]:
        pack_rel = f'themes/packs/{entry["id"]}-v{entry["version"]}.zip'
        with zipfile.ZipFile(root / pack_rel) as source_pack:
            definition = json.loads(source_pack.read("theme.json"))
            for member, digest in definition["files"].items():
                optional_font = (definition["minAppCode"] >= 31 and Path(member).suffix.lower() == ".ttf"
                    and f'themes/{entry["id"]}/{member}' not in gradle_fonts)
                if Path(member).suffix.lower() in {".png", ".webp"} or optional_font:
                    data = source_pack.read(member)
                    if hashlib.sha256(data).hexdigest() == digest:
                        packed_images[f'themes/{entry["id"]}/{member}'] = {
                            "pack": pack_rel, "entry": member, "sha256": digest}
                        if optional_font: packed_fonts[digest] = {"pack": pack_rel, "entry": member, "sha256": digest}
    asset_catalog=json.loads((root/"asset-packs/catalog.json").read_text())
    for item in asset_catalog["packs"]:
        pack_rel=f'asset-packs/packs/{item["id"]}-v{item["version"]}.zip'
        with zipfile.ZipFile(root/pack_rel) as source_pack:
            definition=json.loads(source_pack.read("assets.json"))
            for member,digest in definition["files"].items():
                if Path(member).suffix.lower() in {".png",".ttf",".webp"}:
                    assert hashlib.sha256(source_pack.read(member)).hexdigest()==digest
                    packed_images[f'asset-packs/{item["id"]}/{member}']={"pack":pack_rel,"entry":member,"sha256":digest}
                    if member.endswith(".ttf"):packed_fonts[digest]={"pack":pack_rel,"entry":member,"sha256":digest}
            # Studio's public preview is the same byte image as the offline pack specimen.
            preview_member="previews/specimen.webp"
            packed_images[f'asset-packs/{item["id"]}/preview.webp']={"pack":pack_rel,"entry":preview_member,"sha256":definition["files"][preview_member]}
    for source in (root/"design/asset-fonts").glob("*.ttf"):
        digest=hashlib.sha256(source.read_bytes()).hexdigest()
        if digest in packed_fonts:packed_images[source.relative_to(root).as_posix()]=packed_fonts[digest]
    # The new font library is an authoring source, not a Gradle font-resource input.
    for source in (root/"design/punk-fonts").glob("*.ttf"):
        digest = hashlib.sha256(source.read_bytes()).hexdigest()
        if digest in packed_fonts: packed_images[source.relative_to(root).as_posix()] = packed_fonts[digest]
    omitted = []
    archive = root / "downloads" / args.name
    archive.parent.mkdir(exist_ok=True)
    with zipfile.ZipFile(archive, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for rel in sorted(filter(None, paths)):
            p = Path(rel)
            if p.parts[:2] == ("themes", "packs") and p.name.removesuffix(".sha256") not in included_packs:
                continue
            # Generated native study images remain in Git; ship their renderer, not
            # 36+ MB of screenshots in every Android Studio source download.
            if (p.parts[:2] in {("themes","previews"),("asset-packs","previews"),
                    ("design","home-preview"),("design","settings-preview")} and p.suffix.lower() in {".png",".webp"}):
                continue
            # Generated verification captures are documentation, not test fixtures or build inputs.
            if p.parts[:2] in {("verification","atelier-preview"),("verification","performance-preview"),("verification","reliability-preview")} and p.suffix.lower() in {".png",".webp"}:
                continue
            # Promotional media is available in Git; Android builds need the expanded
            # native resources, not duplicate website/media-kit campaign exports.
            if p.parts[0] == "media-kit" and p.suffix.lower() in {".png",".webp",".jpg",".jpeg"}:
                continue
            if p.parts[:3] == ("design","launcher","collections") and "previews" in p.parts and p.suffix.lower() in {".png",".webp"}:
                continue
            if any(part in excluded for part in p.parts):
                continue
            if p.name in {"local.properties", "key.properties", ".DS_Store", "banner.jpg", "upi_support.jpg"}:
                continue
            is_theme_pack = p.parts[:2] in {("themes", "packs"),("asset-packs", "packs")} and p.suffix.lower() == ".zip"
            if p.suffix.lower() in {".zip", ".apk", ".aab", ".jks", ".keystore", ".log"} and not is_theme_pack:
                continue
            if p.name.startswith(".env") and p.name != ".env.example":
                continue
            path = root / rel
            if not path.is_file():
                continue
            data = path.read_bytes()
            duplicate = packed_images.get(rel)
            if duplicate and hashlib.sha256(data).hexdigest() == duplicate["sha256"]:
                omitted.append({"path": rel, **duplicate})
                continue
            z.write(path, "Veyra/" + rel)
            manifest.append({"path": rel, "bytes": len(data), "sha256": hashlib.sha256(data).hexdigest()})
        z.writestr("Veyra/SOURCE_MANIFEST.json", json.dumps({"package": args.name, "files": manifest, "themeAssetsInPacks": omitted}, indent=2) + "\n")
    with zipfile.ZipFile(archive) as z:
        assert z.testzip() is None
        for entry in manifest:
            assert hashlib.sha256(z.read("Veyra/" + entry["path"])).hexdigest() == entry["sha256"]
        for rel in ("settings.gradle.kts", "gradlew", "gradle/wrapper/gradle-wrapper.jar",
                    "backend/main.go", "backend/SOURCE_MANIFEST.json",
                    "shared/src/jvmSharedMain/kotlin/app/veyra/music/data/backend/VeyraBackend.kt"):
            assert "Veyra/" + rel in z.namelist(), rel
    digest = hashlib.sha256(archive.read_bytes()).hexdigest()
    if archive.stat().st_size > 100 * 1024 * 1024:
        raise SystemExit("Source ZIP exceeds GitHub's 100 MiB file limit; do not commit it")
    archive.with_suffix(".zip.sha256").write_text(f"{digest}  {args.name}\n")
    print(json.dumps({"archive": str(archive), "bytes": archive.stat().st_size,
                      "sourceFiles": len(manifest), "deduplicatedThemeImages": sum(Path(e["path"]).suffix != ".ttf" for e in omitted), "deduplicatedOptionalFonts": sum(Path(e["path"]).suffix == ".ttf" for e in omitted), "sha256": digest}))


if __name__ == "__main__":
    main()
