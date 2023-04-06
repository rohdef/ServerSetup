import dk.rohdef.gourmet.convention.configureCommon
import dk.rohdef.gourmet.convention.kotest

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "dk.rohdef.gourmet.plugins.reboot"
description = ""

configureCommon()
kotest()
kotlin {
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":gourmet-api"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                implementation("io.ktor:ktor-network:2.0.3")
            }
        }
        val nativeTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.3-native-mt")
            }
        }
    }
}