@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package app.veyra.music.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.*
import app.veyra.music.ui.components.AlertAction
import app.veyra.music.ui.components.AlertRule
import app.veyra.music.ui.components.PillTextField
import app.veyra.music.ui.icons.SettingsIcons
import java.io.File
import javax.swing.SwingUtilities
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

// Generated from the app's theme declarations by the preview setup, below.
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
private fun IconArtwork(name: String) {
    val bitmap =
        remember(name) {
            Image.makeFromEncoded(
                    File(
                            "${System.getProperty("veyra.root")}/app/src/main/res/mipmap-xxxhdpi/$name"
                        )
                        .readBytes()
                )
                .toComposeImageBitmap()
        }
    Image(bitmap, null, Modifier.fillMaxSize().scale(1.5f), contentScale = ContentScale.Fit)
}

@Composable
private fun SampleScreen(kind: String) {
    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 22.dp, bottom = 30.dp)
    ) {
        if (kind == "overview") {
            SettingsPageHeader("Settings", SettingsIcons.Sound)
            Text(
                "Sound, style and everything in between.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
            )
            Column(Modifier.padding(horizontal = GROUP_INSET)) {
                listOf(
                        Triple(
                            "Account & integrations",
                            "Scrobbling · Listen together",
                            SettingsIcons.Account,
                        ),
                        Triple("Playback", "Audio quality · Equalizer", SettingsIcons.Sound),
                        Triple("Appearance", "Theme · App icon", SettingsIcons.Appearance),
                        Triple("Library", "Downloads · Storage · Your data", SettingsIcons.Library),
                        Triple(
                            "Performance",
                            "Frame rate · Reduced motion",
                            SettingsIcons.Performance,
                        ),
                        Triple("Advanced", "Language · Troubleshooting", SettingsIcons.Advanced),
                    )
                    .forEachIndexed { i, (title, detail, icon) ->
                        if (i > 0) RowDivider()
                        SettingsRow(icon, title, subtitle = detail, emphasized = true, onClick = {})
                    }
            }
        } else if (kind == "dialog") {
            SettingsPageHeader("Account & integrations", SettingsIcons.Account)
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 32.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                SettingsPickerHeader(
                    "Connect your account",
                    "Keep your listening connected. You can change this at any time.",
                    SettingsIcons.Account,
                )
                Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    PillTextField("", {}, "Username")
                    Spacer(Modifier.height(12.dp))
                    PillTextField("secret", {}, "Password", isPassword = true)
                }
                AlertAction("Continue to sign in", true, {})
                AlertRule()
                AlertAction("Not now", false, {})
            }
            Column(Modifier.padding(horizontal = 20.dp).selectableGroup()) {
                SettingsSectionTitle("App icon", Modifier.padding(vertical = 12.dp))
                Row {
                    SettingsIconChoice("Wave glass", true, {}, Modifier.weight(1f)) {
                        IconArtwork("ic_wave_glass_foreground.png")
                    }
                    SettingsIconChoice("White frost", false, {}, Modifier.weight(1f)) {
                        IconArtwork("ic_wave_frost_foreground.png")
                    }
                }
            }
        } else if (kind == "choices") {
            SettingsPickerHeader(
                "Audio quality",
                "Choose the ceiling for Wi-Fi streaming.",
                SettingsIcons.Sound,
            )
            Column(Modifier.selectableGroup()) {
                SettingsChoiceRow(
                    "Lossless",
                    "Original quality when an enabled source provides it.",
                    true,
                    {},
                )
                SettingsChoiceRow("High", "256 kbps · Around 115 MB per hour", false, {})
                SettingsChoiceRow("Medium", "128 kbps · Around 58 MB per hour", false, {})
                SettingsChoiceRow("Low", "48 kbps · Around 22 MB per hour", false, {})
            }
        } else {
            SettingsPageHeader("Playback", SettingsIcons.Sound)
            SettingsSectionTitle(
                "Your sound",
                Modifier.padding(start = 28.dp, top = 16.dp, bottom = 6.dp),
            )
            Column(Modifier.padding(horizontal = GROUP_INSET)) {
                var normal by remember { mutableStateOf(true) }
                SettingsRow(
                    SettingsIcons.Sound,
                    "Loudness normalization",
                    subtitle = "Keep the volume consistent between tracks.",
                    checked = normal,
                    onClick = { normal = !normal },
                )
                RowDivider()
                SettingsRow(
                    SettingsIcons.Performance,
                    "Dolby Atmos",
                    subtitle = "This device does not have a compatible Dolby decoder.",
                    checked = false,
                    enabled = false,
                    onClick = {},
                )
                RowDivider()
                var fade by remember { mutableFloatStateOf(4f) }
                SliderRow(
                    SettingsIcons.Sound,
                    "Crossfade",
                    "4s",
                    fade,
                    { fade = it },
                    0f..12f,
                    11,
                    subtitle = "Blend one track into the next.",
                    formatValue = { "${it.toInt()}s" },
                )
                RowDivider()
                SettingsRow(SettingsIcons.Sound, "Audio quality", value = "High", onClick = {})
                RowDivider()
                SettingsRow(SettingsIcons.Appearance, "Theme")
                SegmentedControl(
                    listOf("System", "Light", "Dark"),
                    if (MaterialTheme.colorScheme.background == Color.White) 1 else 2,
                    {},
                    Modifier.padding(start = ROW_INSET, end = ROW_INSET, bottom = 16.dp),
                )
                RowDivider()
                SettingsRow(
                    SettingsIcons.Library,
                    "Clear song cache",
                    subtitle = "Remove cached audio. Your downloads stay saved.",
                    onClick = {},
                    destructive = true,
                )
            }
        }
    }
}

