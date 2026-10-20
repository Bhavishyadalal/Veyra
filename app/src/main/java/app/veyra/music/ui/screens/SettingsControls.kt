package app.veyra.music.ui.screens

import app.veyra.music.ui.layout.stackForReadability
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import app.veyra.music.ui.motion.VeyraMotion
import androidx.compose.foundation.interaction.MutableInteractionSource
import app.veyra.music.ui.motion.veyraPressFeedback
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.components.VeyraSurfaces
import app.veyra.music.ui.components.veyraSwitchColors
import app.veyra.music.ui.icons.SettingsIcons

// Shared by every settings destination. No blur, image decode or permanent animation.
internal val GroupShape = RoundedCornerShape(24.dp)
internal val GROUP_INSET = 16.dp
internal val ROW_INSET = 12.dp
internal val ICON_SIZE = 22.dp
internal val ICON_GAP = 14.dp
private val ICON_SLOT = 36.dp
internal val TEXT_INSET = ROW_INSET + ICON_SLOT + ICON_GAP

@Composable
internal fun SettingsSectionTitle(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().semantics { heading() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(3.dp, 18.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            title,
            modifier = Modifier.padding(start = 10.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun RowDivider() {
    HorizontalDivider(
        Modifier.padding(start = TEXT_INSET),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
    )
}

/** One action per row. A switch is rendered passively; the entire row owns its toggle semantics. */
@Composable
internal fun SettingsRow(
    icon: ImageVector? = null,
    title: String,
    subtitle: String? = null,
    subtitleContent: (@Composable () -> Unit)? = null,
    value: String? = null,
    badge: String? = null,
    enabled: Boolean = true,
    iconPainter: Painter? = null,
    onClick: (() -> Unit)? = null,
    emphasized: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    checked: Boolean? = null,
    destructive: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val scheme = MaterialTheme.colorScheme
    val tint = if (destructive) scheme.error else scheme.primary
    val action =
        when {
            checked != null && onCheckedChange == null && onClick != null ->
                Modifier.toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Switch,
                    interactionSource = interaction,
                    indication = null,
                    onValueChange = { onClick() },
                )
            onClick != null ->
                Modifier.clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            else -> Modifier
        }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = stackForReadability(maxWidth.value, LocalDensity.current.fontScale, 300f)
        @Composable
        fun EndControl() {
            when {
                checked != null ->
                    Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        enabled = enabled,
                        modifier =
                            if (onCheckedChange != null)
                                Modifier.semantics { contentDescription = title }
                            else Modifier,
                        colors = veyraSwitchColors(),
                        thumbContent =
                            if (checked) {
                                { Icon(Icons.Rounded.Check, null, Modifier.size(14.dp)) }
                            } else null,
                    )
                trailing != null -> trailing()
                onClick != null -> Chevron()
            }
        }
        Row(
            Modifier.fillMaxWidth()
                .clip(VeyraSurfaces.Row)
                .veyraPressFeedback(interaction, tint, enabled)
                .then(action)
                .semantics(mergeDescendants = true) {}
                .heightIn(min = if (emphasized) 92.dp else 80.dp)
                .padding(horizontal = ROW_INSET, vertical = 15.dp),
            verticalAlignment = if (stacked) Alignment.Top else Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(ICON_SLOT)
                    .clip(CircleShape)
                    .background(if (emphasized) tint.copy(alpha = 0.10f) else Color.Transparent),
                contentAlignment = Alignment.Center,
            ) {
                if (iconPainter != null)
                    Icon(
                        iconPainter,
                        null,
                        Modifier.size(ICON_SIZE),
                        tint = tint.copy(alpha = if (enabled) 1f else 0.45f),
                    )
                else if (icon != null)
                    Icon(
                        icon,
                        null,
                        Modifier.size(ICON_SIZE),
                        tint = tint.copy(alpha = if (enabled) 1f else 0.45f),
                    )
            }
            Spacer(Modifier.width(ICON_GAP))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style =
                        if (emphasized) MaterialTheme.typography.titleLarge
                        else MaterialTheme.typography.titleMedium,
                    fontWeight = if (emphasized) FontWeight.Bold else FontWeight.SemiBold,
                    color =
                        if (destructive) scheme.error
                        else if (enabled) scheme.onSurface else scheme.onSurfaceVariant,
                )
                if (badge != null) {
                    Spacer(Modifier.height(5.dp))
                    Badge(badge)
                }
                if (subtitleContent != null) subtitleContent()
                else if (subtitle != null)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                if (value != null)
                    Text(
                        value,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = tint,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                if (stacked && (checked != null || trailing != null || onClick != null)) {
                    Spacer(Modifier.height(10.dp))
                    EndControl()
                }
            }
            if (!stacked) {
                if (checked != null || trailing != null || onClick != null)
                    Spacer(Modifier.width(12.dp))
                EndControl()
            }
        }
    }
}

@Composable
internal fun SettingsSubRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    badge: String? = null,
    subtitle: String? = null,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = stackForReadability(maxWidth.value, LocalDensity.current.fontScale, 300f)
        Row(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .toggleable(checked, role = Role.Switch, onValueChange = onCheckedChange)
                .heightIn(min = 56.dp)
                .padding(start = TEXT_INSET, end = ROW_INSET, top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (badge != null) Badge(badge)
                if (subtitle != null)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                if (stacked) Switch(checked, onCheckedChange = null, colors = veyraSwitchColors())
            }
            if (!stacked) {
                Spacer(Modifier.width(12.dp))
                Switch(checked, onCheckedChange = null, colors = veyraSwitchColors())
            }
        }
    }
}

