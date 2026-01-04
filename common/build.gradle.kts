plugins {
    `java-library`
}

dependencies {
    // These will be shaded in the final jar
    api("com.google.code.gson:gson:2.10.1")
    api("org.yaml:snakeyaml:2.5")
}

