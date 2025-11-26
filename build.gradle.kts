subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    group = "net.onelitefeather"

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }
    }

    plugins.withType<MavenPublishPlugin> {
        project.extensions.configure<PublishingExtension> {

            publications.create<MavenPublication>("maven") {
                from(components["java"])
            }

            repositories {
                maven {
                    name = "OneLiteFeatherRepository"
                    url = if (project.version.toString().contains("SNAPSHOT")) {
                        uri("https://repo.onelitefeather.dev/onelitefeather-snapshots")
                    } else {
                        uri("https://repo.onelitefeather.dev/onelitefeather-releases")
                    }

                    credentials(PasswordCredentials::class) {
                        username = System.getenv("ONELITEFEATHER_MAVEN_USERNAME")
                        password = System.getenv("ONELITEFEATHER_MAVEN_PASSWORD")
                    }
                }
            }
        }
    }
}