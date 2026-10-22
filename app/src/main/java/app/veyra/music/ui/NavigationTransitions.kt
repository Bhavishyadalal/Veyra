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
fun navigationTransition(motion: NavigationMotion, reduceMotion: Boolean, rtl: Boolean, subtleMotion: Boolean = false, sketch: Boolean = false): ContentTransform {
    val direction = if (rtl) -1 else 1
    val transition = when {
        reduceMotion || motion == NavigationMotion.None -> EnterTransition.None togetherWith ExitTransition.None
        subtleMotion -> (fadeIn(tween(VeyraMotion.Feedback)) togetherWith fadeOut(tween(VeyraMotion.Press))).apply { targetContentZIndex = 1f }
        sketch -> {
            val back = motion == NavigationMotion.Back
            ((slideInHorizontally(tween(240, easing = VeyraMotion.Easing)) { direction * it / (if (back) -22 else 14) } +
                fadeIn(tween(180))) togetherWith fadeOut(tween(160)))
                .apply { targetContentZIndex = 1f }
        }
        // Keep the parent stationary: moving two artwork/blur-backed pages at
        // once adds rendering work and makes text appear to swim on slow frames.
        motion == NavigationMotion.Tab ->
            (fadeIn(tween(VeyraMotion.Feedback)) togetherWith
                fadeOut(tween(1, delayMillis = VeyraMotion.Feedback))).apply { targetContentZIndex = 1f }
        motion == NavigationMotion.Back ->
            (EnterTransition.None togetherWith
                (slideOutHorizontally(tween(VeyraMotion.Exit, easing = VeyraMotion.Easing)) { direction * it / 10 } +
                    fadeOut(tween(VeyraMotion.Exit, easing = VeyraMotion.Easing))))
                .apply { targetContentZIndex = -1f }
        else ->
            ((slideInHorizontally(tween(VeyraMotion.Enter, easing = VeyraMotion.Easing)) { direction * it / 10 } +
                fadeIn(tween(VeyraMotion.Enter, easing = VeyraMotion.Easing))) togetherWith
                fadeOut(tween(1, delayMillis = VeyraMotion.Enter)))
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
