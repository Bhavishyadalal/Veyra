package app.veyra.music.ui.performance

import android.os.SystemClock
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import kotlinx.coroutines.delay

/** Returns true while this optional first-build preparation owns the screen. */
@Composable
internal fun StartupPreparationGate(): Boolean {
    val runner = StartupPreparation.runner
    val state by runner.state.collectAsStateWithLifecycle()
    var continued by rememberSaveable { mutableStateOf(false) }
    val started = rememberSaveable { SystemClock.elapsedRealtime() }
    LaunchedEffect(runner) { withFrameNanos { }; runner.start() }
    if (continued || state.done) return false
    // Avoid flashing preparation copy on later cold launches while checking the saved build.
    if (state.checking) {
        Surface(Modifier.fillMaxSize()) {}
        return true
    }
    var elapsed by remember { mutableIntStateOf(0) }
    // The timer is visible only here; no per-second root/app invalidation.
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
            while (true) {
                elapsed = ((SystemClock.elapsedRealtime() - started) / 1000).toInt().coerceAtLeast(0)
                delay(1000)
            }
        }
    }
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
            Column(Modifier.widthIn(max = 440.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.startup_prepare_title), style = MaterialTheme.typography.headlineMedium)
                Text(stringResource(if (state.failed) R.string.startup_prepare_failed
                    else if (elapsed >= 15) R.string.startup_prepare_slow else R.string.startup_prepare_hint),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                val stages = listOf(R.string.theme_packs, R.string.asset_packs, R.string.appearance_fonts, R.string.app_name)
                Text(stringResource(stages[state.completed.coerceAtMost(stages.lastIndex)]),
                    Modifier.semantics { liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(progress = { state.completed.toFloat() / state.total.coerceAtLeast(1) },
                    modifier = Modifier.fillMaxWidth())
                Text(stringResource(R.string.seconds_short, elapsed), style = MaterialTheme.typography.labelMedium)
                if (state.failed) OutlinedButton(onClick = { runner.start() }) { Text(stringResource(R.string.retry)) }
                Button(onClick = { continued = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.onboarding_next))
                }
            }
        }
    }
    return true
}
