#!/usr/bin/env python3
"""Package staged/tracked project sources, including the mirrored backend."""
import argparse
import hashlib
import json
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
    archive = root / "downloads" / args.name
    archive.parent.mkdir(exist_ok=True)
    with zipfile.ZipFile(archive, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=6) as z:
        for rel in sorted(filter(None, paths)):
            p = Path(rel)
            # Generated native study images remain in Git; ship their renderer, not
            # 36+ MB of screenshots in every Android Studio source download.
            if p.parts[:2] == ("themes", "previews") and p.suffix.lower() in {".png", ".webp"}:
                continue
            if any(part in excluded for part in p.parts):
                continue
            if p.name in {"local.properties", "key.properties", ".DS_Store", "banner.jpg", "upi_support.jpg"}:
                continue
            is_theme_pack = p.parts[:2] == ("themes", "packs") and p.suffix.lower() == ".zip"
            if p.suffix.lower() in {".zip", ".apk", ".aab", ".jks", ".keystore", ".log"} and not is_theme_pack:
                continue
            if p.name.startswith(".env") and p.name != ".env.example":
                continue
            path = root / rel
            if not path.is_file():
                continue
            data = path.read_bytes()
            z.write(path, "Veyra/" + rel)
            manifest.append({"path": rel, "bytes": len(data), "sha256": hashlib.sha256(data).hexdigest()})
        z.writestr("Veyra/SOURCE_MANIFEST.json", json.dumps({"package": args.name, "files": manifest}, indent=2) + "\n")
    with zipfile.ZipFile(archive) as z:
        assert z.testzip() is None
        for entry in manifest:
            assert hashlib.sha256(z.read("Veyra/" + entry["path"])).hexdigest() == entry["sha256"]
        for rel in ("settings.gradle.kts", "gradlew", "gradle/wrapper/gradle-wrapper.jar",
                    "backend/main.go", "backend/SOURCE_MANIFEST.json",
                    "shared/src/jvmSharedMain/kotlin/app/veyra/music/data/backend/VeyraBackend.kt"):
            assert "Veyra/" + rel in z.namelist(), rel
    digest = hashlib.sha256(archive.read_bytes()).hexdigest()
    archive.with_suffix(".zip.sha256").write_text(f"{digest}  {args.name}\n")
    print(json.dumps({"archive": str(archive), "bytes": archive.stat().st_size,
                      "sourceFiles": len(manifest), "sha256": digest}))


if __name__ == "__main__":
    main()
