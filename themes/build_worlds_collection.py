"""Build twenty immutable Worlds packs from reviewed original AI assets, offline.
All existing published packs stay untouched. Color and glyph families are data-only.
"""
from pathlib import Path
import json
import build_reference_collection as compiler

ROOT=Path(__file__).resolve().parent
compiler.PACK_VERSION=1
BASE_PATHS=dict(compiler.PATHS)
# background, surface, raised, accent, accentContainer, text, muted, outline, secondary
PALETTES={
 'jewel':['111322','20233B','343652','DBC8FF','493559','FFF7F1','D7D2EB','9890B5','ADE0C6'],
 'moss':['141E18','243429','374A3B','F5D69B','55462C','FFF9E9','D4DDC9','8FA287','BBDBBD'],
 'cobalt':['101C31','202F49','344460','BCD8FF','394F73','FFF9EC','D3DBEC','8DA3C8','E5D4AA'],
 'honey':['1B160F','2B2419','403625','F9D18F','5A4324','FFF7E5','E3D2B3','A68C63','E8B774'],
 'sakura':['20152B','30223E','463453','FFC5DB','5D354D','FFF6FB','E1CEE9','A387B3','AEE2EF'],
 'sky':['11232D','203643','344C59','C4E6FC','365469','FFF9EE','CFDEE5','86A8B9','F4D5A6'],
 'copper':['221913','34271F','4B392C','F6C49B','5D3E2B','FFF7EB','E5CEB7','AD8A6D','B0DFD2'],
 'ocean':['0D1E2A','18313E','2C4854','BBE4ED','315563','F4FBFF','C8DCE3','82A5B4','F0C2D9'],
 'bakery':['261A1B','3A2928','513D38','FFD3B4','604235','FFF8EC','E9D4C7','B19583','E7BBD2'],
 'rose':['20151D','31232C','473541','F5C6D6','5A384B','FFF6F9','E5CEDC','AB899C','DBC3AE'],
 'clock':['1B2017','2B3525','404B34','EBD7A7','52472F','FFF8E9','D7DDC4','9BA782','BDD6B1'],
 'winter':['101B30','1E2D47','334461','C4DEFF','3A5075','FFF9F0','D1DDEE','8DA6C6','F4CFAC'],
 'indigo':['111B2C','202E45','34435D','D4DDFA','3C4D6D','FFF9EE','D5DEEC','90A4C2','E5C3A1'],
 'prism':['211A31','302842','473C5B','DDCFFF','503A6D','FFF8F1','DDD2EB','A396BC','F6C7A5'],
 'mono':['171719','28282D','3D3D44','E4E4EA','4A3A42','FAFAFC','D4D4DC','9898A3','FFC1C9'],
 'river':['171C21','28323A','3B4853','E8D8B3','514A35','FFF8EA','D2DEE6','8BA0AE','B6D2ED'],
 'cosmos':['101A2D','1D2E48','304562','B9DDF5','365270','F4FAFF','CFDEED','84A6C6','F5C0DB'],
 'camper':['112035','20324B','344963','F7D7A5','54452E','FFF8EB','D1DDE9','8FA8BF','B4D8ED'],
 'blue':['121D36','202F4F','354568','C4D9FF','3C5077','FBF8FF','D8DEF3','94A7CB','F5C7DC'],
 'current':['0E2034','1B344C','304D65','BCDFF5','365870','F8FBFF','D0E0EC','8AAFC4','F4D49C'],
}
FONTS={'GrenzeGotisch':'halloween-nocturne','Fredoka':'playtime-club','Rajdhani':'midnight-express','SpaceGrotesk':'astral-tide','Manrope':'midnight-express','BarlowCondensed':'web-slinger'}
PLAYFUL={'cloud','bakery','snow','prism'}
ANGULAR={'facets','arcade','circuit','film','pixel','radio','stitch'}
COLLECTION=[]
for d in json.loads((ROOT/'worlds-designs.json').read_text()):
 c=dict(d)
 c.update(enum=''.join(w.title() for w in d['id'].split('-')),alias='World'+''.join(w.title() for w in d['id'].split('-')),
   fontTheme=FONTS[d['font']],palette=PALETTES[d['family']],
   corners=[12,16,24] if d['motif'] in ANGULAR else [24,28,34] if d['motif'] in PLAYFUL else [18,24,30],
   seek='bubbles' if d['motif'] in PLAYFUL else 'web' if d['motif'] in ANGULAR or d['motif']=='rail' else 'ember',
   scene='playful' if d['motif'] in PLAYFUL else 'mist',iconQuality=92,brandQuality=92,
   description=d['scene'].split('. ')[0]+'. '+d['font']+' headings, custom glyphs and optional matching Veyra icon.')
 assert len(c['description'])<=300,(c['id'],len(c['description']))
 COLLECTION.append(c)
