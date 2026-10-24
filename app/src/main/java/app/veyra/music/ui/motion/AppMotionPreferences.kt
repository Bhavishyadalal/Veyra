package app.veyra.music.ui.motion

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.settings.AppMotionStyle
import app.veyra.music.data.settings.MotionTempo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Avoid invalidating motion consumers for unrelated text/artwork preferences. */
@Composable fun rememberAppMotionPreferences():Pair<AppMotionStyle,MotionTempo> {
    val flow=remember {AppSettings.experience.map {it.appMotionStyle to it.motionTempo}.distinctUntilChanged()}
    val options by flow.collectAsStateWithLifecycle(initialValue=AppSettings.experience.value.let {it.appMotionStyle to it.motionTempo})
    return options
}
