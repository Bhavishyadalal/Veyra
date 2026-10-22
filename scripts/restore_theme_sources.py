#!/usr/bin/env python3
"""Restore optional editable theme images from a source ZIP's included data packs.
Never overwrites an existing file. App builds do not require this step.
"""
import hashlib
import io
import json
from pathlib import Path
import zipfile


def restore(root):
    root=Path(root).resolve()
    manifest=json.loads((root/'SOURCE_MANIFEST.json').read_text())
    restored=0
    for item in manifest.get('themeAssetsInPacks',[]):
        path=Path(item['path']); pack=Path(item['pack']); member=Path(item['entry'])
        if (path.is_absolute() or pack.is_absolute() or member.is_absolute() or
            any('..' in p.parts for p in (path,pack,member)) or
            len(path.parts)<4 or path.parts[0]!='themes' or pack.parts[:2]!=('themes','packs') or
            path.suffix not in ('.png','.webp') or path.parts[2:]!=member.parts):
            raise ValueError('Invalid theme source mapping')
        target=(root/path).resolve()
        if not target.is_relative_to(root) or not (root/pack).resolve().is_relative_to(root):
            raise ValueError('Theme source path escapes the project')
        if target.exists(): continue # Preserve user edits, even if their checksum differs.
        with zipfile.ZipFile(root/pack) as archive:
            info=archive.getinfo(member.as_posix())
            if not 0 < info.file_size <= 8*1024*1024: raise ValueError('Invalid image size')
            data=archive.read(info)
        if hashlib.sha256(data).hexdigest()!=item['sha256']: raise ValueError('Theme image checksum mismatch')
        target.parent.mkdir(parents=True,exist_ok=True)
        with target.open('xb') as out: out.write(data)
        restored+=1
    return restored


if __name__=='__main__':
    print(f'Restored {restore(Path(__file__).resolve().parents[1])} theme images; existing files preserved.')
