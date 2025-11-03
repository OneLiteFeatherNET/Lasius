package net.onelitefeather.lasius;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.annotations.NotNull;

public class LasiusJavaPlugin extends LasiusBasePlugin {

    @Override
    public void apply(@NotNull Project project) {
        super.apply(project);
        configureJava(project);
    }

    private void configureJava(@NotNull Project project) {
        project.getPluginManager().withPlugin("java-library", _ -> {
            var javaExt = project.getExtensions().getByType(JavaPluginExtension.class);
            javaExt.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(25));
            javaExt.withJavadocJar();
            javaExt.withSourcesJar();

            project.getTasks().withType(JavaCompile.class).configureEach(task -> {
                task.getOptions().setEncoding("UTF-8");
                task.getOptions().getRelease().set(25);
            });

            project.getLogger().lifecycle("Set Java toolchain to Java {} for project {}", 25, project.getName());
        });
    }
}
