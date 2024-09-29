plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.battle"))
    runtimeOnly(project(":robocode.sound"))
    implementation(libs.picocontainer)
}

description = "Robocode UI"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/ui/Module.java")
    }

    jar {
        dependsOn(javadoc)
    }
}