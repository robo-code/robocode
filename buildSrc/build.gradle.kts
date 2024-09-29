plugins {
    java
    `kotlin-dsl`
    alias(libs.plugins.nexus.publish)
}

repositories {
    mavenCentral()
}