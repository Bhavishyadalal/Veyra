#!/usr/bin/env python3
"""Build five independent asset overlays and honest previews of the shipped glyphs/fonts."""
from pathlib import Path
import hashlib,io,json,math,shutil,sys,zipfile
ROOT=Path(__file__).resolve().parent
sys.path.insert(0,str(ROOT.parent/'themes'))
from build_punk_worlds import glyph_paths
from PIL import Image,ImageDraw,ImageFont
import cairosvg
URL='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/asset-packs/'
SPECS=[
 dict(id='spray-parade',name='Spray Parade',font='Bangers',material='punk_torn',seek='tape',motion='punk_torn',ink='#D7FF3B',colors=['#D7FF3B','#FF4BAC','#F3E9CB'],description='Graffiti marker glyphs, paint-stamp transports, torn frames and a short paper slide.'),
 dict(id='paper-pop',name='Paper Pop',font='LilitaOne',material='punk_sticker',seek='beads',motion='punk_sticker',ink='#FFD783',colors=['#D7B8FF','#FFACAB','#FFD783'],description='Scalloped cut-paper controls, offset collage shadows, playful rounded lettering and a buoyant reveal.'),
 dict(id='ink-rebel',name='Ink Rebel',font='RockSalt',material='punk_torn',seek='sketch',motion='deco',ink='#F2E7CC',colors=['#F2E7CC','#FF7864','#C1C9B3'],description='Dry-brush ink symbols, washi stamp frames, handwritten display lettering and a quiet depth reveal.'),
 dict(id='laser-club',name='Laser Club',font='Audiowide',material='punk_chrome',seek='plasma',motion='punk_chrome',ink='#A4EEFF',colors=['#A4EEFF','#DDB3FF','#FFB8D8'],description='Beveled arcade keys, neon outline glyphs, futuristic display type and plasma-style progress.'),
 dict(id='thread-riot',name='Thread Riot',font='Bungee',material='botanical',seek='vine',motion='botanical',ink='#B9E7D3',colors=['#B9E7D3','#FFB297','#C5DAF9'],description='Stitched patch glyphs, denim transports, varsity lettering, woven-line progress and a soft upward reveal.'),
]
def sha(b):return hashlib.sha256(b).hexdigest()
def svg(c,key,path,i,pressed=False):
 col=c['colors'][i%3];dark='#17151E';cream='#FFF1DA';isbutton=key in ('play','pause','previous','next')
 defs=f'<linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop stop-color="{cream}"/><stop offset=".4" stop-color="{col}"/><stop offset="1" stop-color="{c["colors"][(i+1)%3]}"/></linearGradient>'
 frame='';extra='';fill='none';stroke=col;transform='translate(0 0)'
 if isbutton:
  transform='translate(4 4) scale(.7)';fill=dark;stroke=dark
  if c['id']=='spray-parade':
   frame=f'<path d="M4 2L10 3L14 1L18 3L24 4L23 10L25 15L22 23L16 24L11 22L4 25L2 18L3 12L1 7Z" fill="{col}" stroke="{cream}" stroke-width=".65"/>'
   extra=f'<path d="M6 22V25 M20 21V24 M3 5L1 3 M24 8L26 6" stroke="{col}" stroke-width="1.4"/>'
  elif c['id']=='paper-pop':
   points=[]
   for a in range(48):
    angle=a*math.pi/24;r=12 if a%2==0 else 10.9
    points.append(f'{13+math.cos(angle)*r:.3f},{13+math.sin(angle)*r:.3f}')
   frame=f'<polygon points="{" ".join(points)}" transform="translate(.8 1)" fill="#5B3859"/><polygon points="{" ".join(points)}" fill="{col}" stroke="{cream}" stroke-width=".6"/>'
   stroke='#654174';fill=stroke
  elif c['id']=='ink-rebel':
   frame=f'<path d="M4 4Q12 -1 21 4L25 11L22 22L14 24L4 22L1 15Z" fill="{col}"/><path d="M3 3L11 2M17 2L24 8M24 18L20 24M9 24L2 20" fill="none" stroke="{col}" stroke-width="1.3"/>'
  elif c['id']=='laser-club':
   frame=f'<rect x="1.5" y="1.5" width="23" height="23" rx="4" fill="{dark}" stroke="url(#g)" stroke-width="1.4"/><rect x="3.5" y="3.5" width="19" height="19" rx="2.5" fill="none" stroke="{col}" stroke-width=".4"/><path d="M5 3H20" stroke="white" stroke-width=".5"/>'
   stroke='url(#g)';fill=stroke
  else:
   frame=f'<circle cx="13" cy="13" r="12" fill="#284667" stroke="{col}" stroke-width=".9"/><circle cx="13" cy="13" r="10.5" fill="none" stroke="{cream}" stroke-width=".5" stroke-dasharray="1.2 .9"/>'
   stroke=col;fill=col
 else:
  if c['id']=='paper-pop':extra=f'<path d="M2 24H24" stroke="{c["colors"][(i+1)%3]}" stroke-width="1.6"/>'
  if c['id']=='spray-parade':extra=f'<path d="M1 23L6 22 M22 2L25 3" stroke="{col}" stroke-width="1.4"/><circle cx="24" cy="22" r=".8" fill="{col}"/>'
  if c['id']=='ink-rebel':extra=f'<path d="M1 23L6 22L10 23 M22 1L25 3" stroke="{col}" stroke-width=".6"/>'
  if c['id']=='laser-club':extra=f'<path d="M1 8V1H8 M18 25H25V18" stroke="{col}" stroke-width=".6" fill="none"/>'
  if c['id']=='thread-riot':extra=f'<rect x=".6" y=".6" width="24.8" height="24.8" rx="5" fill="#20394D" stroke="{col}" stroke-width=".5" stroke-dasharray="1.2 .8"/>'
 under=extra if not isbutton and c['id']=='thread-riot' else ''
 edge='square' if c['id'] in ('spray-parade','ink-rebel','laser-club') else 'round'
 # Two-pass strokes preserve legibility over arbitrary theme artwork.
 glyph=f'<g transform="{transform}" stroke-linecap="{edge}" stroke-linejoin="round"><path d="{path}" fill="{fill}" stroke="{dark}" stroke-width="3.1"/><path d="{path}" fill="{fill}" stroke="{stroke}" stroke-width="1.7"/></g>'
 if c['id']=='ink-rebel':glyph+=f'<path d="M7 24L12 23M17 24L20 23" stroke="{col}" stroke-width=".35"/>'
 content=under+frame+glyph+('' if under else extra)
 if pressed:content=f'<g transform="translate(.8 1.5) scale(.94 .91)" opacity=".85">{content}</g>'
 return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="-1 -1 28 28" width="128" height="128"><defs>{defs}</defs>{content}</svg>'
