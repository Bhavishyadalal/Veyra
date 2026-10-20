package app.veyra.music.ui.themes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

/** Native, growing text over an edge-to-edge preview, shared with the rendering harness. */
@Composable
fun ThemeStudioArtwork(name: String, model: Any?, badge: String, modifier: Modifier = Modifier) {
    val shape=RoundedCornerShape(24.dp)
    Box(modifier.fillMaxWidth().heightIn(min=224.dp).clip(shape)
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .border(1.dp,MaterialTheme.colorScheme.outlineVariant.copy(alpha=.5f),shape)) {
        Image(rememberAsyncImagePainter(model),null,Modifier.matchParentSize(),contentScale=ContentScale.Crop)
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(
            Color.Black.copy(alpha=.1f),Color.Black.copy(alpha=.15f),Color.Black.copy(alpha=.92f)))))
        Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text(badge,style=MaterialTheme.typography.labelMedium,color=Color.White,
                modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha=.72f))
                    .padding(horizontal=10.dp,vertical=6.dp))
            Spacer(Modifier.height(76.dp))
            Text(name,style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold,color=Color.White,
                modifier=Modifier.semantics { heading() })
        }
    }
}

@Composable
fun ThemeStudioCollection(title: String, description: String, modifier: Modifier = Modifier) {
    val c=MaterialTheme.colorScheme
    val shape=RoundedCornerShape(24.dp)
    Column(modifier.fillMaxWidth().clip(shape).background(Brush.linearGradient(listOf(
        c.primaryContainer,c.surfaceContainerHigh,c.surface)))
        .border(1.dp,c.outlineVariant.copy(alpha=.5f),shape).padding(22.dp),
        verticalArrangement=Arrangement.spacedBy(10.dp)) {
        Text(title,style=MaterialTheme.typography.headlineMedium,color=c.onSurface,
            modifier=Modifier.semantics { heading() })
        Text(description,style=MaterialTheme.typography.bodyMedium,color=c.onSurfaceVariant)
    }
}
