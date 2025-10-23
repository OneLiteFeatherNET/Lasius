package net.onelitefeather.lasius;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LasiusJavaPluginTest {

    private Project project;

    @BeforeEach
    void setUp() {
        this.project = ProjectBuilder.builder().build();
    }

    @Test
    void testApplyPlugins() {
        new LasiusJavaPlugin().apply(project);
        assertTrue(project.getPlugins().hasPlugin("java-library"));
        assertTrue(project.getPlugins().hasPlugin("maven-publish"));
        assertTrue(project.getPlugins().hasPlugin("jacoco"));
    }

    @Test
    void testJavaToolChainApply() {
        LasiusJavaPlugin plugin = new LasiusJavaPlugin();
        plugin.apply(project);

        JavaPluginExtension javaExt = project.getExtensions().getByType(JavaPluginExtension.class);
        JavaLanguageVersion version = javaExt.getToolchain().getLanguageVersion().get();

        assertEquals(25, version.asInt());
        assertNotNull(project.getTasks().findByName("javadocJar"));
        assertNotNull(project.getTasks().findByName("sourcesJar"));
    }

    @Test
    void testJavaCompileOptions() {
        LasiusJavaPlugin plugin = new LasiusJavaPlugin();
        plugin.apply(project);

        JavaCompile compileTask = (JavaCompile) project.getTasks().getByName("compileJava");
        assertEquals("UTF-8", compileTask.getOptions().getEncoding(), "Encoding should be UTF-8");
        assertEquals(25, compileTask.getOptions().getRelease().get(), "Release version should be 25");
    }
}
