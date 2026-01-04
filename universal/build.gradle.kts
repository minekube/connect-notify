plugins {
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit"))
    implementation(project(":velocity"))
    implementation(project(":bungee"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("ConnectNotify-${project.version}.jar")
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))

        // Relocate dependencies to avoid conflicts
        relocate("com.google.gson", "com.minekube.connect.notify.libs.gson")
        relocate("org.yaml.snakeyaml", "com.minekube.connect.notify.libs.snakeyaml")

        // Don't minimize - we need all platform classes
        // minimize() would remove classes that appear "unused" but are loaded by platform

        // Merge service files
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        archiveClassifier.set("unshaded")
    }
}

