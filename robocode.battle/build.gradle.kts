plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(project(":robocode.host"))
    runtimeOnly(project(":robocode.repository"))

    implementation(libs.picocontainer)
}

description = "Robocode Battle"

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/battle/Module.java")
    }
    jar {
        dependsOn(javadoc)
    }
}