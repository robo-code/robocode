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
        into("../.sandbox/test-robots")
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
