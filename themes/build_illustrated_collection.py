"""Build five immutable illustrated beta packs using supported build-30 presets.
Reviewed AI artwork is encoded separately; functional glyphs are original vectors.
"""
from pathlib import Path
import hashlib, io, json, shutil, zipfile, xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parent
URL='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/themes/'
COLLECTION=[
 dict(id='lantern-letters',name='Lantern Letters',medium='Ink & watercolor',tagline='Every song, a letter home.',scene='sketch',seek='sketch',font='PatrickHand',fontSource='margin-notes',
  colors=['10282B','1B3537','30484A','FFD69D','53462F','FFF7E9','E1DED0','A1957C','BBDDC8'],
  description='Ink-and-watercolor koi postal village, apricot lanterns and folded letters. Patrick Hand headings, lantern/letter glyphs, uneven paper frames and short sketch transitions.'),
 dict(id='velvet-orbit',name='Velvet Orbit',medium='Retro risograph',tagline='A record shop beyond the moon.',scene='playful',seek='bubbles',font='SpaceGrotesk',fontSource='astral-tide',
  colors=['1B1A2D','29273C','403B53','F4CF9D','55412D','FFF5E9','E1D5DF','AA91A7','CCB9EF'],
  description='Tactile risograph planets, vinyl rings and an orbital record-shop tram. Space Grotesk headings, planetary glyphs, rounded controls and supported playful spring feedback.'),
 dict(id='moss-mechanism',name='Moss & Mechanism',medium='Botanical engraving',tagline='Let the quiet clockwork sing.',scene='sketch',seek='sketch',font='CormorantGaramond',fontSource='design',
  colors=['1B261C','29362A','3D4938','E5D4A0','4A472C','FBF8E7','DCDFCA','9FA88B','BDD0A4'],
  description='Botanical engraving of a moss-filled pocket-watch observatory, brass gears and delicate moths. Cormorant headings, leaf/gear glyphs and native sketch presentation.'),
 dict(id='sugar-static',name='Sugar Static',medium='Hand-painted gouache',tagline='Sweet little songs. Big feelings.',scene='playful',seek='bubbles',font='Fredoka',fontSource='playtime-club',
  colors=['2D1B27','3D2934','553C47','F6C6AA','5D3E32','FFF5E8','EAD5D9','B69AA4','C0DDC7'],
  description='Gouache strawberry record shops, cassette bakeries and waffle bridges on berry paper. Fredoka headings, confectionery glyphs, rounded controls and supported playful press feedback.'),
 dict(id='tidal-origami',name='Tidal Origami',medium='Layered cut paper',tagline='Fold the world into a song.',scene='sketch',seek='sketch',font='Rajdhani',fontSource='midnight-express',
  colors=['122635','203746','344D5A','D8E7DF','3D4B46','F6F7E9','D0DEE0','8DAAB5','ECC8A2'],
  description='Layered indigo paper waves, a folded whale harbor and copper-gold fish. Rajdhani headings, angular crease glyphs, uneven paper frames and native paper-slide transitions.'),
]
def digest(data):return hashlib.sha256(data).hexdigest()
def paths_for(index):
 keys=['nav_home','nav_search','nav_explore','nav_library','play','pause','previous','next','queue','lyrics','settings','back','download']
 paths={key:ET.fromstring((ROOT/'source-icons/margin-notes'/f'{key}.svg').read_text()).find('.//{http://www.w3.org/2000/svg}path').attrib['d'] for key in keys}
 if index==0:
  paths.update(nav_home='M4 9L12 3L20 9 M6 9L18 9L17 20L7 20Z M9 7L15 7 M9 13L15 13 M12 20L12 23',
   queue='M3 4L21 4L20 13L4 13Z M4 5L12 10L20 5 M7 17L21 17 M7 21L18 21',
   nav_library='M4 4L19 3L21 20L6 21Z M7 8L17 7 M8 12L15 11 M8 16L17 15')
 if index==1:
  paths.update(nav_home='M3 11L12 4L21 11 M5 10L5 21L19 21L19 10 M9 21L9 15L15 15L15 21 M9 9L15 9',
   nav_explore='M20 12A8 8 0 1 1 4 12A8 8 0 0 1 20 12 M2 15Q12 6 22 9 M14 7L13 13L7 17L10 10Z',
   nav_search='M17 10A7 7 0 1 1 3 10A7 7 0 0 1 17 10 M15 15L21 21 M7 10L13 10 M10 7L10 13')
 if index==2:
  paths.update(nav_home='M3 11L12 3L21 11 M5 10L5 21L19 21L19 10 M9 21L9 15L14 15L14 21 M4 18Q0 15 3 12 M20 17Q24 13 21 10',
   nav_explore='M21 12A9 9 0 1 1 3 12A9 9 0 0 1 21 12 M16 6Q18 16 7 18Q6 7 16 6Z M8 17L15 8',
   nav_library='M3 5Q7 3 12 6Q17 3 21 5L20 20Q17 18 12 21Q7 18 3 20Z M12 6L12 21 M6 9Q10 9 9 14Q5 13 6 9 M16 9Q20 9 18 14Q14 13 16 9')
 if index==3:
  paths.update(nav_home='M3 11Q12 0 21 11 M5 10L5 21L19 21L19 10 M9 21L9 16Q12 13 15 16L15 21 M4 10Q6 14 8 10Q10 14 12 10Q14 14 16 10Q18 14 20 10',
   play='M7 5Q7 3 10 5L20 11Q22 12 20 14L10 20Q7 22 7 19Z',
   pause='M6 5Q8 3 10 5L10 19Q8 21 6 19Z M14 5Q16 3 18 5L18 19Q16 21 14 19Z',
   nav_explore='M21 12Q22 22 12 21Q2 22 3 12Q2 2 12 3Q22 2 21 12Z M16 7L13 14L7 17L10 10Z M7 6L8 7 M17 17L18 18')
 if index==4:
  paths.update(nav_home='M2 12L11 3L22 11L18 21L5 20Z M2 12L12 13L22 11 M11 3L12 13L18 21 M8 21L9 16L14 16L15 21',
   nav_explore='M12 2L22 11L13 22L2 13Z M12 2L13 22 M2 13L22 11 M15 7L13 14L7 17L10 10Z',
   nav_library='M3 4L12 7L21 3L20 20L12 22L4 19Z M12 7L12 22 M6 9L9 11 M15 10L18 8',
   queue='M3 4L21 3L12 9Z M3 12L21 11 M3 18L21 17 M3 22L16 21')
 return paths

