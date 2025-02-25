plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
}

description = "Robocode Samples"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    val copyContent by registering(Copy::class) {
        from("src/main/resources") {
            include("**/*.*")
        }
        from("src/main/java") {
            include("**")
        }
        into("../.sandbox/robots")
    }

    val copyRobotClasses by registering(Copy::class) {
        dependsOn(compileJava)

        from("build/classes/java/main")
        into("../.sandbox/robots")
    }

    val copyRobotClassesToTestRobots by registering(Copy::class) {
        dependsOn(compileJava)

        from("build/classes/java/main")
        into("../.sandbox/test-robots")
    }

    javadoc {
        source = sourceSets["main"].java
        include("**/*.java")
    }

    jar {
        dependsOn(copyContent)
        dependsOn(copyRobotClasses)
        dependsOn(copyRobotClassesToTestRobots)
        dependsOn(javadoc)

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from("src/main/java") {
            include("**")
        }
        from("src/main/resources") {
            include("**")
        }
    }
}