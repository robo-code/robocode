rootProject.name = "robocode"

val version: String = providers.gradleProperty("version").get()

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