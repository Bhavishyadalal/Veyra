package app.veyra.music

import app.veyra.music.data.sources.SourceEnablementPolicy
import app.veyra.music.data.sources.SourceKind
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceEnablementPolicyTest {
    @Test fun legacyForcedOffIsCorrectedThenManualOffSurvivesRestarts() {
        val corrected = SourceEnablementPolicy.onStartup(SourceKind.JIOSAAVN, false, true)
        assertTrue(corrected)
        assertTrue(SourceEnablementPolicy.onStartup(SourceKind.JIOSAAVN, corrected, false))
        var switchedOff = false
        repeat(5) { switchedOff = SourceEnablementPolicy.onStartup(SourceKind.JIOSAAVN, switchedOff, false) }
        assertFalse(switchedOff)
    }

    @Test fun userAddedSourcesAreNeverEnabledByTheBuiltinMigration() {
        for (kind in listOf(SourceKind.ADDON, SourceKind.CUSTOM_MODULE)) {
            for (migrate in listOf(false, true)) {
                assertFalse(SourceEnablementPolicy.onStartup(kind, false, migrate))
                assertTrue(SourceEnablementPolicy.onStartup(kind, true, migrate))
            }
        }
    }

    @Test fun youtubeRemainsAvailableForFallbackWithOrWithoutMigration() {
        for (migrate in listOf(false, true)) for (stored in listOf(false, true)) {
            assertTrue(SourceEnablementPolicy.onStartup(SourceKind.YOUTUBE, stored, migrate))
        }
    }

    @Test fun anEnabledJioSaavnStaysEnabledBeforeAndAfterTheCorrection() {
        assertTrue(SourceEnablementPolicy.onStartup(SourceKind.JIOSAAVN, true, true))
        assertTrue(SourceEnablementPolicy.onStartup(SourceKind.JIOSAAVN, true, false))
    }
}
