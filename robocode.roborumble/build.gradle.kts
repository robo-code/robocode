plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.core"))
    implementation("net.sf.robocode:codesize:1.3.0")
    runtimeOnly(project(":robocode.battle"))
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
        dependsOn("javadoc")
    }
}