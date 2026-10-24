#!/usr/bin/env python3
"""Ten independent overlays; preserve every previously published pack byte."""
from pathlib import Path
import json,sys,shutil
from PIL import Image
ROOT=Path(__file__).resolve().parent
sys.path.insert(0,str(ROOT.parent/'scripts'))
from pocket_riot_collection import SPECS,svg
import build_collection as engine
import cairosvg
original=cairosvg.svg2png
def compact(*args,**kw):
 result=original(*args,**kw);target=kw.get('write_to')
 if target and '/icons/' in str(target):
  with Image.open(target) as image:image.convert('RGBA').quantize(colors=64,method=Image.Quantize.FASTOCTREE).save(target,optimize=True)
 return result
if __name__=='__main__':
 for name in ['RubikBubbles','RubikWetPaint']:
  for suffix,source in [('.ttf',name+'.ttf'),('-LICENSE.txt',name+'-OFL.txt')]:
   target=ROOT.parent/'design/asset-fonts'/f'{name}{suffix}'
   if not target.exists():shutil.copyfile(ROOT.parent/'design/punk-fonts'/source,target)
 cairosvg.svg2png=compact;engine.svg=svg
 entries=[engine.build(c) for c in SPECS]
 path=ROOT/'catalog.json';catalog=json.loads(path.read_text());ids={e['id'] for e in entries}
 catalog['packs']=entries+[e for e in catalog['packs'] if e['id'] not in ids]
 text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
 path.write_text(text);(ROOT.parent/'app/src/main/assets/asset-catalog.json').write_text(text)
 print('Built ten new immutable overlays;',len(catalog['packs']),'total')
