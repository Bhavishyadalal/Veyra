package app.veyra.music.data

import app.veyra.music.data.settings.ExperienceOptions
import app.veyra.music.data.settings.ImmersiveStyle
import kotlinx.serialization.json.Json
import kotlin.test.*

class ImmersiveStyleTest {
    @Test fun allTwelveModesPersistWithoutChangingPlaybackOrThemePreferences() {
        assertEquals(12, ImmersiveStyle.entries.size)
        assertEquals(listOf("CLASSIC", "ARTWORK_CINEMA", "THEME_WORLD", "LYRICS_FOCUS", "MINIMAL_NIGHT", "LANDSCAPE_STAGE"),
            ImmersiveStyle.entries.take(6).map { it.name })
        val original = ExperienceOptions(immersive = true, dataSaver = true, doubleTapSeekSeconds = 15)
        for (style in ImmersiveStyle.entries) {
            val options = original.copy(immersiveStyle = style)
            assertEquals(options, ExperienceOptions.decode(Json.encodeToString(ExperienceOptions.serializer(), options)))
        }
    }

    @Test fun unknownFutureModeAndOlderSettingsKeepClassicAvailable() {
        assertEquals(ImmersiveStyle.CLASSIC, ExperienceOptions.decode("""{"immersive":true}""").immersiveStyle)
        assertEquals(ImmersiveStyle.CLASSIC, ExperienceOptions.decode("""{"immersiveStyle":"FUTURE_MODE"}""").immersiveStyle)
    }
}
