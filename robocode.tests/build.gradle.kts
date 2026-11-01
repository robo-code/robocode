plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(testLibs.junit.jupiter)
    implementation(testLibs.hamcrest)
    testRuntimeOnly(testLibs.junit.platform.launcher)

    testImplementation(project(":robocode.core"))
    testImplementation(project(":robocode.host"))
    testImplementation(project(":robocode.battle"))
    testImplementation(project(":robocode.tests.robots"))
}

description = "Robocode Tests"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}