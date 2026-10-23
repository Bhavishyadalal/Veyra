#!/usr/bin/env python3
"""Validate actual pack/launcher files and preservation of the pre-patch published collections."""
from pathlib import Path
import hashlib,json,subprocess,xml.etree.ElementTree as ET
from PIL import Image
root=Path(__file__).resolve().parents[1]
catalog=json.loads((root/'asset-packs/catalog.json').read_text());assert len(catalog['packs'])==15
icons=json.loads((root/'design/launcher/night-vault/catalog.json').read_text())['icons'];assert len(icons)==10
assert (root/'asset-packs/catalog.json').read_bytes()==(root/'app/src/main/assets/asset-catalog.json').read_bytes()
wave=ET.parse(root/'app/src/main/res/drawable/ic_wave_monochrome.xml').getroot()[0].attrib['{http://schemas.android.com/apk/res/android}pathData']
for e in icons:
    source=root/'design/launcher/night-vault'/e['id']
    svg=ET.parse(source.with_suffix('.svg')).getroot()
    assert sum(p.attrib.get('d')==wave for p in svg.iter('{http://www.w3.org/2000/svg}path'))==2
    with Image.open(source.with_suffix('.webp')) as im:assert im.size==(512,512)
    key='collection_'+e['id'].replace('-','_')
    for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
        for suffix,dp in [('',48),('_foreground',108)]:
            with Image.open(root/f'app/src/main/res/mipmap-{density}/ic_{key}{suffix}.webp') as im:assert im.size==(int(dp*scale),)*2
    xml=ET.parse(root/f'app/src/main/res/mipmap-anydpi-v26/ic_{key}.xml').getroot()
    assert xml.find('monochrome').attrib['{http://schemas.android.com/apk/res/android}drawable']=='@drawable/ic_wave_monochrome'
for base,key in [('themes','themes'),('asset-packs','packs')]:
    c=json.loads((root/base/'catalog.json').read_text())
    for e in c[key]:
        data=(root/base/'packs'/f'{e["id"]}-v{e["version"]}.zip').read_bytes()
        assert len(data)==e['bytes'] and hashlib.sha256(data).hexdigest()==e['sha256']
        assert e['url']==f'https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/{base}/packs/{e["id"]}-v{e["version"]}.zip'
# No published old theme/asset ZIP, launcher edition, or retained preview archive may change.
baseline='b6c55920bb9e82793d929ea3d6dddb7774fefa60'
paths=subprocess.check_output(['git','ls-tree','-r','--name-only',baseline,'themes/packs','asset-packs/packs','downloads','design/launcher/collections'],cwd=root,text=True).splitlines()
for rel in paths:
    if rel.startswith('downloads/') and 'beta1.zip' not in rel:continue
    assert (root/rel).read_bytes()==subprocess.check_output(['git','show',baseline+':'+rel],cwd=root),rel
print('10 official-wave masters, 100 density exports, monochrome references, all 77 catalog hashes/URLs, original published packs/icons and three preview archives passed.')
