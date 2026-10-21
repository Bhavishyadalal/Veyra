"""Build the data-only Playtime Club pack and its optional Android icon.
Run build_themes.py to rebuild the complete catalog. Published packs are immutable.
"""
from pathlib import Path
import hashlib, io, json, zipfile
import cairosvg
from PIL import Image

root=Path(__file__).resolve().parent
base=root/'playtime-club'
url='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/themes/'
# Original thick, rounded interface glyphs. Character artwork never replaces controls.
paths={
 'nav_home':'M3 11L12 3L21 11V20Q21 21 20 21H15V15H9V21H4Q3 21 3 20Z',
 'nav_search':'M16 16L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10',
 'nav_explore':'M12 3C17 3 21 7 21 12S17 21 12 21S3 17 3 12S7 3 12 3Z M16 8L14 14L8 16L10 10Z',
 'nav_library':'M3 5V20 M8 5V20 M14 5L18 4L22 20L17 21Z',
 'play':'M8 5Q7 5 7 7V17Q7 20 10 18L19 13Q21 12 19 11Z',
 'pause':'M7 6V18 M17 6V18',
 'previous':'M5 5V19 M19 5L9 12L19 19Z', 'next':'M19 5V19 M5 5L15 12L5 19Z',
 'queue':'M9 6H21 M9 12H21 M9 18H21 M3 6H4 M3 12H4 M3 18H4',
 'lyrics':'M6 3H18Q21 3 21 6V15Q21 18 18 18H10L5 22V18Q3 18 3 15V6Q3 3 6 3Z M8 8V12 M15 8V12',
 'settings':'M12 3V5 M12 19V21 M3 12H5 M19 12H21 M6 6L7 7 M17 17L18 18 M6 18L7 17 M17 7L18 6 M18 12A6 6 0 1 1 6 12A6 6 0 0 1 18 12 M14 12A2 2 0 1 1 10 12A2 2 0 0 1 14 12',
 'back':'M12 5L5 12L12 19 M5 12H21',
 'download':'M12 3V16 M7 11L12 16L17 11 M4 18V20Q4 21 6 21H18Q20 21 20 20V18',
}
(base/'icons').mkdir(exist_ok=True)
source=root/'source-icons/playtime-club';source.mkdir(exist_ok=True)
for key,path in paths.items():
 fill='white' if key=='play' else 'none'
 svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path d="{path}" fill="{fill}" stroke="white" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/></svg>'
 if key=='pause':svg='<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><rect x="5" y="4" width="5" height="16" rx="2.5" fill="white"/><rect x="14" y="4" width="5" height="16" rx="2.5" fill="white"/></svg>'
 (source/f'{key}.svg').write_text(svg)
 cairosvg.svg2png(bytestring=svg.encode(),write_to=str(base/'icons'/f'{key}.png'))
assets={'header':'artwork/header.webp','player':'artwork/player.webp','preview':'artwork/preview.webp','brandIcon':'artwork/brand.webp','headingFont':'fonts/Fredoka.ttf','bodyFont':'fonts/Manrope.ttf'}
assets.update({k:f'icons/{k}.png' for k in paths})
m={'schema':3,'id':'playtime-club','name':'Playtime Club','author':'Bhavishya Dalal','version':1,'minAppCode':25,
 'palette':{'background':'#09152C','surface':'#112342','raised':'#203453','accent':'#FFE18A','accentContainer':'#244B77','text':'#FAFCFF','muted':'#C4D5EE','outline':'#526F99','secondary':'#8DE8E0'},
 'rowCorner':26,'buttonCorner':32,'navCorner':36,'seekStyle':'bubbles','seekHeight':10,'sceneStyle':'playful','tagline':'A little joy. Every play.','assets':assets}
m['files']={str(p.relative_to(base)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
(base/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
buffer=io.BytesIO()
with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for p in sorted(base.rglob('*')):
  if p.is_file():
   info=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,20,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED
   z.writestr(info,p.read_bytes())
pack=root/'packs/playtime-club-v1.zip';payload=buffer.getvalue()
if pack.exists() and pack.read_bytes()!=payload:raise RuntimeError('Published Playtime pack changed: bump its version.')
pack.write_bytes(payload);digest=hashlib.sha256(payload).hexdigest()
pack.with_suffix('.zip.sha256').write_text(f'{digest}  {pack.name}\n')
catalog=json.loads((root/'catalog.json').read_text())
catalog['themes']=[t for t in catalog['themes'] if t['id']!=m['id']]+[{'id':m['id'],'name':m['name'],'description':'An unofficial cartoon music party with Doraemon, Shin-chan and Oggy-inspired fan artwork. Rounded Fredoka headings, butter-yellow controls, mint bubbles, soft press feedback and a matching optional Veyra icon.','version':1,'minAppCode':25,'url':url+'packs/'+pack.name,'sha256':digest,'bytes':len(payload),'preview':url+'playtime-club/artwork/preview.webp'}]
content=json.dumps(catalog,indent=2)+'\n'
(root/'catalog.json').write_text(content);(root.parent/'app/src/main/assets/theme-catalog.json').write_text(content)
res=root.parent/'app/src/main/res'
im=Image.open(root/'launcher-art/playtime-club.webp').convert('RGB')
for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
 for suffix,dp in [('',48),('_foreground',108)]:
  side=int(dp*scale);im.resize((side,side),Image.Resampling.LANCZOS).save(res/f'mipmap-{density}/ic_theme_playtime{suffix}.png',optimize=True)
(res/'mipmap-anydpi-v26/ic_theme_playtime.xml').write_text('<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/theme_playtime_background"/><foreground android:drawable="@mipmap/ic_theme_playtime_foreground"/><monochrome android:drawable="@drawable/ic_wave_monochrome"/></adaptive-icon>\n')
colors=res/'values/theme_icon_colors.xml';s=colors.read_text()
if 'theme_playtime_background' not in s:colors.write_text(s.replace('</resources>','<color name="theme_playtime_background">#09152C</color></resources>'))
print(pack.name,len(payload),digest)
