/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("net.sf.robocode.java-conventions")
}

description = "Robocode Installer"

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.battle"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.host"))
    implementation(project(":robocode.repository"))
    implementation(project(":robocode.roborumble"))
    implementation(project(":robocode.samples"))
    implementation(project(":robocode.sound"))
    implementation(project(":robocode.ui"))
    implementation(project(":robocode.ui.editor"))
    runtimeOnly("org.eclipse.jdt:org.eclipse.jdt.core:3.24.0")
}

tasks.jar{
    dependsOn(configurations.runtimeClasspath)

    //archiveClassifier.set("uber")
    manifest{
        attributes(mapOf("Main-Class" to "net.sf.robocode.installer.AutoExtract"))
    }
    archiveFileName.set("robocode-${project.version}-setup.jar.zip")
    destinationDirectory.set(file("$buildDir/../../target"))

    //from(sourceSets.main.get().output,into("sss"))


    into("javadoc"){
        from("../robocode.api/build/docs/javadoc")
    }

    into("libs"){
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && !it.name.contains("eclipse") && !it.name.contains("robocode.samples") }.map { it }
        })
    }
    into("compilers"){
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && it.name.contains("eclipse") }.map { it }
        })
    }
    into("robots"){
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && it.name.contains("robocode.samples") }.map { zipTree(it) }
        })
    }
    into(""){
        from("../versions.md")
    }
}