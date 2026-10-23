#!/usr/bin/env python3
"""Compile 21 immutable Veyra AI icon editions into bounded native launcher resources.
No drawing edits: only production format/density exports of the approved AI masters.
"""
from pathlib import Path
import json
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1]
ART=ROOT/'design/launcher/collections'

def compile_icons():
 entries=json.loads((ART/'catalog.json').read_text())['icons']
 for e in entries:
  source=ART/(e['id']+'.webp')
  with Image.open(source) as im:
   assert im.size==(512,512),e['id']
   full=im.convert('RGB')
  key='collection_'+e['id'].replace('-','_');res=ROOT/'app/src/main/res'
  for density,scale in [('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]:
   for suffix,dp in [('',48),('_foreground',108)]:
    side=int(dp*scale)
    # Match existing Veyra launcher exports; the OEM launcher owns final masking.
    full.resize((side,side),Image.Resampling.LANCZOS).save(res/f'mipmap-{density}/ic_{key}{suffix}.webp',quality=92,method=6)
  (res/f'mipmap-anydpi-v26/ic_{key}.xml').write_text(
   '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">'
   '<background android:drawable="@color/wave_glass_background"/>'
   f'<foreground android:drawable="@mipmap/ic_{key}_foreground"/>'
   '<monochrome android:drawable="@drawable/ic_wave_monochrome"/></adaptive-icon>\n')
 # Contact sheet and offline gallery use the actual shipped masters.
 preview=ART/'previews';preview.mkdir(exist_ok=True)
 font=ImageFont.truetype(str(ROOT/'themes/midnight-express/fonts/Manrope.ttf'),18)
 sheet=Image.new('RGB',(1400,85+3*280),'#101217');draw=ImageDraw.Draw(sheet)
 draw.text((35,25),'VEYRA / 21 MATCHING ICON EDITIONS',font=font,fill='#F1E9DC')
 for i,e in enumerate(entries):
  x=20+(i%7)*197;y=85+(i//7)*280
  with Image.open(ART/(e['id']+'.webp')) as icon:sheet.paste(icon.resize((176,176),Image.Resampling.LANCZOS),(x,y))
  name=e['name'];draw.text((x,y+187),name,font=ImageFont.truetype(str(ROOT/'themes/midnight-express/fonts/Manrope.ttf'),14),fill='#F1E9DC')
  draw.text((x,y+212),'Theme' if e['kind']=='theme' else 'Asset pack',font=ImageFont.truetype(str(ROOT/'themes/midnight-express/fonts/Manrope.ttf'),12),fill='#AAB7BD')
 # There are exactly three rows; avoid a giant empty gallery canvas.
 sheet.save(preview/'collection.png')
 html='<!doctype html><meta name="viewport" content="width=device-width,initial-scale=1"><title>Veyra icon editions</title><style>body{background:#101217;color:#eee;font:16px system-ui;margin:24px}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:24px}img{width:100%;max-width:256px}article{min-width:0}a{color:#bbebe0}</style><h1>Veyra / 21 icon editions</h1><p>Same connected-wave identity. Sixteen theme editions and five asset editions. Actual launcher masters.</p><main>'
 for e in entries:html+=f'<article><img src="../{e["id"]}.webp" alt="{e["name"]}" loading="lazy"><h2>{e["name"]}</h2><p>{"Theme" if e["kind"]=="theme" else "Asset pack"}</p></article>'
 (preview/'index.html').write_text(html+'</main>')
 print('Compiled',len(entries),'launcher choices, 210 density images, 21 adaptive XMLs and gallery.')
if __name__=='__main__':compile_icons()
