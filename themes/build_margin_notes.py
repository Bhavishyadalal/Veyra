"""Build the beta sketch pack from reviewed artwork and original functional glyphs.
No AI calls or image editing: encode the reviewed masters before running this builder.
"""
from pathlib import Path
import hashlib, io, json, shutil, zipfile, sys
import cairosvg
from build_reference_collection import PATHS
ROOT=Path(__file__).resolve().parent
BASE=ROOT/'margin-notes'
SOURCE=ROOT/'source-icons/margin-notes'
def sha(data): return hashlib.sha256(data).hexdigest()
def build():
    for d in ['icons','artwork','fonts','licenses']: (BASE/d).mkdir(parents=True,exist_ok=True)
    SOURCE.mkdir(parents=True,exist_ok=True)
    paths=dict(PATHS)
    paths.update(nav_home='M3 11L11 3L21 10 M5 10L4 21L19 20L19 10 M9 20L9 15L14 14L15 20',
        nav_search='M16 10C17 18 3 19 3 10C2 1 17 1 16 10Z M15 16L21 22 M6 7L10 5',
        nav_library='M3 5Q7 3 12 6Q17 3 21 5L20 20Q16 18 12 21Q7 18 3 20Z M12 6L12 21 M6 9L9 10 M15 10L18 9',
        nav_explore='M21 12C22 24 1 23 3 11C2 0 22 1 21 12Z M15 7L13 14L7 17L10 10Z',
        play='M7 4Q12 7 21 12Q15 16 8 21Z',pause='M6 4L9 5L10 20L6 20Z M15 5L19 4L18 20L14 20Z',
        previous='M5 4L4 20 M20 4L8 12L19 21Z',next='M19 4L20 20 M4 4L16 12L5 21Z',
        queue='M8 5L21 4 M8 12L19 13 M8 20L21 19 M3 5L4 5 M3 12L4 12 M3 20L4 20',
        lyrics='M4 4L21 3L20 17L12 17L7 22L7 17L3 17Z M7 8L17 7 M7 12L15 12',
        settings='M9 3L15 3L16 6L20 7L22 12L19 15L19 19L14 21L11 18L7 19L3 15L5 11L4 7L8 6Z M16 12C16 17 8 17 8 12C7 7 16 7 16 12Z',
        back='M13 4L4 12L13 21 M5 12L22 11',download='M12 3L11 16 M6 11L11 17L17 12 M3 19L4 22L21 21L21 18')
    for name,d in paths.items():
        svg=f'''<svg xmlns="http://www.w3.org/2000/svg" width="128" height="128" viewBox="0 0 24 24"><g fill="none" stroke="white" stroke-linecap="round" stroke-linejoin="round"><path d="{d}" stroke-width="1.35"/><path d="{d}" transform="translate(.25 -.14)" stroke-width=".55" opacity=".6"/><path d="{d}" transform="translate(-.18 .21)" stroke-width=".35" opacity=".35"/></g></svg>'''
        (SOURCE/f'{name}.svg').write_text(svg)
        cairosvg.svg2png(bytestring=svg.encode(),write_to=str(BASE/'icons'/f'{name}.png'))
    wave='M27 42C22 42 22 48 22 54L22 61C22 71 35 71 35 61L35 54C35 47 35 42 27 42Z M54 24C47 24 47 30 47 36L47 77C47 87 61 87 61 77L61 36C61 30 61 24 54 24Z M80 46C73 46 73 51 73 57L73 64C73 74 86 74 86 64L86 57C86 51 86 46 80 46Z M30 56C40 64 44 62 54 55L54 65C43 72 39 72 30 65Z M54 60C63 65 70 66 80 59L80 68C69 75 62 73 54 69Z'
    brand=f'''<svg xmlns="http://www.w3.org/2000/svg" width="256" height="256" viewBox="0 0 128 128"><path d="M12 9Q61 4 115 12L120 114Q65 122 9 117Z" fill="#292821" stroke="#ead8b0" stroke-width="2"/><path d="M14 13L112 10L117 111L12 119Z" fill="none" stroke="#b6a98e" stroke-width=".8"/><g transform="translate(9 6)" stroke="#f4e7c7"><path d="{wave}" fill="#ead8b0" fill-opacity=".1" stroke-width="1.2"/><path d="{wave}" fill="none" transform="translate(.9 -.6)" stroke-width=".4"/></g><path d="M95 18L97 23L103 23L98 27L100 33L94 29L90 33L91 26L86 23L93 23Z" fill="#e8cd8b"/><path d="M18 100L25 96M19 104L29 99M21 108L32 102M91 105L107 101M91 110L108 106" stroke="#b4a68b" stroke-width=".65"/></svg>'''
    (SOURCE/'brand.svg').write_text(brand)
    cairosvg.svg2png(bytestring=brand.encode(),write_to=str(BASE/'artwork/brand.png'))
    shutil.copyfile(ROOT/'midnight-express/fonts/Manrope.ttf',BASE/'fonts/Manrope.ttf')
    shutil.copyfile(ROOT/'midnight-express/licenses/Manrope-OFL.txt',BASE/'licenses/Manrope-OFL.txt')
    (BASE/'licenses/ARTWORK.txt').write_text('Original AI-generated background artwork created for Veyra using OpenAI image generation. Original vector pencil glyphs and brand treatment created for Veyra; brand preserves the Veyra waveform. No reference photos or third-party character art redistributed.\n')
    (BASE/'licenses/FONT-NOTES.txt').write_text('Unmodified Patrick Hand by Patrick Wagesreiter and Manrope fonts; included upstream SIL Open Font License notices apply.\n')
    colors=['24231F','2B2A25','3D3A31','E8CD8B','504633','FFF5DF','DFD2B6','A09477','AEC6B5']
    assets=dict(header='artwork/preview.webp',preview='artwork/preview.webp',player='artwork/player.webp',brandIcon='artwork/brand.png',headingFont='fonts/PatrickHand.ttf',bodyFont='fonts/Manrope.ttf',**{k:f'icons/{k}.png' for k in paths})
    m=dict(schema=3,id='margin-notes',name='Margin Notes',author='Bhavishya Dalal',version=1,minAppCode=30,
        palette=dict(zip(['background','surface','raised','accent','accentContainer','text','muted','outline','secondary'],['#'+c for c in colors])),
        rowCorner=18,buttonCorner=26,navCorner=30,sceneStyle='sketch',seekStyle='sketch',seekHeight=8,tagline='A little rough. A lot of rhythm.',assets=assets)
    m['files']={str(p.relative_to(BASE)):sha(p.read_bytes()) for p in sorted(BASE.rglob('*')) if p.is_file() and p.name!='theme.json'}
    (BASE/'theme.json').write_text(json.dumps(m,indent=2)+'\n')
    output=ROOT/'packs/margin-notes-v1.zip'
    buffer=io.BytesIO()
    with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
        for p in sorted(BASE.rglob('*')):
            if p.is_file():
                entry=zipfile.ZipInfo(str(p.relative_to(BASE)),date_time=(2026,10,23,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED
                z.writestr(entry,p.read_bytes())
    payload=buffer.getvalue()
    if output.exists() and output.read_bytes()!=payload:
        raise RuntimeError('Published packs are immutable; bump the version before rebuilding.')
    output.write_bytes(payload)
    digest=sha(output.read_bytes());output.with_suffix('.zip.sha256').write_text(f'{digest}  {output.name}\n')
    url='https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/preview/0.1.9-beta1/themes/'
    entry=dict(id=m['id'],name=m['name'],description='An ivory-pencil music sketchbook: graphite paper, crooked doodle glyphs, Patrick Hand headings, uneven control frames, pencil-hatched timeline, press wobble and paper-slide transitions. Beta 0.1.9 engine required.',version=1,minAppCode=30,url=url+'packs/'+output.name,sha256=digest,bytes=output.stat().st_size,preview=url+'margin-notes/artwork/preview.webp')
    catalog=json.loads((ROOT/'catalog.json').read_text());catalog['themes']=[entry]+[e for e in catalog['themes'] if e['id']!=m['id']]
    # A beta catalog is self-contained; stable's main catalog remains unchanged.
    for e in catalog['themes']:
        e['url']=url+f'packs/{e["id"]}-v{e["version"]}.zip';e['preview']=url+e['id']+'/artwork/preview.webp'
    text=json.dumps(catalog,indent=2)+'\n'
    (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
    print(json.dumps({'assets':len(assets),'files':len(m['files']),'bytes':entry['bytes'],'sha256':digest}))
if __name__=='__main__':build()
