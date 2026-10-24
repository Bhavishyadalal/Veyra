#!/usr/bin/env python3
"""Static, responsive gallery of actual installed Chaos Studio assets, not app mockups."""
from pathlib import Path
from html import escape
import json,sys
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT/'themes'))
from render_punk_worlds import render_page
from chaos_collection import theme_specs,asset_specs
out=ROOT/'themes/previews/chaos-studio';out.mkdir(parents=True,exist_ok=True)
cards=[]
for c in theme_specs():
 preview=ROOT/'themes/previews'/c['id'];preview.mkdir(parents=True,exist_ok=True)
 page,all_assets=render_page(c,'../../'+c['id']+'/')
 (preview/'index.html').write_text(page);(preview/'assets.html').write_text(all_assets)
 cards.append(f'<article><img class="wall" loading="lazy" src="../../{c["id"]}/artwork/preview.webp" alt="{escape(c["name"])} wallpaper"><h2>{c["name"]}</h2><p>{c["tagline"]}</p><div class="glyphs">'+''.join(f'<img loading="lazy" src="../../{c["id"]}/icons/{key}.png" alt="{key}">' for key in ['play','pause','nav_home','settings'])+f'</div><a href="../{c["id"]}/index.html">Asset specimen</a> · <a href="../{c["id"]}/assets.html">All 123 roles</a></article>')
assets=[]
for c in asset_specs():
 assets.append(f'<article><img class="specimen" loading="lazy" src="../../../asset-packs/{c["id"]}/preview.webp" alt="{escape(c["name"])} actual assets"><h2>{c["name"]}</h2><p>{c["description"]}</p><a href="../../../asset-packs/previews/{c["id"]}/all-assets.png">All 119 roles</a></article>')
css='*{box-sizing:border-box}body{margin:0;background:#12121d;color:#fff2e3;font:16px system-ui}main{max-width:1350px;padding:36px;margin:auto}h1{font-size:clamp(38px,7vw,88px);line-height:1;margin:22px 0}h2{margin-bottom:8px}p{color:#c9bfd6;line-height:1.6}small{letter-spacing:.15em;color:#d7ff9b}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,260px),1fr));gap:24px}article{background:#222034;padding:16px;border:1px solid #63546e;border-radius:24px}.wall{width:100%;height:290px;object-fit:cover;border-radius:16px}.specimen{width:100%;border-radius:16px}.glyphs{display:flex;gap:12px;margin:16px 0}.glyphs img{width:48px;height:48px}a{color:#c9f2ff;line-height:2.5}header{padding-bottom:30px}section{margin-top:50px}'
(out/'index.html').write_text(f'<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Veyra · Chaos Studio</title><style>{css}</style><main><header><small>VEYRA / BETA BUILD 45</small><h1>Chaos, with<br>every detail.</h1><p>Ten original AI wallpaper worlds. Seven independent asset overlays. Actual runtime glyphs, fonts and pressed states. No app-screen mockups.</p><p>Themes use existing code-32 presets; independent overlays use code-33 presets. New navigation and motion choices require beta code 45. No new launcher aliases are added; theme branding reuses reviewed glass waveform editions.</p></header><section><h2>Ten complete themes</h2><div class="grid">'+''.join(cards)+'</div></section><section><h2>Seven overlays · any theme</h2><div class="grid">'+''.join(assets)+'</div></section></main></html>')
print('Rendered ten full-theme specimens, every-role pages and seven-overlay gallery')
