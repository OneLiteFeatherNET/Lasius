package net.onelitefeather.lasius;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LasiusJavaPlugin implements Plugin<@NotNull Project> {

    private int javaVersion;

    @Override
    public void apply(@NotNull Project project) {
        this.javaVersion = JavaVersionResolver.resolveJavaVersion(project);
        applyPlugins(project);
        configureJava(project);
        configureTasks(project);
        configurePublishing(project);
    }

    private void applyPlugins(@NotNull Project project) {
        project.getPluginManager().apply("java-library");
        project.getPluginManager().apply("maven-publish");
        project.getPluginManager().apply(JacocoPlugin.class);
    }

    private void configureJava(@NotNull Project project) {
        project.getPluginManager().withPlugin("java", java -> {
            var javaExt = project.getExtensions().getByType(JavaPluginExtension.class);
            javaExt.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(javaVersion));
            javaExt.withJavadocJar();
            javaExt.withSourcesJar();

            project.getLogger().lifecycle("Set Java toolchain to Java {} for project {}", javaVersion, project.getName());
        });
    }

    /**
     * Configures tasks for the project.
     * @param project the project to configure tasks for
     */
    private void configureTasks(@NotNull Project project) {
        project.getTasks().withType(JavaCompile.class).configureEach(compileTask -> {
            compileTask.getOptions().setEncoding(StandardCharsets.UTF_8.displayName());
            compileTask.getOptions().getRelease().set(javaVersion);
        });

        project.getTasks().withType(Test.class).configureEach(testTask -> {
            testTask.useJUnitPlatform();
            if (hasMinestomDependency(project)) {
                testTask.jvmArgs("-Dminestom.inside-test=true");
            }

            testTask.getTestLogging().setEvents(java.util.Set.of(
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.FAILED
            ));

            testTask.finalizedBy(project.getTasks().named("jacocoTestReport"));
        });

        project.getTasks().withType(JacocoReport.class).configureEach(jacocoTask -> {
            jacocoTask.dependsOn(project.getTasks().withType(Test.class));
            jacocoTask.getReports().getXml().getRequired().set(true);
            jacocoTask.getReports().getCsv().getRequired().set(true);
        });
    }


    /**
     * Configures publishing for the project.
     *
     * @param project the project to configure publishing for.
     */
    private void configurePublishing(@NotNull Project project) {
        project.getPluginManager().apply("maven-publish");

        project.afterEvaluate(p -> {
            var publishing = p.getExtensions().findByType(PublishingExtension.class);
            if (publishing == null) return;

            publishing.publications(publications ->
                    publications.create("maven", MavenPublication.class, publication ->
                            publication.from(p.getComponents().getByName("java"))
                    )
            );

            publishing.repositories(repos -> repos.maven(maven -> {
                maven.setName("OneLiteFeatherRepository");

                String username = System.getenv("ONELITEFEATHER_MAVEN_USERNAME");
                String password = System.getenv("ONELITEFEATHER_MAVEN_PASSWORD");

                maven.credentials(passwordCredentials -> {
                    passwordCredentials.setUsername(username);
                    passwordCredentials.setPassword(password);
                });

                String version = p.getVersion().toString();
                String url = version.contains("SNAPSHOT")
                        ? "https://repo.onelitefeather.dev/onelitefeather-snapshots"
                        : "https://repo.onelitefeather.dev/onelitefeather-releases";

                maven.setUrl(p.uri(url));
            }));

            project.getLogger().lifecycle("Configured publishing for {}", project.getName());
        });
    }

    /**
     * Checks if the project has a dependency on Minestom.
     *
     * @param project the project to check
     * @return true if the project has a dependency on Minestom, false otherwise
     */
    private boolean hasMinestomDependency(@NotNull Project project) {
        return project.getConfigurations().stream()
                .anyMatch(config -> config.getDependencies().stream()
                        .anyMatch(dep -> dep.getName().toLowerCase().contains("minestom")));
    }
}

