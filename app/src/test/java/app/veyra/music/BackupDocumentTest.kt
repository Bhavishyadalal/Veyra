package app.veyra.music

import app.veyra.music.data.stats.Backup
import org.junit.Assert.*
import org.junit.Test

class BackupDocumentTest {
    @Test fun oldBackupsWithoutFontAssetsRemainReadable() {
        val file=Backup.decode("""{"app":"veyra","version":1,"versionName":"0.1.7","settings":{},"listening":[]}""")
        assertEquals("0.1.7",file.versionName)
        assertTrue(file.assets.isEmpty())
    }
    @Test fun credentialAndArbitraryPathAssetsAreRejectedBeforeRestore() {
        for(key in listOf("../auth","font/../../cookies","secret","artwork")) {
            assertThrows(IllegalArgumentException::class.java) {Backup.decode("""{"app":"veyra","version":2,"settings":{},"listening":[],"assets":{"$key":"AAAA"}}""")}
        }
    }
    @Test fun wrongAppFutureAndInvalidSchemasAreRejected() {
        for(text in listOf("""{"app":"another-app","version":2,"settings":{},"listening":[]}""","""{"app":"veyra","version":999,"settings":{},"listening":[]}""","""{"app":"veyra","version":-1,"settings":{},"listening":[]}""")) {
            assertThrows(IllegalArgumentException::class.java) {Backup.decode(text)}
        }
    }
    @Test fun unrelatedAndIncompleteJsonCannotEraseHistory() {
        for(text in listOf("{}","[]","""{"app":"veyra"}""","""{"app":"veyra","version":2}""")) {
            assertThrows(IllegalArgumentException::class.java) {Backup.decode(text)}
        }
    }
    @Test fun newDocumentKeepsIndependentFontSlots() {
        val file=Backup.decode("""{"app":"veyra","version":2,"settings":{},"listening":[],"assets":{"font/app":"AAAA","font/lyrics":"BBBB"}}""")
        assertEquals(setOf("font/app","font/lyrics"),file.assets.keys)
    }
}
