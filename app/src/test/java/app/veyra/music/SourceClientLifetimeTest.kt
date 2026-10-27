package app.veyra.music

import app.veyra.music.data.sources.SourceConfig
import app.veyra.music.data.sources.SourceKind
import app.veyra.music.data.sources.SourceRegistry
import app.veyra.music.data.sources.addon.AddonClient
import kotlinx.coroutines.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class SourceClientLifetimeTest {
    @Test fun testingAnotherEndpointDoesNotChangeTheSavedServerPolicy() = runBlocking {
        val server = MockWebServer()
        server.start()
        val saved = SourceConfig(id = "candidate-policy", kind = SourceKind.ADDON,
            baseUrl = "https://saved.example.test", allowDownloads = true)
        SourceRegistry.add(saved)
        try {
            server.enqueue(MockResponse().setBody("""{"id":"test","name":"Test","resources":["search","stream"],"allowDownloads":false}"""))
            assertTrue(SourceRegistry.probeCandidate(saved.copy(baseUrl = server.url("/").toString())).isOk)
            assertEquals(saved.baseUrl, SourceRegistry.config(saved.id)!!.baseUrl)
            assertTrue(SourceRegistry.config(saved.id)!!.allowDownloads)
        } finally { SourceRegistry.remove(saved.id); server.shutdown() }
    }

    @Test fun closingTheOwnerCancelsAnOutstandingHttpRequest() = runBlocking {
        val server = MockWebServer()
        server.start()
        val client = AddonClient(server.url("/").toString())
        try {
            server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
            val result = async(Dispatchers.IO) { runCatching { client.manifest() } }
            assertNotNull(server.takeRequest(3, TimeUnit.SECONDS))
            client.close()
            val answer = withTimeout(3000) { result.await() }
            assertTrue(answer.isFailure || answer.getOrThrow().isFailure)
        } finally { client.close(); server.shutdown() }
    }
}
