#!/usr/bin/env python3
"""Verify the backend snapshot against its standalone source manifest."""
import hashlib
import json
from pathlib import Path


def main():
    backend = Path(__file__).resolve().parents[1] / "backend"
    manifest = json.loads((backend / "SOURCE_MANIFEST.json").read_text())
    seen = set()
    for entry in manifest["files"]:
        rel = entry["path"]
        target = backend / rel
        if rel in seen or not target.resolve().is_relative_to(backend.resolve()):
            raise SystemExit(f"Invalid or duplicate manifest path: {rel}")
        seen.add(rel)
        data = target.read_bytes()
        if len(data) != entry["bytes"] or hashlib.sha256(data).hexdigest() != entry["sha256"]:
            raise SystemExit(f"Backend snapshot mismatch: {rel}")
    print(f"Backend manifest verified: {len(seen)} files")


if __name__ == "__main__":
    main()
