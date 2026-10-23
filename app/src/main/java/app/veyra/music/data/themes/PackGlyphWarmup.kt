package app.veyra.music.data.themes

import android.content.Context
import app.veyra.music.ui.themes.themeGlyphRequest
import app.veyra.music.ui.themes.themeGlyphWarmupPlan
import coil3.SingletonImageLoader
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

private val packGlyphDecoders=Semaphore(2)

/** Warm only shared navigation/settings/player glyphs; never eagerly decode a whole pack. */
internal suspend fun warmPackGlyphs(context:Context,assets:Map<String,String>) = coroutineScope {
    val requests=themeGlyphWarmupPlan(assets)
    val loader=try {SingletonImageLoader.get(context)}
        catch(cancel:CancellationException) {throw cancel}
        catch(_:Exception) {return@coroutineScope}
    requests.map {(path,pixels)->async(Dispatchers.IO) {
        packGlyphDecoders.withPermit {
            try {loader.execute(themeGlyphRequest(context,path,pixels))}
            catch(cancel:CancellationException) {throw cancel}
            catch(_:Exception) {} // Native fallback remains usable; prefetch cannot fail activation.
        }
    }}.awaitAll()
    Unit
}
