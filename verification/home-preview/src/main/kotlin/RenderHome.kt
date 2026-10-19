@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package app.veyra.music.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import app.veyra.music.ui.icons.VeyraIcons
import java.io.File
import javax.swing.SwingUtilities
import javax.xml.parsers.DocumentBuilderFactory
import org.jetbrains.skia.EncodedImageFormat

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFFD9C4FF),
        onPrimary = Color(0xFF35204F),
        primaryContainer = Color(0xFF422D5C),
        onPrimaryContainer = Color(0xFFF0E3FF),
        secondary = Color(0xFFE9BEA8),
        onSecondary = Color(0xFF422B20),
        background = Color.Black,
        onBackground = Color.White,
        surface = Color(0xFF0D0D0F),
        onSurface = Color.White,
        surfaceVariant = Color(0xFF1C1C1E),
        onSurfaceVariant = Color(0xFFACA8B9),
        outline = Color(0xFF383443),
    )

private val LightColors =
    lightColorScheme(
        primary = Color(0xFF69408C),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF0E3FF),
        onPrimaryContainer = Color(0xFF35204F),
        secondary = Color(0xFF84523B),
        onSecondary = Color.White,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color(0xFFF7F7F9),
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFF2F2F7),
        onSurfaceVariant = Color(0xFF6E6E73),
        outline = Color(0xFFE5E5EA),
    )

private val VeyraFontFamily =
    FontFamily(
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_regular.ttf"),
            FontWeight.W400,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_medium.ttf"),
            FontWeight.W500,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_semibold.ttf"),
            FontWeight.W600,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_bold.ttf"),
            FontWeight.W700,
        ),
        Font(
            File("${System.getProperty("veyra.root")}/app/src/main/res/font/veyra_heavy.ttf"),
            FontWeight.W800,
        ),
    )
private val VeyraTypography =
    Typography(
            displayLarge =
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    letterSpacing = (-0.8).sp,
                ),
            displayMedium =
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.6).sp,
                ),
            displaySmall =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),
            headlineLarge =
                TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    letterSpacing = (-0.7).sp,
                ),
            headlineMedium =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.4).sp,
                ),
            headlineSmall =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp),
            titleLarge =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.3).sp,
                ),
            titleMedium =
                TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    letterSpacing = (-0.2).sp,
                ),
            titleSmall =
                TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
            bodyLarge =
                TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
            bodyMedium =
                TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
            bodySmall =
                TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
            labelLarge =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 20.sp),
            labelMedium =
                TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 16.sp),
            labelSmall =
                TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 11.sp, lineHeight = 16.sp),
        )
        .withFamily(VeyraFontFamily)

private fun Typography.withFamily(family: FontFamily) =
    Typography(
        displayLarge = displayLarge.copy(fontFamily = family),
        displayMedium = displayMedium.copy(fontFamily = family),
        displaySmall = displaySmall.copy(fontFamily = family),
        headlineLarge = headlineLarge.copy(fontFamily = family),
        headlineMedium = headlineMedium.copy(fontFamily = family),
        headlineSmall = headlineSmall.copy(fontFamily = family),
        titleLarge = titleLarge.copy(fontFamily = family),
        titleMedium = titleMedium.copy(fontFamily = family),
        titleSmall = titleSmall.copy(fontFamily = family),
        bodyLarge = bodyLarge.copy(fontFamily = family),
        bodyMedium = bodyMedium.copy(fontFamily = family),
        bodySmall = bodySmall.copy(fontFamily = family),
        labelLarge = labelLarge.copy(fontFamily = family),
        labelMedium = labelMedium.copy(fontFamily = family),
        labelSmall = labelSmall.copy(fontFamily = family),
    )

@Composable
private fun googleIcon() {
    val vector = remember {
        val xml =
            File(
                System.getProperty("veyra.root"),
                "sharedUi/src/commonMain/composeResources/drawable/ic_google_g.xml",
            )
        val root =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml).documentElement
        val builder = ImageVector.Builder("Google", 28.dp, 28.dp, 48f, 48f)
        val paths = root.getElementsByTagName("path")
        for (i in 0 until paths.length) {
            val path = paths.item(i) as org.w3c.dom.Element
            builder.addPath(
                addPathNodes(path.getAttribute("android:pathData")),
                fill =
                    SolidColor(
                        Color(path.getAttribute("android:fillColor").removePrefix("#").toLong(16))
                    ),
            )
        }
        builder.build()
    }
    Icon(
        rememberVectorPainter(vector),
        null,
        tint = Color.Unspecified,
        modifier = Modifier.size(28.dp),
    )
}

