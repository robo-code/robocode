plugins {
    java
    signing
    `maven-publish`
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

group = "net.sf.robocode"
version = "1.9.5.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
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
    sign(publishing.publications["mavenJava"])
}
