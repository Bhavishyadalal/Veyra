#!/usr/bin/env python3
"""Replace byte-identical legacy foreground exports with Android resource aliases.
Every family/mask/name stays available. Canonical images and authoring masters remain.
"""
from pathlib import Path
import hashlib,json,collections
ROOT=Path(__file__).resolve().parents[1];RES=ROOT/'app/src/main/res';DENSITIES=['mdpi','hdpi','xhdpi','xxhdpi','xxxhdpi']
def main():
 groups=collections.defaultdict(list)
 for p in (RES/'mipmap-mdpi').glob('*_foreground.png'):
  if not p.name.startswith(('ic_liquid_glass','ic_pixel_glass','ic_samsung_glass')):continue
  files=[RES/f'mipmap-{d}'/p.name for d in DENSITIES]
  if all(f.exists() for f in files):groups[tuple(hashlib.sha256(f.read_bytes()).hexdigest() for f in files)].append(p.stem)
 manifest_path=ROOT/'branding/legacy-glass-aliases.json'
 records=json.loads(manifest_path.read_text())['aliases'] if manifest_path.exists() else []
 known={e['name'] for e in records};removed=0
 for hashes,names in groups.items():
  if len(names)<2:continue
  names.sort();canonical=names[0]
  for name in names[1:]:
   if name not in known:records.append(dict(name=name,target=canonical,densitySha256=dict(zip(DENSITIES,hashes))));known.add(name)
   for d in DENSITIES:
    source=RES/f'mipmap-{d}/{name}.png';target=RES/f'mipmap-{d}/{canonical}.png'
    assert source.read_bytes()==target.read_bytes();removed+=source.stat().st_size;source.unlink()
 (RES/'values/legacy_glass_aliases.xml').write_text('<resources>\n'+''.join(f'    <item name="{e["name"]}" type="mipmap">@mipmap/{e["target"]}</item>\n' for e in sorted(records,key=lambda e:e['name']))+'</resources>\n')
 manifest_path.write_text(json.dumps(dict(schema=1,aliases=records),indent=2)+'\n')
 for e in records:
  for d,h in e['densitySha256'].items():assert hashlib.sha256((RES/f'mipmap-{d}/{e["target"]}.png').read_bytes()).hexdigest()==h
 print('Native foreground aliases:',len(records),'; duplicate bytes removed:',removed)
if __name__=='__main__':main()
