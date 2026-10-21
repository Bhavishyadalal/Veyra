package app.veyra.music

import app.veyra.music.data.settings.*
import org.junit.Assert.*
import org.junit.Test

class ExperiencePolicyTest {
    @Test fun defaultsPreserveExistingPlayerAndNetworkPolicy() {
        val options=ExperienceOptions.decode(null)
        assertFalse(options.immersive)
        assertFalse(options.dataSaver)
        assertEquals(0,options.doubleTapSeekSeconds)
        assertEquals(0,options.holdSeekSeconds)
    }
    @Test fun appearanceAndControlsRoundTripWithoutLosingThemeBackground() {
        val options=ExperienceOptions(immersive=true,immersiveBackground=ImmersiveBackground.THEME,
            immersiveLyrics=ImmersiveLyricsPosition.FULL,dataSaver=true,doubleTapSeekSeconds=15)
        assertEquals(options,ExperienceOptions.decode(options.encode()))
    }
    @Test fun malformedAndFuturePreferencesRemainUsable() {
        assertEquals(ExperienceOptions(),ExperienceOptions.decode("broken"))
        val future=ExperienceOptions.decode("""{"immersiveBackground":"HOLOGRAM","unknownFutureSetting":true,"holdSeekSeconds":999,"doubleTapSeekSeconds":-1}""")
        assertEquals(ImmersiveBackground.SONG,future.immersiveBackground)
        assertEquals(20,future.holdSeekSeconds)
        assertEquals(0,future.doubleTapSeekSeconds)
    }
    @Test fun navigationRejectsFutureIdsAndKeepsHomeReachable() {
        assertEquals(listOf(0,1),normalizedNavigation(emptyList()))
        assertEquals(listOf(0,4,5,6,1),normalizedNavigation(listOf(4,4,99,5,6,1,2)))
        assertEquals(listOf(3,0,1),normalizedNavigation(listOf(3,0,1)))
    }
    @Test fun importedFontReferencesCannotBecomeFilePaths() {
        val options=ExperienceOptions(appFont="../../private",lyricsFont="file:///secrets",lyricsScale=Float.NaN).normalized()
        assertEquals("",options.appFont)
        assertEquals("",options.lyricsFont)
        assertEquals(1f,options.lyricsScale,0f)
    }
    @Test fun invalidCountriesAndUnsafeSavedValuesNormalizeBeforeUse() {
        assertEquals("IN",ExperienceOptions(contentCountry="in").normalized().contentCountry)
        assertEquals("",ExperienceOptions(contentCountry="../../US").normalized().contentCountry)
        val options=ExperienceOptions(homeGreeting="Hello ",homeShortcuts=listOf(7,99,7,1),startupTab=999).normalized()
        assertEquals("Hello ",options.homeGreeting)
        assertEquals(listOf(7,1),options.homeShortcuts)
        assertEquals(0,options.startupTab)
    }
    @Test fun seekingClampsToFiniteTrackAndDoesNotOverflow() {
        assertEquals(0L,boundedSeek(2000,-10000,100000))
        assertEquals(100000L,boundedSeek(99000,10000,100000))
        assertEquals(Long.MAX_VALUE,boundedSeek(Long.MAX_VALUE-2,10000,0))
        assertEquals(5000L,boundedSeek(0,5000,-1))
    }
    @Test fun dataSaverUsesBoundedDimensionsOnKnownImageHosts() {
        assertEquals("https://lh3.googleusercontent.com/art=w96-h96-l70-rj",dataSaverArtworkUrl("https://lh3.googleusercontent.com/art=w1200-h1200",1))
        assertEquals("https://lh3.googleusercontent.com/art=w400-h400-l70-rj",dataSaverArtworkUrl("https://lh3.googleusercontent.com/art",2000))
        assertEquals("https://i.ytimg.com/vi/track/mqdefault.jpg",dataSaverArtworkUrl("https://i.ytimg.com/vi/track/maxresdefault.jpg",400))
    }
    @Test fun dataSaverDoesNotModifySignedPrivateOrLookalikeUrls() {
        listOf("content://music/art","/data/art.webp","https://private.example/art.jpg",
            "https://googleusercontent.com.evil.example/art", "https://example.org/googleusercontent.com/art",
            "https://lh3.googleusercontent.com/art?signature=keep", "http://i.ytimg.com/vi/id/maxresdefault.jpg").forEach {
            assertEquals(it,dataSaverArtworkUrl(it,128))
        }
    }
}
