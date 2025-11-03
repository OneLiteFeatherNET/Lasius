package net.onelitefeather.lasius

import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class LasiusKotlinPlugin : LasiusBasePlugin() {
    override fun apply(project: Project) {
        configureKotlin(project)
    }

    private fun configureKotlin(project: Project) {
        project.extensions.findByType(KotlinJvmProjectExtension::class)?.apply {
            jvmToolchain(21)
            project.logger.lifecycle("Configured Kotlin JVM toolchain to Java 21 for ${project.name}")
        }
    }
}
