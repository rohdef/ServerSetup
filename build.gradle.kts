plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

group = "dk.rohdef.recipe-installer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kloggerVersion = "2.7.0"

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting

        commonMain {
            dependencies {
                implementation("it.czerwinski:kotlin-util:1.7.0")

                implementation("io.github.microutils:kotlin-logging:2.1.23")
                implementation("io.github.microutils:kotlin-logging-linuxx64:2.1.23")

                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
            }
        }

        val nativeTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.4.0")
                implementation("io.kotest:kotest-assertions-core:5.4.0")
            }
        }
    }
}
