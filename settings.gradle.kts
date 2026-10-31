rootProject.name = "robocode"

val version: String = providers.gradleProperty("version").get()

// When releasing, a failing release step (e.g. Chocolatey) must not stop the other release steps (e.g. SourceForge).
// Steps that depend on a failed step are still skipped, and the build fails at the end, if any step failed.
if (gradle.startParameter.taskNames.any { it == "release" || it == ":release" }) {
    gradle.startParameter.isContinueOnFailure = true
}

include("robocode.api")
include("robocode.battle")
include("robocode.core")
include("robocode.host")
include("robocode.installer")
include("robocode.content")
include("robocode.repository")
include("robocode.roborumble")
include("robocode.samples")
include("robocode.sound")
include("robocode.tests")
include("robocode.tests.robots")
include("robocode.ui")
include("robocode.ui.editor")
include("robocode.main")

// Check dependencies with this command:  gradlew dependencyUpdates -Drevision=release
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("tankroyale", version)
        }
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
    }
}