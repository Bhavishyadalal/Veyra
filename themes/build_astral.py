"""Build Astral Tide v1 from checked-in artwork/fonts and original vector sources.
No AI service or network is needed to rebuild. Published versions are immutable.
"""
from pathlib import Path
import hashlib, io, json, zipfile
import cairosvg
from PIL import Image

root = Path(__file__).resolve().parent
base = root / 'astral-tide'
source = root / 'source-icons/astral-tide'
source.mkdir(parents=True, exist_ok=True)
(base / 'icons').mkdir(exist_ok=True)
paths = {
    'nav_home': 'M3 11L12 3L21 11 M5 10V20H10V15H14V20H19V10',
    'nav_search': 'M16 16L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10',
    'nav_explore': 'M12 2L15 9L22 12L15 15L12 22L9 15L2 12L9 9Z',
    'nav_library': 'M3 5V21 M8 4V21 M13 5L17 4L22 20L18 21Z',
    'play': 'M7 5Q7 3 9 5L20 11Q22 12 20 13L9 20Q7 21 7 19Z',
    'pause': 'M6 5H9V19H6Z M15 5H18V19H15Z',
    'previous': 'M4 5V19 M19 5L8 12L19 19Z',
    'next': 'M20 5V19 M5 5L16 12L5 19Z',
    'queue': 'M4 5H20 M4 11H16 M4 17H12 M18 16L22 19L18 22Z',
    'lyrics': 'M5 3H19Q21 3 21 5V16Q21 18 19 18H10L4 22V18H3V5Q3 3 5 3Z M8 8H16 M8 13H14',
    'settings': 'M12 3V5 M12 19V21 M3 12H5 M19 12H21 M6 6L8 8 M16 16L18 18 M6 18L8 16 M16 8L18 6 M18 12A6 6 0 1 1 6 12A6 6 0 0 1 18 12 M13 12A1 1 0 1 1 11 12A1 1 0 0 1 13 12',
    'back': 'M12 5L5 12L12 19 M5 12H21',
    'download': 'M12 3V16 M7 11L12 16L17 11 M4 18V21H20V18',
}
for key, path in paths.items():
    fill = 'white' if key in ('play','pause') else 'none'
    svg = f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path d="{path}" fill="{fill}" stroke="white" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg>'
    (source / f'{key}.svg').write_text(svg)
    cairosvg.svg2png(bytestring=svg.encode(), write_to=str(base / 'icons' / f'{key}.png'))

# A new original Veyra waveform/manta mark. Essential content stays in the adaptive safe zone.
mark = '''<svg xmlns="http://www.w3.org/2000/svg" width="432" height="432" viewBox="0 0 108 108">
<defs><radialGradient id="bg"><stop stop-color="#164A4E"/><stop offset="1" stop-color="#06171E"/></radialGradient><linearGradient id="ray" x2="0.7" y2="1"><stop stop-color="#D6FFF2"/><stop offset="1" stop-color="#63CECE"/></linearGradient></defs>
<rect width="108" height="108" fill="url(#bg)"/><circle cx="54" cy="54" r="31" fill="none" stroke="#72BEB9" stroke-opacity=".25" stroke-width=".7"/>
<path d="M24 44 Q35 48 45 43 Q50 41 54 48 Q58 41 63 43 Q73 48 84 44 Q72 59 63 57 Q57 56 54 70 Q51 56 45 57 Q36 59 24 44Z" fill="url(#ray)"/>
<path d="M36 51L43 51L47 47L51 57L55 46L59 53L65 51L72 51" fill="none" stroke="#082D35" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
<circle cx="68" cy="31" r="3" fill="#F2D39C"/><circle cx="36" cy="72" r="1" fill="#A0E8D5"/>
</svg>'''
(source / 'brand.svg').write_text(mark)
full = Image.open(io.BytesIO(cairosvg.svg2png(bytestring=mark.encode()))).convert('RGB')
full.resize((256,256),Image.Resampling.LANCZOS).save(base/'artwork/brand.webp',quality=95,method=6)
full.save(root/'launcher-art/astral-tide.webp',quality=95,method=6)
res = root.parent / 'app/src/main/res'
for density, scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
    for suffix, dp in [('',48),('_foreground',108)]:
        side = int(dp * scale)
        full.resize((side,side),Image.Resampling.LANCZOS).save(res/f'mipmap-{density}/ic_theme_astral{suffix}.png',optimize=True)
