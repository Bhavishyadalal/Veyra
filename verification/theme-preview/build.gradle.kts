plugins {
    kotlin("jvm") version "2.3.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20"
    application
}
repositories { mavenCentral(); google() }
dependencies {
    implementation("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:1.10.3")
    implementation("org.jetbrains.compose.material3:material3-desktop:1.7.3")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.7.3")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose-desktop:2.8.4")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
}
kotlin { jvmToolchain(17) }
val veyraRoot = projectDir.resolve("../..").canonicalFile
sourceSets.main {
    kotlin.srcDir(veyraRoot.resolve("sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui"))
    kotlin.srcDir(veyraRoot.resolve("app/src/main/java/app/veyra/music/ui/screens"))
    kotlin.srcDir(veyraRoot.resolve("app/src/main/java/app/veyra/music/ui/components"))
    kotlin.include("RenderThemes.kt", "PlayerSettingsAdapter.kt", "**/ThemeVisuals.kt",
        "**/PlayerControlPrimitives.kt", "**/PlayerInteractionPolicy.kt", "**/PlayerScrubState.kt",
        "**/ReadableLayout.kt", "**/Customization.kt", "**/VeyraSurfaces.kt", "**/VeyraMotion.kt", "**/VeyraIcons.kt", "**/SettingsIcons.kt",
        "SettingsControls.kt", "VeyraSwitchColors.kt")
}
application {
    mainClass.set("app.veyra.music.ui.player.RenderThemesKt")
    applicationDefaultJvmArgs=listOf("-Dveyra.root=$veyraRoot")
}
