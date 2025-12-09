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

    // Configure signing for all subprojects with both signing and maven-publish plugins
    plugins.withId("maven-publish") {
        plugins.withId("signing") {
            the<SigningExtension>().apply {
                val signingKey: String? by project
                val signingPassword: String? by project

                if (!signingKey.isNullOrBlank()) {
                    useInMemoryPgpKeys(signingKey, signingPassword)
                }

                // Make signing required only when a key is provided
                isRequired = !signingKey.isNullOrBlank()

                if (isRequired) {
                    sign(the<PublishingExtension>().publications)
                }
            }
        }
    }

    // Include robocode.ico in the published artifacts without cross-project output conflicts
    // We copy the icon into a subproject-local build directory, so each module signs its own copy.
    plugins.withId("maven-publish") {
        val prepareRobocodeIcon = tasks.register<Copy>("prepareRobocodeIcon") {
            val srcIcon = file("${rootProject.projectDir}/robocode.content/src/main/resources/robocode.ico")
            val destDir = layout.buildDirectory.dir("publication-resources/icon").get().asFile
            from(srcIcon)
            into(destDir)
            outputs.file(file("${destDir}/robocode.ico"))
        }

        configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                val copiedIcon = layout.buildDirectory.file("publication-resources/icon/robocode.ico").get().asFile
                artifact(copiedIcon) {
                    builtBy(prepareRobocodeIcon)
                    classifier = "icon"
                    extension = "ico"
                }
            }
        }

        // Ensure sign tasks depend on icon preparation
        tasks.withType<Sign>().configureEach {
            dependsOn(prepareRobocodeIcon)
        }
    }
}