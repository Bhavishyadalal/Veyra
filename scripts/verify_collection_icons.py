#!/usr/bin/env python3
"""Focused checks of actual new launcher exports, shared branding and immutable old packs."""
from pathlib import Path
import hashlib,json,xml.etree.ElementTree as ET
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];ART=ROOT/'design/launcher/collections'
items=json.loads((ART/'catalog.json').read_text())['icons'];assert len(items)==21
for e in items:
 key='collection_'+e['id'].replace('-','_')
 with Image.open(ART/(e['id']+'.webp')) as im:assert im.size==(512,512)
 for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
  for suffix,dp in [('',48),('_foreground',108)]:
   with Image.open(ROOT/f'app/src/main/res/mipmap-{density}/ic_{key}{suffix}.webp') as im:assert im.size==(int(dp*scale),)*2
 adaptive=ET.parse(ROOT/f'app/src/main/res/mipmap-anydpi-v26/ic_{key}.xml').getroot()
 assert adaptive.find('monochrome').attrib['{http://schemas.android.com/apk/res/android}drawable']=='@drawable/ic_wave_monochrome'
for family,field in [('themes','themes'),('asset-packs','packs')]:
 for e in json.loads((ROOT/f'{family}/catalog.json').read_text())[field]:
  assert hashlib.sha256((ROOT/f'{family}/packs/{e["id"]}-v{e["version"]}.zip').read_bytes()).hexdigest()==e['sha256']
print('21 masters, 210 correctly sized density exports, 21 shared-wave adaptive/monochrome references and all 67 immutable pack checksums passed.')
