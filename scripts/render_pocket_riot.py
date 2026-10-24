#!/usr/bin/env python3
"""Actual installed skin and matching launcher previews. No simulated app screenshots."""
from pathlib import Path
import json,sys
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'scripts'))
from pocket_riot_collection import SPECS
out=ROOT/'asset-packs/previews/pocket-riot';out.mkdir(parents=True,exist_ok=True)
font=ImageFont.truetype(str(ROOT/'themes/midnight-express/fonts/Manrope.ttf'),22)
small=ImageFont.truetype(str(ROOT/'themes/midnight-express/fonts/Manrope.ttf'),16)
board=Image.new('RGB',(1440,90+5*450),'#10121B');d=ImageDraw.Draw(board)
d.text((28,20),'VEYRA / POCKET RIOT / TEN INDEPENDENT OVERLAYS',font=font,fill='#FFEEDC')
cards=[]
for i,s in enumerate(SPECS):
 x=24+i%2*712;y=78+i//2*450
 with Image.open(ROOT/f'asset-packs/{s["id"]}/preview.webp') as image:board.paste(image.resize((660,370),Image.Resampling.LANCZOS),(x,y))
 d.text((x,y+378),s['name']+' / 119 roles + matching native glass icon',font=small,fill=s['ink'])
 cards.append(f'<article><h2>{s["name"]}</h2><img class="skin" src="../../{s["id"]}/preview.webp" alt="{s["name"]} actual installed skin"><img class="icon" src="../../../design/launcher/pocket-riot/{s["id"]}.webp" alt="Matching Veyra waveform glass icon"><p>{s["description"]}</p><a href="../{s["id"]}/all-assets.png">All 119 roles</a> · <a href="../../packs/{s["id"]}-v1.zip">Pack ZIP</a></article>')
board.save(out/'collection.webp',quality=88,method=6)
(out/'index.html').write_text('<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Veyra / Pocket Riot</title><style>body{background:#10121b;color:#ffeedc;font:16px system-ui;margin:24px}main{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,430px),1fr));gap:24px}article{padding:20px;background:#201e30;border-radius:24px;min-width:0}img.skin{width:100%;border-radius:16px}.icon{width:112px;float:right;margin:12px}a{color:#c8f1ff;line-height:2}p{line-height:1.6}</style><h1>Pocket Riot / cute, bold, independent</h1><p>Actual shipped fonts, glyphs and pressed controls. Theme artwork and palette remain. 72 themes · 32 overlays · 132 launcher choices.</p><main>'+''.join(cards)+'</main></html>')
print('Rendered actual ten-pack collection and gallery')
