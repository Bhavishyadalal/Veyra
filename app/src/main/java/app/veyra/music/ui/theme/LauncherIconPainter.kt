package app.veyra.music.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import app.veyra.music.AppIcon
import app.veyra.music.BuildConfig
import app.veyra.music.R
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

/** Home and picker share bounded native-resource decoding; no bitmap decoding in composition. */
@Composable
internal fun rememberLauncherIconPainter(icon:AppIcon):Painter {
    val context=LocalContext.current
    val fallback=painterResource(R.drawable.ic_wave_monochrome)
    val request=remember(context,icon) {
        val key="launcher/${BuildConfig.VERSION_CODE}/${icon.alias}/256"
        ImageRequest.Builder(context).data(icon.previewRes ?: icon.iconRes).size(256)
            .memoryCacheKey(key).placeholderMemoryCacheKey(key)
            .memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.DISABLED)
            .crossfade(false).build()
    }
    return rememberAsyncImagePainter(request,placeholder=fallback,error=fallback)
}
