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

    task<Exec>("dockerBuild") {
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

    task<Exec>("dockerPush") {
        dependsOn("dockerBuild")
        workingDir = file("../")
        commandLine("docker", "push", "zamboch/roborumble", "--all-tags")
    }

    task<Delete>("chocoClean") {
        delete("build/choco")
    }

    task<Copy>("chocoCopy") {
        dependsOn("chocoClean")
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

    task<Exec>("chocoBuild") {
        dependsOn("chocoCopy")
        workingDir = file("build/choco")
        commandLine("choco", "pack", "--version", "${project.version}")
    }

    task<Exec>("chocoPush") {
        dependsOn("chocoBuild")
        workingDir = file("build/choco")
        commandLine("choco", "push", "robocode.${project.version}.nupkg", "-s", "https://push.chocolatey.org/")
    }

    build {
        // dependsOn("chocoBuild")
        // dependsOn("dockerBuild")
    }

    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}