package app.veyra.music

import app.veyra.music.data.backend.VeyraBackend
import org.junit.Assert.*
import org.junit.Test

class BackendMigrationTest {
    @Test fun retiredManagedServicesAreRecognizedAcrossSavedUrlShapes() {
        for (host in listOf("bitchord-listen-together.onrender.com", "api.bitchord.kushagrasingh.in", "bitchord.kushagrasingh.in")) {
            for (url in listOf(host, "https://$host/", "HTTP://${host.uppercase()}:80/api", "https://$host./")) {
                assertTrue(url, VeyraBackend.isRetired(url))
                assertEquals("", VeyraBackend.migrateSavedServer(url))
            }
        }
    }
    @Test fun deliberateCustomServersArePreserved() {
        for (url in listOf("https://rooms.example.com/path", "http://192.168.1.4:8000", VeyraBackend.ORIGIN)) {
            assertFalse(VeyraBackend.isRetired(url))
            assertEquals(url, VeyraBackend.migrateSavedServer(url))
            assertFalse(VeyraBackend.discardSavedMembership(url, false))
        }
    }
    @Test fun hostMatchingDoesNotRetireSimilarNamesOrUrlUsernames() {
        assertFalse(VeyraBackend.isRetired("https://bitchord.kushagrasingh.in.example.com"))
        assertFalse(VeyraBackend.isRetired("https://bitchord.kushagrasingh.in@rooms.example.com"))
        assertFalse(VeyraBackend.isRetired("https://rooms.example.com/bitchord.kushagrasingh.in"))
    }
    @Test fun firstUpgradeDiscardsDefaultServiceTokensWithoutSendingThemToNewServer() {
        assertTrue(VeyraBackend.discardSavedMembership(null, false))
        assertTrue(VeyraBackend.discardSavedMembership("", false))
        assertTrue(VeyraBackend.discardSavedMembership("https://api.bitchord.kushagrasingh.in", true))
    }
    @Test fun migrationIsIdempotentAndDoesNotDiscardNewServiceMembership() {
        val migrated = VeyraBackend.migrateSavedServer("https://bitchord-listen-together.onrender.com")
        assertEquals(migrated, VeyraBackend.migrateSavedServer(migrated))
        assertFalse(VeyraBackend.discardSavedMembership(migrated, true))
        assertFalse(VeyraBackend.discardSavedMembership(VeyraBackend.ORIGIN, true))
    }
    @Test fun presenceBelongsToOfficialService() {
        assertEquals("https://veyra-listen-together.onrender.com/api/presence", VeyraBackend.PRESENCE_ENDPOINT)
    }
    @Test fun retiredRequestIsRejectedBeforeAnyNetworkOperation() {
        try {
            VeyraBackend.requireActive("https://api.bitchord.kushagrasingh.in")
            fail("Retired request must be rejected")
        } catch (e: IllegalArgumentException) {
            assertEquals(VeyraBackend.EXPIRED_INVITE_MESSAGE, e.message)
        }
        VeyraBackend.requireActive(VeyraBackend.ORIGIN)
    }
}
