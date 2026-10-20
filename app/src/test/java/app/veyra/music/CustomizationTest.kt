package app.veyra.music

import app.veyra.music.ui.customization.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomizationTest {
    @Test fun invalidOrFutureBackupValuesKeepSafeDefaults() {
        assertEquals(AccentPalette.DEFAULT, savedChoice("FUTURE_PALETTE", AccentPalette.DEFAULT))
        assertEquals(ArtworkCorners.DEFAULT, savedChoice(null, ArtworkCorners.DEFAULT))
        assertEquals(PlayerShortcut.NONE, savedChoice("", PlayerShortcut.NONE))
    }
    @Test fun savedChoicesRoundTripWithoutDependingOnTranslatedLabels() {
        for (value in AccentPalette.entries) assertEquals(value, savedChoice(value.name, AccentPalette.DEFAULT))
        for (value in ArtworkCorners.entries) assertEquals(value, savedChoice(value.name, ArtworkCorners.DEFAULT))
        for (value in ListSpacing.entries) assertEquals(value, savedChoice(value.name, ListSpacing.COMFORTABLE))
        for (value in PlayerShortcut.entries) assertEquals(value, savedChoice(value.name, PlayerShortcut.NONE))
    }
    @Test fun compactModeCannotReducePaddingAtLargeTextScales() {
        assertEquals(0, songRowPadding(ListSpacing.COMPACT, 1f))
        assertEquals(4, songRowPadding(ListSpacing.COMPACT, 1.3f))
        assertEquals(4, songRowPadding(ListSpacing.COMPACT, 2f))
        assertEquals(4, songRowPadding(ListSpacing.COMFORTABLE, 1f))
        assertEquals(4, songRowPadding(ListSpacing.COMPACT, Float.NaN))
    }
}
