package app.veyra.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.themes.AppearanceFont
import app.veyra.music.ui.icons.SettingsIcons
import app.veyra.music.ui.theme.appearanceTypography
import app.veyra.music.ui.theme.defaultAppearanceTypography
import app.veyra.music.ui.themes.LocalThemeVisuals
import app.veyra.music.ui.themes.ThemeFontPreview

@Composable
private fun AppearanceFont.label(): String = stringResource(when(this) {
    AppearanceFont.FOLLOW_THEME -> R.string.font_follow_theme
    AppearanceFont.VEYRA_DEFAULT -> R.string.font_veyra_default
    AppearanceFont.MANROPE -> R.string.font_manrope
    AppearanceFont.FREDOKA -> R.string.font_fredoka
    AppearanceFont.SPACE_GROTESK -> R.string.font_space_grotesk
    AppearanceFont.RAJDHANI -> R.string.font_rajdhani
    AppearanceFont.BARLOW_CONDENSED -> R.string.font_barlow_condensed
    AppearanceFont.GRENZE_GOTISCH -> R.string.font_grenze_gotisch
})

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FontSetting() {
    val style by AppSettings.appStyle.collectAsStateWithLifecycle()
    val selected by AppSettings.appearanceFont.collectAsStateWithLifecycle()
    val stronger by AppSettings.strongerText.collectAsStateWithLifecycle()
    var choosing by rememberSaveable {mutableStateOf(false)}
    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.appearance_fonts),
        subtitle=if(style.custom)stringResource(R.string.style_original_only) else selected.label(),onClick={choosing=true})
    if(style.custom) {
        if(choosing) AlertDialog(onDismissRequest={choosing=false},text={Text(stringResource(R.string.style_themes_suspended))},
            confirmButton={TextButton(onClick={choosing=false}) {Text(stringResource(android.R.string.ok))}})
        return
    }
    if(!choosing) return
    var draftName by rememberSaveable {mutableStateOf(selected.name)}
    val draft=AppearanceFont.fromStoredName(draftName)
    val pack=LocalThemeVisuals.current
    val preview=remember(draft,pack,stronger) {
        val base=defaultAppearanceTypography(stronger)
        runCatching {appearanceTypography(base,draft,pack)}.getOrDefault(base)
    }
    ModalBottomSheet(onDismissRequest={choosing=false},
        sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true),
        containerColor=MaterialTheme.colorScheme.background) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        Column(Modifier.fillMaxWidth().heightIn(max=LocalConfiguration.current.screenHeightDp.dp*.85f)
            .padding(horizontal=20.dp).padding(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                Text(stringResource(R.string.appearance_fonts),Modifier.weight(1f),style=MaterialTheme.typography.titleLarge)
                TextButton(onClick={choosing=false}) {Text(stringResource(android.R.string.cancel))}
            }
            ThemeFontPreview(stringResource(R.string.font_preview_heading),stringResource(R.string.font_preview_body),preview)
            LazyColumn(Modifier.fillMaxWidth().weight(1f,fill=false).selectableGroup()) {
                items(AppearanceFont.entries,key={it.name}) { font ->
                    app.veyra.music.ui.themes.ThemeFontChoice(font.label(),draft==font) {draftName=font.name}
                }
                item {Text(stringResource(R.string.font_selection_hint),style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.padding(vertical=8.dp))}
            }
            Button(onClick={AppSettings.setAppearanceFont(draft);choosing=false},modifier=Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.font_apply))
            }
        }
        }
    }
}
