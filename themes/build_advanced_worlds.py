"""Build immutable schema-4 packs. Original glyph paths + native material/motion presets.
Run with CairoSVG installed; regenerate artwork separately with the recorded art direction.
"""
from pathlib import Path
import io,json,hashlib,shutil,zipfile,xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parent
URL='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/themes/'
COLLECTION=[
 dict(id='switchback-radio',name='Switchback Radio',medium='Pixel nocturne',material='pixel',seek='road',font='Rajdhani',fontSource='midnight-express',tagline='Take the long way home.',colors=['071626','11283C','213C51','FFD18A','4C3B27','FFF7E6','D7E1E7','89A5B7','A5D3E8'],description='Pixel-cut controls, stepped glyphs, lane-marker seek and a brisk lateral page reveal beneath an amber mountain night.'),
 dict(id='komorebi-hour',name='Komorebi Hour',medium='Botanical cinema',material='botanical',seek='vine',font='CormorantGaramond',fontSource='design',tagline='Stay where the light falls.',colors=['17231B','26382A','3A4938','EED6A0','4D422A','FFF8E5','E0E3C9','A3AD85','C5D5A2'],description='Botanical linework, alternating soft corners, a leaf-vine progress track and a gentle upward page reveal in golden-hour foliage.'),
 dict(id='rooftop-reverie',name='Rooftop Reverie',medium='Architectural dusk',material='deco',seek='skyline',font='SpaceGrotesk',fontSource='astral-tide',tagline='A city of songs, after sunset.',colors=['151E35','252F46','3D465E','FFD0BA','503934','FFF3E9','E1DBE8','AA9DBA','BACFEE'],description='Art Deco cut frames, architectural double-line glyphs, skyline progress and a restrained depth reveal above a coral-cloud metropolis.'),
 dict(id='apex-telemetry',name='Apex Telemetry',medium='Motorsport engineering',material='telemetry',seek='telemetry',font='Rajdhani',fontSource='midnight-express',tagline='Every beat. Full commitment.',colors=['17191E','272B32','3C4149','FFCD99','513A2B','FFF4E8','E0DFDA','A9A59B','F3DE98'],description='Asymmetric instrument panels, precision race glyphs, sector-tick progress and fast lateral page motion. Original unbranded endurance artwork.'),
 dict(id='cinder-kata',name='Cinder Kata',medium='Anime ink & flame',material='flame',seek='flame',font='BarlowCondensed',fontSource='web-slinger',tagline='Let the quiet catch fire.',colors=['102528','1F3638','364B4B','FFD294','533D26','FFF7E4','E0E1CE','A2B9AE','B6DED0'],description='Flame-leaf corners, tapered brush glyphs, ember-ribbon progress and a short depth reveal. An original sword and flame composition.'),
]
def digest(b):return hashlib.sha256(b).hexdigest()
def build(c,index):
 import cairosvg
 from build_illustrated_collection import paths_for
 p=json.loads((ROOT/'advanced-glyphs.json').read_text())['paths']
 paths=paths_for(0);paths.update(p)
 # Navigation remains recognizable, but each world gets its own core architecture.
 if index==0: paths.update(nav_home='M3 10L12 2L21 10V21H3Z M7 21V13H17V21 M7 16H17 M7 19H17',nav_explore='M12 2V6 M12 18V22 M2 12H6 M18 12H22 M12 6L18 12L12 18L6 12Z',play='M6 3H9V6H13V9H17V12H20V15H17V18H13V21H6Z')
 if index==1: paths.update(nav_home='M3 11L12 3L21 11 M5 10V21H19V10 M9 21V15H15V21 M3 16Q0 11 5 7 M20 18Q24 12 20 7',nav_explore='M12 2Q23 4 21 14Q17 23 6 21Q0 17 3 8Q5 3 12 2Z M7 17Q7 6 17 7Q18 17 7 17 M7 17L16 8')
 if index==2: paths.update(nav_home='M3 21V9L12 2L21 9V21Z M7 21V11H17V21 M10 21V15H14V21 M12 4V7',nav_library='M3 21V5H8V21 M10 21V2H15V21 M17 21V8H22V21 M5 8H6 M12 5H13 M19 11H20')
 if index==3: paths.update(nav_home='M3 21V8L12 3L21 8V21Z M6 21V11H18V21 M6 15H18 M6 18H18',nav_explore='M12 2L22 12L12 22L2 12Z M12 6V9 M18 12H15 M12 18V15 M6 12H9 M12 12L16 8',play='M5 3L21 12L5 21Z M2 8V16')
 if index==4: paths.update(nav_home='M3 11L12 3L21 11 M5 11V21H19V11 M9 21V16H15V21 M3 6Q6 5 4 1 M20 7Q23 4 20 2',nav_explore='M12 2Q22 9 20 17Q14 24 6 20Q0 15 5 7Q5 14 9 13Q14 10 12 2Z M10 18Q8 13 13 10Q13 15 16 17')
 base=ROOT/c['id'];source=ROOT/'source-icons'/c['id']
 for sub in ['icons','artwork','fonts','licenses']:(base/sub).mkdir(parents=True,exist_ok=True)
 source.mkdir(parents=True,exist_ok=True)
 for key,d in paths.items():
  cap='square' if index in [0,2,3] else 'round';join='miter' if index in [0,2,3] else 'round'
  width=[1.75,1.65,1.2,1.75,1.9][index]
  ornament=['<path d="M19 2H22V5" stroke-width="1"/>','<path d="M20 2Q24 2 22 6Q19 5 20 2Z" stroke-width=".65"/>','<path d="M18 2H22V6 M19 3H21V5" stroke-width=".6"/>','<path d="M18 3H23 M20 1L18 5" stroke-width=".8"/>','<path d="M20 1Q24 4 21 7Q22 4 19 4Z" stroke-width=".75"/>'][index]
  # Small pixel worlds use crisp square joins; other worlds use distinctive ink families.
  extra=f'<path d="{d}" transform="translate(.35 .2)" stroke-width=".4" opacity=".5"/>' if index==2 else ''
  svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="-1 -1 26 26"><g fill="none" stroke="white" stroke-width="{width}" stroke-linecap="{cap}" stroke-linejoin="{join}"><path d="{d}"/>{extra}{ornament}</g></svg>'
  (source/f'{key}.svg').write_text(svg);cairosvg.svg2png(bytestring=svg.encode(),write_to=str(base/'icons'/f'{key}.png'))
 bg,surface,raised,accent,container,text,muted,outline,secondary=['#'+v for v in c['colors']]
 wave=ET.fromstring((ROOT/'source-icons/margin-notes/brand.svg').read_text()).find('.//{http://www.w3.org/2000/svg}g/{http://www.w3.org/2000/svg}path').attrib['d']
 frames=[
 '<path d="M20 5H108V14H117V23H124V105H115V115H105V124H23V115H13V105H5V23H13V14H20Z"/>',
 '<path d="M35 6Q119 0 121 39L118 89Q114 124 84 122L29 119Q0 115 6 78L9 37Q12 8 35 6Z"/>',
 '<path d="M24 6H104L122 24V104L104 122H24L6 104V24Z"/>',
 '<path d="M11 5H100L123 28V115H28L5 92V11Z"/>',
 '<path d="M15 5Q84 0 116 26Q128 50 118 94Q89 128 14 120Q23 85 9 56Q1 24 15 5Z"/>',
 ][index]
 brand=f'<svg xmlns="http://www.w3.org/2000/svg" width="256" height="256" viewBox="0 0 128 128"><g fill="{surface}" stroke="{accent}" stroke-width="2">{frames}</g><path d="M23 110H103" stroke="{secondary}" stroke-width="2"/><g transform="translate(10 7)"><path d="{wave}" fill="{text}" stroke="{accent}" stroke-width=".6"/></g></svg>'
 (source/'brand.svg').write_text(brand);cairosvg.svg2png(bytestring=brand.encode(),write_to=str(base/'artwork/brand.png'))
 for font,origin in [('Manrope','midnight-express'),(c['font'],c['fontSource'])]:
  origin=ROOT.parent/'design/app-styles' if origin=='design' else ROOT/origin
  f=origin/f'{font}.ttf' if c['fontSource']=='design' and font!='Manrope' else origin/'fonts'/f'{font}.ttf'
  notice=origin/f'{font}-OFL.txt' if origin.name=='app-styles' else origin/'licenses'/f'{font}-OFL.txt'
  if not notice.exists():notice=origin/'fonts'/f'{font}-OFL.txt'
  shutil.copyfile(f,base/'fonts'/f'{font}.ttf');shutil.copyfile(notice,base/'licenses'/f'{font}-OFL.txt')
 (base/'licenses/ARTWORK.txt').write_text('Original artwork generated with OpenAI image generation for Veyra, using owner-provided references for art direction. Original functional SVG paths and themed badge; official Veyra waveform retained. No reference image, third-party logo or character redistributed.\n')
 (base/'licenses/FONT-NOTES.txt').write_text('Unmodified fonts under included upstream SIL Open Font Licenses.\n')
 assets=dict(header='artwork/preview.webp',player='artwork/preview.webp',preview='artwork/preview.webp',brandIcon='artwork/brand.png',headingFont=f'fonts/{c["font"]}.ttf',bodyFont='fonts/Manrope.ttf',**{k:f'icons/{k}.png' for k in paths})
 m=dict(schema=4,id=c['id'],name=c['name'],author='Bhavishya Dalal',version=1,minAppCode=31,palette=dict(zip(['background','surface','raised','accent','accentContainer','text','muted','outline','secondary'],[bg,surface,raised,accent,container,text,muted,outline,secondary])),rowCorner=20,buttonCorner=24,navCorner=32,sceneStyle='mist',materialStyle=c['material'],seekStyle=c['seek'],seekHeight=10,tagline=c['tagline'],assets=assets)
 m['files']={str(p.relative_to(base)):digest(p.read_bytes()) for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
 assert len(m['files'])<=128
 (base/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
 data=io.BytesIO()
 with zipfile.ZipFile(data,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for p in sorted(base.rglob('*')):
   if p.is_file():
    e=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,23,0,0,0));e.compress_type=zipfile.ZIP_DEFLATED;z.writestr(e,p.read_bytes())
 pack=ROOT/'packs'/f'{c["id"]}-v1.zip';payload=data.getvalue()
 if pack.exists() and pack.read_bytes()!=payload:raise RuntimeError('Published packs are immutable; bump version')
 pack.write_bytes(payload);pack.with_suffix('.zip.sha256').write_text(f'{digest(payload)}  {pack.name}\n')
 return dict(id=c['id'],name=c['name'],description=c['description'],version=1,minAppCode=31,url=URL+'packs/'+pack.name,sha256=digest(payload),bytes=len(payload),preview=URL+c['id']+'/artwork/preview.webp')
if __name__=='__main__':
 entries=[build(c,i) for i,c in enumerate(COLLECTION)]
 catalog=json.loads((ROOT/'catalog.json').read_text());ids={c['id'] for c in COLLECTION};catalog['themes']=entries+[e for e in catalog['themes'] if e['id'] not in ids]
 text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
 (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
 print(json.dumps([{'id':e['id'],'bytes':e['bytes']} for e in entries],indent=2))
