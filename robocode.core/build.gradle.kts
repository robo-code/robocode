plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation("org.picocontainer:picocontainer:2.14.2")
    testImplementation("junit:junit:4.13.1")
}

description = "Robocode Core"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    register("copyVersion", Copy::class) {
        from("../") {
            include("versions.md")
        }
        into("build/resources/main/")
    }
    processResources{
        dependsOn("copyVersion")

    }
    javadoc {
        source = sourceSets["main"].java
        include("net/sf/robocode/core/Module.java")
    }
}