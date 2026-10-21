"""Build Midnight Express v1 from checked-in artwork/fonts and original vector sources.
No AI service or network is needed to rebuild. Published versions are immutable.
"""
from pathlib import Path
import hashlib, io, json, zipfile
import cairosvg
from PIL import Image

root = Path(__file__).resolve().parent
base = root / 'midnight-express'
source = root / 'source-icons/midnight-express'
source.mkdir(parents=True, exist_ok=True)
(base / 'icons').mkdir(exist_ok=True)
paths = {
    'nav_home': 'M3 11L12 3L21 11 M5 10V20H10V15H14V20H19V10',
    'nav_search': 'M16 16L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10',
    'nav_explore': 'M12 3L21 12L12 21L3 12Z M12 8V16 M8 12H16',
    'nav_library': 'M4 4H8V20H4Z M12 4H16V20H12Z M20 4V20',
    'play': 'M7 5Q7 3 9 5L20 11Q22 12 20 13L9 20Q7 21 7 19Z',
    'pause': 'M6 5H9V19H6Z M15 5H18V19H15Z',
    'previous': 'M4 5V19 M19 5L8 12L19 19Z',
    'next': 'M20 5V19 M5 5L16 12L5 19Z',
    'queue': 'M7 5H21 M7 12H21 M7 19H21 M3 5H3.1 M3 12H3.1 M3 19H3.1',
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

# A new original Veyra ticket-waveform mark. Essential content stays in the adaptive safe zone.
mark = '''<svg xmlns="http://www.w3.org/2000/svg" width="432" height="432" viewBox="0 0 108 108">
<defs><radialGradient id="bg"><stop stop-color="#273B51"/><stop offset="1" stop-color="#0C121A"/></radialGradient><linearGradient id="ticket" x2="1" y2="1"><stop stop-color="#FFE1AD"/><stop offset="1" stop-color="#D89A4B"/></linearGradient></defs>
<rect width="108" height="108" fill="url(#bg)"/>
<path d="M24 36H84V46A8 8 0 0 0 84 62V72H24V62A8 8 0 0 0 24 46Z" fill="url(#ticket)"/>
<path d="M34 54H40L44 47L49 62L55 44L60 57L65 51H74" fill="none" stroke="#17212E" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round"/>
<path d="M33 29H75 M33 79H75" stroke="#9CCBE8" stroke-opacity=".6" stroke-width="1.5"/>
<circle cx="33" cy="29" r="2" fill="#FFC778"/><circle cx="75" cy="79" r="2" fill="#FFC778"/>
</svg>'''
(source / 'brand.svg').write_text(mark)
full = Image.open(io.BytesIO(cairosvg.svg2png(bytestring=mark.encode()))).convert('RGB')
full.resize((256,256),Image.Resampling.LANCZOS).save(base/'artwork/brand.webp',quality=95,method=6)
full.save(root/'launcher-art/midnight-express.webp',quality=95,method=6)
res = root.parent / 'app/src/main/res'
for density, scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
    for suffix, dp in [('',48),('_foreground',108)]:
        side = int(dp * scale)
        full.resize((side,side),Image.Resampling.LANCZOS).save(res/f'mipmap-{density}/ic_theme_midnight{suffix}.png',optimize=True)
(res/'mipmap-anydpi-v26/ic_theme_midnight.xml').write_text('<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/theme_midnight_background"/><foreground android:drawable="@mipmap/ic_theme_midnight_foreground"/><monochrome android:drawable="@drawable/ic_midnight_monochrome"/></adaptive-icon>\n')
(res/'drawable/ic_midnight_monochrome.xml').write_text('<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108"><path android:fillColor="#FFFFFFFF" android:pathData="M24,36 H84 V46 A8,8 0,0 0,84 62 V72 H24 V62 A8,8 0,0 0,24 46Z"/></vector>\n')
colors = res/'values/theme_icon_colors.xml'
s = colors.read_text()
if 'theme_midnight_background' not in s:
    colors.write_text(s.replace('</resources>','<color name="theme_midnight_background">#0C121A</color></resources>'))
assets = {'header':'artwork/header.webp','player':'artwork/player.webp','preview':'artwork/preview.webp','brandIcon':'artwork/brand.webp','headingFont':'fonts/Rajdhani.ttf','bodyFont':'fonts/Manrope.ttf'}
assets.update({key:f'icons/{key}.png' for key in paths})
manifest = {
    'schema':2,'id':'midnight-express','name':'Midnight Express','author':'Bhavishya Dalal','version':1,'minAppCode':25,
    'palette':{'background':'#0C121A','surface':'#17212E','raised':'#263548','accent':'#FFC778','accentContainer':'#543920','text':'#FFF7EA','muted':'#C8D4E2','outline':'#627C98','secondary':'#9CCBE8'},
    'rowCorner':12,'buttonCorner':16,'navCorner':24,'seekStyle':'web','seekHeight':8,'sceneStyle':'mist',
    'tagline':'Your next stop. Another song.','assets':assets,
}
manifest['files'] = {str(p.relative_to(base)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
(base/'theme.json').write_text(json.dumps(manifest,indent=2)+'\n')
buffer = io.BytesIO()
with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
    for p in sorted(base.rglob('*')):
        if p.is_file():
            info=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,21,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED
            z.writestr(info,p.read_bytes())
pack = root/'packs/midnight-express-v1.zip'
payload = buffer.getvalue()
if pack.exists() and pack.read_bytes()!=payload:
    raise RuntimeError('Midnight Express v1 already exists with different bytes; bump pack version.')
pack.write_bytes(payload)
digest=hashlib.sha256(payload).hexdigest()
pack.with_suffix('.zip.sha256').write_text(f'{digest}  {pack.name}\n')
url='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/themes/'
catalog=json.loads((root/'catalog.json').read_text())
catalog['themes']=[t for t in catalog['themes'] if t['id']!=manifest['id']]+[{
    'id':manifest['id'],'name':manifest['name'],'description':'Rainy night-train windows, warm amber light and coal-blue surfaces. Bold Rajdhani timetable-style headings, custom controls, a parallel route timeline and an optional ticket-waveform Veyra icon.',
    'version':1,'minAppCode':25,'url':url+'packs/'+pack.name,'sha256':digest,'bytes':len(payload),'preview':url+'midnight-express/artwork/preview.webp'}]
content=json.dumps(catalog,indent=2)+'\n'
(root/'catalog.json').write_text(content)
(root.parent/'app/src/main/assets/theme-catalog.json').write_text(content)
print(pack.name,len(payload),digest)
