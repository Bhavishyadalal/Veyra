package app.veyra.music.ui.themes

private val warmGlyphs=setOf("nav_home","nav_search","nav_explore","nav_library","settings","back","queue","lyrics",
    "account","history","download","shuffle","repeat","favorite","play","pause")
private val warmTransports=setOf("play","pause","previous","next","play_pressed","pause_pressed","previous_pressed","next_pressed")

fun themeGlyphCacheKey(path:String,pixels:Int):String {
    require(pixels == 128 || pixels == 256)
    return "theme-glyph/v1/$pixels/$path"
}

/** At most 24 small decodes; wallpaper, previews and fonts never enter glyph warmup. */
fun themeGlyphWarmupPlan(assets:Map<String,String>):List<Pair<String,Int>> =
    (assets.filterKeys {it in warmGlyphs}.values.map {it to 128} +
        assets.filterKeys {it in warmTransports}.values.map {it to 256}).distinct().take(24)
