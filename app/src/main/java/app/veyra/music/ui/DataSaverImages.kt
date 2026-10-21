package app.veyra.music.ui

import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.settings.dataSaverArtworkUrl
import coil3.intercept.Interceptor
import coil3.request.ImageResult
import coil3.size.Dimension

/** Reads current policy per request; changing Data Saver does not recreate the image loader/cache. */
internal class DataSaverImages : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        if (!AppSettings.experience.value.dataSaver || AppSettings.meteredConnection.value == false) return chain.proceed()
        val url=chain.request.data as? String ?: return chain.proceed()
        val size=(chain.size.width as? Dimension.Pixels)?.px ?: 400
        val resized=dataSaverArtworkUrl(url,size)
        return if (resized==url) chain.proceed() else chain.withRequest(chain.request.newBuilder().data(resized).build()).proceed()
    }
}
