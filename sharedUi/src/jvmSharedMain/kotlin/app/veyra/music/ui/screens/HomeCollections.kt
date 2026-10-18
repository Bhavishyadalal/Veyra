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
import androidx.compose.material3.Icon
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
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    .clickable(role = Role.Button) {
                        if (banners) bannerAnchor = bannerState.currentPage
                        requestedAnchor = if (banners) bannerAnchor
                            else HomeCarouselPaging.bannerItemForTilePage(tileState.currentPage, bannerAnchor, shelf.items.size)
                        banners = !banners
                    }, contentAlignment = Alignment.Center) {
                    Icon(if (banners) VeyraIcons.GridView else Icons.Rounded.ViewCarousel,
                        stringResource(if (banners) Res.string.home_show_tiles else Res.string.home_show_banners),
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
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
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val pageWidth = (maxWidth - PAGE_GUTTER * 2).coerceAtLeast(120.dp).coerceAtMost(620.dp)
                val fontScale = LocalDensity.current.fontScale
                val state = if (showBanners) bannerState else tileState
                val pageSize = remember(pageWidth) { PageSize.Fixed(pageWidth) }
                HorizontalPager(
                    state = state,
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
                            compact = false, modifier = motion.fillMaxWidth()
                                .height((pageWidth * 0.63f).coerceIn(228.dp, 320.dp) * fontScale.coerceAtLeast(1f)))
                    } else {
                        val start = HomeCarouselPaging.firstItemForTilePage(page, shelf.items.size)
                        TilePage(shelf.items.subList(start, minOf(start + HomeCarouselPaging.TILES_PER_PAGE, shelf.items.size)),
                            onItemClick, onItemLongPress, matches, isPlaying,
                            readableRows = fontScale > 1.3f || pageWidth < 280.dp,
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
) {
    if (readableRows) {
        val minimum = maxOf(276.dp, (64.dp * LocalDensity.current.fontScale + 8.dp) * 3)
        Column(modifier.heightIn(min = minimum).padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { item ->
                CompactTrackRow(item.title, item.subtitle, item.thumbnailUrl, { onClick(item) },
                    onHold?.let { { it(item) } }, matches(item), matches(item) && isPlaying, titleMaxLines = 2)
            }
        }
    } else {
        Row(modifier.height(276.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val first = items.first()
            HomeArtCard(first, onClick, onHold, matches(first), isPlaying, compact = false,
                modifier = Modifier.weight(if (items.size == 1) 1f else 1.1f).fillMaxSize())
            if (items.size > 1) {
                Column(Modifier.weight(1f).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items.drop(1).forEach { item ->
                        HomeArtCard(item, onClick, onHold, matches(item), isPlaying, compact = true,
                            modifier = Modifier.weight(1f).fillMaxWidth())
                    }
                }
            }
        }
    }
}

/** Contrast scrim and captions are identical across the banner and tile presentations. */
@Composable
private fun HomeArtCard(
    item: ShelfItem,
    onClick: (ShelfItem) -> Unit,
    onHold: ((ShelfItem) -> Unit)?,
    isCurrent: Boolean,
    isPlaying: Boolean,
    compact: Boolean,
    modifier: Modifier,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val shape = RoundedCornerShape(22.dp)
    Box(modifier.clip(shape).thumbnailBorder(shape).background(MaterialTheme.colorScheme.surfaceVariant)
        .longPressMenuClickable({ onClick(item) }, onHold?.let { { it(item) } })
        .contextClick(onHold?.let { { it(item) } })) {
        AsyncImage(item.thumbnailUrl.artworkAt(if (compact) CARD_ART_PX else HEADER_ART_PX), null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(
            0f to Color.Black.copy(alpha = 0.04f), 0.35f to Color.Black.copy(alpha = 0.08f),
            1f to Color.Black.copy(alpha = 0.90f))))
        Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(if (compact) 12.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            if (!compact) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.17f)),
                    contentAlignment = Alignment.Center) {
                    if (isCurrent && isPlaying) SearchPlayingBars(Modifier.size(20.dp))
                    else if (item.videoId != null) Icon(painterResource(Res.drawable.ic_player_play), null,
                        tint = Color.White, modifier = Modifier.size(28.dp))
                    else Icon(VeyraIcons.ChevronRight, null, tint = Color.White,
                        modifier = Modifier.size(18.dp).graphicsLayer { scaleX = if (rtl) -1f else 1f })
                }
            }
            Text(item.title, style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                color = if (isCurrent) PlayingAccent else Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (item.subtitle.isNotBlank()) Text(localizeCardSubtitle(item.subtitle),
                style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.82f),
                maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
        }
        if (compact && isCurrent && isPlaying) SearchPlayingBars(Modifier.align(Alignment.TopEnd).padding(10.dp).size(18.dp))
    }
}

@Composable
private fun HomePagerControls(state: PagerState, reduceMotion: Boolean) {
    if (state.pageCount <= 1) return
    val scope = rememberCoroutineScope()
    val page = state.currentPage.coerceIn(0, state.pageCount - 1)
    val pageLabel = stringResource(Res.string.home_page, page + 1, state.pageCount)
    Row(Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER), verticalAlignment = Alignment.CenterVertically) {
        Text("${page + 1} / ${state.pageCount}", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f).clearAndSetSemantics { contentDescription = pageLabel })
        HomePageArrow(previous = true, enabled = page > 0 && !state.isScrollInProgress) {
            scope.launch {
                val target = (state.currentPage - 1).coerceAtLeast(0)
                if (reduceMotion) state.scrollToPage(target) else state.animateScrollToPage(target)
            }
        }
        HomePageArrow(previous = false, enabled = page < state.pageCount - 1 && !state.isScrollInProgress) {
            scope.launch {
                val target = (state.currentPage + 1).coerceAtMost(state.pageCount - 1)
                if (reduceMotion) state.scrollToPage(target) else state.animateScrollToPage(target)
            }
        }
    }
}

@Composable
private fun HomePageArrow(previous: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(Modifier.size(48.dp).clip(CircleShape).clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center) {
        Icon(VeyraIcons.ChevronRight,
            stringResource(if (previous) Res.string.widget_previous else Res.string.widget_next),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.28f),
            modifier = Modifier.size(18.dp).graphicsLayer { scaleX = if (previous != rtl) -1f else 1f })
    }
}
