package app.veyra.music.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import app.veyra.music.ui.themes.ThemeIcon as Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.veyra.music.R
import app.veyra.music.data.settings.AppSettings
import app.veyra.music.sharedui.resources.Res
import app.veyra.music.sharedui.resources.ic_google_g
import app.veyra.music.ui.icons.VeyraIcons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import app.veyra.music.ui.components.VeyraWaveform
import app.veyra.music.ui.components.VeyraBrand
import app.veyra.music.ui.rememberIsForeground
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val WelcomeCream = Color(0xFFF5F3EF)
private val WelcomeSilver = Color(0xFFDCE0E4)
private val WelcomeBackground = Color(0xFF08090A)

/** First-install name and optional sign-in, with one brief, keyboard-aware brand reveal. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    signedIn: Boolean,
    initialName: String,
    onNameChosen: (String) -> Unit,
    onSignIn: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var page by rememberSaveable { mutableIntStateOf(PAGE_WELCOME) }
    var name by rememberSaveable { mutableStateOf(initialName) }
    val focusManager = LocalFocusManager.current
    val reduceAnimation by AppSettings.reduceAnimation.collectAsStateWithLifecycle()
    val foreground = rememberIsForeground()
    val keyboardUp = WindowInsets.isImeVisible
    val systemScale = rememberCoroutineScope().coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
    val reduceMotion = reduceAnimation || app.veyra.music.ui.customization.LocalSubtleMotion.current || systemScale == 0f || !foreground
    var introduced by rememberSaveable { mutableStateOf(false) }
    val phase = remember { Animatable(if (introduced || reduceMotion) 1f else 0f) }
    // One short foreground reveal, then completely still. Typing/backgrounding settles it.
    LaunchedEffect(foreground, keyboardUp, reduceMotion) {
        if (!foreground) {
            phase.snapTo(1f)
            return@LaunchedEffect
        }
        val animate = !introduced && !keyboardUp && !reduceMotion
        introduced = true
        if (animate) {
            phase.snapTo(0f)
            phase.animateTo(1f, tween(620))
        } else phase.snapTo(1f)
    }

    BackHandler(enabled = page != PAGE_WELCOME) {
        focusManager.clearFocus()
        page = if (page == PAGE_CONNECT) PAGE_NAME else PAGE_WELCOME
    }
    LaunchedEffect(signedIn, page) {
        if (signedIn && page == PAGE_CONNECT) onFinish()
    }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier.fillMaxSize().background(WelcomeBackground)
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } },
    ) {
        OnboardingBackdrop()
        AnimatedContent(
            targetState = page,
            transitionSpec = {
                val direction = if ((targetState > initialState) != rtl) 1 else -1
                val duration = if (reduceMotion) 0 else 260
                (slideInHorizontally(tween(duration)) { direction * it / 10 } + fadeIn(tween(duration)))
                    .togetherWith(
                        slideOutHorizontally(tween(duration)) { -direction * it / 10 } + fadeOut(tween(duration / 2)),
                    )
            },
            label = "welcomePage",
            modifier = Modifier.fillMaxSize().systemBarsPadding().imePadding(),
        ) { shown ->
            OnboardingPage { compact ->
                when (shown) {
                    PAGE_WELCOME -> WelcomeLanding(
                        compact = compact,
                        reduceMotion = reduceMotion,
                        phase = { phase.value },
                        onStart = { page = PAGE_NAME },
                    )
                    PAGE_NAME -> NamePage(
                        compact = compact,
                        onBack = { focusManager.clearFocus(); page = PAGE_WELCOME },
                        name = name,
                        onNameChange = { name = it.take(NAME_LIMIT) },
                        reduceMotion = reduceMotion,
                        keyboardUp = keyboardUp,
                        phase = { phase.value },
                        onNext = next@{
                            val chosen = name.trim()
                            if (chosen.isEmpty()) return@next
                            focusManager.clearFocus()
                            onNameChosen(chosen)
                            if (signedIn) onFinish() else page = PAGE_CONNECT
                        },
                    )
                    else -> ConnectPage(
                        compact = compact,
                        reduceMotion = reduceMotion,
                        onBack = { page = PAGE_NAME },
                        onSignIn = onSignIn,
                        onSkip = onFinish,
                    )
                }
            }
        }
    }
}

/** Scrolls on short screens/large fonts while keeping the actions low on normal screens. */
@Composable
private fun OnboardingPage(content: @Composable ColumnScope.(Boolean) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val compact = maxHeight < 900.dp
        Column(
            Modifier.widthIn(max = 520.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) { content(compact) }
    }
}

