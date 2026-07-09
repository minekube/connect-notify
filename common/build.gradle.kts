plugins {
    `java-library`
}

dependencies {
    // These will be shaded in the final jar
    api("com.google.code.gson:gson:2.14.0")
    api("org.yaml:snakeyaml:2.6")
}

