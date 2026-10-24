"""Pocket Riot: ten new cute, bold, independent skins. Existing packs are immutable."""
SPECS=[
 dict(id='kitty-mayhem',name='Kitty Mayhem',font='LilitaOne',material='punk_gel',seek='gel',motion='punk_gel',ink='#FFD0E9',colors=['#FFD0E9','#BCEAFF','#FFE6A7'],variant=0,description='Pink enamel cat-ear frames, bold candy lettering and glossy jelly details.'),
 dict(id='mochi-moshpit',name='Mochi Moshpit',font='RubikBubbles',material='punk_sticker',seek='beads',motion='punk_sticker',ink='#FFE8C5',colors=['#FFE8C5','#FFBCD7','#C4EDDA'],variant=1,description='Puffy ivory mochi scallops, strawberry glass and rounded bubble lettering.'),
 dict(id='peach-bruiser',name='Peach Bruiser',font='Bangers',material='punk_rivet',seek='cable',motion='punk_rivet',ink='#FFD0AA',colors=['#FFD0AA','#CBBEFF','#FFEFAE'],variant=2,description='Peach armor facets, lavender bolts and oversized comic-poster lettering.'),
 dict(id='bunny-voltage',name='Bunny Voltage',font='Bungee',material='punk_chrome',seek='plasma',motion='punk_chrome',ink='#D6C5FF',colors=['#D6C5FF','#BEF6FF','#FFD0E5'],variant=3,description='Violet rabbit-ear keycaps, icy chrome rims and playful electric typography.'),
 dict(id='cherry-bombshell',name='Cherry Bombshell',font='NewRocker',material='flame',seek='flame',motion='flame',ink='#FFBEC9',colors=['#FFBEC9','#FFF0C2','#C3DFFF'],variant=4,description='Cherry-red heart-cut seals, cream rock lettering and ember-ribbon progress.'),
 dict(id='teddy-tantrum',name='Teddy Tantrum',font='Rye',material='botanical',seek='sketch',motion='botanical',ink='#FFE0A9',colors=['#FFE0A9','#D0E8FF','#FFD0C4'],variant=5,description='Caramel teddy-ear patches, visible stitching and bold toy-box lettering.'),
 dict(id='bubble-fang',name='Bubble Fang',font='MetalMania',material='punk_gel',seek='gel',motion='punk_gel',ink='#BAF6DF',colors=['#BAF6DF','#FFD1EB','#E2D0FF'],variant=6,description='Mint glass fangs, pink bubble inlays and strong playful-metal headings.'),
 dict(id='sugar-goblin',name='Sugar Goblin',font='Creepster',material='telemetry',seek='telemetry',motion='telemetry',ink='#D8FFA5',colors=['#D8FFA5','#DCC8FF','#FFD1AB'],variant=7,description='Acid-lime horned arcade tiles, lavender candy rivets and mischievous lettering.'),
 dict(id='cosmic-pudding',name='Cosmic Pudding',font='Audiowide',material='deco',seek='skyline',motion='deco',ink='#C5DFFF',colors=['#C5DFFF','#FFE2AB','#FFD2EB'],variant=8,description='Orbital pudding medallions, pearl-star highlights and spacious cosmic type.'),
 dict(id='sticker-syndicate',name='Sticker Syndicate',font='RubikWetPaint',material='punk_torn',seek='tape',motion='punk_torn',ink='#FFD1A8',colors=['#FFD1A8','#B9DEFF','#F1D0FF'],variant=9,description='Orange-and-blue torn enamel stamps, offset print corners and wet-paint headings.'),
]
ICON_STYLES={
 'soda-sabotage':'cyan soda glass, coral-red lacquer, butter-yellow fizz bubbles',
 'velvet-vandal':'burgundy velvet backing, rose quartz glass, pearl silver thorn filigree',
 'acid-daydream':'chartreuse translucent glass, lilac titanium, checkerboard corner inlays',
 'peach-panic':'peach jelly glass, pale blue enamel, ivory inflatable star corner ornaments',
 'ink-and-ivory':'ivory frosted glass, black ink chrome, tiny crimson torn-print corner details',
 'holo-havoc':'prismatic cyan and violet liquid chrome, holographic orbital glints',
 'denim-disorder':'indigo glass, denim-textured dark backing, copper rim stitching, tiny cherry-red felt corner details',
 'cherry-voltage':'ruby-red glass, black patent backing, electric-blue chrome rims, tiny glass cherries at the border',
 'mint-mutiny':'jade and mint glass, champagne gold, tiny clover jewels at corners',
 'lunar-lollipop':'lilac and periwinkle sugar glass, peach pearl metal, tiny crescent and star border accents',
 'gummy-signal':'cyan gummy glass, strawberry-pink chrome, butter-yellow inflated tiny star inlays',
 'razor-petal':'rose-pink glass, lilac titanium, gentle petal-shaped corner ornaments',
 'disco-chrome':'icy cyan polished chrome, violet iridescence, tiny pink mirror facets',
 'paper-revolt':'ivory frosted enamel glass, coral-red chrome rims, torn-ticket border texture',
 'velvet-sigil':'plum velvet backing, lavender crystal, ornate gold crescent filigree',
 'patch-party':'mint and indigo glass, copper stitch details, small peach enamel corner patches',
 'pixel-fizz':'acid-green candy glass, pale blue chrome, tiny stepped arcade pixel border details',
}
ICON_STYLES.update({s['id']:s['description']+' Colors '+', '.join(s['colors']) for s in SPECS})

