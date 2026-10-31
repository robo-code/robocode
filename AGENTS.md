# Robocode agent guidance

## Releases

Before preparing or publishing a release, read [`release.md`](release.md) completely. It is the authoritative release procedure and credential guide.

- Run Gradle on Java 17 or newer. The modules are compiled with a Java 8 toolchain, so the setup JAR runs on Java 8 and newer. Do not run Gradle itself on Java 8.
- `./gradlew release` performs the local release flow: pre-flight checks, build, Maven Central publishing, Chocolatey publishing on Windows, GitHub Release creation, SourceForge publishing (setup JAR, README.md, default download, and `version.html`), and the blog post HTML. A failing step does not stop the independent steps, and a summary at the end lists the results and the manual steps (posting on Blogger and SourceForge news).
- With the local release, push `main` but not the version tag. `gh release create` makes the tag on GitHub; pushing the tag first starts the release workflow, which publishes too.
- Docker publishing is deliberately separate from the release task. Publish the RoboRumble image (`zamboch/roborumble`) with `./gradlew dockerPush` only when it is explicitly required.
- Pushing a `v*` version tag triggers the GitHub Actions release workflow. Update `gradle.properties` and `versions.md` before creating the tag.
- Never run publishing or release commands unless the user explicitly authorizes the external release.
