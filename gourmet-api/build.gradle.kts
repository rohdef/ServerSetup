plugins {
    kotlin("multiplatform") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
}

group = "dk.rohdef.gourmet-api"
version = "1.0-SNAPSHOT"
description = ""

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.arrow-kt:arrow-core:1.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("io.github.microutils:kotlin-logging:2.1.23")
                implementation("io.github.microutils:kotlin-logging-linuxx64:2.1.23")

                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
                implementation("io.kotest:kotest-framework-engine:5.4.2")
                implementation("io.kotest:kotest-assertions-core:5.4.2")
            }
        }

        val nativeMain by getting {
            dependencies {
                implementation(project(":rfpath"))
            }
        }
        val nativeTest by getting
    }
}