/** A genuine landing screen: previews are optional, and Get started always opens name entry. */
@Composable
private fun ColumnScope.WelcomeLanding(
    compact: Boolean,
    reduceMotion: Boolean,
    phase: () -> Float,
    onStart: () -> Unit,
) {
    var preview by rememberSaveable { mutableIntStateOf(0) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val swipeThreshold = with(LocalDensity.current) { 48.dp.toPx() }
    val pose = animateFloatAsState((preview - 1).toFloat(),
        if (reduceMotion) snap() else spring(stiffness = 260f, dampingRatio = 0.9f), label = "welcomeSculpture")
    val titles = listOf(R.string.welcome_space_title, R.string.welcome_lyrics_title, R.string.welcome_together_title)
    val bodies = listOf(R.string.welcome_space_body, R.string.welcome_lyrics_body, R.string.welcome_together_body)
    val chips = listOf(R.string.welcome_music_chip, R.string.welcome_lyrics_chip, R.string.welcome_together_chip)
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VeyraBrand(Modifier.weight(1f), color = WelcomeCream, reveal = phase)
            Text("0${preview + 1}", style = MaterialTheme.typography.titleMedium,
                color = WelcomeCream.copy(alpha = 0.8f),
                modifier = Modifier.clearAndSetSemantics { }
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    .padding(horizontal = 16.dp, vertical = 10.dp))
        }
        Spacer(Modifier.height(if (compact) 12.dp else 24.dp))
        Box(
            Modifier.fillMaxWidth().height(if (compact) 230.dp else 310.dp)
                .pointerInput(preview, rtl, swipeThreshold) {
                    var distance = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { distance = 0f },
                        onDragCancel = { distance = 0f },
                        onDragEnd = { preview = welcomePreviewAfterSwipe(preview, distance, swipeThreshold, rtl) },
                        onHorizontalDrag = { change, amount -> change.consume(); distance += amount },
                    )
                },
        ) {
            Box(Modifier.fillMaxSize().graphicsLayer { scaleX = 1.16f; scaleY = 1.16f }) {
                AsyncImage(
                    model = R.drawable.welcome_silver_wave,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().graphicsLayer {
                        val reveal = phase().coerceIn(0f, 1f)
                        compositingStrategy = CompositingStrategy.Offscreen
                        alpha = 0.5f + reveal * 0.5f
                        scaleX = 1.03f - reveal * 0.03f
                        scaleY = scaleX
                        translationX = if (reduceMotion) 0f else pose.value * 6.dp.toPx()
                    }.drawWithCache {
                        val vertical = Brush.verticalGradient(0f to Color.Transparent, 0.16f to Color.Black,
                            0.8f to Color.Black, 1f to Color.Transparent)
                        val horizontal = Brush.horizontalGradient(0f to Color.Transparent, 0.08f to Color.Black,
                            0.92f to Color.Black, 1f to Color.Transparent)
                        onDrawWithContent {
                            drawContent()
                            drawRect(vertical, blendMode = BlendMode.DstIn)
                            drawRect(horizontal, blendMode = BlendMode.DstIn)
                        }
                    },
                )
            }
            Row(
                Modifier.align(Alignment.BottomEnd).heightIn(min = 48.dp)
                    .clip(CircleShape).background(Color(0xCC1B1D20))
                    .border(1.dp, Color.White.copy(alpha = 0.23f), CircleShape)
                    .clickable(role = Role.Button, onClickLabel = stringResource(R.string.welcome_next_preview)) {
                        preview = nextWelcomePreview(preview)
                    }.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VeyraWaveform(Modifier.size(22.dp), WelcomeCream, phase)
                Text(stringResource(chips[preview]), style = MaterialTheme.typography.labelLarge, color = WelcomeCream)
            }
        }
        Spacer(Modifier.height(if (compact) 22.dp else 30.dp))
        AnimatedContent(
            targetState = preview,
            transitionSpec = {
                val duration = if (reduceMotion) 0 else 240
                val direction = if ((nextWelcomePreview(initialState) == targetState) != rtl) 1 else -1
                (fadeIn(tween(duration)) + slideInHorizontally(tween(duration)) { direction * it / 12 })
                    .togetherWith(fadeOut(tween(duration / 2)) + slideOutHorizontally(tween(duration)) { -direction * it / 12 })
            }, label = "welcomePreview",
        ) { selectedPreview ->
            Column(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
                Text(stringResource(titles[selectedPreview]),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = if (compact) 44.sp else 52.sp,
                        lineHeight = if (compact) 48.sp else 56.sp,
                        letterSpacing = (-1.5).sp, fontWeight = FontWeight.W800),
                    color = WelcomeCream, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(14.dp))
                Text(stringResource(bodies[selectedPreview]),
                    style = MaterialTheme.typography.titleMedium.copy(lineHeight = 25.sp),
                    color = WelcomeCream.copy(alpha = 0.72f))
            }
        }
        Spacer(Modifier.height(16.dp))
    }
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(WELCOME_PREVIEW_COUNT) { item ->
                val description = stringResource(R.string.welcome_preview_position, item + 1, WELCOME_PREVIEW_COUNT)
                Box(Modifier.size(48.dp).clip(CircleShape)
                    .clickable(role = Role.Tab) { preview = item }
                    .semantics { contentDescription = description; selected = preview == item },
                    contentAlignment = Alignment.Center) {
                    Box(Modifier.width(if (item == preview) 28.dp else 14.dp).height(4.dp)
                        .background(if (item == preview) WelcomeCream else Color.White.copy(alpha = 0.16f), CircleShape))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        PillButton(stringResource(R.string.welcome_get_started), onStart,
            primary = true, reduceMotion = reduceMotion, forwardArrow = true)
    }
}

