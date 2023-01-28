plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation("junit:junit:4.13.2")
    testImplementation(project(":robocode.core"))
    testImplementation(project(":robocode.host"))
    testImplementation(project(":robocode.battle"))
    testImplementation(project(":robocode.repository"))
    implementation(project(":robocode.content"))
    implementation(project(":robocode.samples"))
    implementation(project(":robocode.tests.robots"))
}

description = "Robocode Tests"

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}