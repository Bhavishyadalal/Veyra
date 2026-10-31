pluginManagement {
    repositories {
        google()
        mavenCentral()
        // Same Maven Central artifacts through Sonatype's alternate endpoint.
        // Fresh hosted runners have intermittently received missing-artifact
        // responses from repo.maven.apache.org during plugin resolution.
        maven("https://repo1.maven.org/maven2")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2")
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
