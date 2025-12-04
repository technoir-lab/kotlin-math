pluginManagement {
    plugins {
        kotlin("multiplatform") version "2.2.21"
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        id("org.jetbrains.dokka") version "2.1.0"
        id("com.vanniktech.maven.publish") version "0.35.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "kotlin-math"
