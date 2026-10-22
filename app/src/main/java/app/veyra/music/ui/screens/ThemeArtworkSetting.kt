package app.veyra.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.ui.icons.SettingsIcons
import app.veyra.music.ui.themes.ThemeArtworkOptions
import app.veyra.music.ui.themes.ThemeComponentSurface

/** Changes are saved at the end of a slider gesture, never on each pointer event. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeArtworkSetting() {
    var open by rememberSaveable { mutableStateOf(false) }
    val style by AppSettings.appStyle.collectAsStateWithLifecycle()
    val active by app.veyra.music.data.themes.ThemeRepository.active.collectAsStateWithLifecycle()
    val enabled = !style.custom && active != null
    SettingsRow(icon = SettingsIcons.Appearance, title = stringResource(R.string.theme_artwork_controls),
        subtitle = stringResource(if (style.custom) R.string.style_original_only else if (active == null) R.string.theme_artwork_choose_pack else R.string.theme_artwork_controls_hint),
        enabled = enabled, onClick = { open = true })
    if (!open || !enabled) return
    val options by AppSettings.themeArtwork.collectAsStateWithLifecycle()
    ModalBottomSheet(onDismissRequest = { open = false }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        ThemeComponentSurface {
            Column(Modifier.fillMaxWidth().heightIn(max = 650.dp).verticalScroll(rememberScrollState())
                .padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.theme_artwork_controls), style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(R.string.theme_artwork_controls_hint), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                ExperienceSection(stringResource(R.string.theme_artwork_scene)) {
                    SliderRow(SettingsIcons.Appearance, stringResource(R.string.theme_artwork_visibility), "",
                        options.visibility, { AppSettings.setThemeArtwork(AppSettings.themeArtwork.value.copy(visibility = it)) },
                        0f..1f, 0, formatValue = { "${(it * 100).toInt()}%" })
                    SliderRow(SettingsIcons.Appearance, stringResource(R.string.theme_artwork_dim), "",
                        options.dim, { AppSettings.setThemeArtwork(AppSettings.themeArtwork.value.copy(dim = it)) },
                        0f..0.6f, 0, formatValue = { "${(it * 100).toInt()}%" })
                    if (android.os.Build.VERSION.SDK_INT >= 31) SliderRow(SettingsIcons.Appearance, stringResource(R.string.theme_artwork_blur), "",
                        options.blur, { AppSettings.setThemeArtwork(AppSettings.themeArtwork.value.copy(blur = it)) },
                        0f..16f, 0, formatValue = { "${it.toInt()}" })
                }
                ExperienceSection(stringResource(R.string.theme_artwork_surfaces)) {
                    SliderRow(SettingsIcons.Appearance, stringResource(R.string.theme_artwork_opacity), "",
                        options.controlOpacity, { AppSettings.setThemeArtwork(AppSettings.themeArtwork.value.copy(controlOpacity = it)) },
                        .55f..1f, 0, formatValue = { "${(it * 100).toInt()}%" })
                }
                Text(stringResource(R.string.theme_artwork_readability), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { AppSettings.setThemeArtwork(ThemeArtworkOptions()) }) { Text(stringResource(R.string.theme_artwork_reset)) }
                    Button(onClick = { open = false }) { Text(stringResource(R.string.theme_artwork_done)) }
                }
            }
        }
    }
}
