package app.veyra.music.ui.screens

import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.icons.VeyraIcons

internal fun homeMotionMillis(reduced: Boolean, duration: Int) = if (reduced) 0 else duration

/** Neutral, wrapping account entry; authentication remains entirely in its host callback. */
@Composable
internal fun HomeAccountPrompt(
    title: String,
    subtitle: String,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = app.veyra.music.ui.themes.LocalThemeVisuals.current?.takeIf {it.materialStyle != "classic"}?.let {app.veyra.music.ui.themes.themeControlShape(it,24)} ?: RoundedCornerShape(24.dp)
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(colors.surfaceVariant.copy(alpha = .85f), colors.surface)
                )
            )
            .border(1.dp, colors.onSurface.copy(alpha = .10f), shape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onSignIn,
            )
            .background(if (pressed) colors.onSurface.copy(alpha = .08f) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) { leading() }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }
        HomeChevron(previous = false, tint = colors.onSurfaceVariant)
    }
}

/** The same native heading is used by the app and the component review. */
@Composable
internal fun HomeHeading(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    brand: @Composable RowScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = brand,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                title,
                Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colors.onBackground,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
        }
    }
}

/** Fixed touch area with a finite icon fade and stateful pressed feedback. */
@Composable
internal fun HomeActionButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    reducedMotion: Boolean = false,
    selected: Boolean? = null,
    mirrored: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by
        animateFloatAsState(
            if (pressed && enabled && !reducedMotion) .92f else 1f,
            tween(homeMotionMillis(reducedMotion, 120)),
            label = "homeActionPress",
        )
    Box(
        Modifier.size(48.dp)
            .clip(app.veyra.music.ui.themes.LocalThemeVisuals.current?.takeIf {it.materialStyle != "classic"}?.let {app.veyra.music.ui.themes.themeControlShape(it)} ?: CircleShape)
            .background(
                if (selected == true || pressed) colors.onSurface.copy(alpha = .12f)
                else colors.surfaceVariant.copy(alpha = .55f)
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = description
                selected?.let { this.selected = it }
            },
        contentAlignment = Alignment.Center,
    ) {
        Crossfade(
            icon,
            animationSpec = tween(homeMotionMillis(reducedMotion, 140)),
            label = "homeActionIcon",
        ) { current ->
            Icon(
                current,
                null,
                tint = colors.onSurface.copy(alpha = if (enabled) .9f else .28f),
                modifier =
                    Modifier.size(20.dp)
                        .graphicsLayer {
                            scaleX = if (mirrored) -scale else scale
                            scaleY = scale
                        }
                        .clearAndSetSemantics {},
            )
        }
    }
}

@Composable
private fun HomeChevron(previous: Boolean, tint: Color, modifier: Modifier = Modifier) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Icon(
        VeyraIcons.ChevronRight,
        null,
        tint = tint,
        modifier = modifier.size(18.dp).graphicsLayer { scaleX = if (previous != rtl) -1f else 1f },
    )
}

/** Bounded progress instead of a long line of dots; page labels never reflow on digit changes. */
@Composable
internal fun HomePageNavigation(
    page: Int,
    count: Int,
    pageDescription: String,
    previousDescription: String,
    nextDescription: String,
    scrolling: Boolean,
    reducedMotion: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (count <= 1) return
    val safePage = page.coerceIn(0, count - 1)
    val fraction =
        animateFloatAsState(
            (safePage + 1f) / count,
            tween(homeMotionMillis(reducedMotion, 180)),
            label = "homePageProgress",
        )
    val colors = MaterialTheme.colorScheme
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            Modifier.weight(1f).clearAndSetSemantics { contentDescription = pageDescription },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "${safePage + 1} / $count",
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontFeatureSettings = "tnum",
                        textDirection = androidx.compose.ui.text.style.TextDirection.Ltr,
                    ),
                color = colors.onSurfaceVariant,
            )
            Canvas(
                Modifier.widthIn(max = 88.dp).fillMaxWidth().height(2.dp).graphicsLayer {
                    scaleX = if (rtl) -1f else 1f
                }
            ) {
                drawLine(
                    colors.onSurface.copy(alpha = .12f),
                    Offset.Zero,
                    Offset(size.width, 0f),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    colors.onSurface.copy(alpha = .7f),
                    Offset.Zero,
                    Offset(size.width * fraction.value, 0f),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round,
                )
            }
        }
        val previousIcon = VeyraIcons.ChevronRight
        val nextIcon = VeyraIcons.ChevronRight
        HomeActionButton(
            previousIcon,
            previousDescription,
            onPrevious,
            safePage > 0 && !scrolling,
            reducedMotion,
            mirrored = !rtl,
        )
        HomeActionButton(
            nextIcon,
            nextDescription,
            onNext,
            safePage < count - 1 && !scrolling,
            reducedMotion,
            mirrored = rtl,
        )
    }
}

