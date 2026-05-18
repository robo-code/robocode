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
            languageVersion.set(JavaLanguageVersion.of(8)) // Java 8
        }
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

tasks {
    named("clean") {
        delete(".sandbox")
    }

    val validateReleaseCredentials by registering {
        group = "publishing"
        description = "Pre-flight credential checks required for release"
        doLast {
            val signingKey      = project.findProperty("signingKey") as String?
            val signingPassword = project.findProperty("signingPassword") as String?
            val ossrhUser       = project.findProperty("ossrhUsername") as String?
            val ossrhPass       = project.findProperty("ossrhPassword") as String?
            val chocoKey        = System.getenv("CHOCOLATEY_API_KEY")
            val onWindows       = System.getProperty("os.name").lowercase().contains("windows")

            val labels  = mutableListOf<String>()
            val results = mutableListOf<Boolean>()
            fun check(label: String, value: String?, rejectDummy: Boolean = true) {
                labels.add(label)
                results.add(!value.isNullOrBlank() && (!rejectDummy || value != "dummy"))
            }

            val ghOk = ProcessBuilder("gh", "auth", "status")
                .redirectErrorStream(true).start().waitFor() == 0

            val expectedTag = "v${project.version}"
            val gitTagProc  = ProcessBuilder("git", "tag", "--points-at", "HEAD")
                .redirectErrorStream(true).start()
            val tagOk = gitTagProc.inputStream.bufferedReader().readLines()
                .any { it.trim() == expectedTag }
                .also { gitTagProc.waitFor() }

            check("signingKey",      signingKey)
            check("signingPassword", signingPassword, rejectDummy = false)
            check("ossrhUsername",   ossrhUser)
            check("ossrhPassword",   ossrhPass)
            if (onWindows) check("CHOCOLATEY_API_KEY (env var)", chocoKey, rejectDummy = false)

            println("\n🔑 Credential check:")
            labels.zip(results).forEach { (label, ok) -> println("  ${if (ok) "✅" else "❌"} $label") }
            if (!onWindows) println("  ⏭️  CHOCOLATEY_API_KEY (skipped — Chocolatey publish requires Windows)")
            println("  ${if (ghOk)  "✅" else "❌"} GitHub CLI (gh auth status)")
            println("  ${if (tagOk) "✅" else "❌"} git tag $expectedTag on HEAD")
            println()

            val failures = labels.zip(results).filter { !it.second }.map { it.first } +
                listOfNotNull(
                    if (!ghOk)  "GitHub CLI (gh auth status)" else null,
                    if (!tagOk) "git tag $expectedTag not found on HEAD — run: git tag $expectedTag" else null
                )
            if (failures.isNotEmpty()) {
                throw GradleException(
                    "Release pre-flight failed — missing or invalid credentials:\n" +
                    failures.joinToString("\n") { "  ❌ $it" } +
                    "\n\nSee release.md → Prerequisites for setup instructions."
                )
            }
            println("✅ All credentials present. Starting release...\n")
        }
    }

    val createGitHubRelease by registering(Exec::class) {
        group = "publishing"
        description = "Creates a GitHub release and attaches the setup JAR"
        val version = project.version.toString()
        commandLine(
            "gh", "release", "create", "v$version",
            "--title", "Robocode $version",
            "--notes", "See [versions.md](https://github.com/robo-code/robocode/blob/main/versions.md) for release notes.",
            "build/robocode-$version-setup.jar"
        )
    }

    register("release") {
        group = "publishing"
        description = "Full release: pre-flight checks, build, Maven Central, Chocolatey (Windows), GitHub Release"
        dependsOn(validateReleaseCredentials)
        dependsOn("build")
        dependsOn(createGitHubRelease)
        // Per-subproject publish tasks, closeAndRelease, and ordering are wired in projectsEvaluated below
    }
}

// Use projectsEvaluated (runs after ALL afterEvaluate callbacks, including the nexus plugin's)
// so that per-subproject publishAllPublicationsToSonatypeRepository tasks are already registered.
gradle.projectsEvaluated {
    val releaseTask   = tasks.named("release")
    val validateCreds = tasks.named("validateReleaseCredentials")
    tasks.named("build") { mustRunAfter(validateCreds) }

    val publishTasks = subprojects
        .mapNotNull { it.tasks.findByName("publishAllPublicationsToSonatypeRepository") }

    publishTasks.forEach { publishTask ->
        releaseTask.configure { dependsOn(publishTask) }
        publishTask.mustRunAfter("build")
    }

    tasks.findByName("closeAndReleaseSonatypeStagingRepository")?.let { closeTask ->
        releaseTask.configure { dependsOn(closeTask) }
        closeTask.mustRunAfter(*publishTasks.toTypedArray())
    }

    val onWindows   = System.getProperty("os.name").lowercase().contains("windows")
    val chocoKey    = System.getenv("CHOCOLATEY_API_KEY")
    val closeTask   = tasks.findByName("closeAndReleaseSonatypeStagingRepository")
    val ghRelease   = tasks.named("createGitHubRelease")

    var lastBeforeGhRelease: Any = closeTask ?: "build"

    if (onWindows && !chocoKey.isNullOrBlank()) {
        project(":robocode.installer").tasks.findByName("chocoPush")?.let { chocoPush ->
            releaseTask.configure { dependsOn(chocoPush) }
            if (closeTask != null) chocoPush.mustRunAfter(closeTask)
            lastBeforeGhRelease = chocoPush
        }
    }

    ghRelease.configure { mustRunAfter(lastBeforeGhRelease) }
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