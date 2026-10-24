package app.veyra.music.ui

import app.veyra.music.data.settings.AppMotionStyle
import app.veyra.music.data.settings.MotionTempo
import app.veyra.music.ui.motion.appMotionTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
fun navigationTransition(motion: NavigationMotion, reduceMotion: Boolean, rtl: Boolean, subtleMotion: Boolean = false, sketch: Boolean = false, materialStyle: String = "classic", style: AppMotionStyle = AppMotionStyle.DEFAULT, tempo: MotionTempo = MotionTempo.BALANCED): ContentTransform {
    val direction = if (rtl) -1 else 1
    fun timing(base:Int)=(base * when(tempo) {MotionTempo.RELAXED->1.25f;MotionTempo.QUICK->.75f;else->1f}).toInt()
    val transition = when {
        reduceMotion || motion == NavigationMotion.None -> EnterTransition.None togetherWith ExitTransition.None
        subtleMotion -> (fadeIn(tween(timing(VeyraMotion.Feedback))) togetherWith fadeOut(tween(1,delayMillis=timing(VeyraMotion.Feedback)))).apply { targetContentZIndex = 1f }
        style != AppMotionStyle.DEFAULT -> appMotionTransition(style, tempo, motion == NavigationMotion.Back, rtl)
        materialStyle != "classic" && motion != NavigationMotion.Tab -> {
            val back = motion == NavigationMotion.Back
            val duration = timing(when(materialStyle) { "telemetry" -> 170; "botanical" -> 280; "flame" -> 210; "punk_rivet" -> 180; "punk_gel", "punk_sticker" -> 280; "punk_torn" -> 200; "punk_chrome" -> 240; else -> 230 })
            val enter = when(materialStyle) {
                "botanical", "punk_sticker" -> slideInVertically(tween(duration, easing=VeyraMotion.Easing)) { it/35 }
                "deco", "flame", "punk_gel", "punk_chrome" -> scaleIn(tween(duration,easing=VeyraMotion.Easing),initialScale=if(back)1.015f else .975f)
                else -> slideInHorizontally(tween(duration,easing=VeyraMotion.Easing)) { direction*it/(if(back)-18 else 18) }
            }
            ((enter+fadeIn(tween(duration))) togetherWith fadeOut(tween(1,delayMillis=duration))).apply {targetContentZIndex=1f}
        }
        sketch -> {
            val back = motion == NavigationMotion.Back
            ((slideInHorizontally(tween(timing(240), easing = VeyraMotion.Easing)) { direction * it / (if (back) -22 else 14) } +
                fadeIn(tween(timing(180)))) togetherWith fadeOut(tween(1,delayMillis=timing(240))))
                .apply { targetContentZIndex = 1f }
        }
        // Keep the parent stationary: moving two artwork/blur-backed pages at
        // once adds rendering work and makes text appear to swim on slow frames.
        motion == NavigationMotion.Tab ->
            (fadeIn(tween(timing(VeyraMotion.Feedback))) togetherWith
                fadeOut(tween(1, delayMillis = timing(VeyraMotion.Feedback)))).apply { targetContentZIndex = 1f }
        motion == NavigationMotion.Back ->
            (EnterTransition.None togetherWith
                (slideOutHorizontally(tween(timing(VeyraMotion.Exit), easing = VeyraMotion.Easing)) { direction * it / 10 } +
                    fadeOut(tween(timing(VeyraMotion.Exit), easing = VeyraMotion.Easing))))
                .apply { targetContentZIndex = -1f }
        else ->
            ((slideInHorizontally(tween(timing(VeyraMotion.Enter), easing = VeyraMotion.Easing)) { direction * it / 10 } +
                fadeIn(tween(timing(VeyraMotion.Enter), easing = VeyraMotion.Easing))) togetherWith
                fadeOut(tween(1, delayMillis = timing(VeyraMotion.Enter))))
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
