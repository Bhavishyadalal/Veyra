"""Build only five Dreamscape packs from reviewed AI assets, using the immutable compiler.
No network or credentials. Earlier published packs and theme bindings are preserved.
"""
from pathlib import Path
import json
import build_reference_collection as compiler

compiler.PACK_VERSION = 1
BASE_PATHS = dict(compiler.PATHS)
COLLECTION = [
 dict(id='chrome-vesper',name='Chrome Vesper',enum='ChromeVesper',alias='Vesper',font='GrenzeGotisch',fontTheme='halloween-nocturne',palette=['160D15','271923','3C2A36','F7C5DB','593449','FFF5FA','E2C8D8','AA839A','D5DFED'],corners=[10,14,22],seek='web',scene='mist',tagline='Silver edges. Midnight echoes.',motif='gothic',description='Chrome cruciform filigree, black velvet and garnet light. Gothic headings, cathedral navigation glyphs, faceted transport and an optional silver-chain Glass Noir icon.'),
 dict(id='window-wish',name='Window Wish',enum='WindowWish',alias='Wish',font='Fredoka',fontTheme='playtime-club',palette=['171D16','283125','3C4934','F5D79C','55482D','FFF9E8','D8DDC4','94A17C','C7D6B3'],corners=[24,28,32],seek='ember',scene='mist',tagline='A small window. A softer world.',motif='window',description='A moonlit walnut window, clover and original golden chick friends. Fredoka headings, moss-and-cream controls, window navigation glyphs and an optional crescent-window Glass Noir icon.'),
 dict(id='cassette-corner',name='Cassette Corner',enum='CassetteCorner',alias='Cassette',font='Rajdhani',fontTheme='midnight-express',palette=['14201D','23332D','35483F','F7C3AB','594235','FFF8E9','D0DFD5','8AA695','B9DDD0'],corners=[12,16,24],seek='web',scene='mist',tagline='Side A: your favourite little place.',motif='cassette',description='An original calico record-shop cat, mint fluorescent light and coral cassette shelves. Bold Rajdhani, tape-deck controls, record navigation glyphs and an optional miniature cassette Glass Noir icon.'),
 dict(id='azure-bloom',name='Azure Bloom',enum='AzureBloom',alias='Azure',font='SpaceGrotesk',fontTheme='astral-tide',palette=['0D1830','1A2A46','2E4060','BCD9FF','354E73','F5F8FF','CCD9EF','819EC6','F3C3DC'],corners=[20,24,30],seek='ember',scene='mist',tagline='Let the quiet bloom blue.',motif='blossom',description='Luminous blue blossom canopy, pink wildflowers and golden fireflies. Space Grotesk headings, moonstone controls, blossom navigation glyphs and an optional tree-under-glass Veyra icon.'),
 dict(id='strawberry-daydream',name='Strawberry Daydream',enum='StrawberryDaydream',alias='Daydream',font='Fredoka',fontTheme='playtime-club',palette=['281B30','3A2944','503B5B','FFD1DF','624454','FFF8F0','E9D2E8','B195B9','D4E6AD'],corners=[26,30,36],seek='bubbles',scene='playful',tagline='Press play on a sweeter day.',motif='daisy',description='Pastel cut-paper cat and bunny friends, strawberries and ivory daisies. Fredoka headings, readable plum controls, heart-and-petal glyphs and an optional strawberry-charm Glass Noir icon.'),
]
NAV = {
 'gothic':[
  'M3 11L12 2L21 11 M5 10V21H10V16Q12 12 14 16V21H19V10 M9 8L12 5L15 8',
  'M17 17L22 22 M10 2L18 10L10 18L2 10Z M10 6V14 M6 10H14',
  'M12 2L15 9L22 12L15 15L12 22L9 15L2 12L9 9Z M12 7V17 M7 12H17',
  'M3 4L6 2L9 4V21H3Z M12 4L15 2L18 4V21H12Z M21 4V21 M5 7H7 M14 7H16'],
 'window':[
  'M3 11L12 3L21 11 M5 10V21H19V10 M9 12H15V18H9Z M12 12V18',
  'M17 17L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M11 6A4 4 0 1 0 14 12A4 4 0 0 1 11 6',
  'M12 3L20 12L12 21L4 12Z M12 8L16 12L12 16L8 12Z',
  'M3 5Q8 3 12 6Q16 3 21 5V20Q16 18 12 21Q8 18 3 20Z M12 6V21 M6 10H8 M16 10H18'],
 'cassette':[
  'M3 10L12 3L21 10 M4 10V21H20V10 M4 10H20 M8 21V15H16V21 M6 10V13 M11 10V13 M16 10V13',
  'M17 17L22 22 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M13 10A3 3 0 1 1 7 10A3 3 0 0 1 13 10',
  'M21 12A9 9 0 1 1 3 12A9 9 0 0 1 21 12 M15 7L13 14L7 17L10 10Z',
  'M3 4H8V21H3Z M11 4H16V21H11Z M19 5L22 20 M5 8H6 M13 8H14 M5 17H6 M13 17H14'],
 'blossom':[
  'M3 11L12 3L21 11 M5 10V21H19V10 M10 21V15H14V21 M17 4L19 2L21 4L19 6Z',
  'M17 17L22 22 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M10 6L12 8L14 10L12 12L10 14L8 12L6 10L8 8Z',
  'M12 2L15 8L21 12L15 16L12 22L9 16L3 12L9 8Z M12 8V16 M8 12H16',
  'M3 5Q7 3 12 6Q17 3 21 5V20Q17 18 12 21Q7 18 3 20Z M12 6V21 M6 9L8 11 M18 9L16 11'],
 'daisy':[
  'M3 11Q12 0 21 11 M5 10V21H19V10 M9 15Q12 11 15 15Q15 18 12 20Q9 18 9 15Z',
  'M17 17L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M7 8Q10 5 13 8Q14 10 10 13Q6 10 7 8Z',
  'M12 3L15 9L21 12L15 15L12 21L9 15L3 12L9 9Z M12 9L15 12L12 15L9 12Z',
  'M3 5Q7 3 12 6Q17 3 21 5V20Q17 18 12 21Q7 18 3 20Z M12 6V21 M6 9H8 M16 9H18'],
}
# Controlled variants of familiar functional symbols; no ambiguous decorative replacements.
TRANSPORT = {
 'gothic':{'play':'M8 3L21 12L8 21L10 12Z','pause':'M5 4L9 6V18L5 20Z M15 6L19 4V20L15 18Z'},
 'window':{'play':'M8 5Q8 3 10 5L20 11Q22 12 20 13L10 19Q8 21 8 19Z','pause':'M6 5Q8 3 10 5V19Q8 21 6 19Z M14 5Q16 3 18 5V19Q16 21 14 19Z'},
 'cassette':{'play':'M7 4H9L21 12L9 20H7Z','pause':'M5 4H10V20H5Z M14 4H19V20H14Z','queue':'M3 4H21V20H3Z M7 10A2 2 0 1 0 11 10A2 2 0 0 0 7 10 M14 10A2 2 0 1 0 18 10A2 2 0 0 0 14 10 M7 17H17'},
 'blossom':{'play':'M8 4L21 12L8 20Q11 12 8 4Z','pause':'M6 5Q8 3 10 5V19Q8 21 6 19Z M14 5Q16 3 18 5V19Q16 21 14 19Z'},
 'daisy':{'play':'M8 5Q8 3 10 5L20 11Q22 12 20 13L10 19Q8 21 8 19Z','pause':'M6 5Q8 3 10 5V19Q8 21 6 19Z M14 5Q16 3 18 5V19Q16 21 14 19Z'},
}
if __name__ == '__main__':
 entries=[]
 for c in COLLECTION:
  compiler.PATHS = BASE_PATHS | dict(zip(['nav_home','nav_search','nav_explore','nav_library'],NAV[c['motif']])) | TRANSPORT[c['motif']]
  entries.append(compiler.build(c))
 root=Path(__file__).resolve().parent
 catalog=json.loads((root/'catalog.json').read_text());ids={c['id'] for c in COLLECTION}
 catalog['themes']=[t for t in catalog['themes'] if t['id'] not in ids]+entries
 text=json.dumps(catalog,indent=2)+'\n'
 (root/'catalog.json').write_text(text);(root.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