/** Status remains sentence case; size grows naturally with translated or enlarged text. */
@Composable
internal fun Badge(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier =
            Modifier.clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

@Composable
internal fun Chevron() {
    val direction = LocalLayoutDirection.current
    Box(
        Modifier.size(28.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.045f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            SettingsIcons.Chevron,
            null,
            Modifier.size(15.dp).graphicsLayer {
                scaleX = if (direction == LayoutDirection.Rtl) -1f else 1f
            },
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Draft values update immediately; persistence can be deferred to release when a formatter is
 * supplied.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SliderRow(
    icon: ImageVector,
    title: String,
    value: String,
    sliderValue: Float,
    onSliderValue: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    subtitle: String? = null,
    formatValue: ((Float) -> String)? = null,
) {
    var draft by remember(sliderValue) { mutableFloatStateOf(sliderValue) }
    val displayed = formatValue?.invoke(draft) ?: value
    Column {
        SettingsRow(icon = icon, title = title, subtitle = subtitle, value = displayed)
        val colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
            )
        Slider(
            value = if (formatValue != null) draft else sliderValue,
            onValueChange = { next ->
                if (formatValue != null) draft = next else onSliderValue(next)
            },
            onValueChangeFinished = {
                if (formatValue != null && draft != sliderValue) onSliderValue(draft)
            },
            valueRange = valueRange,
            steps = steps,
            colors = colors,
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    colors = colors,
                    drawStopIndicator = null,
                    drawTick = { _, _ -> },
                )
            },
            modifier =
                Modifier.padding(start = TEXT_INSET, end = ROW_INSET, bottom = 8.dp).semantics {
                    contentDescription = title
                },
        )
    }
}

@Composable
internal fun DestructiveRow(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .heightIn(min = 56.dp)
            .padding(horizontal = ROW_INSET, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error.copy(alpha = if (enabled) 1f else 0.5f),
        )
    }
}

/** Switch to a vertical selector when labels need more space, rather than clipping them. */
@Composable
internal fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier.fillMaxWidth().selectableGroup()) {
        val stacked = density.fontScale > 1.25f || maxWidth / options.size.coerceAtLeast(1) < 88.dp
        @Composable
        fun Option(index: Int, label: String, modifier: Modifier) {
            val chosen = index == selectedIndex
            Row(
                modifier
                    .clip(VeyraSurfaces.Row)
                    .background(
                        if (chosen) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent
                    )
                    .selectable(
                        selected = chosen,
                        enabled = enabled,
                        role = Role.RadioButton,
                        onClick = { if (!chosen) onSelect(index) },
                    )
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .alpha(if (enabled) 1f else 0.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (chosen) FontWeight.Bold else FontWeight.Medium,
                    color =
                        if (chosen) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (stacked)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEachIndexed { index, label ->
                    Option(index, label, Modifier.fillMaxWidth())
                }
            }
        else
            Row(
                Modifier.clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.035f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                options.forEachIndexed { index, label -> Option(index, label, Modifier.weight(1f)) }
            }
    }
}

/** A single radio target, with persistent context for both selected and unselected choices. */
@Composable
internal fun SettingsChoiceRow(
    title: String,
    detail: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)
                else Color.Transparent
            )
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .heightIn(min = 80.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected, onClick = null)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
internal fun SettingsPageHeader(title: String, icon: ImageVector) {
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        Spacer(Modifier.width(16.dp))
        Box(
            Modifier.size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
internal fun SettingsPickerHeader(title: String, detail: String, icon: ImageVector) {
    Column(Modifier.padding(start = 28.dp, end = 28.dp, bottom = 20.dp)) {
        Box(
            Modifier.padding(bottom = 18.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, Modifier.size(23.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            detail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

/** The artwork owns the outline; the option itself remains an open, spacious destination. */
@Composable
internal fun SettingsIconChoice(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    preview: @Composable () -> Unit,
) {
    Column(
        modifier
            .clip(VeyraSurfaces.Row)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        if (selected) 2.dp else 1.dp,
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(20.dp),
                    )
                    .padding(4.dp)
            ) {
                preview()
            }
            if (selected)
                Box(
                    Modifier.align(Alignment.BottomEnd)
                        .size(23.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        null,
                        Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
        }
        Text(
            label,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color =
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

/** Google/YouTube account entry. Authentication is supplied by the existing host callback. */
@Composable
internal fun SettingsAccountSummary(
    title: String,
    detail: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    avatar: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick)
                else Modifier
            )
            .semantics(mergeDescendants = true) {}
            .heightIn(min = 96.dp)
            .padding(horizontal = ROW_INSET, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            if (avatar != null) avatar()
            else
                Icon(
                    SettingsIcons.Account,
                    null,
                    Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        if (onClick != null) {
            Spacer(Modifier.width(12.dp))
            Chevron()
        }
    }
}

/** Reveal dependent controls without retaining hidden, clickable settings on exit. */
@Composable
internal fun SettingsOptionsReveal(
    reducedMotion: Boolean,
    content: @Composable () -> Unit,
) {
    val visibility = remember { MutableTransitionState(reducedMotion).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = visibility,
        enter = if (reducedMotion) EnterTransition.None else
            fadeIn(tween(VeyraMotion.Feedback)) +
                expandVertically(tween(VeyraMotion.Section, easing = FastOutSlowInEasing)),
        exit = ExitTransition.None,
    ) {
        Column(Modifier.fillMaxWidth()) { content() }
    }
}
