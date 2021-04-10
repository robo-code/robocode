plugins {
    id("net.sf.robocode.java-conventions")
    application
}

description = "Robocode Installer"

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.battle"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.content"))
    implementation(project(":robocode.host"))
    implementation(project(":robocode.repository"))
    implementation(project(":robocode.roborumble"))
    implementation(project(":robocode.samples"))
    implementation(project(":robocode.sound"))
    implementation(project(":robocode.ui"))
    implementation(project(":robocode.ui.editor"))
    implementation(project(":robocode.content"))
    runtimeOnly("org.eclipse.jdt:org.eclipse.jdt.core:3.24.0")
}

tasks {
    jar {
        dependsOn(configurations.runtimeClasspath)

        manifest {
            attributes(mapOf("Main-Class" to "net.sf.robocode.installer.AutoExtract"))
        }
        archiveFileName.set("robocode-${project.version}-setup.jar")
        destinationDirectory.set(file("$buildDir/../../build"))

        into("javadoc") {
            from("../robocode.api/build/docs/javadoc")
        }

        into("libs") {
            from({
                configurations.runtimeClasspath.get().filter {
                    it.name.endsWith("jar") && !it.name.contains("eclipse") && !it.name.contains("robocode.samples") && !it.name.contains(
                            "robocode.content"
                    )
                }.map { it }
            })
        }
        into("compilers") {
            from({
                configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && it.name.contains("eclipse") }
                        .map { it }
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
        dependsOn("jar")
        workingDir = file("../")
        commandLine("docker", "build", "-t", "zamboch/roborumble:${project.version}" , "-t", "zamboch/roborumble:latest", ".")
    }

    task<Exec>("dockerPush") {
        dependsOn("dockerBuild")
        workingDir = file("../")
        commandLine("docker", "push", "zamboch/roborumble","--all-tags")
    }

    build {
//        dependsOn("dockerBuild")
    }

    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}