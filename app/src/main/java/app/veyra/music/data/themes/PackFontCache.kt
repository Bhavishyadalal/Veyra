package app.veyra.music.data.themes

import android.graphics.Typeface
import android.util.LruCache
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import app.veyra.music.ui.themes.ThemeVisuals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Bounded, process-local font cache. All file/typeface work runs in repository IO or produceState IO. */
internal object PackFontCache {
    private val fonts=LruCache<String,FontFamily>(8)
    fun peek(path:String):FontFamily? = fonts.get(path)
    fun load(path:String):FontFamily {
        fonts.get(path)?.let {return it}
        // Typeface creation must not hold the monitor used by the main-thread lookup.
        val created=FontFamily(Typeface.createFromFile(path))
        return synchronized(fonts) {fonts.get(path) ?: created.also {fonts.put(path,it)}}
    }
    fun peekPair(pack:ThemeVisuals?):Pair<FontFamily,FontFamily>? {
        val heading=pack?.asset("headingFont")?.let(::peek) ?: return null
        val body=pack.asset("bodyFont")?.let(::peek) ?: return null
        return heading to body
    }
}

@Composable
internal fun rememberPackFonts(pack:ThemeVisuals?):Pair<FontFamily,FontFamily>? {
    val heading=pack?.asset("headingFont")
    val body=pack?.asset("bodyFont")
    val cached=PackFontCache.peekPair(pack)
    if(cached != null) return cached
    if(heading == null || body == null) return null
    val loaded by produceState<Pair<Pair<String,String>,Pair<FontFamily,FontFamily>>?>(null,heading,body) {
        value=withContext(Dispatchers.IO) {
            runCatching {(heading to body) to (PackFontCache.load(heading) to PackFontCache.load(body))}.getOrNull()
        }
    }
    return cached ?: loaded?.takeIf {it.first == (heading to body)}?.second
}
