package app.veyra.music.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import coil3.PlatformContext
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.io.File

/** Small immutable local glyphs share the same request in prefetch, rows and player controls. */
fun themeGlyphRequest(context: PlatformContext, path: String, pixels: Int = 128): ImageRequest {
    require(pixels == 128 || pixels == 256)
    return ImageRequest.Builder(context).data(File(path)).size(pixels)
        .memoryCacheKey(themeGlyphCacheKey(path,pixels))
        .memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.DISABLED)
        .crossfade(false).build()
}

@Composable
fun rememberThemeGlyphPainter(path: String, fallback: Painter, pixels: Int = 128): AsyncImagePainter {
    val context=LocalPlatformContext.current
    val request=remember(context,path,pixels) {themeGlyphRequest(context,path,pixels)}
    return rememberAsyncImagePainter(request,placeholder=fallback,error=fallback)
}
