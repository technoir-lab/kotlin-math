import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

group = providers.gradleProperty("GROUP").get()
version = providers.gradleProperty("VERSION_NAME").get()

kotlin {
    jvm()

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    // Tier 1
    macosArm64()
    iosArm64()
    iosSimulatorArm64()

    // Tier 2
    linuxArm64()
    linuxX64()
    watchosArm64()
    watchosSimulatorArm64()

    // Tier 3
    androidNativeArm64()
    watchosDeviceArm64()
    mingwX64 {
        binaries.findTest(DEBUG)!!.linkerOpts = mutableListOf("-Wl,--subsystem,windows")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        reportUndocumented.set(false)
        skipEmptyPackages.set(true)
        skipDeprecated.set(true)
        jdkVersion.set(17)

        // Add Android SDK packages
        enableAndroidDocumentationLink.set(true)

        sourceLink {
            localDirectory.set(project.file("src/commonMain/kotlin"))
            // URL showing where the source code can be accessed through the web browser
            remoteUrl.set(uri("https://github.com/romainguy/kotlin-math/blob/main/${project.name}/src/commonMain/kotlin"))
            // Suffix which is used to append the line number to the URL. Use #L for GitHub
            remoteLineSuffix.set("#L")
        }
    }
}

val dokkaGeneratePublicationHtml = tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml")

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(dokkaGeneratePublicationHtml)
    archiveClassifier.set("javadoc")
    from(dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
}
