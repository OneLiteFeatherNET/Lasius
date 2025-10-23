plugins {
    `java-gradle-plugin`
}

group = "net.onelitefeather"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("lasiusJavaPlugin") {
            id = "net.onelitefeather.lasius-java"
            implementationClass = "net.onelitefeather.lasius.LasiusJavaPlugin"
            displayName = "Lasius Java Plugin"
            description = "A gradle plugin to apply default settings to java projects"
        }
    }
}