"""Five original full-color punk themes. Immutable schema-4 ZIPs require build 32.
AI art-direction boards are previews only; runtime glyphs come from original SVG masters.
"""
from pathlib import Path
import hashlib,io,json,shutil,zipfile,xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parent
URL='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/themes/'
COLLECTION=[
 dict(id='riot-candy',name='Riot Candy',font='RubikWetPaint',material='punk_gel',seek='gel',medium='Gel, chrome & torn zine',tagline='Loud little world.',colors=['0E0D12','1D1A26','302B3F','D7FF3B','334512','FFF5DE','D9CFDE','9B8BAA','FFA6DB'],inks=['D7FF3B','FF55B5','B78BFF','FFF5DE'],description='Acid-lime and pink gel controls, chrome glints, wet-paint lettering, star-head seek and elastic press feedback.'),
 dict(id='chrome-venom',name='Chrome Venom',font='RubikGlitch',material='punk_rivet',seek='cable',medium='Riveted biotech metal',tagline='Bite the silence.',colors=['0B1411','15251E','2B3A30','C2FF83','28442B','F1F9EE','CDDCCD','829E88','D5E4EA'],inks=['BAFF84','D5E4EA','FFD379','91E6B2'],description='Toxic mint, dark titanium, riveted cut frames, glitch display type, braided-cable progress and a quick mechanical reveal.'),
 dict(id='bubble-riot',name='Bubble Riot',font='RubikBubbles',material='punk_sticker',seek='beads',medium='Jelly sticker street punk',tagline='Make some noise.',colors=['1E1234','302047','49305E','FFB8E7','533047','FFF3E6','E8D1EB','AC87BE','AEEAFF'],inks=['8CE7FF','FF91D5','FFE98B','D6B7FF'],description='Sky-blue and pink jelly, puffy sticker glyphs, bubble lettering, bead progress and a buoyant upward page reveal.'),
 dict(id='xerox-afterparty',name='Xerox Afterparty',font='Lacquer',material='punk_torn',seek='tape',medium='Photocopy concert zine',tagline='Stay loud. Stay late.',colors=['181615','292521','40382E','FFE374','4A3B20','FFF6DE','E1D6C5','AD9E87','FFB5A2'],inks=['FFF6DE','FF7C7C','FFE374','D9D0C3'],description='Cream, charcoal and signal-red print accents, rough marker lettering, ink-stamp controls, cassette-tape progress and a short paper slide.'),
 dict(id='cosmic-vandal',name='Cosmic Vandal',font='BungeeShade',material='punk_chrome',seek='plasma',medium='Holographic graffiti chrome',tagline='Leave a louder universe.',colors=['151024','241B3A','382A50','B9F7FF','254454','FAF1FF','DCCFEB','AD92BF','F5B4ED'],inks=['A9F7FF','FFAFE9','DCC5FF','F2F0FF'],description='Prismatic cyan/pink chrome, striped poster type, orbital glyphs, plasma progress and a restrained depth reveal.'),
]
def digest(b):return hashlib.sha256(b).hexdigest()
def glyph_paths():
 from build_illustrated_collection import paths_for
 d=paths_for(0);d.update(json.loads((ROOT/'advanced-glyphs.json').read_text())['paths'])
 d.update(queue='M7 5H22 M7 12H22 M7 19H22 M2 5H3 M2 12H3 M2 19H3',nav_library='M3 3V21 M8 3V21 M13 5L17 4L22 20L18 21Z',play='M8 4L21 12L8 20Z',pause='M6 4H10V20H6Z M15 4H19V20H15Z',previous='M5 4V20 M20 4L7 12L20 20Z',next='M19 4V20 M4 4L17 12L4 20Z')
 return d

