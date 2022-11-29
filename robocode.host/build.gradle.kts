plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation("org.picocontainer:picocontainer:2.15")
    testImplementation("junit:junit:4.13.2")
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