"""Build five immutable, stable-0.1.7-compatible packs from reviewed AI assets.

Run with --launcher only on preview/0.1.8-beta1. Scene/icon design masters are
reviewed separately; this script compiles density assets, glyphs and data packs.
No network calls, credentials or changes to previous published packs.
"""
from pathlib import Path
import argparse
import io
import json
import shutil
import zipfile
import cairosvg
from PIL import Image
from build_reference_collection import PATHS, digest

ROOT = Path(__file__).resolve().parent
COLLECTION = [
    dict(id='patchwork-picnic', name='Patchwork Picnic', font='Fredoka', origin='playtime-club',
         palette=['231A16','342720','49382D','FFD497','5D4228','FFF8E8','E6D1BC','AB8B70','B8D1EF'],
         corners=[24,28,34], seek='bubbles', scene='playful', tagline='Little scraps. Favourite songs.',
         description='Cream puppy stickers, caramel gingham, coffee and denim. Fredoka headings, stitched navigation, soft transport controls and a warm picnic palette.'),
    dict(id='clover-requiem', name='Clover Requiem', font='SpaceGrotesk', origin='astral-tide',
         palette=['141D19','233229','38483A','DAE6A1','465032','F8F9E8','D7E0C9','91A186','BFCBFF'],
         corners=[20,24,30], seek='ember', scene='mist', tagline='Even quiet souls need a song.',
         description='An original masked meadow spirit and little black cat among periwinkle flowers. Moss, ivory and chartreuse, flower navigation and a firefly timeline.'),
    dict(id='sunroom-mixtape', name='Sunroom Mixtape', font='Manrope', origin='midnight-express',
         palette=['201A14','30271F','46392C','F8D09B','58412A','FFF7E8','E4D0B7','A88A6A','C1D0E0'],
         corners=[14,20,26], seek='ember', scene='mist', tagline='An afternoon worth replaying.',
         description='Golden window light, headphones, vinyl and walnut floors. Bold Manrope typography, cassette controls and warm analog navigation details.'),
    dict(id='concrete-afterhours', name='Concrete Afterhours', font='Rajdhani', origin='midnight-express',
         palette=['171C20','282F34','3A434A','F3D29C','52462F','F8F8EF','D0DCE0','8F9DA2','BBCFB6'],
         corners=[10,14,22], seek='web', scene='mist', tagline='Find your rhythm underground.',
         description='Original underground streetwear illustration, graphite concrete and safety amber. Bold Rajdhani headings, architectural glyphs and a rail-line timeline.'),
    dict(id='matcha-mischief', name='Matcha Mischief', font='Fredoka', origin='playtime-club',
         palette=['11251C','20392B','34503A','D8EBA6','42552F','FFF9E7','D7E2C9','8EAC80','F1CFB4'],
         corners=[24,28,36], seek='bubbles', scene='playful', tagline='A little green. A little mischief.',
         description='Original black cat, fluffy bunny and frog-hood bear in a matcha-paper collage. Cream-and-forest controls, clover navigation and rounded Fredoka headings.'),
]

MARKS = [
    'M7 8L9 6L12 8L15 6L17 8 M8 12H16',
    'M12 6Q17 4 17 9Q20 13 15 14Q12 19 9 14Q4 13 7 9Q7 4 12 6Z',
    'M6 8H18V16H6Z M10 11A1 1 0 1 1 8 11A1 1 0 0 1 10 11 M16 11A1 1 0 1 1 14 11A1 1 0 0 1 16 11',
    'M7 6H17V17H7Z M7 10H17 M10 6V10 M14 6V10 M9 20H15',
    'M8 9L7 5L11 7 M16 9L17 5L13 7 M8 13Q12 17 16 13',
]

def glyphs(index):
    paths = dict(PATHS)
    paths['nav_home'] = 'M3 11L12 3L21 11 M5 10V21H19V10 M9 21V15H15V21'
    if index in (0,4):
        paths['nav_home'] = 'M3 11Q12 0 21 11 M5 10V21H19V10 M9 21V16Q12 12 15 16V21'
        paths['play'] = 'M8 5Q8 3 10 5L20 11Q22 12 20 13L10 19Q8 21 8 19Z'
        paths['pause'] = 'M6 5Q8 3 10 5V19Q8 21 6 19Z M14 5Q16 3 18 5V19Q16 21 14 19Z'
    if index == 3:
        paths['nav_home'] += ' M8 8H16 M8 11H16'
        paths['play'] = 'M7 4L21 12L7 20L9 12Z'
    paths['nav_search'] = PATHS['nav_search'] + (' M7 7L13 13 M13 7L7 13' if index==0 else ' M7 10H13')
    paths['nav_explore'] = MARKS[index]
    # Keep an unmistakable compass boundary around the small material motif.
    paths['nav_explore'] = 'M21 12A9 9 0 1 1 3 12A9 9 0 0 1 21 12 ' + paths['nav_explore']
    paths['nav_library'] = ('M3 5Q7 3 12 6Q17 3 21 5V20Q17 18 12 21Q7 18 3 20Z M12 6V21 M6 10H8 M16 10H18'
                            if index in (0,1,4) else PATHS['nav_library'])
    if index==2:
        paths['queue'] = MARKS[2] + ' M8 20H16'
    return paths

