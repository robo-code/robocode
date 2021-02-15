plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
}

description = "Robocode Samples"

tasks.jar {
    from("src/main/java") {
        include("**")
    }
    from("src/main/resources") {
        include("**")
    }
}

tasks {
    register("copyContent", Copy::class) {
        from("src/main/resources") {
            include("**/*.*")
        }
        from("src/main/java") {
            include("**")
        }
        into("../sandbox/robots")
    }
    register("copyClasses", Copy::class) {
        dependsOn(configurations.runtimeClasspath)

        from(compileJava)
        into("../sandbox/robots")
    }
    jar {
        dependsOn("copyContent")
        dependsOn("copyClasses")
    }
}