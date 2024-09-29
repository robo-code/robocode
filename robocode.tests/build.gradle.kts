plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(testLibs.junit)

    testImplementation(project(":robocode.core"))
    testImplementation(project(":robocode.host"))
    testImplementation(project(":robocode.battle"))
    testImplementation(project(":robocode.tests.robots"))
}

description = "Robocode Tests"

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}