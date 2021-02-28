plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation("org.picocontainer:picocontainer:2.14.2")
    testImplementation("junit:junit:4.13.1")
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
        dependsOn("javadoc")
    }
}