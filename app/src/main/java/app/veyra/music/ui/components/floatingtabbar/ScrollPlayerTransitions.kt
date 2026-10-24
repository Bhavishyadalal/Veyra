package app.veyra.music.ui.components.floatingtabbar

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import app.veyra.music.data.settings.ScrollPlayerMotion

internal val LocalScrollPlayerMotion=staticCompositionLocalOf {ScrollPlayerMotion.FLOW}

/** Finite transforms only; no ticking collectors or extra backdrop recordings. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable internal fun rememberScrollBoundsTransform():BoundsTransform {
    val motion=LocalScrollPlayerMotion.current
    return remember(motion) {BoundsTransform {_,_->
        when(motion) {
            ScrollPlayerMotion.ELASTIC -> spring(dampingRatio=.78f,stiffness=300f)
            ScrollPlayerMotion.SETTLE -> spring(dampingRatio=1f,stiffness=220f)
            ScrollPlayerMotion.SOFT_FADE -> tween(0)
            ScrollPlayerMotion.SNAP -> tween(140)
            ScrollPlayerMotion.ZEPHYR -> tween(420)
            ScrollPlayerMotion.CASCADE -> tween(360)
            ScrollPlayerMotion.FOCUS -> tween(280)
            ScrollPlayerMotion.BLOOM -> spring(dampingRatio=.9f,stiffness=350f)
            ScrollPlayerMotion.SWEEP -> tween(300)
            ScrollPlayerMotion.FLOAT -> tween(380)
            ScrollPlayerMotion.COMPRESS -> tween(240)
            ScrollPlayerMotion.UNFOLD -> tween(320)
            ScrollPlayerMotion.GLIDE -> tween(340)
            ScrollPlayerMotion.LIFT -> tween(300)
            else -> spring(dampingRatio=1f,stiffness=500f)
        }
    }}
}

internal fun scrollPlayerTransition(motion:ScrollPlayerMotion,collapsing:Boolean,rtl:Boolean):ContentTransform {
    val direction=(if(collapsing)1 else -1)*(if(rtl)-1 else 1)
    val vertical=if(collapsing)1 else -1
    return when(motion) {
        ScrollPlayerMotion.GLIDE -> (slideInHorizontally(tween(340)){it/8*direction}+fadeIn(tween(180))) togetherWith
            (slideOutHorizontally(tween(280)){-it/8*direction}+fadeOut(tween(140)))
        ScrollPlayerMotion.LIFT -> (slideInVertically(tween(300)){vertical*it/4}+fadeIn(tween(160))) togetherWith
            (slideOutVertically(tween(220)){-vertical*it/4}+fadeOut(tween(120)))
        ScrollPlayerMotion.ELASTIC -> (scaleIn(tween(280),initialScale=.92f)+fadeIn(tween(160))) togetherWith
            (scaleOut(tween(180),targetScale=.96f)+fadeOut(tween(120)))
        ScrollPlayerMotion.SOFT_FADE -> fadeIn(tween(260)) togetherWith fadeOut(tween(220))
        ScrollPlayerMotion.ZEPHYR -> (slideInHorizontally(tween(420)){it/24*direction}+fadeIn(tween(300))) togetherWith fadeOut(tween(200))
        ScrollPlayerMotion.CASCADE -> (slideInVertically(tween(360)){vertical*it/2}+fadeIn(tween(200))) togetherWith fadeOut(tween(120))
        ScrollPlayerMotion.FOCUS -> (scaleIn(tween(280),initialScale=1.045f)+fadeIn(tween(220))) togetherWith fadeOut(tween(160))
        ScrollPlayerMotion.SNAP -> fadeIn(tween(100)) togetherWith fadeOut(tween(80))
        ScrollPlayerMotion.BLOOM -> (scaleIn(tween(300),initialScale=.88f)+fadeIn(tween(180))) togetherWith (scaleOut(tween(160),targetScale=.92f)+fadeOut(tween(100)))
        ScrollPlayerMotion.SWEEP -> (slideInHorizontally(tween(300)){-it/5*direction}+fadeIn(tween(180))) togetherWith fadeOut(tween(100))
        ScrollPlayerMotion.FLOAT -> (slideInVertically(tween(380)){-vertical*it/5}+scaleIn(tween(380),initialScale=.98f)+fadeIn(tween(220))) togetherWith fadeOut(tween(180))
        ScrollPlayerMotion.COMPRESS -> (scaleIn(tween(240),initialScale=.95f)+fadeIn(tween(150))) togetherWith (scaleOut(tween(180),targetScale=1.035f)+fadeOut(tween(120)))
        ScrollPlayerMotion.UNFOLD -> (slideInHorizontally(tween(320)){it/18*direction}+slideInVertically(tween(320)){vertical*it/5}+fadeIn(tween(180))) togetherWith fadeOut(tween(120))
        ScrollPlayerMotion.SETTLE -> (scaleIn(tween(400),initialScale=.985f)+fadeIn(tween(320))) togetherWith fadeOut(tween(220))
        else -> fadeIn(tween(160)) togetherWith fadeOut(tween(160))
    }
}
