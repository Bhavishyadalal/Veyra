from pathlib import Path
import xml.etree.ElementTree as ET,re,json
r=Path(__file__).resolve().parents[1];errors=[];xml_count=0;string_count=0;font_count=0;remaining=[]
for module in ['app','sharedUi','shared']:
 base=r/module/'src'
 for p in base.rglob('*.xml'):
  if any(x in p.parts for x in ['build','.gradle']):continue
  try:tree=ET.parse(p);xml_count+=1
  except Exception as e:errors.append(f'{p.relative_to(r)}: {e}');continue
  if p.parent.name.startswith('values'):
   for e in tree.getroot():
    if e.tag in ['string','string-array','plurals']:
     string_count+=1
     if re.search('bitchord', ''.join(e.itertext()),re.I):errors.append(f'Old visible brand: {p.relative_to(r)}:{e.attrib.get("name")}')
  for font in re.findall(r'@font/(\w+)',p.read_text()):
   font_count+=1
   if not list((r/module/'src/main/res/font').glob(font+'.*')): errors.append(f'Missing font {font}: {p.relative_to(r)}')
 for p in base.rglob('*'):
  if not p.is_file() or p.suffix not in ['.kt','.xml','.cpp','.h']:continue
  s=p.read_text()
  for n,line in enumerate(s.splitlines(),1):
   if re.search('bitchord',line,re.I):
    allowed= any(t in line.lower() for t in ['copyright','https://','http://','android:host=','private const val host ='])
    remaining.append({'file':str(p.relative_to(r)),'line':n,'reason':'copyright or preserved external endpoint' if allowed else 'REVIEW','text':line.strip()})
    if not allowed:errors.append(f'Unexpected brand: {p.relative_to(r)}:{n}')
  if re.search(r'com[._/]music[._/]bitchord',s):errors.append(f'Old package/JNI namespace {p.relative_to(r)}')
# Matching JNI identifiers and native load/library targets after the namespace move.
for p in (r/'app/src/main/cpp').rglob('*'):
 if p.is_file() and p.suffix in ['.cpp','.h']:
  if 'Java_com_music_' in p.read_text(errors='ignore'):errors.append(f'Old JNI export {p}')
for mod in ['app']:
 b=(r/mod/'build.gradle.kts').read_text()
 if 'app.veyra.music' not in b:errors.append(f'Missing package ID in {mod}')
report={'xml_files_parsed':xml_count,'localized_string_entries_checked':string_count,'xml_font_references_resolved':font_count,'errors':errors,'intentional_remaining_brand_references':remaining,'checks':'Static source/resource checks; not a native app runtime test.'}
(r/'verification/branding-audit.json').write_text(json.dumps(report,indent=2))
print(json.dumps({k:v for k,v in report.items() if k!='intentional_remaining_brand_references'},indent=2))
if errors:raise SystemExit(1)
