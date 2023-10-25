plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.host"))
    implementation("org.picocontainer:picocontainer:2.15")
    implementation("net.sf.robocode:codesize:1.3.0")
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
        dependsOn("javadoc")
    }
}