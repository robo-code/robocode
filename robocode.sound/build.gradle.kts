plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation("org.picocontainer:picocontainer:2.15")
}

description = "Robocode Sound"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/sound/Module.java")
    }
    jar {
        dependsOn("javadoc")
    }
}