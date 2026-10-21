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
    kotlin.include("RenderKeepsake.kt", "RenderAtelier.kt", "RenderThemes.kt", "PlayerSettingsAdapter.kt", "**/ThemeVisuals.kt", "**/ThemeNavigation.kt", "**/ThemeStudioComponents.kt", "**/VeyraBrandContent.kt",
        "**/PlayerControlPrimitives.kt", "**/PlayerInteractionPolicy.kt", "**/PlayerScrubState.kt",
        "**/ReadableLayout.kt", "**/Customization.kt", "**/VeyraSurfaces.kt", "**/VeyraMotion.kt", "**/VeyraIcons.kt", "**/SettingsIcons.kt",
        "SettingsControls.kt", "VeyraSwitchColors.kt", "**/HomeComponents.kt")
}
application {
    mainClass.set("app.veyra.music.ui.player.RenderThemesKt")
    applicationDefaultJvmArgs=listOf("-Dveyra.root=$veyraRoot")
}

tasks.named<JavaExec>("run") {
    systemProperty("veyra.study", providers.gradleProperty("study").getOrElse(""))
    systemProperty("veyra.performance", providers.gradleProperty("performance").getOrElse("false"))
    systemProperty("veyra.reliability", providers.gradleProperty("reliability").getOrElse("false"))
    systemProperty("veyra.theme", providers.gradleProperty("theme").getOrElse("halloween-nocturne"))
}

// Isolated native studies for standalone launcher choices; no existing theme renders run.
tasks.register<JavaExec>("runAtelier") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("app.veyra.music.ui.atelier.RenderAtelierKt")
    systemProperty("veyra.root", veyraRoot)
}

// Standard JavaExec tasks keep each new theme in an isolated JVM.
val worldIds = Regex("\\\"id\\\": \\\"([^\\\"]+)\\\"")
    .findAll(veyraRoot.resolve("themes/worlds-designs.json").readText()).map { it.groupValues[1] }.toList()
val worldExpanded = setOf("stained-glass-sonata", "cloud-post-office", "copper-canyon-radio", "blue-hour-sanctuary", "celestial-current", "silent-cinema")
check(worldIds.size == 20)
val worldStudies = worldIds.map { id ->
    tasks.register<JavaExec>("renderWorld-$id") {
        dependsOn("classes")
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.veyra.music.ui.player.RenderThemesKt")
        maxHeapSize = "1g"
        systemProperty("veyra.root", veyraRoot)
        systemProperty("veyra.theme", id)
        systemProperty("veyra.study", if (id in worldExpanded) "home,player,navigation-rtl-large-text" else "home,player")
    }
}
tasks.register("runWorlds") { dependsOn(worldStudies) }

tasks.register<JavaExec>("runKeepsakeUi") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("app.veyra.music.ui.keepsake.RenderKeepsakeKt")
    systemProperty("veyra.root", veyraRoot)
}
val keepsakeStudies = listOf("patchwork-picnic","clover-requiem","sunroom-mixtape","concrete-afterhours","matcha-mischief").map { id ->
    tasks.register<JavaExec>("renderKeepsake-$id") {
        dependsOn("classes")
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("app.veyra.music.ui.player.RenderThemesKt")
        maxHeapSize = "1g"
        systemProperty("veyra.root", veyraRoot)
        systemProperty("veyra.theme", id)
        systemProperty("veyra.study", "home,player,navigation-rtl-large-text")
    }
}
tasks.register("runKeepsake") { dependsOn(keepsakeStudies) }
