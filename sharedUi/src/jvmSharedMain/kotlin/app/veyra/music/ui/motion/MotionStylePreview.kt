package app.veyra.music.ui.motion

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.veyra.music.data.settings.*

/** Runs only when tapped; static during scrolling and while the app is idle. */
@Composable fun MotionStylePreview(style:AppMotionStyle,tempo:MotionTempo,reduced:Boolean,label:String) {
    var page by remember {mutableIntStateOf(0)}
    val rtl=LocalLayoutDirection.current==LayoutDirection.Rtl
    OutlinedButton(onClick={page++},modifier=Modifier.fillMaxWidth()) {
        AnimatedContent(targetState=page,transitionSpec={
            if(reduced)EnterTransition.None togetherWith ExitTransition.None else appMotionTransition(style,tempo,false,rtl)
        },label="motionSpecimen") {value->
            Text(if(value%2==0)"◀  $label" else "$label  ▶",Modifier.padding(8.dp))
        }
    }
}
