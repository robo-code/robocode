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
    idea
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.ben.manes.versions)
}

description = "Robocode - Build the best - destroy the rest!"

val ossrhUsername: String by project
val ossrhPassword: String by project

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}

tasks {
    named("clean") {
        delete(".sandbox")
    }
}

nexusPublishing {
    repositories {
        sonatype {
            // Publishing By Using the Portal OSSRH Staging API:
            // https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            stagingProfileId.set("c7f511545ccf8")
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}

val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }

    // Apply common signing configuration to all subprojects
    plugins.withId("signing") {
        configure<SigningExtension> {
            useGpgCmd() // Use GPG agent instead of key file
        }
    }

    // Include Tank.ico in the published artifacts
    plugins.withId("maven-publish") {
        configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                artifact(file("${rootProject.projectDir}/gfx/Tank/Tank.ico")) {
                    classifier = "icon"
                    extension = "ico"
                }
            }
        }
    }

    // Include robocode.ico in the published artifacts
    plugins.withId("maven-publish") {
        configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                artifact(file("${rootProject.projectDir}/robocode.content/src/main/resources/robocode.ico")) {
                    classifier = "icon"
                    extension = "ico"
                }
            }
        }
    }
}