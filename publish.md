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


```
gradle publishToSonatype closeAndReleaseSonatypeStagingRepository
```