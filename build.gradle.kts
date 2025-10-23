subprojects {
    apply(plugin = "maven-publish")

    group = "net.onelitefeather"

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }
    }
}