package app.veyra.music.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.veyra.music.ui.themes.ThemeIcon
import coil3.compose.AsyncImage

/** A refresh retries failed artwork without discarding successful Coil memory/disk entries. */
val LocalHomeArtworkRevision = staticCompositionLocalOf {0}

/** Designed loading/missing/error fallback beneath the image; no per-tile subcomposition or timers. */
@Composable fun HomeArtwork(model: Any?,contentDescription: String?=null,modifier: Modifier=Modifier,contentScale: ContentScale=ContentScale.Crop) {
    val colors=MaterialTheme.colorScheme
    Box(modifier.background(Brush.linearGradient(listOf(colors.surfaceVariant,colors.surface))),contentAlignment=Alignment.Center) {
        ThemeIcon(Icons.Rounded.MusicNote,null,Modifier.size(28.dp),colors.onSurfaceVariant.copy(alpha=.45f),"music")
        key(model,LocalHomeArtworkRevision.current) {
            AsyncImage(model,contentDescription,Modifier.matchParentSize(),contentScale=contentScale)
        }
    }
}
