plugins {
    kotlin("multiplatform") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
}

group = "dk.rohdef.remote-install"
version = "1.0-SNAPSHOT"
description = "Path library to handle basic file system IO"

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
        val nativeMain by getting {
            dependencies {
                implementation("io.arrow-kt:arrow-core:1.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("io.github.microutils:kotlin-logging:2.1.23")
                implementation("io.github.microutils:kotlin-logging-linuxx64:2.1.23")

                implementation("com.soywiz.korlibs.korio:korio:3.2.0")
                implementation("io.insert-koin:koin-core:3.2.2")
            }
        }
        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
                implementation("io.kotest:kotest-framework-engine:5.4.2")
                implementation("io.kotest:kotest-assertions-core:5.4.2")
            }
        }
    }
}
