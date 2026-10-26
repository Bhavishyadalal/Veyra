package app.veyra.music.ui.components

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import app.veyra.music.AppIcon
import app.veyra.music.ui.theme.rememberLauncherIconPainter
import coil3.compose.AsyncImagePainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings

/** Short entrance signature, replayed on foreground entry, without delaying data or playback. */
@Composable
fun LaunchSignature(generation: Int) {
    val selectedIcon by AppIcon.selected.collectAsStateWithLifecycle()
    val iconPainter = selectedIcon?.let { rememberLauncherIconPainter(it) }
    val iconState = (iconPainter as? AsyncImagePainter)?.state?.collectAsState()?.value
    val iconReady = iconState is AsyncImagePainter.State.Success
    val reduced by AppSettings.reduceAnimation.collectAsStateWithLifecycle()
    val enabled = !reduced && ValueAnimator.areAnimatorsEnabled()
    var visible by remember(generation) { mutableStateOf(enabled) }
    val progress = remember(generation) { Animatable(0f) }
    LaunchedEffect(generation, enabled, iconReady) {
        if (!enabled) { visible = false; return@LaunchedEffect }
        // Avoid default/placeholder flashes while the persisted alias is loading.
        if (!iconReady) return@LaunchedEffect
        progress.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
        visible = false
    }
    if (!visible || !enabled || !iconReady) return
    Box(
        Modifier.fillMaxSize()
            .graphicsLayer { alpha = (1f - ((progress.value - 0.35f) / 0.65f).coerceIn(0f, 1f)) }
            .background(Color(0xFF08090B))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { visible = false }
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = 0.92f + progress.value * 0.08f
                scaleY = scaleX
                translationY = (1f - progress.value) * 12.dp.toPx()
            }) {
            Image(iconPainter, null, Modifier.size(116.dp))
            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.launch_tagline), style = MaterialTheme.typography.labelLarge, color = Color(0xFFB7B8C0))
        }
    }
}
