package app.veyra.music.ui.motion

import androidx.compose.animation.*
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import app.veyra.music.data.settings.AppMotionStyle
import app.veyra.music.data.settings.MotionTempo

/** A dismissed overlay has no incoming page: animate the outgoing panel, never an empty slot. */
fun pageOverlayTransition(opening: Boolean, reduced: Boolean, rtl: Boolean, subtle: Boolean = false,
    style: AppMotionStyle = AppMotionStyle.DEFAULT, tempo: MotionTempo = MotionTempo.BALANCED,
    materialStyle: String = "classic"): ContentTransform {
    val profile = if (style != AppMotionStyle.DEFAULT) style else when (materialStyle) {
        "botanical", "punk_sticker" -> AppMotionStyle.RISE
        "deco", "punk_gel", "punk_chrome" -> AppMotionStyle.SOFT_ZOOM
        else -> AppMotionStyle.SILK
    }
    val transform = when {
        reduced -> EnterTransition.None togetherWith ExitTransition.None
        !opening -> (EnterTransition.None togetherWith
            if (subtle) fadeOut(tween(VeyraMotion.Feedback)) else appMotionPanelExit(profile, tempo, rtl))
            .apply { targetContentZIndex = -1f }
        subtle -> (fadeIn(tween(VeyraMotion.Feedback)) togetherWith
            fadeOut(tween(1, delayMillis = VeyraMotion.Feedback))).apply { targetContentZIndex = 1f }
        else -> appMotionTransition(profile, tempo, false, rtl)
    }
    return ContentTransform(transform.targetContentEnter, transform.initialContentExit,
        transform.targetContentZIndex, SizeTransform(clip = false) { _, _ -> snap() })
}
