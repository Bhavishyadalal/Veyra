package app.veyra.music

import app.veyra.music.data.themes.AppearanceFont
import app.veyra.music.ui.themes.themePreviewCacheKey
import app.veyra.music.ui.themes.ThemeVisuals
import app.veyra.music.ui.themes.themeSceneScrimAlpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.*
import org.junit.Test

class AppearancePolicyTest {
    @Test fun newOrUnknownPreferencesFollowTheThemeAndSafelyFallBackWithoutAPack() {
        for(name in listOf(null,"", "removed-font", "fredoka")) {
            val choice=AppearanceFont.fromStoredName(name)
            assertEquals(AppearanceFont.FOLLOW_THEME,choice)
            assertEquals(AppearanceFont.VEYRA_DEFAULT,choice.resolved(false))
            assertEquals(AppearanceFont.FOLLOW_THEME,choice.resolved(true))
        }
    }
    @Test fun manualFontAndDefaultChoicesSurviveThemeChangesAndPreferenceRoundTrips() {
        AppearanceFont.entries.filter {it!=AppearanceFont.FOLLOW_THEME}.forEach { choice ->
            val restored=AppearanceFont.fromStoredName(choice.name)
            assertEquals(choice,restored.resolved(true))
            assertEquals(choice,restored.resolved(false))
        }
    }
    @Test fun themePreviewIdentityIsStableOnReopeningButSeparatesRevisionsAndDecodeSizes() {
        val thumbnail=themePreviewCacheKey("matcha-mischief",1,false)
        assertEquals(thumbnail,themePreviewCacheKey("matcha-mischief",1,false))
        assertNotEquals(thumbnail,themePreviewCacheKey("matcha-mischief",2,false))
        assertNotEquals(thumbnail,themePreviewCacheKey("matcha-mischief",1,true))
        assertNotEquals(thumbnail,themePreviewCacheKey("clover-requiem",1,false))
    }
    @Test fun prominentBackdropProtectsMutedTextEvenOverWhiteArtwork() {
        val dark=Color(0xFF11251C)
        val muted=Color(0xFFD7E2C9)
        val theme=ThemeVisuals("test","Test",Color.White,dark,dark,dark,16,16,24,emptyMap(),
            background=dark,text=Color.White,muted=muted)
        val alpha=themeSceneScrimAlpha(theme)
        val amount=.70f*(1f-alpha)
        val background=Color(dark.red*(1f-amount)+amount,dark.green*(1f-amount)+amount,
            dark.blue*(1f-amount)+amount).luminance()
        assertTrue(alpha in .50f..1f)
        val contrast=(muted.luminance()+.05f)/(background+.05f)
        assertTrue("alpha=$alpha, contrast=$contrast",contrast>=4.5f)
    }
}
