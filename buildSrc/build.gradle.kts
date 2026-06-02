plugins {
    java
    `kotlin-dsl`
    alias(libs.plugins.nexus.publish)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.test.retry)
}