plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.2.20"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}