@Composable
private fun ColumnScope.NamePage(
    compact: Boolean,
    onBack: () -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
    reduceMotion: Boolean,
    keyboardUp: Boolean,
    phase: () -> Float,
) {
    Column {
        WelcomeProgress(PAGE_NAME, onBack)
        Spacer(Modifier.height(if (keyboardUp || compact) 20.dp else 36.dp))
        if (!keyboardUp) {
            VeyraBrand(prominent = !compact, color = WelcomeCream, reveal = phase)
            Spacer(Modifier.height(if (compact) 12.dp else 16.dp))
            AsyncImage(
                model = R.drawable.welcome_silver_wave,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(if (compact) 96.dp else 148.dp)
                    .clip(RoundedCornerShape(24.dp)),
            )
            Spacer(Modifier.height(16.dp))
        }
        FlowingTitle(stringResource(R.string.onboarding_personalize_title), reduceMotion)
        Spacer(Modifier.height(if (compact) 16.dp else 28.dp))
    }
    Column {
        Text(
            stringResource(R.string.onboarding_name_prompt),
            style = MaterialTheme.typography.titleMedium,
            color = WelcomeCream,
            modifier = Modifier.padding(start = 4.dp),
        )
        Spacer(Modifier.height(12.dp))
        NameField(name, onNameChange, onNext)
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.onboarding_name_footer),
            style = MaterialTheme.typography.bodyMedium,
            color = WelcomeCream.copy(alpha = 0.66f),
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Spacer(Modifier.height(if (compact) 20.dp else 28.dp))
        PillButton(stringResource(R.string.onboarding_next), onNext, primary = true,
            enabled = name.isNotBlank(), reduceMotion = reduceMotion)
    }
}

@Composable
private fun ColumnScope.ConnectPage(
    compact: Boolean,
    reduceMotion: Boolean,
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onSkip: () -> Unit,
) {
    Column {
        WelcomeProgress(PAGE_CONNECT, onBack)
        Spacer(Modifier.height(if (compact) 16.dp else 36.dp))
        VeyraBrand(color = WelcomeCream)
        Spacer(Modifier.height(if (compact) 12.dp else 18.dp))
        FlowingTitle(stringResource(R.string.onboarding_connect_title), reduceMotion)
        Spacer(Modifier.height(if (compact) 12.dp else 18.dp))
        Text(
            stringResource(R.string.onboarding_connect_body),
            style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            color = WelcomeCream.copy(alpha = 0.72f),
        )
        Spacer(Modifier.height(if (compact) 16.dp else 28.dp))
    }
    Column {
        PillButton(stringResource(R.string.onboarding_sign_in_google), onSignIn, primary = true,
            reduceMotion = reduceMotion, leading = {
                Image(painterResource(Res.drawable.ic_google_g), null, Modifier.size(20.dp))
            })
        Spacer(Modifier.height(12.dp))
        PillButton(stringResource(R.string.onboarding_skip_sign_in), onSkip, primary = false,
            reduceMotion = reduceMotion)
    }
}

@Composable
private fun WelcomeProgress(page: Int, onBack: (() -> Unit)? = null) {
    val description = stringResource(if (page == PAGE_NAME) R.string.onboarding_name_prompt else R.string.onboarding_connect_title)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (onBack != null) BackButton(onBack)
        else Box(Modifier.size(8.dp).background(WelcomeSilver, CircleShape))
        Row(
            Modifier.weight(1f).semantics {
                contentDescription = description
                progressBarRangeInfo = ProgressBarRangeInfo(page.toFloat(), 1f..2f)
            },
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            repeat(2) { step ->
                Box(Modifier.weight(1f).height(3.dp).background(
                    if (step < page) WelcomeSilver else Color.White.copy(alpha = 0.16f), CircleShape))
            }
        }
        Text(if (page == PAGE_NAME) "01 / 02" else "02 / 02",
            style = MaterialTheme.typography.labelSmall, color = WelcomeCream.copy(alpha = 0.55f),
            modifier = Modifier.clearAndSetSemantics { })
    }
}

