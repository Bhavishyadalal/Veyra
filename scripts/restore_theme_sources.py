#!/usr/bin/env python3
"""Restore optional editable theme images and non-build font sources from a source ZIP's included data packs.
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
    from restore_source_packs import restore as restore_packs
    restore_packs(root)
    restored=0
    for item in manifest.get('themeAssetsInPacks',[]):
        path=Path(item['path']); pack=Path(item['pack']); member=Path(item['entry'])
        theme_source = len(path.parts)>=4 and path.parts[0] in ('themes','asset-packs') and path.parts[2:]==member.parts and path.suffix in ('.png','.webp','.ttf')
        punk_font = len(path.parts)==3 and path.parts[:2] in (('design','punk-fonts'),('design','asset-fonts')) and path.suffix=='.ttf' and member.parts==('fonts',path.name)
        if (path.is_absolute() or pack.is_absolute() or member.is_absolute() or
            any('..' in p.parts for p in (path,pack,member)) or
            pack.parts[:2] not in (('themes','packs'),('asset-packs','packs')) or not (theme_source or punk_font)):
            raise ValueError('Invalid theme source mapping')
        if theme_source and path.parts[0]!=pack.parts[0]:raise ValueError('Mismatched source/pack namespace')
        if punk_font and pack.parts[0] != ('asset-packs' if path.parts[1]=='asset-fonts' else 'themes'):raise ValueError('Mismatched font/pack namespace')
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
    print(f'Restored {restore(Path(__file__).resolve().parents[1])} theme assets; existing files preserved.')
