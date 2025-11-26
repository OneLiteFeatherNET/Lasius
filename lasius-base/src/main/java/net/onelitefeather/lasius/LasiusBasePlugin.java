package net.onelitefeather.lasius;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Shared base Gradle plugin that configures common behavior for both Java and Kotlin projects.
 */
public abstract class LasiusBasePlugin implements Plugin<@NotNull Project> {

    @Override
    public void apply(@NotNull Project project) {
        applyCommonPlugins(project);
        configureTesting(project);
        configurePublishing(project);
    }

    private void applyCommonPlugins(@NotNull Project project) {
        project.getPluginManager().apply("jacoco");
        project.getPluginManager().apply("maven-publish");
    }

    /**
     * Configures JUnit 5, Jacoco, and test logging.
     */
    private void configureTesting(@NotNull Project project) {
        project.getTasks().withType(Test.class).configureEach(test -> {
            test.useJUnitPlatform();
            test.getTestLogging().setEvents(Set.of(
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.FAILED
            ));
            test.finalizedBy(project.getTasks().named("jacocoTestReport"));
        });

        project.getTasks().withType(JacocoReport.class).configureEach(jacoco -> {
            jacoco.dependsOn(project.getTasks().withType(Test.class));
            jacoco.getReports().getXml().getRequired().set(true);
            jacoco.getReports().getHtml().getRequired().set(true);
            jacoco.getReports().getCsv().getRequired().set(false);
        });
    }

    /**
     * Configures Maven publishing with credentials and repository selection based on version.
     */
    private void configurePublishing(@NotNull Project project) {
        project.afterEvaluate(p -> {
            var publishing = p.getExtensions().findByType(PublishingExtension.class);
            if (publishing == null) {
                return;
            }

            publishing.publications(publications ->
                    publications.create("maven", MavenPublication.class, publication -> {
                        var component = p.getComponents().findByName("java");
                        if (component != null) {
                            publication.from(component);
                        }
                    })
            );

            publishing.repositories(repos -> repos.maven(maven -> {
                String version = p.getVersion().toString();
                String baseUrl = version.contains("SNAPSHOT")
                        ? "https://repo.onelitefeather.dev/onelitefeather-snapshots"
                        : "https://repo.onelitefeather.dev/onelitefeather-releases";

                maven.setName("OneLiteFeatherRepository");
                maven.setUrl(p.uri(baseUrl));

                String username = System.getenv("ONELITEFEATHER_MAVEN_USERNAME");
                String password = System.getenv("ONELITEFEATHER_MAVEN_PASSWORD");

                maven.credentials(passwordCredentials -> {
                    passwordCredentials.setUsername(username);
                    passwordCredentials.setPassword(password);
                });
            }));

            project.getLogger().lifecycle("Configured publishing for {}", project.getName());
        });
    }
}
