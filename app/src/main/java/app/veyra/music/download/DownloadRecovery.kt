package app.veyra.music.download

import app.veyra.music.data.model.Song
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Metadata only. Never persist a signed stream URL, token, or partial-file claim. */
@Serializable
internal data class DownloadRequest(
    val id: String, val title: String, val artist: String, val artwork: String?,
    val duration: String? = null, val albumId: String? = null, val album: String? = null,
    val video: Boolean = false, val videoOrigin: Boolean = video,
    val quality: String? = null, val hiRes: Boolean = false, val explicit: Boolean? = null, val from: String? = null,
) {
    fun song() = Song(id, title, artist, artwork, durationText = duration,
        albumId = albumId, albumName = album, isVideo = video, isVideoOrigin = videoOrigin,
        sourceQuality = quality, sourceHiRes = hiRes, isExplicit = explicit)
    companion object {
        fun of(song: Song, from: String?) = DownloadRequest(song.videoId, song.title, song.artist,
            song.thumbnailUrl, song.durationText, song.albumId, song.albumName, song.isVideo, song.isVideoOrigin, song.sourceQuality, song.sourceHiRes, song.isExplicit, from)
        private val json = Json { ignoreUnknownKeys = true }
        private val serializer = ListSerializer(DownloadRequest.serializer())
        fun encode(items: Collection<DownloadRequest>): String = json.encodeToString(serializer, items.toList())
        fun decode(raw: String): List<DownloadRequest> = runCatching {
            require(raw.length <= 2_000_000)
            json.decodeFromString(serializer, raw).distinctBy { it.id }
        }.getOrDefault(emptyList())
    }
}
