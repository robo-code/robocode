plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

description = "Robocode Content"

dependencies {
    runtimeOnly("org.eclipse.jdt:org.eclipse.jdt.core:3.32.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-stdlib:1.8.10") // Robocode supports Kotlin out of the box
    runtimeOnly("org.apache.bcel:bcel:6.7.0") // for Codesize
}

tasks {
    register("copyContent", Copy::class) {
        from("src/main/resources") {
            include("**/*.*")
        }
        from("../") {
            include("versions.md")
        }
        into("../.sandbox")
    }
    register("copyExternalLibs", Copy::class) {
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && it.name.contains("kotlin") }.map { it }
        })
        into("../.sandbox/libs")
    }
    register("copyCompilers", Copy::class) {
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") && it.name.contains("eclipse") }.map { it }
        })
        into("../.sandbox/compilers")
    }
    processResources {
        dependsOn("copyContent")
        dependsOn("copyExternalLibs")
        dependsOn("copyCompilers")
    }
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}