@Composable
private fun fixtureImage(path: String, modifier: Modifier, scale: ContentScale = ContentScale.Fit) {
    val image =
        remember(path) {
            org.jetbrains.skia.Image.makeFromEncoded(
                    File(System.getProperty("veyra.root"), path).readBytes()
                )
                .toComposeImageBitmap()
        }
    Image(image, null, modifier, contentScale = scale)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeStudy(kind: String) {
    var tiles by remember { mutableStateOf(kind == "tiles") }
    var page by remember { mutableIntStateOf(0) }
    val reduced = kind == "reduced"
    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(
            "VEYRA / 0.1.4-beta1",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "Native Home component review",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HomeHeading("Listen now", "Find your next favorite.") {
            BoxWithConstraints(Modifier.weight(1f)) {
                val iconSize = if (maxWidth < 280.dp) 56.dp else 64.dp
                val wordSize =
                    (minOf(46f, ((maxWidth - iconSize - 14.dp).value / 3.4f).coerceAtLeast(1f)) /
                            androidx.compose.ui.platform.LocalDensity.current.fontScale)
                        .sp
                val brandFont = remember {
                    FontFamily(
                        Font(
                            File(
                                System.getProperty("veyra.root"),
                                "app/src/main/res/font/super_malibu.ttf",
                            )
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    fixtureImage(
                        "media-kit/brand/veyra-app-icon-original.png",
                        Modifier.size(iconSize),
                    )
                    Text(
                        "Veyra",
                        fontFamily = brandFont,
                        fontSize = wordSize,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                    )
                }
            }
        }
        HomeAccountPrompt("Sign in to YouTube Music", "Sign in with Google", {}) { googleIcon() }
        if (kind == "empty") {
            HomeEmptyContent(
                "Your next favorite is out there",
                "No recommendations arrived yet. Refresh to try again.",
                "Refresh recommendations",
                false,
                {},
            )
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Quick picks",
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                HomeActionButton(
                    if (tiles) Icons.Rounded.ViewCarousel else VeyraIcons.GridView,
                    if (tiles) "Show banners" else "Show tiles",
                    { tiles = !tiles },
                    reducedMotion = reduced,
                )
            }
            if (tiles) {
                Row(
                    Modifier.fillMaxWidth().height(276.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    fixtureCard(
                        "Nightfall",
                        "Illustrative artwork",
                        false,
                        kind,
                        Modifier.weight(1.1f).fillMaxHeight(),
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        fixtureCard(
                            "Silver hour",
                            "Illustrative artwork",
                            true,
                            kind,
                            Modifier.weight(1f).fillMaxWidth(),
                        )
                        fixtureCard(
                            "Afterglow",
                            "Illustrative artwork",
                            true,
                            kind,
                            Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
                }
            } else
                fixtureCard(
                    "Nightfall",
                    "Illustrative artwork · component study",
                    false,
                    kind,
                    Modifier.fillMaxWidth().height(if (kind == "large-text") 370.dp else 260.dp),
                )
            HomePageNavigation(
                page,
                8,
                "Page ${page + 1} of 8",
                "Previous",
                "Next",
                false,
                reduced,
                { page = (page - 1).coerceAtLeast(0) },
                { page = (page + 1).coerceAtMost(7) },
            )
            Text(
                "Listen again",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            fixtureCard(
                "A little further",
                "Illustrative artwork",
                true,
                "fallback",
                Modifier.fillMaxWidth().height(140.dp),
            )
        }
        Text(
            "Actual native components · sample content\nArtwork is illustrative, not a music recommendation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun fixtureCard(
    title: String,
    subtitle: String,
    compact: Boolean,
    kind: String,
    modifier: Modifier,
) {
    val rtl = androidx.compose.ui.platform.LocalLayoutDirection.current == LayoutDirection.Rtl
    HomeArtworkFrame(
        title,
        subtitle,
        compact,
        kind == "playing",
        "Now playing",
        Color(0xFFD9C4FF),
        modifier.combinedClickable(onClick = {}, onLongClick = {}),
        artwork = {
            if (kind != "fallback")
                fixtureImage(
                    "media-kit/artwork/campaign-landscape.webp",
                    Modifier.fillMaxSize(),
                    ContentScale.Crop,
                )
        },
        action = {
            Icon(
                VeyraIcons.ChevronRight,
                null,
                tint = Color.White,
                modifier = Modifier.size(22.dp).graphicsLayer { scaleX = if (rtl) -1f else 1f },
            )
        },
    )
}

private fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }

private fun ImageComposeScene.nodes() = semanticsOwners.flatMap { it.rootSemanticsNode.all() }

private fun ImageComposeScene.named(label: String) =
    nodes().single {
        it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains(label) == true
    }

private fun ImageComposeScene.frame(time: Long = 1_000_000_000L) {
    render(time).close()
}

private fun ImageComposeScene.tap(point: Offset) {
    sendPointerEvent(
        PointerEventType.Press,
        point,
        buttons = PointerButtons(isPrimaryPressed = true),
    )
    frame()
    sendPointerEvent(PointerEventType.Release, point, buttons = PointerButtons())
    frame()
}

fun main() =
    SwingUtilities.invokeAndWait {
        val output = File(System.getProperty("veyra.preview.output"))
        output.mkdirs()
        for ((kind, width, height, scale) in
            listOf(
                Scene("banners", 390, 1040, 1f),
                Scene("tiles", 390, 1040, 1f),
                Scene("playing", 390, 1040, 1f),
                Scene("fallback", 390, 1040, 1f),
                Scene("empty", 390, 800, 1f),
                Scene("large-text", 320, 1560, 1.6f),
                Scene("rtl", 390, 1040, 1f),
                Scene("light", 390, 1040, 1f),
                Scene("reduced", 390, 1040, 1f),
            )) {
            val scene =
                ImageComposeScene(
                    width * 2,
                    height * 2,
                    density = Density(2f, scale),
                    layoutDirection =
                        if (kind == "rtl") LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    MaterialTheme(
                        colorScheme = if (kind == "light") LightColors else DarkColors,
                        typography = VeyraTypography,
                    ) {
                        HomeStudy(kind)
                    }
                }
            scene.frame(0)
            val rendered = scene.render(1_000_000_000L)
            File(output, "$kind.png")
                .writeBytes(rendered.encodeToData(EncodedImageFormat.PNG)!!.bytes)
            rendered.close()
            scene.close()
        }
        var checks = 0
        fun verify(label: String, block: () -> Boolean) {
            check(block()) { label }
            checks++
            println("PASS $label")
        }
        var signIns = 0
        val page = mutableIntStateOf(0)
        val scrolling = mutableStateOf(false)
        val selected = mutableStateOf(false)
        val refreshing = mutableStateOf(false)
        var refreshes = 0
        var clicked = 0
        var held = 0
        val scene =
            ImageComposeScene(780, 1400, density = Density(2f)) {
                MaterialTheme(colorScheme = DarkColors, typography = VeyraTypography) {
                    Column {
                        HomeAccountPrompt(
                            "Sign in to YouTube Music",
                            "Sign in with Google",
                            { signIns++ },
                        ) {
                            Text("G")
                        }
                        HomePageNavigation(
                            page.intValue,
                            3,
                            "Page ${page.intValue + 1} of 3",
                            "Previous",
                            "Next",
                            scrolling.value,
                            true,
                            { page.intValue-- },
                            { page.intValue++ },
                        )
                        HomeActionButton(
                            VeyraIcons.GridView,
                            "Layout",
                            { selected.value = !selected.value },
                            selected = selected.value,
                            reducedMotion = true,
                        )
                        HomeEmptyContent(
                            "Empty",
                            "Try again",
                            "Refresh",
                            refreshing.value,
                            { refreshes++ },
                        )
                        HomeArtworkFrame(
                            "Fixture track",
                            "Artist",
                            false,
                            false,
                            "Now playing",
                            Color.White,
                            Modifier.size(260.dp, 180.dp)
                                .combinedClickable(
                                    onClick = { clicked++ },
                                    onLongClick = { held++ },
                                ),
                            artwork = {},
                            action = {},
                        )
                    }
                }
            }
        scene.frame(0)
        val account =
            scene.nodes().single {
                it.config.getOrNull(SemanticsProperties.Text)?.any { text ->
                    text.text == "Sign in to YouTube Music"
                } == true && it.config.contains(SemanticsActions.OnClick)
            }
        verify("Google prompt has one button action and no editable fields") {
            account.config[SemanticsProperties.Role] == Role.Button &&
                scene.nodes().none { it.config.contains(SemanticsProperties.EditableText) }
        }
        scene.tap(account.boundsInRoot.center)
        verify("Google prompt invokes host exactly once") { signIns == 1 }
        verify("first page disables previous") {
            scene.named("Previous").config.contains(SemanticsProperties.Disabled)
        }
        scene.tap(scene.named("Next").boundsInRoot.center)
        verify("next advances exactly one page") { page.intValue == 1 }
        verify("navigation targets are at least 48dp") {
            scene.named("Next").boundsInRoot.width >= 96f &&
                scene.named("Next").boundsInRoot.height >= 96f
        }
        scene.tap(scene.named("Next").boundsInRoot.center)
        verify("last page disables next") {
            page.intValue == 2 && scene.named("Next").config.contains(SemanticsProperties.Disabled)
        }
        scene.tap(scene.named("Next").boundsInRoot.center)
        verify("disabled next cannot escape the collection") { page.intValue == 2 }
        scrolling.value = true
        scene.frame()
        verify("scrolling disables both arrows") {
            scene.named("Previous").config.contains(SemanticsProperties.Disabled) &&
                scene.named("Next").config.contains(SemanticsProperties.Disabled)
        }
        scrolling.value = false
        scene.frame()
        scene.tap(scene.named("Previous").boundsInRoot.center)
        verify("previous returns exactly one page") { page.intValue == 1 }
        scene.tap(scene.named("Layout").boundsInRoot.center)
        verify("layout selection exposes its new state") {
            selected.value && scene.named("Layout").config[SemanticsProperties.Selected]
        }
        val refresh =
            scene.nodes().single {
                it.config.getOrNull(SemanticsProperties.Text)?.any { text ->
                    text.text == "Refresh"
                } == true && it.config.contains(SemanticsActions.OnClick)
            }
        scene.tap(refresh.boundsInRoot.center)
        verify("empty state refresh invokes its host") { refreshes == 1 }
        refreshing.value = true
        scene.frame()
        val disabledRefresh =
            scene.nodes().single {
                it.config.getOrNull(SemanticsProperties.Text)?.any { text ->
                    text.text == "Refresh"
                } == true && it.config.contains(SemanticsActions.OnClick)
            }
        verify("active refresh rejects duplicate requests") {
            disabledRefresh.config.contains(SemanticsProperties.Disabled)
        }
        scene.tap(disabledRefresh.boundsInRoot.center)
        verify("disabled refresh does not invoke its host") { refreshes == 1 }
        val card =
            scene.nodes().single {
                it.config.getOrNull(SemanticsProperties.Text)?.any { text ->
                    text.text == "Fixture track"
                } == true && it.config.contains(SemanticsActions.OnClick)
            }
        card.config[SemanticsActions.OnClick].action!!.invoke()
        card.config[SemanticsActions.OnLongClick].action!!.invoke()
        verify("artwork preserves distinct tap and hold actions") { clicked == 1 && held == 1 }
        verify("reduced motion disables finite feedback motion") {
            homeMotionMillis(true, 180) == 0 && homeMotionMillis(false, 180) == 180
        }
        scene.close()
        val rtlPage = mutableIntStateOf(0)
        val rtlScene =
            ImageComposeScene(
                640,
                200,
                density = Density(2f),
                layoutDirection = LayoutDirection.Rtl,
            ) {
                MaterialTheme(colorScheme = DarkColors, typography = VeyraTypography) {
                    HomePageNavigation(
                        rtlPage.intValue,
                        3,
                        "RTL page",
                        "Previous",
                        "Next",
                        false,
                        true,
                        { rtlPage.intValue-- },
                        { rtlPage.intValue++ },
                    )
                }
            }
        rtlScene.frame(0)
        verify("RTL next arrow occupies the mirrored side") {
            rtlScene.named("Next").boundsInRoot.left < rtlScene.named("Previous").boundsInRoot.left
        }
        rtlScene.tap(rtlScene.named("Next").boundsInRoot.center)
        verify("RTL next advances the logical page") { rtlPage.intValue == 1 }
        rtlScene.close()
        println("$checks Home component checks passed; nine native component boards rendered.")
    }

private data class Scene(val kind: String, val width: Int, val height: Int, val scale: Float)
