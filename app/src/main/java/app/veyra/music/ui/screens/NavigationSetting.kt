package app.veyra.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.*
import app.veyra.music.ui.icons.SettingsIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun NavigationSetting() {
    var open by rememberSaveable {mutableStateOf(false)}
    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.navigation_custom_title),subtitle=stringResource(R.string.navigation_custom_hint),onClick={open=true})
    if(!open)return
    val options by AppSettings.experience.collectAsStateWithLifecycle()
    val scrollAnimations by AppSettings.scrollPlayerAnimations.collectAsStateWithLifecycle()
    val labels=listOf(R.string.home,R.string.search,R.string.explore,R.string.library,R.string.local_music,R.string.replay,R.string.settings).map {stringResource(it)}
    ModalBottomSheet(onDismissRequest={open=false},sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)) {
        app.veyra.music.ui.themes.ThemeComponentSurface {
        Column(Modifier.fillMaxWidth().heightIn(max=680.dp).verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.navigation_custom_title),style=MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.navigation_studio_hint),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            val designChoices=listOf(R.string.navigation_floating,R.string.navigation_docked,R.string.navigation_minimal,R.string.navigation_capsule,R.string.navigation_ribbon,R.string.navigation_orbit,R.string.navigation_monorail,R.string.navigation_harbor,R.string.navigation_ticket,R.string.navigation_pebble,R.string.navigation_prism,R.string.navigation_bracket,R.string.navigation_halo,R.string.navigation_stitch,R.string.navigation_pixel,R.string.navigation_lozenge).map {stringResource(it)}
            BoxWithConstraints {
                val columns=if(maxWidth>=340.dp && androidx.compose.ui.platform.LocalDensity.current.fontScale<1.4f)2 else 1
                Column(verticalArrangement=Arrangement.spacedBy(10.dp)) {
                    NavigationDesign.entries.chunked(columns).forEach {row->
                        Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                            row.forEach {design->app.veyra.music.ui.themes.NavigationStylePreview(design,designChoices[design.ordinal],options.navigationDesign==design,
                                {AppSettings.setExperience(options.copy(navigationDesign=design))},Modifier.weight(1f))}
                        }
                    }
                }
            }
            AppMotionSetting()
            ExperienceSection(stringResource(R.string.scroll_player_animations)) {
                SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.scroll_player_animations),subtitle=stringResource(R.string.scroll_player_animations_hint),checked=scrollAnimations,onClick={AppSettings.setScrollPlayerAnimations(!scrollAnimations)})
                ScrollPlayerMotionSetting()
            }
            ExperienceSection(stringResource(R.string.experience_nav_destinations)) {
            options.navigation.forEachIndexed {index,target->
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                    Text(labels[target],Modifier.weight(1f))
                    TextButton(enabled=index>0,onClick={val next=options.navigation.toMutableList();val previous=next[index-1];next[index-1]=target;next[index]=previous;AppSettings.setExperience(options.copy(navigation=next))}) {Text(stringResource(R.string.navigation_move_up))}
                    TextButton(enabled=target!=0 && options.navigation.size>2,onClick={AppSettings.setExperience(options.copy(navigation=options.navigation-target))}) {Text(stringResource(R.string.navigation_hide))}
                }
            }
            labels.indices.filter {it !in options.navigation}.forEach {target->
                OutlinedButton(enabled=options.navigation.size<5,onClick={AppSettings.setExperience(options.copy(navigation=options.navigation+target))}) {Text(stringResource(R.string.navigation_add,labels[target]))}
            }
            }
            ExperienceSection(stringResource(R.string.experience_nav_behavior)) {
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.navigation_start),subtitle=labels[options.startupTab],options=labels,selectedIndex=options.startupTab,onSelect={AppSettings.setExperience(options.copy(startupTab=it))})
            val labelChoices=listOf(R.string.navigation_labels_always,R.string.navigation_labels_selected,R.string.navigation_labels_hidden).map {stringResource(it)}
            SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.navigation_labels),subtitle=labelChoices[options.navigationLabels.ordinal],options=labelChoices,selectedIndex=options.navigationLabels.ordinal,onSelect={AppSettings.setExperience(options.copy(navigationLabels=NavigationLabels.entries[it]))})
            SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.navigation_search_keyboard),checked=options.searchKeyboard,onClick={AppSettings.setExperience(options.copy(searchKeyboard=!options.searchKeyboard))})
            }
            TextButton(onClick={AppSettings.setExperience(options.copy(navigation=listOf(0,1,2,3),startupTab=0,navigationLabels=NavigationLabels.ALWAYS,navigationDesign=NavigationDesign.FLOATING,scrollPlayerMotion=ScrollPlayerMotion.FLOW))}) {Text(stringResource(R.string.navigation_reset))}
        }
        }
    }
}

/** Also offered beside the original master switch, so the new choices are easy to find. */
@Composable internal fun ScrollPlayerMotionSetting() {
    val options by AppSettings.experience.collectAsStateWithLifecycle()
    val names=listOf(R.string.scroll_motion_flow,R.string.scroll_motion_glide,R.string.scroll_motion_lift,R.string.scroll_motion_fade,R.string.scroll_motion_elastic,R.string.scroll_motion_zephyr,R.string.scroll_motion_cascade,R.string.scroll_motion_focus,R.string.scroll_motion_snap,R.string.scroll_motion_bloom,R.string.scroll_motion_sweep,R.string.scroll_motion_float,R.string.scroll_motion_compress,R.string.scroll_motion_unfold,R.string.scroll_motion_settle).map {stringResource(it)}
    SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.scroll_player_motion),subtitle=names[options.scrollPlayerMotion.ordinal],
        options=names,selectedIndex=options.scrollPlayerMotion.ordinal,onSelect={AppSettings.setExperience(options.copy(scrollPlayerMotion=ScrollPlayerMotion.entries[it]))})
}

/** Independent app-wide choice. Theme motion remains available through Theme default. */
@Composable internal fun AppMotionSetting() {
    val options by AppSettings.experience.collectAsStateWithLifecycle()
    val names=listOf(R.string.app_motion_default,R.string.app_motion_silk,R.string.app_motion_soft_zoom,R.string.app_motion_rise,R.string.app_motion_drift,R.string.app_motion_reveal,R.string.app_motion_quick_cut,R.string.app_motion_velvet,R.string.app_motion_spring,R.string.app_motion_diagonal,R.string.app_motion_shutter).map {stringResource(it)}
    val tempos=listOf(R.string.motion_tempo_relaxed,R.string.motion_tempo_balanced,R.string.motion_tempo_quick).map {stringResource(it)}
    ExperienceSection(stringResource(R.string.app_motion_title)) {
        Text(stringResource(R.string.app_motion_hint),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.app_motion_style),subtitle=names[options.appMotionStyle.ordinal],options=names,selectedIndex=options.appMotionStyle.ordinal,onSelect={AppSettings.setExperience(options.copy(appMotionStyle=AppMotionStyle.entries[it]))})
        SettingsPresetRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.motion_tempo_title),subtitle=tempos[options.motionTempo.ordinal],options=tempos,selectedIndex=options.motionTempo.ordinal,onSelect={AppSettings.setExperience(options.copy(motionTempo=MotionTempo.entries[it]))})
        app.veyra.music.ui.motion.MotionStylePreview(options.appMotionStyle,options.motionTempo,app.veyra.music.ui.motion.rememberVeyraReducedMotion(),stringResource(R.string.motion_preview))
    }
}
