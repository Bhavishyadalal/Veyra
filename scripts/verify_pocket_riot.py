#!/usr/bin/env python3
"""Validate all new glyphs, master provenance, density exports and immutable catalogs."""
from pathlib import Path
import hashlib,json,xml.etree.ElementTree as ET,re
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];ART=ROOT/'design/launcher/pocket-riot'
items=json.loads((ART/'catalog.json').read_text())['icons'];assert len(items)==27
records={e['id']:e for e in json.loads((ART/'generated-sources.json').read_text())['images']}
for e in items:
 master=ART/(e['id']+'.webp');assert hashlib.sha256(master.read_bytes()).hexdigest()==records[e['id']]['masterSha256']
 with Image.open(master) as image:assert image.size==(512,512)
 key=e['id'].replace('-','_')
 for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
  for suffix,dp in [('',48),('_foreground',108)]:
   with Image.open(ROOT/f'app/src/main/res/mipmap-{density}/ic_pocket_{key}{suffix}.webp') as image:assert image.size==(int(dp*scale),)*2
 adaptive=ET.parse(ROOT/f'app/src/main/res/mipmap-anydpi-v26/ic_pocket_{key}.xml').getroot()
 assert adaptive.find('monochrome').attrib['{http://schemas.android.com/apk/res/android}drawable']=='@drawable/ic_wave_monochrome'
for base,field,total in [('themes','themes',72),('asset-packs','packs',32)]:
 entries=json.loads((ROOT/base/'catalog.json').read_text())[field];assert len(entries)==total
 for e in entries:assert hashlib.sha256((ROOT/base/f'packs/{e["id"]}-v{e["version"]}.zip').read_bytes()).hexdigest()==e['sha256']
registry=json.loads((ROOT/'themes/advanced-glyphs.json').read_text());known={a for names in registry['aliases'].values() for a in names}
gaps=set()
for folder in [ROOT/'app/src/main/java',ROOT/'sharedUi/src/jvmSharedMain']:
 for file in folder.rglob('*.kt'):
  for name in re.findall(r'Icons\.(?:AutoMirrored\.)?(?:Rounded|Filled|Outlined|Default|Sharp|TwoTone)\.([A-Za-z0-9_]+)',file.read_text()):
   if name not in known:gaps.add(name)
assert not gaps,gaps
print('27 reviewed glass masters / 270 density exports / canonical monochrome, 104 pack checksums, all standard functional vector aliases passed')
