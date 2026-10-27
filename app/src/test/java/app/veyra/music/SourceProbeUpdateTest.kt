package app.veyra.music

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
}
