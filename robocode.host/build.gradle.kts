plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(libs.picocontainer)

    testImplementation(testLibs.junit.jupiter)
    testRuntimeOnly(testLibs.junit.platform.launcher)
}

description = "Robocode Host"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/host/Module.java")
    }

    jar {
        dependsOn(javadoc)
    }
}