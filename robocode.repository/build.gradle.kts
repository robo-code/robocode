plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.host"))
    implementation(libs.picocontainer)
    testImplementation(testLibs.junit.jupiter)
    testRuntimeOnly(testLibs.junit.platform.launcher)
}

description = "Robocode Repository"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/repository/Module.java")
    }

    jar {
        dependsOn(javadoc)
    }
}