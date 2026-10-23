#!/usr/bin/env python3
"""Ten original gothic/punk overlays. Keep published packs immutable; use only bounded native presets."""
from pathlib import Path
import json,sys,math
import build_collection as engine
from PIL import Image,ImageDraw,ImageFont
import cairosvg
ROOT=Path(__file__).resolve().parent
SPECS=[
 dict(id='cathedral-static',name='Cathedral Static',font='UnifrakturCook',material='deco',seek='skyline',motion='deco',ink='#C4D8FF',colors=['#C4D8FF','#FF9BB4','#EBD5A5'],description='Pointed stained-glass frames, cobalt and crimson tracery, silver blackletter and skyline progress.'),
 dict(id='velvet-hex',name='Velvet Hex',font='GrenzeGotisch',material='botanical',seek='vine',motion='botanical',ink='#DEC4FF',colors=['#DEC4FF','#B3E9DA','#EDC7DC'],description='Plum velvet medallions, silver crescent filigree, gothic lettering and a delicate vine timeline.'),
 dict(id='razor-bloom',name='Razor Bloom',font='NewRocker',material='flame',seek='flame',motion='flame',ink='#FFADB9',colors=['#FFADB9','#E6D3B5','#CAC1FA'],description='Rose-cut crimson seals, thorn corners, sharp rocker headings and an ember-ribbon seek treatment.'),
 dict(id='neon-seance',name='Neon Séance',font='MetalMania',material='punk_chrome',seek='plasma',motion='punk_chrome',ink='#BDEBFF',colors=['#BDEBFF','#D2B0FF','#F5C0E8'],description='Spectral cyan geometry, violet double outlines, metal display lettering and plasma progress.'),
 dict(id='blackout-zine',name='Blackout Zine',font='PirataOne',material='punk_torn',seek='tape',motion='punk_torn',ink='#F1E6CC',colors=['#F1E6CC','#FFABA2','#D7D3E8'],description='Torn charcoal and cream print blocks, red poster marks, pirate-gothic lettering and spliced tape progress.'),
 dict(id='chrome-fang',name='Chrome Fang',font='UnifrakturMaguntia',material='punk_rivet',seek='cable',motion='punk_rivet',ink='#CAF7E8',colors=['#CAF7E8','#CBD9EE','#E5D4F3'],description='Fanged gunmetal panels, mint metal edges, traditional blackletter and a braided-cable timeline.'),
 dict(id='candy-crypt',name='Candy Crypt',font='Creepster',material='punk_sticker',seek='beads',motion='punk_sticker',ink='#FFD3EB',colors=['#FFD3EB','#C4D4FF','#F7E6B5'],description='Pink batwing stickers, cream candy seals, playful horror lettering and a beaded progress track.'),
 dict(id='royal-ruin',name='Royal Ruin',font='MedievalSharp',material='deco',seek='telemetry',motion='deco',ink='#F5DF9D',colors=['#F5DF9D','#BFD7FF','#E9C9D3'],description='Antique-gold crowns, sapphire heraldic frames, medieval headings and instrument-like sector progress.'),
 dict(id='acid-gargoyle',name='Acid Gargoyle',font='Nosifer',material='telemetry',seek='gel',motion='telemetry',ink='#D8FF94',colors=['#D8FF94','#B7DBE4','#E7C2FF'],description='Horned stone cutouts, acid-green ink, dripping display letters and gel-dot seek markers.'),
 dict(id='midnight-stitch',name='Midnight Stitch',font='Rye',material='botanical',seek='sketch',motion='botanical',ink='#C8DDFF',colors=['#C8DDFF','#E8D3AE','#D5BEF1'],description='Dark denim patches, silver embroidery, western varsity lettering and a stitched pencil timeline.'),
]

