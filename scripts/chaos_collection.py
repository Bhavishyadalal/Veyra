"""Shared art direction for ten full themes and seven independent overlays.
All palettes/materials are data-only existing presets. Never rebuild published old packs.
"""
THEMES=[
 ('soda-sabotage','Soda Sabotage','RubikBubbles','punk_gel','gel','00191D','073036','13434A','8CEEFF','174451','FFF4DC','D0E1DF','799B9B','FFB0A1','Glass fizz. Loud feelings.','bubble-riot'),
 ('velvet-vandal','Velvet Vandal','Lacquer','deco','skyline','1A0D1C','2F1830','45273F','F7BEDC','563047','FFF2E6','E9CEDA','A9819D','D6C4FF','A soft kind of rebellion.','velvet-hex'),
 ('acid-daydream','Acid Daydream','RubikGlitch','telemetry','telemetry','151711','252C1B','36422B','D9FF81','3C4B1C','F8F6E4','DBE3C8','94A078','D9C1FF','Dream in high voltage.','riot-candy'),
 ('peach-panic','Peach Panic','RubikBubbles','punk_sticker','beads','241425','3B2438','513046','FFD0A6','5B3B2B','FFF3E3','ECD5DE','B38D9E','B7DFFF','Sweetness with a pulse.','bubble-riot'),
 ('ink-and-ivory','Ink and Ivory','Lacquer','punk_torn','tape','161515','292524','3E3833','FBE8BD','4C4030','FFF5E6','E0D5CB','A2998B','FFB4AC','Turn the page. Turn it up.','xerox-afterparty'),
 ('holo-havoc','Holo Havoc','BungeeShade','punk_chrome','plasma','101329','1B2541','303753','B8F4FF','254457','F8F2FF','D2D8EB','8593B6','F5BBF3','Prisms after midnight.','cosmic-vandal'),
 ('denim-disorder','Denim Disorder','Lacquer','botanical','sketch','0F1A2A','1D2D42','304257','BADBFF','2E4864','FFF0DB','D2DBE4','839BAE','FFC4A5','A mixtape stitched by hand.','midnight-stitch'),
 ('cherry-voltage','Cherry Voltage','RubikGlitch','punk_rivet','cable','210F19','361B29','4F2937','FFB7C5','5C2939','FFF0E3','E8CED8','AC7D95','A9DEFF','Redline your favorite song.','razor-bloom'),
 ('mint-mutiny','Mint Mutiny','RubikWetPaint','botanical','vine','0C201C','19362E','2C4A3C','BCEAC5','2A5137','FFF5DC','D0E2D4','87AC94','FFE0A0','Wild little listening garden.','chrome-venom'),
 ('lunar-lollipop','Lunar Lollipop','RubikBubbles','punk_gel','bubbles','15142D','272442','3D3557','D8C5FF','43335D','FFF0E8','DED2E9','9E8DB6','FFC4BD','Sugar on the dark side.','cosmic-vandal'),
]
ASSETS=[
 ('gummy-signal','Gummy Signal','LilitaOne','punk_gel','gel',['#BAF5FF','#FFB3D7','#FFE6AB'],'Inflated gel frames and star-cut highlights.'),
 ('razor-petal','Razor Petal','NewRocker','flame','flame',['#FFC0D1','#E4C8FF','#FFF1C4'],'Petal-edged rock seals and sharp poster lettering.'),
 ('disco-chrome','Disco Chrome','Audiowide','punk_chrome','plasma',['#C0F8FF','#E6C2FF','#FFD0E8'],'Faceted chrome keys and orbital light traces.'),
 ('paper-revolt','Paper Revolt','Bangers','punk_torn','tape',['#FFF0C8','#FFBEA5','#D1E5FF'],'Torn double-print stamps and bold concert lettering.'),
 ('velvet-sigil','Velvet Sigil','GrenzeGotisch','deco','skyline',['#E5CCFF','#FFCADD','#F7DFB5'],'Gothic arches, crescent seals and fine silver ornament.'),
 ('patch-party','Patch Party','Bungee','botanical','sketch',['#C0E8D7','#FFD0AB','#CFDFFF'],'Stitched denim patches and varsity headline details.'),
 ('pixel-fizz','Pixel Fizz','MetalMania','telemetry','telemetry',['#DEFFAD','#BCE8FF','#F0C5FF'],'Stepped arcade frames and candy instrument marks.'),
]

