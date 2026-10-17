pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // NewPipeExtractor is published via JitPack.
        maven("https://jitpack.io")
    }
}

rootProject.name = "Veyra"
include(":shared")
include(":sharedUi")

// Always include the phone app so Android Studio can discover it
// during the first import, before it writes local.properties. Android Studio
// or Gradle's Android plugin handles SDK discovery and reports missing SDKs.
include(":app")
