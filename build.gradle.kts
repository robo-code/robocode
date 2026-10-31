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

import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder

plugins {
    `java-library`
    idea
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.ben.manes.versions)
}

description = "Robocode - Build the best - destroy the rest!"

val ossrhUsername: String by project
val ossrhPassword: String by project

val releaseRequested = gradle.startParameter.taskNames.any { it == "release" || it == ":release" }

// The release steps in the order they run, by the label shown in the release summary
val releaseSteps = linkedMapOf<String, Task>()

val sourceForgeFilesDir = "/home/frs/project/robocode/robocode"
val sourceForgeVersionFile = "/home/project-web/robocode/htdocs/version/version.html"
val bloggerPostsUrl = "https://www.blogger.com/blog/posts/7868270166586050549"
val sourceForgeNewsUrl = "https://sourceforge.net/p/robocode/news/"

fun sourceForgeSshKey(): File =
    file((project.findProperty("sourceforgeSshKey") as String?) ?: "${System.getProperty("user.home")}/.ssh/sourceforge")

// Runs sftp commands on a SourceForge host with the SSH key, as SourceForge only accepts file uploads via SSH
fun sourceForgeSftp(host: String, vararg commands: String) {
    val user = project.findProperty("sourceforgeUsername") as String?
    val process = ProcessBuilder(
        "sftp", "-i", sourceForgeSshKey().path,
        "-o", "BatchMode=yes", "-o", "IdentitiesOnly=yes", "-o", "StrictHostKeyChecking=accept-new",
        "-b", "-", "$user@$host"
    ).redirectErrorStream(true).start()
    process.outputStream.bufferedWriter().use { it.write(commands.joinToString("\n", postfix = "\n")) }
    val output = process.inputStream.bufferedReader().readText()
    if (process.waitFor() != 0) {
        throw GradleException("sftp to $host failed:\n$output")
    }
}

fun runCommand(vararg command: String): String {
    val process = ProcessBuilder(*command).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().readText()
    if (process.waitFor() != 0) {
        throw GradleException("${command.joinToString(" ")} failed:\n$output")
    }
    return output.trim()
}

