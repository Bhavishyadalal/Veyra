// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        // Keep D8/R8 newer than AGP's bundled dexer: the app uses Kotlin
        // 2.4 metadata in transitive dependencies. R8 9.1.56 also retains the
        // debug register-allocation fixes required by the full player screen.
        classpath("com.android.tools:r8:9.1.56")
    }
}
/* Stable 0.1.9; completed preview retains code 35 on its own branch. */
extra["veyraVersionCode"] = 40
extra["veyraVersionName"] = "0.1.9"

/*
 * Bump this by hand before cutting each sideloaded test build ("beta2",
 * "beta3", ...) and blank it out before cutting the real release. Marks the
 * versionName above as a pre-release: AppUpdateChecker.isNewer() treats any
 * "-suffix" as older than a clean release of the same number, so testers
 * still get the update prompt once the matching tag is actually published.
 *
 * Applied to release builds as well as debug ones, and that is the whole
 * point of it. A sideloaded beta is a *release* build — signed with the real
 * key, installed over the real package — so leaving the marker off it is
 * exactly the case that strands a tester: their build calls itself 1.6.1,
 * the published 1.6.1 then matches it, isNewer() says no, and no prompt ever
 * comes. Blanking this line is the one step that turns a beta into a release,
 * so it is the one place to get right.
 *
 */
extra["veyraBetaSuffix"] = ""

plugins {
    id("com.android.application") version "8.10.1" apply false
    id("com.android.library") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.3.20" apply false
    id("org.jetbrains.kotlin.jvm") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20" apply false
    id("org.jetbrains.compose") version "1.10.3" apply false
}
