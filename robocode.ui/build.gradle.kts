plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.battle"))
    implementation("org.picocontainer:picocontainer:2.14.2")
    runtimeOnly(project(":robocode.sound"))
}

description = "Robocode UI"
