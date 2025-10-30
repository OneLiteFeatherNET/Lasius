package net.onelitefeather.lasius

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.nio.charset.StandardCharsets

class LasiusKotlinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        applyPlugins(project)
        configureKotlin(project)
        configureJavaCompile(project)
        configureTests(project)
        configureJacoco(project)
    }

    private fun applyPlugins(project: Project) {
        project.pluginManager.apply("org.jetbrains.kotlin.jvm")
        project.pluginManager.apply(JacocoPlugin::class.java)
    }

    private fun configureKotlin(project: Project) {
        project.extensions.findByType(KotlinJvmProjectExtension::class)?.apply {
            jvmToolchain(21)
            project.logger.lifecycle("Configured Kotlin JVM toolchain to Java 21 for ${project.name}")
        }
    }

    private fun configureJavaCompile(project: Project) {
        project.tasks.withType(JavaCompile::class).configureEach {
            options.encoding = StandardCharsets.UTF_8.displayName()
            options.release.set(21)
        }
    }

    private fun configureTests(project: Project) {
        project.tasks.withType(Test::class).configureEach {
            useJUnitPlatform()
            if (hasMinestomDependency(project)) {
                jvmArgs("-Dminestom.inside-test=true")
            }

            testLogging {
                events = setOf(
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.FAILED
                )
            }

            finalizedBy(project.tasks.named("jacocoTestReport"))
        }
    }

    private fun configureJacoco(project: Project) {
        project.tasks.withType(JacocoReport::class).configureEach {
            dependsOn(project.tasks.withType(Test::class.java))
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
        }
    }

    private fun hasMinestomDependency(project: Project): Boolean {
        return project.configurations.any { config ->
            config.dependencies.any { dep ->
                dep.name.contains("minestom", ignoreCase = true)
            }
        }
    }
}
