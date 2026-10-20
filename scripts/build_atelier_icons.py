"""Compile checked-in AI Glass Atelier masters to Android density resources, offline.
The 512px masters are asset exports of AI edits of the official Glass Noir reference.
Existing default, legacy and theme-linked icons are not rewritten by this script.
"""
from pathlib import Path
import json
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
ART = ROOT / 'design/launcher/atelier'
CATALOG = json.loads((ART / 'catalog.json').read_text())

def entry_name(id):
    return 'Atelier' + ''.join(word.title() for word in id.split('-'))

def compile_icons():
    res = ROOT / 'app/src/main/res'
    for item in CATALOG['icons']:
        key = item['id'].replace('-', '_')
        with Image.open(ART / (item['id'] + '.webp')) as image:
            assert image.size == (512, 512), item['id']
            full = image.convert('RGB')
        for density, scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
            for suffix, dp in [('',48),('_foreground',108)]:
                side = int(dp * scale)
                full.resize((side, side), Image.Resampling.LANCZOS).save(
                    res / f'mipmap-{density}/ic_atelier_{key}{suffix}.webp', quality=93, method=6)
        (res / f'mipmap-anydpi-v26/ic_atelier_{key}.xml').write_text(
            '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">'
            '<background android:drawable="@color/wave_glass_background"/>'
            f'<foreground android:drawable="@mipmap/ic_atelier_{key}_foreground"/>'
            '<monochrome android:drawable="@drawable/ic_wave_monochrome"/></adaptive-icon>\n')
        print(item['name'] + ': density resources compiled')
    (ART / 'render.properties').write_text('ids='+','.join(item['id'] for item in CATALOG['icons'])+'\n'+''.join(item['id']+'.name='+item['name']+'\n' for item in CATALOG['icons']))

if __name__ == '__main__':
    compile_icons()
