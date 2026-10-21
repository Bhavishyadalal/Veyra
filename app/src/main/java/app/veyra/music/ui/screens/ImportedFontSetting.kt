package app.veyra.music.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.settings.ImportedFonts
import app.veyra.music.ui.icons.SettingsIcons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun ImportedFontSetting() {
    var open by rememberSaveable {mutableStateOf(false)}
    var slot by rememberSaveable {mutableStateOf("app")}
    var busy by remember {mutableStateOf(false)}
    var message by remember {mutableStateOf<String?>(null)}
    val scope=rememberCoroutineScope()
    val context=LocalContext.current
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {uri->
        if(uri!=null)scope.launch {
            busy=true
            try {
                val hash=ImportedFonts.import(context,uri)
                val latest=AppSettings.experience.value
                AppSettings.setExperience(if(slot=="app")latest.copy(appFont=hash) else latest.copy(lyricsFont=hash))
                message=context.getString(R.string.font_import_success)
            } catch(cancelled:kotlinx.coroutines.CancellationException) {throw cancelled}
            catch(error:Exception) {message=error.message ?: context.getString(R.string.font_import_error)}
            finally {busy=false}
        }
    }
    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.font_import_title),subtitle=stringResource(R.string.font_import_hint),onClick={open=true})
    if(!open)return
    val options by AppSettings.experience.collectAsStateWithLifecycle()
    ModalBottomSheet(onDismissRequest={if(!busy)open=false}) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        Column(Modifier.fillMaxWidth().heightIn(max=680.dp).verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Text(stringResource(R.string.font_import_title),style=MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.font_import_hint),style=MaterialTheme.typography.bodyMedium)
            listOf("app" to R.string.font_import_app,"lyrics" to R.string.font_import_lyrics).forEach {(key,label)->
                ExperienceSection(stringResource(label)) {
                OutlinedButton(enabled=!busy,onClick={slot=key;picker.launch(arrayOf("font/*","application/x-font-ttf","application/x-font-opentype","application/octet-stream"))},modifier=Modifier.fillMaxWidth()) {Text(stringResource(label))}
                val hash=if(key=="app")options.appFont else options.lyricsFont
                if(hash.isNotBlank())TextButton(enabled=!busy,onClick={val latest=AppSettings.experience.value;AppSettings.setExperience(if(key=="app")latest.copy(appFont="") else latest.copy(lyricsFont=""))}) {Text(stringResource(R.string.font_import_reset,stringResource(label)))}
            }
            }
            ExperienceSection(stringResource(R.string.font_import_lyrics)) {
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.font_lyrics_size),subtitle="${(options.lyricsScale*100).toInt()}%",options=listOf("80%","100%","120%","150%"),selectedIndex=listOf(.8f,1f,1.2f,1.5f).indexOf(options.lyricsScale).coerceAtLeast(0),onSelect={AppSettings.setExperience(options.copy(lyricsScale=listOf(.8f,1f,1.2f,1.5f)[it]))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.font_lyrics_center),checked=options.centeredLyrics,onClick={AppSettings.setExperience(options.copy(centeredLyrics=!options.centeredLyrics))})
            }
            if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
            message?.let {Text(it,style=MaterialTheme.typography.bodyMedium)}
        }
        }
    }
}
