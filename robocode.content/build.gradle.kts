plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

description = "Robocode Content"

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
    runtimeOnly("org.eclipse.jdt:org.eclipse.jdt.core:3.24.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
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
