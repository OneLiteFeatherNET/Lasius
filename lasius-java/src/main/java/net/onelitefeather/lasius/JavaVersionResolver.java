package net.onelitefeather.lasius;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public interface JavaVersionResolver {

    int DEFAULT_JAVA_VERSION = 25;
    String JAVA_VERSION_PROPERTY = "java.version";

    /**
     * Resolves the Java version from project properties or uses the default.
     *
     * @param project the project to resolve the version for
     * @return the resolved Java version
     */
    static int resolveJavaVersion(@NotNull Project project) {
        if (project.hasProperty(JAVA_VERSION_PROPERTY)) {
            Object versionProperty = project.findProperty(JAVA_VERSION_PROPERTY);
            if (versionProperty instanceof Integer version) {
                return version;
            } else if (versionProperty instanceof String versionString) {
                try {
                    return Integer.parseInt(versionString);
                } catch (NumberFormatException e) {
                    project.getLogger().warn(
                            "Invalid java.version property '{}', using default version {}",
                            versionProperty, DEFAULT_JAVA_VERSION
                    );
                    return DEFAULT_JAVA_VERSION;
                }
            }
        }
        return DEFAULT_JAVA_VERSION;
    }
}
