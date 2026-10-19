plugins {
    kotlin("jvm") version "2.3.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20"
    application
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:1.10.3")
    implementation("org.jetbrains.compose.material3:material3-desktop:1.7.3")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.7.3")
}

kotlin { jvmToolchain(17) }

val veyraRoot = projectDir.resolve("../..").canonicalFile

sourceSets.main {
    kotlin.srcDir("${veyraRoot}/sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens")
    kotlin.srcDir("${veyraRoot}/sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/icons")
    kotlin.include("HomeComponents.kt", "HomeFeedIdentity.kt", "VeyraIcons.kt", "RenderHome.kt")
}

application {
    mainClass.set("app.veyra.music.ui.screens.RenderHomeKt")
    applicationDefaultJvmArgs =
        listOf(
            "-Dveyra.root=${veyraRoot}",
            "-Dveyra.preview.output=${veyraRoot}/design/home-preview",
        )
}
