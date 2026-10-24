#!/usr/bin/env python3
"""Restore included data-only ZIP packs offline. Never overwrite existing files."""
import json
from pathlib import Path
from source_pack_library import LIBRARY,decode

def restore(root):
 root=Path(root).resolve()
 manifest=json.loads((root/'SOURCE_MANIFEST.json').read_text())
 records=manifest.get('packedArchives',[])
 if not records:return 0
 packs=decode((root/LIBRARY).read_bytes(),records);count=0
 # Validate the entire library before writing, and preserve all user edits.
 for path,data in packs.items():
  target=(root/path).resolve()
  if not target.is_relative_to(root):raise ValueError('Source pack path escapes project')
  if target.exists():continue
  target.parent.mkdir(parents=True,exist_ok=True)
  with target.open('xb') as out:out.write(data)
  count+=1
 return count
if __name__=='__main__':print(f'Restored {restore(Path(__file__).resolve().parents[1])} immutable ZIPs; existing files preserved.')
