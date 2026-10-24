package app.veyra.music.ui.motion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import app.veyra.music.data.settings.AppMotionStyle
import app.veyra.music.data.settings.MotionTempo

/** Small, finite page transforms; incoming content moves while the previous page stays still. */
fun appMotionTransition(style: AppMotionStyle, tempo: MotionTempo, back: Boolean, rtl: Boolean): ContentTransform {
    val direction = (if (back) -1 else 1) * (if (rtl) -1 else 1)
    val base = when (style) {
        AppMotionStyle.QUICK_CUT -> 100
        AppMotionStyle.VELVET -> 360
        AppMotionStyle.SPRING -> 320
        AppMotionStyle.REVEAL, AppMotionStyle.SHUTTER -> 280
        else -> 240
    }
    val duration = (base * when (tempo) { MotionTempo.RELAXED -> 1.25f; MotionTempo.QUICK -> .75f; else -> 1f }).toInt()
    val spec = tween<Float>(duration, easing = VeyraMotion.Easing)
    val vertical = tween<androidx.compose.ui.unit.IntOffset>(duration, easing = VeyraMotion.Easing)
    val enter = when (style) {
        AppMotionStyle.SOFT_ZOOM -> scaleIn(spec, initialScale = if(back)1.025f else .965f)
        AppMotionStyle.RISE -> slideInVertically(vertical) { it / 12 * if(back)-1 else 1 }
        AppMotionStyle.DRIFT -> slideInHorizontally(vertical) { it / 7 * direction }
        AppMotionStyle.REVEAL -> slideInVertically(vertical) {-it / 24} + scaleIn(spec, initialScale = .99f)
        AppMotionStyle.VELVET -> scaleIn(spec, initialScale = .985f)
        AppMotionStyle.SPRING -> scaleIn(spring(dampingRatio = .86f, stiffness = 380f), initialScale = .955f)
        AppMotionStyle.DIAGONAL -> slideInHorizontally(vertical) {it / 22 * direction} + slideInVertically(vertical) {it / 28}
        AppMotionStyle.SHUTTER -> slideInHorizontally(vertical) {-it / 24 * direction} + scaleIn(spec, initialScale = 1.015f)
        AppMotionStyle.SILK, AppMotionStyle.DEFAULT -> slideInHorizontally(vertical) {it / 32 * direction}
        AppMotionStyle.QUICK_CUT -> EnterTransition.None
    }
    return ((enter + fadeIn(tween(duration))) togetherWith fadeOut(tween(1, delayMillis = duration)))
        .apply {targetContentZIndex = 1f}
}

fun appMotionDuration(style:AppMotionStyle,tempo:MotionTempo,base:Int=240):Int {
    val factor=when(style) {AppMotionStyle.QUICK_CUT->.5f;AppMotionStyle.VELVET->1.5f;AppMotionStyle.SPRING->1.2f;else->1f}
    val pace=when(tempo) {MotionTempo.RELAXED->1.25f;MotionTempo.QUICK->.75f;else->1f}
    return (base*factor*pace).toInt().coerceIn(60,600)
}

/** Mirrors panel entry without the retained-page delay used for full destinations. */
fun appMotionPanelExit(style:AppMotionStyle,tempo:MotionTempo,rtl:Boolean):ExitTransition {
    val duration=appMotionDuration(style,tempo,200)
    val spec=tween<Float>(duration,easing=VeyraMotion.Easing)
    val move=tween<androidx.compose.ui.unit.IntOffset>(duration,easing=VeyraMotion.Easing)
    val direction=if(rtl)-1 else 1
    val exit=when(style) {
        AppMotionStyle.RISE,AppMotionStyle.REVEAL -> slideOutVertically(move){-it/12}
        AppMotionStyle.DRIFT,AppMotionStyle.SHUTTER -> slideOutHorizontally(move){it/12*direction}
        AppMotionStyle.DIAGONAL -> slideOutHorizontally(move){it/22*direction}+slideOutVertically(move){-it/28}
        AppMotionStyle.SPRING -> scaleOut(spec,targetScale=.955f)
        AppMotionStyle.SOFT_ZOOM -> scaleOut(spec,targetScale=.965f)
        AppMotionStyle.VELVET -> scaleOut(spec,targetScale=.985f)
        AppMotionStyle.SILK -> slideOutVertically(move){-it/32}
        else -> ExitTransition.None
    }
    return exit+fadeOut(spec)
}
