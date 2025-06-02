plugins {
    id("net.sf.robocode.java-conventions")
    application
    `java-library`
}

description = "Robocode Main application"

dependencies {
    api(project(":robocode.api"))
}

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}