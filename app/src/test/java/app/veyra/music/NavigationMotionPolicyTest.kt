package app.veyra.music

import app.veyra.music.ui.NavigationDestination
import app.veyra.music.ui.NavigationMotion
import app.veyra.music.ui.NavigationMotionPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationMotionPolicyTest {
    private fun route(key: String, depth: Int) = NavigationDestination(key, depth)

    @Test fun detailPushAndPopReverseEvenWithOpaqueBrowseIds() {
        val home = route("tab:0", 0)
        val album = route("MPREb_album", 1)
        val artist = route("UC_artist", 2)
        for ((parent, child) in listOf(home to album, album to artist)) {
            assertEquals(NavigationMotion.Forward, NavigationMotionPolicy.between(parent, child))
            assertEquals(NavigationMotion.Back, NavigationMotionPolicy.between(child, parent))
        }
    }

    @Test fun tabChangesUseRestrainedMotionInBothDirections() {
        for (from in 0..3) for (to in 0..3) {
            assertEquals(if (from == to) NavigationMotion.None else NavigationMotion.Tab,
                NavigationMotionPolicy.between(route("tab:$from", 0), route("tab:$to", 0)))
        }
    }

    @Test fun settingsAndNestedContextsReverseToTheirRetainedParent() {
        val detail = route("album", 1)
        val settings = route("settings", 2)
        val account = route("account_scrobbling", 3)
        assertEquals(NavigationMotion.Forward, NavigationMotionPolicy.between(detail, settings))
        assertEquals(NavigationMotion.Back, NavigationMotionPolicy.between(settings, detail))
        assertEquals(NavigationMotion.Forward, NavigationMotionPolicy.between(settings, account))
        assertEquals(NavigationMotion.Back, NavigationMotionPolicy.between(account, settings))
    }

    @Test fun retainedPageDoesNotAnimateAgainWhenItsContextDepthChanges() {
        assertEquals(NavigationMotion.None,
            NavigationMotionPolicy.between(route("settings", 1), route("settings", 2)))
    }
}
