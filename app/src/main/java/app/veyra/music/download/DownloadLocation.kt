package app.veyra.music.download

import java.net.URI

internal enum class DownloadLocation { PRIVATE, PUBLIC, UNKNOWN }

/** Classify the saved destination, independent of today's export preference. */
internal fun downloadLocation(uri: String?, privateRoot: String): DownloadLocation = runCatching {
    val parsed = URI(uri ?: return DownloadLocation.UNKNOWN)
    when (parsed.scheme) {
        "content" -> DownloadLocation.PUBLIC
        "file" -> {
            val path = parsed.path ?: return DownloadLocation.UNKNOWN
            val root = privateRoot.trimEnd('/')
            if (path == root || path.startsWith("$root/")) DownloadLocation.PRIVATE else DownloadLocation.PUBLIC
        }
        else -> DownloadLocation.UNKNOWN
    }
}.getOrDefault(DownloadLocation.UNKNOWN)
