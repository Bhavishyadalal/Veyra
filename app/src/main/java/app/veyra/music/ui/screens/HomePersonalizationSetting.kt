package app.veyra.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.ui.icons.SettingsIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun HomePersonalizationSetting() {
    var open by rememberSaveable {mutableStateOf(false)}
    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.home_personalize),subtitle=stringResource(R.string.home_personalize_hint),onClick={open=true})
    if(!open)return
    val options by AppSettings.experience.collectAsStateWithLifecycle()
    ModalBottomSheet(onDismissRequest={open=false},sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        Column(Modifier.fillMaxWidth().heightIn(max=650.dp).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.home_personalize),style=MaterialTheme.typography.headlineSmall)
            ExperienceSection(stringResource(R.string.experience_home_layout)) {
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.pin_home_header),subtitle=stringResource(R.string.pin_home_header_hint),checked=options.pinHomeHeader,onClick={val latest=AppSettings.experience.value;AppSettings.setExperience(latest.copy(pinHomeHeader=!latest.pinHomeHeader))})
            OutlinedTextField(value=options.homeGreeting,onValueChange={AppSettings.setExperience(options.copy(homeGreeting=it.take(100)))},label={Text(stringResource(R.string.home_greeting))},singleLine=true,modifier=Modifier.fillMaxWidth())
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.home_speed_dial),checked=options.homeSpeedDial,onClick={AppSettings.setExperience(options.copy(homeSpeedDial=!options.homeSpeedDial))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.home_quick_radio),checked=options.quickPicksRadio,onClick={AppSettings.setExperience(options.copy(quickPicksRadio=!options.quickPicksRadio))})
            }
            ExperienceSection(stringResource(R.string.home_shortcuts)) {
            stringArrayResource(R.array.experience_shortcuts).forEachIndexed {index,label->
                SettingsRow(icon=SettingsIcons.Appearance,title=label,checked=index in options.homeShortcuts,enabled=index in options.homeShortcuts || options.homeShortcuts.size<6,
                    onClick={AppSettings.setExperience(options.copy(homeShortcuts=if(index in options.homeShortcuts)options.homeShortcuts-index else options.homeShortcuts+index))})
            }
            }
        }
        }
    }
}
