#!/usr/bin/env python3
"""Export the ten reviewed AI glass masters; never redraw them from flat SVGs.

Only production resizing/encoding and an actual-master contact sheet happen here.
No network or original image-generation files are needed to rebuild the app.
"""
from pathlib import Path
import hashlib
import json
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
ART = ROOT / 'design/launcher/night-vault'
DENSITIES = [('mdpi', 1), ('hdpi', 1.5), ('xhdpi', 2), ('xxhdpi', 3), ('xxxhdpi', 4)]


def compile_icon(entry):
    source = ROOT / 'design/launcher' / entry['source']
    provenance = json.loads((ART / 'generated-sources.json').read_text())
    record = next(e for e in provenance['images'] if e['id'] == entry['id'])
    if hashlib.sha256(source.read_bytes()).hexdigest() != record['masterSha256']:
        raise ValueError(f"Unreviewed glass master: {entry['id']}")
    with Image.open(source) as image:
        if image.size != (512, 512):
            raise ValueError(f"Expected a 512px glass master: {source}")
        full = image.convert('RGB')
    res = ROOT / 'app/src/main/res'
    key = 'collection_' + entry['id'].replace('-', '_')
    for density, scale in DENSITIES:
        for suffix, dp in [('', 48), ('_foreground', 108)]:
            side = int(dp * scale)
            full.resize((side, side), Image.Resampling.LANCZOS).save(
                res / f'mipmap-{density}/ic_{key}{suffix}.webp', quality=92, method=6)
    (res / f'mipmap-anydpi-v26/ic_{key}.xml').write_text(
        '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">'
        '<background android:drawable="@color/wave_glass_background"/>'
        f'<foreground android:drawable="@mipmap/ic_{key}_foreground"/>'
        '<monochrome android:drawable="@drawable/ic_wave_monochrome"/></adaptive-icon>\n')


def compile_icons():
    entries = json.loads((ART / 'catalog.json').read_text())['icons']
    for entry in entries:
        compile_icon(entry)
    preview = ART / 'previews'
    preview.mkdir(exist_ok=True)
    font_path = ROOT / 'themes/midnight-express/fonts/Manrope.ttf'
    sheet = Image.new('RGB', (1500, 740), '#0A0C13')
    draw = ImageDraw.Draw(sheet)
    draw.text((30, 20), 'VEYRA / NIGHT VAULT GLASS EDITIONS',
              font=ImageFont.truetype(str(font_path), 26), fill='#EEF2FF')
    for index, entry in enumerate(entries):
        x, y = 30 + index % 5 * 296, 78 + index // 5 * 326
        with Image.open(ART / (entry['id'] + '.webp')) as icon:
            sheet.paste(icon.resize((260, 260), Image.Resampling.LANCZOS), (x, y))
        draw.text((x, y + 270), entry['name'],
                  font=ImageFont.truetype(str(font_path), 18), fill='#DBE3F7')
    sheet.save(preview / 'glass-editions.webp', quality=92, method=6)
    html = ('<!doctype html><meta name="viewport" content="width=device-width,initial-scale=1">'
            '<title>Veyra · Night Vault glass icons</title><style>'
            'body{background:#0a0c13;color:#eef2ff;font:16px system-ui;margin:24px}'
            'main{display:grid;grid-template-columns:repeat(auto-fit,minmax(min(100%,220px),1fr));gap:24px}'
            'article{min-width:0}img{width:100%;max-width:320px}a{color:#d6ddff}</style>'
            '<h1>Veyra / Night Vault glass editions</h1>'
            '<p>Actual bundled 512px masters · stable 0.1.9 build 39</p><main>')
    for entry in entries:
        html += (f'<article><a href="../{entry["id"]}.webp">'
                 f'<img src="../{entry["id"]}.webp" alt="{entry["name"]} glass launcher icon">'
                 f'</a><h2>{entry["name"]}</h2></article>')
    (preview / 'index.html').write_text(html + '</main>')
    print('Compiled ten AI glass masters, 100 density exports and actual-master gallery.')


if __name__ == '__main__':
    compile_icons()
