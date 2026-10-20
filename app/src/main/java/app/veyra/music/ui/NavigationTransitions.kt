package app.veyra.music.ui

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.snap
import app.veyra.music.ui.motion.VeyraMotion
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
fun navigationTransition(motion: NavigationMotion, reduceMotion: Boolean, rtl: Boolean, subtleMotion: Boolean = false): ContentTransform {
    val direction = if (rtl) -1 else 1
    val transition = when {
        reduceMotion || motion == NavigationMotion.None -> EnterTransition.None togetherWith ExitTransition.None
        subtleMotion -> (fadeIn(tween(VeyraMotion.Feedback)) togetherWith fadeOut(tween(VeyraMotion.Press))).apply { targetContentZIndex = 1f }
        motion == NavigationMotion.Tab ->
            (fadeIn(tween(VeyraMotion.Feedback)) togetherWith ExitTransition.None).apply { targetContentZIndex = 1f }
        motion == NavigationMotion.Back ->
            (slideInHorizontally(tween(VeyraMotion.Enter, easing = VeyraMotion.Easing)) { -direction * it / 60 } togetherWith
                (slideOutHorizontally(tween(VeyraMotion.Enter, easing = VeyraMotion.Easing)) { direction * it / 20 } + fadeOut(tween(VeyraMotion.Exit))))
                .apply { targetContentZIndex = -1f }
        else ->
            ((slideInHorizontally(tween(VeyraMotion.Enter, easing = VeyraMotion.Easing)) { direction * it / 20 } + fadeIn(tween(VeyraMotion.Exit))) togetherWith
                slideOutHorizontally(tween(VeyraMotion.Enter, easing = VeyraMotion.Easing)) { -direction * it / 60 })
                .apply { targetContentZIndex = 1f }
    }
    // Destinations own their scroll state and fill the viewport. Interpolating
    // their measured sizes would remeasure full lists during navigation.
    return ContentTransform(
        targetContentEnter = transition.targetContentEnter,
        initialContentExit = transition.initialContentExit,
        targetContentZIndex = transition.targetContentZIndex,
        sizeTransform = SizeTransform(clip = false) { _, _ -> snap() },
    )
}
