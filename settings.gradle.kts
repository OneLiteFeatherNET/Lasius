rootProject.name = "lasius"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("libs") {
            version("junit", "6.0.1")

            library("junit-bom", "org.junit", "junit-bom").versionRef("junit")
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").withoutVersion()
            library("junit-launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()
        }
    }
}

include("lasius-java")
include("lasius-kotlin")
include("lasius-base")