(res/'mipmap-anydpi-v26/ic_theme_astral.xml').write_text('<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/theme_astral_background"/><foreground android:drawable="@mipmap/ic_theme_astral_foreground"/><monochrome android:drawable="@drawable/ic_astral_monochrome"/></adaptive-icon>\n')
(res/'drawable/ic_astral_monochrome.xml').write_text('<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108"><path android:fillColor="#FFFFFFFF" android:pathData="M24,44 Q35,48 45,43 Q50,41 54,48 Q58,41 63,43 Q73,48 84,44 Q72,59 63,57 Q57,56 54,70 Q51,56 45,57 Q36,59 24,44Z"/></vector>\n')
colors = res/'values/theme_icon_colors.xml'
s = colors.read_text()
if 'theme_astral_background' not in s:
    colors.write_text(s.replace('</resources>','<color name="theme_astral_background">#06171E</color></resources>'))
assets = {'header':'artwork/header.webp','player':'artwork/player.webp','preview':'artwork/preview.webp','brandIcon':'artwork/brand.webp','headingFont':'fonts/SpaceGrotesk.ttf','bodyFont':'fonts/Manrope.ttf'}
assets.update({key:f'icons/{key}.png' for key in paths})
manifest = {
    'schema':2,'id':'astral-tide','name':'Astral Tide','author':'Bhavishya Dalal','version':1,'minAppCode':25,
    'palette':{'background':'#06171E','surface':'#0D2730','raised':'#183A43','accent':'#A0E8D5','accentContainer':'#184C50','text':'#F1FCF9','muted':'#B8D4D4','outline':'#547F84','secondary':'#F2D39C'},
    'rowCorner':20,'buttonCorner':24,'navCorner':30,'seekStyle':'ember','seekHeight':8,'sceneStyle':'mist',
    'tagline':'Drift deeper. Hear everything.','assets':assets,
}
manifest['files'] = {str(p.relative_to(base)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
(base/'theme.json').write_text(json.dumps(manifest,indent=2)+'\n')
buffer = io.BytesIO()
with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
    for p in sorted(base.rglob('*')):
        if p.is_file():
            info=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,21,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED
            z.writestr(info,p.read_bytes())
pack = root/'packs/astral-tide-v1.zip'
payload = buffer.getvalue()
if pack.exists() and pack.read_bytes()!=payload:
    raise RuntimeError('Astral Tide v1 already exists with different bytes; bump pack version.')
pack.write_bytes(payload)
digest=hashlib.sha256(payload).hexdigest()
pack.with_suffix('.zip.sha256').write_text(f'{digest}  {pack.name}\n')
url='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/preview/0.1.7-beta1/themes/'
catalog=json.loads((root/'catalog.json').read_text())
catalog['themes']=[t for t in catalog['themes'] if t['id']!=manifest['id']]+[{
    'id':manifest['id'],'name':manifest['name'],'description':'Bioluminescent manta rays beneath a midnight ocean. Bold Space Grotesk headings, sea-glass mint controls, champagne starlight, custom waveform glyphs and an optional matching Veyra icon.',
    'version':1,'minAppCode':25,'url':url+'packs/'+pack.name,'sha256':digest,'bytes':len(payload),'preview':url+'astral-tide/artwork/preview.webp'}]
content=json.dumps(catalog,indent=2)+'\n'
(root/'catalog.json').write_text(content)
(root.parent/'app/src/main/assets/theme-catalog.json').write_text(content)
print(pack.name,len(payload),digest)
