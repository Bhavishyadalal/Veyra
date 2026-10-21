package app.veyra.music.download

import org.junit.Assert.assertEquals
import org.junit.Test

class DownloadLocationTest {
    @Test fun `private audio and offline packages remain private after export settings change`() {
        assertEquals(DownloadLocation.PRIVATE, downloadLocation("file:///data/app/files/downloads/song.flac", "/data/app/files"))
        assertEquals(DownloadLocation.PRIVATE, downloadLocation("file:///data/app/files/offline-hls/song/playlist.m3u8", "/data/app/files"))
    }
    @Test fun `public files and MediaStore records remain public`() {
        assertEquals(DownloadLocation.PUBLIC, downloadLocation("content://media/audio/8", "/data/app/files"))
        assertEquals(DownloadLocation.PUBLIC, downloadLocation("file:///storage/Music/Veyra/song.flac", "/data/app/files"))
        assertEquals(DownloadLocation.PUBLIC, downloadLocation("file:///data/app/files-other/song.flac", "/data/app/files"))
    }
    @Test fun `missing or invalid records do not invent a saved location`() {
        assertEquals(DownloadLocation.UNKNOWN, downloadLocation(null, "/data/app/files"))
        assertEquals(DownloadLocation.UNKNOWN, downloadLocation("not a uri", "/data/app/files"))
    }
}