def launcher(c):
    res=ROOT.parent/'app/src/main/res'
    key=c['id'].replace('-','')
    master=Image.open(ROOT/'launcher-art'/f'{c["id"]}.webp').convert('RGB')
    for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
        for suffix,dp in [('',48),('_foreground',108)]:
            master.resize((int(dp*scale),int(dp*scale)),Image.Resampling.LANCZOS).save(
                res/f'mipmap-{density}'/f'ic_theme_{key}{suffix}.webp',quality=95,method=6)
    (res/f'mipmap-anydpi-v26/ic_theme_{key}.xml').write_text(
        f'<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/theme_{key}_background"/><foreground android:drawable="@mipmap/ic_theme_{key}_foreground"/><monochrome android:drawable="@drawable/ic_{key}_monochrome"/></adaptive-icon>\n')
    shutil.copyfile(res/'drawable/ic_wave_monochrome.xml',res/f'drawable/ic_{key}_monochrome.xml')
    colors=res/'values/theme_icon_colors.xml';text=colors.read_text()
    if f'theme_{key}_background' not in text:
        colors.write_text(text.replace('</resources>',f'<color name="theme_{key}_background">#{c["palette"][0]}</color></resources>'))

def build(c,index,include_launcher):
    base=ROOT/c['id'];source=ROOT/'source-icons'/c['id']
    source.mkdir(parents=True,exist_ok=True)
    for d in ['artwork','icons','fonts','licenses']: (base/d).mkdir(parents=True,exist_ok=True)
    master=Image.open(ROOT/'launcher-art'/f'{c["id"]}.webp').convert('RGB')
    # Compatible even with the first stable 0.1.7 APK's original 256 px rule.
    master.resize((256,256),Image.Resampling.LANCZOS).save(base/'artwork/brand.webp',quality=94,method=6)
    if include_launcher: launcher(c)
    paths=glyphs(index)
    for name,path in paths.items():
        svg=f'<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><path d="{path}" fill="{"white" if name in ("play","pause") else "none"}" stroke="white" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
        (source/f'{name}.svg').write_text(svg)
        cairosvg.svg2png(bytestring=svg.encode(),write_to=str(base/'icons'/f'{name}.png'))
    for font,origin in [('Manrope','midnight-express'),(c['font'],c['origin'])]:
        src=ROOT/origin
        shutil.copyfile(src/'fonts'/f'{font}.ttf',base/'fonts'/f'{font}.ttf')
        notice=src/'licenses'/f'{font}-OFL.txt'
        if not notice.exists():notice=src/'fonts'/f'{font}-OFL.txt'
        shutil.copyfile(notice,base/'licenses'/f'{font}-OFL.txt')
    (base/'licenses/ARTWORK.txt').write_text('Original AI-generated artwork created for Veyra, inspired by user-provided mood references. Original animal and human characters; no claimed franchise affiliation. Reference images are not redistributed. AI-edited brand artwork preserves the official Veyra waveform. Functional SVG glyphs by Bhavishya Dalal / Veyra.\n')
    (base/'licenses/FONT-NOTES.txt').write_text('Unmodified fonts; upstream SIL Open Font License notices are included.\n')
    palette=dict(zip(['background','surface','raised','accent','accentContainer','text','muted','outline','secondary'],['#'+x for x in c['palette']]))
    assets=dict(header='artwork/preview.webp',player='artwork/preview.webp',preview='artwork/preview.webp',brandIcon='artwork/brand.webp',headingFont=f'fonts/{c["font"]}.ttf',bodyFont='fonts/Manrope.ttf',**{k:f'icons/{k}.png' for k in paths})
    m=dict(schema=3 if c['scene']=='playful' else 2,id=c['id'],name=c['name'],author='Bhavishya Dalal',version=1,minAppCode=26,palette=palette,rowCorner=c['corners'][0],buttonCorner=c['corners'][1],navCorner=c['corners'][2],seekStyle=c['seek'],seekHeight=8,sceneStyle=c['scene'],tagline=c['tagline'],assets=assets)
    m['files']={str(f.relative_to(base)):digest(f.read_bytes()) for f in sorted(base.rglob('*')) if f.is_file() and f.name!='theme.json'}
    (base/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
    props={**{k:str(v) for k,v in m.items() if k not in ['palette','assets','files']},**palette,**{'asset.'+k:v for k,v in assets.items()}}
    (ROOT/'render-config'/f'{c["id"]}.properties').write_text('\n'.join(k+'='+v for k,v in props.items())+'\n')
    buf=io.BytesIO()
    with zipfile.ZipFile(buf,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for f in sorted(base.rglob('*')):
            if f.is_file():
                entry=zipfile.ZipInfo(str(f.relative_to(base)),date_time=(2026,10,21,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED
                z.writestr(entry,f.read_bytes())
    pack=ROOT/'packs'/f'{c["id"]}-v1.zip';payload=buf.getvalue()
    if pack.exists() and pack.read_bytes()!=payload:raise RuntimeError(f'{pack.name} is immutable; bump its version')
    pack.write_bytes(payload);sha=digest(payload)
    pack.with_suffix('.zip.sha256').write_text(f'{sha}  {pack.name}\n')
    prefix='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/main/themes/'
    return dict(id=c['id'],name=c['name'],description=c['description'],version=1,minAppCode=26,url=prefix+'packs/'+pack.name,sha256=sha,bytes=len(payload),preview=prefix+c['id']+'/artwork/preview.webp')

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--launcher',action='store_true')
    args=parser.parse_args()
    entries=[build(c,i,args.launcher) for i,c in enumerate(COLLECTION)]
    catalog=json.loads((ROOT/'catalog.json').read_text());ids={c['id'] for c in COLLECTION}
    catalog['themes']=entries+[t for t in catalog['themes'] if t['id'] not in ids]
    text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
    (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
    print(f'Built {len(entries)} stable-compatible packs; launcher assets: {args.launcher}')