# Recognizable functional navigation, modified with each world's material language.
HOME={
 'facets':'M3 11L12 3L21 11 M5 10V21H19V10 M9 14L12 11L15 14L12 18Z',
 'rail':'M3 11L12 3L21 11 M5 10V21H19V10 M8 13H16V18H8Z M12 13V18',
 'porcelain':'M3 11Q12 0 21 11 M5 10V21H19V10 M9 21V15Q12 12 15 15V21',
 'hex':'M3 11L12 3L21 11 M5 10V21H19V10 M12 12L16 14V18L12 20L8 18V14Z',
 'arcade':'M3 11H6V8H9V5H15V8H18V11H21 M5 11V21H19V11 M9 15H15V21H9Z',
 'cloud':'M3 11Q12 1 21 11 M5 10V21H19V10 M9 21V16Q12 13 15 16V21',
 'radio':'M3 11L12 3L21 11 M5 10V21H19V10 M9 21V15H15V21 M15 6V2 M12 4H18',
 'aquarium':'M3 11L12 3L21 11 M5 10V21H19V10 M8 16Q12 12 16 16 M8 18Q12 14 16 18',
 'bakery':'M3 11L12 3L21 11 M5 10V21H19V10 M8 16Q12 10 16 16V20H8Z',
 'circuit':'M3 11L12 3L21 11 M5 10V21H19V10 M9 21V15H15V21 M17 5H21V8',
 'clock':'M3 11L12 3L21 11 M5 10V21H19V10 M15 15A3 3 0 1 1 9 15A3 3 0 0 1 15 15 M12 13V15H14',
 'snow':'M3 11L12 3L21 11 M5 10V21H19V10 M9 21V15H15V21 M8 7L12 10L16 7',
 'stitch':'M3 11L12 3L21 11 M5 10V21H19V10 M9 14L15 20 M15 14L9 20',
 'prism':'M3 11L12 3L21 11 M5 10V21H19V10 M12 13L16 17L12 21L8 17Z',
 'film':'M3 11L12 3L21 11 M5 10V21H19V10 M9 14H15V19H9Z M6 15H7 M17 15H18',
 'river':'M3 11L12 3L21 11 M5 10V21H19V10 M8 15Q12 12 16 15 M8 18Q12 15 16 18',
 'pixel':'M3 11H6V8H9V5H15V8H18V11H21 M5 11V21H10V16H14V21H19V11',
 'camper':'M3 11L12 3L21 11 M5 10V21H19V10 M8 13H16V18H8Z M8 21V19 M16 21V19',
 'garden':'M3 11Q12 0 21 11 M5 10V21H19V10 M9 21V15Q12 10 15 15V21',
 'current':'M3 11L12 3L21 11 M5 10V21H19V10 M8 15Q10 12 12 15Q14 18 16 15 M8 18Q10 15 12 18Q14 21 16 18',
}
MARK={
 'facets':'M10 6L14 10L10 14L6 10Z','rail':'M6 7H14V13H6Z M8 7V13 M12 7V13',
 'porcelain':'M7 7Q10 4 13 7Q16 10 13 13Q10 16 7 13',
 'hex':'M10 5L14 8V12L10 15L6 12V8Z','arcade':'M6 10H14 M10 6V14',
 'cloud':'M6 11Q3 7 7 7Q10 2 13 7Q17 7 14 12H7',
 'radio':'M7 7Q13 10 7 13 M10 5Q18 10 10 15',
 'aquarium':'M6 10Q10 4 14 10Z M8 10V14 M12 10V14',
 'bakery':'M6 12Q10 4 14 12Z M8 8L10 12 M12 8L10 12',
 'circuit':'M6 6H14V14H6Z M10 3V6 M10 14V17',
 'clock':'M14 10A4 4 0 1 1 6 10A4 4 0 0 1 14 10 M10 7V10H13',
 'snow':'M10 5V15 M6 7L14 13 M6 13L14 7',
 'stitch':'M6 6L14 14 M14 6L6 14',
 'prism':'M10 4L15 10L10 16L5 10Z M10 4V16 M5 10H15',
 'film':'M5 6H15V14H5Z M7 7V8 M13 7V8 M7 12V13 M13 12V13',
 'river':'M5 8Q8 5 11 8Q14 11 16 8 M5 12Q8 9 11 12Q14 15 16 12',
 'pixel':'M7 4H13V7H16V13H13V16H7V13H4V7H7Z',
 'camper':'M5 7H15V13H5Z M7 7V13 M13 7V13 M7 14H7.1 M13 14H13.1',
 'garden':'M10 5Q16 6 13 10Q16 14 10 15Q4 14 7 10Q4 6 10 5Z',
 'current':'M5 10Q10 4 14 10Q10 16 5 10Z M14 10L17 7V13Z',
}
def glyphs(c):
 motif=c['motif']
 paths=BASE_PATHS | {
  'nav_home':HOME[motif],
  'nav_search':'M17 17L22 22 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 '+MARK[motif],
  'nav_explore':('M12 2L21 12L12 22L3 12Z M15 8L13 14L8 16L10 10Z' if motif in ANGULAR else 'M21 12A9 9 0 1 1 3 12A9 9 0 0 1 21 12 M15 8L13 14L8 16L10 10Z'),
  'nav_library':('M3 5Q7 3 12 6Q17 3 21 5V20Q17 18 12 21Q7 18 3 20Z M12 6V21 M6 9H8 M16 9H18' if motif in PLAYFUL or motif in {'porcelain','garden','current'} else 'M3 4H8V21H3Z M11 4H16V21H11Z M19 4L22 20 M5 8H6 M13 8H14'),
  'play':('M7 4L21 12L7 20L9 12Z' if motif in ANGULAR else 'M8 5Q8 3 10 5L20 11Q22 12 20 13L10 19Q8 21 8 19Z'),
  'pause':('M5 4H10V20H5Z M14 4H19V20H14Z' if motif in ANGULAR else 'M6 5Q8 3 10 5V19Q8 21 6 19Z M14 5Q16 3 18 5V19Q16 21 14 19Z'),
 }
 return paths
if __name__=='__main__':
 entries=[]
 for c in COLLECTION:
  compiler.PATHS=glyphs(c)
  entries.append(compiler.build(c))
 catalog=json.loads((ROOT/'catalog.json').read_text());ids={c['id'] for c in COLLECTION}
 catalog['themes']=[t for t in catalog['themes'] if t['id'] not in ids]+entries
 text=json.dumps(catalog,indent=2)+'\n'
 assert len(text.encode())<=65536,'Keep the existing bounded catalog payload'
 (ROOT/'catalog.json').write_text(text);(ROOT.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
