dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("testLibs") {
            from(files("../gradle/test-libs.versions.toml"))
        }
    }
}