def frame(c,col,dark='#13131C'):
 id=c['id'];cream='#F5EADD'
 if id=='cathedral-static':
  return f'<path d="M2 24V10Q2 4 13 1Q24 4 24 10V24Z" fill="{dark}" stroke="{col}" stroke-width="1.1"/><path d="M5 22V10Q5 6 13 3Q21 6 21 10V22 M13 3V7 M4 11H8 M18 11H22" fill="none" stroke="{c["colors"][1]}" stroke-width=".55"/><path d="M10 4L13 2L16 4L13 6Z" fill="{col}"/>'
 if id=='velvet-hex':
  return f'<path d="M13 1Q16 5 22 4Q20 10 25 13Q20 16 22 22Q16 20 13 25Q10 20 4 22Q6 16 1 13Q6 10 4 4Q10 6 13 1Z" fill="#2A1738" stroke="{col}" stroke-width=".9"/><ellipse cx="13" cy="13" rx="10" ry="9" fill="none" stroke="{col}" stroke-width=".4"/><path d="M12 3Q8 5 12 7Q9 6 10 4Z" fill="{cream}"/>'
 if id=='razor-bloom':
  pts=' '.join(f'{13+math.cos(i*math.pi/10)*(12 if i%2==0 else 9.6):.2f},{13+math.sin(i*math.pi/10)*(12 if i%2==0 else 9.6):.2f}' for i in range(20))
  return f'<polygon points="{pts}" fill="#341824" stroke="{col}" stroke-width=".8"/><path d="M4 4L1 2L3 8 M22 22L25 24L23 18 M22 4L24 1L18 3 M4 22L2 25L8 23" fill="none" stroke="{col}" stroke-width=".7"/>'
 if id=='neon-seance':
  return f'<path d="M13 1L25 8L24 20L13 25L2 20L1 8Z" fill="#17142B" stroke="{col}" stroke-width=".9"/><path d="M13 3L23 9L22 19L13 23L4 19L3 9Z" fill="none" stroke="{c["colors"][1]}" stroke-width=".6"/><circle cx="13" cy="2.5" r="1" fill="{cream}"/>'
 if id=='blackout-zine':
  return f'<path d="M2 3L6 2L10 3L15 1L20 3L24 2L23 8L25 13L24 19L25 24L18 23L12 25L6 23L1 24L2 17L1 10Z" fill="{col}" stroke="#18171C" stroke-width=".8"/><path d="M2 4L9 3 M19 24L25 22" stroke="#DF535C" stroke-width="1.6"/>'
 if id=='chrome-fang':
  return f'<path d="M5 2H21L25 6L23 18L19 24L17 20H9L7 24L3 18L1 6Z" fill="#1C292C" stroke="{col}" stroke-width="1"/><path d="M5 4L9 8H17L21 4 M3 8L6 11 M23 8L20 11 M10 22H16" fill="none" stroke="{cream}" stroke-width=".5"/><circle cx="5" cy="6" r=".65" fill="{col}"/><circle cx="21" cy="6" r=".65" fill="{col}"/>'
 if id=='candy-crypt':
  return f'<path d="M3 7L1 3L8 6Q13 1 18 6L25 3L23 7Q27 12 22 17L23 22L17 21Q13 26 9 21L3 22L4 17Q-1 12 3 7Z" fill="#352039" stroke="{col}" stroke-width="1"/><path d="M4 8L7 9M22 8L19 9" stroke="{c["colors"][1]}" stroke-width=".8"/>'
 if id=='royal-ruin':
  return f'<path d="M3 6L1 2L8 5L13 1L18 5L25 2L23 6V19Q19 24 13 25Q7 24 3 19Z" fill="#182337" stroke="{col}" stroke-width="1"/><path d="M5 8V18Q8 21 13 23Q18 21 21 18V8 M6 6H20" fill="none" stroke="{col}" stroke-width=".45"/><path d="M12 3L13 2L14 3L13 4Z" fill="{cream}"/>'
 if id=='acid-gargoyle':
  return f'<path d="M3 8L1 1L9 5H17L25 1L23 8L25 13L22 22L16 21L13 25L10 21L4 22L1 13Z" fill="#25302A" stroke="{col}" stroke-width="1"/><path d="M4 6L6 8 M22 6L20 8 M6 23L9 22 M20 23L17 22" fill="none" stroke="{col}" stroke-width=".5"/>'
 return f'<rect x="1.5" y="1.5" width="23" height="23" rx="6" fill="#1C2B43" stroke="{col}" stroke-width=".9"/><rect x="3" y="3" width="20" height="20" rx="5" fill="none" stroke="{cream}" stroke-width=".5" stroke-dasharray="1 .8"/><path d="M5 21L7 23M19 3L21 5" stroke="{col}" stroke-width=".6"/>'

