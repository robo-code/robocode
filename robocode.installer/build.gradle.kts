plugins {
    id("net.sf.robocode.java-conventions")
    application
}

description = "Robocode Installer"

dependencies {
    // These Robocode artifacts must be installed
    runtimeOnly(project(":robocode.api"))
    runtimeOnly(project(":robocode.battle"))
    runtimeOnly(project(":robocode.core"))
    runtimeOnly(project(":robocode.content"))
    runtimeOnly(project(":robocode.host"))
    runtimeOnly(project(":robocode.repository"))
    runtimeOnly(project(":robocode.roborumble"))
    runtimeOnly(project(":robocode.samples"))
    runtimeOnly(project(":robocode.sound"))
    runtimeOnly(project(":robocode.ui"))
    runtimeOnly(project(":robocode.ui.editor"))
    runtimeOnly(project(":robocode.content"))

    // The built-in Eclipse Compiler for Java (ECJ) must also be installed
    runtimeOnly(libs.eclipse.jdt)
}

tasks {
    jar {
        dependsOn(configurations.runtimeClasspath)

        manifest {
            attributes(mapOf("Main-Class" to "net.sf.robocode.installer.AutoExtract"))
        }
        archiveFileName.set("robocode-${project.version}-setup.jar")
        destinationDirectory.set(file("${layout.buildDirectory.get()}/../../build"))

        into("javadoc") {
            from("../robocode.api/build/docs/javadoc")
        }

        into("libs") {
            from({
                configurations.runtimeClasspath.get().filter {
                    it.name.endsWith("jar") &&
                            !it.name.contains("eclipse") &&
                            !it.name.startsWith("ecj") &&
                            !it.name.contains("robocode.samples") &&
                            !it.name.contains("robocode.content")
                }.map { it }
            })
        }
        into("compilers") {
            from({
                configurations.runtimeClasspath.get().filter {
                    it.name.endsWith("jar") &&
                            (it.name.contains("eclipse") || (it.name.startsWith("ecj")))
                }.map { it }
            })
        }
        into("robots") {
            from({
                configurations.runtimeClasspath.get()
                    .filter { it.name.endsWith("jar") && it.name.contains("robocode.samples") }.map {
                        zipTree(it)
                    }
            }) {
                exclude("META-INF")
                exclude("META-INF/**")
            }
        }
        into("") {
            from({
                configurations.runtimeClasspath.get()
                    .filter { it.name.endsWith("jar") && it.name.contains("robocode.content") }.map {
                        zipTree(it)
                    }
            })
            from("../versions.md")
        }
    }

    val dockerBuild by registering(Exec::class) {
        dependsOn(jar)
        workingDir = file("../")
        commandLine(
            "docker",
            "build",
            "-t",
            "zamboch/roborumble:${project.version}",
            "-t",
            "zamboch/roborumble:latest",
            "."
        )
    }

    val dockerPush by registering(Exec::class) {
        dependsOn(dockerBuild)

        workingDir = file("../")
        commandLine("docker", "push", "zamboch/roborumble", "--all-tags")
    }

    val chocoClean by registering(Delete::class) {
        delete("build/choco")
    }

    val chocoCopy by registering(Copy::class) {
        dependsOn(chocoClean)
        dependsOn(jar)

        from("../build/") {
            include("robocode-${project.version}-setup.jar")
            include("robocode-${project.version}-setup.jar.asc")
            into("tools")
        }
        from("src/chocolatey/") {
            include("**")
        }
        into("build/choco")
    }

    val chocoBuild by registering(Exec::class) {
        dependsOn(chocoCopy)

        workingDir = file("build/choco")
        commandLine("choco", "pack", "--version", "${project.version}")
    }

    val chocoPush by registering(Exec::class) {
        dependsOn(chocoBuild)

        workingDir = file("build/choco")
        commandLine(
            "choco",
            "push",
            "robocode.${project.version}.nupkg",
            "-s",
            "https://push.chocolatey.org/",
            "--api-key",
            System.getenv("CHOCOLATEY_API_KEY")
        )
    }

    build {
        // dependsOn("chocoBuild")
        // dependsOn("dockerBuild")
    }

    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}