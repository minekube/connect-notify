plugins {
    java
    id("com.gradleup.shadow") apply false
}

allprojects {
    group = "com.minekube.connect"
    version = findProperty("version")?.toString()?.takeIf { it != "unspecified" } ?: "1.0.0-SNAPSHOT"
    description = "Discord notifications for Minekube Connect server status"
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ProcessResources> {
        val props = mapOf(
            "version" to project.version,
            "description" to project.description
        )
        filesMatching(listOf("plugin.yml", "bungee.yml", "velocity-plugin.json")) {
            expand(props)
        }
    }
}

