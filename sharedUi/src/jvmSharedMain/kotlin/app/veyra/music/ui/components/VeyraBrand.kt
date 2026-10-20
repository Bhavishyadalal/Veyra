package app.veyra.music.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.veyra.music.sharedui.resources.Res
import app.veyra.music.sharedui.resources.veyra_brand_icon
import org.jetbrains.compose.resources.painterResource

/** Native resource fallback; downloaded branding stays inside the same accessible layout. */
@Composable
fun VeyraBrand(
    modifier: Modifier = Modifier,
    prominent: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onBackground,
    reveal: () -> Float = { 1f },
) {
    VeyraBrandContent(fallbackIcon=painterResource(Res.drawable.veyra_brand_icon),
        modifier=modifier,prominent=prominent,color=color,reveal=reveal)
}
