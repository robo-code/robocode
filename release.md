# Release

## Quick release

**Option A — local release**

One command does everything: pre-flight checks → build → Maven Central → Chocolatey (Windows only) → GitHub Release:

```shell
./gradlew release
```

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
manually when needed (see [Step 6](#step-6--push-docker-image-optional)).

---

## What gets published

| Artifact | Registry | Command |
|----------|----------|---------|
| `robocode.api`, `robocode.core`, etc. (JARs) | Maven Central (`net.sf.robocode`) | `./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository` (or `./gradlew release`) |
| `robocode` (Windows GUI installer) | Chocolatey Community Repository | `./gradlew chocoPush` |
| `zamboch/roborumble` (RoboRumble server) | Docker Hub | `./gradlew dockerPush` |
| `robocode-{version}-setup.jar` | GitHub Release (manual upload) | — |

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

### Step 3 — Commit and tag

```shell
git add gradle.properties versions.md
git commit -m "Release version 1.10.3"
git tag v1.10.3
git push origin main --tags
```

### Step 4 — Build all artifacts

```shell
./gradlew build
```

This produces:
- All module JARs
- `build/robocode-{version}-setup.jar` (self-extracting installer)
- Chocolatey package in `robocode.installer/build/choco/`

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

1. Go to [github.com/robo-code/robocode/releases/new](https://github.com/robo-code/robocode/releases/new).
2. Select the tag `v{version}` created in Step 3.
3. Set the title to `Robocode {version}`.
4. Copy the relevant section from `versions.md` into the release notes.
5. Attach `build/robocode-{version}-setup.jar`.
6. Publish the release.

---

## Troubleshooting

**Signing fails**: Confirm `signing.keyId`, `signing.password`, and `signing.secretKeyRingFile` are
set in `~/.gradle/gradle.properties` and that the key file exists at the specified path.

**Sonatype staging rejected**: Ensure the public GPG key is uploaded to `keyserver.ubuntu.com`
and that all required POM fields (license, SCM, developer) are present.

**`chocoPush` fails**: Confirm `CHOCOLATEY_API_KEY` is set and that `choco` is installed and on `PATH`.
Run `choco --version` to verify.

**Maven Central sync slow**: Normal sync is 10 min; allow up to 2 hours before raising an issue.
