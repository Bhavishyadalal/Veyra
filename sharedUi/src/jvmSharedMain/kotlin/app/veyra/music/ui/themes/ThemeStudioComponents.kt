package app.veyra.music.ui.themes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
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
import coil3.compose.AsyncImage

/** Uses the same Typography as the active preset, without changing app settings. */
@Composable
fun ThemeFontChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().selectable(selected=selected,role=androidx.compose.ui.semantics.Role.RadioButton,
        onClick=onClick).heightIn(min=48.dp).padding(vertical=4.dp),verticalAlignment=Alignment.CenterVertically) {
        androidx.compose.material3.RadioButton(selected=selected,onClick=null)
        Spacer(Modifier.width(10.dp))
        Text(label,Modifier.weight(1f),style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ThemeFontPreview(heading: String, body: String, typography: androidx.compose.material3.Typography) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerLow).padding(14.dp),
        verticalArrangement=Arrangement.spacedBy(6.dp)) {
        Text(heading,style=typography.headlineSmall.copy(textDirection=androidx.compose.ui.text.style.TextDirection.Content),color=MaterialTheme.colorScheme.onSurface)
        Text(body,style=typography.bodyMedium.copy(textDirection=androidx.compose.ui.text.style.TextDirection.Content),color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Native, growing text over an edge-to-edge preview, shared with the rendering harness. */
@Composable
fun ThemeStudioArtwork(name: String, model: Any?, badge: String, modifier: Modifier = Modifier) {
    val shape=RoundedCornerShape(24.dp)
    Box(modifier.fillMaxWidth().heightIn(min=144.dp).clip(shape)
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .border(1.dp,MaterialTheme.colorScheme.outlineVariant.copy(alpha=.5f),shape)) {
        AsyncImage(model,null,Modifier.matchParentSize(),contentScale=ContentScale.Crop)
        Box(Modifier.matchParentSize().background(Brush.verticalGradient(listOf(
            Color.Black.copy(alpha=.1f),Color.Black.copy(alpha=.15f),Color.Black.copy(alpha=.92f)))))
        Column(Modifier.fillMaxWidth().padding(20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Text(badge,style=MaterialTheme.typography.labelMedium,color=Color.White,
                modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha=.72f))
                    .padding(horizontal=10.dp,vertical=6.dp))
            Spacer(Modifier.height(28.dp))
            Text(name,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold,color=Color.White,
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

/** Compact collection entry; the full preview and actions live on its detail page. */
@Composable
fun ThemeStudioRow(name: String, subtitle: String, model: Any?, active: Boolean,
    onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val c=MaterialTheme.colorScheme
    androidx.compose.foundation.layout.Row(modifier.fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(if(active)c.primaryContainer.copy(alpha=.3f) else c.surfaceContainerLow)
        .clickable(enabled=enabled,role=androidx.compose.ui.semantics.Role.Button,onClick=onClick)
        .heightIn(min=88.dp).padding(10.dp),verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Box(Modifier.width(88.dp).height(72.dp).clip(RoundedCornerShape(12.dp)).background(c.surfaceVariant),contentAlignment=Alignment.Center) {
            // Visible while cold/offline decoding fails; cache-hit requests supply
            // their previous pixels immediately instead of fading through a blank.
            if(model!=null) Text("♪",style=MaterialTheme.typography.titleLarge,color=c.onSurfaceVariant)
            if(model!=null) AsyncImage(model,null,Modifier.matchParentSize(),contentScale=ContentScale.Crop)
            else Text("V",style=MaterialTheme.typography.titleLarge,color=c.primary)
        }
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)) {
            Text(name,style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold,color=c.onSurface)
            Text(subtitle,style=MaterialTheme.typography.bodySmall,color=c.onSurfaceVariant)
        }
        if(active) ThemeIcon(androidx.compose.material.icons.Icons.Rounded.Check,null,tint=c.primary,modifier=Modifier.size(20.dp))
        else ThemeIcon(androidx.compose.material.icons.Icons.AutoMirrored.Rounded.ArrowForward,null,tint=c.onSurfaceVariant,modifier=Modifier.size(20.dp))
    }
}

/** Compact radio tile with natural text height and a single accessible selection target. */
@Composable
fun ThemeIconChoice(label: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit,
    modifier: Modifier = Modifier, icon: @Composable () -> Unit) {
    val c=MaterialTheme.colorScheme
    val shape=RoundedCornerShape(16.dp)
    Column(modifier.fillMaxWidth().clip(shape)
        .background(if(selected)c.primaryContainer else c.surfaceContainerLow)
        .border(if(selected)1.5.dp else .5.dp,if(selected)c.primary else c.outlineVariant.copy(alpha=.35f),shape)
        .selectable(selected,enabled=enabled,role=androidx.compose.ui.semantics.Role.RadioButton,onClick=onClick)
        .heightIn(min=108.dp).padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally,
        verticalArrangement=Arrangement.spacedBy(8.dp)) {
        Box(Modifier.fillMaxWidth(),contentAlignment=Alignment.Center) {
            Box(Modifier.size(64.dp),contentAlignment=Alignment.Center) { icon() }
            if(selected) ThemeIcon(androidx.compose.material.icons.Icons.Rounded.Check,null,
                tint=c.primary,modifier=Modifier.align(Alignment.TopEnd).size(16.dp))
        }
        Text(label,style=MaterialTheme.typography.labelLarge,color=if(selected)c.onPrimaryContainer else c.onSurface,
            textAlign=androidx.compose.ui.text.style.TextAlign.Center)
    }
}

/** Apply choices grow with font scaling and expose exactly one checkbox target per row. */
@Composable
fun ThemeApplyOptions(
    hint: String,
    refreshLabel: String, refresh: Boolean, onRefresh: (Boolean) -> Unit,
    iconLabel: String?, matchingIcon: Boolean, onMatchingIcon: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(hint, style = MaterialTheme.typography.bodyMedium)
        @Composable fun Option(label: String, checked: Boolean, change: (Boolean) -> Unit) {
            Row(Modifier.fillMaxWidth()
                .then(Modifier.toggleableOption(checked, enabled, change))
                .heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Checkbox(checked, onCheckedChange = null, enabled = enabled)
                Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (iconLabel != null) Option(iconLabel, matchingIcon, onMatchingIcon)
        Option(refreshLabel, refresh, onRefresh)
    }
}

private fun Modifier.toggleableOption(checked: Boolean, enabled: Boolean, change: (Boolean) -> Unit): Modifier =
    this.then(Modifier.toggleable(checked, enabled = enabled, role = androidx.compose.ui.semantics.Role.Checkbox, onValueChange = change))
