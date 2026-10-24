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
            ScrollPlayerMotion.SOFT_FADE -> tween(0)
            ScrollPlayerMotion.GLIDE -> tween(340)
            ScrollPlayerMotion.LIFT -> tween(300)
            else -> spring(dampingRatio=1f,stiffness=500f)
        }
    }}
}

internal fun scrollPlayerTransition(motion:ScrollPlayerMotion,collapsing:Boolean,rtl:Boolean):ContentTransform {
    val direction=(if(collapsing)1 else -1)*(if(rtl)-1 else 1)
    return when(motion) {
        ScrollPlayerMotion.GLIDE -> (slideInHorizontally(tween(340)){it/8*direction}+fadeIn(tween(180))) togetherWith
            (slideOutHorizontally(tween(280)){-it/8*direction}+fadeOut(tween(140)))
        ScrollPlayerMotion.LIFT -> (slideInVertically(tween(300)){if(collapsing)it/4 else -it/4}+fadeIn(tween(160))) togetherWith
            (slideOutVertically(tween(220)){if(collapsing)-it/4 else it/4}+fadeOut(tween(120)))
        ScrollPlayerMotion.ELASTIC -> (scaleIn(tween(280),initialScale=.92f)+fadeIn(tween(160))) togetherWith
            (scaleOut(tween(180),targetScale=.96f)+fadeOut(tween(120)))
        ScrollPlayerMotion.SOFT_FADE -> fadeIn(tween(260)) togetherWith fadeOut(tween(220))
        else -> fadeIn(tween(160)) togetherWith fadeOut(tween(160))
    }
}
