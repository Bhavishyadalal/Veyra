#!/usr/bin/env python3
"""Validate retained source archives, checksums and their backend snapshots."""
import hashlib
import json
from pathlib import Path
import zipfile

root = Path(__file__).resolve().parents[1]
expected = {
    "Veyra_Android_Source_0.1.5_Stable.zip",
    "Veyra_Android_Source_0.1.4-beta1.zip",
    "Veyra_Android_Source_0.1.5-beta1.zip",
    "Veyra_Android_Source_0.1.6-beta1.zip",
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
        assert "Veyra/gradle/wrapper/gradle-wrapper.jar" in z.namelist(), name
        assert "Veyra/settings.gradle.kts" in z.namelist(), name
        assert not any(p.endswith((".zip", ".apk", ".jks", ".keystore")) for p in paths), name
    print(f"Verified {name}: {len(entries)} source files, current backend and matching SHA-256")
