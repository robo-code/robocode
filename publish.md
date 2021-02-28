# Dev setup
In order to publish new version to Maven repository
You need GPG key. 
```
gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg
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

# Just staging
```
gradle publishToSonatype
```
When you log in , you should be able to see the binaries uploaded to [staging Repository](https://oss.sonatype.org/#stagingRepositories)


# Release to public repos
```
gradle publishToSonatype closeAndReleaseSonatypeStagingRepository
```
This typically occurs within 10 minutes, though updates to search can take up to two hours.

You should be able to see the binaries uploaded to [public Repository](https://repo1.maven.org/maven2/net/sf/robocode/robocode.api/)