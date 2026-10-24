#!/usr/bin/env python3
"""Export reviewed AI glass masters and register every previously missing pack binding."""
from pathlib import Path
import hashlib,json
from PIL import Image,ImageDraw,ImageFont
ROOT=Path(__file__).resolve().parents[1];ART=ROOT/'design/launcher/pocket-riot'
DENSITIES=[('mdpi',1),('hdpi',1.5),('xhdpi',2),('xxhdpi',3),('xxxhdpi',4)]
def enum_name(e):return 'Pocket'+e['kind'].title()+''.join(p.title() for p in e['id'].split('-'))
def register(items):
 p=ROOT/'app/src/main/java/app/veyra/music/AppIcon.kt';s=p.read_text();entries=''
 for e in items:
  name=enum_name(e);key=e['id'].replace('-','_')
  if name+'(' not in s:entries+=f'    {name}("Launcher{name}", R.mipmap.ic_pocket_{key}, R.string.app_icon_pocket_{key}, R.mipmap.ic_pocket_{key}_foreground),\n'
 s=s.replace('    CollectionCathedralStatic(',entries+'    CollectionCathedralStatic(',1)
 for kind,method in [('theme','forTheme'),('asset','forAssetPack')]:
  start=s.index('fun '+method);i=s.index('when',start);j=s.index('{',i)+1
  additions=''.join(f'\n            "{e["id"]}" -> {enum_name(e)}' for e in items if e['kind']==kind and f'"{e["id"]}" ->' not in s[start:s.index('else -> null',start)])
  s=s[:j]+additions+s[j:]
 p.write_text(s)
 p=ROOT/'app/src/main/AndroidManifest.xml';s=p.read_text();entries=''
 for e in items:
  name=enum_name(e);key=e['id'].replace('-','_')
  if f'.Launcher{name}"' not in s:entries+=f'        <activity-alias android:name=".Launcher{name}" android:enabled="false" android:exported="true" android:icon="@mipmap/ic_pocket_{key}" android:roundIcon="@mipmap/ic_pocket_{key}" android:label="@string/app_name" android:targetActivity=".MainActivity"><intent-filter><action android:name="android.intent.action.MAIN"/><category android:name="android.intent.category.LAUNCHER"/></intent-filter></activity-alias>\n'
 p.write_text(s.replace('</application>',entries+'    </application>'))
 p=ROOT/'app/src/main/res/values/strings.xml';s=p.read_text();entries=''
 for e in items:
  key=e['id'].replace('-','_')
  if f'name="app_icon_pocket_{key}"' not in s:entries+=f'    <string name="app_icon_pocket_{key}">{e["name"]} Glass</string>\n'
 p.write_text(s.replace('</resources>',entries+'</resources>'))
 p=ROOT/'app/src/main/java/app/veyra/music/data/themes/IconPickerPolicy.kt';s=p.read_text();needle='    fun collection(icon: AppIcon): IconCollection = when {'
 if 'icon.name.startsWith("PocketTheme")' not in s:s=s.replace(needle,needle+'\n        icon.name.startsWith("PocketTheme") -> IconCollection.THEMES\n        icon.name.startsWith("PocketAsset") -> IconCollection.ASSETS')
 p.write_text(s)
def compile_icons():
 items=json.loads((ART/'catalog.json').read_text())['icons'];records=json.loads((ART/'generated-sources.json').read_text())['images']
 lookup={e['id']:e for e in records}
 for e in items:
  master=ART/(e['id']+'.webp');assert hashlib.sha256(master.read_bytes()).hexdigest()==lookup[e['id']]['masterSha256']
  with Image.open(master) as image:
   assert image.size==(512,512);full=image.convert('RGB')
  key=e['id'].replace('-','_');res=ROOT/'app/src/main/res'
  for density,scale in DENSITIES:
   for suffix,dp in [('',48),('_foreground',108)]:
    side=int(dp*scale);full.resize((side,side),Image.Resampling.LANCZOS).save(res/f'mipmap-{density}/ic_pocket_{key}{suffix}.webp',quality=88,method=6)
  (res/f'mipmap-anydpi-v26/ic_pocket_{key}.xml').write_text('<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android"><background android:drawable="@color/wave_glass_background"/>'+f'<foreground android:drawable="@mipmap/ic_pocket_{key}_foreground"/><monochrome android:drawable="@drawable/ic_wave_monochrome"/></adaptive-icon>\n')
 register(items)
 preview=ART/'previews';preview.mkdir(exist_ok=True);sheet=Image.new('RGB',(1400,40+((len(items)+6)//7)*226),'#10121B');d=ImageDraw.Draw(sheet);font=ImageFont.truetype(str(ROOT/'themes/midnight-express/fonts/Manrope.ttf'),15)
 d.text((20,12),'VEYRA / POCKET RIOT + COMPLETION / ACTUAL GLASS MASTERS',font=font,fill='#FFEEDC')
 for i,e in enumerate(items):
  x=18+(i%7)*197;y=45+(i//7)*226
  with Image.open(ART/(e['id']+'.webp')) as im:sheet.paste(im.resize((176,176),Image.Resampling.LANCZOS),(x,y))
  d.text((x,y+182),e['name'],font=font,fill='#F6E7EE')
 sheet.save(preview/'glass-editions.webp',quality=90,method=6)
 print('Compiled',len(items),'AI masters, 270 density images, aliases, labels and theme/asset bindings.')
if __name__=='__main__':compile_icons()
