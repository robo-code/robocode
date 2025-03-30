plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
}

dependencies {
    implementation(libs.bundles.byte.buddy)

    testImplementation(testLibs.bundles.junit5)
    testImplementation(testLibs.mockito.core)
}

description = "Robocode Security Agent"

java {
    withSourcesJar()
}

tasks {
    test {
        useJUnitPlatform()
    }
}