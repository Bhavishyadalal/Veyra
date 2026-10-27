package app.veyra.music

import app.veyra.music.data.sources.AddonSource
import app.veyra.music.data.sources.SourceConfig
import app.veyra.music.data.sources.SourceKind
import app.veyra.music.data.sources.SourceRegistry
import app.veyra.music.data.sources.withDiscoveredLabel
import org.junit.Assert.*
import org.junit.Test

class SourceProbeUpdateTest {
    private val probed = SourceConfig(id = "addon", kind = SourceKind.ADDON,
        label = "Old name", baseUrl = "https://example.test", enabled = true)

    @Test fun discoveredNamePreservesSwitchesChangedWhileTheProbeWasRunning() {
        val current = probed.copy(enabled = false, allowDownloads = false, checkValidLossless = true)
        val renamed = current.withDiscoveredLabel(probed, "New name")!!
        assertEquals("New name", renamed.label)
        assertFalse(renamed.enabled)
        assertFalse(renamed.allowDownloads)
        assertTrue(renamed.checkValidLossless)
    }

    @Test fun replacedServerAndNewUserLabelRejectAnOlderProbe() {
        assertNull(probed.copy(baseUrl = "https://new.test").withDiscoveredLabel(probed, "Stale name"))
        assertNull(probed.copy(label = "My label").withDiscoveredLabel(probed, "Stale name"))
        assertNull(probed.withDiscoveredLabel(probed, " "))
    }

    @Test fun cosmeticRenameRetainsTheWarmClientButAnEndpointChangeReplacesIt() {
        SourceRegistry.add(probed)
        try {
            val original = SourceRegistry.instance(probed.id)!!
            SourceRegistry.update(probed.copy(label = "Discovered name"))
            assertSame(original, SourceRegistry.instance(probed.id))
            assertEquals("Discovered name", original.displayName)
            SourceRegistry.update(probed.copy(baseUrl = "https://new.test"))
            assertNotSame(original, SourceRegistry.instance(probed.id))
        } finally { SourceRegistry.remove(probed.id) }
    }

    @Test fun candidateAndReplacedClientsCannotChangeSavedPolicy() {
        SourceRegistry.add(probed)
        val candidate = AddonSource(probed.copy(baseUrl = "https://candidate.test"))
        try {
            val old = SourceRegistry.instance(probed.id) as AddonSource
            SourceRegistry.updatePolicy(candidate, false, true)
            assertTrue(SourceRegistry.config(probed.id)!!.allowDownloads)
            SourceRegistry.update(probed.copy(baseUrl = "https://replacement.test"))
            SourceRegistry.updatePolicy(old, false, true)
            assertTrue(SourceRegistry.config(probed.id)!!.allowDownloads)
            val current = SourceRegistry.instance(probed.id) as AddonSource
            SourceRegistry.updatePolicy(current, false, true)
            assertFalse(SourceRegistry.config(probed.id)!!.allowDownloads)
            assertTrue(SourceRegistry.config(probed.id)!!.checkValidLossless)
            assertSame(current, SourceRegistry.instance(probed.id))
        } finally { candidate.release(); SourceRegistry.remove(probed.id) }
    }

    @Test fun parallelEditsKeepEverySourceAndItsClient() {
        val pool = java.util.concurrent.Executors.newFixedThreadPool(8)
        val start = java.util.concurrent.CountDownLatch(1)
        val sources = (1..32).map { probed.copy(id = "parallel-$it") }
        try {
            val tasks = sources.map { config -> pool.submit {
                start.await()
                SourceRegistry.add(config)
                SourceRegistry.setEnabled(config.id, false)
                SourceRegistry.updateDiscoveredLabel(config, "Discovered")
                val source = SourceRegistry.instance(config.id) as AddonSource
                SourceRegistry.updatePolicy(source, false, false)
            } }
            start.countDown()
            tasks.forEach { it.get(10, java.util.concurrent.TimeUnit.SECONDS) }
            sources.forEach { source ->
                val saved = SourceRegistry.config(source.id)!!
                assertFalse(saved.enabled)
                assertFalse(saved.allowDownloads)
                assertEquals("Discovered", saved.label)
                assertNotNull(SourceRegistry.instance(source.id))
            }
        } finally {
            pool.shutdownNow()
            pool.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)
            sources.forEach { SourceRegistry.remove(it.id) }
        }
    }
}
