plugins {
    `java-gradle-plugin`
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":lasius-base"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("lasiusJavaPlugin") {
            id = "${group}.lasius-java"
            implementationClass = "net.onelitefeather.lasius.LasiusJavaPlugin"
            displayName = "Lasius Java Plugin"
            description = "A gradle plugin to apply default settings to java projects"
        }
    }
}