def svg(c,key,d,index,pressed=False):
 colors=['#'+v for v in c['inks']];col=colors[index%4];bg='#'+c['colors'][0];ivory='#'+c['colors'][5]
 transport=key in ['play','pause','previous','next']
 grad=f'<linearGradient id="ink" x1="0" y1="0" x2="1" y2="1" gradientUnits="objectBoundingBox"><stop stop-color="{ivory}"/><stop offset=".25" stop-color="{col}"/><stop offset=".8" stop-color="{col}"/><stop offset="1" stop-color="{ivory}"/></linearGradient>'
 gel=f'<radialGradient id="gel" cx=".3" cy=".15" r=".9"><stop stop-color="{ivory}"/><stop offset=".25" stop-color="{col}"/><stop offset=".75" stop-color="{col}"/><stop offset="1" stop-color="{bg}"/></radialGradient>'
 frame='';ink='url(#ink)';group='';extra=''
 if transport:
  if c['material']=='punk_rivet':
   frame=f'<path d="M8 1H18L25 8V18L18 25H8L1 18V8Z" fill="{bg}" stroke="url(#ink)" stroke-width="1.2"/><path d="M8 3H18L23 8V18L18 23H8L3 18V8Z" fill="none" stroke="{col}" stroke-width=".45"/>'
   for x,y in [(7,4),(20,7),(20,20),(7,22)]:frame+=f'<circle cx="{x}" cy="{y}" r=".7" fill="{ivory}"/>'
  elif c['material']=='punk_torn':frame=f'<path d="M2 3L6 2L9 3L14 1L18 3L23 2L25 8L23 12L25 17L23 24L18 23L13 25L9 23L3 24L1 18L3 12L1 8Z" fill="{ivory if pressed else bg}" stroke="{col}" stroke-width=".7"/>'
  else:
   frame=f'<circle cx="13" cy="13" r="12" fill="url(#gel)" stroke="{ivory}" stroke-width=".45"/><circle cx="13" cy="13" r="10.7" fill="none" stroke="{bg}" stroke-opacity=".35" stroke-width=".4"/><path d="M5 7Q10 1 18 4" fill="none" stroke="white" stroke-opacity=".7" stroke-width=".8" stroke-linecap="round"/>'
   if c['material']=='punk_chrome':frame+=f'<path d="M3 16L8 3L19 5L24 15L17 24Z" fill="none" stroke="{ivory}" stroke-width=".4"/>'
  ink=col if c['material']=='punk_rivet' else bg
  group='translate(4 4) scale(.72)'
  if c['material']=='punk_torn':ink=bg if pressed else ivory
 else:
  if c['material']=='punk_rivet':extra=f'<path d="M2 8V2H8 M18 2H24V8 M24 18V24H18 M8 24H2V18" fill="none" stroke="{ivory}" stroke-opacity=".6" stroke-width=".65"/>'
  elif c['material']=='punk_sticker':extra=f'<path d="M19 3L20 1L21 3L24 4L21 5L20 7L19 5L17 4Z" fill="{col}"/>'
  elif c['material']=='punk_torn':extra=f'<path d="M2 23L6 22 M21 2L24 3" stroke="{col}" stroke-width=".8"/>'
  elif c['material']=='punk_chrome':extra=f'<ellipse cx="13" cy="13" rx="12" ry="8" transform="rotate(-30 13 13)" fill="none" stroke="{col}" stroke-opacity=".6" stroke-width=".45"/>'
  else:extra=f'<path d="M21 1L22 3L25 4L22 5L21 8L20 5L17 4L20 3Z" fill="{col}"/>'
 # Sturdy outlines keep all colored functional glyphs readable on bright artwork.
 cap='square' if c['material'] in ['punk_rivet','punk_torn'] else 'round'
 fill=ink if transport else 'none'
 paths=f'<g transform="{group}" fill="{fill}" stroke-linejoin="round" stroke-linecap="{cap}"><path d="{d}" stroke="{bg}" stroke-width="3.2" fill="{fill}"/><path d="{d}" stroke="{ink}" stroke-width="1.8" fill="{fill}"/></g>'
 content=frame+paths+extra
 if pressed:content=f'<g transform="translate(1 2) scale(.93 .88)" opacity=".88">{content}</g>'
 return f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="-1 -1 28 28"><defs>{grad}{gel}</defs>{content}</svg>'