def svg(c,key,path,i,pressed=False):
 col=c['colors'][i%3];button=key in ('play','pause','previous','next');paper=c['id']=='blackout-zine'
 ornament=frame(c,col)
 if not button: ornament=f'<g opacity=".65">{ornament}</g>'
 transform='translate(4.2 4.2) scale(.68)' if button else 'translate(2.7 2.7) scale(.8)'
 foreground='#211D27' if paper else col
 filled=foreground if button else 'none'
 glyph=f'<g transform="{transform}" stroke-linejoin="round" stroke-linecap="round"><path d="{path}" fill="{filled}" stroke="{"#F4EBDD" if paper else "#101118"}" stroke-width="3.2"/><path d="{path}" fill="{filled}" stroke="{foreground}" stroke-width="1.8"/></g>'
 content=ornament+glyph
 if pressed:content=f'<g transform="translate(.8 1.2) scale(.94 .92)" opacity=".82">{content}</g>'
 return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="-1 -1 28 28">{content}</svg>'

def launcher(c):
 # The flat SVGs are retained as historical studies only. Export the reviewed
 # AI glass master instead; a pack rebuild must never restore the old artwork.
 sys.path.insert(0,str(ROOT.parent/'scripts'))
 from build_night_vault_icons import compile_icon
 entry=dict(id=c['id'],name=c['name'],kind='asset',style=c['description'],source='night-vault/'+c['id']+'.webp')
 compile_icon(entry)
 return entry

def galleries():
 out=ROOT/'previews/night-vault';out.mkdir(parents=True,exist_ok=True)
 html='<!doctype html><meta name="viewport" content="width=device-width,initial-scale=1"><title>Veyra Night Vault · actual assets</title><style>body{background:#0d1019;color:#eee;font:16px system-ui;margin:24px}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,420px),1fr));gap:24px}article{padding:18px;background:#191e2a;border-radius:24px}img{max-width:100%;border-radius:16px}a{color:#c9dcff}</style><h1>VEYRA / NIGHT VAULT</h1><p>Ten independent overlays · real glyphs, fonts and matching waveform icons · app build 39 supplies the redesigned glass launcher icons</p><main>'
 for c in SPECS:
  html+=f'<article><h2>{c["name"]}</h2><img src="../{c["id"]}/specimen.png" alt="{c["name"]} actual assets"><p>{c["description"]}</p><p><a href="../{c["id"]}/all-assets.png">All 119 runtime roles</a> · <a href="../../packs/{c["id"]}-v1.zip">Pack ZIP</a></p><img src="../../../design/launcher/night-vault/{c["id"]}.webp" width="128" alt="Matching Veyra waveform icon"></article>'
 (out/'index.html').write_text(html+'</main>')

if __name__=='__main__':
 # Compact flat-color raster exports; original SVGs remain editable, full-resolution masters.
 original_png=cairosvg.svg2png
 def export_png(*args,**kwargs):
  result=original_png(*args,**kwargs)
  target=kwargs.get('write_to')
  if target and '/icons/' in str(target):
   with Image.open(target) as image:
    indexed=image.convert('RGBA').quantize(colors=64,method=Image.Quantize.FASTOCTREE)
    indexed.save(target,optimize=True)
  return result
 cairosvg.svg2png=export_png
 engine.svg=svg
 entries=[engine.build(c) for c in SPECS]
 path=ROOT/'catalog.json';catalog=json.loads(path.read_text());ids={c['id'] for c in SPECS}
 catalog['packs']=[e for e in catalog['packs'] if e['id'] not in ids]+entries
 text=json.dumps(catalog,indent=2)+'\n';path.write_text(text);(ROOT.parent/'app/src/main/assets/asset-catalog.json').write_text(text)
 icons=[launcher(c) for c in SPECS]
 (ROOT.parent/'design/launcher/night-vault/catalog.json').write_text(json.dumps(dict(schema=1,icons=icons),indent=2)+'\n')
 galleries()
 print('Built ten immutable packs, 1,190 asset roles, ten official-wave launcher editions and native exports.')
