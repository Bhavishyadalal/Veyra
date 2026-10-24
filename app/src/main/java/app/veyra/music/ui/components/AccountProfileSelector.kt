package app.veyra.music.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.TransformOrigin
import app.veyra.music.ui.motion.VeyraMotion
import app.veyra.music.ui.motion.rememberVeyraReducedMotion
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import app.veyra.music.R
import app.veyra.music.auth.GoogleAccountSession
import app.veyra.music.auth.YouTubeProfile
import app.veyra.music.data.settings.AppSettings
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

/** Shared compact selector for every visible YouTube account avatar. */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun AccountProfileSelector(
    accounts: List<GoogleAccountSession>,
    activeAccountId: String?,
    activeProfileId: String?,
    hazeState: HazeState,
    onSelect: (GoogleAccountSession, YouTubeProfile) -> Unit,
    onAddAccount: () -> Unit,
    onRemoveAccount: (GoogleAccountSession) -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberVeyraReducedMotion()
    val motion=app.veyra.music.ui.motion.rememberAppMotionPreferences()
    val custom=motion.first!=app.veyra.music.data.settings.AppMotionStyle.DEFAULT
    val rtl=androidx.compose.ui.platform.LocalLayoutDirection.current==androidx.compose.ui.unit.LayoutDirection.Rtl
    val visibility = remember { MutableTransitionState(false) }
    var afterClose by remember { mutableStateOf<(() -> Unit)?>(null) }
    // Retain the panel until its exit finishes. Back, scrim and actions share
    // this path, including taps during entry and zero-duration system motion.
    fun close(action: () -> Unit) {
        if (afterClose == null) {
            afterClose = action
            visibility.targetState = false
        }
    }
    LaunchedEffect(Unit) { visibility.targetState = true }
    LaunchedEffect(visibility.isIdle, visibility.currentState, afterClose) {
        if (visibility.isIdle && !visibility.currentState) afterClose?.invoke()
    }
    BackHandler { close(onDismiss) }
    val origin = TransformOrigin(if (androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl) 0f else 1f, 0f)
    val enter = VeyraMotion.duration(reducedMotion, app.veyra.music.ui.motion.appMotionDuration(motion.first,motion.second,VeyraMotion.Enter))
    val exit = VeyraMotion.duration(reducedMotion, app.veyra.music.ui.motion.appMotionDuration(motion.first,motion.second,VeyraMotion.Exit))
    var managing by remember { mutableStateOf(false) }
    val shape = MaterialTheme.shapes.extraLarge
    AnimatedVisibility(
        visibleState = visibility,
        enter = fadeIn(tween(enter, easing = VeyraMotion.Easing)),
        exit = fadeOut(tween(exit, easing = VeyraMotion.Easing)),
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = .48f))
                .clickable { close(onDismiss) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                color = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = shape,
                modifier = Modifier
                    .animateEnterExit(
                        enter = if(custom && !reducedMotion)app.veyra.music.ui.motion.appMotionTransition(motion.first,motion.second,false,rtl).targetContentEnter else slideInVertically(tween(enter, easing = VeyraMotion.Easing)) { if (reducedMotion) 0 else -it / 12 } +
                            scaleIn(tween(enter, easing = VeyraMotion.Easing), initialScale = if (reducedMotion) 1f else .94f,
                                transformOrigin = origin),
                        exit = if(custom && !reducedMotion)app.veyra.music.ui.motion.appMotionPanelExit(motion.first,motion.second,rtl) else slideOutVertically(tween(exit, easing = VeyraMotion.Easing)) { if (reducedMotion) 0 else -it / 16 } +
                            scaleOut(tween(exit, easing = VeyraMotion.Easing), targetScale = if (reducedMotion) 1f else .97f,
                                transformOrigin = origin),
                    )
                    .padding(top = 56.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .clip(shape)
                    // A stable tonal surface avoids sampling the full moving page
                    // into a second blur layer on every transition frame.
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable(onClick = {}),
            ) {
                LazyColumn {
                    item { Text(stringResource(R.string.switch_account), style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(20.dp, 18.dp, 20.dp, 8.dp)) }
                    accounts.forEach { account ->
                    item {
                        Text(account.email.ifBlank { account.name.ifBlank { stringResource(R.string.accounts) } },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp, 12.dp, 20.dp, 4.dp))
                    }
                    items(account.profiles.size) { index ->
                        val profile = account.profiles[index]
                        ProfileRow(profile, account.accountId == activeAccountId && profile.profileId == activeProfileId,
                            managing, { close { onSelect(account, profile); onDismiss() } }, { close { onRemoveAccount(account) } })
                    }
                    }
                    item { SelectorAction(Icons.Rounded.Add, stringResource(R.string.add_account), { close(onAddAccount) }) }
                    item { SelectorAction(Icons.Rounded.ManageAccounts, stringResource(R.string.manage_accounts)) { managing = !managing } }
                    item { SelectorAction(Icons.Rounded.Settings, stringResource(R.string.settings), { close(onOpenSettings) }) }
                }
            }
        }
    }
}

@Composable private fun ProfileRow(profile: YouTubeProfile, selected: Boolean, managing: Boolean,
    onClick: () -> Unit, onRemove: () -> Unit) {
    val description = stringResource(if (selected) R.string.selected_account else R.string.switch_account)
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(role = Role.RadioButton, onClick = if (managing) onRemove else onClick)
        .semantics { contentDescription = description }
        .padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (profile.avatar != null) AsyncImage(profile.avatar, null, Modifier.size(38.dp).clip(MaterialTheme.shapes.extraLarge))
        else Icon(Icons.Rounded.Person, null, Modifier.size(38.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) {
            Text(profile.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(profile.handle.ifBlank { stringResource(if (profile.isBrandAccount) R.string.brand_account else R.string.personal) },
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (selected && !managing) Icon(Icons.Rounded.Check, stringResource(R.string.selected_account), tint = MaterialTheme.colorScheme.primary)
        if (managing) Text(stringResource(R.string.sign_out), color = MaterialTheme.colorScheme.error)
    }
}

@Composable private fun SelectorAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).clickable(role = Role.Button, onClick = onClick)
        .semantics { contentDescription = label }.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null); Spacer(Modifier.width(16.dp)); Text(label)
    }
}
