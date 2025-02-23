plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(project(":robocode.samples"))
}

description = "Robocode Tested Robots"

tasks {
    val copyContent by registering(Copy::class) {
        from("src/main/resources") {
            include("**/*.*")
        }
        into("../.sandbox/robots")
    }

    val copyClasses by registering(Copy::class) {
        dependsOn(configurations.runtimeClasspath)

        from(compileJava)
        into("../.sandbox/robots")
    }

    jar {
        dependsOn(copyContent)
        dependsOn(copyClasses)
    }
}

tasks {
    publishMavenJavaPublicationToSonatypeRepository {
        enabled = false
    }
}