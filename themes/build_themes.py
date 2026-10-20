"""Deterministic data packs and Android adaptive icons from checked-in original assets.
No network access, AI service or signing material is needed to rebuild these files.
"""
from pathlib import Path
import json, hashlib, zipfile, io, sys
import cairosvg
from PIL import Image

root=Path(__file__).resolve().parent
url='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/preview/0.1.7-beta1/themes/'
web_paths={
 'nav_home':'M3 10L12 3L21 10V21H15V15H9V21H3Z M8 9L12 6L16 9',
 'nav_search':'M16 16L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M6 10H14 M10 6V14',
 'nav_explore':'M12 2L21 7V17L12 22L3 17V7Z M12 2V22 M3 7L21 17 M21 7L3 17 M7 9L12 6L17 9V15L12 18L7 15Z',
 'nav_library':'M3 4V21 M8 4V21 M13 4L18 3L22 20L17 21Z M14 8L19 7 M16 16L21 15',
 'play':'M7 4L20 12L7 20Z', 'pause':'M7 5V19 M17 5V19',
 'previous':'M4 5V19 M19 5L8 12L19 19Z', 'next':'M20 5V19 M5 5L16 12L5 19Z',
 'queue':'M8 6H21 M8 12H21 M8 18H21 M3 5V7 M3 11V13 M3 17V19',
 'lyrics':'M4 3H20V17H11L5 22V17H4Z M8 8H10V12H8 M14 8H16V12H14',
 'settings':'M12 2L16 6L21 8L20 13L21 18L16 21L12 19L8 21L3 18L4 13L3 8L8 6Z M15 12A3 3 0 1 1 9 12A3 3 0 0 1 15 12',
 'back':'M13 4L5 12L13 20 M5 12H21',
 'download':'M12 3V16 M7 11L12 16L17 11 M4 17V21H20V17',
}
for key,path in web_paths.items():
 svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path d="{path}" fill="none" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
 if key=='pause':svg='<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><rect x="5" y="4" width="5" height="16" rx="1" fill="white"/><rect x="14" y="4" width="5" height="16" rx="1" fill="white"/></svg>'
 if key=='play':svg=svg.replace('fill="none"','fill="white"')
 source=root/'source-icons/web-slinger';source.mkdir(exist_ok=True)
 (source/f'{key}.svg').write_text(svg)
 cairosvg.svg2png(bytestring=svg.encode(),write_to=str(root/'web-slinger/icons'/f'{key}.png'))
# Original Halloween glyphs remain the exact checked-in vector originals.
for source in (root/'source-icons').glob('*.svg'):
 cairosvg.svg2png(url=str(source),write_to=str(root/'halloween-nocturne/icons'/f'{source.stem}.png'))

catalog=[]
for id,version,name,description,style,tagline in [
 ('web-slinger',1,'Web Slinger','Midnight city, crimson glass and cobalt webs. Bold condensed headings, themed Veyra branding, angular control details, web timeline and an optional matching launcher icon.','web','Your city. Your soundtrack.'),
 ('halloween-nocturne',3,'Halloween Nocturne','Moonlit forest, pumpkin amber and gothic headings. Themed Veyra branding, sculpted controls, ember timeline and an optional matching launcher icon.','mist','A different kind of night.')]:
 base=root/id
 if id=='web-slinger':
  palette={'background':'#070C18','surface':'#101A2C','raised':'#1B2A42','accent':'#FF4F66','accentContainer':'#67172B','text':'#F5F8FF','muted':'#BAC9DF','outline':'#4D6384','secondary':'#82B9FF'}
  assets={'header':'artwork/header.webp','player':'artwork/player.webp','preview':'artwork/preview.webp','brandIcon':'artwork/brand.webp','headingFont':'fonts/BarlowCondensed.ttf','bodyFont':'fonts/Manrope.ttf'}
  assets.update({k:f'icons/{k}.png' for k in web_paths})
  m={'schema':2,'id':id,'name':name,'author':'Bhavishya Dalal','version':version,'minAppCode':24,'palette':palette,
     'rowCorner':12,'buttonCorner':16,'navCorner':24,'seekStyle':'web','seekHeight':7,'assets':assets}
 else:
  m=json.loads((base/'theme.json').read_text())
  m.update(schema=2,version=version,minAppCode=24)
  m['assets']['brandIcon']='artwork/brand.webp'
 m.update(sceneStyle=style,tagline=tagline)
 (base/'licenses/ARTWORK.txt').write_text('Original AI-assisted abstract artwork and original interface glyphs for Veyra by Bhavishya Dalal. Distributed under the project GPL-3.0 terms. Font files retain their included SIL Open Font License notices. No third-party music artwork, character illustration or official superhero logo is included. Web Slinger is an unofficial web-and-city fan theme, without affiliation or endorsement.\n')
 m['files']={str(p.relative_to(base)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
 (base/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
 pack=root/'packs'/f'{id}-v{version}.zip'
 buffer=io.BytesIO()
 with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(base.rglob('*')):
   if p.is_file():
    info=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,20,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED
    z.writestr(info,p.read_bytes())
 payload=buffer.getvalue()
 if pack.exists() and pack.read_bytes()!=payload and '--replace-draft' not in sys.argv:
  raise RuntimeError(f'{pack.name} already has different bytes. Bump the pack version; use --replace-draft only before publication.')
 pack.write_bytes(payload)
 digest=hashlib.sha256(payload).hexdigest()
 pack.with_suffix('.zip.sha256').write_text(f'{digest}  {pack.name}\n')
 catalog.append({'id':id,'name':name,'description':description,'version':version,'minAppCode':24,'url':url+'packs/'+pack.name,'sha256':digest,'bytes':pack.stat().st_size,'preview':url+id+'/artwork/preview.webp'})
 print(pack.name,pack.stat().st_size,digest)
content=json.dumps({'schema':1,'themes':catalog},indent=2)+'\n'
(root/'catalog.json').write_text(content)
(root.parent/'app/src/main/assets/theme-catalog.json').write_text(content)
# Adaptive foreground uses 108 dp canvas; essential waveform stays in the safe center.
res=root.parent/'app/src/main/res'
for id,key,bg in [('web-slinger','web','#070C18'),('halloween-nocturne','halloween','#0D0B09')]:
 im=Image.open(root/'launcher-art'/f'{id}.webp').convert('RGB')
 for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
  dest=res/f'mipmap-{density}';dest.mkdir(exist_ok=True)
  for suffix,dp in [('',48),('_foreground',108)]:
   side=int(dp*scale)
   im.resize((side,side),Image.Resampling.LANCZOS).save(dest/f'ic_theme_{key}{suffix}.png',optimize=True)
 adaptive=res/'mipmap-anydpi-v26';adaptive.mkdir(exist_ok=True)
 (adaptive/f'ic_theme_{key}.xml').write_text(f'<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/theme_{key}_background"/><foreground android:drawable="@mipmap/ic_theme_{key}_foreground"/><monochrome android:drawable="@drawable/ic_wave_monochrome"/></adaptive-icon>\n')
colors=res/'values/theme_icon_colors.xml'
colors.write_text('<resources><color name="theme_web_background">#070C18</color><color name="theme_halloween_background">#0D0B09</color></resources>\n')

# Additional schema-3 fan theme; old published packs above remain byte-for-byte immutable.
import runpy
runpy.run_path(str(root / "build_playtime.py"), run_name="__main__")
runpy.run_path(str(root / "build_astral.py"), run_name="__main__")
runpy.run_path(str(root / "build_midnight.py"), run_name="__main__")