def render(c,base,assets):
 out=ROOT/'previews'/c['id'];out.mkdir(parents=True,exist_ok=True)
 body=ImageFont.truetype(str(base/'fonts/Manrope.ttf'),24);small=ImageFont.truetype(str(base/'fonts/Manrope.ttf'),16)
 heading=ImageFont.truetype(str(base/f'fonts/{c["font"]}.ttf'),52 if c['id']!='ink-rebel' else 36)
 im=Image.new('RGB',(1080,780),'#17151E');d=ImageDraw.Draw(im)
 d.text((36,22),'VEYRA / INDEPENDENT ASSET PACK',font=small,fill='#C4C0CC')
 d.text((32,65),c['name'].upper(),font=heading,fill=c['ink'])
 d.text((36,146),'Your theme artwork stays. Your details change.',font=body,fill='#E9E5EF')
 roles=['play','pause','previous','next','nav_home','nav_search','nav_explore','nav_library','settings','back','queue','lyrics']
 for i,key in enumerate(roles):
  x=36+(i%6)*172;y=205+(i//6)*168
  icon=Image.open(base/assets[key]).convert('RGBA').resize((112,112),Image.Resampling.LANCZOS);im.paste(icon,(x,y),icon)
  d.text((x,y+119),key.replace('nav_',''),font=small,fill='#D4CADC')
 d.text((36,565),'Aa / Heading lettering',font=heading,fill=c['ink'])
 d.text((36,650),'Manrope keeps song titles and small labels readable.',font=body,fill='#EFE9F4')
 d.text((36,706),f'Native {c["seek"]} progress + {c["motion"]} page motion / build 33+',font=small,fill='#C4C0CC')
 im.save(out/'specimen.png');im.resize((720,520),Image.Resampling.LANCZOS).save(base/'preview.webp',quality=87)
 rows=math.ceil(len(assets)/9);sheet=Image.new('RGB',(1260,rows*145+100),'#17151E');sd=ImageDraw.Draw(sheet);sd.text((25,18),c['name']+' / actual runtime roles',font=body,fill=c['ink'])
 for i,(key,path) in enumerate(assets.items()):
  x=20+(i%9)*138;y=80+(i//9)*145
  if key.endswith('Font'):sd.text((x,y+20),'Aa',font=heading,fill=c['ink'])
  else:
   icon=Image.open(base/path).convert('RGBA').resize((88,88),Image.Resampling.LANCZOS);sheet.paste(icon,(x,y),icon)
  sd.text((x,y+94),key,font=ImageFont.truetype(str(base/'fonts/Manrope.ttf'),12),fill='#E5DCE9')
 sheet.save(out/'all-assets.png')
def build(c):
 base=ROOT/c['id'];vectors=ROOT/'vectors'/c['id']
 for part in ('icons','fonts','licenses'):(base/part).mkdir(parents=True,exist_ok=True)
 vectors.mkdir(parents=True,exist_ok=True);assets={}
 for i,(key,path) in enumerate(glyph_paths().items()):
  for pressed in ([False,True] if key in ('play','pause','previous','next') else [False]):
   role=key+('_pressed' if pressed else '');s=svg(c,key,path,i,pressed);(vectors/f'{role}.svg').write_text(s)
   size=256 if key in ('play','pause','previous','next') else 128
   cairosvg.svg2png(bytestring=s.encode(),write_to=str(base/'icons'/f'{role}.png'),output_width=size,output_height=size)
   assets[role]=f'icons/{role}.png'
 for name in (c['font'],'Manrope'):
  source=ROOT.parent/'design/asset-fonts'/f'{name}.ttf' if name!='Manrope' else ROOT.parent/'themes/midnight-express/fonts/Manrope.ttf'
  license=ROOT.parent/'design/asset-fonts'/f'{name}-LICENSE.txt' if name!='Manrope' else ROOT.parent/'themes/midnight-express/licenses/Manrope-OFL.txt'
  shutil.copyfile(source,base/'fonts'/f'{name}.ttf');shutil.copyfile(license,base/'licenses'/f'{name}-LICENSE.txt')
 assets.update(headingFont=f'fonts/{c["font"]}.ttf',bodyFont='fonts/Manrope.ttf')
 (base/'licenses/ASSETS.txt').write_text('Original Veyra semantic SVG glyphs and five new material treatments. Runtime assets are authored vector designs, not crops from AI concept art. Heading/body font license notices included unmodified. No remote code, layout files, brand replacements or backgrounds.\n')
 m=dict(schema=1,id=c['id'],name=c['name'],author='Bhavishya Dalal',version=1,minAppCode=33,assets=assets,
  materialStyle=c['material'],seekStyle=c['seek'],seekHeight=10,rowCorner=22,buttonCorner=28,navCorner=32,headingInk=c['ink'],motionStyle=c['motion'])
 render(c,base,assets)
 (base/"previews").mkdir(exist_ok=True);shutil.copyfile(base/"preview.webp",base/"previews/specimen.webp")
 m['files']={str(p.relative_to(base)):sha(p.read_bytes()) for p in sorted(base.rglob('*')) if p.is_file() and p.name not in ('assets.json','preview.webp')}
 assert len(assets)==119 and len(m['files'])<=128
 (base/'assets.json').write_text(json.dumps(m,indent=2)+'\n')
 buffer=io.BytesIO()
 with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for member in ['assets.json']+sorted(m['files']):
   e=zipfile.ZipInfo(member,date_time=(2026,10,23,0,0,0));e.compress_type=zipfile.ZIP_DEFLATED;z.writestr(e,(base/member).read_bytes())
 payload=buffer.getvalue();pack=ROOT/'packs'/f'{c["id"]}-v1.zip';pack.parent.mkdir(exist_ok=True)
 if pack.exists() and pack.read_bytes()!=payload:raise ValueError('Published asset packs are immutable. Bump version.')
 pack.write_bytes(payload);pack.with_suffix('.zip.sha256').write_text(f'{sha(payload)}  {pack.name}\n')
 return dict(id=c['id'],name=c['name'],description=c['description'],version=1,minAppCode=33,url=URL+'packs/'+pack.name,sha256=sha(payload),bytes=len(payload),preview=URL+c['id']+'/preview.webp')
if __name__=='__main__':
 entries=[build(c) for c in SPECS];text=json.dumps(dict(schema=1,packs=entries),indent=2)+'\n'
 (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/asset-catalog.json').write_text(text)
 print(json.dumps(entries,indent=2))
