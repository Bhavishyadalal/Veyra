"""Build six immutable reference-collection packs from checked-in assets (no network).
AI scenes are encoded once in artwork/preview.webp and shared by banner/player/preview.
AI Glass Noir masters live in launcher-art; functional SVG glyphs are rendered below.
Published v1 packs remain immutable; the current collection builds v2.
"""
from pathlib import Path
import hashlib, io, json, shutil, zipfile
import cairosvg
from PIL import Image

ROOT = Path(__file__).resolve().parent
PACK_VERSION = 2
COLLECTION = [
 dict(id='pumpkin-purr', name='Pumpkin Purr', enum='PumpkinPurr', alias='Purr', font='Fredoka', fontTheme='playtime-club',
      palette=['20140F','302019','483027','FFD292','614123','FFF4DC','E4C6AE','A67F63','C4D49B'], corners=[24,28,36], seek='bubbles', scene='playful', tagline='Small frights. Warm little nights.', motif='cat',
      description='Cream ghost kittens, pumpkin lanterns and cocoa paper. Rounded Fredoka headings, soft corners, bubble timeline and an optional amber Glass Noir waveform icon.'),
 dict(id='sunset-crew', name='Sunset Crew', enum='SunsetCrew', alias='Sunset', font='Rajdhani', fontTheme='midnight-express',
      palette=['111323','1D2034','30334B','FFC58D','573626','FFF7ED','D1CCE5','8780A6','CFB2FF'], corners=[12,16,24], seek='web', scene='mist', tagline='Park up. Turn the evening up.', motif='sunset',
      description='Coastal violet coupes and honey sunset. Unofficial SpongeBob / Patrick fan art; characters belong to Nickelodeon / Paramount, no affiliation. Bold Rajdhani headings, road timeline and an optional violet-and-honey Glass Noir icon.'),
 dict(id='starry-spirits', name='Starry Spirits', enum='StarrySpirits', alias='Spirits', font='SpaceGrotesk', fontTheme='astral-tide',
      palette=['0B1C24','152E35','24434A','F3DBA6','4B402B','FFF7E2','C3D9D7','739B9A','A3D8D0'], corners=[20,24,30], seek='ember', scene='mist', tagline='Even ghosts have a favourite song.', motif='ghost',
      description='Gentle sheet ghosts under an original swirling teal-and-gold oil-painted sky. Space Grotesk headings, starlit timeline and an optional teal-and-gold Glass Noir icon.'),
 dict(id='analog-reverie', name='Analog Reverie', enum='AnalogReverie', alias='Analog', font='Manrope', fontTheme='midnight-express',
      palette=['181A15','25281F','383D30','E5D8AE','494331','FAF6E8','D0D1BD','92977E','BCCAA3'], corners=[12,16,22], seek='plain', scene='mist', tagline='Keep the feeling. Play the record.', motif='record',
      description='Warm walnut, smoked olive, vinyl and a luminous swallow. Bold Manrope typography, restrained player details and an optional champagne Glass Noir waveform icon.'),
 dict(id='petal-promise', name='Petal Promise', enum='PetalPromise', alias='Petal', font='Fredoka', fontTheme='playtime-club',
      palette=['27131E','3D2230','543344','FFB9D1','633246','FFF2F6','EAC5D3','AC7D94','D9C39B'], corners=[24,28,36], seek='bubbles', scene='playful', tagline='A little love in every listen.', motif='bow',
      description='Ivory stationery, dusky pink tulips, satin bows and watercolor hearts. Ivory artwork over readable plum-rose controls, rounded Fredoka headings and an optional rose Glass Noir waveform icon.'),
 dict(id='moonlit-herbarium', name='Moonlit Herbarium', enum='MoonlitHerbarium', alias='Herbarium', font='SpaceGrotesk', fontTheme='astral-tide',
      palette=['191322','281F35','3B2E4A','E3CCFF','4C365F','FFF5EB','DBCAE5','9E83AC','DDCEA2'], corners=[20,24,32], seek='ember', scene='mist', tagline='Where quiet things bloom.', motif='moon',
      description='Lavender wildflowers, ivory moths, a crescent moon and a small garden spirit. Space Grotesk headings, gold accents and an optional moonflower-waveform icon.'),
]
PATHS = {
 'nav_home':'M3 11L12 3L21 11 M5 10V20H10V15H14V20H19V10',
 'nav_search':'M16 16L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10',
 'nav_explore':'M12 3L21 12L12 21L3 12Z M12 8V16 M8 12H16',
 'nav_library':'M4 4H8V20H4Z M12 4H16V20H12Z M20 4V20',
 'play':'M8 5L20 12L8 19Z','pause':'M6 5H9V19H6Z M15 5H18V19H15Z',
 'previous':'M4 5V19 M19 5L8 12L19 19Z','next':'M20 5V19 M5 5L16 12L5 19Z',
 'queue':'M7 5H21 M7 12H21 M7 19H21 M3 5H3.1 M3 12H3.1 M3 19H3.1',
 'lyrics':'M5 3H19Q21 3 21 5V16Q21 18 19 18H10L4 22V18H3V5Q3 3 5 3Z M8 8H16 M8 13H14',
 'settings':'M12 3V5 M12 19V21 M3 12H5 M19 12H21 M6 6L8 8 M16 16L18 18 M6 18L8 16 M16 8L18 6 M18 12A6 6 0 1 1 6 12A6 6 0 0 1 18 12',
 'back':'M12 5L5 12L12 19 M5 12H21','download':'M12 3V16 M7 11L12 16L17 11 M4 18V21H20V18',
}


