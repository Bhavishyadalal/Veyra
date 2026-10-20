"""Rebuild the original native icon assets and deterministic Halloween pack/catalog."""
from pathlib import Path
import json, hashlib, zipfile
import cairosvg

root = Path(__file__).resolve().parent
base = root/'halloween-nocturne'
paths = {
 'nav_home':'M3 11L12 3L21 11V21H15V15H9V21H3Z M9 8H9.01 M15 8H15.01',
 'nav_search':'M15.5 15.5L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M7 10L10 7L13 10L10 13Z',
 'nav_explore':'M12 3C19 3 22 10 19 16L16 14L13 18L10 15L6 18L4 14C1 7 5 3 12 3Z M9 9V10 M15 9V10',
 'nav_library':'M4 4V21 M8 4V21 M12 4L17 3L22 20L17 21Z M14 8L19 7 M16 16L21 15',
 'play':'M7 4L20 12L7 20Z',
 'pause':'M7 5V19 M17 5V19',
 'previous':'M4 5V19 M19 5L8 12L19 19Z',
 'next':'M20 5V19 M5 5L16 12L5 19Z',
 'queue':'M8 6H21 M8 12H21 M8 18H21 M3 6H3.01 M3 12H3.01 M3 18H3.01',
 'lyrics':'M4 4H20V17H11L5 22V17H4Z M8 9V12H10V9 M14 9V12H16V9',
 'settings':'M12 3L15 7L20 8L19 13L21 17L16 20L12 18L8 20L3 17L5 13L4 8L9 7Z M15 12A3 3 0 1 1 9 12A3 3 0 0 1 15 12',
 'back':'M14 4L6 12L14 20 M6 12H21',
 'download':'M12 3V16 M7 11L12 16L17 11 M4 17V21H20V17',
}
originals=root/'source-icons';originals.mkdir(exist_ok=True)
for key,path in paths.items():
 svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path d="{path}" fill="none" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
 if key == 'pause':
  svg='<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><rect x="5" y="4" width="5" height="16" rx="1.5" fill="white"/><rect x="14" y="4" width="5" height="16" rx="1.5" fill="white"/></svg>'
 elif key == 'play':
  svg=svg.replace('fill="none"','fill="white"')
 (originals/f'{key}.svg').write_text(svg)
 cairosvg.svg2png(bytestring=svg.encode(),write_to=str(base/'icons'/f'{key}.png'))
assets={'header':'artwork/header.webp','player':'artwork/player.webp','preview':'artwork/preview.webp',
        'headingFont':'fonts/GrenzeGotisch.ttf','bodyFont':'fonts/Manrope.ttf'}
assets.update({k:f'icons/{k}.png' for k in paths})
(base/'LICENSE.txt').write_text('Halloween Nocturne for Veyra. Original AI-assisted artwork and original vector icons by Bhavishya Dalal / Veyra. Redistributed with the Veyra project under its GPL-3.0 terms. Font files retain their accompanying SIL Open Font License notices. No third-party music artwork is included.\n')
files={str(p.relative_to(base)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
# License file goes in a subdirectory so every asset has a bounded canonical path.
if 'LICENSE.txt' in files:
 (base/'licenses').mkdir(exist_ok=True);(base/'LICENSE.txt').replace(base/'licenses/ARTWORK.txt')
 files={str(p.relative_to(base)):hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
manifest={'schema':1,'id':'halloween-nocturne','name':'Halloween Nocturne','author':'Bhavishya Dalal',
 'version':2,'minAppCode':23,'palette':{'background':'#0D0B09','surface':'#17120F','raised':'#261D17',
 'accent':'#FFC17A','accentContainer':'#54321A','text':'#FFF4E6','muted':'#CFBDAA','outline':'#6C5038','secondary':'#C6D2AE'},
 'rowCorner':14,'buttonCorner':18,'navCorner':26,'seekStyle':'ember','seekHeight':8,'assets':assets,'files':files}
(base/'theme.json').write_text(json.dumps(manifest,indent=2)+'\n')
pack=root/'packs/halloween-nocturne-v2.zip';pack.parent.mkdir(exist_ok=True)
with zipfile.ZipFile(pack,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for p in sorted(base.rglob('*')):
  if p.is_file():
   info=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,20,0,0,0))
   info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,p.read_bytes())
digest=hashlib.sha256(pack.read_bytes()).hexdigest();pack.with_suffix('.zip.sha256').write_text(f'{digest}  {pack.name}\n')
url='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/preview/0.1.7-beta1/themes/'
catalog={'schema':1,'themes':[{'id':manifest['id'],'name':manifest['name'],
 'description':'Smoked glass, warm pumpkin amber and gothic headings. Moonlit forest atmosphere throughout the interface, framed artwork, themed song menus, custom navigation glyphs, sculpted buttons and an ember seek bar.',
 'version':2,'minAppCode':23,'url':url+'packs/'+pack.name,'sha256':digest,'bytes':pack.stat().st_size,
 'preview':url+'halloween-nocturne/artwork/preview.webp'}]}
(root/'catalog.json').write_text(json.dumps(catalog,indent=2)+'\n')
(root.parent/'app/src/main/assets/theme-catalog.json').write_text(json.dumps(catalog,indent=2)+'\n')
print(json.dumps({'pack':str(pack),'bytes':pack.stat().st_size,'sha256':digest}))
