package app.veyra.music

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.semantics.*
import androidx.compose.ui.semantics.getOrNull
import app.veyra.music.data.settings.ExperienceOptions
import app.veyra.music.data.settings.ImmersiveStyle
import app.veyra.music.ui.player.ImmersiveMenuContent
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class ImmersiveMenuLazyTest {
    @Test fun openingMenuDoesNotComposeTheEntirePosterGallery() {
        var scene: ImageComposeScene? = null
        fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(390, 540) {
                    MaterialTheme {
                        ImmersiveMenuContent(ExperienceOptions(), null, {}, {}, {}, {}, {}, previewsReady=false)
                    }
                }
                repeat(4) { scene!!.render(it * 40_000_000L).close() }
                val nodes = scene!!.semanticsOwners.flatMap { it.rootSemanticsNode.all() }
                val posters = nodes.filter { it.config.getOrNull(SemanticsProperties.Role) == Role.RadioButton }
                assertTrue("visible posters must exist", posters.isNotEmpty())
                assertTrue("opening must not mount all 12 scenes", posters.size < ImmersiveStyle.entries.size)
                assertNotNull(nodes.firstOrNull { it.config.getOrNull(SemanticsProperties.VerticalScrollAxisRange) != null })
            }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
}
