package app.veyra.music.ui.screens

import app.veyra.music.ui.customization.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.graphics.Color
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import app.veyra.music.ui.motion.VeyraMotion
import app.veyra.music.ui.motion.rememberVeyraReducedMotion
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BlurOff
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material.icons.rounded.MotionPhotosOff
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material.icons.rounded.WebAssetOff
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.LocalMediaRepository
import app.veyra.music.data.NerdStats
import app.veyra.music.data.listentogether.ListenTogether
import app.veyra.music.data.lyrics.translationLanguageName
import app.veyra.music.data.model.Account
import app.veyra.music.data.scrobbling.LastFM
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.data.settings.AudioQuality
import app.veyra.music.data.settings.AutomixPerformanceMode
import app.veyra.music.data.settings.DownloadQuality
import app.veyra.music.data.settings.OutputPcmMode
import app.veyra.music.data.settings.ThemeMode
import app.veyra.music.data.sources.DeviceCodecs
import app.veyra.music.data.stats.Backup
import app.veyra.music.playback.AudioCache
import app.veyra.music.playback.AudioOutputStatus
import app.veyra.music.ui.components.MessageState
import app.veyra.music.ui.components.SearchField
import app.veyra.music.ui.components.isGlassSupported
import app.veyra.music.ui.components.languageDisplayNameRes
import app.veyra.music.ui.components.thumbnailBorder
import app.veyra.music.ui.icons.SettingsIcons
import app.veyra.music.ui.icons.VeyraIcons
import app.veyra.music.ui.performance.resolvePerformanceRefreshRate
import app.veyra.music.ui.performance.supportedPerformanceRefreshRates
import app.veyra.music.ui.player.fullBleedArtworkAvailable
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch



