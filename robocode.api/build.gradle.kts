plugins {
    id("net.sf.robocode.java-conventions")
    java
    signing
    `maven-publish`
}

java {
    withJavadocJar()
    withSourcesJar()
}

description = "Robocode API"

tasks {
    javadoc {
        source = sourceSets["main"].java
        exclude("robocode/exception/**")
        exclude("robocode/robocodeGL/**")
        exclude("gl4java/**")
        exclude("net/sf/robocode/**")

        options.windowTitle = "Robocode ${project.version} API"
        var op = options as StandardJavadocDocletOptions
        op.docTitle = "<h1>Robocode ${project.version} API</h1>"
        op.docFilesSubDirs(true)
        op.use(false)
        op.author(true)
        op.isNoTimestamp = false
        op.bottom("Copyright &#169; 2023 <a href=\"https://robocode.sourceforge.io\">Robocode</a>. All Rights Reserved.")
        op.links = listOf("https://docs.oracle.com/javase/8/docs/api/")
        op.charSet("UTF-8")
        op.source("1.8")
        op.excludeDocFilesSubDir("robocode.exception", "net.sf.robocode", "gl4java", "robocode.robocodeGL")
        op.addStringsOption("exclude", ":").value =
            listOf("gl4java", "robocode.exception", "net.sf.robocode", "robocode.robocodeGL")
    }
    jar {
        manifest {
            attributes(mapOf("Main-Class" to "robocode.Robocode"))
        }
        archiveFileName.set("robocode.jar")
        dependsOn("javadoc")
        into("") {
            from("../versions.md")
        }
    }
}