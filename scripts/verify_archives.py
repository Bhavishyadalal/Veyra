#!/usr/bin/env python3
"""Validate retained source archives, checksums and their backend snapshots."""
import io
import hashlib
import json
from pathlib import Path
import zipfile
from source_pack_library import LIBRARY,decode

root = Path(__file__).resolve().parents[1]
expected = {
    "Veyra_Android_Source_0.1.9_Stable.zip",
    "Veyra_Android_Source_0.2.0-beta1.zip",
    "Veyra_Android_Source_0.1.8-beta1.zip",
    "Veyra_Android_Source_0.1.9-beta1.zip",
}
actual = {p.name for p in (root / "downloads").glob("*.zip")}
if actual != expected:
    raise SystemExit(f"Archive retention mismatch: missing={expected-actual}, extra={actual-expected}")
sidecars = {p.name.removesuffix(".sha256") for p in (root / "downloads").glob("*.zip.sha256")}
if sidecars != expected:
    raise SystemExit("Archive checksum sidecars do not match the retained set")
backend = json.loads((root / "backend/SOURCE_MANIFEST.json").read_text())
for name in sorted(expected):
    path = root / "downloads" / name
    digest = hashlib.sha256(path.read_bytes()).hexdigest()
    if path.with_suffix(".zip.sha256").read_text().strip() != f"{digest}  {name}":
        raise SystemExit(f"Checksum mismatch: {name}")
    with zipfile.ZipFile(path) as z:
        assert z.testzip() is None, name
        manifest = json.loads(z.read("Veyra/SOURCE_MANIFEST.json"))
        packed = decode(z.read("Veyra/" + LIBRARY), manifest["packedArchives"]) if manifest.get("packedArchives") else {}
        def read_source(path): return packed[path] if path in packed else z.read("Veyra/" + path)
        entries = manifest["files"]
        paths = [e["path"] for e in entries]
        assert len(paths) == len(set(paths)), name
        assert set(z.namelist()) == {"Veyra/" + p for p in paths} | {"Veyra/SOURCE_MANIFEST.json"}, name
        for entry in entries:
            data = z.read("Veyra/" + entry["path"])
            assert len(data) == entry["bytes"], (name, entry["path"])
            assert hashlib.sha256(data).hexdigest() == entry["sha256"], (name, entry["path"])
        assert z.read("Veyra/backend/SOURCE_MANIFEST.json") == (root / "backend/SOURCE_MANIFEST.json").read_bytes(), name
        for entry in backend["files"]:
            assert z.read("Veyra/backend/" + entry["path"]) == (root / "backend" / entry["path"]).read_bytes(), (name, entry["path"])
        for image in manifest.get("themeAssetsInPacks", []):
            mapped = Path(image["path"])
            assert not mapped.is_absolute() and ".." not in mapped.parts
            assert mapped.parts[0] in ("themes","asset-packs") or (mapped.parts[:2] in (("design","punk-fonts"),("design","asset-fonts")) and len(mapped.parts)==3 and mapped.suffix==".ttf")
            assert image["path"] not in paths
            assert (image["pack"] in paths or image["pack"] in packed) and image["pack"].startswith(("themes/packs/","asset-packs/packs/"))
            with zipfile.ZipFile(io.BytesIO(read_source(image["pack"]))) as pack:
                assert hashlib.sha256(pack.read(image["entry"])).hexdigest() == image["sha256"]
        assert "Veyra/gradle/wrapper/gradle-wrapper.jar" in z.namelist(), name
        assert "Veyra/settings.gradle.kts" in z.namelist(), name
        assert not any(p.endswith((".apk", ".aab", ".jks", ".keystore")) for p in paths), name
        for rel in (p for p in paths + list(packed) if p.endswith(".zip")):
            assert rel.startswith(("themes/packs/","asset-packs/packs/")), (name, rel)
            data = read_source(rel)
            expected_hash = z.read("Veyra/" + rel + ".sha256").decode().split()[0]
            assert hashlib.sha256(data).hexdigest() == expected_hash
            with zipfile.ZipFile(io.BytesIO(data)) as pack:
                assert pack.testzip() is None
                manifest_name = "assets.json" if rel.startswith("asset-packs/") else "theme.json"
                theme = json.loads(pack.read(manifest_name))
                assert set(pack.namelist()) == set(theme["files"]) | {manifest_name}
                assert len(pack.namelist()) <= (129 if manifest_name=="assets.json" or theme["schema"] == 4 else 49)
                for asset, digest in theme["files"].items():
                    assert not asset.startswith("/") and ".." not in asset.split("/")
                    assert asset.endswith((".webp", ".png", ".ttf", ".txt"))
                    assert hashlib.sha256(pack.read(asset)).hexdigest() == digest
    print(f"Verified {name}: {len(entries)} source files, current backend and matching SHA-256")