def build(c,index):
 import cairosvg
 base=ROOT/c['id'];source=ROOT/'source-icons'/c['id']
 for folder in ['artwork','icons','fonts','licenses']:(base/folder).mkdir(parents=True,exist_ok=True)
 source.mkdir(parents=True,exist_ok=True)
 paths=paths_for(index)
 for name,d in paths.items():
  width=1.35 if c['scene']=='sketch' else 1.85
  second=f'<path d="{d}" transform="translate(.18 -.13)" stroke-width=".5" opacity=".5"/>' if c['scene']=='sketch' else ''
  svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><g fill="none" stroke="white" stroke-width="{width}" stroke-linecap="round" stroke-linejoin="round"><path d="{d}"/>{second}</g></svg>'
  (source/f'{name}.svg').write_text(svg);cairosvg.svg2png(bytestring=svg.encode(),write_to=str(base/'icons'/f'{name}.png'))
 # Retain Veyra's actual waveform, with a theme-specific illustrated badge/frame.
 wave=ET.fromstring((ROOT/'source-icons/margin-notes/brand.svg').read_text()).find('.//{http://www.w3.org/2000/svg}g/{http://www.w3.org/2000/svg}path').attrib['d']
 bg,surface,raised,accent,container,text,muted,outline,secondary=['#'+v for v in c['colors']]
 frames=[
  f'<path d="M10 27L64 5L118 25L114 118L12 119Z" fill="{surface}" stroke="{accent}" stroke-width="2"/><path d="M14 30L63 57L114 28 M22 110L40 101" fill="none" stroke="{outline}"/>',
  f'<rect x="8" y="8" width="112" height="112" rx="32" fill="{surface}" stroke="{accent}" stroke-width="2"/><ellipse cx="64" cy="64" rx="61" ry="21" transform="rotate(-27 64 64)" fill="none" stroke="{secondary}" stroke-width="2"/><circle cx="104" cy="20" r="5" fill="{accent}"/>',
  f'<rect x="10" y="9" width="108" height="111" rx="24" fill="{surface}" stroke="{accent}" stroke-width="2"/><path d="M17 102Q4 73 17 29 M17 75Q3 73 7 61Q22 61 17 75 M17 51Q1 47 7 35Q21 37 17 51 M109 103Q124 73 111 31 M111 81Q127 77 122 65Q108 67 111 81" fill="none" stroke="{secondary}" stroke-width="1.4"/>',
  f'<path d="M20 14Q27 1 40 11Q53 1 64 10Q78 0 88 11Q107 3 111 23Q126 33 116 46Q126 59 117 72Q129 88 114 99Q113 120 95 116Q81 127 66 118Q48 127 36 117Q15 122 15 103Q0 92 11 77Q1 64 10 50Q0 34 15 26Z" fill="{surface}" stroke="{accent}" stroke-width="2"/><path d="M16 25L27 22 M104 103L113 98" stroke="{secondary}" stroke-width="3"/>',
  f'<path d="M64 3L122 62L65 125L5 66Z" fill="{surface}" stroke="{accent}" stroke-width="2"/><path d="M64 3L66 125 M5 66L122 62 M64 3L40 63L65 125" fill="none" stroke="{outline}" stroke-width=".8"/>'
 ]
 brand=f'<svg xmlns="http://www.w3.org/2000/svg" width="256" height="256" viewBox="0 0 128 128">{frames[index]}<g transform="translate(10 7)"><path d="{wave}" fill="{text}" fill-opacity=".88" stroke="{accent}" stroke-width=".6"/></g></svg>'
 (source/'brand.svg').write_text(brand);cairosvg.svg2png(bytestring=brand.encode(),write_to=str(base/'artwork/brand.png'))
 for font,origin in [('Manrope','midnight-express'),(c['font'],c['fontSource'])]:
  origin_font=ROOT.parent/'design/app-styles'/f'{font}.ttf' if origin=='design' else ROOT/origin/'fonts'/f'{font}.ttf'
  notice=ROOT.parent/'design/app-styles'/f'{font}-OFL.txt' if origin=='design' else ROOT/origin/'licenses'/f'{font}-OFL.txt'
  if not notice.exists():notice=ROOT/origin/'fonts'/f'{font}-OFL.txt'
  shutil.copyfile(origin_font,base/'fonts'/f'{font}.ttf');shutil.copyfile(notice,base/'licenses'/f'{font}-OFL.txt')
 (base/'licenses/ARTWORK.txt').write_text(f'Original {c["medium"]}-inspired artwork created for Veyra with OpenAI image generation. Original functional vector glyphs and badge treatment for Veyra; official waveform retained. No reference photos or third-party characters redistributed.\n')
 (base/'licenses/FONT-NOTES.txt').write_text('Unmodified fonts; upstream SIL Open Font License notices are included.\n')
 assets=dict(header='artwork/preview.webp',player='artwork/preview.webp',preview='artwork/preview.webp',brandIcon='artwork/brand.png',headingFont=f'fonts/{c["font"]}.ttf',bodyFont='fonts/Manrope.ttf',**{k:f'icons/{k}.png' for k in paths})
 m=dict(schema=3,id=c['id'],name=c['name'],author='Bhavishya Dalal',version=1,minAppCode=30,
  palette=dict(zip(['background','surface','raised','accent','accentContainer','text','muted','outline','secondary'],[bg,surface,raised,accent,container,text,muted,outline,secondary])),
  rowCorner=18 if c['scene']=='sketch' else 24,buttonCorner=26 if c['scene']=='sketch' else 30,navCorner=32,
  sceneStyle=c['scene'],seekStyle=c['seek'],seekHeight=8,tagline=c['tagline'],assets=assets)
 m['files']={str(p.relative_to(base)):digest(p.read_bytes()) for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
 (base/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
 data=io.BytesIO()
 with zipfile.ZipFile(data,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(base.rglob('*')):
   if p.is_file():
    entry=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,23,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED;z.writestr(entry,p.read_bytes())
 pack=ROOT/'packs'/f'{c["id"]}-v1.zip';payload=data.getvalue()
 if pack.exists() and pack.read_bytes()!=payload:raise RuntimeError(f'{pack.name} is immutable: bump version')
 pack.write_bytes(payload);sha=digest(payload);pack.with_suffix('.zip.sha256').write_text(f'{sha}  {pack.name}\n')
 return dict(id=c['id'],name=c['name'],description=c['description'],version=1,minAppCode=30,url=URL+'packs/'+pack.name,sha256=sha,bytes=len(payload),preview=URL+c['id']+'/artwork/preview.webp')

if __name__=='__main__':
 entries=[build(c,i) for i,c in enumerate(COLLECTION)]
 catalog=json.loads((ROOT/'catalog.json').read_text());ids={c['id'] for c in COLLECTION};catalog['themes']=entries+[e for e in catalog['themes'] if e['id'] not in ids]
 text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
 (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
 print(json.dumps([{'id':e['id'],'bytes':e['bytes'],'sha256':e['sha256']} for e in entries],indent=2))
