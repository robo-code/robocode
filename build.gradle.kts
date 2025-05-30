import org.jreleaser.model.Active
import org.jreleaser.model.Stereotype

/**
 * To build Robocode, you need to run this command:
 * ./gradlew build
 *
 * To clean the build, to run this command:
 * ./gradlew clean
 *
 * To run Robocode you need to build Robocode and do a `cd .sandbox` and write `robocode.bat` or `robocode.sh`
 *
 * The distribution file for Robocode is put into /build and named `robocode.x.x.x.x-setup.jar`
 * The x.x.x.x is a version number like e.g. 1.9.5.4
 *
 * You can run the distribution file with hava like this:
 * java -jar robocode.x.x.x.x-setup.jar
 */

plugins {
    `java-library`
    `maven-publish`
    signing
    idea
    alias(libs.plugins.jreleaser)
    alias(libs.plugins.ben.manes.versions)
}

description = "Robocode - Build the best - destroy the rest!"

// Environment variables for Maven Central Publisher API
val mavenCentralUsername: String by project
val mavenCentralPassword: String by project

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
        withSourcesJar()
        withJavadocJar()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                pom {
                    name = project.name
                    description = project.description ?: "Robocode module"
                    url = "https://github.com/robocode-dev/robocode"

                    licenses {
                        license {
                            name = "EPL-v10"
                            url = "http://www.eclipse.org/legal/epl-v10.html"
                        }
                    }

                    developers {
                        developer {
                            id = "robocode-dev"
                            name = "Robocode Development Team"
                        }
                    }

                    scm {
                        connection = "scm:git:git://github.com/robocode-dev/robocode.git"
                        developerConnection = "scm:git:ssh://github.com:robocode-dev/robocode.git"
                        url = "https://github.com/robocode-dev/robocode/tree/master"
                    }
                }
            }
        }

        repositories {
            maven {
                name = "staging"
                url = uri(layout.buildDirectory.dir("staging-deploy"))
            }
        }
    }

    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
    }

    // Fix the task dependency issue
    tasks.withType<PublishToMavenRepository>().configureEach {
        dependsOn(tasks.withType<Sign>())
    }
}

tasks {
    named("clean") {
        delete(".sandbox")
    }
}

jreleaser {
    project {
        name = "robocode"
        description = "Robocode: Build the best - destroy the rest!"
        longDescription =
            "Robocode is a programming game, where the goal is to develop a robot battle tank to battle against other tanks in Java."
        website = "https://robocode.sourceforge.io/"
        authors = listOf("Robocode Development Team")
        license = "EPL-v10"
        copyright = "2001-2025 Robocode Development Team"
    }

    release {
        github {
            skipRelease = true // Skip GitHub release creation for now
        }
    }

    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            // local staging repository
            artifactory {
                create("local") {
                    enabled = true // Always enable local staging
                    active = Active.SNAPSHOT // Always active regardless of version type

                    url = file("build/staging-deploy").toURI().toString()

                    stagingRepositories = listOf("build/staging-deploy")
                }
            }

            mavenCentral {
                create("releases") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"

                    stagingRepositories = listOf("build/staging-deploy")

                    username = mavenCentralUsername
                    password = mavenCentralPassword

                    // Support for snapshot releases
                    snapshotSupported = true
                }
            }
        }
    }

    distributions {
        create("robocode.api") {
            enabled = true
            active = Active.ALWAYS
            distributionType = org.jreleaser.model.Distribution.DistributionType.JAVA_BINARY
            stereotype = Stereotype.CLI

            artifact {
                path = file("robocode.api/build/libs/robocode.jar")
            }

            java {
                enabled = true
                mainClass = "robocode.Robocode"
            }
        }
    }
}

// Configure signing for subprojects
subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("maven-publish")) {
            configure<PublishingExtension> {
                repositories {
                    maven {
                        name = "staging"
                        url = uri("${rootProject.buildDir}/staging-deploy")
                        // Configure metadata sources explicitly
                        isAllowInsecureProtocol = false
                        metadataSources {
                            mavenPom()
                            artifact()
                        }
                    }
                }
            }
        }
    }
}