/** Artwork is supplied by the existing cached image loader; fallback and captions stay native. */
@Composable
internal fun HomeArtworkFrame(
    title: String,
    subtitle: String,
    compact: Boolean,
    current: Boolean,
    currentLabel: String,
    accent: Color,
    modifier: Modifier,
    artwork: @Composable BoxScope.() -> Unit,
    action: @Composable () -> Unit,
) {
    val theme = app.veyra.music.ui.themes.LocalThemeVisuals.current
    val shape = RoundedCornerShape(theme?.let { (it.rowCorner + if (compact) 4 else 10).dp } ?: if (compact) 20.dp else 26.dp)
    Box(
        modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (current) accent.copy(alpha = .65f) else theme?.outline?.copy(alpha = .8f) ?: Color.White.copy(alpha = .12f),
                shape,
            )
    ) {
        // Visible while loading and after an image failure; never an empty grey block.
        Box(
            Modifier.matchParentSize()
                .background(Brush.linearGradient(listOf(Color(0xFF35363B), Color(0xFF111216)))),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VeyraIcons.Library,
                null,
                tint = Color.White.copy(alpha = .12f),
                modifier = Modifier.size(if (compact) 40.dp else 72.dp),
            )
        }
        artwork()
        Box(
            Modifier.matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = .06f),
                        .25f to Color.Transparent,
                        1f to (theme?.surface ?: Color.Black).copy(alpha = .96f),
                    )
                )
        )
        if (current && !compact)
            Text(
                currentLabel,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier =
                    Modifier.align(Alignment.TopStart)
                        .padding(16.dp)
                        .clip(app.veyra.music.ui.themes.LocalThemeVisuals.current?.takeIf {it.materialStyle != "classic"}?.let {app.veyra.music.ui.themes.themeControlShape(it)} ?: CircleShape)
                        .background(Color.Black.copy(alpha = .65f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        Column(
            Modifier.align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (!compact) {
                Box(
                    Modifier.size(40.dp)
                        .clip(app.veyra.music.ui.themes.LocalThemeVisuals.current?.takeIf {it.materialStyle != "classic"}?.let {app.veyra.music.ui.themes.themeControlShape(it)} ?: CircleShape)
                        .background(theme?.accent?.copy(alpha = .25f) ?: Color.White.copy(alpha = .18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    action()
                }
                Spacer(Modifier.height(4.dp))
            }
            Text(
                title,
                style =
                    if (compact) MaterialTheme.typography.titleMedium
                    else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (current) accent else Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotBlank())
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = .78f),
                    maxLines = if (compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                )
        }
        if (compact && current) Box(Modifier.align(Alignment.TopEnd).padding(10.dp)) { action() }
    }
}

@Composable
internal fun HomeEmptyContent(
    title: String,
    subtitle: String,
    refreshLabel: String,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            VeyraIcons.Library,
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp),
        )
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(
            onClick = onRefresh,
            enabled = !refreshing,
            modifier = Modifier.heightIn(min = 48.dp),
        ) {
            Text(refreshLabel)
        }
    }
}
