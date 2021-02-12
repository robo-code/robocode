/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("net.sf.robocode.java-conventions")
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation("org.picocontainer:picocontainer:2.14.2")
    testImplementation("junit:junit:4.11")
}

description = "Robocode Host"
