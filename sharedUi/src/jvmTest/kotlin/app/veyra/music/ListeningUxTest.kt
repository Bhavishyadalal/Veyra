package app.veyra.music

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import app.veyra.music.data.model.*
import app.veyra.music.data.model.Song as ModelSong
import app.veyra.music.ui.player.PlayerPlatform
import app.veyra.music.ui.screens.HomeExperienceHeader
import app.veyra.music.ui.screens.SearchScreen
import org.junit.Assert.*
import org.junit.Test
import javax.swing.SwingUtilities

@OptIn(ExperimentalComposeUiApi::class)
class ListeningUxTest {
    private fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }
    private fun ImageComposeScene.nodes() = semanticsOwners.flatMap { it.rootSemanticsNode.all() }
    private fun List<SemanticsNode>.label(text: String) = first {
        it.config.getOrNull(SemanticsProperties.Text)?.any { value -> value.text == text } == true
    }
    private fun render(large: Boolean = false, content: @Composable () -> Unit, check: (ImageComposeScene) -> Unit) {
        var scene: ImageComposeScene? = null
        PlayerPlatform.install(RenderPlayerHost())
        try {
            SwingUtilities.invokeAndWait {
                scene = ImageComposeScene(if (large) 320 else 360, 920, density = Density(1f, if (large) 2f else 1f)) {
                    MaterialTheme(colorScheme = darkColorScheme()) { Surface { content() } }
                }
            }
            repeat(8) { index ->
                Thread.sleep(15)
                SwingUtilities.invokeAndWait { scene!!.render(index * 50_000_000L).close() }
            }
            SwingUtilities.invokeAndWait { check(scene!!) }
        } finally { SwingUtilities.invokeAndWait { scene?.close() } }
    }
    @Composable private fun Search(
        query: String = "Evening", rows: UiState<List<SearchResult>>? = null,
        suggestions: List<String> = emptyList(), typeahead: List<SearchResult> = emptyList(),
        submit: () -> Unit = {}, play: (ModelSong) -> Unit = {}, playlist: (ModelSong) -> Unit = {},
    ) {
        SearchScreen(query = query, onQueryChange = {}, filter = SearchFilter.ALL,
            currentSong = null, isPlaying = false, onFilterChange = {}, results = rows,
            loadingMore = false, onLoadMore = {}, listState = rememberLazyListState(),
            scrollResetTrigger = 0, focusRequested = false, onFocusHandled = {},
            onSongClick = { _, _ -> }, onSongLongPress = {}, onSongSwipe = {},
            onTopResultPlay = play, onTopResultPlaylist = playlist, onBrowseClick = {},
            history = emptyList(), suggestions = suggestions, typeaheadResults = typeahead,
            onSubmit = submit, onSuggestionClick = { submit() }, onHistoryClick = {},
            onHistoryRemove = {}, onHistoryClear = {}, contentPadding = PaddingValues(0.dp),
            topPadding = 0.dp, showField = false)
    }

    @Test fun draftHasAnActionEvenWhenCompletionsAreUnavailable() {
        var submitted = 0
        render(content = { Search(submit = { submitted++ }) }) { scene ->
            assertNotNull(scene.nodes().label("Ready to search for “Evening”"))
            val action = scene.nodes().label("Search")
            assertEquals(true, action.config[SemanticsActions.OnClick].action!!.invoke())
            assertEquals(1, submitted)
        }
    }

    @Test fun mediaResultsDoNotDependOnTextCompletionsAndDuplicateRowsAreSafe() {
        var plays = 0
        var submitted = 0
        val row = SearchResult.Track(ModelSong("draft-song", "A quiet evening", "Veyra", null))
        render(content = { Search(typeahead = listOf(row, row), play = { plays++ }, submit = { submitted++ }) }) { scene ->
            val nodes = scene.nodes()
            val song = nodes.label("A quiet evening")
            assertEquals(true, song.config[SemanticsActions.OnClick].action!!.invoke())
            assertEquals(1, plays)
            val action = nodes.label("Search \"Evening\"")
            assertEquals(true, action.config[SemanticsActions.OnClick].action!!.invoke())
            assertEquals(1, submitted)
        }
    }

    @Test fun savedHomeRecoveryHasRetryButNeverRetriesOfflineOrDuringAnUpdate() {
        for ((offline, updating) in listOf(false to false, false to true, true to false)) {
            var retries = 0
            render(content = {
                HomeExperienceHeader(offline, true, updating, "Connection interrupted", emptyList(),
                    null, null, null, reducedMotion = true, onRetry = { retries++ })
            }) { scene ->
                val retry = scene.nodes().filter {
                    it.config.getOrNull(SemanticsProperties.Text)?.any { value -> value.text == "Retry" } == true
                }
                if (offline) assertTrue(retry.isEmpty())
                else {
                    assertEquals(1, retry.size)
                    val node = retry.single()
                    assertEquals(updating, node.config.contains(SemanticsProperties.Disabled))
                    if (!updating) {
                        node.config[SemanticsActions.OnClick].action!!.invoke()
                        assertEquals(1, retries)
                    }
                }
            }
        }
    }

    @Test fun largeTextTopResultActionsRemainWithinTheScreenAndWork() {
        var playlist = 0
        val song = ModelSong("top", "Evening music for your next journey", "Veyra", null)
        render(large = true, content = {
            Search(rows = UiState.Success(listOf(SearchResult.TopTrack(song))), playlist = { playlist++ })
        }) { scene ->
            val nodes = scene.nodes()
            val play = nodes.label("Play")
            val add = nodes.label("Playlist")
            for (node in listOf(play, add)) {
                assertTrue(node.boundsInRoot.left >= 0)
                assertTrue(node.boundsInRoot.right <= 320)
                assertTrue(node.boundsInRoot.bottom <= 920)
            }
            assertTrue("Actions wrap instead of squeezing or clipping", add.boundsInRoot.top >= play.boundsInRoot.bottom)
            scene.render(600_000_000L).use { image ->
                java.io.File("/tmp/veyra53-large-search.png").writeBytes(image.encodeToData()!!.bytes)
            }
            add.config[SemanticsActions.OnClick].action!!.invoke()
            assertEquals(1, playlist)
        }
    }
}
