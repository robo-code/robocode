plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    runtimeOnly(project(":robocode.core"))
    runtimeOnly(project(":robocode.battle"))
    runtimeOnly(libs.codesize)
    testImplementation(testLibs.junit.jupiter)
    testRuntimeOnly(testLibs.junit.platform.launcher)
}

description = "Roborumble Client"

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "roborumble.RoboRumbleAtHome"))
    }
    archiveFileName.set("roborumble.jar")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        source = sourceSets["main"].java
        include("roborumble/RoboRumbleAtHome.java")
    }

    jar {
        dependsOn(javadoc)
    }
}