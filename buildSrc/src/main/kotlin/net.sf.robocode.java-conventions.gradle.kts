group = "net.sf.robocode"

plugins {
    java
    signing
    `maven-publish`
    id("org.gradle.test-retry")
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven {
        url = uri("https://repo.eclipse.org/content/repositories/eclipse-staging")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

// The product runs on Java 8, but JUnit 6 needs Java 17, so only the tests are compiled and run with Java 17 or newer
val testJavaLanguageVersion = 17

configurations.matching { it.name == "testCompileClasspath" || it.name == "testRuntimeClasspath" }.configureEach {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, testJavaLanguageVersion)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    named<JavaCompile>("compileTestJava") {
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(testJavaLanguageVersion))
        })
    }
    // Robot battle tests are timing-sensitive and can intermittently report transient
    // engine errors (e.g. "Unable to stop thread") on slow/loaded CI machines.
    // Retry failed tests a few times so such flakiness does not fail the build.
    withType<Test> {
        useJUnitPlatform()
        retry {
            maxRetries.set(2)
            maxFailures.set(20)
            failOnPassedAfterRetry.set(false)
        }
        // Run tests on another Java version than Java 17, e.g. -PtestJavaVersion=25,
        // as robot security works differently on Java 24+ where the SecurityManager is removed.
        val version = providers.gradleProperty("testJavaVersion").orNull ?: testJavaLanguageVersion.toString()
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(version))
        })
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Robocode")
                description.set("Build the best - destroy the rest!")
                url.set("https://robocode.sourceforge.io/")
                licenses {
                    license {
                        name.set("Eclipse Public License v1.0 (EPL)")
                        url.set("https://robocode.sourceforge.io/license/epl-v10.html")
                    }
                }
                developers {
                    developer {
                        name.set("Mathew A. Nelson")
                    }
                    developer {
                        id.set("flemming-n-larsen")
                        name.set("Flemming N. Larsen")
                        email.set("flemming.n.larsen@gmail.com")
                    }
                    developer {
                        id.set("pavel.savara")
                        name.set("Pavel Savara")
                        email.set("pavel.savara@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:robo-code/robocode.git")
                    developerConnection.set("scm:git:ssh:git@github.com:robo-code/robocode.git")
                    url.set("https://github.com/robo-code/robocode")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project

    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    // Make signing required only when a key is provided
    isRequired = !signingKey.isNullOrBlank()

    if (isRequired) {
        sign(publishing.publications["mavenJava"])
    }
}