def build(c):
 import cairosvg
 base=ROOT/c['id'];source=ROOT/'source-icons'/c['id']
 for sub in ['artwork','icons','fonts','licenses']:(base/sub).mkdir(parents=True,exist_ok=True)
 source.mkdir(parents=True,exist_ok=True)
 paths=glyph_paths();assets={}
 for i,(key,d) in enumerate(paths.items()):
  for pressed in [False,True] if key in ['play','pause','previous','next'] else [False]:
   role=key+('_pressed' if pressed else '');v=svg(c,key,d,i,pressed)
   (source/f'{role}.svg').write_text(v)
   pixels=256 if key in ['play','pause','previous','next'] else 128
   cairosvg.svg2png(bytestring=v.encode(),write_to=str(base/'icons'/f'{role}.png'),output_width=pixels,output_height=pixels)
   assets[role]=f'icons/{role}.png'
 wave=ET.fromstring((ROOT/'source-icons/margin-notes/brand.svg').read_text()).find('.//{http://www.w3.org/2000/svg}g/{http://www.w3.org/2000/svg}path').attrib['d']
 bg,surface,raised,accent,container,text,muted,outline,secondary=['#'+v for v in c['colors']]
 brand=f'<svg xmlns="http://www.w3.org/2000/svg" width="256" height="256" viewBox="0 0 128 128"><defs><linearGradient id="b"><stop stop-color="{accent}"/><stop offset=".5" stop-color="{secondary}"/><stop offset="1" stop-color="{text}"/></linearGradient></defs><rect x="5" y="5" width="118" height="118" rx="26" fill="{bg}" stroke="url(#b)" stroke-width="3"/><path d="M14 111H115 M18 15H87" stroke="{secondary}" stroke-width="2"/><g transform="translate(10 7)"><path d="{wave}" fill="url(#b)" stroke="{text}" stroke-width=".6"/></g></svg>'
 (source/'brand.svg').write_text(brand);cairosvg.svg2png(bytestring=brand.encode(),write_to=str(base/'artwork/brand.png'))
 for font in ['Manrope',c['font']]:
  origin=ROOT/'midnight-express/fonts/Manrope.ttf' if font=='Manrope' else ROOT.parent/'design/punk-fonts'/f'{font}.ttf'
  notice=ROOT/'midnight-express/licenses/Manrope-OFL.txt' if font=='Manrope' else ROOT.parent/'design/punk-fonts'/f'{font}-OFL.txt'
  shutil.copyfile(origin,base/'fonts'/f'{font}.ttf');shutil.copyfile(notice,base/'licenses'/f'{font}-OFL.txt')
 (base/'licenses/ARTWORK.txt').write_text('Original material-collage wallpaper and separate art-direction study generated with OpenAI image generation for Veyra. Runtime glyphs and skins are original SVG designs, not extracted from an AI board. Official Veyra waveform retained. No third-party logos or characters.\n')
 (base/'licenses/FONT-NOTES.txt').write_text('Unmodified heading and body fonts; original upstream SIL Open Font License notices included. AI-board lettering is art direction, not the installed font.\n')
 assets=dict(header='artwork/preview.webp',player='artwork/preview.webp',preview='artwork/preview.webp',brandIcon='artwork/brand.png',headingFont=f'fonts/{c["font"]}.ttf',bodyFont='fonts/Manrope.ttf',**assets)
 m=dict(schema=4,id=c['id'],name=c['name'],author='Bhavishya Dalal',version=1,minAppCode=32,palette=dict(zip(['background','surface','raised','accent','accentContainer','text','muted','outline','secondary'],[bg,surface,raised,accent,container,text,muted,outline,secondary])),rowCorner=22,buttonCorner=28,navCorner=32,sceneStyle='mist',materialStyle=c['material'],iconStyle='original',typeStyle='punk',seekStyle=c['seek'],seekHeight=11,tagline=c['tagline'],assets=assets)
 m['files']={str(p.relative_to(base)):digest(p.read_bytes()) for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
 assert len(m['files'])<=128 and len(assets)==123
 (base/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
 data=io.BytesIO()
 with zipfile.ZipFile(data,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(base.rglob('*')):
   if p.is_file():
    e=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,23,0,0,0));e.compress_type=zipfile.ZIP_DEFLATED;z.writestr(e,p.read_bytes())
 pack=ROOT/'packs'/f'{c["id"]}-v1.zip';payload=data.getvalue()
 if pack.exists() and pack.read_bytes()!=payload:raise RuntimeError('Published packs are immutable; bump version')
 pack.write_bytes(payload);pack.with_suffix('.zip.sha256').write_text(f'{digest(payload)}  {pack.name}\n')
 return dict(id=c['id'],name=c['name'],description=c['description'],version=1,minAppCode=32,url=URL+'packs/'+pack.name,sha256=digest(payload),bytes=len(payload),preview=URL+c['id']+'/artwork/preview.webp')
if __name__=='__main__':
 entries=[build(c) for c in COLLECTION]
 catalog=json.loads((ROOT/'catalog.json').read_text());ids={c['id'] for c in COLLECTION};catalog['themes']=entries+[e for e in catalog['themes'] if e['id'] not in ids]
 text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
 (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
 print(json.dumps([{'id':e['id'],'bytes':e['bytes']} for e in entries],indent=2))
