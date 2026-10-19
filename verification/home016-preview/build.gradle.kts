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
    implementation("org.jetbrains.compose.components:components-resources-desktop:1.10.3")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose-desktop:2.8.4")
}
kotlin { jvmToolchain(17) }
val veyraRoot = projectDir.resolve("../..").canonicalFile
val generated = veyraRoot.resolve("sharedUi/build/generated/compose/resourceGenerator")
sourceSets.main {
    kotlin.srcDir(veyraRoot.resolve("sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/screens"))
    kotlin.srcDir(veyraRoot.resolve("shared/src/jvmSharedMain/kotlin/app/veyra/music/data/home"))
    kotlin.srcDir(veyraRoot.resolve("shared/src/commonMain/kotlin/app/veyra/music/data/model"))
    kotlin.srcDir(generated.resolve("kotlin/commonResClass"))
    kotlin.srcDir(generated.resolve("kotlin/commonMainResourceAccessors"))
    kotlin.include("HomeExperience.kt", "HomeLayout.kt", "Models.kt", "**/Res.kt", "**/String*.kt",
        "RenderHome016.kt", "PageGutterAdapter.kt", "PlayerSettingsAdapter.kt")
}
tasks.processResources {
    from(generated.resolve("preparedResources/commonMain/composeResources")) {
        into("composeResources/app.veyra.music.sharedui.resources")
    }
}
application {
    mainClass.set("app.veyra.music.ui.screens.RenderHome016Kt")
    applicationDefaultJvmArgs = listOf("-Dveyra.root=$veyraRoot")
}
