rootProject.name = "lasius"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("junit", "6.0.0")

            library("junit-bom", "org.junit", "junit-bom").versionRef("junit")
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").withoutVersion()
            library("junit-launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()
        }
    }
}

include("lasius-java")