def digest(data): return hashlib.sha256(data).hexdigest()

def build(c):
 base=ROOT/c['id']; src=ROOT/'source-icons'/c['id']; src.mkdir(parents=True,exist_ok=True)
 for folder in ['artwork','icons','fonts','licenses']: (base/folder).mkdir(parents=True,exist_ok=True)
 assert (base/'artwork/preview.webp').exists(), 'Encode the generated scene before building'
 palette=dict(zip(['background','surface','raised','accent','accentContainer','text','muted','outline','secondary'],['#'+x for x in c['palette']]))
 for name,path in PATHS.items():
  width='2.6' if c['motif'] in ['cat','bow'] else '2.2'
  svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path d="{path}" fill="{"white" if name in ["play","pause"] else "none"}" stroke="white" stroke-width="{width}" stroke-linecap="round" stroke-linejoin="round"/></svg>'
  (src/f'{name}.svg').write_text(svg);cairosvg.svg2png(bytestring=svg.encode(),write_to=str(base/'icons'/f'{name}.png'))
 # The AI-edited official Glass Noir master is the shared source for launcher and Home branding.
 # Density resizing is asset compilation; the master design is never drawn/recolored here.
 full=Image.open(ROOT/'launcher-art'/f'{c["id"]}.webp').convert('RGB')
 assert full.width == full.height and full.width >= 1024, 'Use the reviewed AI Glass Noir master'
 full.resize((512,512),Image.Resampling.LANCZOS).save(base/'artwork/brand.webp',quality=c.get('brandQuality',94),method=6)
 res=ROOT.parent/'app/src/main/res';key=c['alias'].lower()
 for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
  for suffix,dp in [('',48),('_foreground',108)]:
   side=int(dp*scale)
   destination=res/f'mipmap-{density}'/f'ic_theme_{key}{suffix}.webp'
   full.resize((side,side),Image.Resampling.LANCZOS).save(destination,quality=c.get('iconQuality',96),method=6)
   # Keep one resource per name when replacing the previous PNG encoding.
   destination.with_suffix('.png').unlink(missing_ok=True)
 (res/f'mipmap-anydpi-v26/ic_theme_{key}.xml').write_text(f'<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/theme_{key}_background"/><foreground android:drawable="@mipmap/ic_theme_{key}_foreground"/><monochrome android:drawable="@drawable/ic_{key}_monochrome"/></adaptive-icon>\n')
 # Android's single-color themed-icon mode retains the official waveform silhouette.
 shutil.copyfile(res/'drawable/ic_wave_monochrome.xml',res/f'drawable/ic_{key}_monochrome.xml')
 colorfile=res/'values/theme_icon_colors.xml';text=colorfile.read_text()
 if f'theme_{key}_background' not in text:colorfile.write_text(text.replace('</resources>',f'<color name="theme_{key}_background">{palette["background"]}</color></resources>'))
 for font,origin in [('Manrope','midnight-express'),(c['font'],c['fontTheme'])]:
  shutil.copyfile(ROOT/origin/'fonts'/f'{font}.ttf',base/'fonts'/f'{font}.ttf')
  notice=ROOT/origin/'licenses'/f'{font}-OFL.txt'
  if not notice.exists(): notice=ROOT/origin/'fonts'/f'{font}-OFL.txt'
  shutil.copyfile(notice,base/'licenses'/f'{font}-OFL.txt')
 credit='Original AI-generated scene created for Veyra from user-provided visual references. Reference images are not redistributed. Glass Noir brand icon edited with AI from the official Veyra black-and-white glass waveform. Functional SVG glyphs by Bhavishya Dalal / Veyra.\n'
 if c['id']=='sunset-crew':credit+='Unofficial SpongeBob SquarePants / Patrick Star fan artwork. Characters belong to their respective owners, including Nickelodeon / Paramount. No affiliation or endorsement. Vehicles have no claimed manufacturer affiliation.\n'
 (base/'licenses/ARTWORK.txt').write_text(credit)
 (base/'licenses/FONT-NOTES.txt').write_text('Bundled fonts retain their upstream SIL Open Font License notices. No font files were modified.\n')
 assets={'header':'artwork/preview.webp','player':'artwork/preview.webp','preview':'artwork/preview.webp','brandIcon':'artwork/brand.webp','headingFont':f'fonts/{c["font"]}.ttf','bodyFont':'fonts/Manrope.ttf',**{key:f'icons/{key}.png' for key in PATHS}}
 manifest=dict(schema=3 if c['scene']=='playful' else 2,id=c['id'],name=c['name'],author='Bhavishya Dalal',version=PACK_VERSION,minAppCode=25,palette=palette,rowCorner=c['corners'][0],buttonCorner=c['corners'][1],navCorner=c['corners'][2],seekStyle=c['seek'],seekHeight=8,sceneStyle=c['scene'],tagline=c['tagline'],assets=assets)
 manifest['files']={str(p.relative_to(base)):digest(p.read_bytes()) for p in sorted(base.rglob('*')) if p.is_file() and p.name!='theme.json'}
 (base/'theme.json').write_text(json.dumps(manifest,indent=2)+'\n')
 # Renderer projection comes directly from this exact manifest, avoiding hard-coded study palettes.
 props={**{k:str(v) for k,v in manifest.items() if k not in ['palette','assets','files']},**palette,**{'asset.'+k:v for k,v in assets.items()}}
 out=ROOT/'render-config';out.mkdir(exist_ok=True)
 (out/f'{c["id"]}.properties').write_text('\n'.join(k+'='+v for k,v in props.items())+'\n')
 buf=io.BytesIO()
 with zipfile.ZipFile(buf,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as archive:
  for p in sorted(base.rglob('*')):
   if p.is_file():
    info=zipfile.ZipInfo(str(p.relative_to(base)),date_time=(2026,10,21,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;archive.writestr(info,p.read_bytes())
 pack=ROOT/'packs'/f'{c["id"]}-v{PACK_VERSION}.zip';payload=buf.getvalue()
 if pack.exists() and pack.read_bytes()!=payload:raise RuntimeError(f'{pack.name} is immutable; bump the pack version')
 pack.write_bytes(payload);sha=digest(payload);pack.with_suffix('.zip.sha256').write_text(f'{sha}  {pack.name}\n')
 prefix='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/preview/0.1.7-beta1/themes/'
 print(c['name'],len(payload),sha)
 return dict(id=c['id'],name=c['name'],description=c['description'],version=PACK_VERSION,minAppCode=25,url=prefix+'packs/'+pack.name,sha256=sha,bytes=len(payload),preview=prefix+c['id']+'/artwork/preview.webp')

if __name__=='__main__':
 entries=[build(c) for c in COLLECTION]
 catalog=json.loads((ROOT/'catalog.json').read_text());ids={c['id'] for c in COLLECTION}
 catalog['themes']=[t for t in catalog['themes'] if t['id'] not in ids]+entries
 text=json.dumps(catalog,indent=2)+'\n';(ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
