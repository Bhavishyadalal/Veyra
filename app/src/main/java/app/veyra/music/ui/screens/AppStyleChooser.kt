package app.veyra.music.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.ui.icons.SettingsIcons
import app.veyra.music.ui.styles.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppStyleSetting() {
    val selected by AppSettings.appStyle.collectAsStateWithLifecycle()
    var choosing by rememberSaveable { mutableStateOf(false) }
    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.app_style),subtitle=selected.title,onClick={choosing=true})
    if(!choosing)return
    var draftName by rememberSaveable {mutableStateOf(selected.name)}
    val draft=AppStyle.fromStoredName(draftName)
    ModalBottomSheet(onDismissRequest={choosing=false},sheetState=rememberModalBottomSheetState(skipPartiallyExpanded=true)) {
        Column(Modifier.fillMaxWidth().heightIn(max=600.dp).padding(horizontal=20.dp).padding(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.app_style),style=MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.app_style_hint),style=MaterialTheme.typography.bodyMedium)
            LazyColumn(Modifier.weight(1f,false).selectableGroup()) {
                items(AppStyle.entries,key={it.name}) {style->
                    val colors=if(style.custom)style.colorScheme() else MaterialTheme.colorScheme
                    Surface(color=colors.surface,contentColor=colors.onSurface,
                        shape=androidx.compose.foundation.shape.RoundedCornerShape(12.dp),modifier=Modifier.padding(vertical=5.dp)) {
                        Row(Modifier.fillMaxWidth().selectable(style==draft,role=Role.RadioButton,onClick={draftName=style.name})
                            .heightIn(min=76.dp).padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
                            RadioButton(selected=style==draft,onClick=null,colors=RadioButtonDefaults.colors(selectedColor=colors.primary,unselectedColor=colors.onSurfaceVariant))
                            Column(Modifier.weight(1f).padding(start=12.dp)) {
                                Text(style.title,style=MaterialTheme.typography.titleMedium)
                                Text(stringResource(when(style){AppStyle.ORIGINAL->R.string.style_original_hint;AppStyle.MUSIC_EDITORIAL->R.string.style_editorial_hint;AppStyle.LISTENING_ROOM->R.string.style_room_hint;AppStyle.AFTER_DARK->R.string.style_night_hint}),
                                    style=MaterialTheme.typography.bodySmall,color=colors.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End) {
                TextButton(onClick={choosing=false}) {Text(stringResource(android.R.string.cancel))}
                Button(onClick={AppSettings.setAppStyle(draft);choosing=false}) {Text(stringResource(R.string.style_apply))}
            }
        }
    }
}
