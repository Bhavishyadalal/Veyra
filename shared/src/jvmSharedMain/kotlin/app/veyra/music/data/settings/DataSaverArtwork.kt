package app.veyra.music.data.settings

import java.net.URI

/** Only resize known unsigned image URLs; local files, custom servers and signed URLs stay intact. */
fun dataSaverArtworkUrl(url: String, requestedPx: Int): String {
    val uri = runCatching { URI(url) }.getOrNull() ?: return url
    if (uri.scheme != "https" || uri.rawQuery != null || uri.rawFragment != null) return url
    val host = uri.host?.lowercase() ?: return url
    fun isHost(domain:String) = host == domain || host.endsWith(".$domain")
    val px = requestedPx.coerceIn(96, 400)
    return when {
        isHost("googleusercontent.com") || isHost("ggpht.com") -> url.substringBefore('=') + "=w$px-h$px-l70-rj"
        isHost("ytimg.com") -> url.replace(Regex("/(maxresdefault|sddefault|hqdefault|hq720)\\.jpg$"), "/mqdefault.jpg")
        else -> url
    }
}
