#!/usr/bin/env python3
"""Build seven original independent overlays using existing, bounded presets."""
from pathlib import Path
import json,sys
from PIL import Image
ROOT=Path(__file__).resolve().parent
sys.path.insert(0,str(ROOT.parent/'scripts'))
from chaos_collection import asset_specs,svg
import build_collection as engine
import cairosvg
original=cairosvg.svg2png
def compact(*args,**kw):
 result=original(*args,**kw);target=kw.get('write_to')
 if target and '/icons/' in str(target):
  with Image.open(target) as image:image.convert('RGBA').quantize(colors=64,method=Image.Quantize.FASTOCTREE).save(target,optimize=True)
 return result
if __name__=='__main__':
 cairosvg.svg2png=compact;engine.svg=svg
 entries=[engine.build(c) for c in asset_specs()]
 path=ROOT/'catalog.json';catalog=json.loads(path.read_text());ids={e['id'] for e in entries}
 catalog['packs']=entries+[e for e in catalog['packs'] if e['id'] not in ids]
 text=json.dumps(catalog,indent=2)+'\n';assert len(text.encode())<=65536
 path.write_text(text);(ROOT.parent/'app/src/main/assets/asset-catalog.json').write_text(text)
 print('Built',len(entries),'new overlays;',len(catalog['packs']),'total')
