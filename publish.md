# Dev setup

## Create GPG key

Link: [GnuPG cheatsheet](https://devhints.io/gnupg)

In order to publish new version/release of Robocode to the Maven repository you need GPG key.

You can create it using this command:

```shell
gpg --default-new-key-algo rsa4096 --gen-key
```

You can export the public key to a file with this command:

```shell
gpg --export --armor --output public-key.pub
```

Next you need to upload your public key (is in the .pub file) to this server:
http://keyserver.ubuntu.com:11371/

Next, you need to export the secret keys to a file that must be located in your home directory:

```shell
gpg --keyring secring.gpg --export-secret-keys > [user-home-dir]/.gnupg/secring.gpg
```

Where `[user-home-dir]` is the home directory for your user account.

You can list the secret keys with this command, to read out the last 8 digits of the secret GPG key:

```shell
gpg -K
```

You need to have file `~/.gradle/gradle.properties` with

```
signing.keyId=<last-8-digits-of gpg -K>
signing.password=<pass-for-the-gpg-key>
signing.secretKeyRingFile=~/.gnupg/secring.gpg
ossrhUsername=<user-at-issues.sonatype.org>
ossrhPassword=<password-at-issues.sonatype.org>
```

## Just staging

Make sure you are running on Java 8 before running the following commands.

```shell
./gradlew publishToSonatype
```

When you log in, you should be able to see the binaries uploaded to [staging Repository].

## Release to public repos

```shell
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

This typically occurs within 10 minutes, though updates to search can take up to two hours.

You should be able to see the binaries uploaded to [public Repository]


[staging Repository]: https://oss.sonatype.org/#stagingRepositories

[public Repository]: https://repo1.maven.org/maven2/net/sf/robocode/robocode.api/