// Joins the hard-wrapped lines of versions.md, as GitHub renders each line break in release notes as a new line
fun unwrapMarkdown(markdown: String): String {
    val newBlock = Regex("^(#|[-*+] |\\d+\\. |```|\\|)")
    val lines = mutableListOf<String>()
    markdown.lines().forEach { line ->
        val text = line.trimStart()
        if (lines.isNotEmpty() && lines.last().isNotBlank() && !lines.last().trimStart().startsWith("#") &&
            text.isNotEmpty() && !newBlock.containsMatchIn(text)
        ) {
            lines[lines.size - 1] = lines.last() + " " + text
        } else {
            lines.add(line)
        }
    }
    return lines.joinToString("\n")
}

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

            // Only check the active account, as gh uses that, and other accounts might have expired tokens
            val ghOk = ProcessBuilder("gh", "auth", "status", "--active")
                .redirectErrorStream(true).start().waitFor() == 0

            val expectedTag = "v${project.version}"
            val gitTagProc  = ProcessBuilder("git", "tag", "--points-at", "HEAD")
                .redirectErrorStream(true).start()
            val tagOk = gitTagProc.inputStream.bufferedReader().readLines()
                .any { it.trim() == expectedTag }
                .also { gitTagProc.waitFor() }

            // gh release create makes the tag on GitHub from the HEAD commit, so the commit must be pushed
            val pushedProc = ProcessBuilder("git", "branch", "-r", "--contains", "HEAD")
                .redirectErrorStream(true).start()
            val pushedOk = pushedProc.inputStream.bufferedReader().readLines()
                .any { it.trim().startsWith("origin/") }
                .also { pushedProc.waitFor() }

            val versionHeader = file("versions.md").readLines()
                .firstOrNull { it.startsWith("## Version ${project.version} ") }
            val releaseNotesOk = versionHeader != null && !versionHeader.contains("not released yet")

            check("signingKey",      signingKey)
            check("signingPassword", signingPassword, rejectDummy = false)
            check("ossrhUsername",   ossrhUser)
            check("ossrhPassword",   ossrhPass)
            if (onWindows) check("CHOCOLATEY_API_KEY (env var)", chocoKey, rejectDummy = false)
            check("sourceforgeUsername",      project.findProperty("sourceforgeUsername") as String?)
            check("sourceforgeReleaseApiKey", project.findProperty("sourceforgeReleaseApiKey") as String?)

            val sshKey   = sourceForgeSshKey()
            val sshKeyOk = sshKey.isFile

            println("\n🔑 Credential check:")
            labels.zip(results).forEach { (label, ok) -> println("  ${if (ok) "✅" else "❌"} $label") }
            if (!onWindows) println("  ⏭️  CHOCOLATEY_API_KEY (skipped — Chocolatey publish requires Windows)")
            println("  ${if (sshKeyOk) "✅" else "❌"} SourceForge SSH key ($sshKey)")
            println("  ${if (ghOk)  "✅" else "❌"} GitHub CLI (gh auth status)")
            println("  ${if (tagOk) "✅" else "❌"} git tag $expectedTag on HEAD")
            println("  ${if (pushedOk) "✅" else "❌"} HEAD is pushed to origin")
            println("  ${if (releaseNotesOk) "✅" else "❌"} versions.md has a dated section for ${project.version}")
            println()

            val failures = labels.zip(results).filter { !it.second }.map { it.first } +
                listOfNotNull(
                    if (!sshKeyOk) "SourceForge SSH key not found: $sshKey (or set sourceforgeSshKey)" else null,
                    if (!ghOk)  "GitHub CLI (gh auth status)" else null,
                    if (!tagOk) "git tag $expectedTag not found on HEAD — run: git tag $expectedTag" else null,
                    if (!pushedOk) "HEAD is not pushed to origin — run: git push origin main" else null,
                    if (!releaseNotesOk) "versions.md has no section '## Version ${project.version} (<date>)'" else null
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

    // The section of the current version in versions.md, used as README.md on SourceForge and for SourceForge news
    val releaseNotesFile = layout.buildDirectory.file("release/release-notes.md")
    // The same section without hard-wrapped lines, used for the GitHub Release and the blog post
    val unwrappedReleaseNotesFile = layout.buildDirectory.file("release/release-notes-unwrapped.md")

    val prepareReleaseNotes by registering {
        group = "publishing"
        description = "Extracts the section of the current version from versions.md into build/release/"
        val version = project.version.toString()
        inputs.file("versions.md")
        outputs.files(releaseNotesFile, unwrappedReleaseNotesFile)
        doLast {
            val lines = file("versions.md").readLines()
            val start = lines.indexOfFirst { it.startsWith("## Version $version ") }
            if (start < 0) throw GradleException("versions.md has no section for version $version")
            val next = lines.drop(start + 1).indexOfFirst { it.startsWith("## Version ") }
            val end = if (next < 0) lines.size else start + 1 + next
            val notes = lines.subList(start, end).joinToString("\n").trim() + "\n"
            releaseNotesFile.get().asFile.writeText(notes)
            unwrappedReleaseNotesFile.get().asFile.writeText(unwrapMarkdown(notes))
        }
    }

    val createGitHubRelease by registering(Exec::class) {
        group = "publishing"
        description = "Creates a GitHub release with the release notes, tags HEAD, and attaches the setup JAR"
        dependsOn(prepareReleaseNotes)
        val version = project.version.toString()
        doFirst {
            commandLine(
                "gh", "release", "create", "v$version",
                "--target", runCommand("git", "rev-parse", "HEAD"),
                "--title", "Robocode $version",
                "--notes-file", unwrappedReleaseNotesFile.get().asFile.path,
                "build/robocode-$version-setup.jar"
            )
        }
    }

    val uploadToSourceForge by registering {
        group = "publishing"
        description = "Uploads the setup JAR and the release notes (as README.md) to SourceForge Files"
        dependsOn(prepareReleaseNotes)
        val version = project.version.toString()
        doLast {
            val setupJar = file("build/robocode-$version-setup.jar")
            if (!setupJar.isFile) throw GradleException("Setup JAR not found: $setupJar")
            val dir = "$sourceForgeFilesDir/$version"
            sourceForgeSftp(
                "frs.sourceforge.net",
                "-mkdir \"$dir\"", // '-' ignores the error, if the directory already exists
                "put \"${setupJar.invariantSeparatorsPath}\" \"$dir/\"",
                "put \"${releaseNotesFile.get().asFile.invariantSeparatorsPath}\" \"$dir/README.md\""
            )
            println("Uploaded to https://sourceforge.net/projects/robocode/files/robocode/$version/")
        }
    }

    val setSourceForgeDefaultDownload by registering {
        group = "publishing"
        description = "Makes the setup JAR the default download on SourceForge for Windows, macOS, Linux, and BSD"
        dependsOn(uploadToSourceForge)
        val version = project.version.toString()
        doLast {
            val apiKey = project.findProperty("sourceforgeReleaseApiKey") as String
            val url = "https://sourceforge.net/projects/robocode/files/robocode/$version/robocode-$version-setup.jar"
            val body = (listOf("windows", "mac", "linux", "bsd").map { "default=$it" } +
                    "api_key=${URLEncoder.encode(apiKey, "UTF-8")}").joinToString("&")

            // SourceForge might not know an uploaded file right away, so retry for a while
            var attempt = 1
            while (true) {
                val connection = URI(url).toURL().openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.doOutput = true
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.outputStream.use { it.write(body.toByteArray()) }
                val status = connection.responseCode
                if (status in 200..299) {
                    println("Default download on SourceForge is now robocode-$version-setup.jar")
                    break
                }
                val error = connection.errorStream?.bufferedReader()?.readText().orEmpty()
                if (status != 404 || attempt == 6) {
                    throw GradleException("SourceForge responded with HTTP $status: $error")
                }
                println("SourceForge does not know the file yet (HTTP 404), retrying in 10 seconds ($attempt/5)")
                Thread.sleep(10_000)
                attempt++
            }
        }
    }

    val updateSourceForgeVersion by registering {
        group = "publishing"
        description = "Writes the version to version.html, which Robocode reads to tell users about a new version"
        // Users are sent to the default download on SourceForge, so it must be ready before announcing the version
        dependsOn(setSourceForgeDefaultDownload)
        val version = project.version.toString()
        val versionFile = layout.buildDirectory.file("release/version.html")
        doLast {
            val file = versionFile.get().asFile
            file.parentFile.mkdirs()
            file.writeText(version) // Robocode reads the first line, and the file has no line break
            sourceForgeSftp("web.sourceforge.net", "put \"${file.invariantSeparatorsPath}\" \"$sourceForgeVersionFile\"")
            println("https://robocode.sourceforge.io/version/version.html is now $version")
        }
    }

    val prepareBlogPost by registering {
        group = "publishing"
        description = "Converts the release notes to HTML in build/release/blog-post.html for posting on Blogger"
        dependsOn(prepareReleaseNotes)
        val version = project.version.toString()
        val blogPostFile = layout.buildDirectory.file("release/blog-post.html")
        doLast {
            // GitHub renders the Markdown like on GitHub, incl. links to issues like #94
            val html = runCommand(
                "gh", "api", "markdown", "-f", "mode=gfm", "-f", "context=robo-code/robocode",
                "-F", "text=@${unwrappedReleaseNotesFile.get().asFile.path}"
            ).replace(" dir=\"auto\"", "").replace(" class=\"notranslate\"", "")
            val downloadUrl = "https://sourceforge.net/projects/robocode/files/robocode/$version/robocode-$version-setup.jar/download"
            blogPostFile.get().asFile.writeText(
                "$html\n\nDownload Robocode $version from\n<a href=\"$downloadUrl\">here</a>\n"
            )
            println("Blog post written to ${blogPostFile.get().asFile}")
        }
    }

    val releaseSummary by registering {
        group = "publishing"
        description = "Prints which release steps succeeded, failed, or were skipped, and the remaining manual steps"
        val version = project.version.toString()
        doLast {
            println("\n📋 Release summary for $version:")
            releaseSteps.forEach { (label, task) ->
                val state = task.state
                val result = when {
                    state.failure != null -> "❌ $label (failed, see the error below)"
                    state.executed -> "✅ $label"
                    else -> "⏭️  $label (not run, as a step it depends on failed)"
                }
                println("  $result")
            }
            println("\n✍️  Manual steps:")
            println("  - Post on Blogger with build/release/blog-post.html: $bloggerPostsUrl")
            println("  - Post on SourceForge news with build/release/release-notes.md: $sourceForgeNewsUrl")
            println()
        }
    }

    register("release") {
        group = "publishing"
        description = "Full release: pre-flight checks, build, Maven Central, Chocolatey (Windows), GitHub Release, " +
                "SourceForge, and blog post"
        dependsOn(validateReleaseCredentials)
        dependsOn(createGitHubRelease, updateSourceForgeVersion, prepareBlogPost)
        // Per-subproject publish tasks, closeAndRelease, builds, and ordering are wired in projectsEvaluated below
    }
}

