package app.veyra.music.ui

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

/** Opaque destination surfaces stay present under the entering page, avoiding a dark flash. */
fun navigationTransition(motion: NavigationMotion, reduceMotion: Boolean, rtl: Boolean): ContentTransform {
    val direction = if (rtl) -1 else 1
    return when {
        reduceMotion || motion == NavigationMotion.None -> EnterTransition.None togetherWith ExitTransition.None
        motion == NavigationMotion.Tab ->
            (fadeIn(tween(140)) togetherWith ExitTransition.None).apply { targetContentZIndex = 1f }
        motion == NavigationMotion.Back ->
            (slideInHorizontally(tween(240)) { -direction * it / 32 } togetherWith
                (slideOutHorizontally(tween(240)) { direction * it / 10 } + fadeOut(tween(200))))
                .apply { targetContentZIndex = -1f }
        else ->
            ((slideInHorizontally(tween(260)) { direction * it / 10 } + fadeIn(tween(200))) togetherWith
                slideOutHorizontally(tween(260)) { -direction * it / 32 })
                .apply { targetContentZIndex = 1f }
    }
}
