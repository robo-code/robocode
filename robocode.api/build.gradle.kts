import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.type

plugins {
    id("net.sf.robocode.java-conventions")
    `java-library`
    java
}

description = "Robocode API"

/* TODO
<artifactId>maven-deploy-plugin</artifactId>
https://docs.gradle.org/current/userguide/publishing_setup.html
https://docs.gradle.org/current/userguide/java_library_distribution_plugin.html
*/

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "robocode.Robocode"))
    }
    archiveFileName.set("robocode.jar")
}

tasks.javadoc{
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
    op.links = listOf("http://docs.oracle.com/javase/8/docs/api/")
    op.source("1.8")
    op.excludeDocFilesSubDir("robocode.exception","net.sf.robocode","gl4java","robocode.robocodeGL")
    op.addStringsOption("exclude",":").value = listOf("gl4java", "robocode.exception","net.sf.robocode","robocode.robocodeGL")
}

tasks.build{
    dependsOn("javadoc")
}