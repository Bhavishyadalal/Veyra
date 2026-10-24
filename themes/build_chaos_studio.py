#!/usr/bin/env python3
"""Build only the ten new Chaos Studio themes; existing published packs are immutable."""
from pathlib import Path
import json,sys
from PIL import Image
ROOT=Path(__file__).resolve().parent
sys.path.insert(0,str(ROOT.parent/'scripts'))
from chaos_collection import theme_specs,svg
import build_punk_worlds as engine
import cairosvg
original=cairosvg.svg2png
def compact(*args,**kw):
 result=original(*args,**kw)
 target=kw.get('write_to')
 if target and '/icons/' in str(target):
  with Image.open(target) as image:image.convert('RGBA').quantize(colors=64,method=Image.Quantize.FASTOCTREE).save(target,optimize=True)
 return result
if __name__=='__main__':
 cairosvg.svg2png=compact;engine.svg=svg
 entries=[engine.build(c) for c in theme_specs()]
 path=ROOT/'catalog.json';catalog=json.loads(path.read_text());ids={e['id'] for e in entries}
 catalog['themes']=entries+[e for e in catalog['themes'] if e['id'] not in ids]
 text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
 path.write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
 print('Built',len(entries),'new full themes;',len(catalog['themes']),'total')
