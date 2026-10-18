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
import androidx.compose.material3.Icon
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
import androidx.annotation.DrawableRes
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
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
import app.veyra.music.ui.components.VeyraBrand
import app.veyra.music.ui.rememberIsForeground
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

private val WelcomeCream = Color(0xFFF5F3EF)
private val WelcomeChampagne = Color(0xFFD8C4A5)

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
    var page by rememberSaveable { mutableIntStateOf(PAGE_NAME) }
    var name by rememberSaveable { mutableStateOf(initialName) }
    val focusManager = LocalFocusManager.current
    val reduceAnimation by AppSettings.reduceAnimation.collectAsStateWithLifecycle()
    val foreground = rememberIsForeground()
    val keyboardUp = WindowInsets.isImeVisible
    val systemScale = rememberCoroutineScope().coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
    val reduceMotion = reduceAnimation || systemScale == 0f || !foreground
    var introduced by rememberSaveable { mutableStateOf(false) }
    val phase = remember { Animatable(if (introduced || reduceMotion) 1f else 0f) }
    // One short foreground reveal, then completely still. Typing/backgrounding settles it.
    LaunchedEffect(foreground, keyboardUp, reduceMotion) {
        val animate = !introduced && foreground && !keyboardUp && !reduceMotion
        introduced = true
        if (animate) phase.animateTo(1f, tween(620)) else phase.snapTo(1f)
    }

    BackHandler(enabled = page == PAGE_CONNECT) { page = PAGE_NAME }
    LaunchedEffect(signedIn, page) {
        if (signedIn && page == PAGE_CONNECT) onFinish()
    }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier.fillMaxSize().background(Color(0xFF090B0C))
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
                    PAGE_NAME -> NamePage(
                        compact = compact,
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
                        phase = { phase.value },
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
        val compact = maxHeight < 800.dp
        Column(
            Modifier.widthIn(max = 520.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 28.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) { content(compact) }
    }
}

@Composable
private fun ColumnScope.NamePage(
    compact: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit,
    reduceMotion: Boolean,
    keyboardUp: Boolean,
    phase: () -> Float,
) {
    Column {
        WelcomeProgress(PAGE_NAME)
        Spacer(Modifier.height(if (keyboardUp || compact) 20.dp else 36.dp))
        if (!keyboardUp) {
            VeyraBrand(prominent = !compact, color = WelcomeCream, reveal = phase)
            Spacer(Modifier.height(if (compact) 12.dp else 16.dp))
        }
        FlowingTitle(stringResource(R.string.onboarding_title), reduceMotion)
        if (!keyboardUp) {
            Spacer(Modifier.height(if (compact) 12.dp else 20.dp))
            WelcomeArtwork(R.drawable.welcome_glass_portal, phase = phase, compact = compact)
        }
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
    phase: () -> Float,
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
        Spacer(Modifier.height(if (compact) 12.dp else 20.dp))
        WelcomeArtwork(R.drawable.welcome_glass_frequency, phase = phase, portrait = true, compact = compact)
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
        else Box(Modifier.size(8.dp).background(WelcomeChampagne, CircleShape))
        Row(
            Modifier.weight(1f).semantics {
                contentDescription = description
                progressBarRangeInfo = ProgressBarRangeInfo((page + 1).toFloat(), 1f..2f)
            },
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            repeat(2) { step ->
                Box(Modifier.weight(1f).height(3.dp).background(
                    if (step <= page) WelcomeChampagne else Color.White.copy(alpha = 0.16f), CircleShape))
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

/** Bundled, bounded artwork: Coil decodes off the UI thread and reuses its memory cache. */
@Composable
private fun WelcomeArtwork(
    @DrawableRes artwork: Int,
    phase: () -> Float,
    portrait: Boolean = false,
    compact: Boolean = false,
) {
    BoxWithConstraints(Modifier.fillMaxWidth().clearAndSetSemantics { }) {
        val shape = RoundedCornerShape(28.dp)
        Box(
            Modifier.fillMaxWidth().height(
                if (compact) { if (portrait) 112.dp else 128.dp }
                else (maxWidth * 0.56f).coerceIn(140.dp, 210.dp),
            )
                .clip(shape).background(Color(0xFF07090A))
                .border(1.dp, WelcomeChampagne.copy(alpha = 0.2f), shape),
        ) {
            AsyncImage(
                model = artwork,
                contentDescription = null,
                contentScale = if (portrait) ContentScale.Fit else ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer {
                    val progress = phase().coerceIn(0f, 1f)
                    alpha = 0.65f + progress * 0.35f
                    scaleX = 1.04f - progress * 0.04f
                    scaleY = scaleX
                },
            )
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
                listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f)),
            )))
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
            .border(1.dp, WelcomeChampagne.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) Text(stringResource(R.string.onboarding_name_hint), style = style,
            color = WelcomeCream.copy(alpha = 0.5f), maxLines = 1, modifier = Modifier.clearAndSetSemantics { })
        BasicTextField(
            value, onValueChange, singleLine = true, textStyle = style,
            cursorBrush = SolidColor(WelcomeChampagne),
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
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = animateFloatAsState(if (pressed && enabled && !reduceMotion) 0.98f else 1f,
        if (reduceMotion) snap() else spring(stiffness = 900f), label = "welcomePress")
    Row(
        Modifier.fillMaxWidth().heightIn(min = 58.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value; alpha = if (enabled) 1f else 0.4f }
            .clip(CircleShape).background(if (primary) WelcomeCream else Color.White.copy(alpha = 0.07f))
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) { leading(); Spacer(Modifier.width(10.dp)) }
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.W800,
            color = if (primary) Color(0xFF151513) else WelcomeCream,
            textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
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

private const val PAGE_NAME = 0
private const val PAGE_CONNECT = 1
private const val NAME_LIMIT = 40

@Composable
private fun OnboardingBackdrop() {
    Box(Modifier.fillMaxSize().drawWithCache {
        val base = Brush.linearGradient(listOf(Color(0xFF090B0C), Color(0xFF171817), Color(0xFF090B0C)),
            start = Offset.Zero, end = Offset(size.width, size.height))
        val glow = Brush.radialGradient(listOf(WelcomeCream.copy(alpha = 0.06f), Color.Transparent),
            center = Offset(size.width * 0.9f, size.height * 0.26f), radius = size.maxDimension * 0.65f)
        val scrim = Brush.verticalGradient(0f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.42f))
        onDrawBehind { drawRect(base); drawRect(glow); drawRect(scrim) }
    })
}