// Use projectsEvaluated (runs after ALL afterEvaluate callbacks, including the nexus plugin's)
// so that per-subproject publishAllPublicationsToSonatypeRepository tasks are already registered.
gradle.projectsEvaluated {
    val releaseTask   = tasks.named("release")
    val validateCreds = tasks.named("validateReleaseCredentials")

    // The root build task does not build the subprojects, so the release depends on the build task of each project
    val buildTasks = allprojects.mapNotNull { it.tasks.findByName("build") }
    buildTasks.forEach { it.mustRunAfter(validateCreds) }
    releaseTask.configure { dependsOn(buildTasks) }

    val publishTasks = subprojects
        .mapNotNull { it.tasks.findByName("publishAllPublicationsToSonatypeRepository") }

    publishTasks.forEach { publishTask ->
        releaseTask.configure { dependsOn(publishTask) }
        publishTask.mustRunAfter(buildTasks)
    }

    tasks.findByName("closeAndReleaseSonatypeStagingRepository")?.let { closeTask ->
        releaseTask.configure { dependsOn(closeTask) }
        closeTask.mustRunAfter(*publishTasks.toTypedArray())
        releaseSteps["Maven Central"] = closeTask
    }

    val onWindows   = System.getProperty("os.name").lowercase().contains("windows")
    val chocoKey    = System.getenv("CHOCOLATEY_API_KEY")
    val closeTask   = tasks.findByName("closeAndReleaseSonatypeStagingRepository")
    val ghRelease   = tasks.named("createGitHubRelease")

    var lastBeforeGhRelease: Any = closeTask ?: buildTasks

    if (onWindows && !chocoKey.isNullOrBlank()) {
        project(":robocode.installer").tasks.findByName("chocoPush")?.let { chocoPush ->
            releaseTask.configure { dependsOn(chocoPush) }
            if (closeTask != null) chocoPush.mustRunAfter(closeTask)
            lastBeforeGhRelease = chocoPush
            releaseSteps["Chocolatey"] = chocoPush
        }
    }

    ghRelease.configure { mustRunAfter(lastBeforeGhRelease) }
    tasks.named("uploadToSourceForge") { mustRunAfter(ghRelease) }

    releaseSteps["GitHub Release"] = ghRelease.get()
    releaseSteps["SourceForge upload"] = tasks.getByName("uploadToSourceForge")
    releaseSteps["SourceForge default download"] = tasks.getByName("setSourceForgeDefaultDownload")
    releaseSteps["SourceForge version.html"] = tasks.getByName("updateSourceForgeVersion")
    releaseSteps["Blog post (build/release/blog-post.html)"] = tasks.getByName("prepareBlogPost")

    if (releaseRequested) {
        // The release continues when a step fails (see settings.gradle.kts), but nothing must be published,
        // if the pre-flight checks or the build fail, and the staging repository must not be released,
        // if some of the artifacts could not be published to it.
        (publishTasks + releaseSteps.values).forEach { it.dependsOn(validateCreds, buildTasks) }
        closeTask?.dependsOn(publishTasks)
        releaseSteps.values.forEach { it.finalizedBy(tasks.named("releaseSummary")) }
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