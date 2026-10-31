# Release

## Quick release

**Option A — local release**

Bump the version in `gradle.properties`, set the release date in `versions.md`, update the copyright year
(see [Step 2](#step-2--update-the-changelog)), commit, tag HEAD, and push `main`
(but not the tag, see [Step 3](#step-3--commit-and-tag)). Then one command does everything: pre-flight checks → build →
Maven Central → Chocolatey (Windows only) → GitHub Release → SourceForge (setup JAR, README.md, default download,
`version.html`) → blog post HTML:

```shell
./gradlew release
```

If a step fails, the error is shown, but the other steps continue. Steps that depend on a failed step are skipped
(e.g. nothing is published if the pre-flight checks or the build fail, and `version.html` is not updated if the upload
to SourceForge failed). A summary at the end shows the result of each step and the remaining manual steps:
posting on Blogger and SourceForge news.

Docker push is not included — run `./gradlew dockerPush` separately if needed.

---

**Option B — fully automated**

Push a version tag and GitHub Actions does everything (build → Maven Central → Chocolatey → GitHub Release):

```shell
# 1. Bump version in gradle.properties and update versions.md, then:
git add gradle.properties versions.md
git commit -m "Release version 1.10.3"
git tag v1.10.3
git push origin main v1.10.3
```

Requires [GitHub secrets](#github-secrets) to be configured once. Docker push is not automated — run it
manually when needed (see [Step 7](#step-7--push-docker-image-optional)). SourceForge and the announcements are not
automated by GitHub Actions — see [Step 9](#step-9--publish-to-sourceforge) and [Step 10](#step-10--announce-the-release).

---

## What gets published

| Artifact | Registry | Command |
|----------|----------|---------|
| `robocode.api`, `robocode.core`, etc. (JARs) | Maven Central (`net.sf.robocode`) | `./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository` (or `./gradlew release`) |
| `robocode` (Windows GUI installer) | Chocolatey Community Repository | `./gradlew chocoPush` |
| `zamboch/roborumble` (RoboRumble server) | Docker Hub | `./gradlew dockerPush` |
| `robocode-{version}-setup.jar` + release notes | GitHub Release | `./gradlew createGitHubRelease` |
| `robocode-{version}-setup.jar` + `README.md` | SourceForge Files (also the default download) | `./gradlew setSourceForgeDefaultDownload` |
| `version.html` (read by Robocode to check for new versions) | SourceForge web (`robocode.sourceforge.io/version/`) | `./gradlew updateSourceForgeVersion` |

---

## Prerequisites

### 1. GPG key

Signing is required for Maven Central. See [`publish.md`](publish.md) for full setup instructions.

The key must be exported to `~/.gnupg/secring.gpg` and uploaded to a public key server:

```shell
gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg
gpg --keyserver keyserver.ubuntu.com --send-keys $KEY_ID
```

### 2. `~/.gradle/gradle.properties`

Create or update your **user-level** Gradle properties file (never commit this file):

- Windows: `%USERPROFILE%\.gradle\gradle.properties`
- macOS/Linux: `~/.gradle/gradle.properties`

```properties
# Sonatype OSSRH (Maven Central)
ossrhUsername=$OSSRH_USERNAME
ossrhPassword=$OSSRH_PASSWORD

# GPG signing
signing.keyId=$LAST_8_DIGITS_OF_GPG_KEY
signing.password=$GPG_PASSPHRASE
signing.secretKeyRingFile=$HOME/.gnupg/secring.gpg
```

Replace each `$...` placeholder with the real value. The `signing.keyId` is the last 8 digits shown
by `gpg -K`.

### 3. Chocolatey API key

Set the `CHOCOLATEY_API_KEY` environment variable before running `chocoPush`:

```shell
# Windows (PowerShell)
$env:CHOCOLATEY_API_KEY = "your-api-key"

# macOS/Linux
export CHOCOLATEY_API_KEY="your-api-key"
```

Get the API key at [community.chocolatey.org](https://community.chocolatey.org/) → Account → API Key.

### 4. GitHub secrets

For Option A (GitHub Actions), configure these secrets once at
**github.com/robo-code/robocode → Settings → Secrets → Actions**:

| Secret | Value |
|--------|-------|
| `SIGNING_KEY` | GPG private key (full ASCII-armored block, with real newlines) |
| `SIGNING_PASSWORD` | GPG key passphrase |
| `OSSRH_USERNAME` | Sonatype OSSRH username |
| `OSSRH_PASSWORD` | Sonatype OSSRH password |
| `CHOCOLATEY_API_KEY` | Chocolatey Community Repository API key |

### 6. GitHub CLI

Required for GitHub Release creation. Authenticate once with:

```shell
gh auth login
```

### 7. Docker Hub

Log in to Docker Hub before running `dockerPush`:

```shell
docker login
```

### 8. SourceForge

SourceForge only accepts file uploads via SSH, and the release uses an SSH key without a passphrase for this:

```shell
ssh-keygen -t ed25519 -N "" -C "robocode-release@sourceforge" -f ~/.ssh/sourceforge
```

Add the public key (`~/.ssh/sourceforge.pub`) at [sourceforge.net/auth/shell_services](https://sourceforge.net/auth/shell_services).
Then add these to `~/.gradle/gradle.properties`:

```properties
sourceforgeUsername=$SOURCEFORGE_USERNAME
# Release API key from https://sourceforge.net/auth/preferences/, used to set the default download
sourceforgeReleaseApiKey=$SOURCEFORGE_RELEASE_API_KEY
# Optional, if the SSH key is not ~/.ssh/sourceforge
sourceforgeSshKey=$PATH_TO_PRIVATE_KEY
```

---

## Release steps

### Step 1 — Bump the version

Edit `gradle.properties` and update the version number:

```properties
version=1.10.3
```

### Step 2 — Update the changelog

Edit `versions.md` — add a new section at the top with the version and date, and describe all changes
and bugfixes included in this release.

Update the copyright year in all files, if this is the first release of the year:

```shell
./gradlew updateCopyrightYear
```

It sets the end year of the Robocode copyright notices (the `YYYY` in `Copyright (c) 2001-YYYY`) to the current year. The
pre-flight checks of `./gradlew release` and the release workflow on GitHub fail if a copyright notice does not end
with the current year. Run `./gradlew checkCopyrightYear` to check it.

### Step 3 — Commit and tag

```shell
git add gradle.properties versions.md
git commit -m "Release version 1.10.3"
git tag v1.10.3
git push origin main
```

With the local release (Option A), push `main` but not the tag. `./gradlew release` creates the tag on GitHub together
with the GitHub Release, and the release workflow then sees that the release exists and skips publishing. If the tag is
pushed first, the release workflow publishes the release too (Option B).

### Step 4 — Build all artifacts

```shell
./gradlew build
```

This produces:
- All module JARs
- `build/robocode-{version}-setup.jar` (self-extracting installer)
- Chocolatey package in `robocode.installer/build/choco/`

Gradle itself must run on Java 17 or newer (`JAVA_HOME`), but all modules are compiled with a Java 8 toolchain
(`languageVersion.set(JavaLanguageVersion.of(8))` in `build.gradle.kts`). This way, the setup JAR runs on Java 8 and
newer. A JDK 8 must be installed, so Gradle can detect it; check with `./gradlew javaToolchains`.

`versions.md` is included in the setup JAR, so set the release date in `versions.md` before building the release.

### Step 5 — Publish to Maven Central

Stage and release in one step:

```shell
./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository
```

Or stage first for manual review at [oss.sonatype.org](https://oss.sonatype.org/#stagingRepositories):

```shell
./gradlew publishAllPublicationsToSonatypeRepository
```

Then close and release from the Sonatype UI, or:

```shell
./gradlew closeAndReleaseSonatypeStagingRepository
```

Maven Central sync typically completes within 10 minutes; search index updates can take up to 2 hours.
Verify at: `https://repo1.maven.org/maven2/net/sf/robocode/robocode.api/{version}/`

### Step 6 — Publish to Chocolatey

Requires `CHOCOLATEY_API_KEY` environment variable (see Prerequisites above).
Also requires `choco` CLI to be installed (Windows only).

```shell
./gradlew chocoPush
```

This runs `chocoCopy` → `chocoBuild` → `chocoPush` internally, packaging the setup JAR into a
`.nupkg` and pushing it to `https://push.chocolatey.org/`.

Verify at: `https://community.chocolatey.org/packages/robocode/{version}`

### Step 7 — Push Docker image (optional)

Only needed when the RoboRumble server image needs updating. Requires `docker login`.

```shell
./gradlew dockerPush
```

This tags and pushes `zamboch/roborumble:{version}` and `zamboch/roborumble:latest` to Docker Hub.

### Step 8 — Create GitHub Release

```shell
./gradlew createGitHubRelease
```

This creates the release `v{version}` titled `Robocode {version}` with the section of the version in `versions.md` as
release notes, and attaches `build/robocode-{version}-setup.jar`. The tag is created on GitHub from the HEAD commit,
which must be pushed.

### Step 9 — Publish to SourceForge

```shell
./gradlew updateSourceForgeVersion
```

This runs these steps in order, where each step only runs if the previous one succeeded:

1. `uploadToSourceForge`: uploads the setup JAR and the section of the version in `versions.md` as `README.md` to
   [sourceforge.net/projects/robocode/files/robocode/{version}/](https://sourceforge.net/projects/robocode/files/robocode/).
2. `setSourceForgeDefaultDownload`: makes the setup JAR the default download for Windows, macOS, Linux, and BSD. The download link in
   Robocode leads to this download.
3. `updateSourceForgeVersion`: writes the version to `htdocs/version/version.html`. Installed versions of Robocode read
   this file to tell users about the new version, so it must be updated last.

### Step 10 — Announce the release

`./gradlew prepareBlogPost` (part of `./gradlew release`) converts the release notes to HTML in
`build/release/blog-post.html`.

1. Create a post on [Blogger](https://www.blogger.com/blog/posts/7868270166586050549) with `build/release/blog-post.html`.
2. Create a post on [SourceForge news](https://sourceforge.net/p/robocode/news/) with `build/release/release-notes.md`.

---

## Troubleshooting

**Signing fails**: Confirm `signing.keyId`, `signing.password`, and `signing.secretKeyRingFile` are
set in `~/.gradle/gradle.properties` and that the key file exists at the specified path.

**Sonatype staging rejected**: Ensure the public GPG key is uploaded to `keyserver.ubuntu.com`
and that all required POM fields (license, SCM, developer) are present.

**`chocoPush` fails**: Confirm `CHOCOLATEY_API_KEY` is set and that `choco` is installed and on `PATH`.
Run `choco --version` to verify.

**Maven Central sync slow**: Normal sync is 10 min; allow up to 2 hours before raising an issue.
