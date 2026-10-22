package app.veyra.music.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewCarousel
import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.veyra.music.data.model.CARD_ART_PX
import app.veyra.music.data.model.HEADER_ART_PX
import app.veyra.music.data.model.HomeShelf
import app.veyra.music.data.model.ShelfItem
import app.veyra.music.data.model.Song
import app.veyra.music.data.model.artworkAt
import app.veyra.music.sharedui.resources.*
import app.veyra.music.ui.components.PAGE_GUTTER
import app.veyra.music.ui.components.PlayingAccent
import app.veyra.music.ui.components.SearchPlayingBars
import app.veyra.music.ui.components.contextClick
import app.veyra.music.ui.components.longPressMenuClickable
import app.veyra.music.ui.components.thumbnailBorder
import app.veyra.music.ui.icons.VeyraIcons
import app.veyra.music.ui.player.PlayerSettings
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

/** Same content and actions in both presentations; neither drops items from a long shelf. */
@Composable
internal fun HomeCollection(
    shelf: HomeShelf,
    featured: Boolean,
    onItemClick: (ShelfItem) -> Unit,
    onItemLongPress: ((ShelfItem) -> Unit)?,
    currentSong: Song?,
    isPlaying: Boolean,
) {
    if (shelf.items.isEmpty()) return
    val reduceMotion by PlayerSettings.reduceAnimation.collectAsStateWithLifecycle()
    var banners by rememberSaveable(shelf.title, featured) { mutableStateOf(featured) }
    var bannerAnchor by rememberSaveable(shelf.title, featured) { mutableIntStateOf(0) }
    var requestedAnchor by remember(shelf.title, featured) { mutableStateOf<Int?>(null) }
    val bannerState = rememberPagerState { shelf.items.size }
    val tileState = rememberPagerState { HomeCarouselPaging.pageCount(shelf.items.size) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val matches = remember(shelf.items, currentSong) { shelf.items.playingItemMatcher(currentSong) }
    // Wait for the destination pager's first layout, then retain the visible collection position.
    LaunchedEffect(banners) {
        val anchor = requestedAnchor ?: return@LaunchedEffect
        if (banners) bannerState.scrollToPage(anchor.coerceIn(shelf.items.indices))
        else tileState.scrollToPage(HomeCarouselPaging.tilePageForItem(anchor, shelf.items.size))
        requestedAnchor = null
    }
    Column(Modifier.padding(bottom = 24.dp)) {
        SectionHeader(shelf.title, shelf.subtitle, trailing = if (featured) {
            {
                HomeActionButton(
                    icon = if (banners) VeyraIcons.GridView else Icons.Rounded.ViewCarousel,
                    description = stringResource(if (banners) Res.string.home_show_tiles else Res.string.home_show_banners),
                    reducedMotion = reduceMotion,
                    onClick = {
                        if (banners) bannerAnchor = bannerState.settledPage
                        requestedAnchor = if (banners) bannerAnchor
                            else HomeCarouselPaging.bannerItemForTilePage(tileState.settledPage, bannerAnchor, shelf.items.size)
                        banners = !banners
                    },
                )
            }
        } else null)
        AnimatedContent(
            targetState = banners,
            transitionSpec = {
                val duration = if (reduceMotion) 0 else 260
                val direction = (if (targetState) -1 else 1) * (if (rtl) -1 else 1)
                (slideInHorizontally(tween(duration)) { direction * it / 8 } + fadeIn(tween(duration)))
                    .togetherWith(slideOutHorizontally(tween(duration)) { -direction * it / 8 } + fadeOut(tween(duration / 2)))
                    .using(SizeTransform(sizeAnimationSpec = { _, _ -> tween(duration) }))
            },
            label = "homeCollectionLayout",
        ) { showBanners ->
            BoxWithConstraints(Modifier.fillMaxWidth().then(
                if (showBanners != banners) Modifier.clearAndSetSemantics { } else Modifier,
            )) {
                val pageWidth = (maxWidth - PAGE_GUTTER * 2).coerceAtLeast(120.dp).coerceAtMost(620.dp)
                val fontScale = LocalDensity.current.fontScale
                val state = if (showBanners) bannerState else tileState
                val pageSize = remember(pageWidth) { PageSize.Fixed(pageWidth) }
                HorizontalPager(
                    state = state,
                    userScrollEnabled = showBanners == banners,
                    pageSize = pageSize,
                    contentPadding = PaddingValues(horizontal = PAGE_GUTTER),
                    pageSpacing = 12.dp,
                    key = { page ->
                        val index = if (showBanners) page else HomeCarouselPaging.firstItemForTilePage(page, shelf.items.size)
                        val item = shelf.items[index]
                        val id = item.videoId ?: item.browseId ?: item.title
                        "${id.length}:$id:$page"
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { page ->
                    val motion = Modifier.graphicsLayer {
                        if (!reduceMotion) {
                            val distance = abs(state.currentPage - page + state.currentPageOffsetFraction).coerceIn(0f, 1f)
                            scaleX = 1f - distance * 0.025f
                            scaleY = 1f - distance * 0.025f
                            alpha = 1f - distance * 0.12f
                        }
                    }
                    if (showBanners) {
                        val item = shelf.items[page]
                        HomeArtCard(item, onItemClick, onItemLongPress, matches(item), isPlaying,
                            compact = false, interactive = showBanners == banners, modifier = motion.fillMaxWidth()
                                .height((pageWidth * 0.63f).coerceIn(228.dp, 320.dp) * fontScale.coerceAtLeast(1f)))
                    } else {
                        val start = HomeCarouselPaging.firstItemForTilePage(page, shelf.items.size)
                        TilePage(shelf.items.subList(start, minOf(start + HomeCarouselPaging.TILES_PER_PAGE, shelf.items.size)),
                            onItemClick, onItemLongPress, matches, isPlaying,
                            readableRows = fontScale > 1.3f || pageWidth < 280.dp,
                            interactive = showBanners == banners,
                            modifier = motion.fillMaxWidth())
                    }
                }
            }
        }
        key(banners) { HomePagerControls(if (banners) bannerState else tileState, reduceMotion) }
    }
}

@Composable
private fun TilePage(
    items: List<ShelfItem>,
    onClick: (ShelfItem) -> Unit,
    onHold: ((ShelfItem) -> Unit)?,
    matches: (ShelfItem) -> Boolean,
    isPlaying: Boolean,
    readableRows: Boolean,
    modifier: Modifier,
    interactive: Boolean = true,
) {
    if (readableRows) {
        val minimum = maxOf(276.dp, (64.dp * LocalDensity.current.fontScale + 8.dp) * 3)
        Column(modifier.heightIn(min = minimum).padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                CompactTrackRow(item.title, item.subtitle, item.thumbnailUrl, { onClick(item) },
                    onHold?.let { { it(item) } }, matches(item), matches(item) && isPlaying, titleMaxLines = 2, enabled = interactive)
            }
        }
    } else {
        Row(modifier.height(276.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val first = items.first()
            HomeArtCard(first, onClick, onHold, matches(first), isPlaying, compact = false, interactive = interactive,
                modifier = Modifier.weight(if (items.size == 1) 1f else 1.1f).fillMaxSize())
            if (items.size > 1) {
                Column(Modifier.weight(1f).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.drop(1).forEach { item ->
                        HomeArtCard(item, onClick, onHold, matches(item), isPlaying, compact = true, interactive = interactive,
                            modifier = Modifier.weight(1f).fillMaxWidth())
                    }
                }
            }
        }
    }
}

/** Cached artwork, readable captions and original item/menu actions in either presentation. */
@Composable
private fun HomeArtCard(
    item: ShelfItem,
    onClick: (ShelfItem) -> Unit,
    onHold: ((ShelfItem) -> Unit)?,
    isCurrent: Boolean,
    isPlaying: Boolean,
    compact: Boolean,
    modifier: Modifier,
    interactive: Boolean = true,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    HomeArtworkFrame(
        title = item.title, subtitle = localizeCardSubtitle(item.subtitle), compact = compact,
        current = isCurrent, currentLabel = stringResource(Res.string.now_playing), accent = PlayingAccent,
        modifier = modifier.clip(RoundedCornerShape(if (compact) 20.dp else 26.dp))
            .longPressMenuClickable({ onClick(item) }, onHold?.let { { it(item) } }, enabled = interactive)
            .contextClick(if (interactive) onHold?.let { { it(item) } } else null),
        artwork = {
            AsyncImage(item.thumbnailUrl.artworkAt(if (compact) CARD_ART_PX else HEADER_ART_PX), null,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        },
        action = {
            if (isCurrent && isPlaying) SearchPlayingBars(Modifier.size(20.dp))
            else if (item.videoId != null) Icon(painterResource(Res.drawable.ic_player_play), null, themeKey="play",
                tint = Color.White, modifier = Modifier.size(32.dp))
            else Icon(VeyraIcons.ChevronRight, null, tint = Color.White,
                modifier = Modifier.size(18.dp).graphicsLayer { scaleX = if (rtl) -1f else 1f })
        },
    )
}

@Composable
private fun HomePagerControls(state: PagerState, reduceMotion: Boolean) {
    if (state.pageCount <= 1) return
    val scope = rememberCoroutineScope()
    val page = state.settledPage.coerceIn(0, state.pageCount - 1)
    val pageLabel = stringResource(Res.string.home_page, page + 1, state.pageCount)
    HomePageNavigation(page = state.settledPage, count = state.pageCount, pageDescription = pageLabel,
        previousDescription = stringResource(Res.string.widget_previous), nextDescription = stringResource(Res.string.widget_next),
        scrolling = state.isScrollInProgress, reducedMotion = reduceMotion,
        onPrevious = { scope.launch { val target = (state.settledPage - 1).coerceAtLeast(0)
            if (reduceMotion) state.scrollToPage(target) else state.animateScrollToPage(target) } },
        onNext = { scope.launch { val target = (state.settledPage + 1).coerceAtMost(state.pageCount - 1)
            if (reduceMotion) state.scrollToPage(target) else state.animateScrollToPage(target) } },
        modifier = Modifier.padding(horizontal = PAGE_GUTTER, vertical = 8.dp))
}

@Composable
private fun HomePageArrow(previous: Boolean, enabled: Boolean, onClick: () -> Unit) {
    HomeActionButton(VeyraIcons.ChevronRight,
        stringResource(if (previous) Res.string.widget_previous else Res.string.widget_next),
        onClick, enabled = enabled, mirrored = previous != (LocalLayoutDirection.current == LayoutDirection.Rtl))
}
