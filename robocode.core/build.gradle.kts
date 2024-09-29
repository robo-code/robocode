plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":robocode.api"))
    implementation(libs.picocontainer)

    testImplementation(testLibs.junit)
}

description = "Robocode Core"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    register<Copy>("copyVersion") {
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