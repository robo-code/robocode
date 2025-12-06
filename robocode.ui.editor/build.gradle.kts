plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.ui"))
    implementation(libs.picocontainer)
}

description = "Robocode UI Robot editor"

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}