def svg(c,key,path,index,pressed=False):
 """Ten distinct vector treatments; every transport keeps its real semantic path."""
 import math
 col=c['colors'][index%3];secondary=c['colors'][(index+1)%3];dark='#171723';light='#FFF3DF';v=c['variant']
 button=key in ('play','pause','previous','next')
 defs=f'<linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop stop-color="{light}"/><stop offset=".3" stop-color="{col}"/><stop offset=".75" stop-color="{secondary}"/><stop offset="1" stop-color="{col}"/></linearGradient>'
 if v==0:frame=f'<path d="M2 10L2 1L9 5H17L24 1V10Q28 25 13 25Q-2 25 2 10Z" fill="url(#g)" stroke="{light}" stroke-width=".6"/><path d="M5 10L7 7M19 7L21 10" stroke="{secondary}" stroke-width="1.2"/>'
 elif v==1:frame=f'<path d="M4 4Q8 0 13 3Q19 0 23 5Q27 9 23 13Q27 19 22 23Q18 27 13 23Q7 27 3 22Q-1 18 3 13Q-1 8 4 4Z" fill="url(#g)" stroke="{light}" stroke-width=".6"/>'
 elif v==2:frame=f'<path d="M7 1H19L25 7V19L19 25H7L1 19V7Z" fill="#342638" stroke="url(#g)" stroke-width="1.5"/><circle cx="6" cy="6" r="1.2" fill="{secondary}"/><circle cx="20" cy="20" r="1.2" fill="{secondary}"/><path d="M9 3H19L23 7" fill="none" stroke="{light}" stroke-width=".5"/>'
 elif v==3:frame=f'<path d="M4 10Q1 -2 6 1Q10 2 10 7H16Q16 -1 21 1Q25 3 22 10Q28 25 13 25Q-2 25 4 10Z" fill="#2A2040" stroke="url(#g)" stroke-width="1.1"/><path d="M6 4L8 8M20 4L18 8" stroke="{secondary}" stroke-width=".8"/>'
 elif v==4:frame=f'<path d="M13 5C4 -3 -3 9 3 16L13 25L23 16C29 9 22 -3 13 5Z" fill="#461B2D" stroke="url(#g)" stroke-width="1.1"/><path d="M4 10Q5 4 9 5" fill="none" stroke="{light}" stroke-width=".7"/>'
 elif v==5:frame=f'<circle cx="5" cy="5" r="4" fill="{secondary}"/><circle cx="21" cy="5" r="4" fill="{secondary}"/><rect x="2" y="3" width="22" height="22" rx="8" fill="#4D342C" stroke="{col}" stroke-width="1.1"/><rect x="4" y="5" width="18" height="18" rx="6" fill="none" stroke="{light}" stroke-width=".5" stroke-dasharray="1 .8"/>'
 elif v==6:frame=f'<rect x="1" y="1" width="24" height="23" rx="7" fill="url(#g)"/><path d="M5 19L7 26L10 19 M16 19L19 26L21 19" fill="{light}" stroke="{secondary}" stroke-width=".45"/><path d="M5 7Q8 2 17 4" fill="none" stroke="white" stroke-width=".7"/>'
 elif v==7:frame=f'<path d="M3 8L1 1L9 5H17L25 1L23 8V21L18 25H8L3 21Z" fill="#263326" stroke="url(#g)" stroke-width="1.1"/><rect x="5" y="9" width="16" height="12" rx="3" fill="none" stroke="{secondary}" stroke-width=".5"/><circle cx="6" cy="6" r=".8" fill="{light}"/><circle cx="20" cy="6" r=".8" fill="{light}"/>'
 elif v==8:frame=f'<circle cx="13" cy="13" r="10.5" fill="#1C2C49" stroke="url(#g)" stroke-width="1.1"/><ellipse cx="13" cy="13" rx="13" ry="5" transform="rotate(-25 13 13)" fill="none" stroke="{secondary}" stroke-width=".8"/><path d="M21 1L22 3L25 4L22 5L21 7L20 5L18 4L20 3Z" fill="{light}"/>'
 else:frame=f'<path d="M3 1L10 3L15 1L23 3L22 9L25 14L23 23L17 22L11 25L4 23L2 17L0 12L3 7Z" fill="{col}" stroke="{light}" stroke-width=".7"/><path d="M2 20L9 23M17 2L24 6" stroke="{secondary}" stroke-width="2"/><path d="M5 3L10 4" stroke="{dark}" stroke-width=".5"/>'
 if not button:frame=f'<g opacity=".48">{frame}</g>'
 ink=dark if v in (0,1,6,9) and button else col
 filled=ink if button else 'none';transform='translate(4.5 5) scale(.66)' if button else 'translate(3.2 4) scale(.74)'
 glyph=f'<g transform="{transform}" stroke-linecap="round" stroke-linejoin="round"><path d="{path}" fill="{filled}" stroke="{light if v==9 else dark}" stroke-width="3.1"/><path d="{path}" fill="{filled}" stroke="{ink}" stroke-width="1.8"/></g>'
 content=frame+glyph
 if pressed:content=f'<g transform="translate(.8 1.3) scale(.94 .91)" opacity=".86">{content}</g>'
 return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="-1 -1 28 28"><defs>{defs}</defs>{content}</svg>'
