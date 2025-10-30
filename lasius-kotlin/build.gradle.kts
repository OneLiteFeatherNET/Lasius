plugins {
    `kotlin-dsl`
}

dependencies {
    // Needed because your plugin applies and configures the Kotlin plugin
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("lasiusKotlinPlugin") {
            id = "${group}.lasius-kotlin"
            implementationClass = "net.onelitefeather.lasius.LasiusKotlinPlugin"
            displayName = "Lasius Kotlin Plugin"
            description = "A gradle plugin to apply default settings to kotlin projects"
        }
    }
}