"""Five original reference-inspired themes. Reuses the immutable pack compiler.
Only this collection is written; previous published packs/assets are untouched.
"""
from pathlib import Path
import json
import build_reference_collection as compiler

compiler.PACK_VERSION = 1
COLLECTION = [
 dict(id='golden-afterglow',name='Golden Afterglow',enum='GoldenAfterglow',alias='Afterglow',font='Manrope',fontTheme='midnight-express',palette=['181710','29261D','3C382A','F5CC91','554124','FFF8E9','DCCFB7','9C8766','BCD1AC'],corners=[18,24,28],seek='ember',scene='mist',tagline='Stay for one more golden song.',motif='meadow',description='Cinematic amber meadow and dusk silhouettes. Bold Manrope, bronze-and-olive controls, meadow navigation glyphs and an optional sunset-glass Veyra icon.'),
 dict(id='lilypond-parade',name='Lilypond Parade',enum='LilypondParade',alias='Lilypond',font='Fredoka',fontTheme='playtime-club',palette=['102220','18332F','294A43','C4EBCB','315447','F8FFF4','C9DED3','729C8D','FFD0A8'],corners=[26,30,36],seek='bubbles',scene='playful',tagline='Little friends. Lovely little listens.',motif='pond',description='Original pond pals and impressionist lily pads. Fredoka headings, jade surfaces, bubble timeline, lily navigation glyphs and an optional bunny-charm glass Veyra icon.'),
 dict(id='dustbyte-outpost',name='Dustbyte Outpost',enum='DustbyteOutpost',alias='Dustbyte',font='Rajdhani',fontTheme='midnight-express',palette=['211724','302237','45304C','FFD09B','5B3C3F','FFF4ED','E7C9DB','A784A2','D0B5F4'],corners=[8,12,18],seek='web',scene='mist',tagline='A small station. An endless soundtrack.',motif='pixel',description='Pixel-art desert cabin, mauve mesas and copper dusk. Bold Rajdhani, stepped navigation glyphs, route timeline and an optional miniature desert-diorama Veyra icon.'),
 dict(id='alpine-ember',name='Alpine Ember',enum='AlpineEmber',alias='Alpine',font='SpaceGrotesk',fontTheme='astral-tide',palette=['151D21','223036','35454B','FFC5A3','574139','FFF6ED','CEDDDF','819C9D','C8C0EF'],corners=[16,20,26],seek='ember',scene='mist',tagline='Find your cabin in the sound.',motif='alpine',description='Pixel-art mountain cabin, mirrored lake and rose-gold dawn. Space Grotesk headings, pine-and-slate controls, mountain navigation glyphs and an optional alpine glass Veyra icon.'),
 dict(id='starfall-pals',name='Starfall Pals',enum='StarfallPals',alias='Starfall',font='Fredoka',fontTheme='playtime-club',palette=['0D1428','18233D','2A3653','E1CCFF','493959','FFF5ED','CED5EF','8996BB','F5CCAC'],corners=[26,30,36],seek='bubbles',scene='playful',tagline='Catch a star. Keep a song.',motif='stars',description='Original moon-bunny friends under painterly shooting stars. Fredoka headings, ink-blue glass, peach starlight, star-navigation glyphs and an optional celestial bunny-glass Veyra icon.'),
]
# Purpose remains immediately recognizable: home, magnifier, compass, books.
NAV = {
 'meadow':[
  'M3 11L12 4L21 11 M5 10V21H19V10 M10 21V15H14V21 M17 3V6 M20 5L18 7',
  'M17 17L22 22 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M10 6V14 M7 9L10 12L13 9',
  'M21 12A9 9 0 1 1 3 12A9 9 0 0 1 21 12 M15 8L13 14L8 16L10 10Z',
  'M3 4H8V21H3Z M11 4H16V21H11Z M19 5L22 20 M5 8H6 M13 8H14'],
 'pond':[
  'M3 11Q12 0 21 11 M5 10V20Q12 23 19 20V10 M10 21V15Q12 13 14 15V21',
  'M17 17L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M6 10Q10 14 14 10',
  'M12 3Q20 4 21 12Q20 20 12 21Q4 20 3 12Q4 4 12 3 M12 7L16 12L12 17L8 12Z',
  'M3 5Q7 3 12 6Q17 3 21 5V20Q17 18 12 21Q7 18 3 20Z M12 6V21 M6 9H8 M16 9H18'],
 'pixel':[
  'M3 11H6V8H9V5H15V8H18V11H21 M5 11V21H10V16H14V21H19V11',
  'M17 17H19V19H21V21 M4 3H14V5H17V15H14V18H4V15H1V6H4Z',
  'M8 2H16V5H21V19H16V22H8V19H3V5H8Z M12 7H16V12H12V17H8V12H12Z',
  'M3 4H8V21H3Z M11 4H16V21H11Z M19 4H22V21H19 M4 8H7 M12 8H15'],
 'alpine':[
  'M3 12L12 4L21 12 M5 11V21H19V11 M10 21V15H14V21 M15 6V3H18V9',
  'M17 17L22 22 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M5 13L9 7L14 13Z',
  'M12 2L22 20H2Z M8 9L12 12L16 9 M12 15V18',
  'M3 4H8V21H3Z M11 4H16V21H11Z M19 4L22 20 M5 8H6 M13 8H14'],
 'stars':[
  'M3 12L12 4L21 12 M5 11V21H19V11 M10 21V16H14V21 M12 8L13 10L12 12L11 10Z',
  'M17 17L21 21 M18 10A8 8 0 1 1 2 10A8 8 0 0 1 18 10 M10 6L11 9L14 10L11 11L10 14L9 11L6 10L9 9Z',
  'M12 2L15 8L22 12L15 16L12 22L9 16L2 12L9 8Z M12 9V15 M9 12H15',
  'M3 5Q7 3 12 6Q17 3 21 5V20Q17 18 12 21Q7 18 3 20Z M12 6V21 M6 9L8 12 M18 9L16 12'],
}
if __name__ == '__main__':
 entries=[]
 for c in COLLECTION:
  compiler.PATHS.update(dict(zip(['nav_home','nav_search','nav_explore','nav_library'],NAV[c['motif']])))
  entries.append(compiler.build(c))
 root=Path(__file__).resolve().parent
 catalog=json.loads((root/'catalog.json').read_text());ids={c['id'] for c in COLLECTION}
 catalog['themes']=[t for t in catalog['themes'] if t['id'] not in ids]+entries
 text=json.dumps(catalog,indent=2)+'\n'
 (root/'catalog.json').write_text(text);(root.parent/'app/src/main/assets/theme-catalog.json').write_text(text)