/** Searchable settings with category navigation and shared, accessible controls. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun SettingsScreen(
    /** The window's width, for the gates that depend on it. */
    windowWidth: Dp,
    navigationState: SettingsNavigationState,
    signedIn: Boolean,
    account: Account?,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onAccountScrobbling: () -> Unit,
    onEqualizer: () -> Unit,
    onOpenReplay: () -> Unit,
    onLyricsSources: () -> Unit,
    onTranslationLanguage: () -> Unit,
    onSources: () -> Unit,
    onListenTogether: () -> Unit,
    onSpotifyCanvasAuth: () -> Unit,
    onAppLanguage: () -> Unit,
    onEditSignatureApi: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val textResources = androidx.compose.ui.platform.LocalResources.current

    val wifiQuality by AppSettings.audioQualityWifi.collectAsStateWithLifecycle()
    val cellularQuality by AppSettings.audioQualityCellular.collectAsStateWithLifecycle()
    val metered by AppSettings.meteredConnection.collectAsStateWithLifecycle()
    val crossfade by AppSettings.crossfadeSeconds.collectAsStateWithLifecycle()
    val smartFade by AppSettings.smartFadeEnabled.collectAsStateWithLifecycle()
    val automixPerformance by AppSettings.automixPerformanceMode.collectAsStateWithLifecycle()
    val skipSilence by AppSettings.skipSilence.collectAsStateWithLifecycle()
    val dolbyAtmos by AppSettings.dolbyAtmos.collectAsStateWithLifecycle()
    // A property of the hardware, so it is read once rather than remembered
    // against a key that can never change — see [DeviceCodecs.playsDolbyAtmos],
    // which caches the codec-list walk for the life of the process.
    val dolbyAtmosSupported = DeviceCodecs.playsDolbyAtmos
    val spatialAudio by AppSettings.spatialAudio.collectAsStateWithLifecycle()
    val nerdStats by AppSettings.showNerdStats.collectAsStateWithLifecycle()
    val accentPalette by AppSettings.accentPalette.collectAsStateWithLifecycle()
    val artworkCorners by AppSettings.artworkCorners.collectAsStateWithLifecycle()
    val listSpacing by AppSettings.listSpacing.collectAsStateWithLifecycle()
    val subtleMotion by AppSettings.subtleMotion.collectAsStateWithLifecycle()
    val playerShortcut by AppSettings.playerShortcut.collectAsStateWithLifecycle()
    var resetCustomization by rememberSaveable { mutableStateOf<String?>(null) }
    val strongerText by AppSettings.strongerText.collectAsStateWithLifecycle()
    val contrastPlayerControls by AppSettings.contrastPlayerControls.collectAsStateWithLifecycle()
    val reduceAnimation by AppSettings.reduceAnimation.collectAsStateWithLifecycle()
    val reduceDynamicBlur by AppSettings.reduceDynamicBlur.collectAsStateWithLifecycle()
    val liquidGlass by AppSettings.liquidGlass.collectAsStateWithLifecycle()
    val scrollPlayerAnimations by AppSettings.scrollPlayerAnimations.collectAsStateWithLifecycle()
    val liquidGlassSupported = isGlassSupported()
    val lyricsBlur by AppSettings.lyricsBlur.collectAsStateWithLifecycle()
    val animatedCanvas by AppSettings.animatedCanvas.collectAsStateWithLifecycle()
    val canvasOverCellular by AppSettings.canvasOverCellular.collectAsStateWithLifecycle()
    val signatureApiEnabled by AppSettings.signatureApiEnabled.collectAsStateWithLifecycle()
    val signatureApiUrl by AppSettings.signatureApiUrl.collectAsStateWithLifecycle()
    val fullBleedArtwork by AppSettings.fullBleedArtwork.collectAsStateWithLifecycle()
    val legacyMeshGradient by AppSettings.legacyMeshGradient.collectAsStateWithLifecycle()
    val syncedLyrics by AppSettings.syncedLyrics.collectAsStateWithLifecycle()
    val lyricsSources by AppSettings.lyricsSources.collectAsStateWithLifecycle()
    val translationLanguage by AppSettings.translationLanguage.collectAsStateWithLifecycle()
    val theme by AppSettings.themeMode.collectAsStateWithLifecycle()
    val sessionId by AppSettings.audioSessionId.collectAsStateWithLifecycle()
    val outputPcmMode by AppSettings.outputPcmMode.collectAsStateWithLifecycle()
    val preferUsbDac by AppSettings.preferUsbDac.collectAsStateWithLifecycle()
    val loudnessNormalization by AppSettings.loudnessNormalization.collectAsStateWithLifecycle()
    val loudnessOffOnSpeaker by AppSettings.loudnessOffOnSpeaker.collectAsStateWithLifecycle()
    val outputStatus by AudioOutputStatus.current.collectAsStateWithLifecycle()
    val playingDolbyAtmos by remember {
        NerdStats.current.map { it?.isDolbyAtmos == true }.distinctUntilChanged()
    }.collectAsStateWithLifecycle(initialValue = remember { NerdStats.current.value?.isDolbyAtmos == true })
    val cacheLimitBytes by AppSettings.audioCacheLimitBytes.collectAsStateWithLifecycle()
    val showCacheFolder by AppSettings.showCacheFolder.collectAsStateWithLifecycle()
    val downloadQuality by AppSettings.downloadQuality.collectAsStateWithLifecycle()
    val wifiOnlyDownloads by AppSettings.wifiOnlyDownloads.collectAsStateWithLifecycle()
    val exportDownloads by AppSettings.exportDownloads.collectAsStateWithLifecycle()
    val stopOnTaskRemoved by AppSettings.stopOnTaskRemoved.collectAsStateWithLifecycle()
    val hideVolumeBar by AppSettings.hideVolumeBar.collectAsStateWithLifecycle()
    val hideStatusBarInPlayer by AppSettings.hideStatusBarInPlayer.collectAsStateWithLifecycle()
    val hideSongStatus by AppSettings.hideSongStatus.collectAsStateWithLifecycle()
    val showQueueHistory by AppSettings.showQueueHistory.collectAsStateWithLifecycle()
    val swipeToPlayNext by AppSettings.swipeToPlayNext.collectAsStateWithLifecycle()
    val dontRepeatSuggestions by AppSettings.dontRepeatSuggestions.collectAsStateWithLifecycle()
    val preferMusicOnly by AppSettings.preferMusicOnly.collectAsStateWithLifecycle()
    val smartVersionAlignment by AppSettings.smartVersionAlignment.collectAsStateWithLifecycle()
    val filterNonMusicAudio by AppSettings.filterNonMusicAudio.collectAsStateWithLifecycle()
    val localMusicFolderUri by AppSettings.localMusicFolderUri.collectAsStateWithLifecycle()
    val highPerformanceMode by AppSettings.highPerformanceMode.collectAsStateWithLifecycle()
    val performanceRefreshRate by AppSettings.performanceRefreshRate.collectAsStateWithLifecycle()
    val currentDisplay = LocalView.current.display
    val supportedRefreshRates = remember(currentDisplay) {
        currentDisplay.supportedPerformanceRefreshRates()
    }
    val selectedPerformanceRefreshRate = remember(currentDisplay, performanceRefreshRate) {
        currentDisplay.resolvePerformanceRefreshRate(performanceRefreshRate)
    }

    LaunchedEffect(selectedPerformanceRefreshRate, performanceRefreshRate) {
        if (selectedPerformanceRefreshRate != performanceRefreshRate) {
            AppSettings.setPerformanceRefreshRate(selectedPerformanceRefreshRate)
        }
    }

    // Scrobbling states
    val lastfmEnabled by AppSettings.lastfmEnabled.collectAsStateWithLifecycle()
    val lastfmUsername by AppSettings.lastfmUsername.collectAsStateWithLifecycle()
    val lastfmSessionKey by AppSettings.lastfmSessionKey.collectAsStateWithLifecycle()
    val lastfmScrobbleEnabled by AppSettings.lastfmScrobbleEnabled.collectAsStateWithLifecycle()
    val lastfmNowPlayingEnabled by AppSettings.lastfmNowPlaying.collectAsStateWithLifecycle()
    val scrobbleMinDuration by AppSettings.scrobbleMinDuration.collectAsStateWithLifecycle()
    val scrobbleDelayPercent by AppSettings.scrobbleDelayPercent.collectAsStateWithLifecycle()
    val scrobbleDelaySeconds by AppSettings.scrobbleDelaySeconds.collectAsStateWithLifecycle()
    val listenBrainzEnabled by AppSettings.listenBrainzEnabled.collectAsStateWithLifecycle()
    val listenBrainzToken by AppSettings.listenBrainzToken.collectAsStateWithLifecycle()

    val replayGenres by AppSettings.replayGenres.collectAsStateWithLifecycle()

    // Read here so the row can say "In a party · ABC123" rather than making
    // somebody open the screen to find out whether they are still in one.
    val party by ListenTogether.state.collectAsStateWithLifecycle()

    // Search spans all categories; an empty query shows the selected category,
    // exactly as if the field weren't there.
    var searchQuery by navigationState::query
    var showAssetGallery by rememberSaveable { mutableStateOf(false) }
    if (showAssetGallery) AssetGallery(onDismiss = { showAssetGallery = false })
    var showThemeGallery by rememberSaveable { mutableStateOf(false) }
    if (showThemeGallery) ThemeGallery(onDismiss = { showThemeGallery = false })
    var picking by remember { mutableStateOf<QualityTarget?>(null) }
    var pickingDownloadQuality by remember { mutableStateOf(false) }
    var pickingAutomixPerformance by remember { mutableStateOf(false) }
    // What the last export or import did, shown on the row that did it rather
    // than as a toast: a backup is the one action here whose outcome nobody can
    // check by looking at the app afterwards. Held per direction, or an import's
    // result reports itself under the word "Export".
    var exportStatus by remember { mutableStateOf<String?>(null) }
    var importStatus by remember { mutableStateOf<String?>(null) }
    var importCandidate by remember {mutableStateOf<Pair<android.net.Uri,Backup.Summary>?>(null)}
    var exporting by remember { mutableStateOf(false) }
    var importing by remember { mutableStateOf(false) }
    var confirmImport by remember { mutableStateOf(false) }
    var confirmClearSongCache by remember { mutableStateOf(false) }
    var showPerformanceWarning by remember { mutableStateOf(false) }
    var showPerformanceConfirmation by remember { mutableStateOf(false) }
    val backupScope = rememberCoroutineScope()

    val batterySettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        showPerformanceConfirmation = true
    }
    val localMusicFolderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { folder ->
        if (folder == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                folder,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
        AppSettings.setLocalMusicFolderUri(folder.toString())
    }

    /**
     * Both halves go through the system document picker rather than a path of
     * this app's own choosing. That is what puts the file somewhere the user can
     * actually find it — Drive, Files, a folder they already back up — and it
     * means neither direction needs a storage permission, since the grant
     * arrives with the document they picked.
     */
    val exportPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { target ->
        if (target == null) return@rememberLauncherForActivityResult
        backupScope.launch {
            exporting = true
            exportStatus = textResources.getString(R.string.settings_backup_working)
            try {
                exportStatus = Backup.exportTo(context, target).fold(
                    onSuccess = { months ->
                        textResources.getString(R.string.export_succeeded, context.countOfMonths(months))
                    },
                    onFailure = {
                        textResources.getString(R.string.export_failed, it.message ?: textResources.getString(R.string.unknown_error))
                    },
                )
            } finally {
                exporting = false
            }
        }
    }
    val importPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { source ->
        if (source == null) return@rememberLauncherForActivityResult
        backupScope.launch {
            importing = true
            importStatus = textResources.getString(R.string.settings_backup_working)
            try {
                Backup.inspect(context,source).fold(
                    onSuccess={importCandidate=source to it;importStatus=null},
                    onFailure={importStatus=textResources.getString(R.string.import_failed,it.message ?: textResources.getString(R.string.unknown_error))})
            } finally {
                importing = false
            }
        }
    }
    var showListenBrainzTokenDialog by remember { mutableStateOf(false) }
    var showLastfmLoginDialog by remember { mutableStateOf(false) }
    val scrobbleScope = rememberCoroutineScope()

    val version = remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    // Made fresh on every pass rather than remembered: it tallies the rows
    // that answered to the query, and that tally has to start from zero each
    // time the query changes.
    var category by navigationState::category
    val searching = searchQuery.isNotBlank()
    val search = SettingsSearch(searchQuery, category)

    val scrollPositions = rememberSaveable(saver = mapSaver<MutableMap<String, Int>>(
        save = { it.toMap() },
        restore = { values -> values.mapValues { (_, value) -> (value as? Int ?: 0).coerceAtLeast(0) }.toMutableMap() },
    )) { mutableMapOf() }
    val scrollKey = if (searching) "search" else category?.name ?: "overview"
    val scrollState = remember(scrollKey) { ScrollState(scrollPositions[scrollKey] ?: 0) }
    androidx.compose.runtime.DisposableEffect(scrollKey, scrollState) {
        onDispose { scrollPositions[scrollKey] = scrollState.value }
    }
    LaunchedEffect(scrollKey, scrollState) {
        snapshotFlow { scrollState.value }.collect { scrollPositions[scrollKey] = it }
    }
    var isFirstSearchComposition by remember { mutableStateOf(true) }
    LaunchedEffect(searchQuery) {
        if (!isFirstSearchComposition && searchQuery.isNotBlank()) scrollState.scrollTo(0)
        isFirstSearchComposition = false
    }

    val reducedMotion = rememberVeyraReducedMotion()
    val appMotion=app.veyra.music.ui.motion.rememberAppMotionPreferences()
    var categoryIntroduced by remember { mutableStateOf(false) }
    val categoryAlpha = remember { Animatable(1f) }
    var categoryDirection by remember { mutableStateOf(1f) }
    val categoryRtl = androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl
    LaunchedEffect(category, reducedMotion) {
        if (reducedMotion || !categoryIntroduced) {
            categoryAlpha.snapTo(1f)
        } else {
            categoryDirection = (if (category == null) -1f else 1f) * (if (categoryRtl) -1f else 1f)
            categoryAlpha.snapTo(0f)
            categoryAlpha.animateTo(1f, tween(app.veyra.music.ui.motion.appMotionDuration(appMotion.first,appMotion.second,VeyraMotion.Section), easing = VeyraMotion.Easing))
        }
        categoryIntroduced = true
    }

    Box(modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
    ) {
        SettingsPageHeader(
            title = stringResource(if (searching) R.string.settings else category?.title ?: R.string.settings),
            icon = if (searching) SettingsIcons.Advanced else category?.icon ?: SettingsIcons.Sound,
        )
        SearchField(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSubmit = {},
            placeholder = stringResource(R.string.settings_search_hint),
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 4.dp),
        )

        Column(
            Modifier.weight(1f).verticalScroll(scrollState)
                .graphicsLayer {
                    alpha = .55f + .45f * categoryAlpha.value
                    translationX = if (reducedMotion) 0f else categoryDirection * (1f - categoryAlpha.value) * 28.dp.toPx()
                },
        ) {
            if (category == null && !searching) {
                SettingsOverview(onSelect = { category = it })
            }

            SearchableSettingsGroup(search, category = SettingsCategory.ACCOUNT, header = stringResource(R.string.account_integrations)) {
                val accountTitle = stringResource(R.string.account_integrations)
                // What the row opens is the scrobbling screen, so the services it
                // signs into are worth typing at this field even though none of
                // them is named on the row itself.
                row(accountTitle, "scrobbling", "last.fm", "listenbrainz") {
                    SettingsRow(
                        icon = SettingsIcons.Account,
                        title = accountTitle,
                        subtitle = account?.email?.takeIf { it.isNotBlank() }
                            ?: stringResource(if (signedIn) R.string.signed_in else R.string.not_signed_in),
                        onClick = onAccountScrobbling,
                    )
                }
                // Sits with the account rather than with Playback: a party is up to
                // five devices, including guests who use an installation pseudonym.
                val listenTogetherTitle = stringResource(R.string.listen_together)
                row(listenTogetherTitle, "jam", "party", "sync", "friends") {
                    SettingsRow(
                        icon = Icons.Rounded.Groups,
                        title = listenTogetherTitle,
                        subtitle = party.code?.let {
                            stringResource(R.string.listen_together_in_party, it)
                        } ?: stringResource(R.string.listen_together_subtitle),
                        badge = party.members.size.takeIf { party.inParty && it > 1 }?.toString(),
                        onClick = onListenTogether,
                    )
                }
            }

            // The row that used to sit at the top of this group was called
            // "Lossless / HQ Audio" and toggled `SourceRegistry.setModuleEnabled` —
            // it switched the *module source* on and off, not lossless. Sources
            // above lists that as the module's own row now. Lossless itself is no
            // longer a setting at all — see
            // [SourceResolver.requestForNow][app.veyra.music.data.sources.SourceResolver.requestForNow].
            SearchableSettingsGroup(search, category = SettingsCategory.SOUND, header = stringResource(R.string.audio_quality)) {
                val sourceTitle = stringResource(R.string.source)
                val sourceSubtitle = stringResource(R.string.sources_subtitle)
                row(sourceTitle, sourceSubtitle, "addon", "lossless") {
                    SettingsRow(
                        icon = Icons.Rounded.Extension,
                        title = sourceTitle,
                        subtitle = sourceSubtitle,
                        onClick = onSources,
                    )
                }
                val onWifiTitle = stringResource(R.string.on_wifi)
                row(onWifiTitle, "wi-fi", "streaming quality") {
                    SettingsRow(
                        icon = Icons.Rounded.Wifi,
                        title = onWifiTitle,
                        badge = stringResource(R.string.in_use).takeIf { metered == false },
                        value = wifiQuality.localizedLabel(),
                        onClick = { picking = QualityTarget.WIFI },
                    )
                }
                val onMobileDataTitle = stringResource(R.string.on_mobile_data)
                row(onMobileDataTitle, "cellular", "streaming quality") {
                    SettingsRow(
                        icon = SettingsIcons.Performance,
                        title = onMobileDataTitle,
                        badge = stringResource(R.string.in_use).takeIf { metered == true },
                        value = cellularQuality.localizedLabel(),
                        onClick = { picking = QualityTarget.CELLULAR },
                    )
                }
                // Sits with the quality ceilings rather than with Playback: it
                // decides which version of a track gets fetched, the same question
                // the two rows above answer, and not how one is played back.
                //
                // Greyed rather than hidden where the device can't decode E-AC-3.
                // A missing row reads as a feature the app doesn't have; a disabled
                // one with a reason under it is the difference between "Veyra
                // has no Atmos" and "this phone has no Dolby decoder", and only the
                // second is true. The stored preference is left untouched either
                // way — see [AppSettings.dolbyAtmos].
                val dolbyAtmosTitle = stringResource(R.string.dolby_atmos)
                row(dolbyAtmosTitle, "surround", "e-ac-3") {
                    SettingsRow(
                        iconPainter = painterResource(R.drawable.ic_dolby_atmos),
                        title = dolbyAtmosTitle,
                        subtitle = stringResource(
                            if (dolbyAtmosSupported) {
                                R.string.dolby_atmos_subtitle
                            } else {
                                R.string.dolby_atmos_unavailable
                            },
                        ),
                        enabled = dolbyAtmosSupported,
                        checked = dolbyAtmos && dolbyAtmosSupported,
                        onClick = { AppSettings.setDolbyAtmos(!dolbyAtmos) },
                    )
                }
            }

            // Its own group rather than rows bolted onto the two above, because a
            // download is not a third kind of connection. The ceilings answer "what
            // does this minute cost"; these answer "what am I keeping, and when may
            // it be fetched" — and those two questions only make sense read
            // together, which is what puts them side by side here.
            SearchableSettingsGroup(search, category = SettingsCategory.LIBRARY, header = stringResource(R.string.downloads)) {
                val downloadQualityTitle = stringResource(R.string.download_quality)
                row(downloadQualityTitle, "offline", "lossless") {
                    SettingsRow(
                        icon = Icons.Rounded.Download,
                        title = downloadQualityTitle,
                        subtitle = stringResource(R.string.download_quality_subtitle, downloadQuality.perTrack),
                        value = downloadQuality.localizedLabel(),
                        onClick = { pickingDownloadQuality = true },
                    )
                }
                // Reads as part of Download quality above it, not as a setting
                // of its own — same treatment as Play animated cover over
                // cellular gets under Animated cover art.
                val downloadWifiOnlyTitle = stringResource(R.string.download_wifi_only)
                row(downloadWifiOnlyTitle, "wi-fi", "cellular", divided = false) {
                    SettingsSubRow(
                        title = downloadWifiOnlyTitle,
                        checked = wifiOnlyDownloads,
                        onCheckedChange = AppSettings::setWifiOnlyDownloads,
                        badge = stringResource(R.string.blocking).takeIf { wifiOnlyDownloads && metered == true },
                    )
                }
                val exportDownloadsTitle = stringResource(R.string.export_compatible_downloads)
                row(exportDownloadsTitle, "music folder", divided = false) {
                    SettingsSubRow(
                        title = exportDownloadsTitle,
                        checked = exportDownloads,
                        onCheckedChange = AppSettings::setExportDownloads,
                        subtitle = "Music/Veyra".takeIf { exportDownloads },
                    )
                }
                row(stringResource(R.string.export_all_downloads), "export", "music folder", divided = false) {
                    app.veyra.music.ui.components.DownloadExportCard()
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.SOUND, header = stringResource(R.string.playback)) {
                val preferMusicOnlyTitle = stringResource(R.string.prefer_music_only)
                row(preferMusicOnlyTitle, "video", "audio", "music video") {
                    SettingsRow(
                        icon = Icons.Rounded.SmartDisplay,
                        title = preferMusicOnlyTitle,
                        subtitle = stringResource(R.string.prefer_music_only_subtitle),
                        checked = preferMusicOnly,
                        onClick = { AppSettings.setPreferMusicOnly(!preferMusicOnly) },
                    )
                }
                val outputPrecisionTitle = stringResource(R.string.output_precision)
                row(outputPrecisionTitle, "pcm", "bit depth", "sample rate", "dac") {
                    SettingsRow(
                        icon = Icons.Rounded.GraphicEq,
                        title = outputPrecisionTitle,
                        subtitle = buildString {
                            append(outputStatus.sink)
                            append(" · ")
                            append(outputStatus.deviceName)
                            (outputStatus.actualSampleRateHz ?: outputStatus.sampleRatesHz.firstOrNull())
                                ?.let { append(" · ${it / 1000.0} kHz") }
                            append(" · ")
                            append(AudioOutputStatus.encodingLabel(outputStatus))
                        },
                    )
                    SegmentedControl(
                        options = OutputPcmMode.entries.map(OutputPcmMode::label),
                        selectedIndex = OutputPcmMode.entries.indexOf(outputPcmMode),
                        onSelect = { AppSettings.setOutputPcmMode(OutputPcmMode.entries[it]) },
                        modifier = Modifier.padding(start = TEXT_INSET, end = ROW_INSET, bottom = 14.dp),
                    )
                }
                val preferUsbDacTitle = stringResource(R.string.prefer_usb_dac)
                row(preferUsbDacTitle, "headphone", "output") {
                    SettingsSubRow(
                        title = preferUsbDacTitle,
                        checked = preferUsbDac,
                        onCheckedChange = AppSettings::setPreferUsbDac,
                        badge = stringResource(R.string.connected).takeIf { outputStatus.isUsb },
                    )
                }
                val loudnessTitle = stringResource(R.string.loudness_normalization)
                row(loudnessTitle, "loudness", "volume", "normalize", "replaygain", "lufs") {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        title = loudnessTitle,
                        subtitle = stringResource(R.string.loudness_normalization_subtitle),
                        checked = loudnessNormalization,
                        onClick = { AppSettings.setLoudnessNormalization(!loudnessNormalization) },
                    )
                }
                val loudnessSpeakerTitle = stringResource(R.string.loudness_off_on_speaker)
                row(loudnessSpeakerTitle, "loudness", "speaker", "normalize") {
                    SettingsSubRow(
                        title = loudnessSpeakerTitle,
                        checked = loudnessOffOnSpeaker,
                        onCheckedChange = AppSettings::setLoudnessOffOnSpeaker,
                    )
                }
                // Automix decides its own length from each pair of tracks —
                // tempo, key, structure — so it replaces the manual slider rather
                // than needing it set to anything first.
                if (!smartFade) {
                    val crossfadeTitle = stringResource(R.string.crossfade)
                    val crossfadeOff = stringResource(R.string.off)
                    row(crossfadeTitle, "fade", "gapless", reveal = true, reducedMotion = reducedMotion) {
                        SliderRow(
                            icon = Icons.Rounded.Waves,
                            title = crossfadeTitle,
                            subtitle = stringResource(R.string.crossfade_subtitle),
                            value = if (crossfade == 0) stringResource(R.string.off) else "${crossfade}s",
                            sliderValue = crossfade.toFloat(),
                            formatValue = { if (it.roundToInt() == 0) crossfadeOff else "${it.roundToInt()}s" },
                            onSliderValue = { AppSettings.setCrossfadeSeconds(it.roundToInt()) },
                            valueRange = 0f..12f,
                            steps = 11,
                        )
                    }
                }
                val automixTitle = stringResource(R.string.automix)
                row(automixTitle, "crossfade", "smart fade", "mix") {
                    SettingsRow(
                        icon = Icons.Rounded.AutoAwesome,
                        title = automixTitle,
                        subtitle = if (smartFade) {
                            stringResource(R.string.automix_enabled_subtitle)
                        } else {
                            stringResource(R.string.automix_disabled_subtitle)
                        },
                        checked = smartFade,
                        onClick = { AppSettings.setSmartFadeEnabled(!smartFade) },
                    )
                }
                val automixPerformanceTitle = stringResource(R.string.automix_performance)
                row(automixPerformanceTitle, "cpu", "battery") {
                    SettingsRow(
                        icon = SettingsIcons.Sound,
                        title = automixPerformanceTitle,
                        subtitle = stringResource(R.string.automix_performance_subtitle),
                        value = automixPerformance.localizedLabel(),
                        onClick = { pickingAutomixPerformance = true },
                    )
                }
                val skipSilenceTitle = stringResource(R.string.skip_silence)
                row(skipSilenceTitle, "silence") {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.VolumeOff,
                        title = skipSilenceTitle,
                        // Silence skipping is Media3's own processor, and
                        // `DefaultAudioSink.configure` appends that processor list
                        // only on its 16-bit branch — the float branch gets the
                        // format converter and nothing else. So on a float track
                        // this switch really does nothing, and the row says so
                        // rather than letting someone toggle it and wonder. The
                        // equaliser is unaffected: it runs upstream of that sink.
                        subtitle = if (outputStatus.actualEncoding == AudioFormat.ENCODING_PCM_FLOAT) {
                            stringResource(R.string.skip_silence_float_subtitle)
                        } else {
                            stringResource(R.string.skip_silence_subtitle)
                        },
                        checked = skipSilence,
                        onClick = { AppSettings.setSkipSilence(!skipSilence) },
                    )
                }
                val spatialAudioTitle = stringResource(R.string.spatial_audio)
                row(spatialAudioTitle, "surround", "3d") {
                    SettingsRow(
                        icon = Icons.Rounded.SurroundSound,
                        title = spatialAudioTitle,
                        // Widening a JOC stream would fight the object-based mix
                        // Dolby has already spatialized, so the service holds this
                        // off for the duration of an Atmos track — see
                        // `PlaybackService.applySpatialAudioEnabled`. Said on the
                        // row because the switch stays where the listener left it,
                        // and a switch reading "on" over an effect that is not
                        // running is the same silent lie the equaliser screen used
                        // to tell.
                        subtitle = if (playingDolbyAtmos) {
                            stringResource(R.string.spatial_audio_atmos_subtitle)
                        } else {
                            stringResource(R.string.spatial_audio_subtitle)
                        },
                        checked = spatialAudio,
                        onClick = { AppSettings.setSpatialAudio(!spatialAudio) },
                    )
                }
                // The system panel is not listed here as well. A device with a
                // Dolby or Dirac panel has something Veyra cannot reproduce and
                // keeps its row — but one level in, at the foot of the equaliser
                // screen, rather than as a second equaliser entry alongside ours.
                val equalizerTitle = stringResource(R.string.equalizer)
                row(equalizerTitle, "eq", "bass", "treble") {
                    SettingsRow(
                        icon = SettingsIcons.Sound,
                        title = equalizerTitle,
                        subtitle = stringResource(R.string.equalizer_subtitle),
                        onClick = onEqualizer,
                    )
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.APPEARANCE, header = stringResource(R.string.appearance)) {
                row(stringResource(R.string.home_personalize), "greeting", "speed dial", "shortcuts", "quick picks") {HomePersonalizationSetting()}
                row(stringResource(R.string.navigation_custom_title), "tabs", "startup", "labels", "local tab") {NavigationSetting()}
                row(stringResource(R.string.experience_title), "immersive", "data saver", "gestures") { ExperienceSetting() }
                row(stringResource(R.string.app_style), "home", "editorial", "listening room", "after dark") { AppStyleSetting() }
                row(stringResource(R.string.font_import_title), "import", "ttf", "otf", "lyrics font") {ImportedFontSetting()}
                row(stringResource(R.string.appearance_fonts), "font", "typography", "typeface") { FontSetting() }
                row(stringResource(R.string.theme_packs), "themes", "halloween", "fonts", "icons") {
                    SettingsRow(icon = SettingsIcons.Appearance, title = stringResource(R.string.theme_packs),
                        subtitle = stringResource(R.string.theme_packs_subtitle), onClick = { showThemeGallery = true })
                }
                row(stringResource(R.string.asset_packs), "assets", "skins", "overlays", "buttons", "motion") {
                    SettingsRow(icon=SettingsIcons.Appearance,title=stringResource(R.string.asset_packs),
                        subtitle=stringResource(R.string.asset_packs_subtitle),onClick={showAssetGallery=true})
                }
                row(stringResource(R.string.theme_artwork_controls), "artwork", "background", "visibility", "dim", "blur", "transparency") { ThemeArtworkSetting() }
                val themeTitle = stringResource(R.string.theme)
                row(themeTitle, "dark mode", "light mode") {
                    SettingsPresetRow(enabled = !app.veyra.music.ui.styles.LocalAppStyle.current.custom, icon = Icons.Rounded.Brightness4, title = themeTitle, subtitle = if(app.veyra.music.ui.styles.LocalAppStyle.current.custom)stringResource(R.string.style_original_only) else "",
                        options = ThemeMode.entries.map { it.localizedLabel() },
                        selectedIndex = ThemeMode.entries.indexOf(theme),
                        onSelect = { AppSettings.setThemeMode(ThemeMode.entries[it]) })
                }
                row(stringResource(R.string.custom_accent), "accent", "color", "palette") {
                    SettingsPresetRow(enabled = !app.veyra.music.ui.styles.LocalAppStyle.current.custom, icon = SettingsIcons.Appearance, title = stringResource(R.string.custom_accent),
                        subtitle = stringResource(R.string.custom_accent_hint),
                        options = androidx.compose.ui.res.stringArrayResource(R.array.custom_accent_options).toList(),
                        selectedIndex = AccentPalette.entries.indexOf(accentPalette), onSelect = { AppSettings.setAccentPalette(AccentPalette.entries[it]) },
                        swatches = listOf(Color(0xFFD9C4FF), Color(0xFF9BE1D5), Color(0xFFFFD58A), Color(0xFFFFB1C4)))
                }
                row(stringResource(R.string.custom_artwork_corners), "artwork", "corners", "covers") {
                    SettingsPresetRow(enabled = !app.veyra.music.ui.styles.LocalAppStyle.current.custom, icon = SettingsIcons.Icons, title = stringResource(R.string.custom_artwork_corners),
                        subtitle = stringResource(R.string.custom_artwork_corners_hint),
                        options = androidx.compose.ui.res.stringArrayResource(R.array.custom_corner_options).toList(),
                        selectedIndex = ArtworkCorners.entries.indexOf(artworkCorners), onSelect = { AppSettings.setArtworkCorners(ArtworkCorners.entries[it]) })
                }
                row(stringResource(R.string.custom_list_spacing), "density", "spacing", "compact") {
                    SettingsPresetRow(icon = SettingsIcons.Library, title = stringResource(R.string.custom_list_spacing),
                        subtitle = stringResource(R.string.custom_list_spacing_hint),
                        options = androidx.compose.ui.res.stringArrayResource(R.array.custom_spacing_options).toList(),
                        selectedIndex = ListSpacing.entries.indexOf(listSpacing), onSelect = { AppSettings.setListSpacing(ListSpacing.entries[it]) })
                }
                row(stringResource(R.string.custom_motion_level), "motion", "animation") {
                    SettingsPresetRow(icon = SettingsIcons.Performance, title = stringResource(R.string.custom_motion_level),
                        subtitle = stringResource(R.string.custom_motion_level_hint),
                        options = androidx.compose.ui.res.stringArrayResource(R.array.custom_motion_options).toList(),
                        selectedIndex = if (reduceAnimation) 2 else if (subtleMotion) 1 else 0, onSelect = { AppSettings.setMotionLevel(MotionLevel.entries[it]) })
                }
                row(stringResource(R.string.custom_player_shortcut), "player", "shortcut", "download", "sleep", "favorite") {
                    SettingsPresetRow(icon = SettingsIcons.Sound, title = stringResource(R.string.custom_player_shortcut),
                        subtitle = stringResource(R.string.custom_player_shortcut_hint),
                        options = androidx.compose.ui.res.stringArrayResource(R.array.custom_shortcut_options).toList(),
                        selectedIndex = PlayerShortcut.entries.indexOf(playerShortcut), onSelect = { AppSettings.setPlayerShortcut(PlayerShortcut.entries[it]) })
                }
                row(stringResource(R.string.custom_reset_appearance), "reset", "appearance") {
                    SettingsRow(icon = SettingsIcons.Appearance, title = stringResource(R.string.custom_reset_appearance),
                        onClick = { resetCustomization = "appearance" })
                }
                row(stringResource(R.string.custom_reset_player), "reset", "player", "shortcut") {
                    SettingsRow(icon = SettingsIcons.Sound, title = stringResource(R.string.custom_reset_player),
                        onClick = { resetCustomization = "player" })
                }
                val strongerTextTitle = stringResource(R.string.stronger_text)
                row(strongerTextTitle, "font", "bold", "readability", "accessibility") {
                    SettingsRow(icon = SettingsIcons.Appearance, title = strongerTextTitle,
                        subtitle = stringResource(R.string.stronger_text_subtitle),
                        checked = strongerText,
                        onClick = { AppSettings.setStrongerText(!strongerText) })
                }
                val contrastControlsTitle = stringResource(R.string.contrast_player_controls)
                row(contrastControlsTitle, "contrast", "buttons", "player", "accessibility") {
                    SettingsRow(icon = SettingsIcons.Icons, title = contrastControlsTitle,
                        subtitle = stringResource(R.string.contrast_player_controls_subtitle),
                        checked = contrastPlayerControls,
                        onClick = { AppSettings.setContrastPlayerControls(!contrastPlayerControls) })
                }
                val liquidGlassTitle = stringResource(R.string.liquid_glass)
                row(liquidGlassTitle, "glass", "blur") {
                    SettingsRow(
                        icon = Icons.Rounded.AutoAwesome,
                        title = liquidGlassTitle,
                        subtitle = stringResource(
                            if (liquidGlassSupported) {
                                R.string.liquid_glass_subtitle
                            } else {
                                R.string.liquid_glass_unavailable
                            },
                        ),
                        enabled = liquidGlassSupported,
                        checked = liquidGlass,
                        onClick = { AppSettings.setLiquidGlass(!liquidGlass) },
                    )
                }
                val scrollAnimationsTitle = stringResource(R.string.scroll_player_animations)
                row(scrollAnimationsTitle, "scroll", "mini player", "fold", "collapse", "animations") {
                    SettingsRow(
                        icon = Icons.Rounded.AutoAwesome,
                        title = scrollAnimationsTitle,
                        subtitle = stringResource(R.string.scroll_player_animations_hint),
                        checked = scrollPlayerAnimations,
                        onClick = { AppSettings.setScrollPlayerAnimations(!scrollPlayerAnimations) },
                    )
                    ScrollPlayerMotionSetting()
                    AppMotionSetting()
                }
                // Left out where the player won't honour it: a window too wide for
                // the player to fill and too narrow to stand a page beside it keeps
                // the sleeve either way. A docked pane is a phone's width, so it does
                // honour it — see [fullBleedArtworkAvailable].
                if (fullBleedArtworkAvailable(windowWidth)) {
                    val fullScreenCoverArtTitle = stringResource(R.string.full_screen_cover_art)
                    row(fullScreenCoverArtTitle, "artwork", "player") {
                        SettingsRow(
                            icon = Icons.Rounded.Fullscreen,
                            title = fullScreenCoverArtTitle,
                            subtitle = stringResource(R.string.full_screen_cover_art_subtitle),
                            checked = fullBleedArtwork,
                            onClick = { AppSettings.setFullBleedArtwork(!fullBleedArtwork) },
                        )
                    }
                }
                val legacyMeshGradientTitle = stringResource(R.string.legacy_mesh_gradient)
                row(legacyMeshGradientTitle, "background", "player") {
                    SettingsRow(
                        icon = Icons.Rounded.Gradient,
                        title = legacyMeshGradientTitle,
                        subtitle = stringResource(R.string.legacy_mesh_gradient_subtitle),
                        checked = legacyMeshGradient,
                        onClick = { AppSettings.setLegacyMeshGradient(!legacyMeshGradient) },
                    )
                }
                val animatedCoverArtTitle = stringResource(R.string.animated_cover_art)
                // Carries the keywords of the rows that only appear once it is on,
                // so searching "spotify" with animated covers switched off turns up
                // the switch that brings the Spotify row back rather than nothing.
                row(animatedCoverArtTitle, "canvas", "video", "spotify") {
                    SettingsRow(
                        icon = Icons.Rounded.Animation,
                        title = animatedCoverArtTitle,
                        subtitle = stringResource(R.string.animated_cover_art_subtitle),
                        checked = animatedCanvas,
                        onClick = { AppSettings.setAnimatedCanvas(!animatedCanvas) },
                    )
                }
                // Reads as part of the Animated cover art option above it, not
                // as a separate setting. Nothing to narrow while the clip itself
                // is off. Defaults to off: a clip loops for as long as its track
                // plays, so on cellular this is not a one-time video cost but
                // that cost repeated on every loop — see AppSettings.canvasOverCellular.
                if (animatedCanvas) {
                    val coverCellularTitle = stringResource(R.string.animated_cover_cellular)
                    row(coverCellularTitle, "canvas", "cellular", divided = false, reveal = true, reducedMotion = reducedMotion) {
                        SettingsSubRow(
                            title = coverCellularTitle,
                            checked = canvasOverCellular,
                            onCheckedChange = AppSettings::setCanvasOverCellular,
                        )
                    }
                    val spotifyCanvasTitle = stringResource(R.string.integrate_spotify_canvas)
                    row(spotifyCanvasTitle, "spotify", "canvas", divided = false, reveal = true, reducedMotion = reducedMotion) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onSpotifyCanvasAuth)
                                .padding(start = ROW_INSET, end = ROW_INSET, top = 4.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = spotifyCanvasTitle,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Chevron()
                        }
                    }
                }
                val artistSignaturesTitle = stringResource(R.string.artist_signatures)
                row(artistSignaturesTitle, "signature", "artist", "logo") {
                    SettingsRow(
                        icon = SettingsIcons.Appearance,
                        title = artistSignaturesTitle,
                        subtitle = stringResource(R.string.artist_signatures_subtitle),
                        checked = signatureApiEnabled,
                        onClick = { AppSettings.setSignatureApiEnabled(!signatureApiEnabled) },
                    )
                }
                if (signatureApiEnabled) {
                    val signatureUrlTitle = stringResource(R.string.artist_signatures_url)
                    row(signatureUrlTitle, "signature", "api", "url", divided = false, reveal = true, reducedMotion = reducedMotion) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onEditSignatureApi)
                                .padding(start = ROW_INSET, end = ROW_INSET, top = 4.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = signatureApiUrl.ifBlank {
                                    stringResource(R.string.artist_signatures_unconfigured)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Chevron()
                        }
                    }
                }
                val syncedLyricsTitle = stringResource(R.string.synced_lyrics)
                // Same reasoning as Animated cover art above: the lyrics source and
                // translation rows are only here while this is on.
                row(syncedLyricsTitle, "lyrics", "translation", "lrclib", "musixmatch") {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.Notes,
                        title = syncedLyricsTitle,
                        subtitle = stringResource(R.string.synced_lyrics_subtitle),
                        checked = syncedLyrics,
                        onClick = { AppSettings.setSyncedLyrics(!syncedLyrics) },
                    )
                }
                // Nothing to choose between while the feature is off, and the
                // sources are third-party services being reached on the user's
                // connection — which is the part worth being able to narrow.
                if (syncedLyrics) {
                    val lyricsBlurTitle = stringResource(R.string.blur_unfocused_lyrics)
                    row(lyricsBlurTitle, "lyrics", "blur") {
                        SettingsRow(
                            icon = Icons.Rounded.BlurOn,
                            title = lyricsBlurTitle,
                            subtitle = stringResource(R.string.blur_unfocused_lyrics_subtitle),
                            checked = lyricsBlur,
                            onClick = { AppSettings.setLyricsBlur(!lyricsBlur) },
                        )
                    }
                    val lyricsSourcesTitle = stringResource(R.string.lyrics_sources)
                    row(lyricsSourcesTitle, "lyrics", "lrclib", "musixmatch") {
                        SettingsRow(
                            icon = Icons.Rounded.Language,
                            title = lyricsSourcesTitle,
                            subtitle = lyricsSources
                                .sortedBy { it.ordinal }
                                .joinToString(", ") { it.label }
                                .ifEmpty { stringResource(R.string.no_lyrics_sources_enabled) },
                            trailing = { Chevron() },
                            onClick = onLyricsSources,
                        )
                    }
                    val translationLanguageTitle = stringResource(R.string.translation_language)
                    row(translationLanguageTitle, "lyrics", "translate") {
                        SettingsRow(
                            icon = Icons.Rounded.Translate,
                            title = translationLanguageTitle,
                            subtitle = if (translationLanguage.isBlank()) {
                                stringResource(R.string.translation_language_subtitle)
                            } else {
                                translationLanguageName(
                                    translationLanguage,
                                    AppCompatDelegate.getApplicationLocales().get(0) ?: Locale.getDefault(),
                                )
                            },
                            trailing = { Chevron() },
                            onClick = onTranslationLanguage,
                        )
                    }
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.PERFORMANCE, header = stringResource(R.string.performance)) {
                val reduceAnimationTitle = stringResource(R.string.reduce_animation)
                row(reduceAnimationTitle, "motion") {
                    SettingsRow(
                        icon = Icons.Rounded.MotionPhotosOff,
                        title = reduceAnimationTitle,
                        subtitle = stringResource(R.string.reduce_animation_subtitle),
                        checked = reduceAnimation,
                        onClick = { AppSettings.setReduceAnimation(!reduceAnimation) },
                    )
                }
                val reduceDynamicBlurTitle = stringResource(R.string.reduce_dynamic_blur)
                row(reduceDynamicBlurTitle, "blur", "performance") {
                    SettingsRow(
                        icon = Icons.Rounded.BlurOff,
                        title = reduceDynamicBlurTitle,
                        subtitle = stringResource(R.string.reduce_dynamic_blur_subtitle),
                        checked = reduceDynamicBlur,
                        onClick = { AppSettings.setReduceDynamicBlur(!reduceDynamicBlur) },
                    )
                }
                val highPerformanceModeTitle = stringResource(R.string.high_performance_mode)
                row(highPerformanceModeTitle, "frame rate", "refresh rate", "hz", "battery", "smooth") {
                    SettingsRow(
                        icon = VeyraIcons.Performance,
                        title = highPerformanceModeTitle,
                        subtitle = if (highPerformanceMode) {
                            stringResource(R.string.high_performance_active, selectedPerformanceRefreshRate)
                        } else {
                            stringResource(R.string.high_performance_subtitle)
                        },
                        checked = highPerformanceMode,
                        onClick = {
                            if (highPerformanceMode) {
                                AppSettings.setHighPerformanceMode(false)
                            } else {
                                showPerformanceWarning = true
                            }
                        },
                    )
                }
                if (highPerformanceMode) {
                    val refreshRateTitle = stringResource(R.string.refresh_rate)
                    row(refreshRateTitle, "hz", "frame rate", reveal = true, reducedMotion = reducedMotion) {
                        SettingsRow(
                            icon = VeyraIcons.FrameRate,
                            title = refreshRateTitle,
                        )
                        SegmentedControl(
                            options = supportedRefreshRates.map { "$it Hz" },
                            selectedIndex = supportedRefreshRates.indexOf(selectedPerformanceRefreshRate),
                            onSelect = { index ->
                                AppSettings.setPerformanceRefreshRate(supportedRefreshRates[index])
                            },
                            modifier = Modifier.padding(
                                start = TEXT_INSET,
                                end = ROW_INSET,
                                bottom = 14.dp,
                            ),
                        )
                    }
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.LIBRARY, header = stringResource(R.string.local_music)) {
                val localMusicFolderTitle = stringResource(R.string.local_music_folder)
                row(localMusicFolderTitle, "folder", "offline", "files") {
                    SettingsRow(
                        icon = Icons.Rounded.Folder,
                        title = localMusicFolderTitle,
                        subtitle = LocalMediaRepository.selectedFolderLabel(localMusicFolderUri)
                            ?: stringResource(R.string.all_audio_folders),
                        onClick = { localMusicFolderPicker.launch(null) },
                    )
                }
                if (localMusicFolderUri.isNotBlank()) {
                    val useAllAudioFoldersTitle = stringResource(R.string.use_all_audio_folders)
                    row(useAllAudioFoldersTitle, "folder") {
                        SettingsRow(
                            icon = SettingsIcons.Library,
                            title = useAllAudioFoldersTitle,
                            subtitle = stringResource(R.string.use_all_audio_folders_subtitle),
                            onClick = { AppSettings.setLocalMusicFolderUri("") },
                        )
                    }
                }
                val filterNonMusicAudioTitle = stringResource(R.string.filter_non_music_audio)
                row(filterNonMusicAudioTitle, "podcast", "recording") {
                    SettingsRow(
                        icon = Icons.Rounded.FilterAlt,
                        title = filterNonMusicAudioTitle,
                        subtitle = stringResource(R.string.filter_non_music_audio_subtitle),
                        checked = filterNonMusicAudio,
                        onClick = { AppSettings.setFilterNonMusicAudio(!filterNonMusicAudio) },
                    )
                }
            }

            val cacheUnlimited = cacheLimitBytes == AppSettings.UNLIMITED_CACHE_LIMIT_BYTES
            // The slider's last stop, one step past the largest fixed size, is
            // "Unlimited" — see [AppSettings.setAudioCacheLimitBytes].
            val cacheLimitMb = if (cacheUnlimited) CACHE_UNLIMITED_STOP_MB else (cacheLimitBytes / (1024 * 1024)).toInt()
            val unlimitedLabel = stringResource(R.string.song_cache_unlimited)
            val cacheLimitLabel = if (cacheUnlimited) unlimitedLabel else formatCacheSize(cacheLimitMb)
            SearchableSettingsGroup(search, category = SettingsCategory.LIBRARY, header = stringResource(R.string.storage)) {
                val showCacheFolderTitle = stringResource(R.string.show_cache_folder)
                row(showCacheFolderTitle, "cache", "cached songs", "library", "folder") {
                    SettingsRow(
                        icon = Icons.Rounded.Cached,
                        title = showCacheFolderTitle,
                        subtitle = stringResource(R.string.show_cache_folder_subtitle),
                        checked = showCacheFolder,
                        onClick = { AppSettings.setShowCacheFolder(!showCacheFolder) },
                    )
                }
                val songCacheLimitTitle = stringResource(R.string.song_cache_limit)
                row(songCacheLimitTitle, "cache", "space", "unlimited") {
                    SliderRow(
                        icon = Icons.Rounded.Storage,
                        title = songCacheLimitTitle,
                        subtitle = when {
                            cacheUnlimited -> stringResource(R.string.song_cache_unlimited_subtitle)
                            cacheLimitMb > CACHE_WARNING_MB ->
                                stringResource(R.string.song_cache_large_subtitle, cacheLimitLabel)
                            else -> stringResource(R.string.song_cache_limit_subtitle)
                        },
                        value = cacheLimitLabel,
                        sliderValue = cacheLimitMb.toFloat(),
                        formatValue = { if (it.roundToInt() == CACHE_UNLIMITED_STOP_MB) unlimitedLabel else formatCacheSize(it.roundToInt()) },
                        onSliderValue = {
                            AppSettings.setAudioCacheLimitBytes(it.roundToInt().toLong() * 1024 * 1024)
                        },
                        valueRange = (AppSettings.DEFAULT_CACHE_LIMIT_BYTES / (1024 * 1024)).toFloat()..
                            CACHE_UNLIMITED_STOP_MB.toFloat(),
                        steps = 19,
                    )
                }
                val clearSongCacheTitle = stringResource(R.string.clear_song_cache)
                row(clearSongCacheTitle, "cache", "free space") {
                    SettingsRow(
                        icon = Icons.Rounded.DeleteSweep,
                        title = clearSongCacheTitle,
                        destructive = true,
                        subtitle = stringResource(R.string.clear_song_cache_subtitle),
                        onClick = { confirmClearSongCache = true },
                    )
                }
                val clearImageCacheTitle = stringResource(R.string.clear_image_cache)
                row(clearImageCacheTitle, "cache", "artwork", "free space") {
                    SettingsRow(
                        icon = Icons.Rounded.DeleteSweep,
                        title = clearImageCacheTitle,
                        destructive = true,
                        subtitle = stringResource(R.string.clear_image_cache_subtitle),
                        onClick = {
                            val loader = SingletonImageLoader.get(context)
                            loader.memoryCache?.clear()
                            loader.diskCache?.clear()
                            Toast.makeText(context, textResources.getString(R.string.image_cache_cleared), Toast.LENGTH_SHORT).show()
                        },
                    )
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.LIBRARY, header = stringResource(R.string.your_data)) {
                val replayTitle = stringResource(R.string.replay)
                row(replayTitle, "stats", "history", "wrapped") {
                    SettingsRow(
                        icon = Icons.Rounded.BarChart,
                        title = replayTitle,
                        subtitle = stringResource(R.string.replay_subtitle),
                        onClick = onOpenReplay,
                    )
                }
                val workOutGenresTitle = stringResource(R.string.work_out_genres)
                row(workOutGenresTitle, "genre", "replay") {
                    SettingsRow(
                        icon = Icons.Rounded.LocalOffer,
                        title = workOutGenresTitle,
                        subtitle = if (replayGenres) {
                            stringResource(R.string.replay_genres_enabled_subtitle)
                        } else {
                            stringResource(R.string.replay_genres_disabled_subtitle)
                        },
                        checked = replayGenres,
                        onClick = { AppSettings.setReplayGenres(!replayGenres) },
                    )
                }
                val exportDataTitle = stringResource(R.string.export_data)
                row(exportDataTitle, "backup") {
                    SettingsRow(
                        icon = Icons.Rounded.FileUpload,
                        title = exportDataTitle,
                        enabled = !exporting && !importing,
                        subtitle = exportStatus ?: stringResource(R.string.export_data_subtitle),
                        onClick = { exportPicker.launch(Backup.suggestedName()) },
                    )
                }
                val importDataTitle = stringResource(R.string.import_data)
                row(importDataTitle, "backup", "restore") {
                    SettingsRow(
                        icon = Icons.Rounded.FileDownload,
                        title = importDataTitle,
                        enabled = !exporting && !importing,
                        subtitle = importStatus ?: stringResource(R.string.import_data_subtitle),
                        onClick = { confirmImport = true },
                    )
                }
            }

            // No footer: it only restated the stop-on-close row's own subtitle,
            // which sits four rows above it and says the same thing in fewer words.
            SearchableSettingsGroup(search, category = SettingsCategory.ADVANCED, header = stringResource(R.string.miscellaneous)) {
                val playNextOnSwipeTitle = stringResource(R.string.play_next_on_swipe)
                row(playNextOnSwipeTitle, "swipe", "queue") {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                        title = playNextOnSwipeTitle,
                        subtitle = if (swipeToPlayNext) {
                            stringResource(R.string.swipe_plays_next)
                        } else {
                            stringResource(R.string.swipe_adds_to_queue)
                        },
                        checked = swipeToPlayNext,
                        onClick = { AppSettings.setSwipeToPlayNext(!swipeToPlayNext) },
                    )
                }
                val dontRepeatSongsTitle = stringResource(R.string.dont_repeat_songs)
                row(dontRepeatSongsTitle, "radio", "suggestions") {
                    SettingsRow(
                        icon = Icons.Rounded.History,
                        title = dontRepeatSongsTitle,
                        subtitle = stringResource(R.string.dont_repeat_songs_subtitle),
                        checked = dontRepeatSuggestions,
                        onClick = { AppSettings.setDontRepeatSuggestions(!dontRepeatSuggestions) },
                    )
                }
                val stopMusicOnCloseTitle = stringResource(R.string.stop_music_on_close)
                row(stopMusicOnCloseTitle, "notification", "background") {
                    SettingsRow(
                        icon = Icons.Rounded.MusicOff,
                        title = stopMusicOnCloseTitle,
                        subtitle = stringResource(R.string.stop_music_on_close_subtitle),
                        checked = stopOnTaskRemoved,
                        onClick = { AppSettings.setStopOnTaskRemoved(!stopOnTaskRemoved) },
                    )
                }
                val hideVolumeBarTitle = stringResource(R.string.hide_volume_bar)
                row(hideVolumeBarTitle, "volume", "player") {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.VolumeOff,
                        title = hideVolumeBarTitle,
                        subtitle = stringResource(R.string.hide_volume_bar_subtitle),
                        checked = hideVolumeBar,
                        onClick = { AppSettings.setHideVolumeBar(!hideVolumeBar) },
                    )
                }
                val hideStatusBarTitle = stringResource(R.string.hide_status_bar_in_player)
                row(hideStatusBarTitle, "status bar", "immersive", "fullscreen", "player") {
                    SettingsRow(
                        icon = Icons.Rounded.WebAssetOff,
                        title = hideStatusBarTitle,
                        subtitle = stringResource(R.string.hide_status_bar_in_player_subtitle),
                        checked = hideStatusBarInPlayer,
                        onClick = { AppSettings.setHideStatusBarInPlayer(!hideStatusBarInPlayer) },
                    )
                }
                val showQueueHistoryTitle = stringResource(R.string.show_queue_history)
                row(showQueueHistoryTitle, "player", "queue", "history", "previously played") {
                    SettingsRow(
                        icon = Icons.Rounded.History,
                        title = showQueueHistoryTitle,
                        subtitle = stringResource(R.string.show_queue_history_subtitle),
                        checked = showQueueHistory,
                        onClick = { AppSettings.setShowQueueHistory(!showQueueHistory) },
                    )
                }
                val hideSongStatusTitle = stringResource(R.string.hide_song_status)
                row(hideSongStatusTitle, "player", "playing from", "played by") {
                    SettingsRow(
                        icon = Icons.Rounded.VisibilityOff,
                        title = hideSongStatusTitle,
                        subtitle = stringResource(R.string.hide_song_status_subtitle),
                        checked = hideSongStatus,
                        onClick = { AppSettings.setHideSongStatus(!hideSongStatus) },
                    )
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.APPEARANCE, header = stringResource(R.string.app_icon)) {
                val appIconTitle = stringResource(R.string.app_icon)
                row(appIconTitle, "icon", "launcher", "logo", "glass", "frost") {
                    AppIconSetting()
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.APPEARANCE, header = stringResource(R.string.language)) {
                val appLanguageTitle = stringResource(R.string.app_language)
                row(appLanguageTitle, "locale", "translate") {
                    val selectedLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag()
                        ?: Locale.getDefault().toLanguageTag()
                    SettingsRow(
                        icon = Icons.Rounded.Language,
                        title = appLanguageTitle,
                        subtitle = stringResource(languageDisplayNameRes(selectedLanguage)),
                        onClick = onAppLanguage,
                    )
                }
            }

            SearchableSettingsGroup(search, category = SettingsCategory.ADVANCED, header = stringResource(R.string.advanced_options)) {
                val smartAlignmentTitle = stringResource(R.string.smart_version_alignment)
                row(smartAlignmentTitle, "alignment", "sync", "waveform", "video", "audio", "skit", "intro") {
                    SettingsRow(
                        icon = Icons.Rounded.GraphicEq,
                        title = smartAlignmentTitle,
                        subtitle = stringResource(R.string.smart_version_alignment_subtitle),
                        checked = smartVersionAlignment,
                        onClick = { AppSettings.setSmartVersionAlignment(!smartVersionAlignment) },
                    )
                }
                val showNerdStatsTitle = stringResource(R.string.show_nerd_stats)
                row(showNerdStatsTitle, "debug", "bitrate", "codec") {
                    SettingsRow(
                        icon = Icons.Rounded.BarChart,
                        title = showNerdStatsTitle,
                        subtitle = stringResource(R.string.show_nerd_stats_subtitle),
                        checked = nerdStats,
                        onClick = { AppSettings.setShowNerdStats(!nerdStats) },
                    )
                }

            }

            if (category == null || searching) {
                val githubLinks = listOf(
                    Triple(R.string.veyra_github_profile, R.string.veyra_github_profile_handle,
                        "https://github.com/Bhavishyadalal"),
                    Triple(R.string.veyra_github_source, R.string.veyra_github_source_path,
                        "https://github.com/Bhavishyadalal/Veyra"),
                    Triple(R.string.veyra_website_source, R.string.veyra_website_source_path,
                        "https://github.com/Bhavishyadalal/Veyra-website"),
                )
                githubLinks.forEach { (titleRes, subtitleRes, url) ->
                    val title = stringResource(titleRes)
                    val subtitle = stringResource(subtitleRes)
                    if (!searching || search.matches(title, subtitle, "github", "developer", "repository", "source", "code")) {
                        Column(Modifier.padding(horizontal = GROUP_INSET, vertical = 4.dp)) {
                            SettingsRow(
                                icon = if (titleRes == R.string.veyra_github_profile) SettingsIcons.Account else SettingsIcons.Source,
                                title = title,
                                subtitle = subtitle,
                                onClick = {
                                    runCatching {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    }.onFailure {
                                        Toast.makeText(context, textResources.getString(R.string.creator_link_unavailable), Toast.LENGTH_SHORT).show()
                                    }
                                },
                            )
                        }
                    }
                }
                val creatorTitle = stringResource(R.string.creator_instagram)
                if (!searching || search.matches(creatorTitle, "instagram", "bhavishya_dalal_", "creator")) {
                    Column(Modifier.padding(horizontal = GROUP_INSET, vertical = 12.dp)) {
                        SettingsRow(
                            icon = SettingsIcons.Instagram,
                            title = creatorTitle,
                            subtitle = stringResource(R.string.creator_instagram_handle),
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/bhavishya_dalal_/")))
                                }.onFailure {
                                    Toast.makeText(context, textResources.getString(R.string.creator_link_unavailable), Toast.LENGTH_SHORT).show()
                                }
                            },
                        )
                    }
                }
            }

            // Read after every group above has had its turn at the query, which is
            // what makes this an accurate "nothing here" rather than a guess.
            if (searching && !search.anyMatch) {
                MessageState(
                    stringResource(R.string.settings_search_empty, searchQuery),
                    actionLabel = stringResource(R.string.clear_search),
                    onAction = { searchQuery = "" },
                )
            }

            // The version line is the page's colophon, not a setting: it belongs to
            // the whole list, so it goes when the list is narrowed to a few rows.
            if (searchQuery.isBlank()) {
                Text(
                    text = "Veyra $version",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp),
                )
            }
        }
    }
    }

    picking?.let { target ->
        ModalBottomSheet(
            onDismissRequest = { picking = null },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            QualitySheet(
                target = target,
                selected = when (target) {
                    QualityTarget.WIFI -> wifiQuality
                    QualityTarget.CELLULAR -> cellularQuality
                },
                // Writes the one ceiling that was being edited and nothing
                // else. There used to be a `SourceRegistry.applyQualityPreset`
                // call here that flipped the module and JioSaavn switches to
                // match — which meant budgeting *mobile data* switched those
                // sources off while sitting on Wi-Fi, and coming back to Wi-Fi
                // never switched them on again. Which sources a rung consults
                // is now read per stream off the connection in force; see
                // [AudioQuality.permits].
                onSelect = { quality ->
                    when (target) {
                        QualityTarget.WIFI -> AppSettings.setAudioQualityWifi(quality)
                        QualityTarget.CELLULAR -> AppSettings.setAudioQualityCellular(quality)
                    }
                    picking = null
                },
            )
        }
    }

    if (resetCustomization != null) {
        val scope = resetCustomization
        AlertDialog(onDismissRequest = { resetCustomization = null },
            title = { Text(stringResource(if (scope == "appearance") R.string.custom_reset_appearance else R.string.custom_reset_player)) },
            text = { Text(stringResource(if (scope == "appearance") R.string.custom_reset_appearance_hint else R.string.custom_reset_player_hint)) },
            confirmButton = { TextButton(onClick = {
                if (scope == "appearance") AppSettings.resetAppearanceCustomization() else AppSettings.resetPlayerCustomization()
                resetCustomization = null
            }) { Text(stringResource(R.string.custom_reset_confirm)) } },
            dismissButton = { TextButton(onClick = { resetCustomization = null }) { Text(stringResource(R.string.cancel)) } })
    }
    if (pickingDownloadQuality) {
        ModalBottomSheet(
            onDismissRequest = { pickingDownloadQuality = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            DownloadQualitySheet(
                selected = downloadQuality,
                onSelect = { quality ->
                    AppSettings.setDownloadQuality(quality)
                    pickingDownloadQuality = false
                },
            )
        }
    }

    if (pickingAutomixPerformance) {
        ModalBottomSheet(
            onDismissRequest = { pickingAutomixPerformance = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            AutomixPerformanceSheet(
                selected = automixPerformance,
                onSelect = { mode ->
                    AppSettings.setAutomixPerformanceMode(mode)
                    pickingAutomixPerformance = false
                },
            )
        }
    }

    // Everything in the Cached songs folder goes with it, so say so first.
    if (confirmClearSongCache) {
        AlertDialog(
            onDismissRequest = { confirmClearSongCache = false },
            title = { Text(stringResource(R.string.clear_song_cache_title)) },
            text = { Text(stringResource(R.string.clear_song_cache_warning)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClearSongCache = false
                    AudioCache.clear {
                        Toast.makeText(context, textResources.getString(R.string.song_cache_cleared), Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(stringResource(R.string.clear))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearSongCache = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    // Asked before the picker opens rather than after a file is chosen: the
    // thing being confirmed is that this device's own history is about to be
    // thrown away, and that is true whichever file gets picked.
    importCandidate?.let {candidate ->
        AlertDialog(onDismissRequest={importCandidate=null},title={Text(stringResource(R.string.backup_preview_title))},
            text={Text(stringResource(R.string.backup_preview_details,candidate.second.from,candidate.second.months,candidate.second.settings,candidate.second.at))},
            confirmButton={TextButton(enabled=!importing,onClick={
                importCandidate=null
                backupScope.launch {
                    importing=true
                    importStatus=textResources.getString(R.string.settings_backup_working)
                    try {
                        importStatus=Backup.importFrom(context,candidate.first,candidate.second.hash).fold(
                            onSuccess={textResources.getString(R.string.import_succeeded,context.countOfMonths(it.months),it.from)},
                            onFailure={textResources.getString(R.string.import_failed,it.message ?: textResources.getString(R.string.unknown_error))})
                    } finally {importing=false}
                }
            }) {Text(stringResource(R.string.import_data))}},
            dismissButton={TextButton(onClick={importCandidate=null}) {Text(stringResource(R.string.cancel))}})
    }
    if (confirmImport) {
        AlertDialog(
            onDismissRequest = { confirmImport = false },
            title = { Text(stringResource(R.string.import_backup_title)) },
            text = {
                Text(stringResource(R.string.import_backup_warning))
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmImport = false
                    importPicker.launch(arrayOf("application/json", "text/plain", "*/*"))
                }) {
                    Text(stringResource(R.string.choose_file))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmImport = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showPerformanceWarning) {
        AlertDialog(
            onDismissRequest = { showPerformanceWarning = false },
            title = { Text(stringResource(R.string.high_performance_before_enabling)) },
            text = { Text(stringResource(R.string.high_performance_battery_warning)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPerformanceWarning = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        val settingsIntent = intent.takeIf {
                            it.resolveActivity(context.packageManager) != null
                        } ?: Intent(Settings.ACTION_SETTINGS)
                        batterySettingsLauncher.launch(settingsIntent)
                    },
                ) {
                    Text(stringResource(R.string.open_battery_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPerformanceWarning = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showPerformanceConfirmation) {
        AlertDialog(
            onDismissRequest = { showPerformanceConfirmation = false },
            title = { Text(stringResource(R.string.enable_high_performance_title)) },
            text = { Text(stringResource(R.string.enable_high_performance_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPerformanceConfirmation = false
                        AppSettings.setHighPerformanceMode(true)
                    },
                ) {
                    Text(stringResource(R.string.enable))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPerformanceConfirmation = false }) {
                    Text(stringResource(R.string.not_yet))
                }
            },
        )
    }

    if (showListenBrainzTokenDialog) {
        var tokenInput by remember { mutableStateOf(listenBrainzToken) }
        AlertDialog(
            onDismissRequest = { showListenBrainzTokenDialog = false },
            title = { Text(stringResource(R.string.listenbrainz_token)) },
            text = {
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text(stringResource(R.string.api_token)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    AppSettings.setListenBrainzToken(tokenInput.trim())
                    showListenBrainzTokenDialog = false
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showListenBrainzTokenDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showLastfmLoginDialog) {
        var usernameInput by remember { mutableStateOf("") }
        var passwordInput by remember { mutableStateOf("") }
        var lastfmError by remember { mutableStateOf<String?>(null) }
        var lastfmLoading by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!lastfmLoading) showLastfmLoginDialog = false },
            title = { Text(stringResource(R.string.lastfm_login)) },
            text = {
                Column {
                    if (lastfmError != null) {
                        Text(
                            text = lastfmError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text(stringResource(R.string.username)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        label = { Text(stringResource(R.string.password)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        lastfmLoading = true
                        lastfmError = null
                        scrobbleScope.launch {
                            try {
                                // Use the credentials supplied for this build.
                                LastFM.initialize(
                                    apiKey = AppSettings.lastfmApiKey.value,
                                    secret = AppSettings.lastfmSecret.value,
                                )
                                LastFM.getMobileSession(usernameInput.trim(), passwordInput)
                                    .onSuccess { auth ->
                                        AppSettings.setLastfmSessionKey(auth.session.key)
                                        AppSettings.setLastfmUsername(auth.session.name)
                                        AppSettings.setLastfmEnabled(true)
                                        showLastfmLoginDialog = false
                                    }
                                    .onFailure { e ->
                                        lastfmError = e.message ?: textResources.getString(R.string.login_failed)
                                    }
                            } catch (e: Exception) {
                                lastfmError = e.message ?: textResources.getString(R.string.login_failed)
                            } finally {
                                lastfmLoading = false
                            }
                        }
                    },
                    enabled = !lastfmLoading && usernameInput.isNotBlank() && passwordInput.isNotBlank(),
                ) {
                    Text(stringResource(if (lastfmLoading) R.string.signing_in else R.string.sign_in))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLastfmLoginDialog = false }, enabled = !lastfmLoading) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

}

/** "3 months of listening" — the unit a backup is actually measured in. */
private fun Context.countOfMonths(months: Int): String = if (months == 0) {
    getString(R.string.no_listening_history)
} else {
    resources.getQuantityString(R.plurals.listening_month_count, months, months)
}

/** Which ceiling the open picker is editing. */
private enum class QualityTarget(val icon: ImageVector) {
    WIFI(Icons.Rounded.Wifi),
    CELLULAR(SettingsIcons.Performance),
}

@Composable
private fun QualityTarget.localizedTitle(): String = stringResource(
    if (this == QualityTarget.WIFI) R.string.wifi else R.string.mobile_data,
)

@Composable
private fun AudioQuality.localizedLabel(): String = stringResource(
    when (this) {
        AudioQuality.LOW -> R.string.low
        AudioQuality.MEDIUM -> R.string.medium
        AudioQuality.HIGH -> R.string.high
        AudioQuality.LOSSLESS -> R.string.lossless
    },
)

@Composable
private fun DownloadQuality.localizedLabel(): String = stringResource(
    when (this) {
        DownloadQuality.STANDARD -> R.string.standard
        DownloadQuality.HIGH -> R.string.high
        DownloadQuality.LOSSLESS -> R.string.lossless
    },
)

@Composable
private fun ThemeMode.localizedLabel(): String = stringResource(
    when (this) {
        ThemeMode.SYSTEM -> R.string.system
        ThemeMode.LIGHT -> R.string.light
        ThemeMode.DARK -> R.string.dark
    },
)

@Composable
private fun AutomixPerformanceMode.localizedLabel(): String = stringResource(
    when (this) {
        AutomixPerformanceMode.EFFICIENT -> R.string.automix_mode_efficient
        AutomixPerformanceMode.BALANCED -> R.string.automix_mode_balanced
        AutomixPerformanceMode.PERFORMANCE -> R.string.automix_mode_performance
    },
)

/**
 * Hands the device's own effect panel this app's audio session.
 *
 * Shared with [EqualizerScreen], which offers the same escape hatch a second
 * time next to the equaliser it might be an alternative to.
 */
internal fun openEqualizer(context: Context, sessionId: Int) {
    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
        putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId)
        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
        putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
    }
    runCatching { context.startActivity(intent) }.onFailure {
        Toast.makeText(context, context.getString(R.string.no_equalizer), Toast.LENGTH_SHORT).show()
    }
}

/** Above this, the cache limit slider's subtitle warns rather than reassures. */
private const val CACHE_WARNING_MB = 2048

/**
 * Where the cache limit slider's "Unlimited" stop sits: one 512MB step past
 * [AppSettings.MAX_CACHE_LIMIT_BYTES], so the fixed sizes keep their positions.
 */
private const val CACHE_UNLIMITED_STOP_MB = 10 * 1024 + 512

/** "512 MB", "2 GB", "2.5 GB" — whichever reads more naturally at that size. */
private fun formatCacheSize(mb: Int): String {
    if (mb < 1024) return "$mb MB"
    val gb = mb / 1024f
    return if (gb == gb.toInt().toFloat()) "${gb.toInt()} GB" else "%.1f GB".format(Locale.ROOT, gb)
}

/** Who you're signed in as, straight from YouTube Music's account menu. */
@Composable
internal fun AccountCard(
    signedIn: Boolean,
    account: Account?,
    onSignIn: () -> Unit,
    onClick: (() -> Unit)? = null,
    /** Sits inside a [SettingsGroup] that already supplies the card, so draws none of its own. */
    grouped: Boolean = false,
) {
    val action = if (signedIn) onClick else onSignIn
    val avatarUrl = account?.thumbnailUrl?.takeIf { signedIn }
    val avatar: (@Composable () -> Unit)? = if (avatarUrl != null) {
        {
            AsyncImage(model = avatarUrl, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape).thumbnailBorder(CircleShape))
        }
    } else null
    SettingsAccountSummary(
        title = if (signedIn) account?.name ?: stringResource(R.string.signed_in)
            else stringResource(R.string.sign_in_youtube_music),
        detail = if (signedIn) account?.email?.takeIf { it.isNotBlank() }
            ?: stringResource(R.string.youtube_music_account)
            else stringResource(R.string.tap_to_sign_in_google),
        onClick = action,
        modifier = if (grouped) Modifier else Modifier.padding(horizontal = GROUP_INSET)
            .clip(GroupShape).background(MaterialTheme.colorScheme.surfaceVariant),
        avatar = avatar,
    )
}

/** The connection ceiling and its data cost remain visible while choosing. */
@Composable
private fun QualitySheet(target: QualityTarget, selected: AudioQuality, onSelect: (AudioQuality) -> Unit) {
    val haptics = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).selectableGroup().padding(bottom = 24.dp)) {
        SettingsPickerHeader(stringResource(R.string.audio_quality),
            stringResource(R.string.audio_quality_connection, target.localizedTitle().lowercase(Locale.getDefault())), target.icon)
        AudioQuality.entries.reversed().forEach { quality ->
            SettingsChoiceRow(quality.localizedLabel(), stringResource(R.string.quality_hourly, quality.detail, quality.hourly), quality == selected) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect(quality)
            }
        }
    }
}

@Composable
private fun AutomixPerformanceSheet(selected: AutomixPerformanceMode, onSelect: (AutomixPerformanceMode) -> Unit) {
    val haptics = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).selectableGroup().padding(bottom = 24.dp)) {
        SettingsPickerHeader(stringResource(R.string.automix_performance), stringResource(R.string.automix_performance_warning), SettingsIcons.Sound)
        AutomixPerformanceMode.entries.forEach { mode ->
            SettingsChoiceRow(mode.localizedLabel(), stringResource(when (mode) {
                AutomixPerformanceMode.EFFICIENT -> R.string.automix_mode_efficient_subtitle
                AutomixPerformanceMode.BALANCED -> R.string.automix_mode_balanced_subtitle
                AutomixPerformanceMode.PERFORMANCE -> R.string.automix_mode_performance_subtitle
            }), mode == selected) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect(mode)
            }
        }
    }
}

@Composable
private fun DownloadQualitySheet(selected: DownloadQuality, onSelect: (DownloadQuality) -> Unit) {
    val haptics = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).selectableGroup().padding(bottom = 24.dp)) {
        SettingsPickerHeader(stringResource(R.string.download_quality), stringResource(R.string.download_quality_dialog_subtitle), Icons.Rounded.Download)
        DownloadQuality.entries.reversed().forEach { quality ->
            SettingsChoiceRow(quality.localizedLabel(), stringResource(R.string.quality_per_track, quality.detail, quality.perTrack), quality == selected) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onSelect(quality)
            }
        }
    }
}

// ---- Building blocks --------------------------------------------------------

/**
 * What is typed into the field under the Settings title, and whether anything
 * on the screen has answered to it.
 *
 * Categories use existing translated labels. Search spans them all; otherwise
 * only the selected category composes its controls.
 */
internal enum class SettingsCategory(val title: Int, val summary: List<Int>, val icon: ImageVector) {
    ACCOUNT(R.string.account_integrations, listOf(R.string.scrobbling, R.string.listen_together), SettingsIcons.Account),
    SOUND(R.string.playback, listOf(R.string.audio_quality, R.string.equalizer), SettingsIcons.Sound),
    APPEARANCE(R.string.appearance, listOf(R.string.theme, R.string.app_icon), SettingsIcons.Appearance),
    LIBRARY(R.string.library, listOf(R.string.downloads, R.string.storage, R.string.your_data), SettingsIcons.Library),
    PERFORMANCE(R.string.performance, listOf(R.string.high_performance_mode, R.string.reduce_animation), SettingsIcons.Performance),
    ADVANCED(R.string.advanced_options, listOf(R.string.miscellaneous, R.string.show_nerd_stats), SettingsIcons.Advanced),
}

/** Plain, full-width destinations remain readable at large system font sizes. */
@Composable
private fun SettingsOverview(onSelect: (SettingsCategory) -> Unit) {
    Text(
        stringResource(R.string.settings_intro),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
    Column(Modifier.fillMaxWidth().padding(horizontal = GROUP_INSET)) {
        SettingsCategory.entries.forEachIndexed { index, category ->
            if (index > 0) RowDivider()
            SettingsRow(
                icon = category.icon,
                title = stringResource(category.title),
                subtitle = category.summary.map { stringResource(it) }.joinToString(" · "),
                emphasized = true,
                onClick = { onSelect(category) },
            )
        }
    }
}

/** Collects the rows of one group that survived the search. */
private class SettingsGroupScope(
    private val search: SettingsSearch<SettingsCategory>,
    private val header: String?,
) {
    class Entry(val identity: List<String?>, val divided: Boolean, val content: @Composable () -> Unit)

    val entries = mutableListOf<Entry>()

    /**
     * One searchable setting. [keywords] is everything somebody might type
     * looking for it: its title at least, plus whatever its subtitle or the
     * controls under it say that the title doesn't — "Spotify", say, for the
     * canvas link that lives under Animated cover art.
     *
     * [divided] is false for the rows drawn as part of the row above them
     * rather than as settings in their own right, so they keep hugging their
     * parent when both survive the filter.
     */
    fun row(
        vararg keywords: String?,
        divided: Boolean = true,
        reveal: Boolean = false,
        reducedMotion: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        // The header counts for every row beneath it: searching "playback"
        // should turn up the playback group entire, not nothing at all.
        if (search.matches(header, *keywords)) entries += Entry(keywords.toList(), divided) {
            if (reveal) SettingsOptionsReveal(reducedMotion = reducedMotion, content = content)
            else content()
        }
    }
}

/**
 * A [SettingsGroup] whose rows are filtered by the search field, and which
 * takes itself off the screen when none of them are left. Dividers fall
 * between the rows that survived rather than around the gaps left by the ones
 * that didn't, so a filtered section is indistinguishable from a hand-written one.
 */
@Composable
private fun SearchableSettingsGroup(
    search: SettingsSearch<SettingsCategory>,
    category: SettingsCategory,
    header: String? = null,
    footer: String? = null,
    topSpacing: Dp = 26.dp,
    content: @Composable SettingsGroupScope.() -> Unit,
) {
    // Do not compose controls for unopened categories. Search intentionally spans all categories.
    if (!search.includes(category)) return
    val scope = SettingsGroupScope(search, header)
    scope.content()
    if (scope.entries.isEmpty()) return
    SettingsGroup(header = header, footer = footer, topSpacing = topSpacing) {
        scope.entries.forEachIndexed { index, entry ->
            androidx.compose.runtime.key(entry.identity) {
                when {
                    index > 0 && entry.divided -> RowDivider()
                    // An undivided row is drawn tight to the top because something
                    // it belongs to is usually above it. Filtered down to itself it
                    // has nothing to sit under, so it is given that room back.
                    index == 0 && !entry.divided -> Spacer(Modifier.height(10.dp))
                }
                entry.content()
            }
        }
    }
}

/**
 * One section of rows, with a sentence-case heading above and an optional
 * plain-language [footer] below. Rows are separated by [RowDivider].
 */
@Composable
internal fun SettingsGroup(
    header: String? = null,
    footer: String? = null,
    /** Room above the section, where there is no [header] to provide it. */
    topSpacing: Dp = 26.dp,
    content: @Composable () -> Unit,
) {
    if (header != null) {
        SettingsSectionTitle(
            title = header,
            modifier = Modifier.padding(start = GROUP_INSET + ROW_INSET, end = GROUP_INSET + ROW_INSET,
                top = topSpacing, bottom = 6.dp),
        )
    } else {
        Spacer(Modifier.height(topSpacing))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GROUP_INSET),
    ) {
        content()
    }
    if (footer != null) {
        Text(
            text = footer,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                start = GROUP_INSET + 4.dp,
                end = GROUP_INSET + 4.dp,
                top = 8.dp,
            ),
        )
    }
}
