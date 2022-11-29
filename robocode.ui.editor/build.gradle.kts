plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.battle"))
    implementation(project(":robocode.ui"))
    implementation("org.picocontainer:picocontainer:2.15")
}

description = "Robocode UI Robot editor"

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}