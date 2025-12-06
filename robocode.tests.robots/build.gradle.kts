plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.samples"))
}

description = "Robocode Tested Robots"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

    }

    val copyRobotClasses by registering(Copy::class) {
        dependsOn(compileJava)

        from("build/classes/java/main")
        into("../.sandbox/test-robots")
    }

    jar {
        dependsOn(copyContent)
        dependsOn(copyRobotClasses)
    }
}

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}