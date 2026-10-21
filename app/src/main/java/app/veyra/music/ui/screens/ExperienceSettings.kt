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
import app.veyra.music.data.settings.*
import app.veyra.music.ui.icons.SettingsIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun ExperienceSetting() {
    var open by rememberSaveable {mutableStateOf(false)}
    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.experience_title),subtitle=stringResource(R.string.experience_subtitle),onClick={open=true})
    if(!open)return
    val options by AppSettings.experience.collectAsStateWithLifecycle()
    ModalBottomSheet(onDismissRequest={open=false},sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        Column(Modifier.fillMaxWidth().heightIn(max=660.dp).verticalScroll(rememberScrollState()).padding(horizontal=20.dp).padding(bottom=32.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.experience_title),style=MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.experience_subtitle),style=MaterialTheme.typography.bodyMedium)
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.immersive_mode),checked=options.immersive,onClick={AppSettings.setExperience(options.copy(immersive=!options.immersive))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.experience_data_saver),subtitle=stringResource(R.string.experience_data_saver_hint),checked=options.dataSaver,onClick={AppSettings.setExperience(options.copy(dataSaver=!options.dataSaver))})
            val preloadChoices=listOf(stringResource(R.string.preload_opening),stringResource(R.string.preload_next),stringResource(R.string.preload_three))
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.preload_title),subtitle=preloadChoices[options.preload.ordinal],options=preloadChoices,selectedIndex=options.preload.ordinal,onSelect={AppSettings.setExperience(options.copy(preload=PreloadPolicy.entries[it]))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.preload_cellular),checked=options.preloadCellular,onClick={AppSettings.setExperience(options.copy(preloadCellular=!options.preloadCellular))})
            val countries=remember {listOf("")+java.util.Locale.getISOCountries().sortedBy {java.util.Locale("",it).displayCountry}}
            val countryLabels=countries.map {if(it.isBlank())stringResource(R.string.content_country_auto) else java.util.Locale("",it).displayCountry}
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.content_country),subtitle=countryLabels[countries.indexOf(options.contentCountry).coerceAtLeast(0)],options=countryLabels,selectedIndex=countries.indexOf(options.contentCountry).coerceAtLeast(0),onSelect={AppSettings.setExperience(options.copy(contentCountry=countries[it]))})
            val transportChoices=listOf("Follow theme","Glyphs","Rings","Filled")
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.transport_appearance),subtitle=transportChoices[options.transportAppearance.ordinal],options=transportChoices,selectedIndex=options.transportAppearance.ordinal,onSelect={AppSettings.setExperience(options.copy(transportAppearance=TransportAppearance.entries[it]))})
            val scrubberChoices=listOf("Follow theme","Hairline","Bold","Thumb")
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.scrubber_appearance),subtitle=scrubberChoices[options.scrubberAppearance.ordinal],options=scrubberChoices,selectedIndex=options.scrubberAppearance.ordinal,onSelect={AppSettings.setExperience(options.copy(scrubberAppearance=ScrubberAppearance.entries[it]))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.output_device_visibility),checked=options.showOutputDevice,onClick={AppSettings.setExperience(options.copy(showOutputDevice=!options.showOutputDevice))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.artwork_pause_shrink),checked=options.shrinkArtworkOnPause,onClick={AppSettings.setExperience(options.copy(shrinkArtworkOnPause=!options.shrinkArtworkOnPause))})
            val miniDesigns=listOf("Pill","Card","Slim")
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.mini_player_design),subtitle=miniDesigns[options.miniPlayerDesign.ordinal],options=miniDesigns,selectedIndex=options.miniPlayerDesign.ordinal,onSelect={AppSettings.setExperience(options.copy(miniPlayerDesign=MiniPlayerDesign.entries[it]))})
            val miniProgress=listOf("Off","Line","Fill")
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.mini_player_progress),subtitle=miniProgress[options.miniPlayerProgress.ordinal],options=miniProgress,selectedIndex=options.miniPlayerProgress.ordinal,onSelect={AppSettings.setExperience(options.copy(miniPlayerProgress=MiniPlayerProgress.entries[it]))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.mini_player_skip),checked=options.miniSkipButtons,onClick={AppSettings.setExperience(options.copy(miniSkipButtons=!options.miniSkipButtons))})
            val seekChoices=listOf(0,5,10,15,20)
            val seekLabels=seekChoices.map {if(it==0)stringResource(R.string.experience_off) else stringResource(R.string.experience_seconds,it)}
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.experience_double_tap),subtitle=seekLabels[seekChoices.indexOf(options.doubleTapSeekSeconds).coerceAtLeast(0)],options=seekLabels,selectedIndex=seekChoices.indexOf(options.doubleTapSeekSeconds).coerceAtLeast(0),onSelect={AppSettings.setExperience(options.copy(doubleTapSeekSeconds=seekChoices[it]))})
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.experience_hold_seek),subtitle=seekLabels[seekChoices.indexOf(options.holdSeekSeconds).coerceAtLeast(0)],options=seekLabels,selectedIndex=seekChoices.indexOf(options.holdSeekSeconds).coerceAtLeast(0),onSelect={AppSettings.setExperience(options.copy(holdSeekSeconds=seekChoices[it]))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.experience_track_length),subtitle=stringResource(R.string.experience_track_length_hint),checked=options.showTrackLength,onClick={AppSettings.setExperience(options.copy(showTrackLength=!options.showTrackLength))})
        }
        }
    }
}