private fun SemanticsNode.all(): List<SemanticsNode> = listOf(this) + children.flatMap { it.all() }

private fun ImageComposeScene.nodes(): List<SemanticsNode> =
    semanticsOwners.flatMap { it.rootSemanticsNode.all() }

private fun SemanticsNode.text(): String =
    config.getOrNull(SemanticsProperties.Text)?.joinToString { it.text } ?: ""

fun main() =
    SwingUtilities.invokeAndWait {
        val out =
            File(
                System.getProperty(
                    "veyra.preview.output",
                    "${System.getProperty("veyra.root")}/design/settings-preview",
                )
            )
        out.mkdirs()
        for ((kind, width, height, scale) in
            listOf(
                Quad("overview", 390, 844, 1f),
                Quad("controls", 390, 1000, 1f),
                Quad("choices", 390, 740, 1f),
                Quad("controls", 320, 1600, 1.6f),
                Quad("controls-light", 390, 1000, 1f),
                Quad("dialog", 390, 844, 1f),
                Quad("controls-rtl", 390, 1000, 1f),
            )) {
            val scene =
                ImageComposeScene(
                    width * 2,
                    height * 2,
                    density = Density(2f, scale),
                    layoutDirection =
                        if (kind.endsWith("rtl")) LayoutDirection.Rtl else LayoutDirection.Ltr,
                ) {
                    MaterialTheme(
                        colorScheme = if (kind.endsWith("light")) LightColors else DarkColors,
                        typography = VeyraTypography,
                    ) {
                        SampleScreen(if (kind.startsWith("controls")) "controls" else kind)
                    }
                }
            scene.render(0).close()
            val image = scene.render(1_000_000_000L)
            File(out, "$kind-${width}w-${scale}x.png")
                .writeBytes(image.encodeToData(EncodedImageFormat.PNG)!!.bytes)
            image.close()
            scene.close()
        }
        var toggles = 0
        var dualToggles = 0
        var opens = 0
        var selected = 0
        var commits = 0
        val scene =
            ImageComposeScene(780, 2000, density = Density(2f)) {
                MaterialTheme(colorScheme = DarkColors, typography = VeyraTypography) {
                    Column {
                        var on by remember { mutableStateOf(false) }
                        SettingsRow(
                            SettingsIcons.Sound,
                            "Test toggle",
                            checked = on,
                            onClick = {
                                on = !on
                                toggles++
                            },
                        )
                        SettingsRow(
                            SettingsIcons.Sound,
                            "Disabled toggle",
                            checked = false,
                            enabled = false,
                            onClick = { error("Disabled control invoked") },
                        )
                        SettingsRow(
                            SettingsIcons.Account,
                            "Edit service",
                            checked = true,
                            onClick = { opens++ },
                            onCheckedChange = { dualToggles++ },
                        )
                        SegmentedControl(
                            listOf("System", "Light", "Dark"),
                            selected,
                            { selected = it },
                        )
                        SliderRow(
                            SettingsIcons.Sound,
                            "Test slider",
                            "4s",
                            4f,
                            { commits++ },
                            0f..12f,
                            11,
                            formatValue = { "${it.toInt()}s" },
                        )
                        AlertAction("Dialog action", true, { opens++ })
                        AlertAction(
                            "Disabled action",
                            false,
                            { error("Disabled action invoked") },
                            enabled = false,
                        )
                        var input by remember { mutableStateOf("") }
                        PillTextField(input, { input = it }, "Username")
                        PillTextField("secret", {}, "Password", isPassword = true)
                    }
                }
            }
        scene.render(0).close()
        fun click(node: SemanticsNode) {
            check(node.config[SemanticsActions.OnClick].action!!.invoke())
            scene.render(1_000_000_000L).close()
        }
        val toggle =
            scene.nodes().single {
                it.text().contains("Test toggle") &&
                    it.config.getOrNull(SemanticsProperties.ToggleableState) != null
            }
        check(toggle.config[SemanticsProperties.Role] == Role.Switch)
        click(toggle)
        check(toggles == 1)
        check(
            scene
                .nodes()
                .single { it.text().contains("Test toggle") }
                .config[SemanticsProperties.ToggleableState] == ToggleableState.On
        )
        val disabled = scene.nodes().single { it.text().contains("Disabled toggle") }
        check(disabled.config.contains(SemanticsProperties.Disabled))
        val dualSwitch =
            scene.nodes().single {
                it.config
                    .getOrNull(SemanticsProperties.ContentDescription)
                    ?.contains("Edit service") == true
            }
        click(dualSwitch)
        check(dualToggles == 1 && opens == 0)
        click(scene.nodes().single { it.text() == "Edit service" })
        check(opens == 1)
        val radios =
            scene.nodes().filter {
                it.config.getOrNull(SemanticsProperties.Role) == Role.RadioButton
            }
        check(radios.size == 3 && radios.all { it.boundsInRoot.height >= 96f })
        click(radios.last())
        check(selected == 2)
        val slider = scene.nodes().single { it.config.contains(SemanticsActions.SetProgress) }
        check(slider.config[SemanticsActions.SetProgress].action!!.invoke(8f))
        scene.render(1_000_000_000L).close()
        check(commits == 1)
        val action = scene.nodes().single { it.text() == "Dialog action" }
        check(
            action.config[SemanticsProperties.Role] == Role.Button &&
                action.boundsInRoot.height >= 112f
        )
        click(action)
        check(opens == 2)
        check(
            scene
                .nodes()
                .single { it.text() == "Disabled action" }
                .config
                .contains(SemanticsProperties.Disabled)
        )
        // Pointer drag proves that draft values do not persist on every move.
        val bounds = slider.boundsInRoot
        val y = bounds.center.y
        val beforeDrag = commits
        scene.sendPointerEvent(
            PointerEventType.Press,
            Offset(bounds.left + 20f, y),
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        scene.render(2_000_000_000L).close()
        scene.sendPointerEvent(
            PointerEventType.Move,
            Offset(bounds.right - 25f, y),
            buttons = PointerButtons(isPrimaryPressed = true),
        )
        scene.render(2_100_000_000L).close()
        check(commits == beforeDrag)
        scene.sendPointerEvent(
            PointerEventType.Release,
            Offset(bounds.right - 25f, y),
            buttons = PointerButtons(),
        )
        scene.render(2_200_000_000L).close()
        check(commits == beforeDrag + 1)
        val inputNode =
            scene.nodes().single {
                it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains("Username") ==
                    true
            }
        check(inputNode.boundsInRoot.height >= 104f) {
            "Editor bounds=${inputNode.boundsInRoot}; touch=${inputNode.touchBoundsInRoot}"
        }
        check(
            inputNode.config[SemanticsActions.SetText].action!!.invoke(AnnotatedString("listener"))
        )
        scene.render(3_000_000_000L).close()
        check(
            scene
                .nodes()
                .single {
                    it.config
                        .getOrNull(SemanticsProperties.ContentDescription)
                        ?.contains("Username") == true
                }
                .config[SemanticsProperties.EditableText]
                .text == "listener"
        )
        check(
            scene
                .nodes()
                .single {
                    it.config
                        .getOrNull(SemanticsProperties.ContentDescription)
                        ?.contains("Password") == true
                }
                .config
                .contains(SemanticsProperties.Password)
        )
        scene.close()
        println(
            "PASS: 16 native Compose checks: switch role, one toggle callback, state update, disabled semantics, separate edit/toggle actions, radio targets >=48dp, radio callback, slider accessibility commit, dialog button role/minimum height, dialog callback, disabled dialog semantics, no writes while dragging, one write after release, editor height >=52dp, editable labeled field, password semantics."
        )
        println("Rendered actual SettingsControls.kt to ${out.absolutePath}")
    }

private data class Quad(val kind: String, val width: Int, val height: Int, val scale: Float)
