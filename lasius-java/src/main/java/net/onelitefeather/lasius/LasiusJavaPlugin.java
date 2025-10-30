package net.onelitefeather.lasius;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.annotations.NotNull;

public class LasiusJavaPlugin extends LasiusBasePlugin {

    @Override
    public void apply(@NotNull Project project) {
        configureJava(project);
    }

    private void configureJava(@NotNull Project project) {
        project.getPluginManager().withPlugin("java", java -> {
            var javaExt = project.getExtensions().getByType(JavaPluginExtension.class);
            javaExt.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(25));
            javaExt.withJavadocJar();
            javaExt.withSourcesJar();

            project.getLogger().lifecycle("Set Java toolchain to Java {} for project {}", 25, project.getName());
        });
    }
}
