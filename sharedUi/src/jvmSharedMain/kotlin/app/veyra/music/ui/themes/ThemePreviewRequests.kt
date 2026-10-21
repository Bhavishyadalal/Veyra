package app.veyra.music.ui.themes

import coil3.PlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

/** Stable across Studio reopenings and catalog refreshes; revisions never share pixels. */
fun themePreviewCacheKey(id: String, version: Int, detail: Boolean): String =
    "theme-preview/$id/v$version/${if(detail) "detail" else "thumbnail"}"

fun themePreviewRequest(context: PlatformContext, id: String, version: Int, model: Any, detail: Boolean = false): ImageRequest {
    val key=themePreviewCacheKey(id,version,detail)
    // A catalog revision can reuse a public preview path; invalidate HTTP caches too.
    val source=if(model is String && model.startsWith("https://"))
        model + (if('?' in model) "&" else "?") + "themeVersion=$version" else model
    return ImageRequest.Builder(context).data(source)
        .size(if(detail)1024 else 384)
        .memoryCacheKey(key).placeholderMemoryCacheKey(key)
        .diskCacheKey("theme-preview/$id/v$version")
        .memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.ENABLED)
        .crossfade(false).build()
}