def theme_specs():
 for index,row in enumerate(THEMES):
  id,name,font,material,seek,*other=row
  colors=other[:9];tagline,brand=other[9:]
  yield dict(id=id,name=name,font=font,material=material,seek=seek,colors=colors,
   inks=[colors[3],colors[8],colors[5],colors[6]],tagline=tagline,variant=index%7,
   medium='Chaos Studio / original material collage',description=tagline+' '+material.replace('_',' ')+' controls, original AI wallpaper, colored display lettering and '+seek+' progress.',brandSource=brand)

def asset_specs():
 for index,(id,name,font,material,seek,colors,description) in enumerate(ASSETS):
  yield dict(id=id,name=name,font=font,material=material,seek=seek,motion=material,colors=colors,ink=colors[0],description=description,variant=index)

def svg(c,key,path,i,pressed=False):
 """Seven separately authored skins; semantic paths retain their familiar meaning."""
 colors=c.get('inks',c.get('colors'));col=colors[i%len(colors)]
 if not col.startswith('#'):col='#'+col
 secondary=colors[(i+1)%len(colors)];secondary=secondary if secondary.startswith('#') else '#'+secondary
 dark='#171723';light='#FFF4DE';v=c['variant'];button=key in ('play','pause','previous','next')
 defs=f'<linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop stop-color="{light}"/><stop offset=".35" stop-color="{col}"/><stop offset=".75" stop-color="{secondary}"/><stop offset="1" stop-color="{col}"/></linearGradient>'
 if v==0: frame=f'<rect x="1" y="1" width="24" height="24" rx="9" fill="url(#g)"/><path d="M5 7Q8 2 16 4" fill="none" stroke="white" stroke-width="1.2"/><path d="M20 20L22 22" stroke="{dark}" stroke-opacity=".4"/>'
 elif v==1: frame=f'<path d="M13 1L17 5L23 3L22 10L26 13L22 17L23 23L17 22L13 26L9 22L3 23L4 17L0 13L4 9L3 3L9 4Z" fill="{dark}" stroke="url(#g)" stroke-width="1.1"/>'
 elif v==2:frame=f'<path d="M7 1H19L25 7V19L19 25H7L1 19V7Z" fill="{dark}" stroke="url(#g)" stroke-width="1.5"/><path d="M8 3H18L23 8V18L18 23H8L3 18V8Z" fill="none" stroke="{secondary}" stroke-width=".5"/>'
 elif v==3:frame=f'<path d="M2 3L8 1L13 3L21 1L25 5L23 12L25 20L20 25L13 23L5 25L1 20L3 13L1 7Z" fill="{col}" stroke="{light}" stroke-width=".6"/><path d="M3 23L8 22 M18 3L24 2" stroke="{secondary}" stroke-width="1.5"/>'
 elif v==4:frame=f'<path d="M2 24V11Q2 5 13 1Q24 5 24 11V24Z" fill="{dark}" stroke="url(#g)" stroke-width="1"/><path d="M4 22V11Q4 7 13 3Q22 7 22 11V22 M7 21H19" fill="none" stroke="{secondary}" stroke-width=".45"/>'
 elif v==5:frame=f'<rect x="1" y="1" width="24" height="24" rx="5" fill="#24394D" stroke="{col}" stroke-width="1"/><rect x="3" y="3" width="20" height="20" rx="4" fill="none" stroke="{light}" stroke-width=".55" stroke-dasharray="1 .9"/>'
 else:frame=f'<path d="M5 1H21V5H25V21H21V25H5V21H1V5H5Z" fill="{dark}" stroke="{col}" stroke-width="1"/><path d="M6 3H20M3 6V20M6 23H20M23 6V20" fill="none" stroke="{secondary}" stroke-width=".6"/>'
 if not button:frame=f'<g opacity=".42">{frame}</g>'
 ink=dark if v in (0,3) and button else col
 fill=ink if button else 'none'
 scale='translate(4 4) scale(.7)' if button else 'translate(3 3) scale(.77)'
 glyph=f'<g transform="{scale}" fill="{fill}" stroke-linecap="round" stroke-linejoin="round"><path d="{path}" stroke="{light if v==3 else dark}" stroke-width="3.2"/><path d="{path}" stroke="{ink}" stroke-width="1.8"/></g>'
 content=frame+glyph
 if pressed:content=f'<g transform="translate(.7 1.2) scale(.94 .91)" opacity=".85">{content}</g>'
 return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="-1 -1 28 28"><defs>{defs}</defs>{content}</svg>'
