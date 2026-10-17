package app.veyra.music

import app.veyra.music.ui.components.navigationDragPosition
import app.veyra.music.ui.components.navigationIndicatorOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class BottomNavigationGeometryTest {
    @Test fun dragMovesThroughReadingOrder() {
        assertEquals(2f, navigationDragPosition(1, 100f, 100f, 4, false), 0f)
        assertEquals(0f, navigationDragPosition(1, 100f, 100f, 4, true), 0f)
    }
    @Test fun swipeCannotLeaveTheTabRange() {
        assertEquals(3f, navigationDragPosition(1, 900f, 100f, 4, false), 0f)
        assertEquals(0f, navigationDragPosition(1, -900f, 100f, 4, false), 0f)
        assertEquals(3f, navigationDragPosition(1, -900f, 100f, 4, true), 0f)
    }
    @Test fun missingMeasurementDoesNotSelectAnotherTab() {
        for (step in listOf(0f, -1f, Float.NaN, Float.POSITIVE_INFINITY)) {
            assertEquals(2f, navigationDragPosition(2, 100f, step, 4, false), 0f)
        }
        assertEquals(2f, navigationDragPosition(2, Float.NaN, 100f, 4, false), 0f)
    }
    @Test fun indicatorTracksThePhysicalTabInBothDirections() {
        assertEquals(0f, navigationIndicatorOffset(0, 4, 100f, false), 0f)
        assertEquals(300f, navigationIndicatorOffset(0, 4, 100f, true), 0f)
        assertEquals(100f, navigationIndicatorOffset(2, 4, 100f, true), 0f)
    }
    @Test fun emptyOrInvalidSelectionHasSafeGeometry() {
        assertEquals(0f, navigationIndicatorOffset(2, 0, 100f, false), 0f)
        assertEquals(0f, navigationDragPosition(2, 100f, 0f, 0, true), 0f)
        assertEquals(300f, navigationIndicatorOffset(99, 4, 100f, false), 0f)
    }
}
