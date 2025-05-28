plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation(libs.picocontainer)
}

description = "Robocode Sound"

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/sound/Module.java")
    }

    jar {
        dependsOn(javadoc)
    }
}