/** Whole words flow into place; accessibility reads the original localized heading once. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowingTitle(text: String, reduceMotion: Boolean) {
    val words = remember(text) { text.split(Regex("\\s+")).filter(String::isNotEmpty) }
    FlowRow(
        Modifier.fillMaxWidth().clearAndSetSemantics { contentDescription = text; heading() },
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        words.forEachIndexed { index, word ->
            val reveal = remember(text, index) { Animatable(if (reduceMotion) 1f else 0f) }
            LaunchedEffect(text, reduceMotion) {
                if (reduceMotion) reveal.snapTo(1f) else {
                    delay(40L + (index * 30L).coerceAtMost(120L))
                    reveal.animateTo(1f, tween(280))
                }
            }
            Text(
                word,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-1).sp),
                fontWeight = FontWeight.W800,
                color = WelcomeCream,
                modifier = Modifier.graphicsLayer {
                    alpha = reveal.value
                    translationY = ((1f - reveal.value) * 16f).dp.toPx()
                },
            )
        }
    }
}

@Composable
private fun NameField(value: String, onValueChange: (String) -> Unit, onDone: () -> Unit) {
    val prompt = stringResource(R.string.onboarding_name_prompt)
    val style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, color = WelcomeCream)
    Box(
        Modifier.fillMaxWidth().heightIn(min = 64.dp).clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .border(1.dp, WelcomeSilver.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) Text(stringResource(R.string.onboarding_name_hint), style = style,
            color = WelcomeCream.copy(alpha = 0.5f), maxLines = 1, modifier = Modifier.clearAndSetSemantics { })
        BasicTextField(
            value, onValueChange, singleLine = true, textStyle = style,
            cursorBrush = SolidColor(WelcomeSilver),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words,
                autoCorrectEnabled = false, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { onDone() }),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = prompt },
        )
    }
}

@Composable
private fun PillButton(
    label: String,
    onClick: () -> Unit,
    primary: Boolean,
    reduceMotion: Boolean,
    enabled: Boolean = true,
    leading: (@Composable () -> Unit)? = null,
    forwardArrow: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = animateFloatAsState(if (pressed && enabled && !reduceMotion) 0.98f else 1f,
        if (reduceMotion) snap() else spring(stiffness = 900f), label = "welcomePress")
    Row(
        Modifier.fillMaxWidth().heightIn(min = 64.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (enabled) 1f else 0.4f }
            .clip(CircleShape)
            .background(Brush.verticalGradient(if (primary) listOf(Color(0xFFF8F9FA), Color(0xFFCED2D7))
                else listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.04f))))
            .border(1.dp, Color.White.copy(alpha = if (primary) 0.75f else 0.16f), CircleShape)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) { leading(); Spacer(Modifier.width(10.dp)) }
        Text(label, style = if (primary) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W800,
            color = if (primary) Color(0xFF151513) else WelcomeCream,
            textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        if (forwardArrow) {
            Spacer(Modifier.width(12.dp))
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, null,
                tint = if (primary) Color(0xFF151513) else WelcomeCream,
                modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))
        .clickable(role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(VeyraIcons.ChevronRight, stringResource(R.string.onboarding_back), tint = WelcomeCream,
            modifier = Modifier.size(18.dp).graphicsLayer { scaleX = if (rtl) 1f else -1f })
    }
}

private const val PAGE_WELCOME = 0
private const val PAGE_NAME = 1
private const val PAGE_CONNECT = 2
private const val NAME_LIMIT = 40

@Composable
private fun OnboardingBackdrop() {
    Box(Modifier.fillMaxSize().drawWithCache {
        val base = Brush.linearGradient(listOf(WelcomeBackground, Color(0xFF151719), WelcomeBackground),
            start = Offset.Zero, end = Offset(size.width, size.height))
        val glow = Brush.radialGradient(listOf(WelcomeCream.copy(alpha = 0.06f), Color.Transparent),
            center = Offset(size.width * 0.9f, size.height * 0.26f), radius = size.maxDimension * 0.65f)
        val scrim = Brush.verticalGradient(0f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.42f))
        onDrawBehind { drawRect(base); drawRect(glow); drawRect(scrim) }
    })
}
