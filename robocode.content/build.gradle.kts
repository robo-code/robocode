plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

description = "Robocode Content"

dependencies {
    runtimeOnly(libs.eclipse.jdt)

    // Robocode supports Kotlin out of the box
    runtimeOnly(libs.kotlin.stdlib)

    // BCEL is required for Codesize
    runtimeOnly(libs.bcel)

    runtimeOnly(libs.picocontainer)
}

tasks {
    val copyContent by registering(Copy::class) {
        from("src/main/resources") {
            include("**/*.*")
        }
        from("../") {
            include("versions.md")
        }
        into("../.sandbox")
    }

    val copyExternalLibs by registering(Copy::class) {
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { it }
        })
        into("../.sandbox/libs")
    }

    val copyCompilers by registering(Copy::class) {
        from({
            configurations.runtimeClasspath.get().filter {
                it.name.endsWith("jar") &&
                        (it.name.contains("eclipse") || (it.name.startsWith("ecj")))
            }.map { it }
        })
        into("../.sandbox/compilers")
    }

    val copyRobocodeApiLib by registering(Copy::class) {
        dependsOn(project(":robocode.api").tasks.named("jar"))

        from(
            project(":robocode.api").fileTree("build/libs").matching {
                include("robocode.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeBattleLib by registering(Copy::class) {
        dependsOn(project(":robocode.battle").tasks.named("jar"))

        from(
            project(":robocode.battle").fileTree("build/libs").matching {
                include("robocode.battle-*.jar")
                exclude("robocode.battle-*-javadoc.jar", "robocode.battle-*-sources.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeCoreLib by registering(Copy::class) {
        dependsOn(project(":robocode.core").tasks.named("jar"))

        from(
            project(":robocode.core").fileTree("build/libs").matching {
                include("robocode.core-*.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeHostLib by registering(Copy::class) {
        dependsOn(project(":robocode.host").tasks.named("jar"))

        from(
            project(":robocode.host").fileTree("build/libs").matching {
                include("robocode.host-*.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeRepositoryLib by registering(Copy::class) {
        dependsOn(project(":robocode.repository").tasks.named("jar"))

        from(
            project(":robocode.repository").fileTree("build/libs").matching {
                include("robocode.repository-*.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeSoundLib by registering(Copy::class) {
        dependsOn(project(":robocode.sound").tasks.named("jar"))

        from(
            project(":robocode.sound").fileTree("build/libs").matching {
                include("robocode.sound-*.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeUiLib by registering(Copy::class) {
        dependsOn(project(":robocode.ui").tasks.named("jar"))

        from(
            project(":robocode.ui").fileTree("build/libs").matching {
                include("robocode.ui-*.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    val copyRobocodeUiEditorLib by registering(Copy::class) {
        dependsOn(project(":robocode.ui.editor").tasks.named("jar"))

        from(
            project(":robocode.ui.editor").fileTree("build/libs").matching {
                include("robocode.ui.editor-*.jar")
            }
        )
        into("../.sandbox/libs") // Copy to the target directory
    }

    assemble {
        dependsOn(copyRobocodeApiLib)
        dependsOn(copyRobocodeBattleLib)
        dependsOn(copyRobocodeCoreLib)
        dependsOn(copyRobocodeHostLib)
        dependsOn(copyRobocodeRepositoryLib)
        dependsOn(copyRobocodeSoundLib)
        dependsOn(copyRobocodeUiLib)
        dependsOn(copyRobocodeUiEditorLib)

        dependsOn(copyContent)
        dependsOn(copyExternalLibs)
        dependsOn(copyCompilers)
    }
}
