#!/usr/bin/env python3
"""Validate actual pack/launcher files and preservation of the pre-patch published collections."""
from pathlib import Path
import hashlib,io,json,subprocess,xml.etree.ElementTree as ET
from PIL import Image
from route_catalogs import current_branch
root=Path(__file__).resolve().parents[1]
catalog=json.loads((root/'asset-packs/catalog.json').read_text());assert len(catalog['packs'])==15
icons=json.loads((root/'design/launcher/night-vault/catalog.json').read_text())['icons'];assert len(icons)==10
assert (root/'asset-packs/catalog.json').read_bytes()==(root/'app/src/main/assets/asset-catalog.json').read_bytes()
provenance=json.loads((root/'design/launcher/night-vault/generated-sources.json').read_text())
assert len(provenance['images'])==10
assert {e['id'] for e in provenance['images']}=={e['id'] for e in icons}
for e in icons:
    source=root/'design/launcher/night-vault'/e['id']
    record=next(r for r in provenance['images'] if r['id']==e['id'])
    assert hashlib.sha256(source.with_suffix('.webp').read_bytes()).hexdigest()==record['masterSha256']
    with Image.open(source.with_suffix('.webp')) as im:
        assert im.size==(512,512)
        master=im.convert('RGB')
    key='collection_'+e['id'].replace('-','_')
    for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
        for suffix,dp in [('',48),('_foreground',108)]:
            output=root/f'app/src/main/res/mipmap-{density}/ic_{key}{suffix}.webp'
            with Image.open(output) as im:assert im.size==(int(dp*scale),)*2
            expected=io.BytesIO()
            master.resize((int(dp*scale),)*2,Image.Resampling.LANCZOS).save(expected,format='WEBP',quality=92,method=6)
            assert output.read_bytes()==expected.getvalue(),str(output)
    xml=ET.parse(root/f'app/src/main/res/mipmap-anydpi-v26/ic_{key}.xml').getroot()
    assert xml.find('monochrome').attrib['{http://schemas.android.com/apk/res/android}drawable']=='@drawable/ic_wave_monochrome'
for base,key in [('themes','themes'),('asset-packs','packs')]:
    c=json.loads((root/base/'catalog.json').read_text())
    branch=current_branch(root)
    for e in c[key]:
        data=(root/base/'packs'/f'{e["id"]}-v{e["version"]}.zip').read_bytes()
        assert len(data)==e['bytes'] and hashlib.sha256(data).hexdigest()==e['sha256']
        assert e['url']==f'https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/{branch}/{base}/packs/{e["id"]}-v{e["version"]}.zip'
# No published old theme/asset ZIP, launcher edition, or retained preview archive may change.
baseline='d1fd25d59fb0f1c0493ab939e89127ed4fcb12bf'
paths=subprocess.check_output(['git','ls-tree','-r','--name-only',baseline,'themes/packs','asset-packs/packs','downloads','design/launcher/collections'],cwd=root,text=True).splitlines()
for rel in paths:
    if rel.startswith('downloads/') and ('beta1.zip' not in rel or not (root/rel).is_file()):continue # Rotation is checked by verify_archives.py.
    assert (root/rel).read_bytes()==subprocess.check_output(['git','show',baseline+':'+rel],cwd=root),rel
# No other native launcher image or alias can change in this icon-only patch.
changed=subprocess.check_output(['git','diff',baseline,'--name-only','--','app/src/main/res','app/src/main/AndroidManifest.xml'],cwd=root,text=True).splitlines()
allowed={f'app/src/main/res/mipmap-{d}/ic_collection_{e["id"].replace("-","_")}{suffix}.webp' for e in icons for d,_ in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)] for suffix in ['', '_foreground']}
assert set(changed)<=allowed, set(changed)-allowed
print('10 AI glass master hashes, 100 exact density exports, monochrome references, all 77 catalog hashes/URLs, published packs/older icons and retained published preview archives passed.')
