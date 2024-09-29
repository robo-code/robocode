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
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") &&
                    (it.name.contains("eclipse") || (it.name.startsWith("ecj")))}.map { it }
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
